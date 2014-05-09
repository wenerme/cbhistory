package me.wener.cbhistory.core.controller;

import com.google.gson.Gson;
import javax.inject.Inject;
import me.wener.cbhistory.core.CBHistory;
import me.wener.cbhistory.domain.OpInfo;
import me.wener.cbhistory.domain.RawData;
import me.wener.cbhistory.domain.StatusResponse;
import me.wener.cbhistory.repositories.RawDataRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CBAdapterController
{
    @Inject
    private RawDataRepository rawDataRepository;

    private Gson gson = new Gson();

    @RequestMapping(
            value = "/cmt",
            method = {RequestMethod.POST, RequestMethod.GET})
    public StatusResponse cmt(@RequestParam("op") String op)
    {
        RawData rawData = null;
        StatusResponse response = new StatusResponse();
        Long sid = null;
        try
        {
            OpInfo info = CBHistory.parseOp(op);
            sid = info.getSid();
        } catch (Exception ignored)
        {
        }// 只是获取到了错误的数据格式,不用管它
        // 允许 op 直接为 sid,要求没有 cb 原本的那么严格.
        if (sid == null)
        {
            try
            {
                sid = Long.parseLong(op);
            } catch (Exception ignored)
            {
            }
        }

        if (sid != null)
            rawData = rawDataRepository.findOne(sid);

        if (rawData == null)
        {
            // {"status":"error","result":"busy"}
            response.setStatus("error");
            response.setResult("busy");
        } else
        {
            response.setStatus(rawData.getStatus());
            response.setResult(rawData.getResult());
        }

        return response;
    }
}
