// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.impl.provider.entity;

import javax.ws.rs.core.Context;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.xml.bind.JAXBException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.Source;
import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlRootElement;
import com.sun.jersey.core.provider.jaxb.AbstractJAXBProvider;
import java.io.InputStream;
import javax.xml.bind.Unmarshaller;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Providers;
import javax.xml.parsers.SAXParserFactory;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.core.provider.jaxb.AbstractRootElementProvider;

public class XMLRootElementProvider extends AbstractRootElementProvider
{
    private final Injectable<SAXParserFactory> spf;
    
    XMLRootElementProvider(final Injectable<SAXParserFactory> spf, final Providers ps) {
        super(ps);
        this.spf = spf;
    }
    
    XMLRootElementProvider(final Injectable<SAXParserFactory> spf, final Providers ps, final MediaType mt) {
        super(ps, mt);
        this.spf = spf;
    }
    
    @Override
    protected Object readFrom(final Class<Object> type, final MediaType mediaType, final Unmarshaller u, final InputStream entityStream) throws JAXBException {
        final SAXSource s = AbstractJAXBProvider.getSAXSource(this.spf.getValue(), entityStream);
        if (type.isAnnotationPresent(XmlRootElement.class)) {
            return u.unmarshal(s);
        }
        return u.unmarshal(s, type).getValue();
    }
    
    @Produces({ "application/xml" })
    @Consumes({ "application/xml" })
    public static final class App extends XMLRootElementProvider
    {
        public App(@Context final Injectable<SAXParserFactory> spf, @Context final Providers ps) {
            super(spf, ps, MediaType.APPLICATION_XML_TYPE);
        }
    }
    
    @Produces({ "text/xml" })
    @Consumes({ "text/xml" })
    public static final class Text extends XMLRootElementProvider
    {
        public Text(@Context final Injectable<SAXParserFactory> spf, @Context final Providers ps) {
            super(spf, ps, MediaType.TEXT_XML_TYPE);
        }
    }
    
    @Produces({ "*/*" })
    @Consumes({ "*/*" })
    public static final class General extends XMLRootElementProvider
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
