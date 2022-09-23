// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.event;

public class ConfigurationEvent extends Event
{
    public static final EventType<ConfigurationEvent> ANY;
    public static final EventType<ConfigurationEvent> ADD_PROPERTY;
    public static final EventType<ConfigurationEvent> SET_PROPERTY;
    public static final EventType<ConfigurationEvent> CLEAR_PROPERTY;
    public static final EventType<ConfigurationEvent> CLEAR;
    public static final EventType<ConfigurationEvent> ANY_HIERARCHICAL;
    public static final EventType<ConfigurationEvent> ADD_NODES;
    public static final EventType<ConfigurationEvent> CLEAR_TREE;
    public static final EventType<ConfigurationEvent> SUBNODE_CHANGED;
    private static final long serialVersionUID = 20140703L;
    private final String propertyName;
    private final Object propertyValue;
    private final boolean beforeUpdate;
    
    public ConfigurationEvent(final Object source, final EventType<? extends ConfigurationEvent> type, final String propertyName, final Object propertyValue, final boolean beforeUpdate) {
        super(source, type);
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
        this.beforeUpdate = beforeUpdate;
    }
    
    public String getPropertyName() {
        return this.propertyName;
    }
    
    public Object getPropertyValue() {
        return this.propertyValue;
    }
    
    public boolean isBeforeUpdate() {
        return this.beforeUpdate;
    }
    
    static {
        ANY = new EventType<ConfigurationEvent>(Event.ANY, "CONFIGURATION_UPDATE");
        ADD_PROPERTY = new EventType<ConfigurationEvent>(ConfigurationEvent.ANY, "ADD_PROPERTY");
        SET_PROPERTY = new EventType<ConfigurationEvent>(ConfigurationEvent.ANY, "SET_PROPERTY");
        CLEAR_PROPERTY = new EventType<ConfigurationEvent>(ConfigurationEvent.ANY, "CLEAR_PROPERTY");
        CLEAR = new EventType<ConfigurationEvent>(ConfigurationEvent.ANY, "CLEAR");
        ANY_HIERARCHICAL = new EventType<ConfigurationEvent>(ConfigurationEvent.ANY, "HIERARCHICAL");
        ADD_NODES = new EventType<ConfigurationEvent>(ConfigurationEvent.ANY_HIERARCHICAL, "ADD_NODES");
        CLEAR_TREE = new EventType<ConfigurationEvent>(ConfigurationEvent.ANY_HIERARCHICAL, "CLEAR_TREE");
        SUBNODE_CHANGED = new EventType<ConfigurationEvent>(ConfigurationEvent.ANY_HIERARCHICAL, "SUBNODE_CHANGED");
    }
}
