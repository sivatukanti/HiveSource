// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.provider.jaxb;

import java.util.WeakHashMap;
import javax.xml.bind.PropertyException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.sun.jersey.api.provider.jaxb.XmlHeader;
import java.lang.annotation.Annotation;
import org.xml.sax.InputSource;
import javax.xml.transform.sax.SAXSource;
import java.io.InputStream;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.bind.JAXBException;
import javax.ws.rs.core.Context;
import com.sun.jersey.core.util.FeaturesAndProperties;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBContext;
import java.lang.ref.WeakReference;
import java.util.Map;
import com.sun.jersey.core.provider.AbstractMessageReaderWriterProvider;

public abstract class AbstractJAXBProvider<T> extends AbstractMessageReaderWriterProvider<T>
{
    private static final Map<Class<?>, WeakReference<JAXBContext>> jaxbContexts;
    private final Providers ps;
    private final boolean fixedMediaType;
    private final ContextResolver<JAXBContext> mtContext;
    private final ContextResolver<Unmarshaller> mtUnmarshaller;
    private final ContextResolver<Marshaller> mtMarshaller;
    private boolean formattedOutput;
    private boolean xmlRootElementProcessing;
    
    public AbstractJAXBProvider(final Providers ps) {
        this(ps, null);
    }
    
    public AbstractJAXBProvider(final Providers ps, final MediaType mt) {
        this.formattedOutput = false;
        this.xmlRootElementProcessing = false;
        this.ps = ps;
        this.fixedMediaType = (mt != null);
        if (this.fixedMediaType) {
            this.mtContext = ps.getContextResolver(JAXBContext.class, mt);
            this.mtUnmarshaller = ps.getContextResolver(Unmarshaller.class, mt);
            this.mtMarshaller = ps.getContextResolver(Marshaller.class, mt);
        }
        else {
            this.mtContext = null;
            this.mtUnmarshaller = null;
            this.mtMarshaller = null;
        }
    }
    
    @Context
    public void setConfiguration(final FeaturesAndProperties fp) {
        this.formattedOutput = fp.getFeature("com.sun.jersey.config.feature.Formatted");
        this.xmlRootElementProcessing = fp.getFeature("com.sun.jersey.config.feature.XmlRootElementProcessing");
    }
    
    protected boolean isSupported(final MediaType m) {
        return true;
    }
    
    protected final Unmarshaller getUnmarshaller(final Class type, final MediaType mt) throws JAXBException {
        if (this.fixedMediaType) {
            return this.getUnmarshaller(type);
        }
        final ContextResolver<Unmarshaller> uncr = this.ps.getContextResolver(Unmarshaller.class, mt);
        if (uncr != null) {
            final Unmarshaller u = uncr.getContext(type);
            if (u != null) {
                return u;
            }
        }
        return this.getJAXBContext(type, mt).createUnmarshaller();
    }
    
    private Unmarshaller getUnmarshaller(final Class type) throws JAXBException {
        if (this.mtUnmarshaller != null) {
            final Unmarshaller u = this.mtUnmarshaller.getContext(type);
            if (u != null) {
                return u;
            }
        }
        return this.getJAXBContext(type).createUnmarshaller();
    }
    
    protected final Marshaller getMarshaller(final Class type, final MediaType mt) throws JAXBException {
        if (this.fixedMediaType) {
            return this.getMarshaller(type);
        }
        final ContextResolver<Marshaller> mcr = this.ps.getContextResolver(Marshaller.class, mt);
        if (mcr != null) {
            final Marshaller m = mcr.getContext(type);
            if (m != null) {
                return m;
            }
        }
        final Marshaller m = this.getJAXBContext(type, mt).createMarshaller();
        if (this.formattedOutput) {
            m.setProperty("jaxb.formatted.output", this.formattedOutput);
        }
        return m;
    }
    
    private Marshaller getMarshaller(final Class type) throws JAXBException {
        if (this.mtMarshaller != null) {
            final Marshaller u = this.mtMarshaller.getContext(type);
            if (u != null) {
                return u;
            }
        }
        final Marshaller m = this.getJAXBContext(type).createMarshaller();
        if (this.formattedOutput) {
            m.setProperty("jaxb.formatted.output", this.formattedOutput);
        }
        return m;
    }
    
    private JAXBContext getJAXBContext(final Class type, final MediaType mt) throws JAXBException {
        final ContextResolver<JAXBContext> cr = this.ps.getContextResolver(JAXBContext.class, mt);
        if (cr != null) {
            final JAXBContext c = cr.getContext(type);
            if (c != null) {
                return c;
            }
        }
        return this.getStoredJAXBContext(type);
    }
    
    protected JAXBContext getJAXBContext(final Class type) throws JAXBException {
        if (this.mtContext != null) {
            final JAXBContext c = this.mtContext.getContext(type);
            if (c != null) {
                return c;
            }
        }
        return this.getStoredJAXBContext(type);
    }
    
    protected JAXBContext getStoredJAXBContext(final Class type) throws JAXBException {
        synchronized (AbstractJAXBProvider.jaxbContexts) {
            final WeakReference<JAXBContext> ref = AbstractJAXBProvider.jaxbContexts.get(type);
            JAXBContext c = (ref != null) ? ref.get() : null;
            if (c == null) {
                c = JAXBContext.newInstance(type);
                AbstractJAXBProvider.jaxbContexts.put(type, new WeakReference<JAXBContext>(c));
            }
            return c;
        }
    }
    
    protected static SAXSource getSAXSource(final SAXParserFactory spf, final InputStream entityStream) throws JAXBException {
        try {
            return new SAXSource(spf.newSAXParser().getXMLReader(), new InputSource(entityStream));
        }
        catch (Exception ex) {
            throw new JAXBException("Error creating SAXSource", ex);
        }
    }
    
    protected boolean isFormattedOutput() {
        return this.formattedOutput;
    }
    
    protected boolean isXmlRootElementProcessing() {
        return this.xmlRootElementProcessing;
    }
    
    protected void setHeader(final Marshaller m, final Annotation[] annotations) throws PropertyException {
        for (final Annotation a : annotations) {
            if (a instanceof XmlHeader) {
                try {
                    m.setProperty("com.sun.xml.bind.xmlHeaders", ((XmlHeader)a).value());
                }
                catch (PropertyException e) {
                    try {
                        m.setProperty("com.sun.xml.internal.bind.xmlHeaders", ((XmlHeader)a).value());
                    }
                    catch (PropertyException ex) {
                        Logger.getLogger(AbstractJAXBProvider.class.getName()).log(Level.WARNING, "@XmlHeader annotation is not supported with this JAXB implementation. Please use JAXB RI if you need this feature.");
                    }
                }
                break;
            }
        }
    }
    
    static {
        jaxbContexts = new WeakHashMap<Class<?>, WeakReference<JAXBContext>>();
    }
}
