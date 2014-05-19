package me.wener.cbhistory.core;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.mycila.guice.ext.closeable.CloseableModule;
import com.mycila.guice.ext.jsr250.Jsr250Module;
import me.wener.cbhistory.core.modules.ChainInjector;
import me.wener.cbhistory.core.modules.OrmlitePersistModule;
import me.wener.cbhistory.core.modules.PersistModule;
import me.wener.cbhistory.core.modules.PropertiesModule;

public class App
{
    private static Injector injector;

    public static Injector getInjector()
    {
        if (injector == null)
        {
            injector = ChainInjector
                    .start(PropertiesModule
                            .none()
                            .withOptionalResource("default.properties", "db.properties"))
                    .and(Jsr250Module.class, CloseableModule.class)
                    .then(PersistModule.class)
                    .then(OrmlitePersistModule.class)
                    .getInjector();
        }
        return injector;
    }

    public static void main(String[] args)
    {
        Injector injector = getInjector();
    }

}
