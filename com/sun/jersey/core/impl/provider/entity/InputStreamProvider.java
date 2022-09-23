// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.impl.provider.entity;

import javax.ws.rs.WebApplicationException;
import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import java.io.InputStream;
import com.sun.jersey.core.provider.AbstractMessageReaderWriterProvider;

@Produces({ "application/octet-stream", "*/*" })
@Consumes({ "application/octet-stream", "*/*" })
public final class InputStreamProvider extends AbstractMessageReaderWriterProvider<InputStream>
{
    @Override
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return InputStream.class == type;
    }
    
    @Override
    public InputStream readFrom(final Class<InputStream> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws IOException {
        return entityStream;
    }
    
    @Override
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return InputStream.class.isAssignableFrom(type);
    }
    
    @Override
    public long getSize(final InputStream t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        if (t instanceof ByteArrayInputStream) {
            return ((ByteArrayInputStream)t).available();
        }
        return -1L;
    }
    
    @Override
    public void writeTo(final InputStream t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException {
        try {
            AbstractMessageReaderWriterProvider.writeTo(t, entityStream);
        }
        finally {
            t.close();
        }
    }
}
