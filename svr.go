package cbhistory

import (
	"fmt"
	"github.com/jinzhu/gorm"
	_ "github.com/mattn/go-sqlite3"
	"github.com/op/go-logging"
	"github.com/spacemonkeygo/errors"
)

type Server interface {
	Config() *Config
	Start() error
	Init() error
	Database() *gorm.DB
	Repo() *repo
}
type svr struct {
	c          *Config
	db         gorm.DB
	collector  *collector
	httpServer *httpServer
	r          *repo
}

func New() Server {
	return NewServer(LoadConfig("config.toml"))
}
func NewServer(c *Config) Server {
	return &svr{c: c}
}

func (s *svr) Config() *Config {
	return s.c
}
func (s *svr) Repo() *repo {
	return s.r
}
func (s *svr) Database() *gorm.DB {
	db := s.db
	return &db
}
func (s *svr) Init() (err error) {
	if s.httpServer != nil {
		return
	}

	c := s.c

	l, err := logging.LogLevel(c.Log.Level)
	if err != nil {
		return err
	}
	logging.SetLevel(l, "cbh")
	log.Info("Set log level to %v", l)

	t := c.Database.Type
	switch t {
	case "sqlite3":
		s.db, err = gorm.Open(t, c.Database.File)
		log.Info("Use database %v(%v)", t, c.Database.File)
	default:
		err = errors.NotImplementedError.New("Unsupport database type %v", t)
	}
	if err != nil {
		return
	}

	s.db.LogMode(c.Database.Debug)
	s.db.SetLogger(dbLogger(func(v ...interface{}) {
		log.Debug(fmt.Sprint(v...))
	}))
	s.r = &repo{s.Database()}

	s.collector = newCollector(s)
	s.httpServer = newHttpServer(s)
	return
}

type dbLogger func(v ...interface{})

func (l dbLogger) Print(v ...interface{}) {
	l(v...)
}

func (s *svr) Start() (err error) {
	err = s.Init()
	if err != nil {
		return
	}
	if s.c.Collector.Enable {
		err = s.collector.Start()
		if err != nil {
			return
		}
	}
	if s.c.Http.Enable {
		err = s.httpServer.Start()
		if err != nil {
			return
		}
	}
	return nil
}
