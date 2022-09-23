// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security;

import java.io.Serializable;
import org.eclipse.jetty.util.log.Log;
import java.io.IOException;
import javax.servlet.ServletRequest;
import java.security.Principal;
import javax.security.auth.Subject;
import org.eclipse.jetty.util.security.Credential;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jetty.server.UserIdentity;
import java.util.concurrent.ConcurrentMap;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

public abstract class MappedLoginService extends AbstractLifeCycle implements LoginService
{
    private static final Logger LOG;
    protected IdentityService _identityService;
    protected String _name;
    protected final ConcurrentMap<String, UserIdentity> _users;
    
    protected MappedLoginService() {
        this._identityService = new DefaultIdentityService();
        this._users = new ConcurrentHashMap<String, UserIdentity>();
    }
    
    @Override
    public String getName() {
        return this._name;
    }
    
    @Override
    public IdentityService getIdentityService() {
        return this._identityService;
    }
    
    public ConcurrentMap<String, UserIdentity> getUsers() {
        return this._users;
    }
    
    @Override
    public void setIdentityService(final IdentityService identityService) {
        if (this.isRunning()) {
            throw new IllegalStateException("Running");
        }
        this._identityService = identityService;
    }
    
    public void setName(final String name) {
        if (this.isRunning()) {
            throw new IllegalStateException("Running");
        }
        this._name = name;
    }
    
    public void setUsers(final Map<String, UserIdentity> users) {
        if (this.isRunning()) {
            throw new IllegalStateException("Running");
        }
        this._users.clear();
        this._users.putAll((Map<?, ?>)users);
    }
    
    @Override
    protected void doStart() throws Exception {
        this.loadUsers();
        super.doStart();
    }
    
    @Override
    protected void doStop() throws Exception {
        super.doStop();
    }
    
    @Override
    public void logout(final UserIdentity identity) {
        MappedLoginService.LOG.debug("logout {}", identity);
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[" + this._name + "]";
    }
    
    protected synchronized UserIdentity putUser(final String userName, final Object info) {
        UserIdentity identity;
        if (info instanceof UserIdentity) {
            identity = (UserIdentity)info;
        }
        else {
            final Credential credential = (Credential)((info instanceof Credential) ? info : Credential.getCredential(info.toString()));
            final Principal userPrincipal = new KnownUser(userName, credential);
            final Subject subject = new Subject();
            subject.getPrincipals().add(userPrincipal);
            subject.getPrivateCredentials().add(credential);
            subject.setReadOnly();
            identity = this._identityService.newUserIdentity(subject, userPrincipal, IdentityService.NO_ROLES);
        }
        this._users.put(userName, identity);
        return identity;
    }
    
    public synchronized UserIdentity putUser(final String userName, final Credential credential, final String[] roles) {
        final Principal userPrincipal = new KnownUser(userName, credential);
        final Subject subject = new Subject();
        subject.getPrincipals().add(userPrincipal);
        subject.getPrivateCredentials().add(credential);
        if (roles != null) {
            for (final String role : roles) {
                subject.getPrincipals().add(new RolePrincipal(role));
            }
        }
        subject.setReadOnly();
        final UserIdentity identity = this._identityService.newUserIdentity(subject, userPrincipal, roles);
        this._users.put(userName, identity);
        return identity;
    }
    
    public synchronized UserIdentity putUser(final KnownUser userPrincipal, final String[] roles) {
        final Subject subject = new Subject();
        subject.getPrincipals().add(userPrincipal);
        subject.getPrivateCredentials().add(userPrincipal._credential);
        if (roles != null) {
            for (final String role : roles) {
                subject.getPrincipals().add(new RolePrincipal(role));
            }
        }
        subject.setReadOnly();
        final UserIdentity identity = this._identityService.newUserIdentity(subject, userPrincipal, roles);
        this._users.put(userPrincipal._name, identity);
        return identity;
    }
    
    public void removeUser(final String username) {
        this._users.remove(username);
    }
    
    @Override
    public UserIdentity login(final String username, final Object credentials, final ServletRequest request) {
        if (username == null) {
            return null;
        }
        UserIdentity user = this._users.get(username);
        if (user == null) {
            final KnownUser userPrincipal = this.loadUserInfo(username);
            if (userPrincipal != null && userPrincipal.authenticate(credentials)) {
                final String[] roles = this.loadRoleInfo(userPrincipal);
                user = this.putUser(userPrincipal, roles);
                return user;
            }
        }
        else {
            final UserPrincipal principal = (UserPrincipal)user.getUserPrincipal();
            if (principal.authenticate(credentials)) {
                return user;
            }
        }
        return null;
    }
    
    @Override
    public boolean validate(final UserIdentity user) {
        return this._users.containsKey(user.getUserPrincipal().getName()) || this.loadUser(user.getUserPrincipal().getName()) != null;
    }
    
    protected abstract String[] loadRoleInfo(final KnownUser p0);
    
    protected abstract KnownUser loadUserInfo(final String p0);
    
    protected abstract UserIdentity loadUser(final String p0);
    
    protected abstract void loadUsers() throws IOException;
    
    static {
        LOG = Log.getLogger(MappedLoginService.class);
    }
    
    public static class RolePrincipal implements Principal, Serializable
    {
        private static final long serialVersionUID = 2998397924051854402L;
        private final String _roleName;
        
        public RolePrincipal(final String name) {
            this._roleName = name;
        }
        
        @Override
        public String getName() {
            return this._roleName;
        }
    }
    
    public static class Anonymous implements UserPrincipal, Serializable
    {
        private static final long serialVersionUID = 1097640442553284845L;
        
        @Override
        public boolean isAuthenticated() {
            return false;
        }
        
        @Override
        public String getName() {
            return "Anonymous";
        }
        
        @Override
        public boolean authenticate(final Object credentials) {
            return false;
        }
    }
    
    public static class KnownUser implements UserPrincipal, Serializable
    {
        private static final long serialVersionUID = -6226920753748399662L;
        private final String _name;
        private final Credential _credential;
        
        public KnownUser(final String name, final Credential credential) {
            this._name = name;
            this._credential = credential;
        }
        
        @Override
        public boolean authenticate(final Object credentials) {
            return this._credential != null && this._credential.check(credentials);
        }
        
        @Override
        public String getName() {
            return this._name;
        }
        
        @Override
        public boolean isAuthenticated() {
            return true;
        }
        
        @Override
        public String toString() {
            return this._name;
        }
    }
    
    public interface UserPrincipal extends Principal, Serializable
    {
        boolean authenticate(final Object p0);
        
        boolean isAuthenticated();
    }
}
