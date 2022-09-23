// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.plus.jaas.spi;

import org.eclipse.jetty.util.log.Log;
import java.util.Properties;
import java.util.Map;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.Subject;
import java.util.Hashtable;
import javax.naming.directory.InitialDirContext;
import javax.security.auth.callback.Callback;
import java.io.IOException;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.eclipse.jetty.plus.jaas.callback.ObjectCallback;
import javax.security.auth.callback.NameCallback;
import java.util.ArrayList;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.security.auth.login.LoginException;
import javax.naming.directory.SearchControls;
import java.util.List;
import org.eclipse.jetty.util.security.Credential;
import javax.naming.directory.DirContext;
import org.eclipse.jetty.util.log.Logger;

public class LdapLoginModule extends AbstractLoginModule
{
    private static final Logger LOG;
    private String _hostname;
    private int _port;
    private String _authenticationMethod;
    private String _contextFactory;
    private String _bindDn;
    private String _bindPassword;
    private String _userObjectClass;
    private String _userRdnAttribute;
    private String _userIdAttribute;
    private String _userPasswordAttribute;
    private String _userBaseDn;
    private String _roleBaseDn;
    private String _roleObjectClass;
    private String _roleMemberAttribute;
    private String _roleNameAttribute;
    private boolean _debug;
    private boolean _forceBindingLogin;
    private boolean _useLdaps;
    private DirContext _rootContext;
    
    public LdapLoginModule() {
        this._userObjectClass = "inetOrgPerson";
        this._userRdnAttribute = "uid";
        this._userIdAttribute = "cn";
        this._userPasswordAttribute = "userPassword";
        this._roleObjectClass = "groupOfUniqueNames";
        this._roleMemberAttribute = "uniqueMember";
        this._roleNameAttribute = "roleName";
        this._forceBindingLogin = false;
        this._useLdaps = false;
    }
    
    @Override
    public UserInfo getUserInfo(final String username) throws Exception {
        String pwdCredential = this.getUserCredentials(username);
        if (pwdCredential == null) {
            return null;
        }
        pwdCredential = convertCredentialLdapToJetty(pwdCredential);
        final Credential credential = Credential.getCredential(pwdCredential);
        final List<String> roles = this.getUserRoles(this._rootContext, username);
        return new UserInfo(username, credential, roles);
    }
    
    protected String doRFC2254Encoding(final String inputString) {
        final StringBuffer buf = new StringBuffer(inputString.length());
        for (int i = 0; i < inputString.length(); ++i) {
            final char c = inputString.charAt(i);
            switch (c) {
                case '\\': {
                    buf.append("\\5c");
                    break;
                }
                case '*': {
                    buf.append("\\2a");
                    break;
                }
                case '(': {
                    buf.append("\\28");
                    break;
                }
                case ')': {
                    buf.append("\\29");
                    break;
                }
                case '\0': {
                    buf.append("\\00");
                    break;
                }
                default: {
                    buf.append(c);
                    break;
                }
            }
        }
        return buf.toString();
    }
    
    private String getUserCredentials(final String username) throws LoginException {
        String ldapCredential = null;
        final SearchControls ctls = new SearchControls();
        ctls.setCountLimit(1L);
        ctls.setDerefLinkFlag(true);
        ctls.setSearchScope(2);
        final String filter = "(&(objectClass={0})({1}={2}))";
        LdapLoginModule.LOG.debug("Searching for users with filter: '" + filter + "'" + " from base dn: " + this._userBaseDn, new Object[0]);
        try {
            final Object[] filterArguments = { this._userObjectClass, this._userIdAttribute, username };
            final NamingEnumeration<SearchResult> results = this._rootContext.search(this._userBaseDn, filter, filterArguments, ctls);
            LdapLoginModule.LOG.debug("Found user?: " + results.hasMoreElements(), new Object[0]);
            if (!results.hasMoreElements()) {
                throw new LoginException("User not found.");
            }
            final SearchResult result = this.findUser(username);
            final Attributes attributes = result.getAttributes();
            final Attribute attribute = attributes.get(this._userPasswordAttribute);
            if (attribute != null) {
                try {
                    final byte[] value = (byte[])attribute.get();
                    ldapCredential = new String(value);
                }
                catch (NamingException e) {
                    LdapLoginModule.LOG.debug("no password available under attribute: " + this._userPasswordAttribute, new Object[0]);
                }
            }
        }
        catch (NamingException e2) {
            throw new LoginException("Root context binding failure.");
        }
        LdapLoginModule.LOG.debug("user cred is: " + ldapCredential, new Object[0]);
        return ldapCredential;
    }
    
