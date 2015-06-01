package cbhistory
import (
	"strconv"
	"errors"
	"fmt"
	"net/http"
	"io/ioutil"
	"time"
	"encoding/json"
)
var hc = &http.Client{}
func Discover(c string) []int {
	m := regArticle.FindAllStringSubmatch(c, -1)
	ids := make(map[int]bool, len(m))

	for _, v := range m {
		id, err := strconv.Atoi(v[1])
		if (err != nil) {
			log.Warning("Discover id failed: %v", err)
		}
		ids[id] = true
	}

	var keys []int
	for k := range ids {
		keys = append(keys, k)
	}

	return keys
}

func Collect(a *Article, comments map[int]Comment) (err error) {
	// 获取文章页面
	// 解析文章页面信息到 Article
	// 获取 cmt
	// 解析 cmt 信息到 Article
	// 解析 cmt 中的评论
	if a.Sid == 0 {
		return errors.New("Aricle no Sid")
	}
	if a.Sn == "" {
		url := fmt.Sprintf("http://www.cnbeta.com/articles/%d.htm", a.Sid);
		log.Debug("Fetct article %v", url)
		r, err := http.Get(url)
		if err != nil {return err}
		if r.StatusCode != 200 {
			return errors.New("Status is not OK: "+r.Status)
		}
		b, err := ioutil.ReadAll(r.Body)
		if err != nil {return err}
		err = ParsePage(a, string(b))
		if err != nil {return err}
	}
	{
		cmt, err := FetchCmt(a)
		if err != nil {return err}
		if cmt.State != "success" {
			return errors.New(fmt.Sprintf("Wrong cmt response %d:%s", cmt.ErrorCode, cmt.Error))
		}
		//		log.Debug("Fetched Cmt %+v",cmt.Result.CmtStore)
		r := cmt.Result
		a.Fav = r.FavNum.Int()
		a.Join = r.JoinNum.Int()
		a.Comments = r.CommentNum.Int()
		a.Read = r.ViewNum.Int()
		a.Token = r.Token

		for _, v := range r.CmtStore {
			c := Comment{}
			c.Tid = v.Tid.Int()
			c.Sid = v.Sid.Int()
			c.Pid = v.Pid.Int()
			c.Date = (*time.Time)(&v.Date)
			c.Comment = v.Comment
			c.Pros = v.Score.Int()
			c.Cons = v.Reason.Int()
			c.Hostname = v.HostName
			comments[c.Tid] = c
		}
	}
	return
}
func FetchCmt(a *Article) (c *CmtResponse, err error) {
	//	c = new(CmtResponse)
	//	if cache.Get("cmt", a.Sid, c) {
	//		return
	//	}
	//	c = nil
	for p := 1;; p ++ {
		url := fmt.Sprintf("http://www.cnbeta.com/cmt?op=%v,%v,%v", p, a.Sid, a.Sn);
		log.Debug("Fetch Cmt %s", url)
		r, err := http.NewRequest("GET", url, nil)
		if err !=nil {return c, err}
		r.Header.Add("X-Requested-With", "XMLHttpRequest")
		res, err := hc.Do(r)
		if err != nil {return c, err}
		b, err := ioutil.ReadAll(res.Body)
		if err != nil {return c, err}
		log.Debug("Got Cmt %s", string(b))
		cmt := CmtResponse{}
		err = json.Unmarshal(b, &cmt)
		if err != nil {return c, err}
		log.Debug("Parse Cmt %+v", cmt)
		if c == nil {
			c = &cmt
		}else if cmt.Result.CommentNum > 0 {
			// Merge Cmt
			log.Info("Merge Cmt %+v", cmt.Result)
			c.Result.HotList = append(c.Result.HotList, cmt.Result.HotList...)
			c.Result.CmtList = append(c.Result.CmtList, cmt.Result.CmtList...)
			for k, v := range cmt.Result.CmtStore {
				c.Result.CmtStore[k] = v
			}
			for k, v := range cmt.Result.CmtDict {
				c.Result.CmtDict[k] = v
			}
			c.Result.CommentNum += cmt.Result.CommentNum
			c.Result.JoinNum += cmt.Result.JoinNum
			// TODO 需要明确其他参数是否需要叠加
		}

		if cmt.State == "success" && len(cmt.Result.CmtStore) > 70 {
			log.Debug("Comments > 70, will try next page")
			continue
		}
		break
	}
	//	if c.State == "success" {
	//		cache.Put("cmt", a.Sid, c)
	//	}
	return
}
