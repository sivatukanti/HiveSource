// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.compile.Visitable;
import org.apache.derby.iapi.sql.compile.Visitor;

class ConstantExpressionVisitor implements Visitor
{
    public Visitable visit(Visitable evaluateConstantExpressions) throws StandardException {
        if (evaluateConstantExpressions instanceof ValueNode) {
            evaluateConstantExpressions = ((ValueNode)evaluateConstantExpressions).evaluateConstantExpressions();
        }
        return evaluateConstantExpressions;
    }
    
    public boolean stopTraversal() {
        return false;
    }
    
    public boolean skipChildren(final Visitable visitable) {
        return false;
    }
    
    public boolean visitChildrenFirst(final Visitable visitable) {
        return true;
    }
}
