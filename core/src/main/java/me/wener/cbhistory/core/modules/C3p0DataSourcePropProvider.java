package me.wener.cbhistory.core.modules;

import com.google.inject.Inject;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.beans.PropertyVetoException;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.inject.Provider;
import javax.sql.DataSource;
import me.wener.cbhistory.utils.prop.Prop;

/**
 * Provides the C3P0 DataSource.
 */
public final class C3p0DataSourcePropProvider implements Provider<DataSource>
{

    /**
     * The ComboPooledDataSource reference.
     */
    private final ComboPooledDataSource dataSource = new ComboPooledDataSource();

    @Prop(value = "c3p0.acquireRetryDelay", optional = true)
    Integer acquireRetryDelay = null;
    @Prop(value = "jdbc.autoCommit", optional = true)
    Boolean autoCommit = null;
    @Prop(value = "jdbc.username", optional = true)
    String username = null;
    @Prop(value = "jdbc.password", optional = true)
    String password = null;
    @Prop(value = "c3p0.acquireIncrement", optional = true)
    Integer acquireIncrement = null;
    @Prop(value = "c3p0.acquireRetryAttempts", optional = true)
    Integer acquireRetryAttempts = null;
    @Prop(value = "c3p0.initialPoolSize", optional = true)
    Integer initialPoolSize = null;
    @Prop(value = "c3p0.maxAdministrativeTaskTime", optional = true)
    Integer maxAdministrativeTaskTime = null;
    @Prop(value = "c3p0.maxConnectionAge", optional = true)
    Integer maxConnectionAge = null;
    @Prop(value = "c3p0.maxIdleTime", optional = true)
    Integer maxIdleTime = null;
    @Prop(value = "c3p0.maxIdleTimeExcessConnections", optional = true)
    Integer maxIdleTimeExcessConnections = null;
    @Prop(value = "c3p0.maxPoolSize", optional = true)
    Integer maxPoolSize = null;
    @Prop(value = "c3p0.maxStatements", optional = true)
    Integer maxStatements = null;
    @Prop(value = "c3p0.maxStatementsPerConnection", optional = true)
    Integer maxStatementsPerConnection = null;
    @Prop(value = "c3p0.minPoolSize", optional = true)
    Integer minPoolSize = null;
    @Prop(value = "c3p0.idleConnectionTestPeriod", optional = true)
    Integer idleConnectionTestPeriod = null;
    @Prop(value = "c3p0.testConnectionOnCheckin", optional = true)
    Boolean testConnectionOnCheckin = null;
    @Prop(value = "c3p0.testConnectionOnCheckout", optional = true)
    Boolean testConnectionOnCheckout = null;
    @Prop(value = "c3p0.unreturnedConnectionTimeout", optional = true)
    Integer unreturnedConnectionTimeout = null;
    @Prop(value = "c3p0.usesTraditionalReflectiveProxies", optional = true)
    Boolean usesTraditionalReflectiveProxies = null;
    @Prop(value = "c3p0.automaticTestTable", optional = true)
    String automaticTestTable = null;
    @Prop(value = "c3p0.breakAfterAcquireFailure", optional = true)
    Boolean breakAfterAcquireFailure = null;
    @Prop(value = "c3p0.checkoutTimeout", optional = true)
    Integer checkoutTimeout = null;
    @Prop(value = "c3p0.connectionCustomizerClassName", optional = true)
    String connectionCustomizerClassName = null;
    @Prop(value = "c3p0.preferredTestQuery", optional = true)
    String preferredTestQuery = null;
    @Prop(value = "c3p0.propertyCycle", optional = true)
    Integer propertyCycle = null;
    @Prop(value = "jdbc.driver", ignoreSection = true)
    private String driver = null;
    @Prop("jdbc.url")
    private String url = null;

    @PostConstruct
    private void init()
    {
        try
        {
            dataSource.setDriverClass(driver);
        } catch (PropertyVetoException e)
        {
            throw new RuntimeException("Impossible to initialize C3P0 Data Source with driver class '"
                    + driver
                    + "', see nested exceptions", e);
        }

        dataSource.setJdbcUrl(url);

        if (username != null)
            dataSource.setUser(username);
        if (password != null)
            dataSource.setPassword(password);
        if (acquireIncrement != null)
            dataSource.setAcquireIncrement(acquireIncrement);
        if (autoCommit != null)
            dataSource.setAutoCommitOnClose(autoCommit);
        if (acquireRetryAttempts != null)
            dataSource.setAcquireRetryAttempts(acquireRetryAttempts);
        if (acquireRetryDelay != null)
            dataSource.setAcquireRetryDelay(acquireRetryDelay);

        if (initialPoolSize != null)
            dataSource.setInitialPoolSize(initialPoolSize);
        if (maxAdministrativeTaskTime != null)
            dataSource.setMaxAdministrativeTaskTime(maxAdministrativeTaskTime);

        if (maxConnectionAge != null)
            dataSource.setMaxConnectionAge(maxConnectionAge);
        if (maxIdleTime != null)
            dataSource.setMaxIdleTime(maxIdleTime);

        if (maxIdleTimeExcessConnections != null)
            dataSource.setMaxIdleTimeExcessConnections(maxIdleTimeExcessConnections);
        if (maxPoolSize != null)
            dataSource.setMaxPoolSize(maxPoolSize);

        if (maxStatements != null)
            dataSource.setMaxStatements(maxStatements);
        if (maxStatementsPerConnection != null)
            dataSource.setMaxStatementsPerConnection(maxStatementsPerConnection);
        if (minPoolSize != null)
            dataSource.setMinPoolSize(minPoolSize);


        if (idleConnectionTestPeriod != null)
            dataSource.setIdleConnectionTestPeriod(idleConnectionTestPeriod);
        if (testConnectionOnCheckin != null)
            dataSource.setTestConnectionOnCheckin(testConnectionOnCheckin);

        if (testConnectionOnCheckout != null)
            dataSource.setTestConnectionOnCheckout(testConnectionOnCheckout);
        if (unreturnedConnectionTimeout != null)
            dataSource.setUnreturnedConnectionTimeout(unreturnedConnectionTimeout);

        if (usesTraditionalReflectiveProxies != null)
            dataSource.setUsesTraditionalReflectiveProxies(usesTraditionalReflectiveProxies);
        if (automaticTestTable != null)
            dataSource.setAutomaticTestTable(automaticTestTable);
        if (breakAfterAcquireFailure != null)
            dataSource.setBreakAfterAcquireFailure(breakAfterAcquireFailure);
        if (checkoutTimeout != null)
            dataSource.setCheckoutTimeout(checkoutTimeout);

        if (connectionCustomizerClassName != null)
            dataSource.setConnectionCustomizerClassName(connectionCustomizerClassName);
        if (preferredTestQuery != null)
            dataSource.setPreferredTestQuery(preferredTestQuery);
        if (propertyCycle != null)
            dataSource.setPropertyCycle(propertyCycle);

    }

    @Inject(optional = true)
    public void setDriverProperties(@Named("jdbc.driverProperties") final Properties driverProperties)
    {
        dataSource.setProperties(driverProperties);
    }

    @Inject(optional = true)
    public void setConnectionTesterClassName(@Named("c3p0.connectionTesterClassName") final String connectionTesterClassName)
    {
        try
        {
            dataSource.setConnectionTesterClassName(connectionTesterClassName);
        } catch (PropertyVetoException e)
        {
            throw new RuntimeException("Impossible to set C3P0 Data Source connection tester class name '"
                    + connectionTesterClassName
                    + "', see nested exceptions", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public DataSource get()
    {
        return dataSource;
    }

}
