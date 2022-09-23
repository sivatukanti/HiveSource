// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.drivers;

import java.util.concurrent.TimeUnit;
import java.io.UnsupportedEncodingException;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;

public class OperationTrace
{
    private final String name;
    private final TracerDriver driver;
    private int returnCode;
    private long latencyMs;
    private long requestBytesLength;
    private long responseBytesLength;
    private String path;
    private boolean withWatcher;
    private long sessionId;
    private Stat stat;
    private final long startTimeNanos;
    
    public OperationTrace(final String name, final TracerDriver driver) {
        this(name, driver, -1L);
    }
    
    public OperationTrace(final String name, final TracerDriver driver, final long sessionId) {
        this.returnCode = KeeperException.Code.OK.intValue();
        this.startTimeNanos = System.nanoTime();
        this.name = name;
        this.driver = driver;
        this.sessionId = sessionId;
    }
    
    public OperationTrace setReturnCode(final int returnCode) {
        this.returnCode = returnCode;
        return this;
    }
    
    public OperationTrace setRequestBytesLength(final long length) {
        this.requestBytesLength = length;
        return this;
    }
    
    public OperationTrace setRequestBytesLength(final String data) {
        if (data == null) {
            return this;
        }
        try {
            this.setRequestBytesLength(data.getBytes("UTF-8").length);
        }
        catch (UnsupportedEncodingException ex) {}
        return this;
    }
    
    public OperationTrace setRequestBytesLength(final byte[] data) {
        if (data == null) {
            return this;
        }
        return this.setRequestBytesLength(data.length);
    }
    
    public OperationTrace setResponseBytesLength(final long length) {
        this.responseBytesLength = length;
        return this;
    }
    
    public OperationTrace setResponseBytesLength(final byte[] data) {
        if (data == null) {
            return this;
        }
        return this.setResponseBytesLength(data.length);
    }
    
    public OperationTrace setPath(final String path) {
        this.path = path;
        return this;
    }
    
    public OperationTrace setWithWatcher(final boolean withWatcher) {
        this.withWatcher = withWatcher;
        return this;
    }
    
    public OperationTrace setStat(final Stat stat) {
        this.stat = stat;
        return this;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getReturnCode() {
        return this.returnCode;
    }
    
    public long getLatencyMs() {
        return this.latencyMs;
    }
    
    public long getRequestBytesLength() {
        return this.requestBytesLength;
    }
    
    public long getResponseBytesLength() {
        return this.responseBytesLength;
    }
    
    public long getSessionId() {
        return this.sessionId;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public boolean isWithWatcher() {
        return this.withWatcher;
    }
    
    public Stat getStat() {
        return this.stat;
    }
    
    public void commit() {
        final long elapsed = System.nanoTime() - this.startTimeNanos;
        this.latencyMs = TimeUnit.MILLISECONDS.convert(elapsed, TimeUnit.NANOSECONDS);
        if (this.driver instanceof AdvancedTracerDriver) {
            ((AdvancedTracerDriver)this.driver).addTrace(this);
        }
        else {
            this.driver.addTrace(this.name, elapsed, TimeUnit.NANOSECONDS);
        }
    }
}
