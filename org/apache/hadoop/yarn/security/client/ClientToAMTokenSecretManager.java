// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.security.client;

import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.security.token.SecretManager;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import javax.crypto.SecretKey;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class ClientToAMTokenSecretManager extends BaseClientToAMTokenSecretManager
{
    private static final int MASTER_KEY_WAIT_MSEC = 10000;
    private volatile SecretKey masterKey;
    
    public ClientToAMTokenSecretManager(final ApplicationAttemptId applicationAttemptID, final byte[] key) {
        if (key != null) {
            this.masterKey = SecretManager.createSecretKey(key);
        }
        else {
            this.masterKey = null;
        }
    }
    
    @Override
    public byte[] retrievePassword(final ClientToAMTokenIdentifier identifier) throws InvalidToken {
        if (this.masterKey == null) {
            synchronized (this) {
                while (this.masterKey == null) {
                    try {
                        this.wait(10000L);
                    }
                    catch (InterruptedException e) {
                        continue;
                    }
                    break;
                }
            }
        }
        return super.retrievePassword(identifier);
    }
    
    @Override
    public SecretKey getMasterKey(final ApplicationAttemptId applicationAttemptID) {
        return this.masterKey;
    }
    
    public void setMasterKey(final byte[] key) {
        synchronized (this) {
            this.masterKey = SecretManager.createSecretKey(key);
            this.notifyAll();
        }
    }
}
