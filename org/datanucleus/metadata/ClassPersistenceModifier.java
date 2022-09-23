// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

public enum ClassPersistenceModifier
{
    PERSISTENCE_CAPABLE("persistence-capable"), 
    PERSISTENCE_AWARE("persistence-aware"), 
    NON_PERSISTENT("non-persistent");
    
    String name;
    
    private ClassPersistenceModifier(final String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    public static ClassPersistenceModifier getClassPersistenceModifier(final String value) {
        if (value == null) {
            return ClassPersistenceModifier.PERSISTENCE_CAPABLE;
        }
        if (ClassPersistenceModifier.PERSISTENCE_CAPABLE.toString().equalsIgnoreCase(value)) {
            return ClassPersistenceModifier.PERSISTENCE_CAPABLE;
        }
        if (ClassPersistenceModifier.PERSISTENCE_AWARE.toString().equalsIgnoreCase(value)) {
            return ClassPersistenceModifier.PERSISTENCE_AWARE;
        }
        if (ClassPersistenceModifier.NON_PERSISTENT.toString().equalsIgnoreCase(value)) {
            return ClassPersistenceModifier.NON_PERSISTENT;
        }
        return null;
    }
}
