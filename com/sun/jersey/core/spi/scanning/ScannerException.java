// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.spi.scanning;

public class ScannerException extends RuntimeException
{
    public ScannerException() {
    }
    
    public ScannerException(final String message) {
        super(message);
    }
    
    public ScannerException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public ScannerException(final Throwable cause) {
        super(cause);
    }
}
