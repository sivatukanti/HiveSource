// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.compile.Visitable;
import org.apache.derby.iapi.sql.compile.Visitor;

public class ReplaceAggregatesWithCRVisitor implements Visitor
{
    private ResultColumnList rcl;
    private Class skipOverClass;
    private int tableNumber;
    
    public ReplaceAggregatesWithCRVisitor(final ResultColumnList list, final int n) {
        this(list, n, null);
    }
    
    public ReplaceAggregatesWithCRVisitor(final ResultColumnList rcl, final int tableNumber, final Class skipOverClass) {
        this.rcl = rcl;
        this.tableNumber = tableNumber;
        this.skipOverClass = skipOverClass;
    }
    
    public ReplaceAggregatesWithCRVisitor(final ResultColumnList rcl, final Class skipOverClass) {
        this.rcl = rcl;
        this.skipOverClass = skipOverClass;
    }
    
    public Visitable visit(Visitable replaceAggregatesWithColumnReferences) throws StandardException {
        if (replaceAggregatesWithColumnReferences instanceof AggregateNode) {
            replaceAggregatesWithColumnReferences = ((AggregateNode)replaceAggregatesWithColumnReferences).replaceAggregatesWithColumnReferences(this.rcl, this.tableNumber);
        }
        return replaceAggregatesWithColumnReferences;
    }
    
    public boolean skipChildren(final Visitable visitable) {
        return this.skipOverClass != null && this.skipOverClass.isInstance(visitable);
    }
    
    public boolean visitChildrenFirst(final Visitable visitable) {
        return false;
    }
    
    public boolean stopTraversal() {
        return false;
    }
}
