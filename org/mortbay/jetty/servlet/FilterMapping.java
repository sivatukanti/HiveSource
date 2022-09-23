// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.servlet;

import java.util.Arrays;

public class FilterMapping
{
    private int _dispatches;
    private String _filterName;
    private transient FilterHolder _holder;
    private String[] _pathSpecs;
    private String[] _servletNames;
    
    public FilterMapping() {
        this._dispatches = 1;
    }
    
    boolean appliesTo(final String path, final int type) {
        if (((this._dispatches & type) != 0x0 || (this._dispatches == 0 && type == 1)) && this._pathSpecs != null) {
            for (int i = 0; i < this._pathSpecs.length; ++i) {
                if (this._pathSpecs[i] != null && PathMap.match(this._pathSpecs[i], path, true)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    boolean appliesTo(final int type) {
        return (this._dispatches & type) != 0x0 || (this._dispatches == 0 && type == 1);
    }
    
    public int getDispatches() {
        return this._dispatches;
    }
    
    public String getFilterName() {
        return this._filterName;
    }
    
    FilterHolder getFilterHolder() {
        return this._holder;
    }
    
    public String[] getPathSpecs() {
        return this._pathSpecs;
    }
    
    public void setDispatches(final int dispatches) {
        this._dispatches = dispatches;
    }
    
    public void setFilterName(final String filterName) {
        this._filterName = filterName;
    }
    
    void setFilterHolder(final FilterHolder holder) {
        this._holder = holder;
    }
    
    public void setPathSpecs(final String[] pathSpecs) {
        this._pathSpecs = pathSpecs;
    }
    
    public void setPathSpec(final String pathSpec) {
        this._pathSpecs = new String[] { pathSpec };
    }
    
    public String[] getServletNames() {
        return this._servletNames;
    }
    
    public void setServletNames(final String[] servletNames) {
        this._servletNames = servletNames;
    }
    
    public void setServletName(final String servletName) {
        this._servletNames = new String[] { servletName };
    }
    
    public String toString() {
        return "(F=" + this._filterName + "," + ((this._pathSpecs == null) ? "[]" : Arrays.asList(this._pathSpecs).toString()) + "," + ((this._servletNames == null) ? "[]" : Arrays.asList(this._servletNames).toString()) + "," + this._dispatches + ")";
    }
}
