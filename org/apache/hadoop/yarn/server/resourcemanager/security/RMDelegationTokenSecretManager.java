// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.security;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenIdentifier;
import org.apache.hadoop.security.token.SecretManager;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.RMStateStore;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import com.google.common.annotations.VisibleForTesting;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.io.IOException;
import org.apache.hadoop.util.ExitUtil;
import org.apache.hadoop.security.token.delegation.DelegationKey;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.Recoverable;
import org.apache.hadoop.yarn.security.client.RMDelegationTokenIdentifier;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenSecretManager;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class RMDelegationTokenSecretManager extends AbstractDelegationTokenSecretManager<RMDelegationTokenIdentifier> implements Recoverable
{
    private static final Log LOG;
    protected final RMContext rmContext;
    
    public RMDelegationTokenSecretManager(final long delegationKeyUpdateInterval, final long delegationTokenMaxLifetime, final long delegationTokenRenewInterval, final long delegationTokenRemoverScanInterval, final RMContext rmContext) {
        super(delegationKeyUpdateInterval, delegationTokenMaxLifetime, delegationTokenRenewInterval, delegationTokenRemoverScanInterval);
        this.rmContext = rmContext;
    }
    
    @Override
    public RMDelegationTokenIdentifier createIdentifier() {
        return new RMDelegationTokenIdentifier();
    }
    
    @Override
    protected void storeNewMasterKey(final DelegationKey newKey) {
        try {
            RMDelegationTokenSecretManager.LOG.info("storing master key with keyID " + newKey.getKeyId());
            this.rmContext.getStateStore().storeRMDTMasterKey(newKey);
        }
        catch (Exception e) {
            RMDelegationTokenSecretManager.LOG.error("Error in storing master key with KeyID: " + newKey.getKeyId());
            ExitUtil.terminate(1, e);
        }
    }
    
    @Override
    protected void removeStoredMasterKey(final DelegationKey key) {
        try {
            RMDelegationTokenSecretManager.LOG.info("removing master key with keyID " + key.getKeyId());
            this.rmContext.getStateStore().removeRMDTMasterKey(key);
        }
        catch (Exception e) {
            RMDelegationTokenSecretManager.LOG.error("Error in removing master key with KeyID: " + key.getKeyId());
            ExitUtil.terminate(1, e);
        }
    }
    
    @Override
    protected void storeNewToken(final RMDelegationTokenIdentifier identifier, final long renewDate) {
        try {
            RMDelegationTokenSecretManager.LOG.info("storing RMDelegation token with sequence number: " + identifier.getSequenceNumber());
            this.rmContext.getStateStore().storeRMDelegationTokenAndSequenceNumber(identifier, renewDate, identifier.getSequenceNumber());
        }
        catch (Exception e) {
            RMDelegationTokenSecretManager.LOG.error("Error in storing RMDelegationToken with sequence number: " + identifier.getSequenceNumber());
            ExitUtil.terminate(1, e);
        }
    }
    
    @Override
    protected void updateStoredToken(final RMDelegationTokenIdentifier id, final long renewDate) {
        try {
            RMDelegationTokenSecretManager.LOG.info("updating RMDelegation token with sequence number: " + id.getSequenceNumber());
            this.rmContext.getStateStore().updateRMDelegationTokenAndSequenceNumber(id, renewDate, id.getSequenceNumber());
        }
        catch (Exception e) {
            RMDelegationTokenSecretManager.LOG.error("Error in updating persisted RMDelegationToken with sequence number: " + id.getSequenceNumber());
            ExitUtil.terminate(1, e);
        }
    }
    
    @Override
    protected void removeStoredToken(final RMDelegationTokenIdentifier ident) throws IOException {
        try {
            RMDelegationTokenSecretManager.LOG.info("removing RMDelegation token with sequence number: " + ident.getSequenceNumber());
            this.rmContext.getStateStore().removeRMDelegationToken(ident, this.delegationTokenSequenceNumber);
        }
        catch (Exception e) {
            RMDelegationTokenSecretManager.LOG.error("Error in removing RMDelegationToken with sequence number: " + ident.getSequenceNumber());
            ExitUtil.terminate(1, e);
        }
    }
    
    @InterfaceAudience.Private
    @VisibleForTesting
    public synchronized Set<DelegationKey> getAllMasterKeys() {
        final HashSet<DelegationKey> keySet = new HashSet<DelegationKey>();
        keySet.addAll((Collection<?>)this.allKeys.values());
        return keySet;
    }
    
    @InterfaceAudience.Private
    @VisibleForTesting
    public synchronized Map<RMDelegationTokenIdentifier, Long> getAllTokens() {
        final Map<RMDelegationTokenIdentifier, Long> allTokens = new HashMap<RMDelegationTokenIdentifier, Long>();
        for (final Map.Entry<RMDelegationTokenIdentifier, DelegationTokenInformation> entry : this.currentTokens.entrySet()) {
            allTokens.put(entry.getKey(), entry.getValue().getRenewDate());
        }
        return allTokens;
    }
    
    @InterfaceAudience.Private
    @VisibleForTesting
    public int getLatestDTSequenceNumber() {
        return this.delegationTokenSequenceNumber;
    }
    
    @Override
    public void recover(final RMStateStore.RMState rmState) throws Exception {
        RMDelegationTokenSecretManager.LOG.info("recovering RMDelegationTokenSecretManager.");
        for (final DelegationKey dtKey : rmState.getRMDTSecretManagerState().getMasterKeyState()) {
            this.addKey(dtKey);
        }
        final Map<RMDelegationTokenIdentifier, Long> rmDelegationTokens = rmState.getRMDTSecretManagerState().getTokenState();
        this.delegationTokenSequenceNumber = rmState.getRMDTSecretManagerState().getDTSequenceNumber();
        for (final Map.Entry<RMDelegationTokenIdentifier, Long> entry : rmDelegationTokens.entrySet()) {
            this.addPersistedDelegationToken(entry.getKey(), entry.getValue());
        }
    }
    
    public long getRenewDate(final RMDelegationTokenIdentifier ident) throws InvalidToken {
        final DelegationTokenInformation info = this.currentTokens.get(ident);
        if (info == null) {
            throw new InvalidToken("token (" + ident.toString() + ") can't be found in cache");
        }
        return info.getRenewDate();
    }
    
    static {
        LOG = LogFactory.getLog(RMDelegationTokenSecretManager.class);
    }
}
