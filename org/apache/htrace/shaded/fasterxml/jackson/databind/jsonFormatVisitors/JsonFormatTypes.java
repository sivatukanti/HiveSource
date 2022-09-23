// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors;

import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonCreator;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonValue;

public enum JsonFormatTypes
{
    STRING, 
    NUMBER, 
    INTEGER, 
    BOOLEAN, 
    OBJECT, 
    ARRAY, 
    NULL, 
    ANY;
    
    @JsonValue
    public String value() {
        return this.name().toLowerCase();
    }
    
    @JsonCreator
    public static JsonFormatTypes forValue(final String s) {
        return valueOf(s.toUpperCase());
    }
}
