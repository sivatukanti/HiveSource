// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security;

import org.apache.hadoop.security.token.TokenInfo;
import org.apache.hadoop.conf.Configuration;

public class AnnotatedSecurityInfo extends SecurityInfo
{
    @Override
    public KerberosInfo getKerberosInfo(final Class<?> protocol, final Configuration conf) {
        return protocol.getAnnotation(KerberosInfo.class);
    }
    
    @Override
    public TokenInfo getTokenInfo(final Class<?> protocol, final Configuration conf) {
        return protocol.getAnnotation(TokenInfo.class);
    }
}
