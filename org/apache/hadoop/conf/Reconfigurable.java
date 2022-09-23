// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.conf;

import java.util.Collection;

public interface Reconfigurable extends Configurable
{
    void reconfigureProperty(final String p0, final String p1) throws ReconfigurationException;
    
    boolean isPropertyReconfigurable(final String p0);
    
    Collection<String> getReconfigurableProperties();
}
