package cbhistory

import (
	"encoding/json"
	"sort"
	"strconv"
	"strings"
	"time"
)

type CmtResponse struct {
	State     string    `json:"state,omitempty"`
	Message   string    `json:"message,omitempty"`
	Result    CmtResult `json:"result,omitempty"`
	Error     string    `json:"string,omitempty"`
	ErrorCode CmtInt    `json:"error_code,omitempty"`
}

type CmtResult struct {
	CommentNum CmtInt   `json:"comment_num,omitempty"`
	JoinNum    CmtInt   `json:"join_num,omitempty"`
	Open       CmtInt   `json:"open"`
	Token      string   `json:"token"`
	ViewNum    CmtInt   `json:"view_num,omitempty"`
	Page       CmtInt   `json:"page,omitempty"`
	Sid        CmtInt   `json:"sid,omitempty"`
	U          []CmtInt `json:"u"`
	DigNum     CmtInt   `json:"dig_num"`
	FavNum     CmtInt   `json:"fav_num"`

	CmtStore map[string]CmtContent `json:"cmntstore"`
	CmtList  []CmtInfo             `json:"cmntlist"`
	HotList  []CmtInfo             `json:"hotlist"`
	CmtDict  CmtDict               `json:"cmntdict"`
}

type CmtContent struct {
	CmtBaseInfo
	Date     CmtDate `json:"date"`
	Name     string  `json:"name"`
	HostName string  `json:"host_name"`
	Comment  string  `json:"comment"`
	Score    CmtInt  `json:"score"`
	Reason   CmtInt  `json:"reason"`
	UserId   CmtInt  `json:"userid"`
	Icon     string  `json:"icon"`
}
type CmtBaseInfo struct {
	Tid CmtInt `json:"tid"`
	Pid CmtInt `json:"pid"`
	Sid CmtInt `json:"sid"`
}
type CmtInfo struct {
	CmtBaseInfo
	Parent CmtInt `json:"parent"`
	Thread CmtInt `json:"thread"`
}

type CmtInt int
type CmtDate time.Time
type CmtDict map[string][]CmtBaseInfo

func (t CmtDate) MarshalJSON() ([]byte, error) {
	return []byte(`"` + time.Time(t).Format("2006-01-02 15:04:05") + `"`), nil
}
func (t *CmtDate) UnmarshalJSON(s []byte) error {
	r, err := time.ParseInLocation("2006-01-02 15:04:05", strings.Trim(string(s), `"`), time.Local)
	//	r, err := time.Parse("2006-01-02 15:04:05", strings.Trim(string(s), `"`))
	*t = CmtDate(r.Local())
	return err
}
func (t CmtInt) Int() int {
	return int(t)
}
func (t CmtInt) MarshalJSON() ([]byte, error) {
	return []byte(strconv.Itoa(int(t))), nil
}
func (t *CmtInt) UnmarshalJSON(b []byte) error {
	s := string(b)
	if len(s) >= 0 && s[0] == '"' {
		s = strings.Trim(s, `"`)
	}
	if len(s) == 0 {
		*t = CmtInt(0)
		return nil
	}
	i, err := strconv.Atoi(s)
	if err != nil {
		return err
	}
	*t = CmtInt(i)
	return nil
}

func (c CmtDict) MarshalJSON() ([]byte, error) {
	return json.Marshal(c)
}
func (c *CmtDict) UnmarshalJSON(b []byte) error {
	s := string(b)
	if len(s) >= 0 && s[0] == '[' {
		s = "{}"
	}
	x := make(map[string][]CmtBaseInfo)
	err := json.Unmarshal([]byte(s), &x)
	if err != nil {
		return err
	}
	t := CmtDict(x)
	c = &t
	return nil
}

var cmtNotExists = CmtResponse{State: "error", Error: "Not exists", ErrorCode: CmtInt(91001)}
var cmtBusy = CmtResponse{State: "error", Error: "busy", ErrorCode: CmtInt(90001)}

type sortByPros []Comment

func (a sortByPros) Len() int           { return len(a) }
func (a sortByPros) Swap(i, j int)      { a[i], a[j] = a[j], a[i] }
func (a sortByPros) Less(i, j int) bool { return a[i].Pros > (a[j].Pros) }

func makeCmtResponse(a *Article, cmts []Comment) CmtResponse {
	c := CmtResponse{}
	c.State = "success"
	c.Message = "ok"
	r := &c.Result
	r.JoinNum = CmtInt(a.Join)
	r.CommentNum = CmtInt(a.Comments)
	r.DigNum = CmtInt(a.Dig)
	r.FavNum = CmtInt(a.Fav)
	// TODO not sure what open for
	r.Open = 1
	r.Page = 1
	r.ViewNum = CmtInt(a.Read)
	r.Sid = CmtInt(a.Sid)
	r.Token = a.Token

	sort.Sort(sortByPros(cmts))

	r.CmtStore = make(map[string]CmtContent)
	for _, cmt := range cmts {
		content := CmtContent{}
		content.Sid = CmtInt(cmt.Sid)
		content.Comment = cmt.Comment
		content.Date = CmtDate(*cmt.Date)
		content.HostName = cmt.Hostname
		content.Pid = CmtInt(cmt.Pid)
		content.Tid = CmtInt(cmt.Tid)
		content.Reason = CmtInt(cmt.Cons)
		content.Score = CmtInt(cmt.Pros)
		r.CmtStore[strconv.Itoa(cmt.Tid)] = content
	}

	return c
}
