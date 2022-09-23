// 
// Decompiled by Procyon v0.5.36
// 

package com.jolbox.bonecp;

import com.jolbox.bonecp.hooks.ConnectionHook;

public interface BoneCPConfigMBean
{
    String getPoolName();
    
    int getMinConnectionsPerPartition();
    
    int getMaxConnectionsPerPartition();
    
    int getAcquireIncrement();
    
    int getPartitionCount();
    
    String getJdbcUrl();
    
    String getUsername();
    
    long getIdleConnectionTestPeriodInMinutes();
    
    long getIdleMaxAgeInMinutes();
    
    String getConnectionTestStatement();
    
    int getStatementsCacheSize();
    
    int getReleaseHelperThreads();
    
    int getStatementsCachedPerConnection();
    
    ConnectionHook getConnectionHook();
    
    String getInitSQL();
    
    boolean isLogStatementsEnabled();
    
    long getAcquireRetryDelayInMs();
    
    boolean isLazyInit();
    
    boolean isTransactionRecoveryEnabled();
    
    int getAcquireRetryAttempts();
    
    String getConnectionHookClassName();
    
    boolean isDisableJMX();
    
    long getQueryExecuteTimeLimitInMs();
    
    int getPoolAvailabilityThreshold();
    
    boolean isDisableConnectionTracking();
    
    long getConnectionTimeoutInMs();
    
    long getCloseConnectionWatchTimeoutInMs();
    
    int getStatementReleaseHelperThreads();
    
    long getMaxConnectionAgeInSeconds();
    
    String getConfigFile();
    
    String getServiceOrder();
    
    boolean isStatisticsEnabled();
}
