// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.security.client;

import org.apache.hadoop.security.token.TokenIdentifier;
import javax.crypto.SecretKey;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.security.token.SecretManager;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public abstract class BaseClientToAMTokenSecretManager extends SecretManager<ClientToAMTokenIdentifier>
{
    @InterfaceAudience.Private
    public abstract SecretKey getMasterKey(final ApplicationAttemptId p0);
    
    @InterfaceAudience.Private
    public synchronized byte[] createPassword(final ClientToAMTokenIdentifier identifier) {
        return SecretManager.createPassword(identifier.getBytes(), this.getMasterKey(identifier.getApplicationAttemptID()));
    }
    
    @InterfaceAudience.Private
    @Override
    public byte[] retrievePassword(final ClientToAMTokenIdentifier identifier) throws InvalidToken {
        final SecretKey masterKey = this.getMasterKey(identifier.getApplicationAttemptID());
        if (masterKey == null) {
            throw new InvalidToken("Illegal client-token!");
        }
        return SecretManager.createPassword(identifier.getBytes(), masterKey);
    }
    
    @InterfaceAudience.Private
    @Override
    public ClientToAMTokenIdentifier createIdentifier() {
        return new ClientToAMTokenIdentifier();
    }
}
