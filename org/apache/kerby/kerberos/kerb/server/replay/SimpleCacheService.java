// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.server.replay;

import java.util.HashSet;
import java.util.Set;

public class SimpleCacheService implements CacheService
{
    private final Set<RequestRecord> requests;
    
    public SimpleCacheService() {
        this.requests = new HashSet<RequestRecord>();
    }
    
    @Override
    public boolean checkAndCache(final RequestRecord request) {
        if (this.requests.contains(request)) {
            return true;
        }
        this.requests.add(request);
        return false;
    }
    
    @Override
    public void clear() {
        this.requests.clear();
    }
}
