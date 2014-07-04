
长期目标
-------
* 优化发现的处理,调整发现的时间设置

* 实现rest接口, 作为简单的联系,以便于开发接下来的其他接口
	/op/discover/{sid}
	   /update/{sid}

* 可只更新文章信息而不更新评论
	在文章评论过期后,文章相关信息也还会在 rawdata 中,此时依然需要更新该信息

* 将发现调度的时间配置到 default.properties

* 考虑将 Comment 分离出一个 User 实体,这样 Comment 中的空字段会大大减少
	但是序列化操作的时候会比较麻烦,可以考虑 BeanCopy 拷贝.
	存储的时候也比较麻烦,需要每个对象分离出来存储
	目前和用户相关的有三个字段, icon name userid
	但是 name 不一定需要用户存在

v 0.5.8
-------
* 实现 Props 配置支持

v 0.5.5
-------
* 添加 MyBatis 模块
* 抽取出一个通用的 server 层,也便于抽取出统一的响应处理
* 实现 server-madvoc-ace

v 0.5.0
-------

* 实现一个简单的 servlet 作为服务器
	即 server-madvoc
* 实现 server-madvoc 和 server-madvoc-jetty

v 0.4.0
--------

* 实现简单的 jws 服务, 能提供评论的查询
* 使用 缓存实现 RawDataService 

v 0.3.4
--------

* 插件式的模块加载

v 0.3.3
------

* 实现按文章发布的时间区段来更新文章
* 处理了自定义的 log.level

v 0.3.2
------

* PropertiesModule 添加一个 withSystemProperties 功能
* PropertiesModule 检测当前目录

v 0.3.1
------

* 严重的设计失误, 需要将涉及到的 date 类型更正为 joda.date
	* 因为 ormlite 和 hibernate 都是有对 joda date 的支持,所以这样实现是可以的
	* 对于 Gson, 需要一个 serializer, 需要将所有 new Gson 的方法提取出来,在一个固定的地方统一创建 Gson 对象
	* 这里使用到的是 LocalDateTime

v 0.2.1
------

* 修正没有存储 pid 字段的 bug
* 修改 CBHistory 生成 rawdata 的接口, 目前重构后还没修改这个的接口
* 实现批更新事件处理

v 0.2.0
-------
* 全新的,纯 guice + ormlite 实现的 core

v 0.1.x
-------
* 使用 guice+ormlite

v 0.0.3
-------

v 0.0.2
-------

* 将 ProcessCenter 分割为几个小模块
	* 一个 ProcessCenter 处理所有操作感觉太大了,
* [暂缓] 优化事件的并发控制,避免处理相同文章的事件并发,也就是说处理相同文章的事件只能存在一个
* 在检测到较多文章时,先过滤后再分发事件,这样可以减少大量不必要的处理


遗弃列表
-------

* __不能实现__ 抽取数据库底层实现
* __放弃__ 优化 rawdata 的处理,在发现 rawdata 中没有评论时,不能保存该 rawdata,
	这里也会涉及到一个更新 rawdata 的过程,即会更新 rawdata 中文章相关数据
	* 需要修改 rawdata 的实现模式
		1. 缓存文件
		2. 手动生成 rawdata,生成步骤稍微有点复杂~
	* 不再将 rawData 存入数据库
* __放弃__ 按照文章最后更新时间来更新文章
	* 同时也需要考虑文章发布时间,否则积累的会非常多
	* 已经更改更新策略, 依然是间隔更新,而不是定时更新
	* 根据文章发布时间段来更新