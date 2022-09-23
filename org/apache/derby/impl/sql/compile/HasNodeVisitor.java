// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.Visitable;
import org.apache.derby.iapi.sql.compile.Visitor;

public class HasNodeVisitor implements Visitor
{
    protected boolean hasNode;
    private Class nodeClass;
    private Class skipOverClass;
    
    public HasNodeVisitor(final Class nodeClass) {
        this.nodeClass = nodeClass;
    }
    
    public HasNodeVisitor(final Class nodeClass, final Class skipOverClass) {
        this.nodeClass = nodeClass;
        this.skipOverClass = skipOverClass;
    }
    
    public Visitable visit(final Visitable visitable) {
        if (this.nodeClass.isInstance(visitable)) {
            this.hasNode = true;
        }
        return visitable;
    }
    
    public boolean stopTraversal() {
        return this.hasNode;
    }
    
    public boolean skipChildren(final Visitable visitable) {
        return this.skipOverClass != null && this.skipOverClass.isInstance(visitable);
    }
    
    public boolean visitChildrenFirst(final Visitable visitable) {
        return false;
    }
    
    public boolean hasNode() {
        return this.hasNode;
    }
    
    public void reset() {
        this.hasNode = false;
    }
}
