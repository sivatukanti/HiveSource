// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.impl.provider.entity;

import javax.ws.rs.core.Context;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import java.io.OutputStream;
import java.io.IOException;
import javax.xml.bind.UnmarshalException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.xml.transform.Source;
import java.io.InputStream;
import javax.ws.rs.core.MultivaluedMap;
import com.sun.jersey.impl.ImplMessages;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Providers;
import javax.xml.parsers.SAXParserFactory;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.core.provider.jaxb.AbstractJAXBProvider;

public class XMLRootObjectProvider extends AbstractJAXBProvider<Object>
{
    private final Injectable<SAXParserFactory> spf;
    
    XMLRootObjectProvider(final Injectable<SAXParserFactory> spf, final Providers ps) {
        super(ps);
        this.spf = spf;
    }
    
    XMLRootObjectProvider(final Injectable<SAXParserFactory> spf, final Providers ps, final MediaType mt) {
        super(ps, mt);
        this.spf = spf;
    }
    
    @Override
    protected JAXBContext getStoredJAXBContext(final Class type) throws JAXBException {
        return null;
    }
    
    @Override
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        try {
            return Object.class == type && this.isSupported(mediaType) && this.getUnmarshaller(type, mediaType) != null;
        }
        catch (JAXBException cause) {
            throw new RuntimeException(ImplMessages.ERROR_UNMARSHALLING_JAXB(type), cause);
        }
    }
    
    @Override
    public Object readFrom(final Class<Object> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws IOException {
        try {
            return this.getUnmarshaller(type, mediaType).unmarshal(AbstractJAXBProvider.getSAXSource(this.spf.getValue(), entityStream));
        }
        catch (UnmarshalException ex) {
            throw new WebApplicationException(ex, Response.Status.BAD_REQUEST);
        }
        catch (JAXBException ex2) {
            throw new WebApplicationException(ex2, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Override
    public boolean isWriteable(final Class<?> arg0, final Type arg1, final Annotation[] arg2, final MediaType mediaType) {
        return false;
    }
    
    @Override
    public void writeTo(final Object arg0, final Class<?> arg1, final Type arg2, final Annotation[] arg3, final MediaType arg4, final MultivaluedMap<String, Object> arg5, final OutputStream arg6) throws IOException, WebApplicationException {
        throw new IllegalArgumentException();
    }
    
    @Produces({ "application/xml" })
    @Consumes({ "application/xml" })
    public static final class App extends XMLRootObjectProvider
    {
        public App(@Context final Injectable<SAXParserFactory> spf, @Context final Providers ps) {
            super(spf, ps, MediaType.APPLICATION_XML_TYPE);
        }
    }
    
    @Produces({ "text/xml" })
    @Consumes({ "text/xml" })
    public static final class Text extends XMLRootObjectProvider
    {
        public Text(@Context final Injectable<SAXParserFactory> spf, @Context final Providers ps) {
            super(spf, ps, MediaType.TEXT_XML_TYPE);
        }
    }
    
    @Produces({ "*/*" })
    @Consumes({ "*/*" })
    public static final class General extends XMLRootObjectProvider
    {
        public General(@Context final Injectable<SAXParserFactory> spf, @Context final Providers ps) {
            super(spf, ps);
        }
        
        @Override
        protected boolean isSupported(final MediaType m) {
            return m.getSubtype().endsWith("+xml");
        }
    }
}
