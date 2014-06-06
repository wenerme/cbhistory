package me.wener.cbhistory.server.madvoc.ace;

import jodd.madvoc.config.ManualMadvocConfigurator;
import lombok.extern.slf4j.Slf4j;
import me.wener.cbhistory.server.madvoc.CommentAction;

@Slf4j
public class ServerMadvocConfigurator extends ManualMadvocConfigurator
{
    @Override
    public void configure()
    {
        log.info("ServerMadvocConfigurator");
        actionsManager.register(CommentAction.class, "cmt");
    }
}
