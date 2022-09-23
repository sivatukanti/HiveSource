// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.security;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Permission;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.util.IdUtil;
import java.security.Principal;
import java.util.Set;
import org.apache.derby.authentication.SystemPrincipal;
import java.util.HashSet;
import javax.security.auth.Subject;

public class SecurityUtil
{
    public static Subject createSystemPrincipalSubject(final String s) {
        final HashSet<SystemPrincipal> principals = new HashSet<SystemPrincipal>();
        if (s != null) {
            principals.add(new SystemPrincipal(s));
            principals.add(new SystemPrincipal(getAuthorizationId(s)));
        }
        final HashSet<Object> set = new HashSet<Object>();
        return new Subject(true, principals, set, set);
    }
    
    private static String getAuthorizationId(final String s) {
        if (s == null) {
            throw new NullPointerException("name can't be null");
        }
        if (s.length() == 0) {
            throw new IllegalArgumentException("name can't be empty");
        }
        try {
            return IdUtil.getUserAuthorizationId(s);
        }
        catch (StandardException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
    }
    
    public static void checkSubjectHasPermission(final Subject subject, final Permission permission) {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            final /* synthetic */ PrivilegedAction val$runCheck = new PrivilegedAction<Void>(permission) {
                final /* synthetic */ Permission val$perm;
                
                public Void run() {
                    AccessController.checkPermission(this.val$perm);
                    return null;
                }
            };
            
            public Void run() {
                Subject.doAsPrivileged(subject, (PrivilegedAction<Object>)this.val$runCheck, null);
                return null;
            }
        });
    }
    
    public static void checkUserHasPermission(final String s, final Permission permission) {
        if (System.getSecurityManager() == null) {
            return;
        }
        checkSubjectHasPermission(createSystemPrincipalSubject(s), permission);
    }
}
