// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.compile.Visitable;
import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.iapi.sql.compile.Visitor;

public class ReferencedTablesVisitor implements Visitor
{
    private JBitSet tableMap;
    
    public ReferencedTablesVisitor(final JBitSet tableMap) {
        this.tableMap = tableMap;
    }
    
    public Visitable visit(final Visitable visitable) throws StandardException {
        if (visitable instanceof ColumnReference) {
            ((ColumnReference)visitable).getTablesReferenced(this.tableMap);
        }
        else if (visitable instanceof Predicate) {
            this.tableMap.or(((Predicate)visitable).getReferencedSet());
        }
        else if (visitable instanceof ResultSetNode) {
            this.tableMap.or(((ResultSetNode)visitable).getReferencedTableMap());
        }
        return visitable;
    }
    
    public boolean skipChildren(final Visitable visitable) {
        return visitable instanceof Predicate || visitable instanceof ResultSetNode;
    }
    
    public boolean visitChildrenFirst(final Visitable visitable) {
        return false;
    }
    
    public boolean stopTraversal() {
        return false;
    }
    
    JBitSet getTableMap() {
        return this.tableMap;
    }
}
