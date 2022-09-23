// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ext;

import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.Deserializers;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.Serializers;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanDescription;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializationConfig;
import java.io.Serializable;

public class OptionalHandlerFactory implements Serializable
{
    private static final long serialVersionUID = 1L;
    private static final String PACKAGE_PREFIX_JAVAX_XML = "javax.xml.";
    private static final String SERIALIZERS_FOR_JAVAX_XML = "org.apache.htrace.shaded.fasterxml.jackson.databind.ext.CoreXMLSerializers";
    private static final String DESERIALIZERS_FOR_JAVAX_XML = "org.apache.htrace.shaded.fasterxml.jackson.databind.ext.CoreXMLDeserializers";
    private static final String CLASS_NAME_DOM_NODE = "org.w3c.dom.Node";
    private static final String CLASS_NAME_DOM_DOCUMENT = "org.w3c.dom.Node";
    private static final String SERIALIZER_FOR_DOM_NODE = "org.apache.htrace.shaded.fasterxml.jackson.databind.ext.DOMSerializer";
    private static final String DESERIALIZER_FOR_DOM_DOCUMENT = "org.apache.htrace.shaded.fasterxml.jackson.databind.ext.DOMDeserializer$DocumentDeserializer";
    private static final String DESERIALIZER_FOR_DOM_NODE = "org.apache.htrace.shaded.fasterxml.jackson.databind.ext.DOMDeserializer$NodeDeserializer";
    public static final OptionalHandlerFactory instance;
    
    protected OptionalHandlerFactory() {
    }
    
    public JsonSerializer<?> findSerializer(final SerializationConfig config, final JavaType type, final BeanDescription beanDesc) {
        final Class<?> rawType = type.getRawClass();
        final String className = rawType.getName();
        if (className.startsWith("javax.xml.") || this.hasSupertypeStartingWith(rawType, "javax.xml.")) {
            final String factoryName = "org.apache.htrace.shaded.fasterxml.jackson.databind.ext.CoreXMLSerializers";
            final Object ob = this.instantiate(factoryName);
            if (ob == null) {
                return null;
            }
            return ((Serializers)ob).findSerializer(config, type, beanDesc);
        }
        else {
            if (this.doesImplement(rawType, "org.w3c.dom.Node")) {
                return (JsonSerializer<?>)this.instantiate("org.apache.htrace.shaded.fasterxml.jackson.databind.ext.DOMSerializer");
            }
            return null;
        }
    }
    
    public JsonDeserializer<?> findDeserializer(final JavaType type, final DeserializationConfig config, final BeanDescription beanDesc) throws JsonMappingException {
        final Class<?> rawType = type.getRawClass();
        final String className = rawType.getName();
        if (className.startsWith("javax.xml.") || this.hasSupertypeStartingWith(rawType, "javax.xml.")) {
            final String factoryName = "org.apache.htrace.shaded.fasterxml.jackson.databind.ext.CoreXMLDeserializers";
            final Object ob = this.instantiate(factoryName);
            if (ob == null) {
                return null;
            }
            return ((Deserializers)ob).findBeanDeserializer(type, config, beanDesc);
        }
        else {
            if (this.doesImplement(rawType, "org.w3c.dom.Node")) {
                return (JsonDeserializer<?>)this.instantiate("org.apache.htrace.shaded.fasterxml.jackson.databind.ext.DOMDeserializer$DocumentDeserializer");
            }
            if (this.doesImplement(rawType, "org.w3c.dom.Node")) {
                return (JsonDeserializer<?>)this.instantiate("org.apache.htrace.shaded.fasterxml.jackson.databind.ext.DOMDeserializer$NodeDeserializer");
            }
            return null;
        }
    }
    
    private Object instantiate(final String className) {
        try {
            return Class.forName(className).newInstance();
        }
        catch (LinkageError e) {}
        catch (Exception ex) {}
        return null;
    }
    
    private boolean doesImplement(final Class<?> actualType, final String classNameToImplement) {
        for (Class<?> type = actualType; type != null; type = type.getSuperclass()) {
            if (type.getName().equals(classNameToImplement)) {
                return true;
            }
            if (this.hasInterface(type, classNameToImplement)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean hasInterface(final Class<?> type, final String interfaceToImplement) {
        Class[] arr$;
        final Class<?>[] interfaces = (Class<?>[])(arr$ = type.getInterfaces());
        for (final Class<?> iface : arr$) {
            if (iface.getName().equals(interfaceToImplement)) {
                return true;
            }
        }
        arr$ = interfaces;
        for (final Class<?> iface : arr$) {
            if (this.hasInterface(iface, interfaceToImplement)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean hasSupertypeStartingWith(final Class<?> rawType, final String prefix) {
        for (Class<?> supertype = rawType.getSuperclass(); supertype != null; supertype = supertype.getSuperclass()) {
            if (supertype.getName().startsWith(prefix)) {
                return true;
            }
        }
        for (Class<?> cls = rawType; cls != null; cls = cls.getSuperclass()) {
            if (this.hasInterfaceStartingWith(cls, prefix)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean hasInterfaceStartingWith(final Class<?> type, final String prefix) {
        Class[] arr$;
        final Class<?>[] interfaces = (Class<?>[])(arr$ = type.getInterfaces());
        for (final Class<?> iface : arr$) {
            if (iface.getName().startsWith(prefix)) {
                return true;
            }
        }
        arr$ = interfaces;
        for (final Class<?> iface : arr$) {
            if (this.hasInterfaceStartingWith(iface, prefix)) {
                return true;
            }
        }
        return false;
    }
    
    static {
        instance = new OptionalHandlerFactory();
    }
}
