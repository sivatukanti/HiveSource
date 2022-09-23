// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.servlet;

import org.eclipse.jetty.util.component.ContainerLifeCycle;
import java.io.IOException;
import org.eclipse.jetty.util.TypeUtil;
import java.util.EnumSet;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.http.PathMap;
import javax.servlet.DispatcherType;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.Dumpable;

@ManagedObject("Filter Mappings")
public class FilterMapping implements Dumpable
{
    public static final int DEFAULT = 0;
    public static final int REQUEST = 1;
    public static final int FORWARD = 2;
    public static final int INCLUDE = 4;
    public static final int ERROR = 8;
    public static final int ASYNC = 16;
    public static final int ALL = 31;
    private int _dispatches;
    private String _filterName;
    private transient FilterHolder _holder;
    private String[] _pathSpecs;
    private String[] _servletNames;
    
    public static DispatcherType dispatch(final String type) {
        if ("request".equalsIgnoreCase(type)) {
            return DispatcherType.REQUEST;
        }
        if ("forward".equalsIgnoreCase(type)) {
            return DispatcherType.FORWARD;
        }
        if ("include".equalsIgnoreCase(type)) {
            return DispatcherType.INCLUDE;
        }
        if ("error".equalsIgnoreCase(type)) {
            return DispatcherType.ERROR;
        }
        if ("async".equalsIgnoreCase(type)) {
            return DispatcherType.ASYNC;
        }
        throw new IllegalArgumentException(type);
    }
    
    public static int dispatch(final DispatcherType type) {
        switch (type) {
            case REQUEST: {
                return 1;
            }
            case ASYNC: {
                return 16;
            }
            case FORWARD: {
                return 2;
            }
            case INCLUDE: {
                return 4;
            }
            case ERROR: {
                return 8;
            }
            default: {
                throw new IllegalArgumentException(type.toString());
            }
        }
    }
    
    public FilterMapping() {
        this._dispatches = 0;
    }
    
    boolean appliesTo(final String path, final int type) {
        if (this.appliesTo(type)) {
            for (int i = 0; i < this._pathSpecs.length; ++i) {
                if (this._pathSpecs[i] != null && PathMap.match(this._pathSpecs[i], path, true)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    boolean appliesTo(final int type) {
        if (this._dispatches == 0) {
            return type == 1 || (type == 16 && this._holder.isAsyncSupported());
        }
        return (this._dispatches & type) != 0x0;
    }
    
    public boolean appliesTo(final DispatcherType t) {
        return this.appliesTo(dispatch(t));
    }
    
    public boolean isDefaultDispatches() {
        return this._dispatches == 0;
    }
    
    @ManagedAttribute(value = "filter name", readonly = true)
    public String getFilterName() {
        return this._filterName;
    }
    
    FilterHolder getFilterHolder() {
        return this._holder;
    }
    
    @ManagedAttribute(value = "url patterns", readonly = true)
    public String[] getPathSpecs() {
        return this._pathSpecs;
    }
    
    public void setDispatcherTypes(final EnumSet<DispatcherType> dispatcherTypes) {
        this._dispatches = 0;
        if (dispatcherTypes != null) {
            if (dispatcherTypes.contains(DispatcherType.ERROR)) {
                this._dispatches |= 0x8;
            }
            if (dispatcherTypes.contains(DispatcherType.FORWARD)) {
                this._dispatches |= 0x2;
            }
            if (dispatcherTypes.contains(DispatcherType.INCLUDE)) {
                this._dispatches |= 0x4;
            }
            if (dispatcherTypes.contains(DispatcherType.REQUEST)) {
                this._dispatches |= 0x1;
            }
            if (dispatcherTypes.contains(DispatcherType.ASYNC)) {
                this._dispatches |= 0x10;
            }
        }
    }
    
    public void setDispatches(final int dispatches) {
        this._dispatches = dispatches;
    }
    
    public void setFilterName(final String filterName) {
        this._filterName = filterName;
    }
    
    void setFilterHolder(final FilterHolder holder) {
        this._holder = holder;
        this.setFilterName(holder.getName());
    }
    
    public void setPathSpecs(final String[] pathSpecs) {
        this._pathSpecs = pathSpecs;
    }
    
    public void setPathSpec(final String pathSpec) {
        this._pathSpecs = new String[] { pathSpec };
    }
    
    @ManagedAttribute(value = "servlet names", readonly = true)
    public String[] getServletNames() {
        return this._servletNames;
    }
    
    public void setServletNames(final String[] servletNames) {
        this._servletNames = servletNames;
    }
    
    public void setServletName(final String servletName) {
        this._servletNames = new String[] { servletName };
    }
    
    @Override
    public String toString() {
        return TypeUtil.asList(this._pathSpecs) + "/" + TypeUtil.asList(this._servletNames) + "==" + this._dispatches + "=>" + this._filterName;
    }
    
    @Override
    public void dump(final Appendable out, final String indent) throws IOException {
        out.append(String.valueOf(this)).append("\n");
    }
    
    @Override
    public String dump() {
        return ContainerLifeCycle.dump(this);
    }
}
