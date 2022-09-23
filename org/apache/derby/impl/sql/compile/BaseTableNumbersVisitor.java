// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.compile.Visitable;
import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.iapi.sql.compile.Visitor;

public class BaseTableNumbersVisitor implements Visitor
{
    private JBitSet tableMap;
    private int columnNumber;
    
    public BaseTableNumbersVisitor(final JBitSet tableMap) {
        this.tableMap = tableMap;
        this.columnNumber = -1;
    }
    
    protected void setTableMap(final JBitSet tableMap) {
        this.tableMap = tableMap;
    }
    
    protected void reset() {
        this.tableMap.clearAll();
        this.columnNumber = -1;
    }
    
    protected int getColumnNumber() {
        return this.columnNumber;
    }
    
    public Visitable visit(final Visitable visitable) throws StandardException {
        ResultColumn resultColumn = null;
        if (visitable instanceof ColumnReference) {
            resultColumn = ((ColumnReference)visitable).getSource();
            if (resultColumn == null) {
                return visitable;
            }
        }
        else if (visitable instanceof ResultColumn) {
            resultColumn = (ResultColumn)visitable;
        }
        else if (visitable instanceof SelectNode) {
            ((SelectNode)visitable).getFromList().accept(this);
        }
        else if (visitable instanceof FromBaseTable) {
            this.tableMap.set(((FromBaseTable)visitable).getTableNumber());
        }
        if (resultColumn != null) {
            final int tableNumber = resultColumn.getTableNumber();
            if (tableNumber >= 0) {
                ValueNode valueNode;
                for (valueNode = resultColumn.getExpression(); valueNode instanceof VirtualColumnNode; valueNode = resultColumn.getExpression()) {
                    resultColumn = ((VirtualColumnNode)valueNode).getSourceColumn();
                }
                if (valueNode instanceof ColumnReference) {
                    valueNode.accept(this);
                }
                else {
                    this.tableMap.set(tableNumber);
                    this.columnNumber = resultColumn.getColumnPosition();
                }
            }
            else if (visitable instanceof ColumnReference) {
                final ColumnReference columnReference = (ColumnReference)visitable;
                columnReference.getTablesReferenced(this.tableMap);
                this.columnNumber = columnReference.getColumnNumber();
            }
        }
        return visitable;
    }
    
    public boolean skipChildren(final Visitable visitable) {
        return visitable instanceof FromBaseTable || visitable instanceof SelectNode || visitable instanceof PredicateList;
    }
    
    public boolean stopTraversal() {
        return false;
    }
    
    public boolean visitChildrenFirst(final Visitable visitable) {
        return false;
    }
}
