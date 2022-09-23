// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.crypto;

public class UnsupportedCodecException extends RuntimeException
{
    private static final long serialVersionUID = 6713920435487942224L;
    
    public UnsupportedCodecException() {
    }
    
    public UnsupportedCodecException(final String message) {
        super(message);
    }
    
    public UnsupportedCodecException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public UnsupportedCodecException(final Throwable cause) {
        super(cause);
    }
}
