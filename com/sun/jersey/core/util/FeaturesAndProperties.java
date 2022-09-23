// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.util;

import java.util.Map;

public interface FeaturesAndProperties
{
    public static final String FEATURE_DISABLE_XML_SECURITY = "com.sun.jersey.config.feature.DisableXmlSecurity";
    public static final String FEATURE_FORMATTED = "com.sun.jersey.config.feature.Formatted";
    public static final String FEATURE_XMLROOTELEMENT_PROCESSING = "com.sun.jersey.config.feature.XmlRootElementProcessing";
    public static final String FEATURE_PRE_1_4_PROVIDER_PRECEDENCE = "com.sun.jersey.config.feature.Pre14ProviderPrecedence";
    
    Map<String, Boolean> getFeatures();
    
    boolean getFeature(final String p0);
    
    Map<String, Object> getProperties();
    
    Object getProperty(final String p0);
}
