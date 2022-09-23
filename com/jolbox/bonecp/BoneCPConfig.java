// 
// Decompiled by Procyon v0.5.36
// 

package com.jolbox.bonecp;

import org.slf4j.LoggerFactory;
import com.google.common.base.Objects;
import java.lang.reflect.Field;
import java.net.URL;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import java.lang.reflect.Method;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import com.google.common.base.Preconditions;
import com.google.common.annotations.VisibleForTesting;
import java.util.Properties;
import javax.sql.DataSource;
import com.jolbox.bonecp.hooks.ConnectionHook;
import org.slf4j.Logger;
import java.io.Serializable;

public class BoneCPConfig implements BoneCPConfigMBean, Cloneable, Serializable
{
    private static final String USER = "user";
    private static final String PASSWORD = "password";
    private static final long serialVersionUID = 6090570773474131622L;
    private static final String CONFIG_TOSTRING = "JDBC URL = %s, Username = %s, partitions = %d, max (per partition) = %d, min (per partition) = %d, idle max age = %d min, idle test period = %d min, strategy = %s";
    private static final String CONFIG_DS_TOSTRING = "JDBC URL = (via datasource bean), Username = (via datasource bean), partitions = %d, max (per partition) = %d, min (per partition) = %d, idle max age = %d min, idle test period = %d min, strategy = %s";
    private static final Logger logger;
    private int minConnectionsPerPartition;
    private int maxConnectionsPerPartition;
    private int acquireIncrement;
    private int partitionCount;
    private String jdbcUrl;
    private String username;
    private String password;
    private long idleConnectionTestPeriodInSeconds;
    private long idleMaxAgeInSeconds;
    private String connectionTestStatement;
    private int statementsCacheSize;
    private int statementsCachedPerConnection;
    private int releaseHelperThreads;
    private int statementReleaseHelperThreads;
    private ConnectionHook connectionHook;
    private String initSQL;
    private boolean closeConnectionWatch;
    private boolean logStatementsEnabled;
    private long acquireRetryDelayInMs;
    private int acquireRetryAttempts;
    private boolean lazyInit;
    private boolean transactionRecoveryEnabled;
    private String connectionHookClassName;
    private ClassLoader classLoader;
    private String poolName;
    private boolean disableJMX;
    private DataSource datasourceBean;
    private long queryExecuteTimeLimitInMs;
    private int poolAvailabilityThreshold;
    private boolean disableConnectionTracking;
    @VisibleForTesting
    protected Properties driverProperties;
    private long connectionTimeoutInMs;
    private long closeConnectionWatchTimeoutInMs;
    private long maxConnectionAgeInSeconds;
    private String configFile;
    private String serviceOrder;
    private boolean statisticsEnabled;
    private boolean defaultAutoCommit;
    private boolean defaultReadOnly;
    private String defaultTransactionIsolation;
    private String defaultCatalog;
    private int defaultTransactionIsolationValue;
    private boolean externalAuth;
    private boolean deregisterDriverOnClose;
    private boolean nullOnConnectionTimeout;
    private boolean resetConnectionOnClose;
    private boolean detectUnresolvedTransactions;
    private String poolStrategy;
    private boolean closeOpenStatements;
    private boolean detectUnclosedStatements;
    private Properties clientInfo;
    
    public String getPoolName() {
        return this.poolName;
    }
    
    public void setPoolName(final String poolName) {
        this.poolName = Preconditions.checkNotNull(poolName);
    }
    
    public int getMinConnectionsPerPartition() {
        return this.minConnectionsPerPartition;
    }
    
    public void setMinConnectionsPerPartition(final int minConnectionsPerPartition) {
        this.minConnectionsPerPartition = minConnectionsPerPartition;
    }
    
    public int getMaxConnectionsPerPartition() {
        return this.maxConnectionsPerPartition;
    }
    
    public void setMaxConnectionsPerPartition(final int maxConnectionsPerPartition) {
        this.maxConnectionsPerPartition = maxConnectionsPerPartition;
    }
    
    public int getAcquireIncrement() {
        return this.acquireIncrement;
    }
    
    public void setAcquireIncrement(final int acquireIncrement) {
        this.acquireIncrement = acquireIncrement;
    }
    
    public int getPartitionCount() {
        return this.partitionCount;
    }
    
    public void setPartitionCount(final int partitionCount) {
        this.partitionCount = partitionCount;
    }
    
    public String getJdbcUrl() {
        return this.jdbcUrl;
    }
    
