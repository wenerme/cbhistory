package cbhistory
import (
	"time"
	"reflect"
	"sync"
	"net/http"
	"io/ioutil"
	"sort"
)

// 收集器的存储接口
type CollectorStore interface {
	Store(interface{}) error
	FindById(id interface{}, out interface{}) (bool, error)
	FindAllArticleDateBetween(a time.Time, b time.Time) ([]Article, error)
	FindAllArticleUpdateBetween(a time.Time, b time.Time) ([]Article, error)
	Init()
}

type collector struct {
	// 发现的文章
	discovered     chan int
	// 待存储文章
	storeArticle   chan Article
	// 待存储评论
	storeComment   chan Comment
	// 等待收集的文章
	pendingArticle chan Article
	r              bool
	s              CollectorStore
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
	go c.schedule()
	go c.collect()
	return nil
}

func clear(v interface{}) {
	p := reflect.ValueOf(v).Elem()
	p.Set(reflect.Zero(p.Type()))
}

func (c *collector)Update(ids... int) {
	for _, v := range ids {
		c.discovered <- v
	}
}
func (c *collector)DiscoverUrl(url string) {
	go func() {
		r, err := http.Get(url);
		if err != nil {log.Warning("Discover url %v faield:%v", url, err); return}
		b, err := ioutil.ReadAll(r.Body)
		if err != nil {log.Warning("Read all %v faield:%v", url, err); return}
		ids := Discover(string(b))
		log.Info("In %s discoved  (%v)%v", url, len(ids), ids)
		for _, i := range ids {
			c.discovered <- i
		}
	}()
}
func (c *collector)schedule() {
	// 定时扫描指定页面
	// 定时查询快指定间隔时间中的文章

	discoverUrl := func(url string) func() {
		return func() {
			c.DiscoverUrl(url)
		}
	}

	//	discoverBetween := func()func(){
	//
	//	}

	jobs := make([]Job, 0)
	jobs = append(jobs, []Job{
		NewJob("HomePage", 30 * time.Minute, 20 * time.Second, discoverUrl("http://www.cnbeta.com/")),
		NewJob("RankPage", 2 * time.Hour, 10 * time.Second, discoverUrl("http://www.cnbeta.com/rank/show.htm")),
		NewJob("TopPage", 2 * time.Hour, 30 * time.Second, discoverUrl("http://www.cnbeta.com/top10.htm")),
		NewJob("UpdateExpired", 30 * time.Minute, 40 * time.Second, func() {
			log.Info("Update article between time")
		}),
	}...)
	sort.Sort(byTimeAsc(jobs))
	log.Info("Jobs %+v", jobs)
	for c.r {
		time.Sleep(2*time.Second)
		log.Debug("Job tick")
		now := time.Now()
		for now.After(jobs[0].At) {
			job := jobs[0]
			log.Info("Do job %v", job)
			job.At = job.At.Add(job.Interval)
			jobs = append(jobs[1:], job)
			sort.Sort(byTimeAsc(jobs))
			log.Info("Next job %v", jobs[0])
			job.Do()
		}
	}
}
func (c *collector)collect() {
	// 收集发现的文章
	collecting := make(map[int]interface{})
	rw := sync.RWMutex{}
	for c.r {
		select {
		case id := <-c.discovered:
			a := Article{}
			a.Sid = id
			found, err := c.s.FindById(id, &a)
			if err != nil {log.Warning("Get article faield %v:%v", a, err); continue}
			if found {
				if a.Outdated {
					log.Info("Article outdated, will not update %v", a)
					continue
				}
				var minimalUpdateInterval time.Duration = 30 * time.Minute
				if a.Update != nil && a.Update.Add(minimalUpdateInterval).After(time.Now()) {
					// 判断是否需要更新
					log.Info("Article last update is less than %s, will not update %v", minimalUpdateInterval.String(), a)
					continue
				}
			}
			rw.Lock()
			collecting[id] = nil
			rw.Unlock()
			log.Info("Pending update article %+v", a)
			c.pendingArticle <- a
		case a := <-c.pendingArticle:
			comments := make(map[int]Comment)
			err := Collect(&a, comments)
			if err != nil {log.Warning("Collec article faield %v:%v", a, err); continue}
			if a.Comments > 0 && len(comments) == 0 {
				a.Outdated = true
				log.Info("Article oudated %v", a)
			}
			c.storeArticle <- a
			for _, v := range comments {
				c.storeComment <- v
			}
		case <-time.After(time.Second):
		}
	}
}
func (c *collector)store() {
	for c.r {
		select {
		case a := <-c.storeArticle:
			t := time.Now()
			a.Update = &t
			err := c.s.Store(&a)
			if err != nil {
				log.Warning("Store article faield %v:%v", a, err);
			}else {
				log.Debug("Store article %+v", a)
			}
		case cmt := <-c.storeComment:
			err := c.s.Store(&cmt)
			if err != nil {
				log.Warning("Store comment faield %v:%v", cmt, err);
			}else {
				log.Debug("Store comment %+v", cmt)
			}
		case <-time.After(time.Second):
		}
	}
}

type Job struct {
	Name     string
	At       time.Time
	Interval time.Duration
	Do       func()
}
// 如果 interval <= 0, 则该 Job 不会重复执行
func NewJob(n string, i time.Duration, d time.Duration, v func()) Job {
	return Job{n, time.Now().Add(d), i, v }
}
//func (j *job)NextTime() {
//	j.At = time.Now().Add(j.Interval)
//}
type Scheduler struct {
	jobs []Job
}
// 返回是否还有 Job 等待处理
func (s *Scheduler) Schedule() (bool) {
	jobs := s.jobs
	now := time.Now()
	for now.After(jobs[0].At) {
		job := jobs[0]
		log.Debug("Do job %v", job)
		job.At = job.At.Add(job.Interval)
		jobs = append(jobs[1:], job)
		sort.Sort(byTimeAsc(jobs))
		log.Debug("Next job %v", jobs[0])
		job.Do()
	}
	s.jobs = jobs
	return len(jobs) > 0
}
type byTimeAsc []Job
func (a byTimeAsc) Len() int { return len(a) }
func (a byTimeAsc) Swap(i, j int) { a[i], a[j] = a[j], a[i] }
func (a byTimeAsc) Less(i, j int) bool { return a[i].At.Before(a[j].At) }
