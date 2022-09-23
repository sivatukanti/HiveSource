// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.pkix;

public class PkiException extends Exception
{
    private static final long serialVersionUID = 7305497872367599428L;
    
    public PkiException(final String message) {
        super(message);
    }
    
    public PkiException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
