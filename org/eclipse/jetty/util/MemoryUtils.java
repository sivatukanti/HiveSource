// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.security.AccessController;
import java.security.PrivilegedAction;

public class MemoryUtils
{
    private static final int cacheLineBytes;
    
    private MemoryUtils() {
    }
    
    public static int getCacheLineBytes() {
        return MemoryUtils.cacheLineBytes;
    }
    
    public static int getIntegersPerCacheLine() {
        return getCacheLineBytes() >> 2;
    }
    
    public static int getLongsPerCacheLine() {
        return getCacheLineBytes() >> 3;
    }
    
    static {
        final int defaultValue = 64;
        int value = 64;
        try {
            value = Integer.parseInt(AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
                @Override
                public String run() {
                    return System.getProperty("org.eclipse.jetty.util.cacheLineBytes", String.valueOf(64));
                }
            }));
        }
        catch (Exception ex) {}
        cacheLineBytes = value;
    }
}
