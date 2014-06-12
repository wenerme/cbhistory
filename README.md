> ![cbHistory-西贝历史评论插件](https://raw.githubusercontent.com/wenerme/cbhistory-extension/master/images/title.png)
>
> cbHistory-西贝历史评论
>
> 给 cnbeta 上无法显示评论的文章,提供一个评论服务器.可使用 [chrome 插件][cbhistory-extension],无缝衔接.

---

目录
----

- [模块概述](#模块概述)
- [杂谈](#杂谈)
    - [项目历程](#项目历程)
    - [作案动机](#作案动机)
- [技术细节](#技术细节)
- [相关项目](#相关项目)

模块概述
=======

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

server-jws 简单的 jws 服务实现
-----------

能够提供基础的评论查询服务,无任何依赖, 默认的地址端口为 `plugin.server.jws.url = http://127.0.0.1:8880/cmt`,
该配置可以被覆盖.

杂谈
====

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
* 作为学习的练习项目  
	熟悉一些技术的使用和开发  
	锻炼自己的实践能力

技术细节
========

既然作为练习项目,肯定要牵扯很多需要练习和学习的技术,在这里按模块详细整理下

parent
-------
项目的基础配置模块

* lombok
	项目中主要的编程模式,大量的使用了lombok提供的功能
	几乎所有的 Setter/Getter, ToString, HashCode, Equals 都是生成的
* guava
	谷歌的辅助工具包,guava在整个项目中大量使用
* joda-time
	项目中实体数据均是使用的 joda-time,在计算的时候也便于处理
	jpa 和 ormlite 均使用了相应的办法来支持 joda-time
	实体类上主要用的是 LocalDateTime, 没有储存时区,这样操作也比较合理
	
core
-----
数据收集核心模块

* gson
	序列化和解析 json数据
* jodd-http
	辅助网络获取的操作
* jodd-largarto
	解析网页
* jodd-props
	使用 props 作为配置文件
* jodd-bean
	beanUtils, 主要用来偷懒, 拷贝bean属性
* guice
	依赖注入
* mycila-guice-jsr250
	对 guice 的扩展,支持 jsr250 标签
* javax.inject
	使用标准的注入注解
* ormlite-core, ormlite-jdbc
	在 core 中使用的轻量级的 orm 工具
* c3p0
	连接池咯
* slf4j, log4j-over-slf4j, jcl-over-slf4j
	整个日志记录系统是使用的 slf4j 的来做的转接
* logback-classic
	slf4j 使用 logback 来记录的日志

### 实现细节

文章的抓取解析,评论抓取解析 等操作均是通过事件驱动的,主要使用的是 guava 中的 `EventBus`,在这里是使用的异步的 `EventBus`, 因此不管哪个步骤出现异常, 均对程序的运行没有任何影响.

core 中主要是使用的 guice 来实现的 `DI`, guice 够轻量级,而且专注于依赖注入, 配置模块的 dsl 也非常友好.在这里还实现了注入可选的 properties 文件,注入插件式的模块. 插件式的模块加载是使用的 guava 中的反射来扫描的 `me.wener.cbhistory.modules` 包进行动态的模块发现和加载,有了这一步后才能能动态的加载其他模块.

在 core 里,为了尽量的简单,所以使用的是 ormlite 来做的 orm, 虽然是轻量级的框架, 但是操作起来也非常方便和省力.主要便于直接操作 dao 对象.

除此之外, 在 core 里还有强大的配置实现和动态的配置加载功能. 对于插件来说, 配置信息是可以动态加载的. 非常便于插件的协调配置, 并且 props 的配置非常简单而且实用, 也是支持 utf-8 的, 程序中还实现了注入 list 和 map 两种简单的容器配置. 配置能力非常强.

定时发现调度非常简单, 数量也不是很多, 所以干脆就直接使用的 Timer, 而没有引入其他的调度库. So far so good.

server-jws
----------
使用 jdk 自带的 jws 来提供了一个非常简单的评论查询服务. 不过由于 jws 不能返回非 XML 数据. 所以在返回的 json 外面套了一层 `<cbhistory>` 标签

server-madvoc
-------------

* jodd-madvo
    一款简单的 mvc 框架

非常轻量级的服务器解决方案, 使用 servlet 作为容器.

server-madvoc-jetty
-------------------

使用内嵌的 `jetty` 来运行 `server-madvoc`, 因此不需要部署也能直接的在本地运行.

analysis
--------
好戏一般放在后头,对么 ? 作为数据分析统计的基础模块, 主要提供简单的查询和 高大上的工具.

* guice-spring
    集成 spring 到 guice
* spring-boot-starter-data-jpa
    因为偷懒,直接使用的 spring boot 的依赖
    里面包含了 hibernate 的 jpa 实现 和 spring data
* querydsl-jpa, querydsl-apt
    提供 querydsl 支持, 高大上的查询工具
* usertype.core
    让 hibernate 可以使用一些其他的类型作为存储,目前主要为了支持 joda-time

作为统计分析的基础模块, 主要提供 jap+spring data 的持久层解决方案, 为了辅助查询, 引入了 querydsl. 虽然直接依赖较少, 但是在背后是引入了非常的东西的, 整套的 hibernate 和 spring
都引入了进来. 为其他的操作做一个铺垫.

persistence-mybatis
------------------
在学习 MyBatis 的时候引入了该模块, 目前实现了简单的查询, MyBatis 的主要优势也是高效的查询. 而且并没有引入新的实体类型, MaBatis 的 Mapper 操作返回的依然是 Core 中的实体类型. 因为实体类型中使用了 joda-time, 添加了对 DateTime 和 LocalDateTime 类型的支持.

相关项目
--------
* [cbhistory 服务端][cbhistory-server]
* [cbhistory chrome 插件][cbhistory-extension]

 [cbhistory-server]:https://github.com/wenerme/cbhistory
 [cbhistory-extension]:https://github.com/wenerme/cbhistory-extension
 [db.properties]:https://github.com/wenerme/cbhistory/blob/master/core/src/main/resources/db.properties
 [default.properties]:https://github.com/wenerme/cbhistory/blob/master/core/src/main/resources/default.properties
 [pom.xml]:https://github.com/wenerme/cbhistory/blob/master/pom.xml
 
