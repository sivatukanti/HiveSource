// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder.combined;

import org.apache.commons.configuration2.builder.DefaultParametersHandler;
import org.apache.commons.configuration2.builder.DefaultParametersManager;
import org.apache.commons.configuration2.builder.BuilderParameters;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.builder.ConfigurationBuilder;

public interface CombinedBuilderProperties<T>
{
    T setInheritSettings(final boolean p0);
    
    T setDefinitionBuilder(final ConfigurationBuilder<? extends HierarchicalConfiguration<?>> p0);
    
    T registerProvider(final String p0, final ConfigurationBuilderProvider p1);
    
    T setBasePath(final String p0);
    
    T setDefinitionBuilderParameters(final BuilderParameters p0);
    
    T setChildDefaultParametersManager(final DefaultParametersManager p0);
    
     <D> T registerChildDefaultsHandler(final Class<D> p0, final DefaultParametersHandler<? super D> p1);
    
     <D> T registerChildDefaultsHandler(final Class<D> p0, final DefaultParametersHandler<? super D> p1, final Class<?> p2);
}
