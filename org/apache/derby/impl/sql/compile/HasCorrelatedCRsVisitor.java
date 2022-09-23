// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.Visitable;
import org.apache.derby.iapi.sql.compile.Visitor;

public class HasCorrelatedCRsVisitor implements Visitor
{
    private boolean hasCorrelatedCRs;
    
    public Visitable visit(final Visitable visitable) {
        if (visitable instanceof ColumnReference) {
            if (((ColumnReference)visitable).getCorrelated()) {
                this.hasCorrelatedCRs = true;
            }
        }
        else if (visitable instanceof VirtualColumnNode) {
            if (((VirtualColumnNode)visitable).getCorrelated()) {
                this.hasCorrelatedCRs = true;
            }
        }
        else if (visitable instanceof MethodCallNode && (((MethodCallNode)visitable).getMethodName().equals("getTriggerExecutionContext") || ((MethodCallNode)visitable).getMethodName().equals("TriggerOldTransitionRows") || ((MethodCallNode)visitable).getMethodName().equals("TriggerNewTransitionRows"))) {
            this.hasCorrelatedCRs = true;
        }
        return visitable;
    }
    
    public boolean stopTraversal() {
        return this.hasCorrelatedCRs;
    }
    
    public boolean skipChildren(final Visitable visitable) {
        return false;
    }
    
    public boolean visitChildrenFirst(final Visitable visitable) {
        return false;
    }
    
    public boolean hasCorrelatedCRs() {
        return this.hasCorrelatedCRs;
    }
    
    public void setHasCorrelatedCRs(final boolean hasCorrelatedCRs) {
        this.hasCorrelatedCRs = hasCorrelatedCRs;
    }
}
