// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.identity.backend;

import org.apache.kerby.kerberos.kerb.KrbException;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.LinkedHashMap;
import org.apache.kerby.kerberos.kerb.identity.KrbIdentity;
import java.util.Map;

public class MemoryIdentityBackend extends AbstractIdentityBackend
{
    private static final int DEFAULT_STORAGE_SIZE = 10000000;
    private Map<String, KrbIdentity> storage;
    private int storageSize;
    
    public MemoryIdentityBackend() {
        this.storageSize = 10000000;
    }
    
    @Override
    protected void doInitialize() {
        final Map<String, KrbIdentity> tmpMap = new LinkedHashMap<String, KrbIdentity>(this.storageSize) {
            private static final long serialVersionUID = 714064587685837472L;
            
            @Override
            protected boolean removeEldestEntry(final Map.Entry<String, KrbIdentity> eldest) {
                return this.size() > MemoryIdentityBackend.this.storageSize;
            }
        };
        this.storage = new ConcurrentHashMap<String, KrbIdentity>(tmpMap);
    }
    
    @Override
    protected KrbIdentity doGetIdentity(final String principalName) {
        return this.storage.get(principalName);
    }
    
    @Override
    protected KrbIdentity doAddIdentity(final KrbIdentity identity) {
        this.storage.put(identity.getPrincipalName(), identity);
        return identity;
    }
    
    @Override
    protected KrbIdentity doUpdateIdentity(final KrbIdentity identity) {
        return this.storage.put(identity.getPrincipalName(), identity);
    }
    
    @Override
    protected void doDeleteIdentity(final String principalName) {
        this.storage.remove(principalName);
    }
    
    @Override
    protected Iterable<String> doGetIdentities() throws KrbException {
        final List<String> identities = new ArrayList<String>(this.storage.keySet());
        Collections.sort(identities);
        return identities;
    }
}
