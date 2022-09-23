// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public class AbandonedTrace
{
    private final AbandonedConfig config;
    private volatile Exception createdBy;
    private final List traceList;
    private volatile long lastUsed;
    
    public AbandonedTrace() {
        this.traceList = new ArrayList();
        this.lastUsed = 0L;
        this.config = null;
        this.init(null);
    }
    
    public AbandonedTrace(final AbandonedConfig config) {
        this.traceList = new ArrayList();
        this.lastUsed = 0L;
        this.config = config;
        this.init(null);
    }
    
    public AbandonedTrace(final AbandonedTrace parent) {
        this.traceList = new ArrayList();
        this.lastUsed = 0L;
        this.config = parent.getConfig();
        this.init(parent);
    }
    
    private void init(final AbandonedTrace parent) {
        if (parent != null) {
            parent.addTrace(this);
        }
        if (this.config == null) {
            return;
        }
        if (this.config.getLogAbandoned()) {
            this.createdBy = new AbandonedObjectException();
        }
    }
    
    protected AbandonedConfig getConfig() {
        return this.config;
    }
    
    protected long getLastUsed() {
        return this.lastUsed;
    }
    
    protected void setLastUsed() {
        this.lastUsed = System.currentTimeMillis();
    }
    
    protected void setLastUsed(final long time) {
        this.lastUsed = time;
    }
    
    protected void setStackTrace() {
        if (this.config == null) {
            return;
        }
        if (this.config.getLogAbandoned()) {
            this.createdBy = new AbandonedObjectException();
        }
    }
    
    protected void addTrace(final AbandonedTrace trace) {
        synchronized (this.traceList) {
            this.traceList.add(trace);
        }
        this.setLastUsed();
    }
    
    protected void clearTrace() {
        synchronized (this.traceList) {
            this.traceList.clear();
        }
    }
    
    protected List getTrace() {
        synchronized (this.traceList) {
            return new ArrayList(this.traceList);
        }
    }
    
    public void printStackTrace() {
        if (this.createdBy != null && this.config != null) {
            this.createdBy.printStackTrace(this.config.getLogWriter());
        }
        synchronized (this.traceList) {
            for (final AbandonedTrace at : this.traceList) {
                at.printStackTrace();
            }
        }
    }
    
    protected void removeTrace(final AbandonedTrace trace) {
        synchronized (this.traceList) {
            this.traceList.remove(trace);
        }
    }
    
    static class AbandonedObjectException extends Exception
    {
        private static final long serialVersionUID = 7398692158058772916L;
        private static final SimpleDateFormat format;
        private final long _createdTime;
        
        public AbandonedObjectException() {
            this._createdTime = System.currentTimeMillis();
        }
        
        @Override
        public String getMessage() {
            final String msg;
            synchronized (AbandonedObjectException.format) {
                msg = AbandonedObjectException.format.format(new Date(this._createdTime));
            }
            return msg;
        }
        
        static {
            format = new SimpleDateFormat("'DBCP object created' yyyy-MM-dd HH:mm:ss 'by the following code was never closed:'");
        }
    }
}
