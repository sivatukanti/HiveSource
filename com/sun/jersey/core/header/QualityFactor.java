// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.header;

public interface QualityFactor
{
    public static final String QUALITY_FACTOR = "q";
    public static final int MINUMUM_QUALITY = 0;
    public static final int MAXIMUM_QUALITY = 1000;
    public static final int DEFAULT_QUALITY_FACTOR = 1000;
    
    int getQuality();
}
