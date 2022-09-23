// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.plus.jaas;

import java.security.acl.Group;
import java.security.Principal;

public interface RoleCheckPolicy
{
    boolean checkRole(final String p0, final Principal p1, final Group p2);
}
