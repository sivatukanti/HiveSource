// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.servlet.jmx;

import org.eclipse.jetty.servlet.FilterMapping;
import org.eclipse.jetty.jmx.ObjectMBean;

public class FilterMappingMBean extends ObjectMBean
{
    public FilterMappingMBean(final Object managedObject) {
        super(managedObject);
    }
    
    @Override
    public String getObjectNameBasis() {
        if (this._managed != null && this._managed instanceof FilterMapping) {
            final FilterMapping mapping = (FilterMapping)this._managed;
            final String name = mapping.getFilterName();
            if (name != null) {
                return name;
            }
        }
        return super.getObjectNameBasis();
    }
}
