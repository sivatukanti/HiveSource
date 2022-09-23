// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.annotation.XmlRootElement;

public final class JSONHelper
{
    private static JaxbProvider jaxbProvider;
    
    private JSONHelper() {
    }
    
    public static String getRootElementName(final Class<Object> clazz) {
        final XmlRootElement e = clazz.getAnnotation(XmlRootElement.class);
        if (e == null) {
            return getVariableName(clazz.getSimpleName());
        }
        if ("##default".equals(e.name())) {
            return getVariableName(clazz.getSimpleName());
        }
        return e.name();
    }
    
    private static String getVariableName(final String baseName) {
        return NameUtil.toMixedCaseName(NameUtil.toWordList(baseName), false);
    }
    
    public static JaxbProvider getJaxbProvider(final JAXBContext jaxbContext) {
        for (final SupportedJaxbProvider provider : SupportedJaxbProvider.values()) {
            try {
                final Class<?> jaxbContextClass = getJaxbContextClass(jaxbContext);
                Class<?> clazz = null;
                if (SupportedJaxbProvider.JAXB_JDK.equals(provider)) {
                    clazz = ClassLoader.getSystemClassLoader().loadClass(SupportedJaxbProvider.JAXB_JDK.getJaxbContextClassName());
                }
                else {
                    clazz = Class.forName(provider.getJaxbContextClassName());
                }
                if (clazz.isAssignableFrom(jaxbContextClass)) {
                    return JSONHelper.jaxbProvider = provider;
                }
            }
            catch (ClassNotFoundException ex) {}
        }
        throw new IllegalStateException("No JAXB provider found for the following JAXB context: " + ((jaxbContext == null) ? null : jaxbContext.getClass()));
    }
    
    private static Class<?> getJaxbContextClass(final JAXBContext jaxbContext) throws ClassNotFoundException {
        if (jaxbContext != null) {
            return jaxbContext.getClass();
        }
        return ClassLoader.getSystemClassLoader().loadClass(SupportedJaxbProvider.JAXB_JDK.getJaxbContextClassName());
    }
    
    public static boolean isNaturalNotationEnabled() {
        try {
            Class.forName("com.sun.xml.bind.annotation.OverrideAnnotationOf");
            return true;
        }
        catch (ClassNotFoundException e) {
            if (JSONHelper.jaxbProvider == SupportedJaxbProvider.JAXB_RI) {
                return false;
            }
            try {
                ClassLoader.getSystemClassLoader().loadClass("com.sun.xml.internal.bind.annotation.OverrideAnnotationOf");
                return true;
            }
            catch (ClassNotFoundException e) {
                return JSONHelper.jaxbProvider != SupportedJaxbProvider.JAXB_JDK && (JSONHelper.jaxbProvider == null || JSONHelper.jaxbProvider == SupportedJaxbProvider.MOXY);
            }
        }
    }
    
    public static Map<String, Object> createPropertiesForJaxbContext(final Map<String, Object> properties) {
        final Map<String, Object> jaxbProperties = new HashMap<String, Object>(properties.size() + 1);
        final String retainReferenceToInfo = "retainReferenceToInfo";
        jaxbProperties.putAll(properties);
        jaxbProperties.put("retainReferenceToInfo", Boolean.TRUE);
        return jaxbProperties;
    }
}
