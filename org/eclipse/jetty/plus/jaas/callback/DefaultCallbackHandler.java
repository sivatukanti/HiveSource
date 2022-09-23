// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.plus.jaas.callback;

import java.io.IOException;
import java.util.List;
import java.util.Arrays;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.eclipse.jetty.util.security.Password;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.Callback;
import org.eclipse.jetty.server.Request;

public class DefaultCallbackHandler extends AbstractCallbackHandler
{
    private Request _request;
    
    public void setRequest(final Request request) {
        this._request = request;
    }
    
    @Override
    public void handle(final Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; ++i) {
            if (callbacks[i] instanceof NameCallback) {
                ((NameCallback)callbacks[i]).setName(this.getUserName());
            }
            else if (callbacks[i] instanceof ObjectCallback) {
                ((ObjectCallback)callbacks[i]).setObject(this.getCredential());
            }
            else if (callbacks[i] instanceof PasswordCallback) {
                if (this.getCredential() instanceof Password) {
                    ((PasswordCallback)callbacks[i]).setPassword(((Password)this.getCredential()).toString().toCharArray());
                }
                else {
                    if (!(this.getCredential() instanceof String)) {
                        throw new UnsupportedCallbackException(callbacks[i], "User supplied credentials cannot be converted to char[] for PasswordCallback: try using an ObjectCallback instead");
                    }
                    ((PasswordCallback)callbacks[i]).setPassword(((String)this.getCredential()).toCharArray());
                }
            }
            else {
                if (!(callbacks[i] instanceof RequestParameterCallback)) {
                    throw new UnsupportedCallbackException(callbacks[i]);
                }
                final RequestParameterCallback callback = (RequestParameterCallback)callbacks[i];
                callback.setParameterValues(Arrays.asList(this._request.getParameterValues(callback.getParameterName())));
            }
        }
    }
}
