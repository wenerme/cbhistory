
ע��
----

* cb�����۱���ʱ��Ϊ 2 ��

ʵ�ֲ���
-------

* ʵ�������ռ�
* ʵ���������
* ʵ�� ����,����chrome���
* ʵ������ͳ��

Server ���
-----------

* ͨ��more,��ȡ�� page >= 2 ����������
* �������µ�ʱ��, ���ж������Ƿ����,�������
,�����һ�� �ռ����������۵� task,���ͬʱ,�����������Ϣ
* Server ÿ�� 1m ����Ƿ������������� task,
�������ִ��,�����task��Ҫ���ռ���������.
��ִ�к� �ٴ����һ�� task,������ݾ����ʱ���ж�,Ӧ���Ǻ̵ܶ�,
��ȷ�����������Ƿ񻹻��б仯.
������ռ����µ�ʱ��,�����Ѿ�û��������,�����۱��浽 unchange,
�Ҳ������task

* ʹ�� node-cron ��ʵ�� schedule,��Ӷ�ʱ task
* ���ȴ�һ������ more ��ʼ,������Ҳ��Ҫ��ʱ����,�Լ���µ�����.
* ��Ҫע�����, node-cron �����Ҫ�ܵ���,�Ա��´ο�ʼ��ʱ�� ��������.

* ���� db��ʱ��,����ʹ�� Ƕ���nodejs db [nedb](https://github.com/louischatriot/nedb)
* ���� mongodb Ҳ��, nedb��api��mongodb���Ӽ�

�ļ���
------

* data/ �洢���ݵ��ļ���
	* unchange/ �洢�����ٸı������,Ҳ���������Ѿ����������۵�
	* change/ �洢����ı������

����
-----

* sid// ����id
* tid// �ο�id
* pid// parent id
	
�ļ�����
--------

�������ռ��׶�,�������Ҳ���Ҫ���������ݿ�,
������Ҫ���ļ�����ʽ�����ڱ���

* [sid].article.json ���µ�����
* [sid].comments.json �������۵�����

�ᷢ���ļ�������
--------------

* CommentsRequest(sid)
* ArticleList(page)

����url
: http://www.cnbeta.com/cmt

���������Ҫ������
: op ��
X-Requested-With: XMLHttpRequest

����һ�����е� cURL ����,ID Ϊ 287053
curl "http://www.cnbeta.com/cmt" -H "X-Requested-With: XMLHttpRequest" --data "op=MSwyODcwNTMsMzBhZjI"%"253DjO"%"252BfWcX"%"252F" --compressed

��һ��
-----
287931
op:MSwyODc5MzEsOTUyNGM%3DkreE8FRh

��������
-------

�ж� �Ƿ�ɹ�
�� result ���� base64 ����
���������� json �и� cnbeta ǰ׺
ȥ��ǰ׺��ת��Ϊ����

�������Ľ����ݲ�����,�Ȼ�ȡ�����������˵

op�㷨
------

̽�������� id 287931

op: encodeURIComponent(eval(
a(
"aR%91%C5%C5%D2%D3%C9%93%93%D3%A4j%5C%98%D1%C8%CC%90RSSRr%9D%84r%89%99%95%8A%95z%81%9C%8DoRSSRr%9D%84r%89%99%95%8A%95z%81%A1z%A0%E6%E7%DA%91da")))

function a(b){
b=unescape(b);
var c=String.fromCharCode(b.charCodeAt(0)-b.length);
for(var d=1;d<b.length;d++){c+=String.fromCharCode(b.charCodeAt(d)-c.charCodeAt(d-1))};return c
}

$.cbcode.en64(page+','+GV.DETAIL.SID+','+GV.DETAIL.SN,true,8)

true Ϊʹ��iunicode ,8 Ϊ sublen,�������� bash64 �ַ��ĳ���

var b64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
Math.floor(Math.random() * b64.length)

MSwyODc5MzEsOTUyNGM=7AwOqFik
=�ź�����Ǳ仯��

atob('MSwyODc5MzEsOTUyNGM=')
"1,287931,9524c"
Ҳ���� 
page+','+GV.DETAIL.SID+','+GV.DETAIL.SN

page һ���� 1,��ʱ��û��cb�Ͽ�������ҳ��
GV.DETAIL.SID
GV.DETAIL.SN
��Щֵ���� html ��,ÿ�����µ� sn ����һ��
GV.DETAIL = {SID:"287931",POST_URL:"/comment",POST_VIEW_URL:"/cmt",SN:"9524c"};
ƥ�������
^GV\.DETAIL[^\{]+(?<data>\{[^\}]+})

Ȼ�� encodeURIComponent

ҳ�����
------------

### ����ҳ��
$('#news_title') ���±���
$('.introduction p').text() ���
$('.where a').text() ��Դ

article
-------

cmntdict ��һ�����������е�����
	��˳��,һ��һ����Ƕ����ʾ��
	pid ���� id

	tid Ϊ���۵�id
	sid Ϊ���µ�id
	
cmntstore ����Ҫ��Ҫ�洢������
cmtlist
	parent �� pid �ǵȼ۵�
hotlist �����������б�

  "comment_num": "58", ������
  "join_num": "58", ��ʾ��������
  "token": "c122f8ceee68bada98d1c1499e55848b9dbe83b3",
  "view_num": 17458, �Ķ���
  "page": "1",
  "sid": "287961",
  "u": [],
  "dig_num": "12", ��
  "fav_num": "0"
	
```
"sid": "266441",
"title_show": "��ͯ����Ŧ�۵��ʳ�����մ�",
"hometext_show_short": "�ɶ��и�Ů��ͯ����ҽԺ������һ����������һöŦ�۵�ص���ͯ����Ȼ�ɹ�ȡ����أ�����ͯ��ʳ���ѱ���ؼ�Һ��ʴ��ҽ�����ѣ��ҳ�Ҫ�ú���Զ��Ŧ�۵�����಻���۵ġ�ɱ�֡����������������Ҫ��ʳ���������ҽ��",
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
<!-- �������һ��ʼ�Ϳ��Ա����, ������ǻ���ı��,���Ƿֿ����� -->
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
"name": "������ʿ",
"host_name": "����",
"comment": "�ձ�ͬѧ��������ḻ��",
"score": "0",
"reason": "0",
"userid": "0",
"icon": ""
```

* tid	������id
* pic	�����ۻظ�������
* sid	����id
* date
* name
* host_name
* comment
* userid
* icon
<!-- ���ϵ��ǲ���ı�� -->
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


