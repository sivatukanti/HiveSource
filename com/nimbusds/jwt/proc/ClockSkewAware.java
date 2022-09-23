// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jwt.proc;

public interface ClockSkewAware
{
    int getMaxClockSkew();
    
    void setMaxClockSkew(final int p0);
}
