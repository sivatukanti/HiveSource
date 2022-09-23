// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.util;

public class StringIgnoreCaseKeyComparator implements KeyComparator<String>
{
    public static final StringIgnoreCaseKeyComparator SINGLETON;
    
    @Override
    public int hash(final String k) {
        return k.toLowerCase().hashCode();
    }
    
    @Override
    public boolean equals(final String x, final String y) {
        return x.equalsIgnoreCase(y);
    }
    
    @Override
    public int compare(final String o1, final String o2) {
        return o1.compareToIgnoreCase(o2);
    }
    
    static {
        SINGLETON = new StringIgnoreCaseKeyComparator();
    }
}
