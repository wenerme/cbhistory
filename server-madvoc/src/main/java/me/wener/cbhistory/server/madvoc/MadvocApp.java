package me.wener.cbhistory.server.madvoc;

import jodd.madvoc.WebApplication;
import jodd.madvoc.component.MadvocConfig;
import me.wener.cbhistory.core.App;


public class MadvocApp extends WebApplication
{
    @Override
    public void registerMadvocComponents()
    {
        super.registerMadvocComponents();
        CommentAction commentAction = App.getInjector().getInstance(CommentAction.class);

        registerComponent(commentAction);
    }
}
