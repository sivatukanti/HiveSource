// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security.jaspi.modules;

import java.security.Principal;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import javax.security.auth.message.AuthException;
import org.eclipse.jetty.util.security.Credential;
import org.eclipse.jetty.util.security.Password;
import org.eclipse.jetty.util.B64Code;
import java.security.cert.X509Certificate;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.Subject;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.callback.CallbackHandler;

public class ClientCertAuthModule extends BaseAuthModule
{
    public ClientCertAuthModule() {
    }
    
    public ClientCertAuthModule(final CallbackHandler callbackHandler) {
        super(callbackHandler);
    }
    
    @Override
    public AuthStatus validateRequest(final MessageInfo messageInfo, final Subject clientSubject, final Subject serviceSubject) throws AuthException {
        final HttpServletRequest request = (HttpServletRequest)messageInfo.getRequestMessage();
        final HttpServletResponse response = (HttpServletResponse)messageInfo.getResponseMessage();
        final X509Certificate[] certs = (X509Certificate[])request.getAttribute("javax.servlet.request.X509Certificate");
        try {
            if (certs == null || certs.length == 0 || certs[0] == null) {
                response.sendError(403, "A client certificate is required for accessing this web application but the server's listener is not configured for mutual authentication (or the client did not provide a certificate).");
                return AuthStatus.SEND_FAILURE;
            }
            Principal principal = certs[0].getSubjectDN();
            if (principal == null) {
                principal = certs[0].getIssuerDN();
            }
            final String username = (principal == null) ? "clientcert" : principal.getName();
            final String password = new String(B64Code.encode(certs[0].getSignature()));
            if (this.login(clientSubject, username, new Password(password), "CLIENT_CERT", messageInfo)) {
                return AuthStatus.SUCCESS;
            }
            if (!this.isMandatory(messageInfo)) {
                return AuthStatus.SUCCESS;
            }
            response.sendError(403, "The provided client certificate does not correspond to a trusted user.");
            return AuthStatus.SEND_FAILURE;
        }
        catch (IOException e) {
            throw new AuthException(e.getMessage());
        }
        catch (UnsupportedCallbackException e2) {
            throw new AuthException(e2.getMessage());
        }
    }
}
