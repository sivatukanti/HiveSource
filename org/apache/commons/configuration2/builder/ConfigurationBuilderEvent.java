// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder;

import org.apache.commons.configuration2.event.EventType;
import org.apache.commons.configuration2.event.Event;

public class ConfigurationBuilderEvent extends Event
{
    public static final EventType<ConfigurationBuilderEvent> ANY;
    public static final EventType<ConfigurationBuilderEvent> RESET;
    public static final EventType<ConfigurationBuilderEvent> CONFIGURATION_REQUEST;
    
    public ConfigurationBuilderEvent(final ConfigurationBuilder<?> source, final EventType<? extends ConfigurationBuilderEvent> evType) {
        super(source, evType);
    }
    
    @Override
    public ConfigurationBuilder<?> getSource() {
        return (ConfigurationBuilder<?>)super.getSource();
    }
    
    static {
        ANY = new EventType<ConfigurationBuilderEvent>(Event.ANY, "BUILDER");
        RESET = new EventType<ConfigurationBuilderEvent>(ConfigurationBuilderEvent.ANY, "RESET");
        CONFIGURATION_REQUEST = new EventType<ConfigurationBuilderEvent>(ConfigurationBuilderEvent.ANY, "CONFIGURATION_REQUEST");
    }
}
