package cbhistory

import (
	"github.com/syndtr/goleveldb/leveldb"
	"github.com/syndtr/goleveldb/leveldb/opt"
	"fmt"
	"encoding/json"
)
var cache *_cache
func init() {
	o := &opt.Options{
		//		Filter: filter.NewBloomFilter(128),
		BlockCacheCapacity: 120,
	}
	var err error
	db, err := leveldb.OpenFile("cache", o)
	if err != nil {panic(err)}
	cache = &_cache{db}
	cache.db.Delete(nil, nil)
}
type _cache struct {
	db *leveldb.DB
}
func (c *_cache)Put(ns string, key, val interface{}) {
	k := fmt.Sprintf("%s-%v", ns, key)
	b, err := json.Marshal(val)
	if err != nil {
		log.Warning("Marshal json error", err)
		return
	}
	err = c.db.Put([]byte(k), b, nil)
	if err != nil {
		log.Warning("Put cache error", err)
		return
	}
	log.Debug("Put cache '%s' %s", k, string(b))
}
func (c *_cache)Get(ns string, key, val interface{}) (found bool) {
	k := fmt.Sprintf("%s-%v", ns, key)
	b, err := c.db.Get([]byte(k), nil)
	if err != nil {
		if err != leveldb.ErrNotFound {
			log.Warning("Get cache error", err)
		}else {
			log.Debug("Get cache %s not found", k, string(b))
		}
		return
	}
	err = json.Unmarshal(b, val)
	if err != nil {
		log.Warning("Unmarshal json error", err)
		return
	}
	log.Debug("Get cache %s %s", k, string(b))
	return true
}