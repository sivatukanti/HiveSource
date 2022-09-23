// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.Optimizable;
import org.apache.derby.iapi.error.StandardException;
import java.util.ArrayList;
import org.apache.derby.iapi.sql.compile.RowOrdering;

class RowOrderingImpl implements RowOrdering
{
    private final ArrayList ordering;
    ColumnOrdering columnsAlwaysOrdered;
    private final ArrayList alwaysOrderedOptimizables;
    ColumnOrdering currentColumnOrdering;
    private final ArrayList unorderedOptimizables;
    
    RowOrderingImpl() {
        this.ordering = new ArrayList();
        this.alwaysOrderedOptimizables = new ArrayList();
        this.unorderedOptimizables = new ArrayList();
        this.columnsAlwaysOrdered = new ColumnOrdering(3);
    }
    
    public boolean isColumnAlwaysOrdered(final int n, final int n2) {
        return this.columnsAlwaysOrdered.contains(n, n2);
    }
    
    public boolean orderedOnColumn(final int n, final int index, final int n2, final int n3) throws StandardException {
        return this.vectorContainsOptimizable(n2, this.alwaysOrderedOptimizables) || this.columnsAlwaysOrdered.contains(n2, n3) || (index < this.ordering.size() && this.ordering.get(index).ordered(n, n2, n3));
    }
    
    public boolean orderedOnColumn(final int n, final int n2, final int n3) throws StandardException {
        if (this.vectorContainsOptimizable(n2, this.alwaysOrderedOptimizables)) {
            return true;
        }
        if (this.columnsAlwaysOrdered.contains(n2, n3)) {
            return true;
        }
        boolean b = false;
        for (int i = 0; i < this.ordering.size(); ++i) {
            if (((ColumnOrdering)this.ordering.get(i)).ordered(n, n2, n3)) {
                b = true;
                break;
            }
        }
        return b;
    }
    
    private boolean vectorContainsOptimizable(final int n, final ArrayList list) {
        for (int i = list.size() - 1; i >= 0; --i) {
            final Optimizable optimizable = list.get(i);
            if (optimizable.hasTableNumber() && optimizable.getTableNumber() == n) {
                return true;
            }
        }
        return false;
    }
    
    public void addOrderedColumn(final int n, final int n2, final int n3) {
        if (!this.unorderedOptimizables.isEmpty()) {
            return;
        }
        ColumnOrdering e;
        if (this.ordering.isEmpty()) {
            e = new ColumnOrdering(n);
            this.ordering.add(e);
        }
        else {
            e = this.ordering.get(this.ordering.size() - 1);
        }
        e.addColumn(n2, n3);
    }
    
    public void nextOrderPosition(final int n) {
        if (!this.unorderedOptimizables.isEmpty()) {
            return;
        }
        this.currentColumnOrdering = new ColumnOrdering(n);
        this.ordering.add(this.currentColumnOrdering);
    }
    
    public void optimizableAlwaysOrdered(final Optimizable e) {
        if (this.unorderedOptimizablesOtherThan(e)) {
            return;
        }
        final boolean hasTableNumber = e.hasTableNumber();
        final int n = hasTableNumber ? e.getTableNumber() : 0;
        if ((this.ordering.isEmpty() || (hasTableNumber && this.ordering.get(0).hasTable(n))) && hasTableNumber && !this.columnsAlwaysOrdered.hasAnyOtherTable(n)) {
            if (e.hasTableNumber()) {
                this.removeOptimizable(e.getTableNumber());
            }
            this.alwaysOrderedOptimizables.add(e);
        }
    }
    
    public void columnAlwaysOrdered(final Optimizable optimizable, final int n) {
        this.columnsAlwaysOrdered.addColumn(optimizable.getTableNumber(), n);
    }
    
    public boolean alwaysOrdered(final int n) {
        return this.vectorContainsOptimizable(n, this.alwaysOrderedOptimizables);
    }
    
    public void removeOptimizable(final int n) {
        for (int i = this.ordering.size() - 1; i >= 0; --i) {
            final ColumnOrdering columnOrdering = this.ordering.get(i);
            columnOrdering.removeColumns(n);
            if (columnOrdering.empty()) {
                this.ordering.remove(i);
            }
        }
        this.columnsAlwaysOrdered.removeColumns(n);
        this.removeOptimizableFromVector(n, this.unorderedOptimizables);
        this.removeOptimizableFromVector(n, this.alwaysOrderedOptimizables);
    }
    
    private void removeOptimizableFromVector(final int n, final ArrayList list) {
        for (int i = list.size() - 1; i >= 0; --i) {
            final Optimizable optimizable = list.get(i);
            if (optimizable.hasTableNumber() && optimizable.getTableNumber() == n) {
                list.remove(i);
            }
        }
    }
    
    public void addUnorderedOptimizable(final Optimizable e) {
        this.unorderedOptimizables.add(e);
    }
    
    public void copy(final RowOrdering rowOrdering) {
        final RowOrderingImpl rowOrderingImpl = (RowOrderingImpl)rowOrdering;
        rowOrderingImpl.ordering.clear();
        rowOrderingImpl.currentColumnOrdering = null;
        rowOrderingImpl.unorderedOptimizables.clear();
        for (int i = 0; i < this.unorderedOptimizables.size(); ++i) {
            rowOrderingImpl.unorderedOptimizables.add(this.unorderedOptimizables.get(i));
        }
        rowOrderingImpl.alwaysOrderedOptimizables.clear();
        for (int j = 0; j < this.alwaysOrderedOptimizables.size(); ++j) {
            rowOrderingImpl.alwaysOrderedOptimizables.add(this.alwaysOrderedOptimizables.get(j));
        }
        for (int k = 0; k < this.ordering.size(); ++k) {
            final ColumnOrdering columnOrdering = this.ordering.get(k);
            rowOrderingImpl.ordering.add(columnOrdering.cloneMe());
            if (columnOrdering == this.currentColumnOrdering) {
                rowOrderingImpl.rememberCurrentColumnOrdering(k);
            }
        }
        rowOrderingImpl.columnsAlwaysOrdered = null;
        if (this.columnsAlwaysOrdered != null) {
            rowOrderingImpl.columnsAlwaysOrdered = this.columnsAlwaysOrdered.cloneMe();
        }
    }
    
    private void rememberCurrentColumnOrdering(final int index) {
        this.currentColumnOrdering = this.ordering.get(index);
    }
    
    public String toString() {
        return null;
    }
    
    private boolean unorderedOptimizablesOtherThan(final Optimizable optimizable) {
        for (int i = 0; i < this.unorderedOptimizables.size(); ++i) {
            if (this.unorderedOptimizables.get(i) != optimizable) {
                return true;
            }
        }
        return false;
    }
}
