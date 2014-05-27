package me.wener.cbhistory.modules;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Injector;
import java.util.EnumSet;
import javax.servlet.DispatcherType;
import javax.xml.ws.Endpoint;
import lombok.extern.slf4j.Slf4j;
import me.wener.cbhistory.core.App;
import me.wener.cbhistory.core.pluggable.AfterConfigureCompleteEvent;
import me.wener.cbhistory.server.madvoc.MadvocServerFilter;
import me.wener.cbhistory.service.RawDataService;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

@Slf4j
public class MadvocServerJettyModule extends AbstractPlugin
{
    public MadvocServerJettyModule()
    {
        getEventBus().register(this);
    }

    @Subscribe
    public void afterStarted(AfterConfigureCompleteEvent e)
    {
        int port = 8080;
        Server server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.addServlet(org.eclipse.jetty.servlet.DefaultServlet.class, "/");
        context.addFilter(MadvocServerFilter.class, "/*", EnumSet.of(DispatcherType.INCLUDE, DispatcherType.REQUEST));

        server.setHandler(context);

        try
        {
            server.start();
            log.info("在端口 {} 启动 jetty 服务成功", port);
        } catch (Exception ex)
        {
            throw new RuntimeException("启动 jetty 服务时出现异常",ex);
        }
    }
}
