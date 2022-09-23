// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import org.apache.hadoop.security.UserGroupInformation;

public class UserIdentityProvider implements IdentityProvider
{
    @Override
    public String makeIdentity(final Schedulable obj) {
        final UserGroupInformation ugi = obj.getUserGroupInformation();
        if (ugi == null) {
            return null;
        }
        return ugi.getUserName();
    }
}
