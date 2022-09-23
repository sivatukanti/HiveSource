// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.util;

public interface RestrictedResourceRetriever extends ResourceRetriever
{
    int getConnectTimeout();
    
    void setConnectTimeout(final int p0);
    
    int getReadTimeout();
    
    void setReadTimeout(final int p0);
    
    int getSizeLimit();
    
    void setSizeLimit(final int p0);
}
