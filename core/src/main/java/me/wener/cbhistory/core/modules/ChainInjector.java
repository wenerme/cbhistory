package me.wener.cbhistory.core.modules;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import java.util.List;
import lombok.Getter;

public class ChainInjector
{
    @Getter
    private Injector injector;

    protected ChainInjector(Injector injector)
    {
        this.injector = injector;
    }

    public static ChainInjector start(Module... modules)
    {
        return new ChainInjector(Guice.createInjector(modules));
    }

    public static ChainInjector start(Injector injector)
    {
        return new ChainInjector(injector);
    }

    @SafeVarargs
    public final ChainInjector then(Class<? extends Module>... modules)
    {
        List<Module> moduleList = Lists.newArrayList();
        for (Class<? extends Module> module : modules)
            moduleList.add(injector.getInstance(module));
        injector = injector.createChildInjector(moduleList);
        return this;
    }

    public ChainInjector then(Iterable<Module> modules)
    {
        for (Module module : modules)
            injector.injectMembers(module);
        injector = injector.createChildInjector(modules);
        return this;
    }

    public ChainInjector then(Module... modules)
    {
        for (Module module : modules)
            injector.injectMembers(module);
        injector = injector.createChildInjector(modules);
        return this;
    }

    public ChainInjector then(Module module)
    {
        injector.injectMembers(module);
        injector = injector.createChildInjector(module);
        return this;
    }
}
