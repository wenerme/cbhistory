package me.wener.cbhistory.core.process;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import javax.inject.Named;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import me.wener.cbhistory.core.CBHistory;
import me.wener.cbhistory.core.Events;
import me.wener.cbhistory.core.event.TryFoundArticleEvent;
import me.wener.cbhistory.core.event.TryUpdateCommentEvent;
import me.wener.cbhistory.core.event.UpdateCommentEvent;
import me.wener.cbhistory.domain.Article;
import me.wener.cbhistory.domain.Comment;
import me.wener.cbhistory.domain.RawComment;
import me.wener.cbhistory.domain.RawData;
import me.wener.cbhistory.util.CodecUtils;
import org.joda.time.DateTime;

@Named
@Slf4j
public class CommentProcess extends CommonProcess
{

    @Subscribe
    @AllowConcurrentEvents
    public void getCommentFromInternet(TryUpdateCommentEvent e)
    {
        Article article = e.getArticle();

        log.debug("处理尝试更新文章事件, {}", article);

        if (!isArticleNeedUpdate(article))
        {
            log.debug("文章尚不需要更新. {}", article);
            return;
        }

        String op = CBHistory.calcOp(article);
        String url = "http://www.cnbeta.com/cmt";
        HttpRequest request = HttpRequest.post(url);
        request
                .contentType("application/x-www-form-urlencoded")
                .header("X-Requested-With", "XMLHttpRequest")
                .form("op", op);

        HttpResponse response = insureResponse(request);
        if (response == null)
        {
            log.error("获取评论失败,无法获取相应,请求的url为: {},参数op: {} 文章: {}"
                    , url, op, article);
            return;
        }

        RawData raw = article.getRawData();
        if (raw == null)
            raw = gson.fromJson(response.bodyText(), RawData.class);
        else
            CodecUtils.jsonMergeTo(response.bodyText(), raw);

        if (!raw.getStatus().equals("success"))
        {
            log.error("获取到的评论内容状态异常 :{}, 文章为: {}", raw, article);
            return;
        }

        // 设置好关系
        article.setRawData(raw);
        // TODO 保存 raw 数据
        raw.setSid(article.getSid());

        Events.post(new UpdateCommentEvent(article, raw));
    }

    @Subscribe
    @AllowConcurrentEvents
    public void parseComment(UpdateCommentEvent e)
    {
        Article article = e.getArticle();
        article = articleSvc.save(article);

        log.debug("更新评论: {}", e);

        String result = CodecUtils.decodeBase64(e.getRawContent().getResult());
        result = result.replaceFirst("^cnbeta", "");// 去除前缀
        RawComment rawComment = new RawComment();
        // 设置好已有的评论
        Set<Comment> comments = Sets.newHashSet(article.getComments());
        {
            if (comments == null)
            {
                comments = Sets.newHashSet();
                article.setComments(comments);
            }
            Map<String, Comment> cmtMap = Maps.newHashMap();

            for (Comment comment : comments)
                cmtMap.put(comment.getTid().toString(), comment);

            rawComment.setCommentList(cmtMap);
        }
        // 更新
        CodecUtils.jsonMergeTo(result, rawComment);
        CodecUtils.jsonMergeTo(result, article);

        comments.addAll(rawComment.getCommentList().values());

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

        article.setLastUpdateDate(new Date());

        // 保存状态
        // 这里的更新稍微有点麻烦,但是为了确保正确性,也就这样了
        Iterable<Comment> saved = commentSvc.save(article.getComments());
        article.setComments(Sets.newHashSet(saved));
        article = articleSvc.save(article);

        log.info("完成文章的更新. {}", article);
        // 添加下次更新的事件调度
        {
            // 距离失效前60分钟
            DateTime expiredDate = getCommentExpiredDate(article).minusMinutes(60);
            TryFoundArticleEvent event = new TryFoundArticleEvent(article.getSid());
            scheduler.schedule(event, expiredDate.toDate());
        }

        Events.finish(e);
    }
}
