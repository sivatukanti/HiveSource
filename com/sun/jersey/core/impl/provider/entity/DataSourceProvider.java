// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.impl.provider.entity;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import com.sun.jersey.core.util.ReaderWriter;
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
import javax.activation.DataSource;
import com.sun.jersey.core.provider.AbstractMessageReaderWriterProvider;

@Produces({ "application/octet-stream", "*/*" })
@Consumes({ "application/octet-stream", "*/*" })
public class DataSourceProvider extends AbstractMessageReaderWriterProvider<DataSource>
{
    public DataSourceProvider() {
        final Class<?> c = DataSource.class;
    }
    
    @Override
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return DataSource.class == type;
    }
    
    @Override
    public DataSource readFrom(final Class<DataSource> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws IOException {
        final ByteArrayDataSource ds = new ByteArrayDataSource(entityStream, (mediaType == null) ? null : mediaType.toString());
        return ds;
    }
    
    @Override
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return DataSource.class.isAssignableFrom(type);
    }
    
    @Override
    public void writeTo(final DataSource t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException {
        final InputStream in = t.getInputStream();
        try {
            AbstractMessageReaderWriterProvider.writeTo(in, entityStream);
        }
        finally {
            in.close();
        }
    }
    
    public static class ByteArrayDataSource implements DataSource
    {
        private byte[] data;
        private int len;
        private String type;
        private String name;
        
        public ByteArrayDataSource(final InputStream is, final String type) throws IOException {
            this.len = -1;
            this.name = "";
            final DSByteArrayOutputStream os = new DSByteArrayOutputStream();
            ReaderWriter.writeTo(is, os);
            this.data = os.getBuf();
            this.len = os.getCount();
            if (this.data.length - this.len > 262144) {
                this.data = os.toByteArray();
                this.len = this.data.length;
            }
            this.type = type;
        }
        
        @Override
        public InputStream getInputStream() throws IOException {
            if (this.data == null) {
                throw new IOException("no data");
            }
            if (this.len < 0) {
                this.len = this.data.length;
            }
            return new ByteArrayInputStream(this.data, 0, this.len);
        }
        
        @Override
        public OutputStream getOutputStream() throws IOException {
            throw new IOException("cannot do this");
        }
        
        @Override
        public String getContentType() {
            return this.type;
        }
        
        @Override
        public String getName() {
            return this.name;
        }
        
        public void setName(final String name) {
            this.name = name;
        }
        
        static class DSByteArrayOutputStream extends ByteArrayOutputStream
        {
            public byte[] getBuf() {
                return this.buf;
            }
            
            public int getCount() {
                return this.count;
            }
        }
    }
}
