// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jackson.jaxrs;

import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;
import org.codehaus.jackson.map.introspect.JacksonAnnotationIntrospector;
import java.util.ArrayList;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;

public class MapperConfigurator
{
    protected ObjectMapper _mapper;
    protected ObjectMapper _defaultMapper;
    protected Annotations[] _defaultAnnotationsToUse;
    protected Class<? extends AnnotationIntrospector> _jaxbIntrospectorClass;
    
    public MapperConfigurator(final ObjectMapper mapper, final Annotations[] defAnnotations) {
        this._mapper = mapper;
        this._defaultAnnotationsToUse = defAnnotations;
    }
    
    public synchronized ObjectMapper getConfiguredMapper() {
        return this._mapper;
    }
    
    public synchronized ObjectMapper getDefaultMapper() {
        if (this._defaultMapper == null) {
            this._setAnnotations(this._defaultMapper = new ObjectMapper(), this._defaultAnnotationsToUse);
        }
        return this._defaultMapper;
    }
    
    public synchronized void setMapper(final ObjectMapper m) {
        this._mapper = m;
    }
    
    public synchronized void setAnnotationsToUse(final Annotations[] annotationsToUse) {
        this._setAnnotations(this.mapper(), annotationsToUse);
    }
    
    public synchronized void configure(final DeserializationConfig.Feature f, final boolean state) {
        this.mapper().configure(f, state);
    }
    
    public synchronized void configure(final SerializationConfig.Feature f, final boolean state) {
        this.mapper().configure(f, state);
    }
    
    public synchronized void configure(final JsonParser.Feature f, final boolean state) {
        this.mapper().configure(f, state);
    }
    
    public synchronized void configure(final JsonGenerator.Feature f, final boolean state) {
        this.mapper().configure(f, state);
    }
    
    protected ObjectMapper mapper() {
        if (this._mapper == null) {
            this._setAnnotations(this._mapper = new ObjectMapper(), this._defaultAnnotationsToUse);
        }
        return this._mapper;
    }
    
    protected void _setAnnotations(final ObjectMapper mapper, final Annotations[] annotationsToUse) {
        AnnotationIntrospector intr;
        if (annotationsToUse == null || annotationsToUse.length == 0) {
            intr = AnnotationIntrospector.nopInstance();
        }
        else {
            intr = this._resolveIntrospectors(annotationsToUse);
        }
        mapper.getDeserializationConfig().setAnnotationIntrospector(intr);
        mapper.getSerializationConfig().setAnnotationIntrospector(intr);
    }
    
    protected AnnotationIntrospector _resolveIntrospectors(final Annotations[] annotationsToUse) {
        final ArrayList<AnnotationIntrospector> intr = new ArrayList<AnnotationIntrospector>();
        for (final Annotations a : annotationsToUse) {
            if (a != null) {
                intr.add(this._resolveIntrospector(a));
            }
        }
        final int count = intr.size();
        if (count == 0) {
            return AnnotationIntrospector.nopInstance();
        }
        AnnotationIntrospector curr = intr.get(0);
        for (int i = 1, len = intr.size(); i < len; ++i) {
            curr = AnnotationIntrospector.pair(curr, intr.get(i));
        }
        return curr;
    }
    
    protected AnnotationIntrospector _resolveIntrospector(final Annotations ann) {
        switch (ann) {
            case JACKSON: {
                return new JacksonAnnotationIntrospector();
            }
            case JAXB: {
                try {
                    if (this._jaxbIntrospectorClass == null) {
                        this._jaxbIntrospectorClass = JaxbAnnotationIntrospector.class;
                    }
                    return (AnnotationIntrospector)this._jaxbIntrospectorClass.newInstance();
                }
                catch (Exception e) {
                    throw new IllegalStateException("Failed to instantiate JaxbAnnotationIntrospector: " + e.getMessage(), e);
                }
                break;
            }
        }
        throw new IllegalStateException();
    }
}
