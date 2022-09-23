// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.client.security;

import java.io.IOException;
import org.eclipse.jetty.client.HttpDestination;
import java.util.HashMap;
import java.util.Map;

public class HashRealmResolver implements RealmResolver
{
    private Map<String, Realm> _realmMap;
    
    public void addSecurityRealm(final Realm realm) {
        if (this._realmMap == null) {
            this._realmMap = new HashMap<String, Realm>();
        }
        this._realmMap.put(realm.getId(), realm);
    }
    
    public Realm getRealm(final String realmName, final HttpDestination destination, final String path) throws IOException {
        return this._realmMap.get(realmName);
    }
}
