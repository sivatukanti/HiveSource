// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.compile.Visitable;
import org.apache.derby.iapi.sql.compile.Visitor;

public class RemapCRsVisitor implements Visitor
{
    private boolean remap;
    
    public RemapCRsVisitor(final boolean remap) {
        this.remap = remap;
    }
    
    public Visitable visit(final Visitable visitable) throws StandardException {
        if (visitable instanceof ColumnReference) {
            final ColumnReference columnReference = (ColumnReference)visitable;
            if (this.remap) {
                columnReference.remapColumnReferences();
            }
            else {
                columnReference.unRemapColumnReferences();
            }
        }
        return visitable;
    }
    
    public boolean skipChildren(final Visitable visitable) {
        return visitable instanceof SubqueryNode;
    }
    
    public boolean visitChildrenFirst(final Visitable visitable) {
        return false;
    }
    
    public boolean stopTraversal() {
        return false;
    }
}
