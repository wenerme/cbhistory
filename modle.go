package cbhistory
import "time"

type Article struct {
	// 文章ID
	Sid    int `gorm:"primary_key"`
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
	Update *time.Time
	// 文章发布日期
	Date   *time.Time
	// 阅读人数
	Read     int
	// 文章来源
	Source   string
	// 文章标题
	Title    string
	Token    string
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
	Join     int
	// 评论日期
	Date     *time.Time
}