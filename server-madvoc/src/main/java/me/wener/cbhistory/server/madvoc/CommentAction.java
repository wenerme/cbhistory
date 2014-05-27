package me.wener.cbhistory.server.madvoc;

import com.google.common.base.Charsets;
import javax.inject.Inject;
import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.result.RawData;
import jodd.util.MimeTypes;
import lombok.extern.slf4j.Slf4j;
import me.wener.cbhistory.core.CBHistory;
import me.wener.cbhistory.domain.OpInfo;
import me.wener.cbhistory.domain.StatusResponse;
import me.wener.cbhistory.service.RawDataService;
import me.wener.cbhistory.util.Same;

@Slf4j
@MadvocAction
public class CommentAction
{
    @Inject
    private static RawDataService rawDataSvc;

    @In
    String op;

    @Action("/cmt")
    public RawData cmt()
    {
        StatusResponse response = null;
        OpInfo info = null;
        try
        {
            info = CBHistory.parseOp(op);
        } catch (Exception ex)
        {
            response = StatusResponse.error("解析 OP 参数错误 " + op);
        }

        if (info != null)
        {
            try
            {
                me.wener.cbhistory.domain.RawData rawData = rawDataSvc.findBySid(info.getSid(), info.getPage());
                if (rawData != null)
                    response = StatusResponse.success(rawData);
                else
                    response = StatusResponse.error("无结果数据.");
            } catch (Exception e)
            {
                log.error("查询出现异常 " + info, e);
                response = StatusResponse.error("查询过程中发生异常: " + e.getMessage());
            }
        }

        return returnJson(response);
    }

    private RawData returnJson(Object response)
    {
        return new RawData(Same.getGson().toJson(response).getBytes(Charsets.UTF_8), MimeTypes.MIME_APPLICATION_JSON);
    }
}
