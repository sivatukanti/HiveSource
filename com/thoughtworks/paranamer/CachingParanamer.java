// 
// Decompiled by Procyon v0.5.36
// 

package com.thoughtworks.paranamer;

import java.lang.reflect.AccessibleObject;
import java.util.WeakHashMap;

public class CachingParanamer implements Paranamer
{
    public static final String __PARANAMER_DATA = "v1.0 \ncom.thoughtworks.paranamer.CachingParanamer <init> com.thoughtworks.paranamer.Paranamer delegate \ncom.thoughtworks.paranamer.CachingParanamer lookupParameterNames java.lang.AccessibleObject methodOrConstructor \ncom.thoughtworks.paranamer.CachingParanamer lookupParameterNames java.lang.AccessibleObject, boolean methodOrCtor,throwExceptionIfMissing \n";
    private final Paranamer delegate;
    private final WeakHashMap<AccessibleObject, String[]> methodCache;
    
    public CachingParanamer() {
        this(new DefaultParanamer());
    }
    
    public CachingParanamer(final Paranamer delegate) {
        this.methodCache = new WeakHashMap<AccessibleObject, String[]>();
        this.delegate = delegate;
    }
    
    public String[] lookupParameterNames(final AccessibleObject methodOrConstructor) {
        return this.lookupParameterNames(methodOrConstructor, true);
    }
    
    public String[] lookupParameterNames(final AccessibleObject methodOrCtor, final boolean throwExceptionIfMissing) {
        if (this.methodCache.containsKey(methodOrCtor)) {
            return this.methodCache.get(methodOrCtor);
        }
        final String[] names = this.delegate.lookupParameterNames(methodOrCtor, throwExceptionIfMissing);
        this.methodCache.put(methodOrCtor, names);
        return names;
    }
}
