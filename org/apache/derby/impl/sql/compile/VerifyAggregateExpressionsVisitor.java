// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.compile.Visitable;
import org.apache.derby.iapi.sql.compile.Visitor;

public class VerifyAggregateExpressionsVisitor implements Visitor
{
    private GroupByList groupByList;
    
    public VerifyAggregateExpressionsVisitor(final GroupByList groupByList) {
        this.groupByList = groupByList;
    }
    
    public Visitable visit(final Visitable visitable) throws StandardException {
        if (visitable instanceof ColumnReference) {
            final ColumnReference columnReference = (ColumnReference)visitable;
            if (this.groupByList == null) {
                throw StandardException.newException("42Y35", columnReference.getSQLColumnName());
            }
            if (this.groupByList.findGroupingColumn(columnReference) == null) {
                throw StandardException.newException("42Y36", columnReference.getSQLColumnName());
            }
        }
        else if (visitable instanceof SubqueryNode) {
            final SubqueryNode subqueryNode = (SubqueryNode)visitable;
            if (subqueryNode.getSubqueryType() != 17 || subqueryNode.hasCorrelatedCRs()) {
                throw StandardException.newException((this.groupByList == null) ? "42Y29" : "42Y30");
            }
            final HasNodeVisitor hasNodeVisitor = new HasNodeVisitor(AggregateNode.class);
            subqueryNode.accept(hasNodeVisitor);
            if (hasNodeVisitor.hasNode()) {
                throw StandardException.newException((this.groupByList == null) ? "42Y29" : "42Y30");
            }
        }
        return visitable;
    }
    
    public boolean skipChildren(final Visitable visitable) throws StandardException {
        return visitable instanceof AggregateNode || visitable instanceof SubqueryNode || (visitable instanceof ValueNode && this.groupByList != null && this.groupByList.findGroupingColumn((ValueNode)visitable) != null);
    }
    
    public boolean stopTraversal() {
        return false;
    }
    
    public boolean visitChildrenFirst(final Visitable visitable) {
        return false;
    }
}
