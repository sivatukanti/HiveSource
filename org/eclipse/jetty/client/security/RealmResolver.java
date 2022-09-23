// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.client.security;

import java.io.IOException;
import org.eclipse.jetty.client.HttpDestination;

public interface RealmResolver
{
    Realm getRealm(final String p0, final HttpDestination p1, final String p2) throws IOException;
}
