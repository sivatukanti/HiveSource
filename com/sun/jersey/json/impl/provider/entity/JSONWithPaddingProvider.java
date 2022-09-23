// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl.provider.entity;

import javax.ws.rs.ext.MessageBodyWriter;
import com.sun.jersey.json.impl.ImplMessages;
import javax.ws.rs.core.GenericEntity;
import java.io.OutputStream;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.io.InputStream;
import javax.ws.rs.core.MultivaluedMap;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.util.HashMap;
import javax.ws.rs.core.Context;
import com.sun.jersey.spi.MessageBodyWorkers;
import java.util.Set;
import java.util.Map;
import java.util.logging.Logger;
import com.sun.jersey.api.json.JSONWithPadding;
import com.sun.jersey.core.provider.AbstractMessageReaderWriterProvider;

public class JSONWithPaddingProvider extends AbstractMessageReaderWriterProvider<JSONWithPadding>
{
    private static final Logger LOGGER;
    private final Map<String, Set<String>> javascriptTypes;
    @Context
    MessageBodyWorkers bodyWorker;
    
    public JSONWithPaddingProvider() {
        (this.javascriptTypes = new HashMap<String, Set<String>>()).put("application", new HashSet<String>(Arrays.asList("x-javascript", "ecmascript", "javascript")));
        this.javascriptTypes.put("text", new HashSet<String>(Arrays.asList("ecmascript", "jscript")));
    }
    
    private boolean isJavascript(final MediaType m) {
        final Set<String> subtypes = this.javascriptTypes.get(m.getType());
        return subtypes != null && subtypes.contains(m.getSubtype());
    }
    
    @Override
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return false;
    }
    
    @Override
    public JSONWithPadding readFrom(final Class<JSONWithPadding> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws IOException, WebApplicationException {
        throw new UnsupportedOperationException("Not supported by design.");
    }
    
    @Override
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return type == JSONWithPadding.class;
    }
    
    @Override
    public void writeTo(final JSONWithPadding t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException, WebApplicationException {
        Object jsonEntity = t.getJsonSource();
        Type entityGenericType = jsonEntity.getClass();
        Class<?> entityType = jsonEntity.getClass();
        final boolean genericEntityUsed = jsonEntity instanceof GenericEntity;
        if (genericEntityUsed) {
            final GenericEntity ge = (GenericEntity)jsonEntity;
            jsonEntity = ge.getEntity();
            entityGenericType = ge.getType();
            entityType = (Class<?>)ge.getRawType();
        }
        final boolean isJavaScript = this.isJavascript(mediaType);
        final MediaType workerMediaType = isJavaScript ? MediaType.APPLICATION_JSON_TYPE : mediaType;
        final MessageBodyWriter bw = this.bodyWorker.getMessageBodyWriter(entityType, entityGenericType, annotations, workerMediaType);
        if (bw == null) {
            if (!genericEntityUsed) {
                JSONWithPaddingProvider.LOGGER.severe(ImplMessages.ERROR_NONGE_JSONP_MSG_BODY_WRITER_NOT_FOUND(jsonEntity, workerMediaType));
            }
            else {
                JSONWithPaddingProvider.LOGGER.severe(ImplMessages.ERROR_JSONP_MSG_BODY_WRITER_NOT_FOUND(jsonEntity, workerMediaType));
            }
            throw new WebApplicationException(500);
        }
        if (isJavaScript) {
            entityStream.write(t.getCallbackName().getBytes());
            entityStream.write(40);
        }
        bw.writeTo(jsonEntity, entityType, entityGenericType, annotations, workerMediaType, httpHeaders, entityStream);
        if (isJavaScript) {
            entityStream.write(41);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(JSONWithPaddingProvider.class.getName());
    }
}
