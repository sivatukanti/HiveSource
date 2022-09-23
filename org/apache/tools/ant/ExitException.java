// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant;

public class ExitException extends SecurityException
{
    private static final long serialVersionUID = 2772487854280543363L;
    private int status;
    
    public ExitException(final int status) {
        super("ExitException: status " + status);
        this.status = status;
    }
    
    public ExitException(final String msg, final int status) {
        super(msg);
        this.status = status;
    }
    
    public int getStatus() {
        return this.status;
    }
}
