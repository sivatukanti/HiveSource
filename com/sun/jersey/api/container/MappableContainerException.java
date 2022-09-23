// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.container;

public class MappableContainerException extends ContainerException
{
    public MappableContainerException(final Throwable cause) {
        super(strip(cause));
    }
    
    private static Throwable strip(Throwable cause) {
        if (cause instanceof MappableContainerException) {
            do {
                final MappableContainerException mce = (MappableContainerException)cause;
                cause = mce.getCause();
            } while (cause instanceof MappableContainerException);
        }
        return cause;
    }
}
