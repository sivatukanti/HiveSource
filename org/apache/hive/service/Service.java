// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service;

import org.apache.hadoop.hive.conf.HiveConf;

public interface Service
{
    void init(final HiveConf p0);
    
    void start();
    
    void stop();
    
    void register(final ServiceStateChangeListener p0);
    
    void unregister(final ServiceStateChangeListener p0);
    
    String getName();
    
    HiveConf getHiveConf();
    
    STATE getServiceState();
    
    long getStartTime();
    
    public enum STATE
    {
        NOTINITED, 
        INITED, 
        STARTED, 
        STOPPED;
    }
}
