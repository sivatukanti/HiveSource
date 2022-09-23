// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.plus.jaas;

import java.util.Enumeration;
import java.security.acl.Group;
import java.security.Principal;

public class StrictRoleCheckPolicy implements RoleCheckPolicy
{
    public boolean checkRole(final String roleName, final Principal runAsRole, final Group roles) {
        if (runAsRole != null) {
            return roleName.equals(runAsRole.getName());
        }
        if (roles == null) {
            return false;
        }
        Enumeration<? extends Principal> rolesEnum;
        boolean found;
        Principal p;
        for (rolesEnum = roles.members(), found = false; rolesEnum.hasMoreElements() && !found; found = roleName.equals(p.getName())) {
            p = (Principal)rolesEnum.nextElement();
        }
        return found;
    }
}
