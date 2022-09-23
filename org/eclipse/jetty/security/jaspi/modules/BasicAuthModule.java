// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security.jaspi.modules;

import org.eclipse.jetty.util.log.Log;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.Subject;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.AuthException;
import java.util.Map;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.callback.CallbackHandler;
import org.eclipse.jetty.util.log.Logger;

public class BasicAuthModule extends BaseAuthModule
{
    private static final Logger LOG;
    private String realmName;
    private static final String REALM_KEY = "org.eclipse.jetty.security.jaspi.modules.RealmName";
    
    public BasicAuthModule() {
    }
    
    public BasicAuthModule(final CallbackHandler callbackHandler, final String realmName) {
        super(callbackHandler);
        this.realmName = realmName;
    }
    
    @Override
    public void initialize(final MessagePolicy requestPolicy, final MessagePolicy responsePolicy, final CallbackHandler handler, final Map options) throws AuthException {
        super.initialize(requestPolicy, responsePolicy, handler, options);
        this.realmName = options.get("org.eclipse.jetty.security.jaspi.modules.RealmName");
    }
    
    @Override
    public AuthStatus validateRequest(final MessageInfo messageInfo, final Subject clientSubject, final Subject serviceSubject) throws AuthException {
        final HttpServletRequest request = (HttpServletRequest)messageInfo.getRequestMessage();
        final HttpServletResponse response = (HttpServletResponse)messageInfo.getResponseMessage();
        final String credentials = request.getHeader("Authorization");
        try {
            if (credentials != null) {
                if (BasicAuthModule.LOG.isDebugEnabled()) {
                    BasicAuthModule.LOG.debug("Credentials: " + credentials, new Object[0]);
                }
                if (this.login(clientSubject, credentials, "BASIC", messageInfo)) {
                    return AuthStatus.SUCCESS;
                }
            }
            if (!this.isMandatory(messageInfo)) {
                return AuthStatus.SUCCESS;
            }
            response.setHeader("WWW-Authenticate", "basic realm=\"" + this.realmName + '\"');
            response.sendError(401);
            return AuthStatus.SEND_CONTINUE;
        }
        catch (IOException e) {
            throw new AuthException(e.getMessage());
        }
        catch (UnsupportedCallbackException e2) {
            throw new AuthException(e2.getMessage());
        }
    }
    
    static {
        LOG = Log.getLogger(BasicAuthModule.class);
    }
}
