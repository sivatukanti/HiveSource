// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.adapter;

public class DerbySQLFunction
{
    public static int ascii(final String code) {
        return code.charAt(0);
    }
    
    public static int matches(final String text, final String pattern) {
        return text.matches(pattern) ? 1 : 0;
    }
}
