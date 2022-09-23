// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.security;

public class ConstraintMapping
{
    String method;
    String pathSpec;
    Constraint constraint;
    
    public Constraint getConstraint() {
        return this.constraint;
    }
    
    public void setConstraint(final Constraint constraint) {
        this.constraint = constraint;
    }
    
    public String getMethod() {
        return this.method;
    }
    
    public void setMethod(final String method) {
        this.method = method;
    }
    
    public String getPathSpec() {
        return this.pathSpec;
    }
    
    public void setPathSpec(final String pathSpec) {
        this.pathSpec = pathSpec;
    }
}
