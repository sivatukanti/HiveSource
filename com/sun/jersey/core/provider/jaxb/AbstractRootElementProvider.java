// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.provider.jaxb;

import java.nio.charset.Charset;
import javax.xml.bind.Marshaller;
import com.sun.jersey.core.provider.AbstractMessageReaderWriterProvider;
import java.io.OutputStream;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlRootElement;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Providers;

public abstract class AbstractRootElementProvider extends AbstractJAXBProvider<Object>
{
    public AbstractRootElementProvider(final Providers ps) {
        super(ps);
    }
    
    public AbstractRootElementProvider(final Providers ps, final MediaType mt) {
        super(ps, mt);
    }
    
    @Override
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return (type.getAnnotation(XmlRootElement.class) != null || type.getAnnotation(XmlType.class) != null) && this.isSupported(mediaType);
    }
    
    @Override
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return type.getAnnotation(XmlRootElement.class) != null && this.isSupported(mediaType);
    }
    
    @Override
    public final Object readFrom(final Class<Object> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws IOException {
        try {
            return this.readFrom(type, mediaType, this.getUnmarshaller(type, mediaType), entityStream);
        }
        catch (UnmarshalException ex) {
            throw new WebApplicationException(ex, Response.Status.BAD_REQUEST);
        }
        catch (JAXBException ex2) {
            throw new WebApplicationException(ex2, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
    
    protected Object readFrom(final Class<Object> type, final MediaType mediaType, final Unmarshaller u, final InputStream entityStream) throws JAXBException {
        if (type.isAnnotationPresent(XmlRootElement.class)) {
            return u.unmarshal(entityStream);
        }
        return u.unmarshal(new StreamSource(entityStream), type).getValue();
    }
    
    @Override
    public final void writeTo(final Object t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException {
        try {
            final Marshaller m = this.getMarshaller(type, mediaType);
            final Charset c = AbstractMessageReaderWriterProvider.getCharset(mediaType);
            if (c != AbstractRootElementProvider.UTF8) {
                m.setProperty("jaxb.encoding", c.name());
            }
            this.setHeader(m, annotations);
            this.writeTo(t, mediaType, c, m, entityStream);
        }
        catch (JAXBException ex) {
            throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
    
    protected void writeTo(final Object t, final MediaType mediaType, final Charset c, final Marshaller m, final OutputStream entityStream) throws JAXBException {
        m.marshal(t, entityStream);
    }
}
