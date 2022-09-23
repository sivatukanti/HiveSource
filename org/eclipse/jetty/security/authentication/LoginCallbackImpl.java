// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security.authentication;

import org.eclipse.jetty.security.IdentityService;
import java.security.Principal;
import javax.security.auth.Subject;

public class LoginCallbackImpl implements LoginCallback
{
    private final Subject subject;
    private final String userName;
    private Object credential;
    private boolean success;
    private Principal userPrincipal;
    private String[] roles;
    
    public LoginCallbackImpl(final Subject subject, final String userName, final Object credential) {
        this.roles = IdentityService.NO_ROLES;
        this.subject = subject;
        this.userName = userName;
        this.credential = credential;
    }
    
    @Override
    public Subject getSubject() {
        return this.subject;
    }
    
    @Override
    public String getUserName() {
        return this.userName;
    }
    
    @Override
    public Object getCredential() {
        return this.credential;
    }
    
    @Override
    public boolean isSuccess() {
        return this.success;
    }
    
    @Override
    public void setSuccess(final boolean success) {
        this.success = success;
    }
    
    @Override
    public Principal getUserPrincipal() {
        return this.userPrincipal;
    }
    
    @Override
    public void setUserPrincipal(final Principal userPrincipal) {
        this.userPrincipal = userPrincipal;
    }
    
    @Override
    public String[] getRoles() {
        return this.roles;
    }
    
    @Override
    public void setRoles(final String[] groups) {
        this.roles = groups;
    }
    
    @Override
    public void clearPassword() {
        if (this.credential != null) {
            this.credential = null;
        }
    }
}
