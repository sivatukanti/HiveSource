// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.impl.provider.entity;

import javax.ws.rs.WebApplicationException;
import java.io.BufferedInputStream;
import com.sun.jersey.core.util.ReaderWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import java.io.File;
import com.sun.jersey.core.provider.AbstractMessageReaderWriterProvider;

@Produces({ "application/octet-stream", "*/*" })
@Consumes({ "application/octet-stream", "*/*" })
public final class FileProvider extends AbstractMessageReaderWriterProvider<File>
{
    @Override
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return File.class == type;
    }
    
    @Override
    public File readFrom(final Class<File> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws IOException {
        final File f = File.createTempFile("rep", "tmp");
        final OutputStream out = new BufferedOutputStream(new FileOutputStream(f));
        try {
            AbstractMessageReaderWriterProvider.writeTo(entityStream, out);
        }
        finally {
            out.close();
        }
        return f;
    }
    
    @Override
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return File.class.isAssignableFrom(type);
    }
    
    @Override
    public void writeTo(final File t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException {
        final InputStream in = new BufferedInputStream(new FileInputStream(t), ReaderWriter.BUFFER_SIZE);
        try {
            AbstractMessageReaderWriterProvider.writeTo(in, entityStream);
        }
        finally {
            in.close();
        }
    }
    
    @Override
    public long getSize(final File t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return t.length();
    }
}
