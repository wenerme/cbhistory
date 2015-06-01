package cbhistory

import (
	_ "github.com/mattn/go-sqlite3"
	"github.com/jinzhu/gorm"
	"fmt"
)


type GormCollectorStore struct {
	*gorm.DB
}
func (this *GormCollectorStore)FindById(id interface{}, v interface{}) (bool, error) {
	switch v.(type){
		case *Article:
		if r := this.Where("sid = ?", id).Find(v); r.Error != nil {
			if r.Error == gorm.RecordNotFound {
				return false, nil
			}else {
				return false, r.Error
			}
		}
		case *Comment:
		if r := this.Where("tid = ?", id).Find(v); r.Error != nil {
			if r.Error == gorm.RecordNotFound {
				return false, nil
			}else {
				return false, r.Error
			}
		}

		default:
		panic(fmt.Sprintf("Can not find %T with %v", v, id))
	}
	return true, nil
}
func (this *GormCollectorStore)Store(v interface{}) error {
	switch v.(type){
		case *Article:
		c := v.(*Article)
		tc := Article{}
		if this.Where("sid = ?", c.Sid).Find(&tc).Error == gorm.RecordNotFound {
			if r := this.Create(c); r.Error != nil {
				log.Warning("Insert article faield %+v:%v", c, r.Error)
			}else {
				log.Debug("Insert article %+v", c)
			}
		}else {
			if r := this.Update(c); r.Error != nil {
				log.Warning("Update article faield %+v:%v", c, r.Error)
			}else {
				log.Debug("Update article %+v", c)
			}
		}
		case *Comment:
		c := v.(*Comment)
		tc := Comment{}
		if this.Where("tid = ?", c.Tid).Find(&tc).Error == gorm.RecordNotFound {
			if r := this.Create(c); r.Error != nil {
				log.Warning("Insert comment faield %+v:%v", c, r.Error)
			}else {
				log.Debug("Insert comment %+v", c)
			}
		}else {
			if r := this.Update(c); r.Error != nil {
				log.Warning("Update comment faield %+v:%v", c, r.Error)
			}else {
				log.Debug("Update comment %+v", c)
			}
		}

		default:
		panic(fmt.Sprintf("Can not store %+v", v))
	}
	return nil
}
func (this *GormCollectorStore)Init() error {
	a := &Article{}
	c := &Comment{}
	this.DropTableIfExists(a)
	this.DropTableIfExists(c)
	this.CreateTable(a)
	this.CreateTable(c)
	this.Model(c).AddIndex("comment_sid", "sid").AddIndex("comment_pid", "pid")
	log.Info("Init store, clear article and comment")
	return nil
}