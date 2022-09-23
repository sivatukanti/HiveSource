// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.template;

import com.sun.jersey.api.container.ContainerException;

public class TemplateContextException extends ContainerException
{
    public TemplateContextException() {
    }
    
    public TemplateContextException(final String message) {
        super(message);
    }
    
    public TemplateContextException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public TemplateContextException(final Throwable cause) {
        super(cause);
    }
}
