// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.json;

import java.util.Iterator;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import javax.xml.bind.Validator;
import com.sun.jersey.json.impl.JSONMarshallerImpl;
import com.sun.jersey.json.impl.JSONUnmarshallerImpl;
import com.sun.jersey.json.impl.BaseJSONUnmarshaller;
import javax.xml.bind.Unmarshaller;
import com.sun.jersey.json.impl.BaseJSONMarshaller;
import javax.xml.bind.Marshaller;
import com.sun.jersey.json.impl.JSONHelper;
import java.util.Collections;
import javax.xml.bind.JAXBException;
import java.util.Map;
import javax.xml.bind.JAXBContext;

public final class JSONJAXBContext extends JAXBContext implements JSONConfigurated
{
    @Deprecated
    public static final String NAMESPACE = "com.sun.jersey.impl.json.";
    @Deprecated
    public static final String JSON_NOTATION = "com.sun.jersey.impl.json.notation";
    @Deprecated
    public static final String JSON_ENABLED = "com.sun.jersey.impl.json.enabled";
    @Deprecated
    public static final String JSON_ROOT_UNWRAPPING = "com.sun.jersey.impl.json.root.unwrapping";
    @Deprecated
    public static final String JSON_ARRAYS = "com.sun.jersey.impl.json.arrays";
    @Deprecated
    public static final String JSON_NON_STRINGS = "com.sun.jersey.impl.json.non.strings";
    @Deprecated
    public static final String JSON_ATTRS_AS_ELEMS = "com.sun.jersey.impl.json.attrs.as.elems";
    @Deprecated
    public static final String JSON_XML2JSON_NS = "com.sun.jersey.impl.json.xml.to.json.ns";
    private static final Map<String, Object> defaultJsonProperties;
    private JSONConfiguration jsonConfiguration;
    private final JAXBContext jaxbContext;
    static final Map<String, JSONConfiguration.Notation> _notationMap;
    
    public JSONJAXBContext(final Class... classesToBeBound) throws JAXBException {
        this(JSONConfiguration.DEFAULT, classesToBeBound);
    }
    
    public JSONJAXBContext(final JSONConfiguration config, final Class... classesToBeBound) throws JAXBException {
        if (config == null) {
            throw new IllegalArgumentException("JSONConfiguration MUST not be null");
        }
        this.jsonConfiguration = config;
        if (config.getNotation() == JSONConfiguration.Notation.NATURAL) {
            this.jaxbContext = JAXBContext.newInstance(classesToBeBound, JSONHelper.createPropertiesForJaxbContext(Collections.emptyMap()));
        }
        else {
            this.jaxbContext = JAXBContext.newInstance(classesToBeBound);
        }
    }
    
    public JSONJAXBContext(final Class[] classesToBeBound, final Map<String, Object> properties) throws JAXBException {
        this.jaxbContext = JAXBContext.newInstance(classesToBeBound, this.createProperties(properties));
        if (this.jsonConfiguration == null) {
            this.jsonConfiguration = JSONConfiguration.DEFAULT;
        }
    }
    
    public JSONJAXBContext(final JSONConfiguration config, final Class[] classesToBeBound, final Map<String, Object> properties) throws JAXBException {
        if (config == null) {
            throw new IllegalArgumentException("JSONConfiguration MUST not be null");
        }
        this.jsonConfiguration = config;
        if (config.getNotation() == JSONConfiguration.Notation.NATURAL) {
            final Map<String, Object> myProps = JSONHelper.createPropertiesForJaxbContext(properties);
            this.jaxbContext = JAXBContext.newInstance(classesToBeBound, myProps);
        }
        else {
            this.jaxbContext = JAXBContext.newInstance(classesToBeBound, properties);
        }
    }
    
    public JSONJAXBContext(final String contextPath) throws JAXBException {
        this(JSONConfiguration.DEFAULT, contextPath);
    }
    
