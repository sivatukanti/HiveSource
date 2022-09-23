// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.util.ReuseFactory;
import java.util.ArrayList;

class ColumnOrdering
{
    int myDirection;
    private final ArrayList columns;
    private final ArrayList tables;
    
    ColumnOrdering(final int myDirection) {
        this.columns = new ArrayList();
        this.tables = new ArrayList();
        this.myDirection = myDirection;
    }
    
    boolean ordered(final int n, final int n2, final int n3) {
        return (n == 3 || n == this.myDirection) && this.contains(n2, n3);
    }
    
    boolean contains(final int n, final int n2) {
        for (int i = 0; i < this.columns.size(); ++i) {
            final Integer n3 = this.columns.get(i);
            if (this.tables.get(i) == n && n3 == n2) {
                return true;
            }
        }
        return false;
    }
    
    int direction() {
        return this.myDirection;
    }
    
    void addColumn(final int n, final int n2) {
        this.tables.add(ReuseFactory.getInteger(n));
        this.columns.add(ReuseFactory.getInteger(n2));
    }
    
    void removeColumns(final int n) {
        for (int i = this.tables.size() - 1; i >= 0; --i) {
            if ((int)this.tables.get(i) == n) {
                this.tables.remove(i);
                this.columns.remove(i);
            }
        }
    }
    
    boolean empty() {
        return this.tables.isEmpty();
    }
    
    ColumnOrdering cloneMe() {
        final ColumnOrdering columnOrdering = new ColumnOrdering(this.myDirection);
        for (int i = 0; i < this.columns.size(); ++i) {
            columnOrdering.columns.add(this.columns.get(i));
            columnOrdering.tables.add(this.tables.get(i));
        }
        return columnOrdering;
    }
    
    boolean hasTable(final int n) {
        return this.tables.contains(ReuseFactory.getInteger(n));
    }
    
    boolean hasAnyOtherTable(final int n) {
        for (int i = 0; i < this.tables.size(); ++i) {
            if ((int)this.tables.get(i) != n) {
                return true;
            }
        }
        return false;
    }
    
    public String toString() {
        return "";
    }
}
