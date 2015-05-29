package me.wener.cbhistory.core.process;

import com.google.common.base.Strings;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import java.util.Collection;
import javax.inject.Named;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import me.wener.cbhistory.core.Events;
import me.wener.cbhistory.core.event.process.TryFoundArticleEvent;
import me.wener.cbhistory.core.event.process.TryUpdateCommentEvent;
import me.wener.cbhistory.core.event.process.UpdateCommentEvent;
import me.wener.cbhistory.domain.entity.Article;
import me.wener.cbhistory.domain.entity.Comment;
import me.wener.cbhistory.parser.CnBetaParser;
import me.wener.cbhistory.parser.Response;
import org.joda.time.LocalDateTime;

@Named
@Slf4j
public class CommentProcess extends CommonProcess
{
    public static final int COMMENTS_PRE_PAGE = 80;
    @Inject
    CnBetaParser parser;

    @Subscribe
    @AllowConcurrentEvents
    public void downloadCommentData(TryUpdateCommentEvent e)
    {
        Article article = e.getArticle();

        log.debug("处理尝试更新文章事件, {}", article);

        if (Strings.isNullOrEmpty(article.getSn()))
        {
            log.error("尝试更新评论的文章无 sn. sid:{}", article.getSid());
            Events.post(new TryFoundArticleEvent(article.getSid()));
            return;
        }

        String op = parser.opCode(article, e.getPage());
        String url = "http://www.cnbeta.com/cmt";
        HttpRequest request = HttpRequest.post(url);
        request
                .contentType("application/x-www-form-urlencoded")
                .header("X-Requested-With", "XMLHttpRequest")
                .form("op", op);

        HttpResponse response = insureResponse(request);
        if (response == null)
        {
            log.error("获取评论失败,无法获取响应,请求的url为: {},参数op: {} 文章: {}"
                    , url, op, article);
            return;
        }

        if (response.statusCode() != 200)
        {
            log.error("获取 URL 返回状态码异常 status: {} 请求的url为: {},参数op: {} 文章: {}"
                    , response.statusCode(), url, op, article);
            return;
        }

        String body = response.bodyText();
        if (Strings.isNullOrEmpty(body))
        {
            log.warn("请求返回空字符串 请求的url为: {}, op:{} SID:{}", url, op, article.getSid());
            return;
        }

        Response status = parser.asResponse(body);
        if (!status.isSuccess())
        {
            log.error("获取到的评论内容状态异常 :{}, SID: {}. 可能请求太频繁", status, article.getSid());
            return;
        }

        Events.post(new UpdateCommentEvent(article, status).setPage(e.getPage()));
    }

    @Subscribe
    @AllowConcurrentEvents
    public void parseComment(UpdateCommentEvent e)
    {
        Response content = e.getContent();
        Article article = e.getArticle();

        log.debug("更新评论: {}", e);

        Collection<Comment> comments = parser.asComments(article, content);


        article.setLastUpdateDate(LocalDateTime.now());
        articleSvc.save(article);
        commentSvc.save(comments);

        log.debug("更新文章评论第 {} 页, 共 {} 条评论. 文章 sid: {}"
                , e.getPage(), comments.size(), article.getSid());


        // 如果文章的条数少于默认文章一页的条数,则尝试更新下一页
        if (comments.size() < COMMENTS_PRE_PAGE)
        {
            log.debug("完成文章评论的更新. sid: {}", article.getSid());
            Events.finish(e);
        } else
        {
            Events.post(new TryUpdateCommentEvent(article).setPage(e.getPage() + 1));
        }

    }

}
