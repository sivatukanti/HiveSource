// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.server.replay;

public interface CacheService
{
    boolean checkAndCache(final RequestRecord p0);
    
    void clear();
}
