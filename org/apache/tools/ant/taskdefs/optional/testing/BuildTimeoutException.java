// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.testing;

import org.apache.tools.ant.Location;
import org.apache.tools.ant.BuildException;

public class BuildTimeoutException extends BuildException
{
    private static final long serialVersionUID = -8057644603246297562L;
    
    public BuildTimeoutException() {
    }
    
    public BuildTimeoutException(final String message) {
        super(message);
    }
    
    public BuildTimeoutException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public BuildTimeoutException(final String msg, final Throwable cause, final Location location) {
        super(msg, cause, location);
    }
    
    public BuildTimeoutException(final Throwable cause) {
        super(cause);
    }
    
    public BuildTimeoutException(final String message, final Location location) {
        super(message, location);
    }
    
    public BuildTimeoutException(final Throwable cause, final Location location) {
        super(cause, location);
    }
}
