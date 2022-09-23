// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder;

import java.util.Map;

public interface BuilderParameters
{
    public static final String RESERVED_PARAMETER_PREFIX = "config-";
    
    Map<String, Object> getParameters();
}
