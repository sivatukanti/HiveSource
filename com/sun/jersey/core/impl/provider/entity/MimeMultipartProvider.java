// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.impl.provider.entity;

import java.io.OutputStream;
import java.io.IOException;
import javax.mail.MessagingException;
import javax.mail.internet.ParseException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import java.io.InputStream;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.mail.internet.MimeMultipart;
import com.sun.jersey.core.provider.AbstractMessageReaderWriterProvider;

public final class MimeMultipartProvider extends AbstractMessageReaderWriterProvider<MimeMultipart>
{
    public MimeMultipartProvider() {
        final Class<?> c = MimeMultipart.class;
    }
    
    @Override
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return type == MimeMultipart.class;
    }
    
    @Override
    public MimeMultipart readFrom(final Class<MimeMultipart> type, final Type genericType, final Annotation[] annotations, MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws IOException {
        if (mediaType == null) {
            mediaType = new MediaType("multipart", "form-data");
        }
        final ByteArrayDataSource ds = new ByteArrayDataSource(entityStream, mediaType.toString());
        try {
            return new MimeMultipart(ds);
        }
        catch (ParseException ex) {
            throw new WebApplicationException(ex, Response.Status.BAD_REQUEST);
        }
        catch (MessagingException ex2) {
            throw new WebApplicationException(ex2, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Override
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return type == MimeMultipart.class;
    }
    
    @Override
    public void writeTo(final MimeMultipart t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException {
        try {
            t.writeTo(entityStream);
        }
        catch (MessagingException ex) {
            throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
