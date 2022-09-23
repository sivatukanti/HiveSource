// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.impl.provider.entity;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.io.OutputStream;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Produces;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.ext.MessageBodyWriter;

@Produces({ "application/octet-stream", "*/*" })
public final class StreamingOutputProvider implements MessageBodyWriter<StreamingOutput>
{
    @Override
    public boolean isWriteable(final Class<?> t, final Type gt, final Annotation[] as, final MediaType mediaType) {
        return StreamingOutput.class.isAssignableFrom(t);
    }
    
    @Override
    public long getSize(final StreamingOutput o, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return -1L;
    }
    
    @Override
    public void writeTo(final StreamingOutput o, final Class<?> t, final Type gt, final Annotation[] as, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entity) throws IOException {
        o.write(entity);
    }
}
