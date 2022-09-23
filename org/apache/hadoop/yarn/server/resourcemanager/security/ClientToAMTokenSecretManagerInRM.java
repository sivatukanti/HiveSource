// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.security;

import org.apache.hadoop.security.token.SecretManager;
import java.util.HashMap;
import javax.crypto.SecretKey;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import java.util.Map;
import org.apache.hadoop.yarn.security.client.BaseClientToAMTokenSecretManager;

public class ClientToAMTokenSecretManagerInRM extends BaseClientToAMTokenSecretManager
{
    private Map<ApplicationAttemptId, SecretKey> masterKeys;
    
    public ClientToAMTokenSecretManagerInRM() {
        this.masterKeys = new HashMap<ApplicationAttemptId, SecretKey>();
    }
    
    public synchronized SecretKey createMasterKey(final ApplicationAttemptId applicationAttemptID) {
        return this.generateSecret();
    }
    
    public synchronized void registerApplication(final ApplicationAttemptId applicationAttemptID, final SecretKey key) {
        this.masterKeys.put(applicationAttemptID, key);
    }
    
    public synchronized SecretKey registerMasterKey(final ApplicationAttemptId applicationAttemptID, final byte[] keyData) {
        final SecretKey key = SecretManager.createSecretKey(keyData);
        this.registerApplication(applicationAttemptID, key);
        return key;
    }
    
    public synchronized void unRegisterApplication(final ApplicationAttemptId applicationAttemptID) {
        this.masterKeys.remove(applicationAttemptID);
    }
    
    @Override
    public synchronized SecretKey getMasterKey(final ApplicationAttemptId applicationAttemptID) {
        return this.masterKeys.get(applicationAttemptID);
    }
}
