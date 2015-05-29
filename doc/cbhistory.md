
注意
----

* cb的评论保存时间为 2 天

实现步骤
-------

* 实现数据收集
* 实现数据入库
* 实现 适配,开发chrome插件
* 实现数据统计

Server 框架
-----------

* 通过more,获取到 page >= 2 的所有文章
* 检查该文章的时间, 以判断评论是否可用,如果可用
,则添加一个 收集该文章评论的 task,与此同时,缓存该文章信息
* Server 每隔 1m 检查是否有满足条件的 task,
如果有则执行,这里的task主要是收集文章评论.
当执行后 再次添加一次 task,这个根据具体的时间判断,应该是很短的,
以确保文章评论是否还会有变化.
如果在收集文章的时候,发现已经没有评论了,则将评论保存到 unchange,
且不在添加task

* 使用 node-cron 来实现 schedule,添加定时 task
* 首先从一个解析 more 开始,该任务也需要定时运行,以检测新的文章.
* 需要注意的是, node-cron 最好需要能导出,以便下次开始的时候 继续运行.

* 集成 db的时候,考虑使用 嵌入的nodejs db [nedb](https://github.com/louischatriot/nedb)
* 或者 mongodb 也行, nedb的api是mongodb的子集

文件夹
------

* data/ 存储数据的文件夹
	* unchange/ 存储不会再改变的数据,也就是文章已经不能再评论的
	* change/ 存储还会改变的数据

名词
-----

* sid// 文章id
* tid// 游客id
* pid// parent id
	
文件命名
--------

在数据收集阶段,数据暂且不需要保存在数据库,
所以需要以文件的形式保存在本地

* [sid].article.json 文章的数据
* [sid].comments.json 文章评论的数据

会发出的几种请求
--------------

* CommentsRequest(sid)
* ArticleList(page)

评论url
: http://www.cnbeta.com/cmt

发送请求必要的两项
: op 和
X-Requested-With: XMLHttpRequest

这是一个可行的 cURL 请求,ID 为 287053
curl "http://www.cnbeta.com/cmt" -H "X-Requested-With: XMLHttpRequest" --data "op=MSwyODcwNTMsMzBhZjI"%"253DjO"%"252BfWcX"%"252F" --compressed

另一个
-----
287931
op:MSwyODc5MzEsOTUyNGM%3DkreE8FRh

解析过程
-------

判断 是否成功
对 result 解析 base64 解码
解析出来的 json 有个 cnbeta 前缀
去除前缀后转换为对象

接下来的解析暂不考虑,先获取到最基本的再说

op算法
------

探究的文章 id 287931

op: encodeURIComponent(eval(
a(
"aR%91%C5%C5%D2%D3%C9%93%93%D3%A4j%5C%98%D1%C8%CC%90RSSRr%9D%84r%89%99%95%8A%95z%81%9C%8DoRSSRr%9D%84r%89%99%95%8A%95z%81%A1z%A0%E6%E7%DA%91da")))

function a(b){
b=unescape(b);
var c=String.fromCharCode(b.charCodeAt(0)-b.length);
for(var d=1;d<b.length;d++){c+=String.fromCharCode(b.charCodeAt(d)-c.charCodeAt(d-1))};return c
}

$.cbcode.en64(page+','+GV.DETAIL.SID+','+GV.DETAIL.SN,true,8)

true 为使用iunicode ,8 为 sublen,随意生成 bash64 字符的长度

var b64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
Math.floor(Math.random() * b64.length)

MSwyODc5MzEsOTUyNGM=7AwOqFik
=号后面的是变化的

atob('MSwyODc5MzEsOTUyNGM=')
"1,287931,9524c"
也就是 
page+','+GV.DETAIL.SID+','+GV.DETAIL.SN

page 一般是 1,暂时还没在cb上看到过翻页的
GV.DETAIL.SID
GV.DETAIL.SN
这些值都在 html 里,每个文章的 sn 都不一样
GV.DETAIL = {SID:"287931",POST_URL:"/comment",POST_VIEW_URL:"/cmt",SN:"9524c"};
匹配出该行
^GV\.DETAIL[^\{]+(?<data>\{[^\}]+})

然后 encodeURIComponent

页面解析
------------

### 文章页面
$('#news_title') 文章标题
$('.introduction p').text() 简介
$('.where a').text() 稿源

article
-------

cmntdict 是一条评论里所有的内容
	按顺序,一个一个的嵌入显示的
	pid 即父 id

	tid 为评论的id
	sid 为文章的id
	
cmntstore 是主要需要存储的内容
cmtlist
	parent 和 pid 是等价的
hotlist 是热门评论列表

  "comment_num": "58", 评论数
  "join_num": "58", 显示的评论数
  "token": "c122f8ceee68bada98d1c1499e55848b9dbe83b3",
  "view_num": 17458, 阅读量
  "page": "1",
  "sid": "287961",
  "u": [],
  "dig_num": "12", 顶
  "fav_num": "0"
	
```
"sid": "266441",
"title_show": "男童误吞纽扣电池食道被烧穿",
"hometext_show_short": "成都市妇女儿童中心医院抢救了一名意外吞下一枚纽扣电池的男童，虽然成功取出电池，但男童的食道已被电池碱液腐蚀。医生提醒，家长要让孩子远离纽扣电池这类不起眼的“杀手”。孩子误吞异物后要禁食，并尽快就医。",
"logo": "http://static.cnbetacdn.com/topics/alert.png",
"url_show": "/articles/266441.htm",
"counter": "6434",
"comments": "21",
"score": "-1",
"time": "2013-12-30 16:13:48"
```

* sid
* title
* intro
* logo
* time
<!-- 上面的是一开始就可以保存的, 下面的是还会改变的,考虑分开保存 -->
* counter
* comments
* score

comment
-------
```
"tid": "8164628",
"pid": "0",
"sid": "266603",
"date": "2014-01-01 02:47:06",
"name": "匿名人士",
"host_name": "美国",
"comment": "日本同学想象力真丰富。",
"score": "0",
"reason": "0",
"userid": "0",
"icon": ""
```

* tid	该评论id
* pic	该评论回复的评论
* sid	文章id
* date
* name
* host_name
* comment
* userid
* icon
<!-- 以上的是不会改变的 -->
* score
* reason


POST /cmt HTTP/1.1
Host: www.cnbeta.com
Connection: keep-alive
Content-Length: 43
Cache-Control: no-cache
Pragma: no-cache
Origin: http://www.cnbeta.com
X-Requested-With: XMLHttpRequest
Content-Type: application/x-www-form-urlencoded; charset=UTF-8
Accept: application/json, text/javascript, */*; q=0.01
User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.131 Safari/537.36
DNT: 1
Referer: http://www.cnbeta.com/articles/287053.htm
Accept-Encoding: gzip,deflate,sdch
Accept-Language: zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4
Cookie: bdshare_firstime=1378828789490; __atuvc=1%7C39; Hm_lvt_4216c57ef1855492a9281acd553f8a6e=1379308005,1379404132,1379913100,1380685545; __utmz=208385984.1396521174.35.1.utmccn=(direct)|utmcsr=(direct)|utmcmd=(none); YII_CSRF_TOKEN=67cf464da5ba82ba6ca7ba4a5b48537fcc6fa063; PHPSESSID=i3mkpku8ik59reh0advlmuqf71; __utma=208385984.245992300.1378828785.1398848292.1398852693.41; __utmc=208385984; tmc=3.208385984.27610461.1398848478010.1398852695812.1398852709956; tma=208385984.93138167.1378828786306.1386306253046.1386596181248.10; tmd=256.208385984.93138167.1378828786306.; bfd_session_id=bfd_g=1339c62e29d72396e12c1de3ccebc9d&bfd_s=208385984.79002531.1398852695805; __utmb=208385984


