// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.servlet;

interface UriPatternMatcher
{
    boolean matches(final String p0);
    
    String extractPath(final String p0);
    
    UriPatternType getPatternType();
}
