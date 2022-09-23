// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server;

import org.apache.hadoop.security.token.TokenInfo;
import java.lang.annotation.Annotation;
import org.apache.hadoop.yarn.server.api.ResourceTrackerPB;
import org.apache.hadoop.security.KerberosInfo;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.SecurityInfo;

public class RMNMSecurityInfoClass extends SecurityInfo
{
    @Override
    public KerberosInfo getKerberosInfo(final Class<?> protocol, final Configuration conf) {
        if (!protocol.equals(ResourceTrackerPB.class)) {
            return null;
        }
        return new KerberosInfo() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }
            
            @Override
            public String serverPrincipal() {
                return "yarn.resourcemanager.principal";
            }
            
            @Override
            public String clientPrincipal() {
                return "yarn.nodemanager.principal";
            }
        };
    }
    
    @Override
    public TokenInfo getTokenInfo(final Class<?> protocol, final Configuration conf) {
        return null;
    }
}
