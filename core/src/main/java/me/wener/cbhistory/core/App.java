package me.wener.cbhistory.core;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mycila.guice.ext.closeable.CloseableModule;
import com.mycila.guice.ext.jsr250.Jsr250Module;
import me.wener.cbhistory.core.modules.ChainInjector;
import me.wener.cbhistory.core.modules.OrmlitePersistModule;
import me.wener.cbhistory.core.modules.PersistModule;
import me.wener.cbhistory.core.modules.PropertiesModule;

public class App
{
    public static void main(String[] args)
    {
        Injector injector = ChainInjector
                .start(PropertiesModule
                        .none()
                        .withOptionalResource("default.properties", "db.properties")
                        , new Jsr250Module(), new CloseableModule())
                .then(PersistModule.class)
                .then(OrmlitePersistModule.class)
                .getInjector();
    }

}
