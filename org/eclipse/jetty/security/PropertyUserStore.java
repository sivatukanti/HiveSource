// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security;

import org.eclipse.jetty.util.log.Log;
import java.util.EventListener;
import java.security.Principal;
import java.util.Iterator;
import java.util.Set;
import java.util.Collection;
import javax.security.auth.Subject;
import org.eclipse.jetty.util.security.Credential;
import org.eclipse.jetty.util.StringUtil;
import java.util.HashSet;
import java.util.Properties;
import java.io.IOException;
import org.eclipse.jetty.util.resource.PathResource;
import java.io.File;
import java.util.HashMap;
import java.util.ArrayList;
import org.eclipse.jetty.server.UserIdentity;
import java.util.Map;
import java.util.List;
import org.eclipse.jetty.util.resource.Resource;
import java.nio.file.Path;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.PathWatcher;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

public class PropertyUserStore extends AbstractLifeCycle implements PathWatcher.Listener
{
    private static final Logger LOG;
    private Path _configPath;
    private Resource _configResource;
    private PathWatcher pathWatcher;
    private boolean hotReload;
    private IdentityService _identityService;
    private boolean _firstLoad;
    private final List<String> _knownUsers;
    private final Map<String, UserIdentity> _knownUserIdentities;
    private List<UserListener> _listeners;
    
    public PropertyUserStore() {
        this.hotReload = false;
        this._identityService = new DefaultIdentityService();
        this._firstLoad = true;
        this._knownUsers = new ArrayList<String>();
        this._knownUserIdentities = new HashMap<String, UserIdentity>();
    }
    
    @Deprecated
    public String getConfig() {
        if (this._configPath != null) {
            return this._configPath.toString();
        }
        return null;
    }
    
    public void setConfig(final String config) {
        try {
            final Resource configResource = Resource.newResource(config);
            if (configResource.getFile() == null) {
                throw new IllegalArgumentException(config + " is not a file");
            }
            this.setConfigPath(configResource.getFile());
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
    
    public Path getConfigPath() {
        return this._configPath;
    }
    
    public void setConfigPath(final String configFile) {
        if (configFile == null) {
            this._configPath = null;
        }
        else {
            this._configPath = new File(configFile).toPath();
        }
    }
    
    public void setConfigPath(final File configFile) {
        if (configFile == null) {
            this._configPath = null;
            return;
        }
        this._configPath = configFile.toPath();
    }
    
    public void setConfigPath(final Path configPath) {
        this._configPath = configPath;
    }
    
    public UserIdentity getUserIdentity(final String userName) {
        return this._knownUserIdentities.get(userName);
    }
    
    public Resource getConfigResource() throws IOException {
        if (this._configResource == null) {
            this._configResource = new PathResource(this._configPath);
        }
        return this._configResource;
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
    public String toString() {
        final StringBuilder s = new StringBuilder();
        s.append(this.getClass().getName());
        s.append("[");
        s.append("users.count=").append(this._knownUsers.size());
        s.append("identityService=").append(this._identityService);
        s.append("]");
        return s.toString();
    }
    
    private void loadUsers() throws IOException {
        if (this._configPath == null) {
            return;
        }
        if (PropertyUserStore.LOG.isDebugEnabled()) {
            PropertyUserStore.LOG.debug("Loading " + this + " from " + this._configPath, new Object[0]);
        }
        final Properties properties = new Properties();
        if (this.getConfigResource().exists()) {
            properties.load(this.getConfigResource().getInputStream());
        }
        final Set<String> known = new HashSet<String>();
        for (final Map.Entry<Object, Object> entry : properties.entrySet()) {
            final String username = entry.getKey().trim();
            String credentials = entry.getValue().trim();
            String roles = null;
            final int c = credentials.indexOf(44);
            if (c > 0) {
                roles = credentials.substring(c + 1).trim();
                credentials = credentials.substring(0, c).trim();
            }
            if (username != null && username.length() > 0 && credentials != null && credentials.length() > 0) {
                String[] roleArray = IdentityService.NO_ROLES;
                if (roles != null && roles.length() > 0) {
                    roleArray = StringUtil.csvSplit(roles);
                }
                known.add(username);
                final Credential credential = Credential.getCredential(credentials);
                final Principal userPrincipal = new MappedLoginService.KnownUser(username, credential);
                final Subject subject = new Subject();
                subject.getPrincipals().add(userPrincipal);
                subject.getPrivateCredentials().add(credential);
                if (roles != null) {
                    for (final String role : roleArray) {
                        subject.getPrincipals().add(new MappedLoginService.RolePrincipal(role));
                    }
                }
                subject.setReadOnly();
                this._knownUserIdentities.put(username, this._identityService.newUserIdentity(subject, userPrincipal, roleArray));
                this.notifyUpdate(username, credential, roleArray);
            }
        }
        synchronized (this._knownUsers) {
            if (!this._firstLoad) {
                for (final String user : this._knownUsers) {
                    if (!known.contains(user)) {
                        this._knownUserIdentities.remove(user);
                        this.notifyRemove(user);
                    }
                }
            }
            this._knownUsers.clear();
            this._knownUsers.addAll(known);
        }
        this._firstLoad = false;
        if (PropertyUserStore.LOG.isDebugEnabled()) {
            PropertyUserStore.LOG.debug("Loaded " + this + " from " + this._configPath, new Object[0]);
        }
    }
    
    @Override
    protected void doStart() throws Exception {
        super.doStart();
        this.loadUsers();
        if (this.isHotReload() && this._configPath != null) {
            (this.pathWatcher = new PathWatcher()).watch(this._configPath);
            this.pathWatcher.addListener(this);
            this.pathWatcher.setNotifyExistingOnStart(false);
            this.pathWatcher.start();
        }
    }
    
    @Override
    public void onPathWatchEvent(final PathWatcher.PathWatchEvent event) {
        try {
            this.loadUsers();
        }
        catch (IOException e) {
            PropertyUserStore.LOG.warn(e);
        }
    }
    
    @Override
    protected void doStop() throws Exception {
        super.doStop();
        if (this.pathWatcher != null) {
            this.pathWatcher.stop();
        }
        this.pathWatcher = null;
    }
    
    private void notifyUpdate(final String username, final Credential credential, final String[] roleArray) {
        if (this._listeners != null) {
            final Iterator<UserListener> i = this._listeners.iterator();
            while (i.hasNext()) {
                i.next().update(username, credential, roleArray);
            }
        }
    }
    
    private void notifyRemove(final String username) {
        if (this._listeners != null) {
            final Iterator<UserListener> i = this._listeners.iterator();
            while (i.hasNext()) {
                i.next().remove(username);
            }
        }
    }
    
    public void registerUserListener(final UserListener listener) {
        if (this._listeners == null) {
            this._listeners = new ArrayList<UserListener>();
        }
        this._listeners.add(listener);
    }
    
    static {
        LOG = Log.getLogger(PropertyUserStore.class);
    }
    
    public interface UserListener
    {
        void update(final String p0, final Credential p1, final String[] p2);
        
        void remove(final String p0);
    }
}
