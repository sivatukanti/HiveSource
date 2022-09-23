// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.model.parameter.multivalued;

import java.security.AccessController;
import com.sun.jersey.api.container.ContainerException;
import javax.xml.bind.UnmarshalException;
import com.sun.jersey.impl.ImplMessages;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import java.io.Reader;
import org.xml.sax.InputSource;
import java.security.PrivilegedAction;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlRootElement;
import com.sun.jersey.spi.StringReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.core.Context;
import javax.xml.parsers.SAXParserFactory;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.StringReaderProvider;
import java.util.WeakHashMap;
import javax.xml.bind.JAXBException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.Unmarshaller;
import javax.ws.rs.ext.ContextResolver;
import javax.xml.bind.JAXBContext;
import java.util.Map;

public class JAXBStringReaderProviders
{
    private static final Map<Class, JAXBContext> jaxbContexts;
    private final ContextResolver<JAXBContext> context;
    private final ContextResolver<Unmarshaller> unmarshaller;
    
    public JAXBStringReaderProviders(final Providers ps) {
        this.context = ps.getContextResolver(JAXBContext.class, null);
        this.unmarshaller = ps.getContextResolver(Unmarshaller.class, null);
    }
    
    protected final Unmarshaller getUnmarshaller(final Class type) throws JAXBException {
        if (this.unmarshaller != null) {
            final Unmarshaller u = this.unmarshaller.getContext(type);
            if (u != null) {
                return u;
            }
        }
        return this.getJAXBContext(type).createUnmarshaller();
    }
    
    private final JAXBContext getJAXBContext(final Class type) throws JAXBException {
        if (this.context != null) {
            final JAXBContext c = this.context.getContext(type);
            if (c != null) {
                return c;
            }
        }
        return this.getStoredJAXBContext(type);
    }
    
    protected JAXBContext getStoredJAXBContext(final Class type) throws JAXBException {
        synchronized (JAXBStringReaderProviders.jaxbContexts) {
            JAXBContext c = JAXBStringReaderProviders.jaxbContexts.get(type);
            if (c == null) {
                c = JAXBContext.newInstance(type);
                JAXBStringReaderProviders.jaxbContexts.put(type, c);
            }
            return c;
        }
    }
    
    static {
        jaxbContexts = new WeakHashMap<Class, JAXBContext>();
    }
    
    public static class RootElementProvider extends JAXBStringReaderProviders implements StringReaderProvider
    {
        private final Injectable<SAXParserFactory> spf;
        
        public RootElementProvider(@Context final Injectable<SAXParserFactory> spf, @Context final Providers ps) {
            super(ps);
            this.spf = spf;
        }
        
        @Override
        public StringReader getStringReader(final Class type, final Type genericType, final Annotation[] annotations) {
            final boolean supported = type.getAnnotation(XmlRootElement.class) != null || type.getAnnotation(XmlType.class) != null;
            if (!supported) {
                return null;
            }
            return new StringReader() {
                @Override
                public Object fromString(final String value) {
                    return AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                        @Override
                        public Object run() {
                            try {
                                final SAXSource source = new SAXSource(RootElementProvider.this.spf.getValue().newSAXParser().getXMLReader(), new InputSource(new java.io.StringReader(value)));
                                final Unmarshaller u = RootElementProvider.this.getUnmarshaller(type);
                                if (type.isAnnotationPresent(XmlRootElement.class)) {
                                    return u.unmarshal(source);
                                }
                                return u.unmarshal(source, (Class<Object>)type).getValue();
                            }
                            catch (UnmarshalException ex) {
                                throw new ExtractorContainerException(ImplMessages.ERROR_UNMARSHALLING_JAXB(type), ex);
                            }
                            catch (JAXBException ex2) {
                                throw new ContainerException(ImplMessages.ERROR_UNMARSHALLING_JAXB(type), ex2);
                            }
                            catch (Exception ex3) {
                                throw new ContainerException(ImplMessages.ERROR_UNMARSHALLING_JAXB(type), ex3);
                            }
                        }
                    });
                }
            };
        }
    }
}