    public void setJdbcUrl(final String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public void setUsername(final String username) {
        this.username = username;
    }
    
    public void setUser(final String username) {
        this.setUsername(username);
    }
    
    public String getUser() {
        return this.getUsername();
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
    
    @Deprecated
    public long getIdleConnectionTestPeriod() {
        BoneCPConfig.logger.warn("Please use getIdleConnectionTestPeriodInMinutes in place of getIdleConnectionTestPeriod. This method has been deprecated.");
        return this.getIdleConnectionTestPeriodInMinutes();
    }
    
    @Deprecated
    public void setIdleConnectionTestPeriod(final long idleConnectionTestPeriod) {
        BoneCPConfig.logger.warn("Please use setIdleConnectionTestPeriodInMinutes in place of setIdleConnectionTestPeriod. This method has been deprecated.");
        this.setIdleConnectionTestPeriod(idleConnectionTestPeriod * 60L, TimeUnit.SECONDS);
    }
    
    public long getIdleConnectionTestPeriodInMinutes() {
        return this.idleConnectionTestPeriodInSeconds / 60L;
    }
    
    public long getIdleConnectionTestPeriod(final TimeUnit timeUnit) {
        return timeUnit.convert(this.idleConnectionTestPeriodInSeconds, TimeUnit.SECONDS);
    }
    
    public void setIdleConnectionTestPeriodInMinutes(final long idleConnectionTestPeriod) {
        this.setIdleConnectionTestPeriod(idleConnectionTestPeriod * 60L, TimeUnit.SECONDS);
    }
    
    public void setIdleConnectionTestPeriodInSeconds(final long idleConnectionTestPeriod) {
        this.setIdleConnectionTestPeriod(idleConnectionTestPeriod, TimeUnit.SECONDS);
    }
    
    public void setIdleConnectionTestPeriod(final long idleConnectionTestPeriod, final TimeUnit timeUnit) {
        this.idleConnectionTestPeriodInSeconds = TimeUnit.SECONDS.convert(idleConnectionTestPeriod, Preconditions.checkNotNull(timeUnit));
    }
    
    @Deprecated
    public long getIdleMaxAge() {
        BoneCPConfig.logger.warn("Please use getIdleMaxAgeInMinutes in place of getIdleMaxAge. This method has been deprecated.");
        return this.getIdleMaxAgeInMinutes();
    }
    
    public long getIdleMaxAge(final TimeUnit timeUnit) {
        return timeUnit.convert(this.idleMaxAgeInSeconds, TimeUnit.SECONDS);
    }
    
    public long getIdleMaxAgeInMinutes() {
        return this.idleMaxAgeInSeconds / 60L;
    }
    
    @Deprecated
    public void setIdleMaxAge(final long idleMaxAge) {
        BoneCPConfig.logger.warn("Please use setIdleMaxAgeInMinutes in place of setIdleMaxAge. This method has been deprecated.");
        this.setIdleMaxAgeInMinutes(idleMaxAge);
    }
    
    public void setIdleMaxAgeInMinutes(final long idleMaxAge) {
        this.setIdleMaxAge(idleMaxAge * 60L, TimeUnit.SECONDS);
    }
    
    public void setIdleMaxAgeInSeconds(final long idleMaxAge) {
        this.setIdleMaxAge(idleMaxAge, TimeUnit.SECONDS);
    }
    
    public void setIdleMaxAge(final long idleMaxAge, final TimeUnit timeUnit) {
        this.idleMaxAgeInSeconds = TimeUnit.SECONDS.convert(idleMaxAge, Preconditions.checkNotNull(timeUnit));
    }
    
    public String getConnectionTestStatement() {
        return this.connectionTestStatement;
    }
    
    public void setConnectionTestStatement(final String connectionTestStatement) {
        this.connectionTestStatement = Preconditions.checkNotNull(connectionTestStatement);
    }
    
    @Deprecated
    public int getPreparedStatementsCacheSize() {
        BoneCPConfig.logger.warn("Please use getStatementsCacheSize in place of getPreparedStatementsCacheSize. This method has been deprecated.");
        return this.statementsCacheSize;
    }
    
    @Deprecated
    public int getPreparedStatementCacheSize() {
        BoneCPConfig.logger.warn("Please use getStatementsCacheSize in place of getPreparedStatementCacheSize. This method has been deprecated.");
        return this.statementsCacheSize;
    }
    
    @Deprecated
    public void setPreparedStatementsCacheSize(final int preparedStatementsCacheSize) {
        BoneCPConfig.logger.warn("Please use setStatementsCacheSize in place of setPreparedStatementsCacheSize. This method has been deprecated.");
        this.statementsCacheSize = preparedStatementsCacheSize;
    }
    
    public void setStatementsCacheSize(final int statementsCacheSize) {
        this.statementsCacheSize = statementsCacheSize;
    }
    
    public int getStatementsCacheSize() {
        return this.statementsCacheSize;
    }
    
    @Deprecated
    public void setStatementCacheSize(final int statementsCacheSize) {
        BoneCPConfig.logger.warn("Please use setStatementsCacheSize in place of setStatementCacheSize. This method has been deprecated.");
        this.statementsCacheSize = statementsCacheSize;
    }
    
    @Deprecated
    public int getStatementCacheSize() {
        BoneCPConfig.logger.warn("Please use getStatementsCacheSize in place of getStatementCacheSize. This method has been deprecated.");
        return this.statementsCacheSize;
    }
    
    @Deprecated
    public int getReleaseHelperThreads() {
        return this.releaseHelperThreads;
    }
    
    @Deprecated
    public void setReleaseHelperThreads(final int releaseHelperThreads) {
        BoneCPConfig.logger.warn("releaseHelperThreads has been deprecated -- it tends to slow down your application more.");
        this.releaseHelperThreads = releaseHelperThreads;
    }
    
    @Deprecated
    public int getStatementsCachedPerConnection() {
        return this.statementsCachedPerConnection;
    }
    
    @Deprecated
    public void setStatementsCachedPerConnection(final int statementsCachedPerConnection) {
        this.statementsCachedPerConnection = statementsCachedPerConnection;
    }
    
    public ConnectionHook getConnectionHook() {
        return this.connectionHook;
    }
    
    public void setConnectionHook(final ConnectionHook connectionHook) {
        this.connectionHook = connectionHook;
    }
    
    public String getInitSQL() {
        return this.initSQL;
    }
    
    public void setInitSQL(final String initSQL) {
        this.initSQL = Preconditions.checkNotNull(initSQL);
    }
    
    public boolean isCloseConnectionWatch() {
        return this.closeConnectionWatch;
    }
    
    public void setCloseConnectionWatch(final boolean closeConnectionWatch) {
        this.closeConnectionWatch = closeConnectionWatch;
    }
    
    public boolean isLogStatementsEnabled() {
        return this.logStatementsEnabled;
    }
    
    public void setLogStatementsEnabled(final boolean logStatementsEnabled) {
        this.logStatementsEnabled = logStatementsEnabled;
    }
    
    @Deprecated
    public long getAcquireRetryDelay() {
        BoneCPConfig.logger.warn("Please use getAcquireRetryDelayInMs in place of getAcquireRetryDelay. This method has been deprecated.");
        return this.acquireRetryDelayInMs;
    }
    
    @Deprecated
    public void setAcquireRetryDelay(final int acquireRetryDelayInMs) {
        BoneCPConfig.logger.warn("Please use setAcquireRetryDelayInMs in place of setAcquireRetryDelay. This method has been deprecated.");
        this.acquireRetryDelayInMs = acquireRetryDelayInMs;
    }
    
    public long getAcquireRetryDelayInMs() {
        return this.acquireRetryDelayInMs;
    }
    
    public long getAcquireRetryDelay(final TimeUnit timeUnit) {
        return timeUnit.convert(this.acquireRetryDelayInMs, TimeUnit.MILLISECONDS);
    }
    
    public void setAcquireRetryDelayInMs(final long acquireRetryDelay) {
        this.setAcquireRetryDelay(acquireRetryDelay, TimeUnit.MILLISECONDS);
    }
    
    public void setAcquireRetryDelay(final long acquireRetryDelay, final TimeUnit timeUnit) {
        this.acquireRetryDelayInMs = TimeUnit.MILLISECONDS.convert(acquireRetryDelay, timeUnit);
    }
    
    public boolean isLazyInit() {
        return this.lazyInit;
    }
    
    public void setLazyInit(final boolean lazyInit) {
        this.lazyInit = lazyInit;
    }
    
    public boolean isTransactionRecoveryEnabled() {
        return this.transactionRecoveryEnabled;
    }
    
    public void setTransactionRecoveryEnabled(final boolean transactionRecoveryEnabled) {
        this.transactionRecoveryEnabled = transactionRecoveryEnabled;
    }
    
    public int getAcquireRetryAttempts() {
        return this.acquireRetryAttempts;
    }
    
    public void setAcquireRetryAttempts(final int acquireRetryAttempts) {
        this.acquireRetryAttempts = acquireRetryAttempts;
    }
    
    public void setConnectionHookClassName(final String connectionHookClassName) {
        this.connectionHookClassName = Preconditions.checkNotNull(connectionHookClassName);
        try {
            final Object hookClass = this.loadClass(connectionHookClassName).newInstance();
            this.connectionHook = (ConnectionHook)hookClass;
        }
        catch (Exception e) {
            BoneCPConfig.logger.error("Unable to create an instance of the connection hook class (" + connectionHookClassName + ")");
            this.connectionHook = null;
        }
    }
    
    public String getConnectionHookClassName() {
        return this.connectionHookClassName;
    }
    
    public boolean isDisableJMX() {
        return this.disableJMX;
    }
    
    public void setDisableJMX(final boolean disableJMX) {
        this.disableJMX = disableJMX;
    }
    
    public DataSource getDatasourceBean() {
        return this.datasourceBean;
    }
    
    public void setDatasourceBean(final DataSource datasourceBean) {
        this.datasourceBean = datasourceBean;
    }
    
    @Deprecated
    public long getQueryExecuteTimeLimit() {
        BoneCPConfig.logger.warn("Please use getQueryExecuteTimeLimitInMs in place of getQueryExecuteTimeLimit. This method has been deprecated.");
        return this.queryExecuteTimeLimitInMs;
    }
    
    @Deprecated
    public void setQueryExecuteTimeLimit(final int queryExecuteTimeLimit) {
        BoneCPConfig.logger.warn("Please use setQueryExecuteTimeLimitInMs in place of setQueryExecuteTimeLimit. This method has been deprecated.");
        this.setQueryExecuteTimeLimit(queryExecuteTimeLimit, TimeUnit.MILLISECONDS);
    }
    
    public long getQueryExecuteTimeLimitInMs() {
        return this.queryExecuteTimeLimitInMs;
    }
    
    public long getQueryExecuteTimeLimit(final TimeUnit timeUnit) {
        return timeUnit.convert(this.queryExecuteTimeLimitInMs, TimeUnit.MILLISECONDS);
    }
    
    public void setQueryExecuteTimeLimitInMs(final long queryExecuteTimeLimit) {
        this.setQueryExecuteTimeLimit(queryExecuteTimeLimit, TimeUnit.MILLISECONDS);
    }
    
    public void setQueryExecuteTimeLimit(final long queryExecuteTimeLimit, final TimeUnit timeUnit) {
        this.queryExecuteTimeLimitInMs = TimeUnit.MILLISECONDS.convert(queryExecuteTimeLimit, timeUnit);
    }
    
    public int getPoolAvailabilityThreshold() {
        return this.poolAvailabilityThreshold;
    }
    
    public void setPoolAvailabilityThreshold(final int poolAvailabilityThreshold) {
        this.poolAvailabilityThreshold = poolAvailabilityThreshold;
    }
    
    public boolean isDisableConnectionTracking() {
        return this.disableConnectionTracking;
    }
    
    public void setDisableConnectionTracking(final boolean disableConnectionTracking) {
        this.disableConnectionTracking = disableConnectionTracking;
    }
    
    @Deprecated
    public long getConnectionTimeout() {
        BoneCPConfig.logger.warn("Please use getConnectionTimeoutInMs in place of getConnectionTimeout. This method has been deprecated.");
        return this.connectionTimeoutInMs;
    }
    
    @Deprecated
    public void setConnectionTimeout(final long connectionTimeout) {
        BoneCPConfig.logger.warn("Please use setConnectionTimeoutInMs in place of setConnectionTimeout. This method has been deprecated.");
        this.connectionTimeoutInMs = connectionTimeout;
    }
    
    public long getConnectionTimeoutInMs() {
        return this.connectionTimeoutInMs;
    }
    
    public long getConnectionTimeout(final TimeUnit timeUnit) {
        return timeUnit.convert(this.connectionTimeoutInMs, TimeUnit.MILLISECONDS);
    }
    
    public void setConnectionTimeoutInMs(final long connectionTimeoutinMs) {
        this.setConnectionTimeout(connectionTimeoutinMs, TimeUnit.MILLISECONDS);
    }
    
    public void setConnectionTimeout(final long connectionTimeout, final TimeUnit timeUnit) {
        this.connectionTimeoutInMs = TimeUnit.MILLISECONDS.convert(connectionTimeout, timeUnit);
    }
    
    public Properties getDriverProperties() {
        return this.driverProperties;
    }
    
    public void setDriverProperties(final Properties driverProperties) {
        (this.driverProperties = new Properties()).putAll(Preconditions.checkNotNull(driverProperties));
    }
    
    @Deprecated
    public long getCloseConnectionWatchTimeout() {
        BoneCPConfig.logger.warn("Please use getCloseConnectionWatchTimeoutInMs in place of getCloseConnectionWatchTimeout. This method has been deprecated.");
        return this.closeConnectionWatchTimeoutInMs;
    }
    
    @Deprecated
    public void setCloseConnectionWatchTimeout(final long closeConnectionWatchTimeout) {
        BoneCPConfig.logger.warn("Please use setCloseConnectionWatchTimeoutInMs in place of setCloseConnectionWatchTimeout. This method has been deprecated.");
        this.setCloseConnectionWatchTimeoutInMs(closeConnectionWatchTimeout);
    }
    
    public long getCloseConnectionWatchTimeoutInMs() {
        return this.closeConnectionWatchTimeoutInMs;
    }
    
    public long getCloseConnectionWatchTimeout(final TimeUnit timeUnit) {
        return timeUnit.convert(this.closeConnectionWatchTimeoutInMs, TimeUnit.MILLISECONDS);
    }
    
    public void setCloseConnectionWatchTimeoutInMs(final long closeConnectionWatchTimeout) {
        this.setCloseConnectionWatchTimeout(closeConnectionWatchTimeout, TimeUnit.MILLISECONDS);
    }
    
    public void setCloseConnectionWatchTimeout(final long closeConnectionWatchTimeout, final TimeUnit timeUnit) {
        this.closeConnectionWatchTimeoutInMs = TimeUnit.MILLISECONDS.convert(closeConnectionWatchTimeout, timeUnit);
    }
    
    @Deprecated
    public int getStatementReleaseHelperThreads() {
        return this.statementReleaseHelperThreads;
    }
    
    @Deprecated
    public void setStatementReleaseHelperThreads(final int statementReleaseHelperThreads) {
        BoneCPConfig.logger.warn("statementReleaseHelperThreads has been deprecated -- it tends to slow down your application more.");
        this.statementReleaseHelperThreads = statementReleaseHelperThreads;
    }
    
    @Deprecated
    public long getMaxConnectionAge() {
        BoneCPConfig.logger.warn("Please use getMaxConnectionAgeInSeconds in place of getMaxConnectionAge. This method has been deprecated.");
        return this.maxConnectionAgeInSeconds;
    }
    
    public long getMaxConnectionAgeInSeconds() {
        return this.maxConnectionAgeInSeconds;
    }
    
    public long getMaxConnectionAge(final TimeUnit timeUnit) {
        return timeUnit.convert(this.maxConnectionAgeInSeconds, TimeUnit.SECONDS);
    }
    
    @Deprecated
    public void setMaxConnectionAge(final long maxConnectionAgeInSeconds) {
        BoneCPConfig.logger.warn("Please use setmaxConnectionAgeInSecondsInSeconds in place of setMaxConnectionAge. This method has been deprecated.");
        this.maxConnectionAgeInSeconds = maxConnectionAgeInSeconds;
    }
    
    public void setMaxConnectionAgeInSeconds(final long maxConnectionAgeInSeconds) {
        this.setMaxConnectionAge(maxConnectionAgeInSeconds, TimeUnit.SECONDS);
    }
    
    public void setMaxConnectionAge(final long maxConnectionAge, final TimeUnit timeUnit) {
        this.maxConnectionAgeInSeconds = TimeUnit.SECONDS.convert(maxConnectionAge, timeUnit);
    }
    
    public String getConfigFile() {
        return this.configFile;
    }
    
    public void setConfigFile(final String configFile) {
        this.configFile = Preconditions.checkNotNull(configFile);
    }
    
    public String getServiceOrder() {
        return this.serviceOrder;
    }
    
    public void setServiceOrder(final String serviceOrder) {
        this.serviceOrder = Preconditions.checkNotNull(serviceOrder);
    }
    
    public boolean isStatisticsEnabled() {
        return this.statisticsEnabled;
    }
    
    public void setStatisticsEnabled(final boolean statisticsEnabled) {
        this.statisticsEnabled = statisticsEnabled;
    }
    
    public boolean getDefaultAutoCommit() {
        return this.defaultAutoCommit;
    }
    
    public void setDefaultAutoCommit(final boolean defaultAutoCommit) {
        this.defaultAutoCommit = defaultAutoCommit;
    }
    
    public Boolean getDefaultReadOnly() {
        return this.defaultReadOnly;
    }
    
    public void setDefaultReadOnly(final Boolean defaultReadOnly) {
        this.defaultReadOnly = Preconditions.checkNotNull(defaultReadOnly);
    }
    
    public String getDefaultCatalog() {
        return this.defaultCatalog;
    }
    
    public void setDefaultCatalog(final String defaultCatalog) {
        this.defaultCatalog = Preconditions.checkNotNull(defaultCatalog);
    }
    
    public String getDefaultTransactionIsolation() {
        return this.defaultTransactionIsolation;
    }
    
    public void setDefaultTransactionIsolation(final String defaultTransactionIsolation) {
        this.defaultTransactionIsolation = Preconditions.checkNotNull(defaultTransactionIsolation);
    }
    
    protected int getDefaultTransactionIsolationValue() {
        return this.defaultTransactionIsolationValue;
    }
    
    protected void setDefaultTransactionIsolationValue(final int defaultTransactionIsolationValue) {
        this.defaultTransactionIsolationValue = defaultTransactionIsolationValue;
    }
    
    public BoneCPConfig() {
        this.minConnectionsPerPartition = 1;
        this.maxConnectionsPerPartition = 2;
        this.acquireIncrement = 2;
        this.partitionCount = 1;
        this.idleConnectionTestPeriodInSeconds = 14400L;
        this.idleMaxAgeInSeconds = 3600L;
        this.statementsCacheSize = 0;
        this.statementsCachedPerConnection = 0;
        this.releaseHelperThreads = 0;
        this.statementReleaseHelperThreads = 0;
        this.acquireRetryDelayInMs = 7000L;
        this.acquireRetryAttempts = 5;
        this.classLoader = this.getClassLoader();
        this.queryExecuteTimeLimitInMs = 0L;
        this.poolAvailabilityThreshold = 0;
        this.connectionTimeoutInMs = 0L;
        this.closeConnectionWatchTimeoutInMs = 0L;
        this.maxConnectionAgeInSeconds = 0L;
        this.serviceOrder = "FIFO";
        this.defaultAutoCommit = true;
        this.defaultTransactionIsolationValue = -1;
        this.poolStrategy = "DEFAULT";
        this.loadProperties("bonecp-default-config.xml");
        this.loadProperties("bonecp-config.xml");
    }
    
    public BoneCPConfig(final Properties props) throws Exception {
        this();
        this.setProperties(Preconditions.checkNotNull(props));
    }
    
    public BoneCPConfig(final String sectionName) throws Exception {
        this(BoneCPConfig.class.getResourceAsStream("/bonecp-config.xml"), Preconditions.checkNotNull(sectionName));
    }
    
    public BoneCPConfig(final InputStream xmlConfigFile, final String sectionName) throws Exception {
        this();
        this.setXMLProperties(xmlConfigFile, Preconditions.checkNotNull(sectionName));
    }
    
    private void setXMLProperties(final InputStream xmlConfigFile, final String sectionName) throws Exception {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            final DocumentBuilder db = dbf.newDocumentBuilder();
            final Document doc = db.parse(xmlConfigFile);
            doc.getDocumentElement().normalize();
            final Properties settings = this.parseXML(doc, null);
            if (sectionName != null) {
                settings.putAll(this.parseXML(doc, sectionName));
            }
            this.setProperties(settings);
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            if (xmlConfigFile != null) {
                xmlConfigFile.close();
            }
        }
    }
    
    private String lowerFirst(final String name) {
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }
    
    public void setProperties(final Properties props) throws Exception {
        for (final Method method : BoneCPConfig.class.getDeclaredMethods()) {
            String tmp = null;
            Label_0465: {
                if (method.getName().startsWith("is")) {
                    tmp = this.lowerFirst(method.getName().substring(2));
                }
                else {
                    if (!method.getName().startsWith("set")) {
                        break Label_0465;
                    }
                    tmp = this.lowerFirst(method.getName().substring(3));
                }
                if (method.getParameterTypes().length == 1 && method.getParameterTypes()[0].equals(Integer.TYPE)) {
                    String val = props.getProperty(tmp);
                    if (val == null) {
                        val = props.getProperty("bonecp." + tmp);
                    }
                    if (val != null) {
                        try {
                            method.invoke(this, Integer.parseInt(val));
                        }
                        catch (NumberFormatException ex) {}
                    }
                }
                else if (method.getParameterTypes().length == 1 && method.getParameterTypes()[0].equals(Long.TYPE)) {
                    String val = props.getProperty(tmp);
                    if (val == null) {
                        val = props.getProperty("bonecp." + tmp);
                    }
                    if (val != null) {
                        try {
                            method.invoke(this, Long.parseLong(val));
                        }
                        catch (NumberFormatException ex2) {}
                    }
                }
                else if (method.getParameterTypes().length == 1 && method.getParameterTypes()[0].equals(String.class)) {
                    String val = props.getProperty(tmp);
                    if (val == null) {
                        val = props.getProperty("bonecp." + tmp);
                    }
                    if (val != null) {
                        method.invoke(this, val);
                    }
                }
                if (method.getParameterTypes().length == 1 && method.getParameterTypes()[0].equals(Boolean.TYPE)) {
                    String val = props.getProperty(tmp);
                    if (val == null) {
                        val = props.getProperty("bonecp." + tmp);
                    }
                    if (val != null) {
                        method.invoke(this, Boolean.parseBoolean(val));
                    }
                }
            }
        }
    }
    
    private Properties parseXML(final Document doc, final String sectionName) {
        int found = -1;
        final Properties results = new Properties();
        NodeList config = null;
        if (sectionName == null) {
            config = doc.getElementsByTagName("default-config");
            found = 0;
        }
        else {
            config = doc.getElementsByTagName("named-config");
            if (config != null && config.getLength() > 0) {
                for (int i = 0; i < config.getLength(); ++i) {
                    final Node node = config.item(i);
                    if (node.getNodeType() == 1) {
                        final NamedNodeMap attributes = node.getAttributes();
                        if (attributes != null && attributes.getLength() > 0) {
                            final Node name = attributes.getNamedItem("name");
                            if (name.getNodeValue().equalsIgnoreCase(sectionName)) {
                                found = i;
                                break;
                            }
                        }
                    }
                }
            }
            if (found == -1) {
                config = null;
                BoneCPConfig.logger.warn("Did not find " + sectionName + " section in config file. Reverting to defaults.");
            }
        }
        if (config != null && config.getLength() > 0) {
            final Node node2 = config.item(found);
            if (node2.getNodeType() == 1) {
                final Element elementEntry = (Element)node2;
                final NodeList childNodeList = elementEntry.getChildNodes();
                for (int j = 0; j < childNodeList.getLength(); ++j) {
                    final Node node_j = childNodeList.item(j);
                    if (node_j.getNodeType() == 1) {
                        final Element piece = (Element)node_j;
                        final NamedNodeMap attributes2 = piece.getAttributes();
                        if (attributes2 != null && attributes2.getLength() > 0) {
                            results.put(attributes2.item(0).getNodeValue(), piece.getTextContent());
                        }
                    }
                }
            }
        }
        return results;
    }
    
    public boolean isExternalAuth() {
        return this.externalAuth;
    }
    
    public void setExternalAuth(final boolean externalAuth) {
        this.externalAuth = externalAuth;
    }
    
    public void sanitize() {
        if (this.configFile != null) {
            this.loadProperties(this.configFile);
        }
        if (this.poolStrategy == null || (!this.poolStrategy.equalsIgnoreCase("DEFAULT") && !this.poolStrategy.equalsIgnoreCase("CACHED"))) {
            BoneCPConfig.logger.warn("Unrecognised pool strategy. Allowed values are DEFAULT and CACHED. Setting to DEFAULT.");
            this.poolStrategy = "DEFAULT";
        }
        this.poolStrategy = this.poolStrategy.toUpperCase();
        if (this.poolAvailabilityThreshold < 0 || this.poolAvailabilityThreshold > 100) {
            this.poolAvailabilityThreshold = 20;
        }
        if (this.defaultTransactionIsolation != null) {
            this.defaultTransactionIsolation = this.defaultTransactionIsolation.trim().toUpperCase();
            if (this.defaultTransactionIsolation.equals("NONE")) {
                this.defaultTransactionIsolationValue = 0;
            }
            else if (this.defaultTransactionIsolation.equals("READ_COMMITTED") || this.defaultTransactionIsolation.equals("READ COMMITTED")) {
                this.defaultTransactionIsolationValue = 2;
            }
            else if (this.defaultTransactionIsolation.equals("REPEATABLE_READ") || this.defaultTransactionIsolation.equals("REPEATABLE READ")) {
                this.defaultTransactionIsolationValue = 4;
            }
            else if (this.defaultTransactionIsolation.equals("READ_UNCOMMITTED") || this.defaultTransactionIsolation.equals("READ UNCOMMITTED")) {
                this.defaultTransactionIsolationValue = 1;
            }
            else if (this.defaultTransactionIsolation.equals("SERIALIZABLE")) {
                this.defaultTransactionIsolationValue = 8;
            }
            else {
                BoneCPConfig.logger.warn("Unrecognized defaultTransactionIsolation value. Using driver default.");
                this.defaultTransactionIsolationValue = -1;
            }
        }
        if (this.maxConnectionsPerPartition < 1) {
            BoneCPConfig.logger.warn("Max Connections < 1. Setting to 20");
            this.maxConnectionsPerPartition = 20;
        }
        if (this.minConnectionsPerPartition < 0) {
            BoneCPConfig.logger.warn("Min Connections < 0. Setting to 1");
            this.minConnectionsPerPartition = 1;
        }
        if (this.minConnectionsPerPartition > this.maxConnectionsPerPartition) {
            BoneCPConfig.logger.warn("Min Connections > max connections");
            this.minConnectionsPerPartition = this.maxConnectionsPerPartition;
        }
        if (this.acquireIncrement <= 0) {
            BoneCPConfig.logger.warn("acquireIncrement <= 0. Setting to 1.");
            this.acquireIncrement = 1;
        }
        if (this.partitionCount < 1) {
            BoneCPConfig.logger.warn("partitions < 1! Setting to 1");
            this.partitionCount = 1;
        }
        if (this.releaseHelperThreads < 0) {
            BoneCPConfig.logger.warn("releaseHelperThreads < 0! Setting to 0");
            this.releaseHelperThreads = 0;
        }
        if (this.statementReleaseHelperThreads < 0) {
            BoneCPConfig.logger.warn("statementReleaseHelperThreads < 0! Setting to 0");
            this.statementReleaseHelperThreads = 0;
        }
        if (this.statementsCacheSize < 0) {
            BoneCPConfig.logger.warn("preparedStatementsCacheSize < 0! Setting to 0");
            this.statementsCacheSize = 0;
        }
        if (this.acquireRetryDelayInMs <= 0L) {
            this.acquireRetryDelayInMs = 1000L;
        }
        if (!this.externalAuth && this.datasourceBean == null && this.driverProperties == null && (this.jdbcUrl == null || this.jdbcUrl.trim().equals(""))) {
            BoneCPConfig.logger.warn("JDBC url was not set in config!");
        }
        if (!this.externalAuth && this.datasourceBean == null && this.driverProperties == null && (this.username == null || this.username.trim().equals(""))) {
            BoneCPConfig.logger.warn("JDBC username was not set in config!");
        }
        if (!this.externalAuth && this.datasourceBean == null && this.driverProperties == null && this.password == null) {
            BoneCPConfig.logger.warn("JDBC password was not set in config!");
        }
        if (!this.externalAuth && this.datasourceBean == null && this.driverProperties != null) {
            if (this.driverProperties.get("user") == null && this.username == null) {
                BoneCPConfig.logger.warn("JDBC username not set in driver properties and not set in pool config either");
            }
            else if (this.driverProperties.get("user") == null && this.username != null) {
                BoneCPConfig.logger.warn("JDBC username not set in driver properties, copying it from pool config");
                this.driverProperties.setProperty("user", this.username);
            }
            else if (this.username != null && !this.driverProperties.get("user").equals(this.username)) {
                BoneCPConfig.logger.warn("JDBC username set in driver properties does not match the one set in the pool config.  Overriding it with pool config.");
                this.driverProperties.setProperty("user", this.username);
            }
        }
        if (!this.externalAuth && this.datasourceBean == null && this.driverProperties != null) {
            if (this.driverProperties.get("password") == null && this.password == null) {
                BoneCPConfig.logger.warn("JDBC password not set in driver properties and not set in pool config either");
            }
            else if (this.driverProperties.get("password") == null && this.password != null) {
                BoneCPConfig.logger.warn("JDBC password not set in driver properties, copying it from pool config");
                this.driverProperties.setProperty("password", this.password);
            }
            else if (this.password != null && !this.driverProperties.get("password").equals(this.password)) {
                BoneCPConfig.logger.warn("JDBC password set in driver properties does not match the one set in the pool config. Overriding it with pool config.");
                this.driverProperties.setProperty("password", this.password);
            }
            this.username = this.driverProperties.getProperty("user");
            this.password = this.driverProperties.getProperty("password");
        }
        if (this.username != null) {
            this.username = this.username.trim();
        }
        if (this.jdbcUrl != null) {
            this.jdbcUrl = this.jdbcUrl.trim();
        }
        if (this.password != null) {
            this.password = this.password.trim();
        }
        if (this.connectionTestStatement != null) {
            this.connectionTestStatement = this.connectionTestStatement.trim();
        }
        this.serviceOrder = this.serviceOrder.toUpperCase();
        if (!this.serviceOrder.equals("FIFO") && !this.serviceOrder.equals("LIFO")) {
            BoneCPConfig.logger.warn("Queue service order is not set to FIFO or LIFO. Defaulting to FIFO.");
            this.serviceOrder = "FIFO";
        }
        if (this.logStatementsEnabled && !BoneCPConfig.logger.isDebugEnabled()) {
            BoneCPConfig.logger.warn("LogStatementsEnabled is set to true, but log4j level is not set at DEBUG. Disabling statement logging.");
            this.logStatementsEnabled = false;
        }
    }
    
    protected void loadProperties(final String filename) {
        final ClassLoader thclassLoader = Thread.currentThread().getContextClassLoader();
        if (thclassLoader != null) {
            final URL url = thclassLoader.getResource(filename);
            if (url != null) {
                try {
                    this.setXMLProperties(url.openStream(), null);
                }
                catch (Exception ex) {}
            }
        }
    }
    
    @Override
    public String toString() {
        String result = null;
        if (this.datasourceBean != null) {
            result = String.format("JDBC URL = (via datasource bean), Username = (via datasource bean), partitions = %d, max (per partition) = %d, min (per partition) = %d, idle max age = %d min, idle test period = %d min, strategy = %s", this.partitionCount, this.maxConnectionsPerPartition, this.minConnectionsPerPartition, this.getIdleMaxAgeInMinutes(), this.getIdleConnectionTestPeriodInMinutes(), this.poolStrategy);
        }
        else {
            result = String.format("JDBC URL = %s, Username = %s, partitions = %d, max (per partition) = %d, min (per partition) = %d, idle max age = %d min, idle test period = %d min, strategy = %s", this.jdbcUrl, this.username, this.partitionCount, this.maxConnectionsPerPartition, this.minConnectionsPerPartition, this.getIdleMaxAgeInMinutes(), this.getIdleConnectionTestPeriodInMinutes(), this.poolStrategy);
        }
        return result;
    }
    
    protected Class<?> loadClass(final String clazz) throws ClassNotFoundException {
        if (this.classLoader == null) {
            return Class.forName(clazz);
        }
        return Class.forName(clazz, true, this.classLoader);
    }
    
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }
    
