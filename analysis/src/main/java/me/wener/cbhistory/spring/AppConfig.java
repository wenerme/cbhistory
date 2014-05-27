package me.wener.cbhistory.spring;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;
import me.wener.cbhistory.domain.entity.EntityPkg;
import me.wener.cbhistory.repo.RepoPkg;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackageClasses = RepoPkg.class, includeFilters = @ComponentScan.Filter(Named.class))
@ComponentScan(basePackageClasses = EntityPkg.class, includeFilters = @ComponentScan.Filter(Named.class))
public class AppConfig
{
    @Inject
    static DataSource dataSource;
    @Inject
    @Named("persistence.unit")
    static String persistenceUnit;

    public DataSource getDataSource()
    {
        return dataSource;
    }

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean getLocalContainerEntityManagerFactoryBean()
    {
        LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
        bean.setDataSource(getDataSource());
        bean.setPersistenceUnitName(persistenceUnit);
        return bean;
    }

    @Bean(name = "transactionManager")
    public JpaTransactionManager getJpaTransactionManager()
    {
        JpaTransactionManager manager = new JpaTransactionManager();
        manager.setDataSource(getDataSource());
        return manager;
    }
}
