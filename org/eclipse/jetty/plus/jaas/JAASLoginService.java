// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.plus.jaas;

import org.eclipse.jetty.util.log.Log;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.security.auth.login.LoginException;
import java.security.Principal;
import org.eclipse.jetty.util.Loader;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import org.eclipse.jetty.plus.jaas.callback.ObjectCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import org.eclipse.jetty.server.UserIdentity;
import org.eclipse.jetty.security.DefaultIdentityService;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import javax.security.auth.login.LoginContext;
import javax.security.auth.Subject;
import org.eclipse.jetty.security.IdentityService;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

public class JAASLoginService extends AbstractLifeCycle implements LoginService
{
    private static final Logger LOG;
    public static String DEFAULT_ROLE_CLASS_NAME;
    public static String[] DEFAULT_ROLE_CLASS_NAMES;
    protected String[] _roleClassNames;
    protected String _callbackHandlerClass;
    protected String _realmName;
    protected String _loginModuleName;
    protected JAASUserPrincipal _defaultUser;
    protected IdentityService _identityService;
    
    public JAASLoginService() {
        this._roleClassNames = JAASLoginService.DEFAULT_ROLE_CLASS_NAMES;
        this._defaultUser = new JAASUserPrincipal(null, null, null);
    }
    
    public JAASLoginService(final String name) {
        this();
        this._realmName = name;
        this._loginModuleName = name;
    }
    
    public String getName() {
        return this._realmName;
    }
    
    public void setName(final String name) {
        this._realmName = name;
    }
    
    public IdentityService getIdentityService() {
        return this._identityService;
    }
    
    public void setIdentityService(final IdentityService identityService) {
        this._identityService = identityService;
    }
    
    public void setLoginModuleName(final String name) {
        this._loginModuleName = name;
    }
    
    public void setCallbackHandlerClass(final String classname) {
        this._callbackHandlerClass = classname;
    }
    
    public void setRoleClassNames(final String[] classnames) {
        final ArrayList<String> tmp = new ArrayList<String>();
        if (classnames != null) {
            tmp.addAll(Arrays.asList(classnames));
        }
        if (!tmp.contains(JAASLoginService.DEFAULT_ROLE_CLASS_NAME)) {
            tmp.add(JAASLoginService.DEFAULT_ROLE_CLASS_NAME);
        }
        this._roleClassNames = tmp.toArray(new String[tmp.size()]);
    }
    
    public String[] getRoleClassNames() {
        return this._roleClassNames;
    }
    
    @Override
    protected void doStart() throws Exception {
        if (this._identityService == null) {
            this._identityService = new DefaultIdentityService();
        }
        super.doStart();
    }
    
    public UserIdentity login(final String username, final Object credentials) {
        try {
            CallbackHandler callbackHandler = null;
            if (this._callbackHandlerClass == null) {
                callbackHandler = new CallbackHandler() {
                    public void handle(final Callback[] callbacks) throws IOException, UnsupportedCallbackException {
                        for (final Callback callback : callbacks) {
                            if (callback instanceof NameCallback) {
                                ((NameCallback)callback).setName(username);
                            }
                            else if (callback instanceof PasswordCallback) {
                                ((PasswordCallback)callback).setPassword(credentials.toString().toCharArray());
                            }
                            else if (callback instanceof ObjectCallback) {
                                ((ObjectCallback)callback).setObject(credentials);
                            }
                        }
                    }
                };
            }
            else {
                final Class clazz = Loader.loadClass(this.getClass(), this._callbackHandlerClass);
                callbackHandler = clazz.newInstance();
            }
            final Subject subject = new Subject();
            final LoginContext loginContext = new LoginContext(this._loginModuleName, subject, callbackHandler);
            loginContext.login();
            final JAASUserPrincipal userPrincipal = new JAASUserPrincipal(this.getUserName(callbackHandler), subject, loginContext);
            subject.getPrincipals().add(userPrincipal);
            return this._identityService.newUserIdentity(subject, userPrincipal, this.getGroups(subject));
        }
        catch (LoginException e) {
            JAASLoginService.LOG.warn(e);
        }
        catch (IOException e2) {
            JAASLoginService.LOG.warn(e2);
        }
        catch (UnsupportedCallbackException e3) {
            JAASLoginService.LOG.warn(e3);
        }
        catch (InstantiationException e4) {
            JAASLoginService.LOG.warn(e4);
        }
        catch (IllegalAccessException e5) {
            JAASLoginService.LOG.warn(e5);
        }
        catch (ClassNotFoundException e6) {
            JAASLoginService.LOG.warn(e6);
        }
        return null;
    }
    
    public boolean validate(final UserIdentity user) {
        return true;
    }
    
    private String getUserName(final CallbackHandler callbackHandler) throws IOException, UnsupportedCallbackException {
        final NameCallback nameCallback = new NameCallback("foo");
        callbackHandler.handle(new Callback[] { nameCallback });
        return nameCallback.getName();
    }
    
    public void logout(final UserIdentity user) {
        final Set<JAASUserPrincipal> userPrincipals = user.getSubject().getPrincipals(JAASUserPrincipal.class);
        final LoginContext loginContext = userPrincipals.iterator().next().getLoginContext();
        try {
            loginContext.logout();
        }
        catch (LoginException e) {
            JAASLoginService.LOG.warn(e);
        }
    }
    
    private String[] getGroups(final Subject subject) {
        final String[] roleClassNames = this.getRoleClassNames();
        final Collection<String> groups = new LinkedHashSet<String>();
        try {
            for (final String roleClassName : roleClassNames) {
                final Class load_class = Thread.currentThread().getContextClassLoader().loadClass(roleClassName);
                final Set<Principal> rolesForType = subject.getPrincipals((Class<Principal>)load_class);
                for (final Principal principal : rolesForType) {
                    groups.add(principal.getName());
                }
            }
            return groups.toArray(new String[groups.size()]);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    static {
        LOG = Log.getLogger(JAASLoginService.class);
        JAASLoginService.DEFAULT_ROLE_CLASS_NAME = "org.eclipse.jetty.plus.jaas.JAASRole";
        JAASLoginService.DEFAULT_ROLE_CLASS_NAMES = new String[] { JAASLoginService.DEFAULT_ROLE_CLASS_NAME };
    }
}
