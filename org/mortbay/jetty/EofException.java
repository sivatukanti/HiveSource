// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty;

import java.io.EOFException;

public class EofException extends EOFException
{
    public EofException() {
    }
    
    public EofException(final String reason) {
        super(reason);
    }
    
    public EofException(final Throwable th) {
        this.initCause(th);
    }
}
