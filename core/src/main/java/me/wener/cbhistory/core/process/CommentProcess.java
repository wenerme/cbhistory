package me.wener.cbhistory.core.process;

import com.google.common.base.Strings;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import java.util.Collection;
import javax.inject.Named;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import me.wener.cbhistory.core.CBHistory;
import me.wener.cbhistory.core.Events;
import me.wener.cbhistory.core.event.process.TryFoundArticleEvent;
import me.wener.cbhistory.core.event.process.TryUpdateCommentEvent;
import me.wener.cbhistory.core.event.process.UpdateCommentEvent;
import me.wener.cbhistory.domain.entity.Article;
import me.wener.cbhistory.domain.entity.Comment;
import me.wener.cbhistory.domain.RawComment;
import me.wener.cbhistory.domain.RawData;
import me.wener.cbhistory.util.CodecUtils;
import org.joda.time.LocalDateTime;

@Named
@Slf4j
public class CommentProcess extends CommonProcess
{

    public static final int COMMENTS_PRE_PAGE = 99;

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

        String op = CBHistory.calcOp(article, e.getPage());
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
        }else if(response.statusCode() != 200)
        {
            log.error("获取 URL 返回状态码异常 status: {} 请求的url为: {},参数op: {} 文章: {}"
                    , response.statusCode(), url, op, article);
            return;
        }

        RawData raw = gson.fromJson(response.bodyText(), RawData.class);
        if (raw == null)
        {
            log.error("转换的 rawData 为 null SID: {}", article.getSid());
            return;
        }
        if (!raw.getStatus().equals("success"))
        {
            log.error("获取到的评论内容状态异常 :{}, SID: {}. 可能请求太频繁", raw, article.getSid());
            return;
        }

        Events.post(new UpdateCommentEvent(article, raw).setPage(e.getPage()));
    }

    @Subscribe
    @AllowConcurrentEvents
    public void parseComment(UpdateCommentEvent e)
    {
        Article article = e.getArticle();

        log.debug("更新评论: {}", e);

        String result = CodecUtils.decodeBase64(e.getRawContent().getResult());
        result = result.replaceFirst("^cnbeta", "");// 去除前缀
        RawComment rawComment;
        Collection<Comment> comments;

        // 解析
        rawComment = gson.fromJson(result, RawComment.class);
        // 更新文章附加的其他信息
        CodecUtils.jsonMergeTo(result, article);

        comments = rawComment.getCommentList().values();

        for (Comment comment : comments)
        {
            // 将 pid 为 0 的值置为空, 因为 id 为 0 的评论是不存在的
            if (comment.getPid() != null && comment.getPid() == 0)
                comment.setPid(null);
            // 将匿名人士的名字设置为 null
            if (comment.getName() != null && comment.getName().equals("匿名人士"))
                comment.setName(null);
            // 不存储 userId = 0
            if (comment.getUserId() != null && comment.getUserId() == 0)
                comment.setUserId(null);
            // 有可能为空字符串
            if (comment.getIcon() != null && comment.getIcon().equals(""))
                comment.setIcon(null);
        }


        article.setLastUpdateDate(LocalDateTime.now());
        articleSvc.save(article);
        commentSvc.save(comments);

        log.info("更新文章评论第 {} 页, 共 {} 条评论. 文章 sid: {}"
                , e.getPage(), comments.size(), article.getSid());


        // 如果文章的条数少于默认文章一页的条数,则尝试更新下一页
        if (comments.size() < COMMENTS_PRE_PAGE)
        {
            log.info("完成文章评论的更新. sid: {}", article.getSid());
            Events.finish(e);
        } else
        {
            Events.post(new TryUpdateCommentEvent(article).setPage(e.getPage() + 1));
        }

    }

}
