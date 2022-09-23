// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.token;

import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;

public interface DtFetcher
{
    Text getServiceName();
    
    boolean isTokenRequired();
    
    Token<?> addDelegationTokens(final Configuration p0, final Credentials p1, final String p2, final String p3) throws Exception;
}
