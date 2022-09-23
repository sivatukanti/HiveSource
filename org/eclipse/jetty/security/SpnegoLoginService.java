// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security;

import org.eclipse.jetty.util.log.Log;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.GSSException;
import java.security.Principal;
import javax.security.auth.Subject;
import org.ietf.jgss.Oid;
import org.ietf.jgss.GSSManager;
import org.eclipse.jetty.util.B64Code;
import org.eclipse.jetty.server.UserIdentity;
import javax.servlet.ServletRequest;
import org.eclipse.jetty.util.resource.Resource;
import java.util.Properties;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

public class SpnegoLoginService extends AbstractLifeCycle implements LoginService
{
    private static final Logger LOG;
    protected IdentityService _identityService;
    protected String _name;
    private String _config;
    private String _targetName;
    
    public SpnegoLoginService() {
    }
    
    public SpnegoLoginService(final String name) {
        this.setName(name);
    }
    
    public SpnegoLoginService(final String name, final String config) {
        this.setName(name);
        this.setConfig(config);
    }
    
    @Override
    public String getName() {
        return this._name;
    }
    
    public void setName(final String name) {
        if (this.isRunning()) {
            throw new IllegalStateException("Running");
        }
        this._name = name;
    }
    
    public String getConfig() {
        return this._config;
    }
    
    public void setConfig(final String config) {
        if (this.isRunning()) {
            throw new IllegalStateException("Running");
        }
        this._config = config;
    }
    
    @Override
    protected void doStart() throws Exception {
        final Properties properties = new Properties();
        final Resource resource = Resource.newResource(this._config);
        properties.load(resource.getInputStream());
        this._targetName = properties.getProperty("targetName");
        SpnegoLoginService.LOG.debug("Target Name {}", this._targetName);
        super.doStart();
    }
    
    @Override
    public UserIdentity login(final String username, final Object credentials, final ServletRequest request) {
        final String encodedAuthToken = (String)credentials;
        byte[] authToken = B64Code.decode(encodedAuthToken);
        final GSSManager manager = GSSManager.getInstance();
        try {
            final Oid krb5Oid = new Oid("1.3.6.1.5.5.2");
            final GSSName gssName = manager.createName(this._targetName, null);
            final GSSCredential serverCreds = manager.createCredential(gssName, Integer.MAX_VALUE, krb5Oid, 2);
            final GSSContext gContext = manager.createContext(serverCreds);
            if (gContext == null) {
                SpnegoLoginService.LOG.debug("SpnegoUserRealm: failed to establish GSSContext", new Object[0]);
            }
            else {
                while (!gContext.isEstablished()) {
                    authToken = gContext.acceptSecContext(authToken, 0, authToken.length);
                }
                if (gContext.isEstablished()) {
                    final String clientName = gContext.getSrcName().toString();
                    final String role = clientName.substring(clientName.indexOf(64) + 1);
                    SpnegoLoginService.LOG.debug("SpnegoUserRealm: established a security context", new Object[0]);
                    SpnegoLoginService.LOG.debug("Client Principal is: " + gContext.getSrcName(), new Object[0]);
                    SpnegoLoginService.LOG.debug("Server Principal is: " + gContext.getTargName(), new Object[0]);
                    SpnegoLoginService.LOG.debug("Client Default Role: " + role, new Object[0]);
                    final SpnegoUserPrincipal user = new SpnegoUserPrincipal(clientName, authToken);
                    final Subject subject = new Subject();
                    subject.getPrincipals().add(user);
                    return this._identityService.newUserIdentity(subject, user, new String[] { role });
                }
            }
        }
        catch (GSSException gsse) {
            SpnegoLoginService.LOG.warn(gsse);
        }
        return null;
    }
    
    @Override
    public boolean validate(final UserIdentity user) {
        return false;
    }
    
    @Override
    public IdentityService getIdentityService() {
        return this._identityService;
    }
    
    @Override
    public void setIdentityService(final IdentityService service) {
        this._identityService = service;
    }
    
    @Override
    public void logout(final UserIdentity user) {
    }
    
    static {
        LOG = Log.getLogger(SpnegoLoginService.class);
    }
}
