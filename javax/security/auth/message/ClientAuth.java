// 
// Decompiled by Procyon v0.5.36
// 

package javax.security.auth.message;

import javax.security.auth.Subject;

public interface ClientAuth
{
    void cleanSubject(final MessageInfo p0, final Subject p1) throws AuthException;
    
    AuthStatus secureRequest(final MessageInfo p0, final Subject p1) throws AuthException;
    
    AuthStatus validateResponse(final MessageInfo p0, final Subject p1, final Subject p2) throws AuthException;
}