    private List<String> getUserRoles(final DirContext dirContext, final String username) throws LoginException, NamingException {
        final String userDn = this._userRdnAttribute + "=" + username + "," + this._userBaseDn;
        return this.getUserRolesByDn(dirContext, userDn);
    }
    
    private List<String> getUserRolesByDn(final DirContext dirContext, final String userDn) throws LoginException, NamingException {
        final List<String> roleList = new ArrayList<String>();
        if (dirContext == null || this._roleBaseDn == null || this._roleMemberAttribute == null || this._roleObjectClass == null) {
            return roleList;
        }
        final SearchControls ctls = new SearchControls();
        ctls.setDerefLinkFlag(true);
        ctls.setSearchScope(2);
        final String filter = "(&(objectClass={0})({1}={2}))";
        final Object[] filterArguments = { this._roleObjectClass, this._roleMemberAttribute, userDn };
        final NamingEnumeration<SearchResult> results = dirContext.search(this._roleBaseDn, filter, filterArguments, ctls);
        LdapLoginModule.LOG.debug("Found user roles?: " + results.hasMoreElements(), new Object[0]);
        while (results.hasMoreElements()) {
            final SearchResult result = results.nextElement();
            final Attributes attributes = result.getAttributes();
            if (attributes == null) {
                continue;
            }
            final Attribute roleAttribute = attributes.get(this._roleNameAttribute);
            if (roleAttribute == null) {
                continue;
            }
            final NamingEnumeration<?> roles = roleAttribute.getAll();
            while (roles.hasMore()) {
                roleList.add(roles.next().toString());
            }
        }
        return roleList;
    }
    
    @Override
    public boolean login() throws LoginException {
        try {
            if (this.getCallbackHandler() == null) {
                throw new LoginException("No callback handler");
            }
            final Callback[] callbacks = this.configureCallbacks();
            this.getCallbackHandler().handle(callbacks);
            final String webUserName = ((NameCallback)callbacks[0]).getName();
            final Object webCredential = ((ObjectCallback)callbacks[1]).getObject();
            if (webUserName == null || webCredential == null) {
                this.setAuthenticated(false);
                return this.isAuthenticated();
            }
            if (this._forceBindingLogin) {
                return this.bindingLogin(webUserName, webCredential);
            }
            final UserInfo userInfo = this.getUserInfo(webUserName);
            if (userInfo == null) {
                this.setAuthenticated(false);
                return false;
            }
            this.setCurrentUser(new JAASUserInfo(userInfo));
            if (webCredential instanceof String) {
                return this.credentialLogin(Credential.getCredential((String)webCredential));
            }
            return this.credentialLogin(webCredential);
        }
        catch (UnsupportedCallbackException e3) {
            throw new LoginException("Error obtaining callback information.");
        }
        catch (IOException e) {
            if (this._debug) {
                e.printStackTrace();
            }
            throw new LoginException("IO Error performing login.");
        }
        catch (Exception e2) {
            if (this._debug) {
                e2.printStackTrace();
            }
            throw new LoginException("Error obtaining user info.");
        }
    }
    
    protected boolean credentialLogin(final Object webCredential) throws LoginException {
        this.setAuthenticated(this.getCurrentUser().checkCredential(webCredential));
        return this.isAuthenticated();
    }
    
    public boolean bindingLogin(final String username, final Object password) throws LoginException, NamingException {
        final SearchResult searchResult = this.findUser(username);
        final String userDn = searchResult.getNameInNamespace();
        LdapLoginModule.LOG.info("Attempting authentication: " + userDn, new Object[0]);
        final Hashtable<Object, Object> environment = this.getEnvironment();
        environment.put("java.naming.security.principal", userDn);
        environment.put("java.naming.security.credentials", password);
        final DirContext dirContext = new InitialDirContext(environment);
        final List<String> roles = this.getUserRolesByDn(dirContext, userDn);
        final UserInfo userInfo = new UserInfo(username, null, roles);
        this.setCurrentUser(new JAASUserInfo(userInfo));
        this.setAuthenticated(true);
        return true;
    }
    
    private SearchResult findUser(final String username) throws NamingException, LoginException {
        final SearchControls ctls = new SearchControls();
        ctls.setCountLimit(1L);
        ctls.setDerefLinkFlag(true);
        ctls.setSearchScope(2);
        final String filter = "(&(objectClass={0})({1}={2}))";
        LdapLoginModule.LOG.info("Searching for users with filter: '" + filter + "'" + " from base dn: " + this._userBaseDn, new Object[0]);
        final Object[] filterArguments = { this._userObjectClass, this._userIdAttribute, username };
        final NamingEnumeration<SearchResult> results = this._rootContext.search(this._userBaseDn, filter, filterArguments, ctls);
        LdapLoginModule.LOG.info("Found user?: " + results.hasMoreElements(), new Object[0]);
        if (!results.hasMoreElements()) {
            throw new LoginException("User not found.");
        }
        return results.nextElement();
    }
    
