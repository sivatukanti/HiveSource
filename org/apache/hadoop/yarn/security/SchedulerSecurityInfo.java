// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.security;

import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.security.token.TokenSelector;
import java.lang.annotation.Annotation;
import org.apache.hadoop.yarn.api.ApplicationMasterProtocolPB;
import org.apache.hadoop.security.token.TokenInfo;
import org.apache.hadoop.security.KerberosInfo;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.security.SecurityInfo;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class SchedulerSecurityInfo extends SecurityInfo
{
    @Override
    public KerberosInfo getKerberosInfo(final Class<?> protocol, final Configuration conf) {
        return null;
    }
    
    @Override
    public TokenInfo getTokenInfo(final Class<?> protocol, final Configuration conf) {
        if (!protocol.equals(ApplicationMasterProtocolPB.class)) {
            return null;
        }
        return new TokenInfo() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }
            
            @Override
            public Class<? extends TokenSelector<? extends TokenIdentifier>> value() {
                return AMRMTokenSelector.class;
            }
        };
    }
}
