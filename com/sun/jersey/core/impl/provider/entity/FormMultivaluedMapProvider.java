// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.impl.provider.entity;

import javax.ws.rs.WebApplicationException;
import java.io.OutputStream;
import java.io.IOException;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.io.InputStream;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MultivaluedMap;

@Produces({ "application/x-www-form-urlencoded" })
@Consumes({ "application/x-www-form-urlencoded" })
public final class FormMultivaluedMapProvider extends BaseFormProvider<MultivaluedMap<String, String>>
{
    private final Type mapType;
    
    public FormMultivaluedMapProvider() {
        final ParameterizedType iface = (ParameterizedType)this.getClass().getGenericSuperclass();
        this.mapType = iface.getActualTypeArguments()[0];
    }
    
    @Override
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return type == MultivaluedMap.class && (type == genericType || this.mapType.equals(genericType));
    }
    
    @Override
    public MultivaluedMap<String, String> readFrom(final Class<MultivaluedMap<String, String>> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws IOException {
        return ((BaseFormProvider<MultivaluedMapImpl>)this).readFrom(new MultivaluedMapImpl(), mediaType, entityStream);
    }
    
    @Override
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return MultivaluedMap.class.isAssignableFrom(type);
    }
    
    @Override
    public void writeTo(final MultivaluedMap<String, String> t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException {
        this.writeTo(t, mediaType, entityStream);
    }
}
