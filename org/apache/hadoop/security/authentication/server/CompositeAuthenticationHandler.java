// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.authentication.server;

import java.util.Collection;

public interface CompositeAuthenticationHandler extends AuthenticationHandler
{
    Collection<String> getTokenTypes();
}
