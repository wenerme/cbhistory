package me.wener.cbhistory.modules;

import me.wener.cbhistory.core.pluggable.PlugInfo;
import me.wener.cbhistory.server.madvoc.CommentAction;

@PlugInfo(name = "madvoc 服务", author = "wener<wenermail@gmail.com>")
public class MadvocServerModule extends AbstractPluginModule
{
    @Override
    protected void configure()
    {
        requestStaticInjection(CommentAction.class);
    }
}
