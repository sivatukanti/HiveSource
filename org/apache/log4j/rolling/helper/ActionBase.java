// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.rolling.helper;

import java.io.IOException;

public abstract class ActionBase implements Action
{
    private boolean complete;
    private boolean interrupted;
    
    protected ActionBase() {
        this.complete = false;
        this.interrupted = false;
    }
    
    public abstract boolean execute() throws IOException;
    
    public synchronized void run() {
        if (!this.interrupted) {
            try {
                this.execute();
            }
            catch (IOException ex) {
                this.reportException(ex);
            }
            this.complete = true;
            this.interrupted = true;
        }
    }
    
    public synchronized void close() {
        this.interrupted = true;
    }
    
    public boolean isComplete() {
        return this.complete;
    }
    
    protected void reportException(final Exception ex) {
    }
}
