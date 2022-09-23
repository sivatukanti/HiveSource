// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.servlet.jmx;

import org.eclipse.jetty.servlet.Holder;
import org.eclipse.jetty.jmx.ObjectMBean;

public class HolderMBean extends ObjectMBean
{
    public HolderMBean(final Object managedObject) {
        super(managedObject);
    }
    
    @Override
    public String getObjectNameBasis() {
        if (this._managed != null && this._managed instanceof Holder) {
            final Holder holder = (Holder)this._managed;
            final String name = holder.getName();
            if (name != null) {
                return name;
            }
        }
        return super.getObjectNameBasis();
    }
}
