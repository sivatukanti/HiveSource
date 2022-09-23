// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.crypto.key.kms;

import org.apache.hadoop.security.token.delegation.web.DelegationTokenIdentifier;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public final class KMSDelegationToken
{
    public static final String TOKEN_KIND_STR = "kms-dt";
    public static final Text TOKEN_KIND;
    
    private KMSDelegationToken() {
    }
    
    static {
        TOKEN_KIND = new Text("kms-dt");
    }
    
    public static class KMSDelegationTokenIdentifier extends DelegationTokenIdentifier
    {
        public KMSDelegationTokenIdentifier() {
            super(KMSDelegationToken.TOKEN_KIND);
        }
        
        @Override
        public Text getKind() {
            return KMSDelegationToken.TOKEN_KIND;
        }
    }
}
