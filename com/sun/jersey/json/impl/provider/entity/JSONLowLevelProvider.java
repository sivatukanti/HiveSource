// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl.provider.entity;

import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import com.sun.jersey.core.provider.AbstractMessageReaderWriterProvider;

public abstract class JSONLowLevelProvider<T> extends AbstractMessageReaderWriterProvider<T>
{
    private final Class<T> c;
    
    protected JSONLowLevelProvider(final Class<T> c) {
        this.c = c;
    }
    
    @Override
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return type == this.c && this.isSupported(mediaType);
    }
    
    @Override
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return type == this.c && this.isSupported(mediaType);
    }
    
    protected boolean isSupported(final MediaType m) {
        return true;
    }
}
