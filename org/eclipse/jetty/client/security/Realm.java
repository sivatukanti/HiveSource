// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.client.security;

public interface Realm
{
    String getId();
    
    String getPrincipal();
    
    String getCredentials();
}
