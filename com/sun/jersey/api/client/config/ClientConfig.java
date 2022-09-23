// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.client.config;

import java.util.Set;
import com.sun.jersey.core.util.FeaturesAndProperties;

public interface ClientConfig extends FeaturesAndProperties
{
    public static final String PROPERTY_FOLLOW_REDIRECTS = "com.sun.jersey.client.property.followRedirects";
    public static final String PROPERTY_READ_TIMEOUT = "com.sun.jersey.client.property.readTimeout";
    public static final String PROPERTY_CONNECT_TIMEOUT = "com.sun.jersey.client.property.connectTimeout";
    public static final String PROPERTY_CHUNKED_ENCODING_SIZE = "com.sun.jersey.client.property.chunkedEncodingSize";
    public static final String PROPERTY_BUFFER_RESPONSE_ENTITY_ON_EXCEPTION = "com.sun.jersey.client.property.bufferResponseEntityOnException";
    public static final String PROPERTY_THREADPOOL_SIZE = "com.sun.jersey.client.property.threadpoolSize";
    
    Set<Class<?>> getClasses();
    
    Set<Object> getSingletons();
    
    boolean getPropertyAsFeature(final String p0);
}
