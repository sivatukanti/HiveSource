// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jackson.jaxrs;

import org.codehaus.jackson.map.ObjectMapper;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.ext.Provider;

@Provider
@Consumes({ "application/json", "text/json" })
@Produces({ "application/json", "text/json" })
public class JacksonJaxbJsonProvider extends JacksonJsonProvider
{
    public static final Annotations[] DEFAULT_ANNOTATIONS;
    
    public JacksonJaxbJsonProvider() {
        this(null, JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS);
    }
    
    public JacksonJaxbJsonProvider(final Annotations... annotationsToUse) {
        this(null, annotationsToUse);
    }
    
    public JacksonJaxbJsonProvider(final ObjectMapper mapper, final Annotations[] annotationsToUse) {
        super(mapper, annotationsToUse);
    }
    
    static {
        DEFAULT_ANNOTATIONS = new Annotations[] { Annotations.JACKSON, Annotations.JAXB };
    }
}
