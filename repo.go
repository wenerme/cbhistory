package cbhistory

import (
	"fmt"
	"github.com/jinzhu/gorm"
	_ "github.com/mattn/go-sqlite3"
	"github.com/op/go-logging"
	"github.com/spacemonkeygo/errors"
	"time"
)

type repo struct {
	*gorm.DB
}

func (r *repo) getCmt(id int, cmt *CmtResponse) (find bool, err error) {
	log.Debug("Get cmt respnse %v", id)
	a := &Article{}
	if find, err = r.FindById(id, a); !find {
		*cmt = cmtNotExists
		return
	}
	var cmts []Comment
	_, err = r.FindById(id, &cmts)
	if err != nil {
		return
	}
	*cmt = makeCmtResponse(a, cmts)
	return true, nil
}
func (r *repo) getArticleCmts(id int, cmts *[]Comment) (err error) {
	err = r.Table("article").Where("sid = ?", id).Find(cmts).Error
	return
}
func (this *repo) Database() *gorm.DB {
	return this.DB
}

func (this *repo) FindById(id interface{}, v interface{}) (find bool, err error) {
	if log.IsEnabledFor(logging.DEBUG) {
		defer log.Debug("%v(%v %v) %#v ", id, find, err, v)
	}
	switch v.(type) {
	case *Article:
		err = this.Where("sid = ?", id).Find(v).Error
	case *[]Comment:
		err = this.Table("comment").Where("sid = ?", id).Find(v).Error
	case *Comment:
		err = this.Where("tid = ?", id).Find(v).Error
	case *CmtResponse:
		find, err = this.getCmt(id.(int), v.(*CmtResponse))

	default:
		return false, errors.New(fmt.Sprintf("Can not find %T with %v", v, id))
	}
	if err == nil {
		find = true
	}
	if err == gorm.RecordNotFound {
		err = nil
	}
	return
}
func (this *repo) Store(v interface{}) error {
	switch v.(type) {
	case *Article:
		c := v.(*Article)
		tc := Article{}
		if this.Where("sid = ?", c.Sid).Find(&tc).Error == gorm.RecordNotFound {
			if r := this.Create(c); r.Error != nil {
				log.Warning("Insert article faield %+v:%v", c, r.Error)
			} else {
				log.Debug("Insert article %+v", c)
			}
		} else {
			if r := this.Save(c); r.Error != nil {
				log.Warning("Update article faield %+v:%v", c, r.Error)
			} else {
				log.Debug("Update article %+v", c)
			}
		}
	case *Comment:
		c := v.(*Comment)
		tc := Comment{}
		if this.Where("tid = ?", c.Tid).Find(&tc).Error == gorm.RecordNotFound {
			if r := this.Create(c); r.Error != nil {
				log.Warning("Insert comment faield %+v:%v", c, r.Error)
			} else {
				log.Debug("Insert comment %+v", c)
			}
		} else {
			if r := this.Save(c); r.Error != nil {
				log.Warning("Update comment faield %+v:%v", c, r.Error)
			} else {
				log.Debug("Update comment %+v", c)
			}
		}

	default:
		panic(fmt.Sprintf("Can not store %+v", v))
	}
	return nil
}
func (this *repo) FindAllArticleDateBetween(a time.Time, b time.Time) ([]Article, error) {
	items := make([]Article, 0)
	return items, this.Where("date between ? and ?", a, b).Find(&items).Error
}
func (this *repo) FindAllArticleUpdateBetween(a time.Time, b time.Time) ([]Article, error) {
	items := make([]Article, 0)
	return items, this.Where(`"update" between ? and ?`, a, b).Find(&items).Error
}
func (this *repo) Init() {
	a := &Article{}
	c := &Comment{}
	this.DropTableIfExists(a)
	this.DropTableIfExists(c)
	this.CreateTable(a)
	this.CreateTable(c)
	this.Model(c).AddIndex("comment_sid", "sid").AddIndex("comment_pid", "pid")
	log.Info("Init store, clear article and comment")
}
func (this *repo) Clear() {
	a := &Article{}
	c := &Comment{}

	this.Delete(a)
	this.Delete(c)
}
