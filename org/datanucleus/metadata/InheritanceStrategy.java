// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

public enum InheritanceStrategy
{
    SUBCLASS_TABLE("subclass-table"), 
    NEW_TABLE("new-table"), 
    SUPERCLASS_TABLE("superclass-table"), 
    COMPLETE_TABLE("complete-table");
    
    String name;
    
    private InheritanceStrategy(final String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    public static InheritanceStrategy getInheritanceStrategy(final String value) {
        if (value == null) {
            return null;
        }
        if (InheritanceStrategy.SUBCLASS_TABLE.toString().equals(value)) {
            return InheritanceStrategy.SUBCLASS_TABLE;
        }
        if (InheritanceStrategy.NEW_TABLE.toString().equals(value)) {
            return InheritanceStrategy.NEW_TABLE;
        }
        if (InheritanceStrategy.SUPERCLASS_TABLE.toString().equals(value)) {
            return InheritanceStrategy.SUPERCLASS_TABLE;
        }
        if (InheritanceStrategy.COMPLETE_TABLE.toString().equals(value)) {
            return InheritanceStrategy.COMPLETE_TABLE;
        }
        return InheritanceStrategy.NEW_TABLE;
    }
}
