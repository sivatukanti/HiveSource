// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.identity.backend;

import org.slf4j.LoggerFactory;
import java.util.Collections;
import org.apache.kerby.kerberos.kerb.type.ad.AuthorizationType;
import java.io.IOException;
import org.apache.kerby.kerberos.kerb.type.ad.AuthorizationDataEntry;
import org.apache.kerby.kerberos.kerb.type.ad.AdToken;
import org.apache.kerby.kerberos.kerb.type.base.KrbToken;
import org.apache.kerby.kerberos.kerb.type.base.TokenFormat;
import org.apache.kerby.kerberos.kerb.type.ad.AuthorizationData;
import org.apache.kerby.kerberos.kerb.type.ticket.EncTicketPart;
import org.apache.kerby.kerberos.kerb.type.kdc.KdcClientRequest;
import org.apache.kerby.kerberos.kerb.identity.KrbIdentity;
import org.apache.kerby.kerberos.kerb.identity.BatchTrans;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.slf4j.Logger;
import org.apache.kerby.config.Configured;

public abstract class AbstractIdentityBackend extends Configured implements IdentityBackend
{
    private static Logger logger;
    
    protected BackendConfig getBackendConfig() {
        return (BackendConfig)this.getConfig();
    }
    
    @Override
    public void initialize() throws KrbException {
        AbstractIdentityBackend.logger.debug("initialize called");
        this.doInitialize();
    }
    
    @Override
    public boolean supportBatchTrans() {
        return false;
    }
    
    @Override
    public BatchTrans startBatchTrans() throws KrbException {
        throw new KrbException("Transaction isn't supported");
    }
    
    protected void doInitialize() throws KrbException {
    }
    
    @Override
    public void start() {
        this.doStart();
        AbstractIdentityBackend.logger.debug("start called");
    }
    
    protected void doStart() {
    }
    
    @Override
    public void stop() throws KrbException {
        this.doStop();
        AbstractIdentityBackend.logger.debug("stop called");
    }
    
    protected void doStop() throws KrbException {
    }
    
    @Override
    public void release() {
        this.doRelease();
        AbstractIdentityBackend.logger.debug("release called");
    }
    
    protected void doRelease() {
    }
    
    @Override
    public Iterable<String> getIdentities() throws KrbException {
        AbstractIdentityBackend.logger.debug("getIdentities called");
        return this.doGetIdentities();
    }
    
    protected abstract Iterable<String> doGetIdentities() throws KrbException;
    
    @Override
    public KrbIdentity getIdentity(final String principalName) throws KrbException {
        if (principalName == null || principalName.isEmpty()) {
            throw new IllegalArgumentException("Invalid principal name");
        }
        AbstractIdentityBackend.logger.debug("getIdentity called, principalName = {}", principalName);
        final KrbIdentity identity = this.doGetIdentity(principalName);
        AbstractIdentityBackend.logger.debug("getIdentity {}, principalName = {}", (identity != null) ? "successful" : "failed", principalName);
        return identity;
    }
    
    protected abstract KrbIdentity doGetIdentity(final String p0) throws KrbException;
    
    @Override
    public AuthorizationData getIdentityAuthorizationData(final KdcClientRequest kdcClientRequest, final EncTicketPart encTicketPart) throws KrbException {
        if (kdcClientRequest == null) {
            throw new IllegalArgumentException("Invalid identity");
        }
        AbstractIdentityBackend.logger.debug("getIdentityAuthorizationData called, krbIdentity = {}", kdcClientRequest.getClientPrincipal());
        final AuthorizationData authData = this.doGetIdentityAuthorizationData(kdcClientRequest, encTicketPart);
        AbstractIdentityBackend.logger.debug("getIdentityAuthorizationData {}, authData = {}", (authData != null) ? "successful" : "failed", authData);
        return authData;
    }
    
    protected AuthorizationData doGetIdentityAuthorizationData(final KdcClientRequest kdcClientRequest, final EncTicketPart encTicketPart) throws KrbException {
        if (kdcClientRequest.isToken()) {
            final KrbToken krbToken = new KrbToken(kdcClientRequest.getToken(), TokenFormat.JWT);
            final AdToken adToken = new AdToken();
            adToken.setToken(krbToken);
            final AuthorizationData authzData = new AuthorizationData();
            final AuthorizationDataEntry authzDataEntry = new AuthorizationDataEntry();
            try {
                authzDataEntry.setAuthzData(adToken.encode());
            }
            catch (IOException e) {
                throw new KrbException("Error encoding AdToken", e);
            }
            authzDataEntry.setAuthzType(AuthorizationType.AD_TOKEN);
            authzData.setElements(Collections.singletonList(authzDataEntry));
            return authzData;
        }
        return null;
    }
    
    @Override
    public KrbIdentity addIdentity(final KrbIdentity identity) throws KrbException {
        if (identity == null) {
            throw new IllegalArgumentException("null identity to add");
        }
        if (this.doGetIdentity(identity.getPrincipalName()) != null) {
            throw new KrbException("Principal already exists: " + identity.getPrincipalName());
        }
        final KrbIdentity added = this.doAddIdentity(identity);
        AbstractIdentityBackend.logger.debug("addIdentity {}, principalName = {}", (added != null) ? "successful" : "failed", identity.getPrincipalName());
        return added;
    }
    
    protected abstract KrbIdentity doAddIdentity(final KrbIdentity p0) throws KrbException;
    
    @Override
    public KrbIdentity updateIdentity(final KrbIdentity identity) throws KrbException {
        if (identity == null) {
            throw new IllegalArgumentException("null identity to update");
        }
        if (this.doGetIdentity(identity.getPrincipalName()) == null) {
            AbstractIdentityBackend.logger.error("Error occurred while updating identity, principal " + identity.getPrincipalName() + " does not exists.");
            throw new KrbException("Principal does not exist.");
        }
        final KrbIdentity updated = this.doUpdateIdentity(identity);
        AbstractIdentityBackend.logger.debug("updateIdentity {}, principalName = {}", (updated != null) ? "successful" : "failed", identity.getPrincipalName());
        return updated;
    }
    
    protected abstract KrbIdentity doUpdateIdentity(final KrbIdentity p0) throws KrbException;
    
    @Override
    public void deleteIdentity(final String principalName) throws KrbException {
        AbstractIdentityBackend.logger.debug("deleteIdentity called, principalName = {}", principalName);
        if (principalName == null) {
            throw new IllegalArgumentException("null identity to remove");
        }
        if (this.doGetIdentity(principalName) == null) {
            AbstractIdentityBackend.logger.error("Error occurred while deleting identity, principal " + principalName + " does not exists.");
            throw new KrbException("Principal does not exist.");
        }
        this.doDeleteIdentity(principalName);
    }
    
    protected abstract void doDeleteIdentity(final String p0) throws KrbException;
    
    static {
        AbstractIdentityBackend.logger = LoggerFactory.getLogger(AbstractIdentityBackend.class);
    }
}