    public void setClassLoader(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    
    public BoneCPConfig clone() throws CloneNotSupportedException {
        final BoneCPConfig clone = (BoneCPConfig)super.clone();
        final Field[] arr$;
        final Field[] fields = arr$ = this.getClass().getDeclaredFields();
        for (final Field field : arr$) {
            try {
                field.set(clone, field.get(this));
            }
            catch (Exception ex) {}
        }
        return clone;
    }
    
    public boolean hasSameConfiguration(final BoneCPConfig that) {
        return that != null && Objects.equal(this.acquireIncrement, that.getAcquireIncrement()) && Objects.equal(this.acquireRetryDelayInMs, that.getAcquireRetryDelayInMs()) && Objects.equal(this.closeConnectionWatch, that.isCloseConnectionWatch()) && Objects.equal(this.logStatementsEnabled, that.isLogStatementsEnabled()) && Objects.equal(this.connectionHook, that.getConnectionHook()) && Objects.equal(this.connectionTestStatement, that.getConnectionTestStatement()) && Objects.equal(this.idleConnectionTestPeriodInSeconds, that.getIdleConnectionTestPeriod(TimeUnit.SECONDS)) && Objects.equal(this.idleMaxAgeInSeconds, that.getIdleMaxAge(TimeUnit.SECONDS)) && Objects.equal(this.initSQL, that.getInitSQL()) && Objects.equal(this.jdbcUrl, that.getJdbcUrl()) && Objects.equal(this.maxConnectionsPerPartition, that.getMaxConnectionsPerPartition()) && Objects.equal(this.minConnectionsPerPartition, that.getMinConnectionsPerPartition()) && Objects.equal(this.partitionCount, that.getPartitionCount()) && Objects.equal(this.releaseHelperThreads, that.getReleaseHelperThreads()) && Objects.equal(this.statementsCacheSize, that.getStatementsCacheSize()) && Objects.equal(this.username, that.getUsername()) && Objects.equal(this.password, that.getPassword()) && Objects.equal(this.lazyInit, that.isLazyInit()) && Objects.equal(this.transactionRecoveryEnabled, that.isTransactionRecoveryEnabled()) && Objects.equal(this.acquireRetryAttempts, that.getAcquireRetryAttempts()) && Objects.equal(this.statementReleaseHelperThreads, that.getStatementReleaseHelperThreads()) && Objects.equal(this.closeConnectionWatchTimeoutInMs, that.getCloseConnectionWatchTimeout()) && Objects.equal(this.connectionTimeoutInMs, that.getConnectionTimeoutInMs()) && Objects.equal(this.datasourceBean, that.getDatasourceBean()) && Objects.equal(this.getQueryExecuteTimeLimitInMs(), that.getQueryExecuteTimeLimitInMs()) && Objects.equal(this.poolAvailabilityThreshold, that.getPoolAvailabilityThreshold()) && Objects.equal(this.poolName, that.getPoolName()) && Objects.equal(this.disableConnectionTracking, that.isDisableConnectionTracking());
    }
    
    public boolean isDeregisterDriverOnClose() {
        return this.deregisterDriverOnClose;
    }
    
    public void setDeregisterDriverOnClose(final boolean deregisterDriverOnClose) {
        this.deregisterDriverOnClose = deregisterDriverOnClose;
    }
    
    public boolean isNullOnConnectionTimeout() {
        return this.nullOnConnectionTimeout;
    }
    
    public void setNullOnConnectionTimeout(final boolean nullOnConnectionTimeout) {
        this.nullOnConnectionTimeout = nullOnConnectionTimeout;
    }
    
    public boolean isResetConnectionOnClose() {
        return this.resetConnectionOnClose;
    }
    
    public void setResetConnectionOnClose(final boolean resetConnectionOnClose) {
        this.resetConnectionOnClose = resetConnectionOnClose;
    }
    
    public boolean isDetectUnresolvedTransactions() {
        return this.detectUnresolvedTransactions;
    }
    
    public void setDetectUnresolvedTransactions(final boolean detectUnresolvedTransactions) {
        this.detectUnresolvedTransactions = detectUnresolvedTransactions;
    }
    
    public String getPoolStrategy() {
        return this.poolStrategy;
    }
    
    public void setPoolStrategy(final String poolStrategy) {
        this.poolStrategy = poolStrategy;
    }
    
    public boolean isCloseOpenStatements() {
        return this.closeOpenStatements;
    }
    
    public void setCloseOpenStatements(final boolean closeOpenStatements) {
        this.closeOpenStatements = closeOpenStatements;
    }
    
    public boolean isDetectUnclosedStatements() {
        return this.detectUnclosedStatements;
    }
    
    public void setDetectUnclosedStatements(final boolean detectUnclosedStatements) {
        this.detectUnclosedStatements = detectUnclosedStatements;
    }
    
    public void setClientInfo(final Properties properties) {
        this.clientInfo = properties;
    }
    
    public Properties getClientInfo() {
        return this.clientInfo;
    }
    
    static {
        logger = LoggerFactory.getLogger(BoneCPConfig.class);
    }
}
