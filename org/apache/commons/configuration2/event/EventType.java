// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.event;

import java.util.HashSet;
import java.util.Set;
import java.io.Serializable;

public class EventType<T extends Event> implements Serializable
{
    private static final long serialVersionUID = 20150416L;
    private static final String FMT_TO_STRING = "%s [ %s ]";
    private final EventType<? super T> superType;
    private final String name;
    
    public EventType(final EventType<? super T> superEventType, final String typeName) {
        this.superType = superEventType;
        this.name = typeName;
    }
    
    public EventType<? super T> getSuperType() {
        return this.superType;
    }
    
    public String getName() {
        return this.name;
    }
    
    @Override
    public String toString() {
        return String.format("%s [ %s ]", this.getClass().getSimpleName(), this.getName());
    }
    
    public static Set<EventType<?>> fetchSuperEventTypes(final EventType<?> eventType) {
        final Set<EventType<?>> types = new HashSet<EventType<?>>();
        for (EventType<?> currentType = eventType; currentType != null; currentType = currentType.getSuperType()) {
            types.add(currentType);
        }
        return types;
    }
    
    public static boolean isInstanceOf(final EventType<?> derivedType, final EventType<?> baseType) {
        for (EventType<?> currentType = derivedType; currentType != null; currentType = currentType.getSuperType()) {
            if (currentType == baseType) {
                return true;
            }
        }
        return false;
    }
}
