// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.Visitable;

public class HasTableFunctionVisitor extends HasNodeVisitor
{
    public HasTableFunctionVisitor() {
        super(FromVTI.class);
    }
    
    public Visitable visit(final Visitable visitable) {
        if (visitable instanceof FromVTI && ((FromVTI)visitable).isDerbyStyleTableFunction()) {
            this.hasNode = true;
        }
        return visitable;
    }
}
