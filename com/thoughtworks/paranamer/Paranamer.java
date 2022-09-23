// 
// Decompiled by Procyon v0.5.36
// 

package com.thoughtworks.paranamer;

import java.lang.reflect.AccessibleObject;

public interface Paranamer
{
    public static final String[] EMPTY_NAMES = new String[0];
    public static final String __PARANAMER_DATA = "lookupParameterNames java.lang.reflect.AccessibleObject methodOrConstructor \nlookupParameterNames java.lang.reflect.AccessibleObject,boolean methodOrConstructor,throwExceptionIfMissing \n";
    
    String[] lookupParameterNames(final AccessibleObject p0);
    
    String[] lookupParameterNames(final AccessibleObject p0, final boolean p1);
}
