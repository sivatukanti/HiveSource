// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.impl.provider.entity;

import javax.ws.rs.WebApplicationException;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import java.io.Reader;
import com.sun.jersey.core.provider.AbstractMessageReaderWriterProvider;

@Produces({ "text/plain", "*/*" })
@Consumes({ "text/plain", "*/*" })
public final class ReaderProvider extends AbstractMessageReaderWriterProvider<Reader>
{
    @Override
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return Reader.class == type;
    }
    
    @Override
    public Reader readFrom(final Class<Reader> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws IOException {
        return new BufferedReader(new InputStreamReader(entityStream, AbstractMessageReaderWriterProvider.getCharset(mediaType)));
    }
    
    @Override
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return Reader.class.isAssignableFrom(type);
    }
    
    @Override
    public void writeTo(final Reader t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException {
        try {
            final OutputStreamWriter out = new OutputStreamWriter(entityStream, AbstractMessageReaderWriterProvider.getCharset(mediaType));
            AbstractMessageReaderWriterProvider.writeTo(t, out);
            out.flush();
        }
        finally {
            t.close();
        }
    }
}
