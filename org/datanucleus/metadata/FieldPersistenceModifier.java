// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

public enum FieldPersistenceModifier
{
    PERSISTENT, 
    TRANSACTIONAL, 
    NONE, 
    DEFAULT;
    
    public static FieldPersistenceModifier getFieldPersistenceModifier(final String value) {
        if (value == null) {
            return null;
        }
        if (FieldPersistenceModifier.PERSISTENT.toString().equalsIgnoreCase(value)) {
            return FieldPersistenceModifier.PERSISTENT;
        }
        if (FieldPersistenceModifier.TRANSACTIONAL.toString().equalsIgnoreCase(value)) {
            return FieldPersistenceModifier.TRANSACTIONAL;
        }
        if (FieldPersistenceModifier.NONE.toString().equalsIgnoreCase(value)) {
            return FieldPersistenceModifier.NONE;
        }
        return null;
    }
}
