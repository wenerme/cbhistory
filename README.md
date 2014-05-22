> ![cbHistory-西贝历史评论插件](https://raw.githubusercontent.com/wenerme/cbhistory-extension/master/images/title.png)
>
> cbHistory-西贝历史评论
>
> 给 cnbeta 上无法显示评论的文章,提供一个评论服务器.可使用 chrome 插件,无缝衔接.

---

目录
----

- [core-数据收集模块](#core-数据收集模块)
- [项目历程](#项目历程)
- [作案动机](#作案动机)
- [相关项目](#相关项目)

core-数据收集模块
------------

cbhistory 的数据收集模块,如果想要自己运行主要需要参考[db.properties]里的数据库连接配置,
和 [default.properties] 中的默认配置来修改参数.可以在运行目录下建立一个 `app.properties`
来覆盖这些设置.

在 `doc` 目录下有一些这个程序的相关设计信息.

### 主要功能

* 自动化文章/评论发现入库
* 定时触发发现功能
* 定时发现即将过期的文章

项目历程
-----

* 从新改造后,主要实现一个 core 模块,仅仅实现数据收集入库的功能  
	使用的架构为 Guice + Ormlite,暂未完成提供评论数据的服务端,收集数据为主要任务  
	整个服务端的运行时消耗霎时减小.目前运行时内存差不多 500M 左右
* 但是由于这一套框架太过于庞大了,我本地运行起来要使用 3G+ 的内存,所以准备了改造计划.
* 最开始由 Spring Data, Spring Schedule, JPA(Hibernate), Spring Web 实现了完整的客户端  
	配合 cbhistory 插件能完成取代 cb 评论服务器


作案动机
-------

* cb 没有评论,就没有了色彩
* 很久以前就像写这么一个东西了
* 消磨 2014.5.1 的时间

相关项目
--------
* [cbhistory 服务端](https://github.com/wenerme/cbhistory)
* [cbhistory chrome 插件](https://github.com/wenerme/cbhistory-extension)

 [db.properties]:https://github.com/wenerme/cbhistory/blob/master/core/src/main/resources/db.properties
 [default.properties]:https://github.com/wenerme/cbhistory/blob/master/core/src/main/resources/default.properties
 [pom.xml]:https://github.com/wenerme/cbhistory/blob/master/pom.xml
 
