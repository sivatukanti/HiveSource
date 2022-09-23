// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.webapp;

import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import javax.ws.rs.core.MediaType;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;
import javax.ws.rs.ext.Provider;
import com.google.inject.Singleton;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;

@Singleton
@Provider
@InterfaceStability.Unstable
@InterfaceAudience.Private
public class YarnJacksonJaxbJsonProvider extends JacksonJaxbJsonProvider
{
    @Override
    public ObjectMapper locateMapper(final Class<?> type, final MediaType mediaType) {
        final ObjectMapper mapper = super.locateMapper(type, mediaType);
        configObjectMapper(mapper);
        return mapper;
    }
    
    public static void configObjectMapper(final ObjectMapper mapper) {
        final AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
        mapper.setAnnotationIntrospector(introspector);
        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
    }
}
