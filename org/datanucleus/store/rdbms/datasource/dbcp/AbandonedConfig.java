// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp;

import java.io.OutputStream;
import java.io.PrintWriter;

public class AbandonedConfig
{
    private boolean removeAbandoned;
    private int removeAbandonedTimeout;
    private boolean logAbandoned;
    private PrintWriter logWriter;
    
    public AbandonedConfig() {
        this.removeAbandoned = false;
        this.removeAbandonedTimeout = 300;
        this.logAbandoned = false;
        this.logWriter = new PrintWriter(System.out);
    }
    
    public boolean getRemoveAbandoned() {
        return this.removeAbandoned;
    }
    
    public void setRemoveAbandoned(final boolean removeAbandoned) {
        this.removeAbandoned = removeAbandoned;
    }
    
    public int getRemoveAbandonedTimeout() {
        return this.removeAbandonedTimeout;
    }
    
    public void setRemoveAbandonedTimeout(final int removeAbandonedTimeout) {
        this.removeAbandonedTimeout = removeAbandonedTimeout;
    }
    
    public boolean getLogAbandoned() {
        return this.logAbandoned;
    }
    
    public void setLogAbandoned(final boolean logAbandoned) {
        this.logAbandoned = logAbandoned;
    }
    
    public PrintWriter getLogWriter() {
        return this.logWriter;
    }
    
    public void setLogWriter(final PrintWriter logWriter) {
        this.logWriter = logWriter;
    }
}
