// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.event.EventSource;
import org.apache.commons.configuration2.ImmutableConfiguration;

public interface ConfigurationBuilder<T extends ImmutableConfiguration> extends EventSource
{
    T getConfiguration() throws ConfigurationException;
}
