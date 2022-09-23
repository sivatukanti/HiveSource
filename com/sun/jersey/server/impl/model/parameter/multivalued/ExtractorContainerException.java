// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.model.parameter.multivalued;

import com.sun.jersey.api.container.ContainerException;

public class ExtractorContainerException extends ContainerException
{
    public ExtractorContainerException() {
    }
    
    public ExtractorContainerException(final String message) {
        super(message);
    }
    
    public ExtractorContainerException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public ExtractorContainerException(final Throwable cause) {
        super(cause);
    }
}
