// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

class JSchPartialAuthException extends JSchException
{
    String methods;
    
    public JSchPartialAuthException() {
    }
    
    public JSchPartialAuthException(final String s) {
        super(s);
        this.methods = s;
    }
    
    public String getMethods() {
        return this.methods;
    }
}
