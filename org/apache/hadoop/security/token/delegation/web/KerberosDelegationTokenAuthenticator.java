// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.token.delegation.web;

import org.apache.hadoop.security.authentication.client.Authenticator;
import org.apache.hadoop.security.authentication.client.KerberosAuthenticator;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class KerberosDelegationTokenAuthenticator extends DelegationTokenAuthenticator
{
    public KerberosDelegationTokenAuthenticator() {
        super(new KerberosAuthenticator() {
            @Override
            protected Authenticator getFallBackAuthenticator() {
                return new PseudoDelegationTokenAuthenticator();
            }
        });
    }
}
