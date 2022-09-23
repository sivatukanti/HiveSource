// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.component;

import org.eclipse.jetty.util.annotation.ManagedOperation;
import org.eclipse.jetty.util.annotation.ManagedObject;

@ManagedObject
public interface Destroyable
{
    @ManagedOperation(value = "Destroys this component", impact = "ACTION")
    void destroy();
}
