// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.component;

import java.io.IOException;
import org.eclipse.jetty.util.annotation.ManagedOperation;
import org.eclipse.jetty.util.annotation.ManagedObject;

@ManagedObject("Dumpable Object")
public interface Dumpable
{
    @ManagedOperation(value = "Dump the nested Object state as a String", impact = "INFO")
    String dump();
    
    void dump(final Appendable p0, final String p1) throws IOException;
}
