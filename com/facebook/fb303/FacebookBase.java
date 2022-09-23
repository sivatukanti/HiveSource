// 
// Decompiled by Procyon v0.5.36
// 

package com.facebook.fb303;

import org.apache.thrift.TException;
import java.util.Map;
import java.util.AbstractMap;
import java.util.concurrent.ConcurrentHashMap;

public abstract class FacebookBase implements FacebookService.Iface
{
    private String name_;
    private long alive_;
    private final ConcurrentHashMap<String, Long> counters_;
    private final ConcurrentHashMap<String, String> options_;
    
    protected FacebookBase(final String name) {
        this.counters_ = new ConcurrentHashMap<String, Long>();
        this.options_ = new ConcurrentHashMap<String, String>();
        this.name_ = name;
        this.alive_ = System.currentTimeMillis() / 1000L;
    }
    
    @Override
    public String getName() {
        return this.name_;
    }
    
    @Override
    public abstract fb_status getStatus();
    
    @Override
    public String getStatusDetails() {
        return "";
    }
    
    public void deleteCounter(final String key) {
        this.counters_.remove(key);
    }
    
    public void resetCounter(final String key) {
        this.counters_.put(key, 0L);
    }
    
    public long incrementCounter(final String key) {
        final long val = this.getCounter(key) + 1L;
        this.counters_.put(key, val);
        return val;
    }
    
    public long incrementCounter(final String key, final long increment) {
        final long val = this.getCounter(key) + increment;
        this.counters_.put(key, val);
        return val;
    }
    
    public long setCounter(final String key, final long value) {
        this.counters_.put(key, value);
        return value;
    }
    
    @Override
    public AbstractMap<String, Long> getCounters() {
        return this.counters_;
    }
    
    @Override
    public long getCounter(final String key) {
        final Long val = this.counters_.get(key);
        if (val == null) {
            return 0L;
        }
        return val;
    }
    
    @Override
    public void setOption(final String key, final String value) {
        this.options_.put(key, value);
    }
    
    @Override
    public String getOption(final String key) {
        return this.options_.get(key);
    }
    
    @Override
    public AbstractMap<String, String> getOptions() {
        return this.options_;
    }
    
    @Override
    public long aliveSince() {
        return this.alive_;
    }
    
    public String getCpuProfile() {
        return "";
    }
    
    @Override
    public void reinitialize() {
    }
    
    @Override
    public void shutdown() {
    }
}
