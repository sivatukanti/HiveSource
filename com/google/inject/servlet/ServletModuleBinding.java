// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.servlet;

import java.util.Map;

public interface ServletModuleBinding
{
    UriPatternType getUriPatternType();
    
    String getPattern();
    
    Map<String, String> getInitParams();
    
    boolean matchesUri(final String p0);
}
