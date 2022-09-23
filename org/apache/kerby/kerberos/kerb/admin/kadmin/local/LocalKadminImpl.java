// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.admin.kadmin.local;

import org.slf4j.LoggerFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.apache.kerby.kerberos.kerb.keytab.Keytab;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ArrayList;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import java.util.List;
import org.apache.kerby.kerberos.kerb.identity.KrbIdentity;
import org.apache.kerby.kerberos.kerb.common.EncryptionUtil;
import org.apache.kerby.KOptions;
import org.apache.kerby.kerberos.kerb.common.KrbUtil;
import java.io.File;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.server.KdcSetting;
import org.apache.kerby.kerberos.kerb.server.KdcUtil;
import org.apache.kerby.kerberos.kerb.identity.backend.BackendConfig;
import org.apache.kerby.kerberos.kerb.server.KdcConfig;
import org.apache.kerby.kerberos.kerb.identity.backend.IdentityBackend;
import org.apache.kerby.kerberos.kerb.server.ServerSetting;
import org.slf4j.Logger;

public class LocalKadminImpl implements LocalKadmin
{
    private static final Logger LOG;
    private final ServerSetting serverSetting;
    private final IdentityBackend backend;
    
    public LocalKadminImpl(final KdcConfig kdcConfig, final BackendConfig backendConfig) throws KrbException {
        this.backend = KdcUtil.getBackend(backendConfig);
        this.serverSetting = new KdcSetting(kdcConfig, backendConfig);
    }
    
    public LocalKadminImpl(final ServerSetting serverSetting) throws KrbException {
        this.backend = KdcUtil.getBackend(serverSetting.getBackendConfig());
        this.serverSetting = serverSetting;
    }
    
    public LocalKadminImpl(final File confDir) throws KrbException {
        KdcConfig tmpKdcConfig = KdcUtil.getKdcConfig(confDir);
        if (tmpKdcConfig == null) {
            tmpKdcConfig = new KdcConfig();
        }
        BackendConfig tmpBackendConfig = KdcUtil.getBackendConfig(confDir);
        if (tmpBackendConfig == null) {
            tmpBackendConfig = new BackendConfig();
        }
        this.serverSetting = new KdcSetting(tmpKdcConfig, tmpBackendConfig);
        this.backend = KdcUtil.getBackend(tmpBackendConfig);
    }
    
    public LocalKadminImpl(final KdcSetting kdcSetting, final IdentityBackend backend) {
        this.serverSetting = kdcSetting;
        this.backend = backend;
    }
    
    private String getTgsPrincipal() {
        return KrbUtil.makeTgsPrincipal(this.serverSetting.getKdcRealm()).getName();
    }
    
    @Override
    public String getKadminPrincipal() {
        return KrbUtil.makeKadminPrincipal(this.serverSetting.getKdcRealm()).getName();
    }
    
    @Override
    public void checkBuiltinPrincipals() throws KrbException {
        final String tgsPrincipal = this.getTgsPrincipal();
        final String kadminPrincipal = this.getKadminPrincipal();
        if (this.backend.getIdentity(tgsPrincipal) == null || this.backend.getIdentity(kadminPrincipal) == null) {
            final String errorMsg = "The built-in principals do not exist in backend, please run the kdcinit tool.";
            LocalKadminImpl.LOG.error(errorMsg);
            throw new KrbException(errorMsg);
        }
    }
    
    @Override
    public void createBuiltinPrincipals() throws KrbException {
        final String tgsPrincipal = this.getTgsPrincipal();
        if (this.backend.getIdentity(tgsPrincipal) != null) {
            final String errorMsg = "The tgs principal already exists in backend.";
            LocalKadminImpl.LOG.error(errorMsg);
            throw new KrbException(errorMsg);
        }
        this.addPrincipal(tgsPrincipal);
        final String kadminPrincipal = this.getKadminPrincipal();
        if (this.backend.getIdentity(kadminPrincipal) == null) {
            this.addPrincipal(kadminPrincipal);
            return;
        }
        final String errorMsg2 = "The kadmin principal already exists in backend.";
        LocalKadminImpl.LOG.error(errorMsg2);
        throw new KrbException(errorMsg2);
    }
    
