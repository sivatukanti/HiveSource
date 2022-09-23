// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.container;

import java.security.PrivilegedAction;
import javax.ws.rs.core.SecurityContext;

public interface SubjectSecurityContext extends SecurityContext
{
    Object doAsSubject(final PrivilegedAction p0);
}
