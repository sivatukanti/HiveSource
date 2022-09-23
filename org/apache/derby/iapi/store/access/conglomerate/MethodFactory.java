// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.access.conglomerate;

import org.apache.derby.catalog.UUID;
import java.util.Properties;
import org.apache.derby.iapi.services.monitor.ModuleSupportable;

public interface MethodFactory extends ModuleSupportable
{
    public static final String MODULE = "org.apache.derby.iapi.store.access.conglomerate.MethodFactory";
    
    Properties defaultProperties();
    
    boolean supportsImplementation(final String p0);
    
    String primaryImplementationType();
    
    boolean supportsFormat(final UUID p0);
    
    UUID primaryFormat();
}
