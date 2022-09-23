// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import javax.servlet.ServletException;

public class QuietServletException extends ServletException
{
    public QuietServletException() {
    }
    
    public QuietServletException(final String message, final Throwable rootCause) {
        super(message, rootCause);
    }
    
    public QuietServletException(final String message) {
        super(message);
    }
    
    public QuietServletException(final Throwable rootCause) {
        super(rootCause);
    }
}
