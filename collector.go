package cbhistory
import (
	"github.com/go-xorm/xorm"
	"time"
	"reflect"
	"sync"
	"github.com/rakyll/coop"
	"net/http"
	"io/ioutil"
)

type collector struct {
	e              *xorm.Engine
	// 发现的文章
	Discovered     chan int
	// 待存储文章
	StoreArticle   chan Article
	// 待存储评论
	StoreComment   chan Comment
	// 等待收集的文章
	PendingArticle chan Article
	r              bool
}
func NewCollector(e *xorm.Engine) *collector {
	return &collector{
		e,
		make(chan int, 200),
		make(chan Article, 100),
		make(chan Comment, 200),
		make(chan Article, 100),
		false,
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
	e := c.e
	for c.r {
		select {
		case id := <-c.Discovered:
			a := Article{}
			found, err := e.Id(id).Get(&a)
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
	e := c.e
	ta := Article{}
	tc := Comment{}
	for c.r {
		select {
		case a := <-c.StoreArticle:
			clear(&ta)
			t := time.Now()
			a.Update = &t
			found, err := e.Id(a.Sid).Get(&ta)
			if err != nil {log.Warning("Get comment faield %v:%v", a, err); continue}
			if found {
				_, err := e.Update(a)
				if err != nil {log.Warning("Update article faield", err); continue}
			}else {
				_, err := e.Insert(a)
				if err != nil {log.Warning("Insert article faield", err); continue}
			}
		case cmt := <-c.StoreComment:
			clear(&tc)
			found, err := c.e.Id(cmt.Sid).Get(&tc)
			if err != nil {log.Warning("Get comment faield %v:%v", cmt, err); continue}
			if found {
				log.Debug("Insert Comment %v", cmt)
				_, err := c.e.Update(cmt)
				if err != nil {log.Warning("Update comment faield", err); continue}
			}else {
				log.Debug("Insert Comment %v", cmt)
				_, err := c.e.Insert(cmt)
				if err != nil {log.Warning("Insert comment faield", err); continue}
			}
		case <-time.After(time.Second):
		}
	}
}