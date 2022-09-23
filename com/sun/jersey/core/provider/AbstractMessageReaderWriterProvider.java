// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.provider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.core.MediaType;
import java.io.Writer;
import java.io.Reader;
import java.io.IOException;
import com.sun.jersey.core.util.ReaderWriter;
import java.io.OutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.MessageBodyReader;

public abstract class AbstractMessageReaderWriterProvider<T> implements MessageBodyReader<T>, MessageBodyWriter<T>
{
    public static final Charset UTF8;
    
    public static final void writeTo(final InputStream in, final OutputStream out) throws IOException {
        ReaderWriter.writeTo(in, out);
    }
    
    public static final void writeTo(final Reader in, final Writer out) throws IOException {
        ReaderWriter.writeTo(in, out);
    }
    
    public static final Charset getCharset(final MediaType m) {
        return ReaderWriter.getCharset(m);
    }
    
    public static final String readFromAsString(final InputStream in, final MediaType type) throws IOException {
        return ReaderWriter.readFromAsString(in, type);
    }
    
    public static final void writeToAsString(final String s, final OutputStream out, final MediaType type) throws IOException {
        ReaderWriter.writeToAsString(s, out, type);
    }
    
    @Override
    public long getSize(final T t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return -1L;
    }
    
    static {
        UTF8 = ReaderWriter.UTF8;
    }
}
