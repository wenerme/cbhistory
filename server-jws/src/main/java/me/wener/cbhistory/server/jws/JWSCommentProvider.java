package me.wener.cbhistory.server.jws;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;
import javafx.animation.Animation;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.BindingType;
import javax.xml.ws.Provider;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.http.HTTPBinding;
import jodd.http.HttpUtil;
import jodd.http.HttpValuesMap;
import lombok.Setter;
import me.wener.cbhistory.core.CBHistory;
import me.wener.cbhistory.domain.OpInfo;
import me.wener.cbhistory.domain.RawData;
import me.wener.cbhistory.domain.StatusResponse;
import me.wener.cbhistory.domain.entity.Article;
import me.wener.cbhistory.service.ArticleService;
import me.wener.cbhistory.service.CommentService;
import me.wener.cbhistory.service.RawDataService;
import me.wener.cbhistory.util.Same;

@WebServiceProvider
@BindingType(HTTPBinding.HTTP_BINDING)
public class JWSCommentProvider implements Provider<Source>
{
    @Setter
    private ArticleService articleSvc;
    @Setter
    private CommentService commentSvc;
    @Setter
    private RawDataService rawDataSvc;

    // 由于这个注入不是用 guice,所以该对象不能由 guice 来实例化
    // 也就是 所有的 service 需要自己手动注入
    @Resource
    WebServiceContext wsContext;

    public JWSCommentProvider()
    {
    }

    public JWSCommentProvider(ArticleService articleSvc, CommentService commentSvc, RawDataService rawDataSvc)
    {
        this.articleSvc = articleSvc;
        this.commentSvc = commentSvc;
        this.rawDataSvc = rawDataSvc;
    }

    @Override
    public Source invoke(Source request)
    {
        MessageContext msgContext = wsContext.getMessageContext();
        String queryString = (String) msgContext.get(MessageContext.QUERY_STRING);
        StatusResponse response = null;
        if (Strings.isNullOrEmpty(queryString))
            response = StatusResponse.error("无操作");
        else{
            HttpValuesMap queries = HttpUtil.parseQuery(queryString, true);
            String param = (String) queries.getFirst("op");
            if (! Strings.isNullOrEmpty(param))
                response = getResponseByOP(param);
        }

        if (response == null)
            response = StatusResponse.error("未知的操作: "+queryString);


        return returnJson(response);
    }
    public Source returnJson(Object obj)
    {
        String result = "<cbhistory>%s</cbhistory>";
        result = String.format(result, Same.getGson().toJson(obj));
        return new StreamSource(new ByteArrayInputStream(result.getBytes(Charsets.UTF_8)));
    }

    private StatusResponse getResponseByOP(String op)
    {
        StatusResponse response = null;
        OpInfo info = null;
        try
        {
            info = CBHistory.parseOp(op);
        }catch (Exception ex)
        {
            return StatusResponse.error("解析 OP 参数错误 "+op);
        }

        Article article = articleSvc.findOne(info.getSid());
        if (article == null)
            return StatusResponse.error("所查找的文章不存在, 当前信息为"+info);

        RawData rawData = CBHistory.getRawDataFrom(article, commentSvc.findAllBySid(article.getSid()));

        return StatusResponse.success(rawData);
    }
}
