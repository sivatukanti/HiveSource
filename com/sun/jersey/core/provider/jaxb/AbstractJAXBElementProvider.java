// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.provider.jaxb;

import java.nio.charset.Charset;
import javax.xml.bind.Marshaller;
import com.sun.jersey.core.provider.AbstractMessageReaderWriterProvider;
import java.io.OutputStream;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import javax.ws.rs.core.MultivaluedMap;
import java.lang.reflect.ParameterizedType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBElement;

public abstract class AbstractJAXBElementProvider extends AbstractJAXBProvider<JAXBElement<?>>
{
    public AbstractJAXBElementProvider(final Providers ps) {
        super(ps);
    }
    
    public AbstractJAXBElementProvider(final Providers ps, final MediaType mt) {
        super(ps, mt);
    }
    
    @Override
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return type == JAXBElement.class && genericType instanceof ParameterizedType && this.isSupported(mediaType);
    }
    
    @Override
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return JAXBElement.class.isAssignableFrom(type) && this.isSupported(mediaType);
    }
    
    @Override
    public final JAXBElement<?> readFrom(final Class<JAXBElement<?>> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws IOException {
        final ParameterizedType pt = (ParameterizedType)genericType;
        final Class ta = (Class)pt.getActualTypeArguments()[0];
        try {
            return this.readFrom(ta, mediaType, this.getUnmarshaller(ta, mediaType), entityStream);
        }
        catch (UnmarshalException ex) {
            throw new WebApplicationException(ex, Response.Status.BAD_REQUEST);
        }
        catch (JAXBException ex2) {
            throw new WebApplicationException(ex2, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
    
    protected abstract JAXBElement<?> readFrom(final Class<?> p0, final MediaType p1, final Unmarshaller p2, final InputStream p3) throws JAXBException;
    
    @Override
    public final void writeTo(final JAXBElement<?> t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException {
        try {
            final Marshaller m = this.getMarshaller(t.getDeclaredType(), mediaType);
            final Charset c = AbstractMessageReaderWriterProvider.getCharset(mediaType);
            if (c != AbstractJAXBElementProvider.UTF8) {
                m.setProperty("jaxb.encoding", c.name());
            }
            this.setHeader(m, annotations);
            this.writeTo(t, mediaType, c, m, entityStream);
        }
        catch (JAXBException ex) {
            throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
    
    protected abstract void writeTo(final JAXBElement<?> p0, final MediaType p1, final Charset p2, final Marshaller p3, final OutputStream p4) throws JAXBException;
}
