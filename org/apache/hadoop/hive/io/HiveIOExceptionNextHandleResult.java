// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.io;

public class HiveIOExceptionNextHandleResult
{
    private boolean handled;
    private boolean handleResult;
    
    public boolean getHandled() {
        return this.handled;
    }
    
    public void setHandled(final boolean handled) {
        this.handled = handled;
    }
    
    public boolean getHandleResult() {
        return this.handleResult;
    }
    
    public void setHandleResult(final boolean handleResult) {
        this.handleResult = handleResult;
    }
    
    public void clear() {
        this.handled = false;
        this.handleResult = false;
    }
}
