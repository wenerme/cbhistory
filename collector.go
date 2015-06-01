package cbhistory
import (
	"time"
	"reflect"
	"sync"
	"github.com/rakyll/coop"
	"net/http"
	"io/ioutil"
)

// 收集器的存储接口
type CollectorStore interface {
	Store(interface{}) error
	FindById(id interface{}, out interface{}) (bool, error)
	Init() error
}

type collector struct {
	// 发现的文章
	Discovered     chan int
	// 待存储文章
	StoreArticle   chan Article
	// 待存储评论
	StoreComment   chan Comment
	// 等待收集的文章
	PendingArticle chan Article
	r              bool
	s CollectorStore
}
func NewCollector(s CollectorStore) *collector {
	return &collector{
		make(chan int, 200),
		make(chan Article, 100),
		make(chan Comment, 200),
		make(chan Article, 100),
		false,
		s,
	};
}

func (c *collector)Start() error {
	// 扫描指定页面
	// 打开定时发现任务
	// 打开处理线程
	c.r = true
	log.Info("Start collecting")
	go c.store()
	go c.discover()
	go c.collect()
	return nil
}

func clear(v interface{}) {
	p := reflect.ValueOf(v).Elem()
	p.Set(reflect.Zero(p.Type()))
}

func (c *collector)DiscoverUrl(url string) {
	go func() {
		r, err := http.Get(url);
		if err != nil {log.Warning("Discover url %v faield:%v", url, err); return}
		b, err := ioutil.ReadAll(r.Body)
		if err != nil {log.Warning("Read all %v faield:%v", url, err); return}
		ids := Discover(string(b))
		log.Info("With %s discoved  (%v)%v", url, len(ids), ids)
		for _, i := range ids {
			c.Discovered <- i
		}
	}()
}
func (c *collector)discover() {
	// 定时扫描指定页面
	// 定时查询快指定间隔时间中的文章

	coop.Every(1*time.Hour, func() {
		c.DiscoverUrl("http://www.cnbeta.com/")
	})

}
func (c *collector)collect() {
	// 收集发现的文章
	collecting := make(map[int]interface{})
	rw := sync.RWMutex{}
	for c.r {
		select {
		case id := <-c.Discovered:
			a := Article{}
			a.Sid = id
			found, err := c.s.FindById(id, &a)
			if err != nil {log.Warning("Get article faield %v:%v", a, err); continue}
			if found {
				// 判断是否需要更新
				log.Info("Article no need update %+v", a)
				continue
			}
			rw.Lock()
			collecting[id] = nil
			rw.Unlock()
			log.Info("Pending update article %+v", a)
			c.PendingArticle <- a
		case a := <-c.PendingArticle:
			comments := make(map[int]Comment)
			err := Collect(&a, comments)
			if err != nil {log.Warning("Collec article faield %v:%v", a, err); continue}
			c.StoreArticle <- a
			for _, v := range comments {
				c.StoreComment <- v
			}
		case <-time.After(time.Second):
		}
	}
}
func (c *collector)store() {
	for c.r {
		select {
		case a := <-c.StoreArticle:
			t := time.Now()
			a.Update = &t
			err := c.s.Store(&a)
			if err != nil {log.Warning("Store article faield %v:%v", a, err); }
		case cmt := <-c.StoreComment:
			err := c.s.Store(&cmt)
			if err != nil {log.Warning("Store comment faield %v:%v", cmt, err); }
		case <-time.After(time.Second):
		}
	}
}
