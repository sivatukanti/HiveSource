// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util.optional;

import java.security.Permission;
import org.apache.tools.ant.ExitException;

public class NoExitSecurityManager extends SecurityManager
{
    @Override
    public void checkExit(final int status) {
        throw new ExitException(status);
    }
    
    @Override
    public void checkPermission(final Permission perm) {
    }
}
