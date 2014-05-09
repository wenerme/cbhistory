package me.wener.cbhistory.core;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mycila.guice.ext.closeable.CloseableModule;
import com.mycila.guice.ext.jsr250.Jsr250Module;

public class App extends AbstractModule
{
    public static void main(String[] args)
    {
        Injector injector = Guice.createInjector(new App(),
                new Jsr250Module(),
                new CloseableModule()
        );
    }

    @Override
    protected void configure()
    {

    }
}
