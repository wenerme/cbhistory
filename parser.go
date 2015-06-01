package cbhistory
import (
	"regexp"
	"github.com/PuerkitoBio/goquery"
	"strings"
	"time"
	"strconv"
	"errors"
)

type Parser interface {
	Article(string, *Article) (error)
	ArticleJson(string, *Article) (error)
	CommentJson(string) ([]Comment, error)
	Discover(string) []int
}

type pV3 struct {

}

var regArticle = regexp.MustCompile("articles/(\\d+)")
var regDetail = regexp.MustCompile("GV\\.DETAIL[^=]*=([^;]+)")
var regJSObject = regexp.MustCompile(`(\w+)\s*:\s*"([^"]+)"`)


func simpleJSObject(s string) map[string]string {
	o := make(map[string]string)
	m := regJSObject.FindAllStringSubmatch(s, -1)
	for _, v := range m {
		o[v[1]] = v[2]
	}
	return o
}

func FillArticle(a *Article, c *CmtResponse) {
	r := c.Result
	a.Fav = r.FavNum.Int()
	a.Join = r.JoinNum.Int()
	a.Comments = r.CommentNum.Int()
	a.Read = r.ViewNum.Int()
	a.Token = r.Token
}

func ParseDetail(c string, a *Article) (*Article) {

	return a
}


func ParseArticle(c string) (a Article, err error) {
	doc, err := goquery.NewDocumentFromReader(strings.NewReader(c))
	if (err != nil) { return }
	a.Title = strings.TrimSpace(doc.Find("#news_title").Text())
	a.Intro = strings.TrimSpace(doc.Find(".introduction p").Text())

	{
		bar := doc.Find(".title_bar")
		tmp := strings.TrimSpace(bar.Find(".where").Text())
		a.Source = tmp[strings.Index(tmp, "：")+len("："):]

		d, err := time.Parse("2006-01-02 15:04:05", bar.Find(".date").Text())
		if (err != nil) { return a, err}
		a.Date = &d
	}

	{
		detail := regDetail.FindStringSubmatch(c)[1];
		o := simpleJSObject(detail)
		a.Sn = o["SN"]
		a.Sid, _ = strconv.Atoi(o["SID"])
	}

	return
}

func ParsePage(a *Article, c string) (err error) {
	doc, err := goquery.NewDocumentFromReader(strings.NewReader(c))
	if (err != nil) { return }
	a.Title = strings.TrimSpace(doc.Find("#news_title").Text())
	a.Intro = strings.TrimSpace(doc.Find(".introduction p").Text())
	if len(a.Title) == 0 || len(a.Intro) == 0 {return errors.New("Title and Intro not found, may not an article page")}
	{
		bar := doc.Find(".title_bar")
		tmp := strings.TrimSpace(bar.Find(".where").Text())
		a.Source = tmp[strings.Index(tmp, "：")+len("："):]

		d, err := time.ParseInLocation("2006-01-02 15:04:05", bar.Find(".date").Text(), time.Local)
		if (err != nil) { return err}
		a.Date = &d
	}

	{
		detail := regDetail.FindStringSubmatch(c)[1];
		o := simpleJSObject(detail)
		a.Sn = o["SN"]
		a.Sid, _ = strconv.Atoi(o["SID"])
	}

	return
}