    public JSONJAXBContext(final JSONConfiguration config, final String contextPath) throws JAXBException {
        if (config == null) {
            throw new IllegalArgumentException("JSONConfiguration MUST not be null");
        }
        if (config.getNotation() == JSONConfiguration.Notation.NATURAL) {
            this.jaxbContext = JAXBContext.newInstance(contextPath, Thread.currentThread().getContextClassLoader(), this.createProperties(JSONHelper.createPropertiesForJaxbContext(Collections.emptyMap())));
        }
        else {
            this.jaxbContext = JAXBContext.newInstance(contextPath, Thread.currentThread().getContextClassLoader());
        }
        this.jsonConfiguration = config;
    }
    
    public JSONJAXBContext(final String contextPath, final ClassLoader classLoader) throws JAXBException {
        this.jaxbContext = JAXBContext.newInstance(contextPath, classLoader);
        this.jsonConfiguration = JSONConfiguration.DEFAULT;
    }
    
    public JSONJAXBContext(final String contextPath, final ClassLoader classLoader, final Map<String, Object> properties) throws JAXBException {
        this.jaxbContext = JAXBContext.newInstance(contextPath, classLoader, this.createProperties(properties));
        if (this.jsonConfiguration == null) {
            this.jsonConfiguration = JSONConfiguration.DEFAULT;
        }
    }
    
    public JSONJAXBContext(final JSONConfiguration config, final String contextPath, final ClassLoader classLoader, final Map<String, Object> properties) throws JAXBException {
        if (config == null) {
            throw new IllegalArgumentException("JSONConfiguration MUST not be null");
        }
        if (config.getNotation() == JSONConfiguration.Notation.NATURAL) {
            final Map<String, Object> myProps = JSONHelper.createPropertiesForJaxbContext(properties);
            this.jaxbContext = JAXBContext.newInstance(contextPath, classLoader, myProps);
        }
        else {
            this.jaxbContext = JAXBContext.newInstance(contextPath, classLoader, properties);
        }
        this.jsonConfiguration = config;
    }
    
    public static JSONMarshaller getJSONMarshaller(final Marshaller marshaller, final JAXBContext jaxbContext) {
        if (marshaller instanceof JSONMarshaller) {
            return (JSONMarshaller)marshaller;
        }
        return new BaseJSONMarshaller(marshaller, jaxbContext, JSONConfiguration.DEFAULT);
    }
    
    public static JSONUnmarshaller getJSONUnmarshaller(final Unmarshaller unmarshaller, final JAXBContext jaxbContext) {
        if (unmarshaller instanceof JSONUnmarshaller) {
            return (JSONUnmarshaller)unmarshaller;
        }
        return new BaseJSONUnmarshaller(unmarshaller, jaxbContext, JSONConfiguration.DEFAULT);
    }
    
    @Override
    public JSONConfiguration getJSONConfiguration() {
        return this.jsonConfiguration;
    }
    
    public JSONUnmarshaller createJSONUnmarshaller() throws JAXBException {
        return new JSONUnmarshallerImpl(this, this.getJSONConfiguration());
    }
    
    public JSONMarshaller createJSONMarshaller() throws JAXBException {
        return new JSONMarshallerImpl(this, this.getJSONConfiguration());
    }
    
    @Override
    public Unmarshaller createUnmarshaller() throws JAXBException {
        return new JSONUnmarshallerImpl(this.jaxbContext, this.getJSONConfiguration());
    }
    
    @Override
    public Marshaller createMarshaller() throws JAXBException {
        return new JSONMarshallerImpl(this.jaxbContext, this.getJSONConfiguration());
    }
    
    @Override
    public Validator createValidator() throws JAXBException {
        return this.jaxbContext.createValidator();
    }
    
    public JAXBContext getOriginalJaxbContext() {
        return this.jaxbContext;
    }
    
    private Map<String, Object> createProperties(final Map<String, Object> properties) {
        Map<String, Object> workProperties = new HashMap<String, Object>();
        workProperties.putAll(JSONJAXBContext.defaultJsonProperties);
        workProperties.putAll(properties);
        if (JSONNotation.NATURAL == workProperties.get("com.sun.jersey.impl.json.notation")) {
            workProperties = JSONHelper.createPropertiesForJaxbContext(workProperties);
        }
        this.processProperties(workProperties);
        return workProperties;
    }
    
