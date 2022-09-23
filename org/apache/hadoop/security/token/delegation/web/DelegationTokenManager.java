// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.token.delegation.web;

import org.apache.hadoop.security.token.delegation.ZKDelegationTokenSecretManager;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.slf4j.LoggerFactory;
import java.io.DataInput;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.security.token.SecretManager;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenIdentifier;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.UserGroupInformation;
import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenSecretManager;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public class DelegationTokenManager
{
    private static final Logger LOG;
    public static final String ENABLE_ZK_KEY = "zk-dt-secret-manager.enable";
    public static final String PREFIX = "delegation-token.";
    public static final String UPDATE_INTERVAL = "delegation-token.update-interval.sec";
    public static final long UPDATE_INTERVAL_DEFAULT = 86400L;
    public static final String MAX_LIFETIME = "delegation-token.max-lifetime.sec";
    public static final long MAX_LIFETIME_DEFAULT = 604800L;
    public static final String RENEW_INTERVAL = "delegation-token.renew-interval.sec";
    public static final long RENEW_INTERVAL_DEFAULT = 86400L;
    public static final String REMOVAL_SCAN_INTERVAL = "delegation-token.removal-scan-interval.sec";
    public static final long REMOVAL_SCAN_INTERVAL_DEFAULT = 3600L;
    private AbstractDelegationTokenSecretManager secretManager;
    private boolean managedSecretManager;
    
    public DelegationTokenManager(final Configuration conf, final Text tokenKind) {
        this.secretManager = null;
        if (conf.getBoolean("zk-dt-secret-manager.enable", false)) {
            this.secretManager = new ZKSecretManager(conf, tokenKind);
        }
        else {
            this.secretManager = new DelegationTokenSecretManager(conf, tokenKind);
        }
        this.managedSecretManager = true;
    }
    
    public void setExternalDelegationTokenSecretManager(final AbstractDelegationTokenSecretManager secretManager) {
        this.secretManager.stopThreads();
        this.secretManager = secretManager;
        this.managedSecretManager = false;
    }
    
    public void init() {
        if (this.managedSecretManager) {
            try {
                this.secretManager.startThreads();
            }
            catch (IOException ex) {
                throw new RuntimeException("Could not start " + this.secretManager.getClass() + ": " + ex.toString(), ex);
            }
        }
    }
    
    public void destroy() {
        if (this.managedSecretManager) {
            this.secretManager.stopThreads();
        }
    }
    
    public Token<? extends AbstractDelegationTokenIdentifier> createToken(final UserGroupInformation ugi, final String renewer) {
        return this.createToken(ugi, renewer, null);
    }
    
    public Token<? extends AbstractDelegationTokenIdentifier> createToken(final UserGroupInformation ugi, String renewer, final String service) {
        DelegationTokenManager.LOG.debug("Creating token with ugi:{}, renewer:{}, service:{}.", ugi, renewer, (service != null) ? service : "");
        renewer = ((renewer == null) ? ugi.getShortUserName() : renewer);
        final String user = ugi.getUserName();
        final Text owner = new Text(user);
        Text realUser = null;
        if (ugi.getRealUser() != null) {
            realUser = new Text(ugi.getRealUser().getUserName());
        }
        final AbstractDelegationTokenIdentifier tokenIdentifier = (AbstractDelegationTokenIdentifier)this.secretManager.createIdentifier();
        tokenIdentifier.setOwner(owner);
        tokenIdentifier.setRenewer(new Text(renewer));
        tokenIdentifier.setRealUser(realUser);
        final Token token = new Token((T)tokenIdentifier, this.secretManager);
        if (service != null) {
            token.setService(new Text(service));
        }
        return (Token<? extends AbstractDelegationTokenIdentifier>)token;
    }
    
    public long renewToken(final Token<? extends AbstractDelegationTokenIdentifier> token, final String renewer) throws IOException {
        DelegationTokenManager.LOG.debug("Renewing token:{} with renewer:{}.", token, renewer);
        return this.secretManager.renewToken(token, renewer);
    }
    
    public void cancelToken(final Token<? extends AbstractDelegationTokenIdentifier> token, String canceler) throws IOException {
        DelegationTokenManager.LOG.debug("Cancelling token:{} with canceler:{}.", token, canceler);
        canceler = ((canceler != null) ? canceler : this.verifyToken(token).getShortUserName());
        this.secretManager.cancelToken(token, canceler);
    }
    
    public UserGroupInformation verifyToken(final Token<? extends AbstractDelegationTokenIdentifier> token) throws IOException {
        final AbstractDelegationTokenIdentifier id = this.secretManager.decodeTokenIdentifier(token);
        this.secretManager.verifyToken(id, token.getPassword());
        return id.getUser();
    }
    
    @VisibleForTesting
    public AbstractDelegationTokenSecretManager getDelegationTokenSecretManager() {
        return this.secretManager;
    }
    
    private static DelegationTokenIdentifier decodeToken(final Token<DelegationTokenIdentifier> token, final Text tokenKind) throws IOException {
        final ByteArrayInputStream buf = new ByteArrayInputStream(token.getIdentifier());
        final DataInputStream dis = new DataInputStream(buf);
        final DelegationTokenIdentifier id = new DelegationTokenIdentifier(tokenKind);
        id.readFields(dis);
        dis.close();
        return id;
    }
    
    static {
        LOG = LoggerFactory.getLogger(DelegationTokenManager.class);
    }
    
    private static class DelegationTokenSecretManager extends AbstractDelegationTokenSecretManager<DelegationTokenIdentifier>
    {
        private Text tokenKind;
        
        public DelegationTokenSecretManager(final Configuration conf, final Text tokenKind) {
            super(conf.getLong("delegation-token.update-interval.sec", 86400L) * 1000L, conf.getLong("delegation-token.max-lifetime.sec", 604800L) * 1000L, conf.getLong("delegation-token.renew-interval.sec", 86400L) * 1000L, conf.getLong("delegation-token.removal-scan-interval.sec", 3600L) * 1000L);
            this.tokenKind = tokenKind;
        }
        
        @Override
        public DelegationTokenIdentifier createIdentifier() {
            return new DelegationTokenIdentifier(this.tokenKind);
        }
        
        @Override
        public DelegationTokenIdentifier decodeTokenIdentifier(final Token<DelegationTokenIdentifier> token) throws IOException {
            return decodeToken(token, this.tokenKind);
        }
    }
    
    private static class ZKSecretManager extends ZKDelegationTokenSecretManager<DelegationTokenIdentifier>
    {
        private Text tokenKind;
        
        public ZKSecretManager(final Configuration conf, final Text tokenKind) {
            super(conf);
            this.tokenKind = tokenKind;
        }
        
        @Override
        public DelegationTokenIdentifier createIdentifier() {
            return new DelegationTokenIdentifier(this.tokenKind);
        }
        
        @Override
        public DelegationTokenIdentifier decodeTokenIdentifier(final Token<DelegationTokenIdentifier> token) throws IOException {
            return decodeToken(token, this.tokenKind);
        }
    }
}
