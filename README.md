![cbHistory-西贝历史评论插件](https://raw.githubusercontent.com/wenerme/cbhistory-extension/master/images/title.png)

cbHistory-西贝历史评论插件
--------------------------

这个为 cbhistory 的服务端,如果想要自己运行主要需要更改[db.properties]里的数据库连接配置,
或者在同级目录下添加一个 `app.properties` 来覆盖 `db.properties` 的配置.
关于可以设置的属性,在 [default.properties] 里有说明.

在 `doc` 目录下有一些这个程序的相关设计信息.

关于打包
--------
看看 [pom.xml] 里面的一些注释,可以轻松的设置为打包 jar/war.

自动化发现文章过程
---------

* 这个程序使用的 `spring` 的调度管理,设置的自动化发现配置主要在 
	[ScheduleEvents.java] 下面.现在的自动发现比较简单,但是还算实用.
* 在更新完一个文章过后会添加一个调度事件, 
	在 [ProcessCenter.java] 中的 `parseComment` 下.当更新一个文章评论成功后,
	会在一定时间后再次触发更新.
	
作案动机
-------

* cb 没有评论,就没有了色彩
* 很久以前就像写这么一个东西了
* 消磨 2014.5.1 的时间

相关项目
--------
* [cbhistory 服务端](https://github.com/wenerme/cbhistory)
* [cbhistory chrome 插件](https://github.com/wenerme/cbhistory-extension)

 [db.properties]:https://github.com/wenerme/cbhistory/blob/master/src/main/resources/db.properties
 [default.properties]:https://github.com/wenerme/cbhistory/blob/master/src/main/resources/default.properties
 [pom.xml]:https://github.com/wenerme/cbhistory/blob/master/pom.xml
 [ScheduleEvents.java]:https://github.com/wenerme/cbhistory/blob/master/src/main/java/me/wener/cbhistory/core/ScheduleEvents.java
 [ProcessCenter.java]:https://github.com/wenerme/cbhistory/blob/master/src/main/java/me/wener/cbhistory/core/ProcessCenter.java
	
