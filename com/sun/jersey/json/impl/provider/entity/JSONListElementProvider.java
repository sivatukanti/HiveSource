// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl.provider.entity;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import java.io.Reader;
import com.sun.jersey.json.impl.JSONHelper;
import java.io.InputStreamReader;
import com.sun.jersey.core.provider.AbstractMessageReaderWriterProvider;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.util.Iterator;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.bind.JAXBException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.Writer;
import com.sun.jersey.json.impl.Stax2JsonFactory;
import com.sun.jersey.api.json.JSONConfigurated;
import com.sun.jersey.api.json.JSONConfiguration;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import javax.xml.bind.Marshaller;
import java.nio.charset.Charset;
import java.util.Collection;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.core.Context;
import com.sun.jersey.core.util.FeaturesAndProperties;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Providers;
import com.sun.jersey.core.provider.jaxb.AbstractListElementProvider;

public class JSONListElementProvider extends AbstractListElementProvider
{
    boolean jacksonEntityProviderTakesPrecedence;
    
    JSONListElementProvider(final Providers ps) {
        super(ps);
        this.jacksonEntityProviderTakesPrecedence = false;
    }
    
    JSONListElementProvider(final Providers ps, final MediaType mt) {
        super(ps, mt);
        this.jacksonEntityProviderTakesPrecedence = false;
    }
    
    @Context
    @Override
    public void setConfiguration(final FeaturesAndProperties fp) {
        super.setConfiguration(fp);
        this.jacksonEntityProviderTakesPrecedence = fp.getFeature("com.sun.jersey.api.json.POJOMappingFeature");
    }
    
    @Override
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return !this.jacksonEntityProviderTakesPrecedence && super.isReadable(type, genericType, annotations, mediaType);
    }
    
    @Override
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return !this.jacksonEntityProviderTakesPrecedence && super.isWriteable(type, genericType, annotations, mediaType);
    }
    
    @Override
    public final void writeList(final Class<?> elementType, final Collection<?> t, final MediaType mediaType, final Charset c, final Marshaller m, final OutputStream entityStream) throws JAXBException, IOException {
        final OutputStreamWriter osw = new OutputStreamWriter(entityStream, c);
        JSONConfiguration origJsonConfig = JSONConfiguration.DEFAULT;
        if (m instanceof JSONConfigurated) {
            origJsonConfig = ((JSONConfigurated)m).getJSONConfiguration();
        }
        final JSONConfiguration unwrappingJsonConfig = JSONConfiguration.createJSONConfigurationWithRootUnwrapping(origJsonConfig, true);
        final XMLStreamWriter jxsw = Stax2JsonFactory.createWriter(osw, unwrappingJsonConfig, elementType, this.getStoredJAXBContext(elementType), true);
        final String invisibleRootName = this.getRootElementName(elementType);
        final String elementName = this.getElementName(elementType);
        try {
            if (!origJsonConfig.isRootUnwrapping()) {
                osw.append(String.format("{\"%s\":", elementName));
                osw.flush();
            }
            jxsw.writeStartDocument();
            jxsw.writeStartElement(invisibleRootName);
            for (final Object o : t) {
                m.marshal(o, jxsw);
            }
            jxsw.writeEndElement();
            jxsw.writeEndDocument();
            jxsw.flush();
            if (!origJsonConfig.isRootUnwrapping()) {
                osw.append("}");
                osw.flush();
            }
        }
        catch (XMLStreamException ex) {
            Logger.getLogger(JSONListElementProvider.class.getName()).log(Level.SEVERE, null, ex);
            throw new JAXBException(ex.getMessage(), ex);
        }
    }
    
    @Override
    protected final XMLStreamReader getXMLStreamReader(final Class<?> elementType, final MediaType mediaType, final Unmarshaller u, final InputStream entityStream) throws XMLStreamException {
        JSONConfiguration c = JSONConfiguration.DEFAULT;
        final Charset charset = AbstractMessageReaderWriterProvider.getCharset(mediaType);
        if (u instanceof JSONConfigurated) {
            c = ((JSONConfigurated)u).getJSONConfiguration();
        }
        try {
            return Stax2JsonFactory.createReader(new InputStreamReader(entityStream, charset), c, JSONHelper.getRootElementName((Class<Object>)elementType), elementType, this.getStoredJAXBContext(elementType), true);
        }
        catch (JAXBException e) {
            throw new XMLStreamException(e);
        }
    }
    
    @Produces({ "application/json" })
    @Consumes({ "application/json" })
    public static final class App extends JSONListElementProvider
    {
        public App(@Context final Providers ps) {
            super(ps, MediaType.APPLICATION_JSON_TYPE);
        }
    }
    
    @Produces({ "*/*" })
    @Consumes({ "*/*" })
    public static final class General extends JSONListElementProvider
    {
        public General(@Context final Providers ps) {
            super(ps);
        }
        
        @Override
        protected boolean isSupported(final MediaType m) {
            return !this.jacksonEntityProviderTakesPrecedence && m.getSubtype().endsWith("+json");
        }
    }
}
