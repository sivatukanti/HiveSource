// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.monitor;

import org.apache.derby.iapi.error.StandardException;
import java.util.Properties;

public interface ModuleControl
{
    void boot(final boolean p0, final Properties p1) throws StandardException;
    
    void stop();
}
