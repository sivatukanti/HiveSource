// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.session;

import org.apache.commons.logging.LogFactory;
import org.apache.hive.service.auth.HiveAuthFactory;
import org.apache.hadoop.hive.shims.Utils;
import org.apache.hadoop.fs.FileSystem;
import java.io.IOException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hive.service.cli.HiveSQLException;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hive.service.cli.thrift.TProtocolVersion;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.ql.metadata.Hive;
import org.apache.hadoop.security.UserGroupInformation;

public class HiveSessionImplwithUGI extends HiveSessionImpl
{
    public static final String HS2TOKEN = "HiveServer2ImpersonationToken";
    private UserGroupInformation sessionUgi;
    private String delegationTokenStr;
    private Hive sessionHive;
    private HiveSession proxySession;
    static final Log LOG;
    
    public HiveSessionImplwithUGI(final TProtocolVersion protocol, final String username, final String password, final HiveConf hiveConf, final String ipAddress, final String delegationToken) throws HiveSQLException {
        super(protocol, username, password, hiveConf, ipAddress);
        this.sessionUgi = null;
        this.delegationTokenStr = null;
        this.sessionHive = null;
        this.proxySession = null;
        this.setSessionUGI(username);
        this.setDelegationToken(delegationToken);
        Hive.set((Hive)null);
        try {
            this.sessionHive = Hive.get(this.getHiveConf());
        }
        catch (HiveException e) {
            throw new HiveSQLException("Failed to setup metastore connection", (Throwable)e);
        }
    }
    
    public void setSessionUGI(final String owner) throws HiveSQLException {
        if (owner == null) {
            throw new HiveSQLException("No username provided for impersonation");
        }
        if (UserGroupInformation.isSecurityEnabled()) {
            try {
                this.sessionUgi = UserGroupInformation.createProxyUser(owner, UserGroupInformation.getLoginUser());
                return;
            }
            catch (IOException e) {
                throw new HiveSQLException("Couldn't setup proxy user", e);
            }
        }
        this.sessionUgi = UserGroupInformation.createRemoteUser(owner);
    }
    
    public UserGroupInformation getSessionUgi() {
        return this.sessionUgi;
    }
    
    public String getDelegationToken() {
        return this.delegationTokenStr;
    }
    
    @Override
    protected synchronized void acquire(final boolean userAccess) {
        super.acquire(userAccess);
        if (this.sessionHive != null) {
            Hive.set(this.sessionHive);
        }
    }
    
    @Override
    public void close() throws HiveSQLException {
        try {
            this.acquire(true);
            this.cancelDelegationToken();
        }
        finally {
            try {
                super.close();
            }
            finally {
                try {
                    FileSystem.closeAllForUGI(this.sessionUgi);
                }
                catch (IOException ioe) {
                    throw new HiveSQLException("Could not clean up file-system handles for UGI: " + this.sessionUgi, ioe);
                }
            }
        }
    }
    
    private void setDelegationToken(final String delegationTokenStr) throws HiveSQLException {
        this.delegationTokenStr = delegationTokenStr;
        if (delegationTokenStr != null) {
            this.getHiveConf().set("hive.metastore.token.signature", "HiveServer2ImpersonationToken");
            try {
                Utils.setTokenStr(this.sessionUgi, delegationTokenStr, "HiveServer2ImpersonationToken");
            }
            catch (IOException e) {
                throw new HiveSQLException("Couldn't setup delegation token in the ugi", e);
            }
        }
    }
    
    private void cancelDelegationToken() throws HiveSQLException {
        if (this.delegationTokenStr != null) {
            try {
                Hive.get(this.getHiveConf()).cancelDelegationToken(this.delegationTokenStr);
            }
            catch (HiveException e) {
                throw new HiveSQLException("Couldn't cancel delegation token", (Throwable)e);
            }
            Hive.closeCurrent();
        }
    }
    
    @Override
    protected HiveSession getSession() {
        assert this.proxySession != null;
        return this.proxySession;
    }
    
    public void setProxySession(final HiveSession proxySession) {
        this.proxySession = proxySession;
    }
    
    @Override
    public String getDelegationToken(final HiveAuthFactory authFactory, final String owner, final String renewer) throws HiveSQLException {
        return authFactory.getDelegationToken(owner, renewer);
    }
    
    @Override
    public void cancelDelegationToken(final HiveAuthFactory authFactory, final String tokenStr) throws HiveSQLException {
        authFactory.cancelDelegationToken(tokenStr);
    }
    
    @Override
    public void renewDelegationToken(final HiveAuthFactory authFactory, final String tokenStr) throws HiveSQLException {
        authFactory.renewDelegationToken(tokenStr);
    }
    
    static {
        LOG = LogFactory.getLog(HiveSessionImplwithUGI.class);
    }
}
