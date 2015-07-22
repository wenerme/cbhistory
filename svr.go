package cbhistory

import (
	"github.com/jinzhu/gorm"
	_ "github.com/mattn/go-sqlite3"
	"github.com/spacemonkeygo/errors"
)

type Server interface {
	Config() *Config
	Start() error
	Database() *gorm.DB
}
type svr struct {
	c          *Config
	db         gorm.DB
	collector  *collector
	httpServer *httpServer
}

func NewServer(c *Config) Server {
	return &svr{c: c}
}

func (s *svr) Config() *Config {
	return s.c
}
func (s *svr) Database() *gorm.DB {
	db := s.db
	return &db
}
func (s *svr) init() (err error) {
	if s.httpServer != nil {
		return
	}

	c := s.c
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
	s.collector = newCollector(s)
	s.httpServer = newHttpServer(s)
	return
}
func (s *svr) Start() (err error) {
	err = s.init()
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
