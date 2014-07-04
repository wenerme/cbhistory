package me.wener.cbhistory.spring;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;
import me.wener.cbhistory.domain.entity.EntityPkg;
import me.wener.cbhistory.repo.RepoPkg;
import me.wener.cbhistory.utils.prop.Prop;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackageClasses = EntityPkg.class, includeFilters = @ComponentScan.Filter(Named.class))
@EnableTransactionManagement(proxyTargetClass = true)
@EnableJpaRepositories(basePackageClasses = RepoPkg.class, includeFilters = @ComponentScan.Filter(Named.class))
public class SpringContextConfig
{
    @Inject
    static DataSource dataSource;
    @Prop("persistence.unit")
    static String persistenceUnit;

    public DataSource getDataSource()
    {
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory()
    {
        LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
        bean.setDataSource(getDataSource());
        bean.setPersistenceUnitName(persistenceUnit);
        return bean;
    }

    @Bean
    public JpaTransactionManager transactionManager()
    {
        JpaTransactionManager manager = new JpaTransactionManager();
        manager.setDataSource(getDataSource());
        return manager;
    }
}
