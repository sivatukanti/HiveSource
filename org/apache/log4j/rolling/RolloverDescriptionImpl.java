// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.rolling;

import org.apache.log4j.rolling.helper.Action;

public final class RolloverDescriptionImpl implements RolloverDescription
{
    private final String activeFileName;
    private final boolean append;
    private final Action synchronous;
    private final Action asynchronous;
    
    public RolloverDescriptionImpl(final String activeFileName, final boolean append, final Action synchronous, final Action asynchronous) {
        if (activeFileName == null) {
            throw new NullPointerException("activeFileName");
        }
        this.append = append;
        this.activeFileName = activeFileName;
        this.synchronous = synchronous;
        this.asynchronous = asynchronous;
    }
    
    public String getActiveFileName() {
        return this.activeFileName;
    }
    
    public boolean getAppend() {
        return this.append;
    }
    
    public Action getSynchronous() {
        return this.synchronous;
    }
    
    public Action getAsynchronous() {
        return this.asynchronous;
    }
}
