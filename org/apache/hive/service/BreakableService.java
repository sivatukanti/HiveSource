// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service;

import org.apache.hadoop.hive.conf.HiveConf;

public class BreakableService extends AbstractService
{
    private boolean failOnInit;
    private boolean failOnStart;
    private boolean failOnStop;
    private final int[] counts;
    
    public BreakableService() {
        this(false, false, false);
    }
    
    public BreakableService(final boolean failOnInit, final boolean failOnStart, final boolean failOnStop) {
        super("BreakableService");
        this.counts = new int[4];
        this.failOnInit = failOnInit;
        this.failOnStart = failOnStart;
        this.failOnStop = failOnStop;
        this.inc(Service.STATE.NOTINITED);
    }
    
    private int convert(final Service.STATE state) {
        switch (state) {
            case NOTINITED: {
                return 0;
            }
            case INITED: {
                return 1;
            }
            case STARTED: {
                return 2;
            }
            case STOPPED: {
                return 3;
            }
            default: {
                return 0;
            }
        }
    }
    
    private void inc(final Service.STATE state) {
        final int index = this.convert(state);
        final int[] counts = this.counts;
        final int n = index;
        ++counts[n];
    }
    
    public int getCount(final Service.STATE state) {
        return this.counts[this.convert(state)];
    }
    
    private void maybeFail(final boolean fail, final String action) {
        if (fail) {
            throw new BrokenLifecycleEvent(action);
        }
    }
    
    @Override
    public void init(final HiveConf conf) {
        this.inc(Service.STATE.INITED);
        this.maybeFail(this.failOnInit, "init");
        super.init(conf);
    }
    
    @Override
    public void start() {
        this.inc(Service.STATE.STARTED);
        this.maybeFail(this.failOnStart, "start");
        super.start();
    }
    
    @Override
    public void stop() {
        this.inc(Service.STATE.STOPPED);
        this.maybeFail(this.failOnStop, "stop");
        super.stop();
    }
    
    public void setFailOnInit(final boolean failOnInit) {
        this.failOnInit = failOnInit;
    }
    
    public void setFailOnStart(final boolean failOnStart) {
        this.failOnStart = failOnStart;
    }
    
    public void setFailOnStop(final boolean failOnStop) {
        this.failOnStop = failOnStop;
    }
    
    public static class BrokenLifecycleEvent extends RuntimeException
    {
        BrokenLifecycleEvent(final String action) {
            super("Lifecycle Failure during " + action);
        }
    }
}
