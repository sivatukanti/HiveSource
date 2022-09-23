// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder;

import org.apache.commons.configuration2.ImmutableConfiguration;
import org.apache.commons.configuration2.event.EventType;

public class ConfigurationBuilderResultCreatedEvent extends ConfigurationBuilderEvent
{
    public static final EventType<ConfigurationBuilderResultCreatedEvent> RESULT_CREATED;
    private final ImmutableConfiguration configuration;
    
    public ConfigurationBuilderResultCreatedEvent(final ConfigurationBuilder<?> source, final EventType<? extends ConfigurationBuilderResultCreatedEvent> evType, final ImmutableConfiguration createdConfiguration) {
        super(source, evType);
        if (createdConfiguration == null) {
            throw new IllegalArgumentException("Configuration must not be null!");
        }
        this.configuration = createdConfiguration;
    }
    
    public ImmutableConfiguration getConfiguration() {
        return this.configuration;
    }
    
    static {
        RESULT_CREATED = new EventType<ConfigurationBuilderResultCreatedEvent>(ConfigurationBuilderResultCreatedEvent.ANY, "RESULT_CREATED");
    }
}
