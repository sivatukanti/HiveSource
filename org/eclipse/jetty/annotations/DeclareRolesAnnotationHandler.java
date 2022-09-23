// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.annotations;

import org.eclipse.jetty.security.ConstraintSecurityHandler;
import javax.annotation.security.DeclareRoles;
import javax.servlet.Servlet;
import org.eclipse.jetty.webapp.WebAppContext;

public class DeclareRolesAnnotationHandler extends AnnotationIntrospector.AbstractIntrospectableAnnotationHandler
{
    protected WebAppContext _context;
    
    public DeclareRolesAnnotationHandler(final WebAppContext context) {
        super(false);
        this._context = context;
    }
    
    @Override
    public void doHandle(final Class clazz) {
        if (!Servlet.class.isAssignableFrom(clazz)) {
            return;
        }
        final DeclareRoles declareRoles = clazz.getAnnotation(DeclareRoles.class);
        if (declareRoles == null) {
            return;
        }
        final String[] roles = declareRoles.value();
        if (roles != null && roles.length > 0) {
            for (final String r : roles) {
                ((ConstraintSecurityHandler)this._context.getSecurityHandler()).addRole(r);
            }
        }
    }
}
