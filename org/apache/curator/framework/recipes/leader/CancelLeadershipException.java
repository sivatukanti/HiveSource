// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.leader;

public class CancelLeadershipException extends RuntimeException
{
    public CancelLeadershipException() {
    }
    
    public CancelLeadershipException(final String message) {
        super(message);
    }
    
    public CancelLeadershipException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public CancelLeadershipException(final Throwable cause) {
        super(cause);
    }
}
