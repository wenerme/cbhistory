package me.wener.cbhistory.core.modules;

import com.google.inject.Inject;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.beans.PropertyVetoException;
import java.util.Properties;
import javax.inject.Named;
import javax.inject.Provider;
import javax.sql.DataSource;

/**
 * Provides the C3P0 DataSource.
 */
public final class C3p0DataSourceProvider implements Provider<DataSource>
{

    /**
     * The ComboPooledDataSource reference.
     */
    private final ComboPooledDataSource dataSource = new ComboPooledDataSource();

    /**
     * Creates a new ComboPooledDataSource using the needed parameter.
     *
     * @param driver The JDBC driver class.
     * @param url    the database URL of the form <code>jdbc:subprotocol:subname</code>.
     */
    @Inject
    public C3p0DataSourceProvider(@Named("jdbc.driver") final String driver,
                                  @Named("jdbc.url") final String url)
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
    }

    @Inject(optional = true)
    public void setUser(@Named("jdbc.username") final String username)
    {
        dataSource.setUser(username);
    }

    @Inject(optional = true)
    public void setPassword(@Named("jdbc.password") final String password)
    {
        dataSource.setPassword(password);
    }

    @Inject(optional = true)
    public void setAcquireIncrement(@Named("c3p0.acquireIncrement") final int acquireIncrement)
    {
        dataSource.setAcquireIncrement(acquireIncrement);
    }

    @Inject(optional = true)
    public void setAcquireRetryAttempts(@Named("c3p0.acquireRetryAttempts") final int acquireRetryAttempts)
    {
        dataSource.setAcquireRetryAttempts(acquireRetryAttempts);
    }

    @Inject(optional = true)
    public void setAcquireRetryDelay(@Named("c3p0.acquireRetryDelay") final int acquireRetryDelay)
    {
        dataSource.setAcquireRetryDelay(acquireRetryDelay);
    }

    @Inject(optional = true)
    public void setAutoCommitOnClose(@Named("jdbc.autoCommit") final boolean autoCommit)
    {
        dataSource.setAutoCommitOnClose(autoCommit);
    }

    @Inject(optional = true)
    public void setDriverProperties(@Named("jdbc.driverProperties") final Properties driverProperties)
    {
        dataSource.setProperties(driverProperties);
    }

    @Inject(optional = true)
    public void setAautomaticTestTable(@Named("c3p0.automaticTestTable") final String automaticTestTable)
    {
        dataSource.setAutomaticTestTable(automaticTestTable);
    }

    @Inject(optional = true)
    public void setBreakAfterAcquireFailure(@Named("c3p0.breakAfterAcquireFailure") final boolean breakAfterAcquireFailure)
    {
        dataSource.setBreakAfterAcquireFailure(breakAfterAcquireFailure);
    }

    @Inject(optional = true)
    public void setCheckoutTimeout(@Named("c3p0.checkoutTimeout") final int checkoutTimeout)
    {
        dataSource.setCheckoutTimeout(checkoutTimeout);
    }

    @Inject(optional = true)
    public void setConnectionCustomizerClassName(@Named("c3p0.connectionCustomizerClassName") final String connectionCustomizerClassName)
    {
        dataSource.setConnectionCustomizerClassName(connectionCustomizerClassName);
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

    @Inject(optional = true)
    public void setIdleConnectionTestPeriod(@Named("c3p0.idleConnectionTestPeriod") final int idleConnectionTestPeriod)
    {
        dataSource.setIdleConnectionTestPeriod(idleConnectionTestPeriod);
    }

    @Inject(optional = true)
    public void setInitialPoolSize(@Named("c3p0.initialPoolSize") final int initialPoolSize)
    {
        dataSource.setInitialPoolSize(initialPoolSize);
    }

    @Inject(optional = true)
    public void setMaxAdministrativeTaskTime(@Named("c3p0.maxAdministrativeTaskTime") final int maxAdministrativeTaskTime)
    {
        dataSource.setMaxAdministrativeTaskTime(maxAdministrativeTaskTime);
    }

    @Inject(optional = true)
    public void setMaxConnectionAge(@Named("c3p0.maxConnectionAge") final int maxConnectionAge)
    {
        dataSource.setMaxConnectionAge(maxConnectionAge);
    }

    @Inject(optional = true)
    public void setMaxIdleTime(@Named("c3p0.maxIdleTime") final int maxIdleTime)
    {
        dataSource.setMaxIdleTime(maxIdleTime);
    }

    @Inject(optional = true)
    public void setMaxIdleTimeExcessConnections(@Named("c3p0.maxIdleTimeExcessConnections") final int maxIdleTimeExcessConnections)
    {
        dataSource.setMaxIdleTimeExcessConnections(maxIdleTimeExcessConnections);
    }

    @Inject(optional = true)
    public void setMaxPoolSize(@Named("c3p0.maxPoolSize") final int maxPoolSize)
    {
        dataSource.setMaxPoolSize(maxPoolSize);
    }

    @Inject(optional = true)
    public void setMaxStatements(@Named("c3p0.maxStatements") final int maxStatements)
    {
        dataSource.setMaxStatements(maxStatements);
    }

    @Inject(optional = true)
    public void setMaxStatementsPerConnection(@Named("c3p0.maxStatementsPerConnection") final int maxStatementsPerConnection)
    {
        dataSource.setMaxStatementsPerConnection(maxStatementsPerConnection);
    }

    @Inject(optional = true)
    public void setMinPoolSize(@Named("c3p0.minPoolSize") final int minPoolSize)
    {
        dataSource.setMinPoolSize(minPoolSize);
    }

    @Inject(optional = true)
    public void setPreferredTestQuery(@Named("c3p0.preferredTestQuery") final String preferredTestQuery)
    {
        dataSource.setPreferredTestQuery(preferredTestQuery);
    }

    @Inject(optional = true)
    public void setPropertyCycle(@Named("c3p0.propertyCycle") final int propertyCycle)
    {
        dataSource.setPropertyCycle(propertyCycle);
    }

    @Inject(optional = true)
    public void setTestConnectionOnCheckin(@Named("c3p0.testConnectionOnCheckin") final boolean testConnectionOnCheckin)
    {
        dataSource.setTestConnectionOnCheckin(testConnectionOnCheckin);
    }

    @Inject(optional = true)
    public void setTestConnectionOnCheckout(@Named("c3p0.testConnectionOnCheckout") final boolean testConnectionOnCheckout)
    {
        dataSource.setTestConnectionOnCheckout(testConnectionOnCheckout);
    }

    @Inject(optional = true)
    public void setUnreturnedConnectionTimeout(@Named("c3p0.unreturnedConnectionTimeout") final int unreturnedConnectionTimeout)
    {
        dataSource.setUnreturnedConnectionTimeout(unreturnedConnectionTimeout);
    }

    @Inject(optional = true)
    public void setUsesTraditionalReflectiveProxies(@Named("c3p0.usesTraditionalReflectiveProxies") final boolean usesTraditionalReflectiveProxies)
    {
        dataSource.setUsesTraditionalReflectiveProxies(usesTraditionalReflectiveProxies);
    }

    /**
     * {@inheritDoc}
     */
    public DataSource get()
    {
        return dataSource;
    }

}
