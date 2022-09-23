// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.event;

public class ConfigurationErrorEvent extends Event
{
    public static final EventType<ConfigurationErrorEvent> ANY;
    public static final EventType<ConfigurationErrorEvent> READ;
    public static final EventType<ConfigurationErrorEvent> WRITE;
    private static final long serialVersionUID = 20140712L;
    private final EventType<?> errorOperationType;
    private final String propertyName;
    private final Object propertyValue;
    private final Throwable cause;
    
    public ConfigurationErrorEvent(final Object source, final EventType<? extends ConfigurationErrorEvent> eventType, final EventType<?> operationType, final String propName, final Object propValue, final Throwable cause) {
        super(source, eventType);
        this.errorOperationType = operationType;
        this.propertyName = propName;
        this.propertyValue = propValue;
        this.cause = cause;
    }
    
    public EventType<?> getErrorOperationType() {
        return this.errorOperationType;
    }
    
    public String getPropertyName() {
        return this.propertyName;
    }
    
    public Object getPropertyValue() {
        return this.propertyValue;
    }
    
    public Throwable getCause() {
        return this.cause;
    }
    
    static {
        ANY = new EventType<ConfigurationErrorEvent>(Event.ANY, "ERROR");
        READ = new EventType<ConfigurationErrorEvent>(ConfigurationErrorEvent.ANY, "READ_ERROR");
        WRITE = new EventType<ConfigurationErrorEvent>(ConfigurationErrorEvent.ANY, "WRITE_ERROR");
    }
}
