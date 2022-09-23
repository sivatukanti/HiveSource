// 
// Decompiled by Procyon v0.5.36
// 

package net.sf.jpam.jaas;

import org.apache.commons.logging.LogFactory;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.CredentialExpiredException;
import javax.security.auth.login.AccountExpiredException;
import net.sf.jpam.PamReturnValue;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.Callback;
import javax.security.auth.login.LoginException;
import net.sf.jpam.Pam;
import java.util.Map;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.Subject;
import org.apache.commons.logging.Log;
import javax.security.auth.spi.LoginModule;

public class JpamLoginModule implements LoginModule
{
    private static final Log LOG;
    private static final String SERVICE_NAME_OPTION = "serviceName";
    private Subject subject;
    private CallbackHandler callbackHandler;
    private Map sharedState;
    private Map options;
    private Pam pam;
    
    public boolean abort() throws LoginException {
        return true;
    }
    
    public boolean commit() throws LoginException {
        return true;
    }
    
    public boolean login() throws LoginException {
        this.pam = this.createPam();
        final Callback[] callbacks = new Callback[2];
        String username = null;
        final NameCallback nameCallback = new NameCallback("Enter Username: ");
        callbacks[0] = nameCallback;
        String credentials = null;
        final PasswordCallback passwordCallback = new PasswordCallback("Enter Credentials: ", false);
        callbacks[1] = passwordCallback;
        try {
            this.callbackHandler.handle(callbacks);
        }
        catch (IOException e) {
            JpamLoginModule.LOG.error("IOException handling login: " + e.getMessage(), e);
            throw new LoginException(e.getMessage());
        }
        catch (UnsupportedCallbackException e2) {
            JpamLoginModule.LOG.error("UnsupportedCallbackException handling login: " + e2.getMessage(), e2);
            throw new LoginException(e2.getMessage());
        }
        username = nameCallback.getName();
        credentials = String.copyValueOf(passwordCallback.getPassword());
        boolean authenticated = false;
        final PamReturnValue pamReturnValue = this.pam.authenticate(username, credentials);
        if (pamReturnValue.equals(PamReturnValue.PAM_SUCCESS)) {
            authenticated = true;
            return authenticated;
        }
        if (pamReturnValue.equals(PamReturnValue.PAM_ACCT_EXPIRED)) {
            throw new AccountExpiredException(PamReturnValue.PAM_ACCT_EXPIRED.toString());
        }
        if (pamReturnValue.equals(PamReturnValue.PAM_CRED_EXPIRED)) {
            throw new CredentialExpiredException(PamReturnValue.PAM_CRED_EXPIRED.toString());
        }
        throw new FailedLoginException(pamReturnValue.toString());
    }
    
    private Pam createPam() {
        String serviceName = this.options.get("serviceName");
        if (serviceName == null) {
            JpamLoginModule.LOG.debug("No serviceName configured in JAAS configuration file. Using default service name of net-sf-jpam");
            serviceName = "net-sf-jpam";
        }
        else {
            JpamLoginModule.LOG.debug("Using service name of " + serviceName + " from JAAS configuration file");
        }
        final Pam pam = new Pam(serviceName);
        return pam;
    }
    
    public boolean logout() throws LoginException {
        return true;
    }
    
    public void initialize(final Subject subject, final CallbackHandler callbackHandler, final Map sharedState, final Map options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = sharedState;
        this.options = options;
    }
    
    public Pam getPam() {
        return this.pam;
    }
    
    static {
        LOG = LogFactory.getLog(JpamLoginModule.class.getName());
    }
}
