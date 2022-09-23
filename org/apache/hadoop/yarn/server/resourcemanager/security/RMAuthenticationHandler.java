// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.security;

import java.io.DataInput;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.authentication.client.AuthenticationException;
import org.apache.hadoop.yarn.security.client.RMDelegationTokenIdentifier;
import org.apache.hadoop.security.token.Token;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.hadoop.security.authentication.server.AuthenticationToken;
import org.apache.hadoop.security.authentication.server.KerberosAuthenticationHandler;

public class RMAuthenticationHandler extends KerberosAuthenticationHandler
{
    public static final String TYPE = "kerberos-dt";
    public static final String HEADER = "Hadoop-YARN-Auth-Delegation-Token";
    static RMDelegationTokenSecretManager secretManager;
    static boolean secretManagerInitialized;
    
    @Override
    public String getType() {
        return "kerberos-dt";
    }
    
    @Override
    public boolean managementOperation(final AuthenticationToken token, final HttpServletRequest request, final HttpServletResponse response) {
        return true;
    }
    
    @Override
    public AuthenticationToken authenticate(final HttpServletRequest request, final HttpServletResponse response) throws IOException, AuthenticationException {
        final String delegationParam = this.getEncodedDelegationTokenFromRequest(request);
        AuthenticationToken token;
        if (delegationParam != null) {
            final Token<RMDelegationTokenIdentifier> dt = new Token<RMDelegationTokenIdentifier>();
            dt.decodeFromUrlString(delegationParam);
            final UserGroupInformation ugi = this.verifyToken(dt);
            if (ugi == null) {
                throw new AuthenticationException("Invalid token");
            }
            final String shortName = ugi.getShortUserName();
            token = new AuthenticationToken(shortName, ugi.getUserName(), this.getType());
        }
        else {
            token = super.authenticate(request, response);
            if (token != null) {
                token = new AuthenticationToken(token.getUserName(), token.getName(), super.getType());
            }
        }
        return token;
    }
    
    protected UserGroupInformation verifyToken(final Token<RMDelegationTokenIdentifier> token) throws IOException {
        if (!RMAuthenticationHandler.secretManagerInitialized) {
            throw new IllegalStateException("Secret manager not initialized");
        }
        final ByteArrayInputStream buf = new ByteArrayInputStream(token.getIdentifier());
        final DataInputStream dis = new DataInputStream(buf);
        final RMDelegationTokenIdentifier id = RMAuthenticationHandler.secretManager.createIdentifier();
        try {
            id.readFields(dis);
            RMAuthenticationHandler.secretManager.verifyToken(id, token.getPassword());
        }
        catch (Throwable t) {
            return null;
        }
        finally {
            dis.close();
        }
        return id.getUser();
    }
    
    protected String getEncodedDelegationTokenFromRequest(final HttpServletRequest req) {
        final String header = req.getHeader("Hadoop-YARN-Auth-Delegation-Token");
        return header;
    }
    
    public static void setSecretManager(final RMDelegationTokenSecretManager manager) {
        RMAuthenticationHandler.secretManager = manager;
        RMAuthenticationHandler.secretManagerInitialized = true;
    }
    
    static {
        RMAuthenticationHandler.secretManagerInitialized = false;
    }
}
