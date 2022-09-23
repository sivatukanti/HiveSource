// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.util;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;

public final class SaxHelper
{
    private SaxHelper() {
    }
    
    public static boolean isXdkParserFactory(final SAXParserFactory parserFactory) {
        return isXdkFactory(parserFactory, "oracle.xml.jaxp.JXSAXParserFactory");
    }
    
    public static boolean isXdkDocumentBuilderFactory(final DocumentBuilderFactory builderFactory) {
        return isXdkFactory(builderFactory, "oracle.xml.jaxp.JXDocumentBuilderFactory");
    }
    
    private static boolean isXdkFactory(final Object factory, final String className) {
        try {
            final Class<?> xdkFactoryClass = Class.forName(className);
            return xdkFactoryClass.isAssignableFrom(factory.getClass());
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }
}
