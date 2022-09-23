// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.impl.provider.entity;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import com.sun.jersey.core.provider.AbstractMessageReaderWriterProvider;

@Produces({ "application/octet-stream", "*/*" })
@Consumes({ "application/octet-stream", "*/*" })
public final class ByteArrayProvider extends AbstractMessageReaderWriterProvider<byte[]>
{
    public boolean supports(final Class type) {
        return type == byte[].class;
    }
    
    @Override
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return type == byte[].class;
    }
    
    @Override
    public byte[] readFrom(final Class<byte[]> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        AbstractMessageReaderWriterProvider.writeTo(entityStream, out);
        return out.toByteArray();
    }
    
    @Override
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return type == byte[].class;
    }
    
    @Override
    public void writeTo(final byte[] t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException {
        entityStream.write(t);
    }
    
    @Override
    public long getSize(final byte[] t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return t.length;
    }
}
