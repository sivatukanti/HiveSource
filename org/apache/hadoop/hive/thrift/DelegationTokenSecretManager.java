// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.thrift;

import org.apache.hadoop.security.token.TokenIdentifier;
import java.io.DataInput;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import org.apache.hadoop.security.token.SecretManager;
import org.apache.hadoop.io.Text;
import java.io.IOException;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenSecretManager;

public class DelegationTokenSecretManager extends AbstractDelegationTokenSecretManager<DelegationTokenIdentifier>
{
    public DelegationTokenSecretManager(final long delegationKeyUpdateInterval, final long delegationTokenMaxLifetime, final long delegationTokenRenewInterval, final long delegationTokenRemoverScanInterval) {
        super(delegationKeyUpdateInterval, delegationTokenMaxLifetime, delegationTokenRenewInterval, delegationTokenRemoverScanInterval);
    }
    
    @Override
    public DelegationTokenIdentifier createIdentifier() {
        return new DelegationTokenIdentifier();
    }
    
    public synchronized void cancelDelegationToken(final String tokenStrForm) throws IOException {
        final Token<DelegationTokenIdentifier> t = new Token<DelegationTokenIdentifier>();
        t.decodeFromUrlString(tokenStrForm);
        final String user = UserGroupInformation.getCurrentUser().getUserName();
        this.cancelToken(t, user);
    }
    
    public synchronized long renewDelegationToken(final String tokenStrForm) throws IOException {
        final Token<DelegationTokenIdentifier> t = new Token<DelegationTokenIdentifier>();
        t.decodeFromUrlString(tokenStrForm);
        final String user = UserGroupInformation.getCurrentUser().getUserName();
        return this.renewToken(t, user);
    }
    
    public synchronized String getDelegationToken(final String renewer) throws IOException {
        final UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
        final Text owner = new Text(ugi.getUserName());
        Text realUser = null;
        if (ugi.getRealUser() != null) {
            realUser = new Text(ugi.getRealUser().getUserName());
        }
        final DelegationTokenIdentifier ident = new DelegationTokenIdentifier(owner, new Text(renewer), realUser);
        final Token<DelegationTokenIdentifier> t = new Token<DelegationTokenIdentifier>(ident, this);
        return t.encodeToUrlString();
    }
    
    public String getUserFromToken(final String tokenStr) throws IOException {
        final Token<DelegationTokenIdentifier> delegationToken = new Token<DelegationTokenIdentifier>();
        delegationToken.decodeFromUrlString(tokenStr);
        final ByteArrayInputStream buf = new ByteArrayInputStream(delegationToken.getIdentifier());
        final DataInputStream in = new DataInputStream(buf);
        final DelegationTokenIdentifier id = this.createIdentifier();
        id.readFields(in);
        return id.getUser().getShortUserName();
    }
}
