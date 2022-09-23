// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.security.Credential;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.io.IOException;
import org.eclipse.jetty.server.UserIdentity;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.log.Logger;

public class HashLoginService extends MappedLoginService implements PropertyUserStore.UserListener
{
    private static final Logger LOG;
    private PropertyUserStore _propertyUserStore;
    private String _config;
    private boolean hotReload;
    
    public HashLoginService() {
        this.hotReload = false;
    }
    
    public HashLoginService(final String name) {
        this.hotReload = false;
        this.setName(name);
    }
    
    public HashLoginService(final String name, final String config) {
        this.hotReload = false;
        this.setName(name);
        this.setConfig(config);
    }
    
    public String getConfig() {
        return this._config;
    }
    
    @Deprecated
    public Resource getConfigResource() {
        return null;
    }
    
    public void setConfig(final String config) {
        this._config = config;
    }
    
    public boolean isHotReload() {
        return this.hotReload;
    }
    
    public void setHotReload(final boolean enable) {
        if (this.isRunning()) {
            throw new IllegalStateException("Cannot set hot reload while user store is running");
        }
        this.hotReload = enable;
    }
    
    @Deprecated
    public void setRefreshInterval(final int sec) {
    }
    
    @Deprecated
    public int getRefreshInterval() {
        return this.hotReload ? 1 : 0;
    }
    
    @Override
    protected UserIdentity loadUser(final String username) {
        return null;
    }
    
    public void loadUsers() throws IOException {
    }
    
    @Override
    protected String[] loadRoleInfo(final KnownUser user) {
        final UserIdentity id = this._propertyUserStore.getUserIdentity(user.getName());
        if (id == null) {
            return null;
        }
        final Set<RolePrincipal> roles = id.getSubject().getPrincipals(RolePrincipal.class);
        if (roles == null) {
            return null;
        }
        final List<String> list = new ArrayList<String>();
        for (final RolePrincipal r : roles) {
            list.add(r.getName());
        }
        return list.toArray(new String[roles.size()]);
    }
    
    @Override
    protected KnownUser loadUserInfo(final String userName) {
        final UserIdentity id = this._propertyUserStore.getUserIdentity(userName);
        if (id != null) {
            return (KnownUser)id.getUserPrincipal();
        }
        return null;
    }
    
    @Override
    protected void doStart() throws Exception {
        super.doStart();
        if (this._propertyUserStore == null) {
            if (HashLoginService.LOG.isDebugEnabled()) {
                HashLoginService.LOG.debug("doStart: Starting new PropertyUserStore. PropertiesFile: " + this._config + " hotReload: " + this.hotReload, new Object[0]);
            }
            (this._propertyUserStore = new PropertyUserStore()).setHotReload(this.hotReload);
            this._propertyUserStore.setConfigPath(this._config);
            this._propertyUserStore.registerUserListener(this);
            this._propertyUserStore.start();
        }
    }
    
    @Override
    protected void doStop() throws Exception {
        super.doStop();
        if (this._propertyUserStore != null) {
            this._propertyUserStore.stop();
        }
        this._propertyUserStore = null;
    }
    
    @Override
    public void update(final String userName, final Credential credential, final String[] roleArray) {
        if (HashLoginService.LOG.isDebugEnabled()) {
            HashLoginService.LOG.debug("update: " + userName + " Roles: " + roleArray.length, new Object[0]);
        }
    }
    
    @Override
    public void remove(final String userName) {
        if (HashLoginService.LOG.isDebugEnabled()) {
            HashLoginService.LOG.debug("remove: " + userName, new Object[0]);
        }
        this.removeUser(userName);
    }
    
    static {
        LOG = Log.getLogger(HashLoginService.class);
    }
    
    public class HashKnownUser extends KnownUser
    {
        String[] _roles;
        
        public HashKnownUser(final String name, final Credential credential) {
            super(name, credential);
        }
        
        public void setRoles(final String[] roles) {
            this._roles = roles;
        }
        
        public String[] getRoles() {
            return this._roles;
        }
    }
}
