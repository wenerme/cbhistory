package cbhistory
import (
	"time"
	"fmt"
)

type Article struct {
	// 文章ID
	Sid      int `gorm:"primary_key"`
	Sn       string
	// 顶的数量
	Dig      int
	// 评论数量
	Comments int
	//
	Fav      int
	// 简介
	Intro    string
	// 参与人数
	Join     int
	// 该文章更新时间
	Update   *time.Time
	// 文章发布日期
	Date     *time.Time
	// 阅读人数
	Read     int
	// 文章来源
	Source   string
	// 文章标题
	Title    string
	Token    string
	// 是否该文章已经过期
	Outdated bool
}

func (a Article)TableName() string {
	return "article"
}
func (a Article)String() string {
	return fmt.Sprintf("Article{sid=%v, sn=%v, oudated=%v, update=%v, date=%v, title=%v}",
		a.Sid, a.Sn, a.Outdated, a.Update, a.Date, a.Title)
}

type Comment struct {
	// 评论ID
	Tid int `gorm:"primary_key"`
	// 父评论ID
	Pid int
	// 文章ID
	Sid int
	// 评论内容
	Comment  string
	// 反对数
	Cons     int
	// 支持数
	Pros     int
	// 发言者所在地区
	Hostname string
	// 评论日期
	Date     *time.Time
}

func (a Comment)TableName() string {
	return "comment"
}