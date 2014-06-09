package me.wener.cbhistory.server;

import com.google.common.base.Strings;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import me.wener.cbhistory.core.CBHistory;
import me.wener.cbhistory.domain.OpInfo;
import me.wener.cbhistory.domain.RawData;
import me.wener.cbhistory.domain.StatusResponse;
import me.wener.cbhistory.service.RawDataService;

@Slf4j
public class BaseServer
{
    @Inject
    private RawDataService rawDataSvc;

    public StatusResponse getCmtResponse(String op)
    {
        StatusResponse response = null;
        RawData rawData = null;
        try
        {
            if (Strings.isNullOrEmpty(op))
                return StatusResponse.error("查询参数为空.");
            rawData = getRawDataByOp(op);
        } catch (Exception ex)
        {
            log.error("查询 rawData 发生异常", ex);
            response = StatusResponse.error("查询失败.");
        }

        if (response == null && rawData == null)
            response = StatusResponse.error("无结果数据.");

        return response;
    }

    public RawData getRawDataByOp(String op) throws RuntimeException
    {
        OpInfo info = null;
        info = CBHistory.parseOp(op);

        if (info != null)
            return rawDataSvc.findBySid(info.getSid(), info.getPage());

        return null;
    }
}
