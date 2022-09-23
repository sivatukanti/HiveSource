// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2;

import java.util.Collection;
import org.apache.commons.configuration2.interpol.Lookup;
import java.util.Map;
import org.apache.commons.configuration2.interpol.ConfigurationInterpolator;
import org.apache.commons.configuration2.sync.SynchronizerSupport;

public interface Configuration extends ImmutableConfiguration, SynchronizerSupport
{
    Configuration subset(final String p0);
    
    void addProperty(final String p0, final Object p1);
    
    void setProperty(final String p0, final Object p1);
    
    void clearProperty(final String p0);
    
    void clear();
    
    ConfigurationInterpolator getInterpolator();
    
    void setInterpolator(final ConfigurationInterpolator p0);
    
    void installInterpolator(final Map<String, ? extends Lookup> p0, final Collection<? extends Lookup> p1);
}
