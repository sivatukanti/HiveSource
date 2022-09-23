// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import java.io.Serializable;

public class ForeignKeyAction implements Serializable
{
    public static final ForeignKeyAction CASCADE;
    public static final ForeignKeyAction RESTRICT;
    public static final ForeignKeyAction NULL;
    public static final ForeignKeyAction DEFAULT;
    public static final ForeignKeyAction NONE;
    private final int typeId;
    
    protected ForeignKeyAction(final int i) {
        this.typeId = i;
    }
    
    @Override
    public String toString() {
        switch (this.getType()) {
            case 1: {
                return "cascade";
            }
            case 2: {
                return "restrict";
            }
            case 3: {
                return "null";
            }
            case 4: {
                return "default";
            }
            case 5: {
                return "none";
            }
            default: {
                return "";
            }
        }
    }
    
    @Override
    public int hashCode() {
        return this.typeId;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof ForeignKeyAction && ((ForeignKeyAction)o).typeId == this.typeId;
    }
    
    protected int getType() {
        return this.typeId;
    }
    
    public static ForeignKeyAction getForeignKeyAction(final String value) {
        if (value == null) {
            return null;
        }
        if (ForeignKeyAction.CASCADE.toString().equalsIgnoreCase(value)) {
            return ForeignKeyAction.CASCADE;
        }
        if (ForeignKeyAction.DEFAULT.toString().equalsIgnoreCase(value)) {
            return ForeignKeyAction.DEFAULT;
        }
        if (ForeignKeyAction.NULL.toString().equalsIgnoreCase(value)) {
            return ForeignKeyAction.NULL;
        }
        if (ForeignKeyAction.RESTRICT.toString().equalsIgnoreCase(value)) {
            return ForeignKeyAction.RESTRICT;
        }
        if (ForeignKeyAction.NONE.toString().equalsIgnoreCase(value)) {
            return ForeignKeyAction.NONE;
        }
        return null;
    }
    
    static {
        CASCADE = new ForeignKeyAction(1);
        RESTRICT = new ForeignKeyAction(2);
        NULL = new ForeignKeyAction(3);
        DEFAULT = new ForeignKeyAction(4);
        NONE = new ForeignKeyAction(5);
    }
}
