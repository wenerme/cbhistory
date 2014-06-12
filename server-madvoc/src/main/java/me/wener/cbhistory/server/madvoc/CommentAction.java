package me.wener.cbhistory.server.madvoc;

import com.google.common.base.Charsets;
import javax.inject.Inject;
import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.result.RawData;
import jodd.util.MimeTypes;
import lombok.extern.slf4j.Slf4j;
import me.wener.cbhistory.server.BaseServer;
import me.wener.cbhistory.service.RawDataService;
import me.wener.cbhistory.utils.Same;

@Slf4j
@MadvocAction
public class CommentAction
{
    @Inject
    private static RawDataService rawDataSvc;
    @Inject
    private static BaseServer server;
    @In
    String op;

    @Action("/cmt")
    public RawData cmt()
    {
        return returnJson(server.getCmtResponse(op));
    }

    private RawData returnJson(Object response)
    {
        return new RawData(Same.getGson().toJson(response).getBytes(Charsets.UTF_8), MimeTypes.MIME_APPLICATION_JSON);
    }
}
