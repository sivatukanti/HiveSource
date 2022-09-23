// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.client.security;

import java.io.IOException;
import org.eclipse.jetty.client.HttpDestination;

public class SimpleRealmResolver implements RealmResolver
{
    private Realm _realm;
    
    public SimpleRealmResolver(final Realm realm) {
        this._realm = realm;
    }
    
    public Realm getRealm(final String realmName, final HttpDestination destination, final String path) throws IOException {
        return this._realm;
    }
}
