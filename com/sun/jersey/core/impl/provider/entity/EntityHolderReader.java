// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.impl.provider.entity;

import java.io.IOException;
import javax.ws.rs.WebApplicationException;
import java.io.BufferedInputStream;
import com.sun.jersey.core.util.ReaderWriter;
import java.io.InputStream;
import javax.ws.rs.core.MultivaluedMap;
import java.lang.reflect.ParameterizedType;
import com.sun.jersey.core.provider.EntityHolder;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.core.Context;
import com.sun.jersey.spi.MessageBodyWorkers;
import java.util.logging.Logger;
import javax.ws.rs.ext.MessageBodyReader;

public final class EntityHolderReader implements MessageBodyReader<Object>
{
    private static final Logger LOGGER;
    private final MessageBodyWorkers bodyWorker;
    
    public EntityHolderReader(@Context final MessageBodyWorkers bodyWorker) {
        this.bodyWorker = bodyWorker;
    }
    
    @Override
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        if (type != EntityHolder.class) {
            return false;
        }
        if (!(genericType instanceof ParameterizedType)) {
            return false;
        }
        final ParameterizedType pt = (ParameterizedType)genericType;
        final Type t = pt.getActualTypeArguments()[0];
        return t instanceof Class || t instanceof ParameterizedType;
    }
    
    @Override
    public Object readFrom(final Class<Object> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {
        if (!entityStream.markSupported()) {
            entityStream = new BufferedInputStream(entityStream, ReaderWriter.BUFFER_SIZE);
        }
        entityStream.mark(1);
        if (entityStream.read() == -1) {
            return new EntityHolder();
        }
        entityStream.reset();
        final ParameterizedType pt = (ParameterizedType)genericType;
        final Type t = pt.getActualTypeArguments()[0];
        final Class entityClass = (Class)((t instanceof Class) ? t : ((Class)((ParameterizedType)t).getRawType()));
        final Type entityGenericType = (t instanceof Class) ? entityClass : t;
        final MessageBodyReader br = this.bodyWorker.getMessageBodyReader((Class<Object>)entityClass, entityGenericType, annotations, mediaType);
        if (br == null) {
            EntityHolderReader.LOGGER.severe("A message body reader for the type, " + type + ", could not be found");
            throw new WebApplicationException();
        }
        final Object o = br.readFrom(entityClass, entityGenericType, annotations, mediaType, httpHeaders, entityStream);
        return new EntityHolder(o);
    }
    
    static {
        LOGGER = Logger.getLogger(EntityHolderReader.class.getName());
    }
}
