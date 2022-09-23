// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.servlet;

import java.util.Arrays;

public class ServletMapping
{
    private String[] _pathSpecs;
    private String _servletName;
    
    public String[] getPathSpecs() {
        return this._pathSpecs;
    }
    
    public String getServletName() {
        return this._servletName;
    }
    
    public void setPathSpecs(final String[] pathSpecs) {
        this._pathSpecs = pathSpecs;
    }
    
    public void setPathSpec(final String pathSpec) {
        this._pathSpecs = new String[] { pathSpec };
    }
    
    public void setServletName(final String servletName) {
        this._servletName = servletName;
    }
    
    public String toString() {
        return "(S=" + this._servletName + "," + ((this._pathSpecs == null) ? "[]" : Arrays.asList(this._pathSpecs).toString()) + ")";
    }
}
