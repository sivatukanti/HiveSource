// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

public class SftpException extends Exception
{
    public int id;
    private Throwable cause;
    
    public SftpException(final int id, final String message) {
        super(message);
        this.cause = null;
        this.id = id;
    }
    
    public SftpException(final int id, final String message, final Throwable e) {
        super(message);
        this.cause = null;
        this.id = id;
        this.cause = e;
    }
    
    @Override
    public String toString() {
        return this.id + ": " + this.getMessage();
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
