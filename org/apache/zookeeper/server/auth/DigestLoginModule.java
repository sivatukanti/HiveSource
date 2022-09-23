// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.auth;

import java.util.Map;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.Subject;
import javax.security.auth.spi.LoginModule;

public class DigestLoginModule implements LoginModule
{
    private Subject subject;
    
    @Override
    public boolean abort() {
        return false;
    }
    
    @Override
    public boolean commit() {
        return true;
    }
    
    @Override
    public void initialize(final Subject subject, final CallbackHandler callbackHandler, final Map<String, ?> sharedState, final Map<String, ?> options) {
        if (options.containsKey("username")) {
            this.subject = subject;
            final String username = (String)options.get("username");
            this.subject.getPublicCredentials().add(username);
            final String password = (String)options.get("password");
            this.subject.getPrivateCredentials().add(password);
        }
    }
    
    @Override
    public boolean logout() {
        return true;
    }
    
    @Override
    public boolean login() {
        return true;
    }
}
