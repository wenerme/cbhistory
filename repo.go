package cbhistory

import (
	"fmt"
	"github.com/jinzhu/gorm"
	_ "github.com/mattn/go-sqlite3"
	"time"
)

type repo struct {
	*gorm.DB
}

func (r *repo) getCmt(id int, cmt *CmtResponse) (find bool, err error) {
	a := &Article{}
	if find, err = r.FindById(id, a); !find {
		c := cmtNotExists
		cmt = &c
		return
	}
	var cmts []Comment
	err = r.Table("article").Where("sid = ?", id).Find(&cmts).Error
	if err != nil {
		return
	}
	response := makeCmtResponse(a, cmts)
	cmt = &response
	return true, nil
}
func (r *repo) getArticleCmts(id int, cmts *[]Comment) (err error) {
	err = r.Table("article").Where("sid = ?", id).Find(cmts).Error
	return
}
func (this *repo) Database() *gorm.DB {
	return this.DB
}

func (this *repo) FindById(id interface{}, v interface{}) (bool, error) {
	switch v := v.(type) {
	case *Article:
		if r := this.Where("sid = ?", id).Find(v); r.Error != nil {
			if r.Error == gorm.RecordNotFound {
				return false, nil
			} else {
				return false, r.Error
			}
		}
	case *Comment:
		if r := this.Where("tid = ?", id).Find(v); r.Error != nil {
			if r.Error == gorm.RecordNotFound {
				return false, nil
			} else {
				return false, r.Error
			}
		}
	case *CmtResponse:
		return this.getCmt(id.(int), v)
	default:
		panic(fmt.Sprintf("Can not find %T with %v", v, id))
	}
	return true, nil
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
