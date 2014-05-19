package me.wener.cbhistory.core.process;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import java.util.Collection;
import java.util.Collections;
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

        // 不在这里做判断,只在更新文章的时候做判断
//        if (!isArticleNeedUpdate(article))
//        {
//            log.debug("文章尚不需要更新. {}", article);
//            return;
//        }

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
        }

        // FIXME 移除该段,不在需要从文章处获取rowData
//        RawData raw = article.getRawData();
//        if (raw == null)
//            raw = gson.fromJson(response.bodyText(), RawData.class);
//        else
//            CodecUtils.jsonMergeTo(response.bodyText(), raw);

        RawData raw = gson.fromJson(response.bodyText(), RawData.class);
        if (!raw.getStatus().equals("success"))
        {
            log.error("获取到的评论内容状态异常 :{}, 文章为: {}", raw, article);
            return;
        }

            // FIXME 移除该段, Row 数据不在需要保存,只需要更新,并且为动态生成
//        // 设置好关系
//        article.setRawData(raw);
//        raw.setSid(article.getSid());

        Events.post(new UpdateCommentEvent(article, raw).setPage(e.getPage()));
    }

    @Subscribe
    @AllowConcurrentEvents
    public void parseCommentCorrect(UpdateCommentEvent e)
    {
        Article article = e.getArticle();

        log.debug("更新评论: {}", e);

        String result = CodecUtils.decodeBase64(e.getRawContent().getResult());
        result = result.replaceFirst("^cnbeta", "");// 去除前缀
        RawComment rawComment;
        Collection<Comment> comments;
        {
            // FIXME 移除, 不再需要理会已经存在的评论信息
//            if (comments == null)
//            {
//                comments = Sets.newHashSet();
//                article.setComments(comments);
//            }
//            Map<String, Comment> cmtMap = Maps.newHashMap();
//
//            for (Comment comment : comments)
//                cmtMap.put(comment.getTid().toString(), comment);
//
//            rawComment.setCommentList(cmtMap);
        }
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

        article.setLastUpdateDate(new Date());

        // 保存状态
        // 这里的更新稍微有点麻烦,但是为了确保正确性,也就这样了
//        Iterable<Comment> saved = commentSvc.save(comments);
        commentSvc.save(comments);
//        article.setComments(Sets.newHashSet(saved));
        article = articleSvc.save(article);

        log.info("更新文章评论第 {} 页, 共 {} 条评论. 文章 sid: {}"
                , e.getPage(), comments.size(), article.getSid());

        // 添加下次更新的事件调度
        {
            // TODO 发布调度事件
            // 距离失效前60分钟
//            DateTime expiredDate = getCommentExpiredDate(article).minusMinutes(60);
//            TryFoundArticleEvent event = new TryFoundArticleEvent(article.getSid());
//            scheduler.schedule(event, expiredDate.toDate());
        }

        // 如果当前获取的有内容,更新完当前页,尝试更新下一页
        if (comments.size() > 0)
        {
            Events.post(new TryUpdateCommentEvent(article).setPage(e.getPage() + 1));
        }else
        {
            log.info("完成文章评论的更新. sid: {}", article.getSid());
            Events.finish(e);
        }

    }

}