    private void processProperties(final Map<String, Object> properties) {
        final Collection<String> jsonKeys = new HashSet<String>();
        for (final String k : Collections.unmodifiableSet((Set<?>)properties.keySet())) {
            if (k.startsWith("com.sun.jersey.impl.json.")) {
                jsonKeys.add(k);
            }
        }
        if (!jsonKeys.isEmpty() && this.jsonConfiguration == null) {
            JSONConfiguration.Notation pNotation = JSONConfiguration.Notation.MAPPED;
            if (properties.containsKey("com.sun.jersey.impl.json.notation")) {
                final Object nO = properties.get("com.sun.jersey.impl.json.notation");
                if (nO instanceof JSONNotation || nO instanceof String) {
                    pNotation = JSONJAXBContext._notationMap.get(nO.toString());
                }
            }
            this.jsonConfiguration = this.getConfiguration(pNotation, properties);
        }
        for (final String k : jsonKeys) {
            properties.remove(k);
        }
    }
    
    private JSONConfiguration getConfiguration(final JSONConfiguration.Notation pNotation, final Map<String, Object> properties) {
        final String[] a = new String[0];
        switch (pNotation) {
            case BADGERFISH: {
                return JSONConfiguration.badgerFish().build();
            }
            case MAPPED_JETTISON: {
                final JSONConfiguration.MappedJettisonBuilder mappedJettisonBuilder = JSONConfiguration.mappedJettison();
                if (properties.containsKey("com.sun.jersey.impl.json.xml.to.json.ns")) {
                    mappedJettisonBuilder.xml2JsonNs(properties.get("com.sun.jersey.impl.json.xml.to.json.ns"));
                }
                return mappedJettisonBuilder.build();
            }
            case NATURAL: {
                return JSONConfiguration.natural().build();
            }
            default: {
                final JSONConfiguration.MappedBuilder mappedBuilder = JSONConfiguration.mapped();
                if (properties.containsKey("com.sun.jersey.impl.json.arrays")) {
                    mappedBuilder.arrays((String[])properties.get("com.sun.jersey.impl.json.arrays").toArray(a));
                }
                if (properties.containsKey("com.sun.jersey.impl.json.attrs.as.elems")) {
                    mappedBuilder.attributeAsElement((String[])properties.get("com.sun.jersey.impl.json.attrs.as.elems").toArray(a));
                }
                if (properties.containsKey("com.sun.jersey.impl.json.non.strings")) {
                    mappedBuilder.nonStrings((String[])properties.get("com.sun.jersey.impl.json.non.strings").toArray(a));
                }
                if (properties.containsKey("com.sun.jersey.impl.json.root.unwrapping")) {
                    mappedBuilder.rootUnwrapping(properties.get("com.sun.jersey.impl.json.root.unwrapping"));
                }
                return mappedBuilder.build();
            }
        }
    }
    
    static {
        (defaultJsonProperties = new HashMap<String, Object>()).put("com.sun.jersey.impl.json.notation", JSONNotation.MAPPED);
        JSONJAXBContext.defaultJsonProperties.put("com.sun.jersey.impl.json.root.unwrapping", Boolean.TRUE);
        _notationMap = new HashMap<String, JSONConfiguration.Notation>() {
            {
                this.put(JSONNotation.BADGERFISH.toString(), JSONConfiguration.Notation.BADGERFISH);
                this.put(JSONNotation.MAPPED.toString(), JSONConfiguration.Notation.MAPPED);
                this.put(JSONNotation.MAPPED_JETTISON.toString(), JSONConfiguration.Notation.MAPPED_JETTISON);
                this.put(JSONNotation.NATURAL.toString(), JSONConfiguration.Notation.NATURAL);
            }
        };
    }
    
    @Deprecated
    public enum JSONNotation
    {
        @Deprecated
        MAPPED, 
        @Deprecated
        MAPPED_JETTISON, 
        @Deprecated
        BADGERFISH, 
        @Deprecated
        NATURAL;
    }
}
