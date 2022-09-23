// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.security;

import java.util.List;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import org.mortbay.jetty.Response;
import java.io.PrintStream;
import java.util.HashSet;
import org.mortbay.jetty.Request;
import java.security.Principal;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Map;
import java.util.Properties;
import org.mortbay.log.Log;
import java.io.IOException;
import org.mortbay.util.Scanner;
import java.util.HashMap;
import org.mortbay.resource.Resource;
import org.mortbay.component.AbstractLifeCycle;

public class HashUserRealm extends AbstractLifeCycle implements UserRealm, SSORealm
{
    public static final String __SSO = "org.mortbay.http.SSO";
    private String _realmName;
    private String _config;
    private Resource _configResource;
    protected HashMap _users;
    protected HashMap _roles;
    private SSORealm _ssoRealm;
    private Scanner _scanner;
    private int _refreshInterval;
    
    public HashUserRealm() {
        this._users = new HashMap();
        this._roles = new HashMap(7);
        this._refreshInterval = 0;
    }
    
    public HashUserRealm(final String name) {
        this._users = new HashMap();
        this._roles = new HashMap(7);
        this._refreshInterval = 0;
        this._realmName = name;
    }
    
    public HashUserRealm(final String name, final String config) throws IOException {
        this._users = new HashMap();
        this._roles = new HashMap(7);
        this._refreshInterval = 0;
        this._realmName = name;
        this.setConfig(config);
    }
    
    public String getConfig() {
        return this._config;
    }
    
    public Resource getConfigResource() {
        return this._configResource;
    }
    
    public void setConfig(final String config) throws IOException {
        this._config = config;
        this._configResource = Resource.newResource(this._config);
        this.loadConfig();
    }
    
    public void setRefreshInterval(final int msec) {
        this._refreshInterval = msec;
    }
    
    public int getRefreshInterval() {
        return this._refreshInterval;
    }
    
    protected void loadConfig() throws IOException {
        synchronized (this) {
            this._users.clear();
            this._roles.clear();
            if (Log.isDebugEnabled()) {
                Log.debug("Load " + this + " from " + this._config);
            }
            final Properties properties = new Properties();
            properties.load(this._configResource.getInputStream());
            for (final Map.Entry entry : properties.entrySet()) {
                final String username = entry.getKey().toString().trim();
                String credentials = entry.getValue().toString().trim();
                String roles = null;
                final int c = credentials.indexOf(44);
                if (c > 0) {
                    roles = credentials.substring(c + 1).trim();
                    credentials = credentials.substring(0, c).trim();
                }
                if (username != null && username.length() > 0 && credentials != null && credentials.length() > 0) {
                    this.put(username, credentials);
                    if (roles == null || roles.length() <= 0) {
                        continue;
                    }
                    final StringTokenizer tok = new StringTokenizer(roles, ", ");
                    while (tok.hasMoreTokens()) {
                        this.addUserToRole(username, tok.nextToken());
                    }
                }
            }
        }
    }
    
    public void setName(final String name) {
        this._realmName = name;
    }
    
    public String getName() {
        return this._realmName;
    }
    
    public Principal getPrincipal(final String username) {
        return this._users.get(username);
    }
    
    public Principal authenticate(final String username, final Object credentials, final Request request) {
        final KnownUser user;
        synchronized (this) {
            user = this._users.get(username);
        }
        if (user == null) {
            return null;
        }
        if (user.authenticate(credentials)) {
            return user;
        }
        return null;
    }
    
    public void disassociate(final Principal user) {
    }
    
    public Principal pushRole(Principal user, final String role) {
        if (user == null) {
            user = new User();
        }
        return new WrappedUser(user, role);
    }
    
    public Principal popRole(final Principal user) {
        final WrappedUser wu = (WrappedUser)user;
        return wu.getUserPrincipal();
    }
    
    public synchronized Object put(final Object name, final Object credentials) {
        if (credentials instanceof Principal) {
            return this._users.put(name.toString(), credentials);
        }
        if (credentials instanceof Password) {
            return this._users.put(name, new KnownUser(name.toString(), (Credential)credentials));
        }
        if (credentials != null) {
            return this._users.put(name, new KnownUser(name.toString(), Credential.getCredential(credentials.toString())));
        }
        return null;
    }
    
    public synchronized void addUserToRole(final String userName, final String roleName) {
        HashSet userSet = this._roles.get(roleName);
        if (userSet == null) {
            userSet = new HashSet(11);
            this._roles.put(roleName, userSet);
        }
        userSet.add(userName);
    }
    
