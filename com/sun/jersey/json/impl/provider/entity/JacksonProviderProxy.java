// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl.provider.entity;

import java.util.List;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Injectable;
import java.io.OutputStream;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.io.InputStream;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import com.sun.jersey.spi.inject.InjectableProviderContext;
import com.sun.jersey.core.spi.component.ComponentInjector;
import javax.ws.rs.ext.Providers;
import javax.ws.rs.core.Context;
import com.sun.jersey.core.util.FeaturesAndProperties;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.MessageBodyReader;

public class JacksonProviderProxy implements MessageBodyReader<Object>, MessageBodyWriter<Object>
{
    JacksonJsonProvider pojoProvider;
    JacksonJaxbJsonProvider jaxbProvider;
    boolean jacksonEntityProviderFeatureSet;
    
    public JacksonProviderProxy() {
        this.pojoProvider = new JacksonJsonProvider();
        this.jaxbProvider = new JacksonJaxbJsonProvider();
        this.jacksonEntityProviderFeatureSet = false;
    }
    
    @Context
    public void setFeaturesAndProperties(final FeaturesAndProperties fp) {
        this.jacksonEntityProviderFeatureSet = fp.getFeature("com.sun.jersey.api.json.POJOMappingFeature");
    }
    
    @Context
    public void setProviders(final Providers p) {
        new ComponentInjector<JacksonJsonProvider>(new ProvidersInjectableProviderContext(p), JacksonJsonProvider.class).inject(this.pojoProvider);
        new ComponentInjector<JacksonJaxbJsonProvider>(new ProvidersInjectableProviderContext(p), JacksonJaxbJsonProvider.class).inject(this.jaxbProvider);
    }
    
    @Override
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return this.jacksonEntityProviderFeatureSet && (this.jaxbProvider.isReadable(type, genericType, annotations, mediaType) || this.pojoProvider.isReadable(type, genericType, annotations, mediaType));
    }
    
    @Override
    public Object readFrom(final Class<Object> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws IOException, WebApplicationException {
        return this.jaxbProvider.isReadable(type, genericType, annotations, mediaType) ? this.jaxbProvider.readFrom(type, genericType, annotations, mediaType, httpHeaders, entityStream) : this.pojoProvider.readFrom(type, genericType, annotations, mediaType, httpHeaders, entityStream);
    }
    
    @Override
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return this.jacksonEntityProviderFeatureSet && (this.jaxbProvider.isWriteable(type, genericType, annotations, mediaType) || this.pojoProvider.isWriteable(type, genericType, annotations, mediaType));
    }
    
    @Override
    public long getSize(final Object t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return this.jaxbProvider.isWriteable(type, genericType, annotations, mediaType) ? this.jaxbProvider.getSize(t, type, genericType, annotations, mediaType) : this.pojoProvider.getSize(t, type, genericType, annotations, mediaType);
    }
    
    @Override
    public void writeTo(final Object t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException, WebApplicationException {
        if (this.jaxbProvider.isWriteable(type, genericType, annotations, mediaType)) {
            this.jaxbProvider.writeTo(t, type, genericType, annotations, mediaType, httpHeaders, entityStream);
        }
        else {
            this.pojoProvider.writeTo(t, type, genericType, annotations, mediaType, httpHeaders, entityStream);
        }
    }
    
    private static class ProvidersInjectableProviderContext implements InjectableProviderContext
    {
        final Providers p;
        final Injectable i;
        
        private ProvidersInjectableProviderContext(final Providers p) {
            this.p = p;
            this.i = new Injectable() {
                @Override
                public Object getValue() {
                    return p;
                }
            };
        }
        
        @Override
        public boolean isAnnotationRegistered(final Class<? extends Annotation> ac, final Class<?> cc) {
            return ac == Context.class;
        }
        
        @Override
        public boolean isInjectableProviderRegistered(final Class<? extends Annotation> ac, final Class<?> cc, final ComponentScope s) {
            return this.isAnnotationRegistered(ac, cc);
        }
        
        @Override
        public <A extends Annotation, C> Injectable getInjectable(final Class<? extends Annotation> ac, final ComponentContext ic, final A a, final C c, final ComponentScope s) {
            return (c == Providers.class) ? this.i : null;
        }
        
        @Override
        public <A extends Annotation, C> Injectable getInjectable(final Class<? extends Annotation> ac, final ComponentContext ic, final A a, final C c, final List<ComponentScope> ls) {
            return (c == Providers.class) ? this.i : null;
        }
        
        @Override
        public <A extends Annotation, C> InjectableScopePair getInjectableWithScope(final Class<? extends Annotation> ac, final ComponentContext ic, final A a, final C c, final List<ComponentScope> ls) {
            return (c == Providers.class) ? new InjectableScopePair(this.i, ls.get(0)) : null;
        }
    }
}
