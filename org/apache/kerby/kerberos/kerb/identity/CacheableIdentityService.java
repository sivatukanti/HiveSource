// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.identity;

import org.apache.kerby.kerberos.kerb.type.ad.AuthorizationData;
import org.apache.kerby.kerberos.kerb.type.ticket.EncTicketPart;
import org.apache.kerby.kerberos.kerb.type.kdc.KdcClientRequest;
import java.util.concurrent.ConcurrentHashMap;
import java.util.LinkedHashMap;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.config.Config;
import java.util.Map;
import org.apache.kerby.config.Configured;

public class CacheableIdentityService extends Configured implements IdentityService
{
    private static final int DEFAULT_CACHE_SIZE = 1000;
    private Map<String, KrbIdentity> idCache;
    private int cacheSize;
    private IdentityService underlying;
    
    public CacheableIdentityService(final Config config, final IdentityService underlying) {
        super(config);
        this.cacheSize = 1000;
        this.underlying = underlying;
        this.init();
    }
    
    @Override
    public boolean supportBatchTrans() {
        return false;
    }
    
    @Override
    public BatchTrans startBatchTrans() throws KrbException {
        throw new KrbException("Transaction isn't supported");
    }
    
    private void init() {
        final Map<String, KrbIdentity> tmpMap = new LinkedHashMap<String, KrbIdentity>(this.cacheSize) {
            private static final long serialVersionUID = -6911200685333503214L;
            
            @Override
            protected boolean removeEldestEntry(final Map.Entry<String, KrbIdentity> eldest) {
                return this.size() > CacheableIdentityService.this.cacheSize;
            }
        };
        this.idCache = new ConcurrentHashMap<String, KrbIdentity>(tmpMap);
    }
    
    @Override
    public Iterable<String> getIdentities() throws KrbException {
        return this.underlying.getIdentities();
    }
    
    @Override
    public KrbIdentity getIdentity(final String principalName) throws KrbException {
        if (this.idCache.containsKey(principalName)) {
            return this.idCache.get(principalName);
        }
        final KrbIdentity identity = this.underlying.getIdentity(principalName);
        if (identity != null) {
            this.idCache.put(principalName, identity);
        }
        return identity;
    }
    
    @Override
    public KrbIdentity addIdentity(final KrbIdentity identity) throws KrbException {
        final KrbIdentity added = this.underlying.addIdentity(identity);
        if (added != null) {
            this.idCache.put(added.getPrincipalName(), added);
        }
        return added;
    }
    
    @Override
    public KrbIdentity updateIdentity(final KrbIdentity identity) throws KrbException {
        final KrbIdentity updated = this.underlying.updateIdentity(identity);
        if (updated != null) {
            this.idCache.put(updated.getPrincipalName(), updated);
        }
        return updated;
    }
    
    @Override
    public void deleteIdentity(final String principalName) throws KrbException {
        if (this.idCache.containsKey(principalName)) {
            this.idCache.remove(principalName);
        }
        this.underlying.deleteIdentity(principalName);
    }
    
    @Override
    public AuthorizationData getIdentityAuthorizationData(final KdcClientRequest kdcClientRequest, final EncTicketPart encTicketPart) throws KrbException {
        return this.underlying.getIdentityAuthorizationData(kdcClientRequest, encTicketPart);
    }
}
