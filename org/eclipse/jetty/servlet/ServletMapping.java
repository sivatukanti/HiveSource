// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.servlet;

import java.io.IOException;
import java.util.Arrays;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;

@ManagedObject("Servlet Mapping")
public class ServletMapping
{
    private String[] _pathSpecs;
    private String _servletName;
    private boolean _default;
    
    @ManagedAttribute(value = "url patterns", readonly = true)
    public String[] getPathSpecs() {
        return this._pathSpecs;
    }
    
    @ManagedAttribute(value = "servlet name", readonly = true)
    public String getServletName() {
        return this._servletName;
    }
    
    public void setPathSpecs(final String[] pathSpecs) {
        this._pathSpecs = pathSpecs;
    }
    
    public boolean containsPathSpec(final String pathSpec) {
        if (this._pathSpecs == null || this._pathSpecs.length == 0) {
            return false;
        }
        for (final String p : this._pathSpecs) {
            if (p.equals(pathSpec)) {
                return true;
            }
        }
        return false;
    }
    
    public void setPathSpec(final String pathSpec) {
        this._pathSpecs = new String[] { pathSpec };
    }
    
    public void setServletName(final String servletName) {
        this._servletName = servletName;
    }
    
    @ManagedAttribute(value = "default", readonly = true)
    public boolean isDefault() {
        return this._default;
    }
    
    public void setDefault(final boolean fromDefault) {
        this._default = fromDefault;
    }
    
    @Override
    public String toString() {
        return ((this._pathSpecs == null) ? "[]" : Arrays.asList(this._pathSpecs).toString()) + "=>" + this._servletName;
    }
    
    public void dump(final Appendable out, final String indent) throws IOException {
        out.append(String.valueOf(this)).append("\n");
    }
}
