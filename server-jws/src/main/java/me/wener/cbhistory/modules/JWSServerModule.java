package me.wener.cbhistory.modules;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Injector;
import javax.xml.ws.Endpoint;
import lombok.extern.slf4j.Slf4j;
import me.wener.cbhistory.core.App;
import me.wener.cbhistory.core.pluggable.event.AfterConfigureCompleteEvent;
import me.wener.cbhistory.core.pluggable.PlugInfo;
import me.wener.cbhistory.server.jws.JWSCommentProvider;
import me.wener.cbhistory.service.RawDataService;

@PlugInfo(name = "使用自带的 jws 来部署一个获取评论的服务器"
        , author = "wener<wenermail@gmail.com>")
@Slf4j
public class JWSServerModule extends AbstractPlugin
{
    public JWSServerModule()
    {
        getEventBus().register(this);
    }

    @Subscribe
    public void afterStarted(AfterConfigureCompleteEvent e)
    {
        String address = "http://127.0.0.1:8880/cmt";

        try
        {
            Injector injector = App.getInjector();
            JWSCommentProvider provider = new JWSCommentProvider()
                    .setRawDataSvc(injector.getInstance(RawDataService.class));

            Endpoint.publish(address, provider);
            log.info("在 {} 启动 jws 服务完成.", address);
        } catch (Exception ex)
        {
            log.error("在 {} 启动 jws 服务时出现异常", address, ex);
        }
        getEventBus().unregister(this);
    }
}