    public boolean reauthenticate(final Principal user) {
        return ((User)user).isAuthenticated();
    }
    
    public synchronized boolean isUserInRole(final Principal user, final String roleName) {
        if (user instanceof WrappedUser) {
            return ((WrappedUser)user).isUserInRole(roleName);
        }
        if (user == null || !(user instanceof User) || ((User)user).getUserRealm() != this) {
            return false;
        }
        final HashSet userSet = this._roles.get(roleName);
        return userSet != null && userSet.contains(user.getName());
    }
    
    public void logout(final Principal user) {
    }
    
    public String toString() {
        return "Realm[" + this._realmName + "]==" + this._users.keySet();
    }
    
    public void dump(final PrintStream out) {
        out.println(this + ":");
        out.println(super.toString());
        out.println(this._roles);
    }
    
    public SSORealm getSSORealm() {
        return this._ssoRealm;
    }
    
    public void setSSORealm(final SSORealm ssoRealm) {
        this._ssoRealm = ssoRealm;
    }
    
    public Credential getSingleSignOn(final Request request, final Response response) {
        if (this._ssoRealm != null) {
            return this._ssoRealm.getSingleSignOn(request, response);
        }
        return null;
    }
    
    public void setSingleSignOn(final Request request, final Response response, final Principal principal, final Credential credential) {
        if (this._ssoRealm != null) {
            this._ssoRealm.setSingleSignOn(request, response, principal, credential);
        }
    }
    
    public void clearSingleSignOn(final String username) {
        if (this._ssoRealm != null) {
            this._ssoRealm.clearSingleSignOn(username);
        }
    }
    
    protected void doStart() throws Exception {
        super.doStart();
        if (this._scanner != null) {
            this._scanner.stop();
        }
        if (this.getRefreshInterval() > 0) {
            (this._scanner = new Scanner()).setScanInterval(this.getRefreshInterval());
            final List dirList = new ArrayList(1);
            dirList.add(this._configResource.getFile());
            this._scanner.setScanDirs(dirList);
            this._scanner.setFilenameFilter(new FilenameFilter() {
                public boolean accept(final File dir, final String name) {
                    final File f = new File(dir, name);
                    try {
                        if (f.compareTo(HashUserRealm.this._configResource.getFile()) == 0) {
                            return true;
                        }
                    }
                    catch (IOException e) {
                        return false;
                    }
                    return false;
                }
            });
            this._scanner.addListener(new Scanner.BulkListener() {
                public void filesChanged(final List filenames) throws Exception {
                    if (filenames == null) {
                        return;
                    }
                    if (filenames.isEmpty()) {
                        return;
                    }
                    if (filenames.size() == 1 && filenames.get(0).equals(HashUserRealm.this._config)) {
                        HashUserRealm.this.loadConfig();
                    }
                }
                
                public String toString() {
                    return "HashUserRealm$Scanner";
                }
            });
            this._scanner.setReportExistingFilesOnStartup(false);
            this._scanner.setRecursive(false);
            this._scanner.start();
        }
    }
    
    protected void doStop() throws Exception {
        super.doStop();
        if (this._scanner != null) {
            this._scanner.stop();
        }
        this._scanner = null;
    }
    
    private class User implements Principal
    {
        List roles;
        
        private User() {
            this.roles = null;
        }
        
        private UserRealm getUserRealm() {
            return HashUserRealm.this;
        }
        
        public String getName() {
            return "Anonymous";
        }
        
        public boolean isAuthenticated() {
            return false;
        }
        
        public String toString() {
            return this.getName();
        }
    }
    
    private class KnownUser extends User
    {
        private String _userName;
        private Credential _cred;
        
        KnownUser(final String name, final Credential credential) {
            this._userName = name;
            this._cred = credential;
        }
        
        boolean authenticate(final Object credentials) {
            return this._cred != null && this._cred.check(credentials);
        }
        
        public String getName() {
            return this._userName;
        }
        
        public boolean isAuthenticated() {
            return true;
        }
    }
    
    private class WrappedUser extends User
    {
        private Principal user;
        private String role;
        
        WrappedUser(final Principal user, final String role) {
            this.user = user;
            this.role = role;
        }
        
        Principal getUserPrincipal() {
            return this.user;
        }
        
        public String getName() {
            return "role:" + this.role;
        }
        
        public boolean isAuthenticated() {
            return true;
        }
        
        public boolean isUserInRole(final String role) {
            return this.role.equals(role);
        }
    }
}