    @Override
    public void deleteBuiltinPrincipals() throws KrbException {
        this.deletePrincipal(this.getTgsPrincipal());
        this.deletePrincipal(this.getKadminPrincipal());
    }
    
    @Override
    public KdcConfig getKdcConfig() {
        return this.serverSetting.getKdcConfig();
    }
    
    @Override
    public BackendConfig getBackendConfig() {
        return this.serverSetting.getBackendConfig();
    }
    
    @Override
    public IdentityBackend getIdentityBackend() {
        return this.backend;
    }
    
    @Override
    public void addPrincipal(String principal) throws KrbException {
        principal = this.fixPrincipal(principal);
        this.addPrincipal(principal, new KOptions());
    }
    
    @Override
    public void addPrincipal(String principal, final KOptions kOptions) throws KrbException {
        principal = this.fixPrincipal(principal);
        final KrbIdentity identity = AdminHelper.createIdentity(principal, kOptions);
        final List<EncryptionKey> keys = EncryptionUtil.generateKeys(this.getKdcConfig().getEncryptionTypes());
        identity.addKeys(keys);
        this.backend.addIdentity(identity);
    }
    
    @Override
    public void addPrincipal(String principal, final String password) throws KrbException {
        principal = this.fixPrincipal(principal);
        this.addPrincipal(principal, password, new KOptions());
    }
    
    @Override
    public void addPrincipal(String principal, final String password, final KOptions kOptions) throws KrbException {
        principal = this.fixPrincipal(principal);
        final KrbIdentity identity = AdminHelper.createIdentity(principal, kOptions);
        final List<EncryptionKey> keys = EncryptionUtil.generateKeys(principal, password, this.getKdcConfig().getEncryptionTypes());
        identity.addKeys(keys);
        this.backend.addIdentity(identity);
    }
    
    @Override
    public void exportKeytab(final File keytabFile, String principal) throws KrbException {
        principal = this.fixPrincipal(principal);
        final List<String> principals = new ArrayList<String>(1);
        principals.add(principal);
        this.exportKeytab(keytabFile, principals);
    }
    
    @Override
    public void exportKeytab(final File keytabFile, final List<String> principals) throws KrbException {
        final List<KrbIdentity> identities = new LinkedList<KrbIdentity>();
        for (final String principal : principals) {
            final KrbIdentity identity = this.backend.getIdentity(principal);
            if (identity == null) {
                throw new KrbException("Can not find the identity for principal " + principal);
            }
            identities.add(identity);
        }
        AdminHelper.exportKeytab(keytabFile, identities);
    }
    
    @Override
    public void exportKeytab(final File keytabFile) throws KrbException {
        final Keytab keytab = AdminHelper.createOrLoadKeytab(keytabFile);
        final Iterable<String> principals = this.backend.getIdentities();
        for (final String principal : principals) {
            final KrbIdentity identity = this.backend.getIdentity(principal);
            if (identity != null) {
                AdminHelper.exportToKeytab(keytab, identity);
            }
        }
        AdminHelper.storeKeytab(keytab, keytabFile);
    }
    
    @Override
    public void removeKeytabEntriesOf(final File keytabFile, String principal) throws KrbException {
        principal = this.fixPrincipal(principal);
        AdminHelper.removeKeytabEntriesOf(keytabFile, principal);
    }
    
    @Override
    public void removeKeytabEntriesOf(final File keytabFile, String principal, final int kvno) throws KrbException {
        principal = this.fixPrincipal(principal);
        AdminHelper.removeKeytabEntriesOf(keytabFile, principal, kvno);
    }
    
    @Override
    public void removeOldKeytabEntriesOf(final File keytabFile, String principal) throws KrbException {
        principal = this.fixPrincipal(principal);
        AdminHelper.removeOldKeytabEntriesOf(keytabFile, principal);
    }
    
    @Override
    public void deletePrincipal(String principal) throws KrbException {
        principal = this.fixPrincipal(principal);
        this.backend.deleteIdentity(principal);
    }
    
