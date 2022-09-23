// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

public enum DiscriminatorStrategy
{
    NONE("none"), 
    VALUE_MAP("value-map"), 
    CLASS_NAME("class-name");
    
    String name;
    
    private DiscriminatorStrategy(final String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    public static DiscriminatorStrategy getDiscriminatorStrategy(final String value) {
        if (value == null) {
            return null;
        }
        if (DiscriminatorStrategy.NONE.toString().equals(value)) {
            return DiscriminatorStrategy.NONE;
        }
        if (DiscriminatorStrategy.VALUE_MAP.toString().equals(value)) {
            return DiscriminatorStrategy.VALUE_MAP;
        }
        if (DiscriminatorStrategy.CLASS_NAME.toString().equals(value)) {
            return DiscriminatorStrategy.CLASS_NAME;
        }
        return null;
    }
}
