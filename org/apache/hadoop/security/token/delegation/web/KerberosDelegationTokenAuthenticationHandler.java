// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.token.delegation.web;

import org.apache.hadoop.security.authentication.server.AuthenticationHandler;
import org.apache.hadoop.security.authentication.server.KerberosAuthenticationHandler;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public class KerberosDelegationTokenAuthenticationHandler extends DelegationTokenAuthenticationHandler
{
    public KerberosDelegationTokenAuthenticationHandler() {
        super(new KerberosAuthenticationHandler("kerberos-dt"));
    }
}
