// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api;

import java.lang.reflect.Type;
import com.sun.jersey.core.spi.factory.ResponseImpl;

public final class JResponseAsResponse extends ResponseImpl
{
    private final JResponse<?> jr;
    
    JResponseAsResponse(final JResponse<?> jr) {
        this(jr, jr.getType());
    }
    
    JResponseAsResponse(final JResponse<?> jr, final Type type) {
        super(jr.getStatusType(), jr.getMetadata(), jr.getEntity(), type);
        this.jr = jr;
    }
    
    public JResponse<?> getJResponse() {
        return this.jr;
    }
}
