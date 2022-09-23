// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder.combined;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.ConfigurationBuilder;

public interface ConfigurationBuilderProvider
{
    ConfigurationBuilder<? extends Configuration> getConfigurationBuilder(final ConfigurationDeclaration p0) throws ConfigurationException;
}
