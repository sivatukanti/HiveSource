// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

public abstract class OrderedColumn extends QueryTreeNode
{
    protected static final int UNMATCHEDPOSITION = -1;
    protected int columnPosition;
    
    public OrderedColumn() {
        this.columnPosition = -1;
    }
    
    public boolean isAscending() {
        return true;
    }
    
    public boolean isNullsOrderedLow() {
        return false;
    }
    
    public String toString() {
        return "";
    }
    
    public int getColumnPosition() {
        return this.columnPosition;
    }
    
    public void setColumnPosition(final int columnPosition) {
        this.columnPosition = columnPosition;
    }
}
