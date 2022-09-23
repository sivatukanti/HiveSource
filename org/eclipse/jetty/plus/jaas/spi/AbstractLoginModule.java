// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.plus.jaas.spi;

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import org.eclipse.jetty.plus.jaas.JAASPrincipal;
import org.eclipse.jetty.plus.jaas.JAASRole;
import java.util.List;
import java.security.Principal;
import java.util.Map;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import org.eclipse.jetty.plus.jaas.callback.ObjectCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.Callback;
import javax.security.auth.login.LoginException;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.spi.LoginModule;

public abstract class AbstractLoginModule implements LoginModule
{
    private CallbackHandler callbackHandler;
    private boolean authState;
    private boolean commitState;
    private JAASUserInfo currentUser;
    private Subject subject;
    
    public AbstractLoginModule() {
        this.authState = false;
        this.commitState = false;
    }
    
    public Subject getSubject() {
        return this.subject;
    }
    
    public void setSubject(final Subject s) {
        this.subject = s;
    }
    
    public JAASUserInfo getCurrentUser() {
        return this.currentUser;
    }
    
    public void setCurrentUser(final JAASUserInfo u) {
        this.currentUser = u;
    }
    
    public CallbackHandler getCallbackHandler() {
        return this.callbackHandler;
    }
    
    public void setCallbackHandler(final CallbackHandler h) {
        this.callbackHandler = h;
    }
    
    public boolean isAuthenticated() {
        return this.authState;
    }
    
    public boolean isCommitted() {
        return this.commitState;
    }
    
    public void setAuthenticated(final boolean authState) {
        this.authState = authState;
    }
    
    public void setCommitted(final boolean commitState) {
        this.commitState = commitState;
    }
    
    public boolean abort() throws LoginException {
        this.currentUser = null;
        return this.isAuthenticated() && this.isCommitted();
    }
    
    public boolean commit() throws LoginException {
        if (!this.isAuthenticated()) {
            this.currentUser = null;
            this.setCommitted(false);
            return false;
        }
        this.setCommitted(true);
        this.currentUser.setJAASInfo(this.subject);
        return true;
    }
    
    public Callback[] configureCallbacks() {
        final Callback[] callbacks = { new NameCallback("Enter user name"), new ObjectCallback() };
        return callbacks;
    }
    
    public abstract UserInfo getUserInfo(final String p0) throws Exception;
    
    public boolean login() throws LoginException {
        try {
            if (this.callbackHandler == null) {
                throw new LoginException("No callback handler");
            }
            final Callback[] callbacks = this.configureCallbacks();
            this.callbackHandler.handle(callbacks);
            final String webUserName = ((NameCallback)callbacks[0]).getName();
            final Object webCredential = ((ObjectCallback)callbacks[1]).getObject();
            if (webUserName == null || webCredential == null) {
                this.setAuthenticated(false);
                return this.isAuthenticated();
            }
            final UserInfo userInfo = this.getUserInfo(webUserName);
            if (userInfo == null) {
                this.setAuthenticated(false);
                return this.isAuthenticated();
            }
            this.currentUser = new JAASUserInfo(userInfo);
            this.setAuthenticated(this.currentUser.checkCredential(webCredential));
            return this.isAuthenticated();
        }
        catch (IOException e) {
            throw new LoginException(e.toString());
        }
        catch (UnsupportedCallbackException e2) {
            throw new LoginException(e2.toString());
        }
        catch (Exception e3) {
            e3.printStackTrace();
            throw new LoginException(e3.toString());
        }
    }
    
    public boolean logout() throws LoginException {
        this.currentUser.unsetJAASInfo(this.subject);
        return true;
    }
    
    public void initialize(final Subject subject, final CallbackHandler callbackHandler, final Map<String, ?> sharedState, final Map<String, ?> options) {
        this.callbackHandler = callbackHandler;
        this.subject = subject;
    }
    
    public class JAASUserInfo
    {
        private UserInfo user;
        private Principal principal;
        private List<JAASRole> roles;
        
        public JAASUserInfo(final UserInfo u) {
            this.setUserInfo(u);
        }
        
        public String getUserName() {
            return this.user.getUserName();
        }
        
        public Principal getPrincipal() {
            return this.principal;
        }
        
        public void setUserInfo(final UserInfo u) {
            this.user = u;
            this.principal = new JAASPrincipal(u.getUserName());
            this.roles = new ArrayList<JAASRole>();
            if (u.getRoleNames() != null) {
                final Iterator<String> itor = u.getRoleNames().iterator();
                while (itor.hasNext()) {
                    this.roles.add(new JAASRole(itor.next()));
                }
            }
        }
        
        public void setJAASInfo(final Subject subject) {
            subject.getPrincipals().add(this.principal);
            subject.getPrivateCredentials().add(this.user.getCredential());
            subject.getPrincipals().addAll(this.roles);
        }
        
        public void unsetJAASInfo(final Subject subject) {
            subject.getPrincipals().remove(this.principal);
            subject.getPrivateCredentials().remove(this.user.getCredential());
            subject.getPrincipals().removeAll(this.roles);
        }
        
        public boolean checkCredential(final Object suppliedCredential) {
            return this.user.checkCredential(suppliedCredential);
        }
    }
}
