// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.servlet.jmx;

import org.eclipse.jetty.servlet.ServletMapping;
import org.eclipse.jetty.jmx.ObjectMBean;

public class ServletMappingMBean extends ObjectMBean
{
    public ServletMappingMBean(final Object managedObject) {
        super(managedObject);
    }
    
    @Override
    public String getObjectNameBasis() {
        if (this._managed != null && this._managed instanceof ServletMapping) {
            final ServletMapping mapping = (ServletMapping)this._managed;
            final String name = mapping.getServletName();
            if (name != null) {
                return name;
            }
        }
        return super.getObjectNameBasis();
    }
}
