// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.server.replay;

public class ReplayCheckServiceImpl implements ReplayCheckService
{
    private CacheService cacheService;
    
    public ReplayCheckServiceImpl(final CacheService cacheService) {
        this.cacheService = cacheService;
    }
    
    public ReplayCheckServiceImpl() {
        this(new SimpleCacheService());
    }
    
    @Override
    public boolean checkReplay(final String clientPrincipal, final String serverPrincipal, final long requestTime, final int microseconds) {
        final RequestRecord record = new RequestRecord(clientPrincipal, serverPrincipal, requestTime, microseconds);
        return this.cacheService.checkAndCache(record);
    }
}
