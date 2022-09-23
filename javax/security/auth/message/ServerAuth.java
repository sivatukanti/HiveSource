// 
// Decompiled by Procyon v0.5.36
// 

package javax.security.auth.message;

import javax.security.auth.Subject;

public interface ServerAuth
{
    void cleanSubject(final MessageInfo p0, final Subject p1) throws AuthException;
    
    AuthStatus secureResponse(final MessageInfo p0, final Subject p1) throws AuthException;
    
    AuthStatus validateRequest(final MessageInfo p0, final Subject p1, final Subject p2) throws AuthException;
}
