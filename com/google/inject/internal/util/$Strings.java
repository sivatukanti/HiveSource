// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.util;

public class $Strings
{
    private $Strings() {
    }
    
    public static String capitalize(final String s) {
        if (s.length() == 0) {
            return s;
        }
        final char first = s.charAt(0);
        final char capitalized = Character.toUpperCase(first);
        return (first == capitalized) ? s : (capitalized + s.substring(1));
    }
}
