// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.exceptions;

public class ApplicationMasterNotRegisteredException extends YarnException
{
    private static final long serialVersionUID = 13498238L;
    
    public ApplicationMasterNotRegisteredException(final Throwable cause) {
        super(cause);
    }
    
    public ApplicationMasterNotRegisteredException(final String message) {
        super(message);
    }
    
    public ApplicationMasterNotRegisteredException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
