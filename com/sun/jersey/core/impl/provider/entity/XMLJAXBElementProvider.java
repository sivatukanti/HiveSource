// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.impl.provider.entity;

import javax.ws.rs.core.Context;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import java.io.OutputStream;
import javax.xml.bind.Marshaller;
import java.nio.charset.Charset;
import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import com.sun.jersey.core.provider.jaxb.AbstractJAXBProvider;
import javax.xml.bind.JAXBElement;
import java.io.InputStream;
import javax.xml.bind.Unmarshaller;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Providers;
import javax.xml.parsers.SAXParserFactory;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.core.provider.jaxb.AbstractJAXBElementProvider;

public class XMLJAXBElementProvider extends AbstractJAXBElementProvider
{
    private final Injectable<SAXParserFactory> spf;
    
    public XMLJAXBElementProvider(final Injectable<SAXParserFactory> spf, final Providers ps) {
        super(ps);
        this.spf = spf;
    }
    
    public XMLJAXBElementProvider(final Injectable<SAXParserFactory> spf, final Providers ps, final MediaType mt) {
        super(ps, mt);
        this.spf = spf;
    }
    
    @Override
    protected final JAXBElement<?> readFrom(final Class<?> type, final MediaType mediaType, final Unmarshaller u, final InputStream entityStream) throws JAXBException {
        return u.unmarshal(AbstractJAXBProvider.getSAXSource(this.spf.getValue(), entityStream), type);
    }
    
    @Override
    protected final void writeTo(final JAXBElement<?> t, final MediaType mediaType, final Charset c, final Marshaller m, final OutputStream entityStream) throws JAXBException {
        m.marshal(t, entityStream);
    }
    
    @Produces({ "application/xml" })
    @Consumes({ "application/xml" })
    public static final class App extends XMLJAXBElementProvider
    {
        public App(@Context final Injectable<SAXParserFactory> spf, @Context final Providers ps) {
            super(spf, ps, MediaType.APPLICATION_XML_TYPE);
        }
    }
    
    @Produces({ "text/xml" })
    @Consumes({ "text/xml" })
    public static final class Text extends XMLJAXBElementProvider
    {
        public Text(@Context final Injectable<SAXParserFactory> spf, @Context final Providers ps) {
            super(spf, ps, MediaType.TEXT_XML_TYPE);
        }
    }
    
    @Produces({ "*/*" })
    @Consumes({ "*/*" })
    public static final class General extends XMLJAXBElementProvider
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
