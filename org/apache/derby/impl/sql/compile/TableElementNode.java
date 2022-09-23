// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

public class TableElementNode extends QueryTreeNode
{
    public static final int AT_UNKNOWN = 0;
    public static final int AT_ADD_FOREIGN_KEY_CONSTRAINT = 1;
    public static final int AT_ADD_PRIMARY_KEY_CONSTRAINT = 2;
    public static final int AT_ADD_UNIQUE_CONSTRAINT = 3;
    public static final int AT_ADD_CHECK_CONSTRAINT = 4;
    public static final int AT_DROP_CONSTRAINT = 5;
    public static final int AT_MODIFY_COLUMN = 6;
    public static final int AT_DROP_COLUMN = 7;
    String name;
    int elementType;
    
    public void init(final Object o) {
        this.name = (String)o;
    }
    
    public void init(final Object o, final Object o2) {
        this.name = (String)o;
        this.elementType = (int)o2;
    }
    
    public String toString() {
        return "";
    }
    
    boolean hasPrimaryKeyConstraint() {
        return false;
    }
    
    boolean hasUniqueKeyConstraint() {
        return false;
    }
    
    boolean hasForeignKeyConstraint() {
        return false;
    }
    
    boolean hasCheckConstraint() {
        return false;
    }
    
    boolean hasConstraint() {
        return false;
    }
    
    public String getName() {
        return this.name;
    }
    
    int getElementType() {
        if (this.hasForeignKeyConstraint()) {
            return 1;
        }
        if (this.hasPrimaryKeyConstraint()) {
            return 2;
        }
        if (this.hasUniqueKeyConstraint()) {
            return 3;
        }
        if (this.hasCheckConstraint()) {
            return 4;
        }
        if (this instanceof ConstraintDefinitionNode) {
            return 5;
        }
        if (!(this instanceof ModifyColumnNode)) {
            return this.elementType;
        }
        if (this.getNodeType() == 113) {
            return 7;
        }
        return 6;
    }
}
