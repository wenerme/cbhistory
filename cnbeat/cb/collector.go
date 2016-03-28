package cbhistory

import (
	"io/ioutil"
	"net/http"
	"sort"
	"sync"
	"time"
)

type collector struct {
	// 发现的文章
	discovered chan int
	// 待存储文章
	storeArticle chan Article
	// 待存储评论
	storeComment chan Comment
	// 等待收集的文章
	pendingArticle chan Article
	r              bool
	s              *repo
	jobs           []Job
	svr            Server
}

func newCollector(s Server) *collector {
	c := &collector{
		discovered:     make(chan int, 200),
		storeArticle:   make(chan Article, 100),
		storeComment:   make(chan Comment, 200),
		pendingArticle: make(chan Article, 100),
		r:              false,
		s:              &repo{s.Database()},
	}

	c.svr = s
	return c
}

func (c *collector) Start() error {
	c.r = true

	// 添加配置中的任务
	for _, j := range c.svr.Config().Collector.Jobs {
		job := NewJob(j.Description, j.Interval.Duration, j.Delay.Duration, nil)
		t, e := j.Type, j.Expression
		job.Do = func() { c.do(&job, t, e) }
		c.jobs = append(c.jobs, job)
		log.Debug("Add job %v", j)
	}
	log.Info("Start collecting")
	go c.store()
	go c.schedule()
	go c.collect()
	return nil
}

func (c *collector) Update(ids ...int) {
	for _, v := range ids {
		c.discovered <- v
	}
}
func (c *collector) DiscoverUrl(url string) {
	go func() {
		r, err := http.Get(url)
		if err != nil {
			log.Warning("Discover url %v faield:%v", url, err)
			return
		}
		b, err := ioutil.ReadAll(r.Body)
		if err != nil {
			log.Warning("Read all %v faield:%v", url, err)
			return
		}
		ids := FindArticleIds(string(b))
		log.Info("In %s discoved  (%v)%v", url, len(ids), ids)
		for _, i := range ids {
			c.discovered <- i
		}
	}()
}
func (c *collector) do(j *Job, t string, exp string) {
	log.Debug("Do %v with %v", t, exp)
	switch t {
	case "sql":
		ids := make([]int, 0)
		if err := c.svr.Database().Raw(exp).Pluck("id", &ids).Error; err != nil {
			panic(err)
		}
		log.Info("For job %v will update %v", j.Desc, ids)
	case "url":
		c.DiscoverUrl(exp)

	}
}

func (c *collector) schedule() {
	sort.Sort(byTimeAsc(c.jobs))

	for c.r {
		time.Sleep(2 * time.Second)

		now := time.Now()
		for now.After(c.jobs[0].At) {
			job := c.jobs[0]
			log.Info("Do job %v", job)
			job.At = job.At.Add(job.Interval)
			c.jobs = append(c.jobs[1:], job)
			sort.Sort(byTimeAsc(c.jobs))
			log.Info("Next job %v", c.jobs[0])
			job.Do()
		}
	}
}
func (c *collector) collect() {
	// 收集发现的文章
	collecting := make(map[int]interface{})
	rw := sync.RWMutex{}
	for c.r {
		select {
		case id := <-c.discovered:
			a := Article{}
			a.Sid = id
			found, err := c.s.FindById(id, &a)
			if err != nil {
				log.Warning("Get article faield %v:%v", a, err)
				continue
			}
			if found {
				if a.Outdated {
					log.Debug("Article outdated, will not update %v", a)
					continue
				}
				var minimalUpdateInterval time.Duration = 10 * time.Minute
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
			if err != nil {
				log.Warning("Collec article faield %v:%v", a, err)
				continue
			}
			if a.Comments > 0 && len(comments) == 0 {
				a.Outdated = true
				log.Info("Oudated Article get no comments %v", a)
			} else if a.Date != nil && a.Date.Before(time.Now().Add(-10*24*time.Hour)) {
				// 使十天前的文章过期
				a.Outdated = true
				log.Info("Oudated Article too old %v", a)
			}
			if a.Date == nil || a.Intro == "" || a.Title == "" {
				log.Warning("Article missing field %+v", a)
			}
			c.storeArticle <- a
			for _, v := range comments {
				c.storeComment <- v
			}
			log.Info("Article updated %+v", a)
		case <-time.After(time.Second):
		}
	}
}
func (c *collector) store() {
	for c.r {
		select {
		case a := <-c.storeArticle:
			t := time.Now()
			a.Update = &t
			err := c.s.Store(&a)
			if err != nil {
				log.Warning("Store article faield %v:%v", a, err)
			} else {
				log.Debug("Store article %+v", a)
			}
		case cmt := <-c.storeComment:
			err := c.s.Store(&cmt)
			if err != nil {
				log.Warning("Store comment faield %v:%v", err, cmt)
			} else {
				log.Debug("Store comment %+v", cmt)
			}
		case <-time.After(time.Second):
		}
	}
}

type Job struct {
	Desc     string
	At       time.Time
	Interval time.Duration
	Do       func()
}

// 如果 interval <= 0, 则该 Job 不会重复执行
func NewJob(n string, i time.Duration, d time.Duration, v func()) Job {
	return Job{n, time.Now().Add(d), i, v}
}

//func (j *job)NextTime() {
//	j.At = time.Now().Add(j.Interval)
//}
type Scheduler struct {
	jobs []Job
}

// 返回是否还有 Job 等待处理
func (s *Scheduler) Schedule() bool {
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

func (a byTimeAsc) Len() int           { return len(a) }
func (a byTimeAsc) Swap(i, j int)      { a[i], a[j] = a[j], a[i] }
func (a byTimeAsc) Less(i, j int) bool { return a[i].At.Before(a[j].At) }
