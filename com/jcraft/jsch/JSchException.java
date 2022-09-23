// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

public class JSchException extends Exception
{
    private Throwable cause;
    
    public JSchException() {
        this.cause = null;
    }
    
    public JSchException(final String s) {
        super(s);
        this.cause = null;
    }
    
    public JSchException(final String s, final Throwable e) {
        super(s);
        this.cause = null;
        this.cause = e;
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
