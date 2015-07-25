> ![cbHistory-西贝历史评论插件](https://raw.githubusercontent.com/wenerme/cbhistory-extension/master/images/title.png)
>
> cbHistory-西贝历史评论
>
> 给 cnbeta 上无法显示评论的文章,提供一个评论服务器.可使用 [chrome 插件][cbhistory-extension],无缝衔接.

---

# Get & Run

```bash
go get -u github.com/wenerme/cbhistory/...
go install github.com/wenerme/cbhistory/cmd/cbhistory
# Use default config
cp $GOPATH/github.com/wenerme/cbhistory/config.toml .
# Read to run
cbhistory
```

相关项目
--------
* [cbhistory 服务端][cbhistory-server]
* [cbhistory chrome 插件][cbhistory-extension]
* [cnbeta 统计信息](http://wenerme.github.io/cbhistory/)

 [cbhistory-server]:https://github.com/wenerme/cbhistory
 [cbhistory-extension]:https://github.com/wenerme/cbhistory-extension
 [db.properties]:https://github.com/wenerme/cbhistory/blob/master/core/src/main/resources/db.properties
 [default.properties]:https://github.com/wenerme/cbhistory/blob/master/core/src/main/resources/default.properties
 [pom.xml]:https://github.com/wenerme/cbhistory/blob/master/pom.xml
 
