// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.localization;

public interface Localizable
{
    public static final String NOT_LOCALIZABLE = new String("\u0000");
    
    String getKey();
    
    Object[] getArguments();
    
    String getResourceBundleName();
}
