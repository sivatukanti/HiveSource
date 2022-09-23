// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.impl.provider.entity;

import javax.ws.rs.WebApplicationException;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import com.sun.jersey.core.provider.AbstractMessageReaderWriterProvider;

@Produces({ "text/plain", "*/*" })
@Consumes({ "text/plain", "*/*" })
public final class StringProvider extends AbstractMessageReaderWriterProvider<String>
{
    @Override
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return type == String.class;
    }
    
    @Override
    public String readFrom(final Class<String> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws IOException {
        return AbstractMessageReaderWriterProvider.readFromAsString(entityStream, mediaType);
    }
    
    @Override
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return type == String.class;
    }
    
    @Override
    public void writeTo(final String t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException {
        AbstractMessageReaderWriterProvider.writeToAsString(t, entityStream, mediaType);
    }
}
