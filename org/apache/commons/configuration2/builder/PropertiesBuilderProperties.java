// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.PropertiesConfigurationLayout;

public interface PropertiesBuilderProperties<T>
{
    T setIncludesAllowed(final boolean p0);
    
    T setLayout(final PropertiesConfigurationLayout p0);
    
    T setIOFactory(final PropertiesConfiguration.IOFactory p0);
}
