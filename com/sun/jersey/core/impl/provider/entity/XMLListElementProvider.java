// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.impl.provider.entity;

import javax.ws.rs.core.Context;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import java.io.IOException;
import javax.xml.bind.JAXBException;
import java.util.Iterator;
import javax.xml.bind.PropertyException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.OutputStream;
import javax.xml.bind.Marshaller;
import java.nio.charset.Charset;
import java.util.Collection;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import javax.xml.bind.Unmarshaller;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Providers;
import javax.xml.stream.XMLInputFactory;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.core.provider.jaxb.AbstractListElementProvider;

public class XMLListElementProvider extends AbstractListElementProvider
{
    private final Injectable<XMLInputFactory> xif;
    
    XMLListElementProvider(final Injectable<XMLInputFactory> xif, final Providers ps) {
        super(ps);
        this.xif = xif;
    }
    
    XMLListElementProvider(final Injectable<XMLInputFactory> xif, final Providers ps, final MediaType mt) {
        super(ps, mt);
        this.xif = xif;
    }
    
    @Override
    protected final XMLStreamReader getXMLStreamReader(final Class<?> elementType, final MediaType mediaType, final Unmarshaller u, final InputStream entityStream) throws XMLStreamException {
        return this.xif.getValue().createXMLStreamReader(entityStream);
    }
    
    @Override
    public final void writeList(final Class<?> elementType, final Collection<?> t, final MediaType mediaType, final Charset c, final Marshaller m, final OutputStream entityStream) throws JAXBException, IOException {
        final String rootElement = this.getRootElementName(elementType);
        final String cName = c.name();
        entityStream.write(String.format("<?xml version=\"1.0\" encoding=\"%s\" standalone=\"yes\"?>", cName).getBytes(cName));
        String property = "com.sun.xml.bind.xmlHeaders";
        String header;
        try {
            header = (String)m.getProperty(property);
        }
        catch (PropertyException e) {
            property = "com.sun.xml.internal.bind.xmlHeaders";
            try {
                header = (String)m.getProperty(property);
            }
            catch (PropertyException ex) {
                header = null;
                Logger.getLogger(XMLListElementProvider.class.getName()).log(Level.WARNING, "@XmlHeader annotation is not supported with this JAXB implementation. Please use JAXB RI if you need this feature.");
            }
        }
        if (header != null) {
            m.setProperty(property, "");
            entityStream.write(header.getBytes(cName));
        }
        entityStream.write(String.format("<%s>", rootElement).getBytes(cName));
        for (final Object o : t) {
            m.marshal(o, entityStream);
        }
        entityStream.write(String.format("</%s>", rootElement).getBytes(cName));
    }
    
    @Produces({ "application/xml" })
    @Consumes({ "application/xml" })
    public static final class App extends XMLListElementProvider
    {
        public App(@Context final Injectable<XMLInputFactory> xif, @Context final Providers ps) {
            super(xif, ps, MediaType.APPLICATION_XML_TYPE);
        }
    }
    
    @Produces({ "text/xml" })
    @Consumes({ "text/xml" })
    public static final class Text extends XMLListElementProvider
    {
        public Text(@Context final Injectable<XMLInputFactory> xif, @Context final Providers ps) {
            super(xif, ps, MediaType.TEXT_XML_TYPE);
        }
    }
    
    @Produces({ "*/*" })
    @Consumes({ "*/*" })
    public static final class General extends XMLListElementProvider
    {
        public General(@Context final Injectable<XMLInputFactory> xif, @Context final Providers ps) {
            super(xif, ps);
        }
        
        @Override
        protected boolean isSupported(final MediaType m) {
            return m.getSubtype().endsWith("+xml");
        }
    }
}