    @Override
    public void modifyPrincipal(String principal, final KOptions kOptions) throws KrbException {
        principal = this.fixPrincipal(principal);
        final KrbIdentity identity = this.backend.getIdentity(principal);
        if (identity == null) {
            throw new KrbException("Principal \"" + principal + "\" does not exist.");
        }
        AdminHelper.updateIdentity(identity, kOptions);
        this.backend.updateIdentity(identity);
    }
    
    @Override
    public void renamePrincipal(String oldPrincipalName, String newPrincipalName) throws KrbException {
        oldPrincipalName = this.fixPrincipal(oldPrincipalName);
        newPrincipalName = this.fixPrincipal(newPrincipalName);
        final KrbIdentity oldIdentity = this.backend.getIdentity(newPrincipalName);
        if (oldIdentity != null) {
            throw new KrbException("Principal \"" + oldIdentity.getPrincipalName() + "\" is already exist.");
        }
        final KrbIdentity identity = this.backend.getIdentity(oldPrincipalName);
        if (identity == null) {
            throw new KrbException("Principal \"" + oldPrincipalName + "\" does not exist.");
        }
        this.backend.deleteIdentity(oldPrincipalName);
        identity.setPrincipalName(newPrincipalName);
        identity.setPrincipal(new PrincipalName(newPrincipalName));
        this.backend.addIdentity(identity);
    }
    
    @Override
    public KrbIdentity getPrincipal(final String principalName) throws KrbException {
        final KrbIdentity identity = this.backend.getIdentity(principalName);
        return identity;
    }
    
    @Override
    public List<String> getPrincipals() throws KrbException {
        final Iterable<String> principalNames = this.backend.getIdentities();
        final List<String> principalList = new LinkedList<String>();
        final Iterator<String> iterator = principalNames.iterator();
        while (iterator.hasNext()) {
            principalList.add(iterator.next());
        }
        return principalList;
    }
    
    @Override
    public List<String> getPrincipals(final String globString) throws KrbException {
        final Pattern pt = AdminHelper.getPatternFromGlobPatternString(globString);
        if (pt == null) {
            return this.getPrincipals();
        }
        final Boolean containsAt = pt.pattern().indexOf(64) != -1;
        final List<String> result = new LinkedList<String>();
        final List<String> principalNames = this.getPrincipals();
        for (final String principal : principalNames) {
            final String toMatch = containsAt ? principal : principal.split("@")[0];
            final Matcher m = pt.matcher(toMatch);
            if (m.matches()) {
                result.add(principal);
            }
        }
        return result;
    }
    
    @Override
    public void changePassword(String principal, final String newPassword) throws KrbException {
        principal = this.fixPrincipal(principal);
        final KrbIdentity identity = this.backend.getIdentity(principal);
        if (identity == null) {
            throw new KrbException("Principal " + principal + "was not found. Please check the input and try again");
        }
        final List<EncryptionKey> keys = EncryptionUtil.generateKeys(principal, newPassword, this.getKdcConfig().getEncryptionTypes());
        identity.addKeys(keys);
        this.backend.updateIdentity(identity);
    }
    
    @Override
    public void updateKeys(String principal) throws KrbException {
        principal = this.fixPrincipal(principal);
        final KrbIdentity identity = this.backend.getIdentity(principal);
        if (identity == null) {
            throw new KrbException("Principal " + principal + "was not found. Please check the input and try again");
        }
        final List<EncryptionKey> keys = EncryptionUtil.generateKeys(this.getKdcConfig().getEncryptionTypes());
        identity.addKeys(keys);
        this.backend.updateIdentity(identity);
    }
    
    @Override
    public void release() throws KrbException {
        if (this.backend != null) {
            this.backend.stop();
        }
    }
    
    @Override
    public int size() throws KrbException {
        return this.getPrincipals().size();
    }
    
    private String fixPrincipal(String principal) {
        if (!principal.contains("@")) {
            principal = principal + "@" + this.serverSetting.getKdcRealm();
        }
        return principal;
    }
    
    static {
        LOG = LoggerFactory.getLogger(LocalKadminImpl.class);
    }
}
