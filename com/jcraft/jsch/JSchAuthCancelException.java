// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

class JSchAuthCancelException extends JSchException
{
    String method;
    
    JSchAuthCancelException() {
    }
    
    JSchAuthCancelException(final String s) {
        super(s);
        this.method = s;
    }
    
    public String getMethod() {
        return this.method;
    }
}
