// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.compile.Visitable;
import org.apache.derby.iapi.sql.compile.Visitor;

class SubstituteExpressionVisitor implements Visitor
{
    private ValueNode source;
    private ValueNode target;
    private Class skipOverClass;
    
    SubstituteExpressionVisitor(final ValueNode source, final ValueNode target, final Class skipOverClass) {
        this.source = source;
        this.target = target;
        this.skipOverClass = skipOverClass;
    }
    
    public ValueNode getSource() {
        return this.source;
    }
    
    public Visitable visit(final Visitable visitable) throws StandardException {
        if (!(visitable instanceof ValueNode)) {
            return visitable;
        }
        if (((ValueNode)visitable).isEquivalent(this.source)) {
            return this.target;
        }
        return visitable;
    }
    
    public boolean stopTraversal() {
        return false;
    }
    
    public boolean skipChildren(final Visitable visitable) {
        return this.skipOverClass != null && this.skipOverClass.isInstance(visitable);
    }
    
    public boolean visitChildrenFirst(final Visitable visitable) {
        return false;
    }
}
