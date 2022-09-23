// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security;

import org.eclipse.jetty.util.security.Constraint;

public class ConstraintMapping
{
    String _method;
    String[] _methodOmissions;
    String _pathSpec;
    Constraint _constraint;
    
    public Constraint getConstraint() {
        return this._constraint;
    }
    
    public void setConstraint(final Constraint constraint) {
        this._constraint = constraint;
    }
    
    public String getMethod() {
        return this._method;
    }
    
    public void setMethod(final String method) {
        this._method = method;
    }
    
    public String getPathSpec() {
        return this._pathSpec;
    }
    
    public void setPathSpec(final String pathSpec) {
        this._pathSpec = pathSpec;
    }
    
    public void setMethodOmissions(final String[] omissions) {
        this._methodOmissions = omissions;
    }
    
    public String[] getMethodOmissions() {
        return this._methodOmissions;
    }
}