    @Override
    public void initialize(final Subject subject, final CallbackHandler callbackHandler, final Map<String, ?> sharedState, final Map<String, ?> options) {
        super.initialize(subject, callbackHandler, sharedState, options);
        this._hostname = (String)options.get("hostname");
        this._port = Integer.parseInt((String)options.get("port"));
        this._contextFactory = (String)options.get("contextFactory");
        this._bindDn = (String)options.get("bindDn");
        this._bindPassword = (String)options.get("bindPassword");
        this._authenticationMethod = (String)options.get("authenticationMethod");
        this._userBaseDn = (String)options.get("userBaseDn");
        this._roleBaseDn = (String)options.get("roleBaseDn");
        if (options.containsKey("forceBindingLogin")) {
            this._forceBindingLogin = Boolean.parseBoolean((String)options.get("forceBindingLogin"));
        }
        if (options.containsKey("useLdaps")) {
            this._useLdaps = Boolean.parseBoolean((String)options.get("useLdaps"));
        }
        this._userObjectClass = this.getOption(options, "userObjectClass", this._userObjectClass);
        this._userRdnAttribute = this.getOption(options, "userRdnAttribute", this._userRdnAttribute);
        this._userIdAttribute = this.getOption(options, "userIdAttribute", this._userIdAttribute);
        this._userPasswordAttribute = this.getOption(options, "userPasswordAttribute", this._userPasswordAttribute);
        this._roleObjectClass = this.getOption(options, "roleObjectClass", this._roleObjectClass);
        this._roleMemberAttribute = this.getOption(options, "roleMemberAttribute", this._roleMemberAttribute);
        this._roleNameAttribute = this.getOption(options, "roleNameAttribute", this._roleNameAttribute);
        this._debug = Boolean.parseBoolean(String.valueOf(this.getOption(options, "debug", Boolean.toString(this._debug))));
        try {
            this._rootContext = new InitialDirContext(this.getEnvironment());
        }
        catch (NamingException ex) {
            throw new IllegalStateException("Unable to establish root context", ex);
        }
    }
    
    @Override
    public boolean commit() throws LoginException {
        try {
            this._rootContext.close();
        }
        catch (NamingException e) {
            throw new LoginException("error closing root context: " + e.getMessage());
        }
        return super.commit();
    }
    
    @Override
    public boolean abort() throws LoginException {
        try {
            this._rootContext.close();
        }
        catch (NamingException e) {
            throw new LoginException("error closing root context: " + e.getMessage());
        }
        return super.abort();
    }
    
    private String getOption(final Map<String, ?> options, final String key, final String defaultValue) {
        final Object value = options.get(key);
        if (value == null) {
            return defaultValue;
        }
        return (String)value;
    }
    
    public Hashtable<Object, Object> getEnvironment() {
        final Properties env = new Properties();
        env.put("java.naming.factory.initial", this._contextFactory);
        if (this._hostname != null) {
            env.put("java.naming.provider.url", (this._useLdaps ? "ldaps://" : "ldap://") + this._hostname + ((this._port == 0) ? "" : (":" + this._port)) + "/");
        }
        if (this._authenticationMethod != null) {
            env.put("java.naming.security.authentication", this._authenticationMethod);
        }
        if (this._bindDn != null) {
            env.put("java.naming.security.principal", this._bindDn);
        }
        if (this._bindPassword != null) {
            env.put("java.naming.security.credentials", this._bindPassword);
        }
        return env;
    }
    
    public static String convertCredentialJettyToLdap(final String encryptedPassword) {
        if ("MD5:".startsWith(encryptedPassword.toUpperCase())) {
            return "{MD5}" + encryptedPassword.substring("MD5:".length(), encryptedPassword.length());
        }
        if ("CRYPT:".startsWith(encryptedPassword.toUpperCase())) {
            return "{CRYPT}" + encryptedPassword.substring("CRYPT:".length(), encryptedPassword.length());
        }
        return encryptedPassword;
    }
    
    public static String convertCredentialLdapToJetty(final String encryptedPassword) {
        if (encryptedPassword == null) {
            return encryptedPassword;
        }
        if ("{MD5}".startsWith(encryptedPassword.toUpperCase())) {
            return "MD5:" + encryptedPassword.substring("{MD5}".length(), encryptedPassword.length());
        }
        if ("{CRYPT}".startsWith(encryptedPassword.toUpperCase())) {
            return "CRYPT:" + encryptedPassword.substring("{CRYPT}".length(), encryptedPassword.length());
        }
        return encryptedPassword;
    }
    
    static {
        LOG = Log.getLogger(LdapLoginModule.class);
    }
}
