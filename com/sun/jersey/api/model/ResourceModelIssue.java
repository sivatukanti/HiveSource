// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.model;

public class ResourceModelIssue
{
    Object source;
    String message;
    boolean fatal;
    
    public ResourceModelIssue(final Object source, final String message) {
        this(source, message, false);
    }
    
    public ResourceModelIssue(final Object source, final String message, final boolean fatal) {
        this.source = source;
        this.message = message;
        this.fatal = fatal;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public boolean isFatal() {
        return this.fatal;
    }
    
    public Object getSource() {
        return this.source;
    }
}
