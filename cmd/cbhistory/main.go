package main

import (
	"fmt"
	"github.com/jinzhu/gorm"
	cbh "github.com/wenerme/cbhistory"
)

func main() {
	db, err := gorm.Open("sqlite3", "./cbh.db")
	if err != nil {
		panic(err)
	}
	s := &cbh.GormCollectorStore{&db}
	c := cbh.NewCollector(s)
	_ = c
	c.Start()
	ids := make([]int, 0)
	_ = ids

	if err := db.Raw(`
	SELECT sid
	FROM article
	WHERE outdated=0`).Pluck("sid", &ids).Error; err != nil {
		panic(err)
	}
	fmt.Printf("Need update %v", ids)
	c.Update(ids...)
	select {} // block forever
}
