package me.wener.cbhistory.core.modules;

import com.google.inject.AbstractModule;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigureModule extends AbstractModule
{
//    @Inject
//    @Named("cbhistory.version")
    private String appVersion = "";
    @Override
    protected void configure()
    {

    }
    @PostConstruct
    private void report()
    {
        log.info("当前程序版本: {}", appVersion);
    }
}
