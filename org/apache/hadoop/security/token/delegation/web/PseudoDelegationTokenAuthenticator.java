// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.token.delegation.web;

import org.apache.hadoop.security.authentication.client.Authenticator;
import java.io.IOException;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.authentication.client.PseudoAuthenticator;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class PseudoDelegationTokenAuthenticator extends DelegationTokenAuthenticator
{
    public PseudoDelegationTokenAuthenticator() {
        super(new PseudoAuthenticator() {
            @Override
            protected String getUserName() {
                try {
                    return UserGroupInformation.getCurrentUser().getShortUserName();
                }
                catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }
}
