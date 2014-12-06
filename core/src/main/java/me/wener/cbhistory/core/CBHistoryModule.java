package me.wener.cbhistory.core;

import com.google.inject.AbstractModule;
import me.wener.cbhistory.parser.CnBetaParser;
import me.wener.cbhistory.parser.v2.CnBetaV2Parser;

public class CBHistoryModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(CnBetaParser.class).toInstance(new CnBetaV2Parser());
    }
}
