// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service;

import org.apache.hadoop.hive.conf.HiveConf;

public class FilterService implements Service
{
    private final Service service;
    private final long startTime;
    
    public FilterService(final Service service) {
        this.startTime = System.currentTimeMillis();
        this.service = service;
    }
    
    @Override
    public void init(final HiveConf config) {
        this.service.init(config);
    }
    
    @Override
    public void start() {
        this.service.start();
    }
    
    @Override
    public void stop() {
        this.service.stop();
    }
    
    @Override
    public void register(final ServiceStateChangeListener listener) {
        this.service.register(listener);
    }
    
    @Override
    public void unregister(final ServiceStateChangeListener listener) {
        this.service.unregister(listener);
    }
    
    @Override
    public String getName() {
        return this.service.getName();
    }
    
    @Override
    public HiveConf getHiveConf() {
        return this.service.getHiveConf();
    }
    
    @Override
    public STATE getServiceState() {
        return this.service.getServiceState();
    }
    
    @Override
    public long getStartTime() {
        return this.startTime;
    }
}
