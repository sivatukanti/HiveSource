// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.authorize;

import java.io.PrintWriter;
import java.io.PrintStream;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.security.AccessControlException;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class AuthorizationException extends AccessControlException
{
    private static final long serialVersionUID = 1L;
    private static StackTraceElement[] stackTrace;
    
    public AuthorizationException() {
    }
    
    public AuthorizationException(final String message) {
        super(message);
    }
    
    public AuthorizationException(final Throwable cause) {
        super(cause);
    }
    
    @Override
    public StackTraceElement[] getStackTrace() {
        return AuthorizationException.stackTrace;
    }
    
    @Override
    public void printStackTrace() {
        this.printStackTrace(System.err);
    }
    
    @Override
    public void printStackTrace(final PrintStream s) {
        s.println(this);
    }
    
    @Override
    public void printStackTrace(final PrintWriter s) {
        s.println(this);
    }
    
    static {
        AuthorizationException.stackTrace = new StackTraceElement[0];
    }
}
