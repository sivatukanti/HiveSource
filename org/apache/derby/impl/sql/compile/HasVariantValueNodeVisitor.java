// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.compile.Visitable;
import org.apache.derby.iapi.sql.compile.Visitor;

public class HasVariantValueNodeVisitor implements Visitor
{
    private boolean hasVariant;
    private int variantType;
    private boolean ignoreParameters;
    
    public HasVariantValueNodeVisitor() {
        this.variantType = 0;
        this.ignoreParameters = false;
    }
    
    public HasVariantValueNodeVisitor(final int variantType, final boolean ignoreParameters) {
        this.variantType = variantType;
        this.ignoreParameters = ignoreParameters;
    }
    
    public Visitable visit(final Visitable visitable) throws StandardException {
        if (visitable instanceof ValueNode) {
            if (this.ignoreParameters && ((ValueNode)visitable).requiresTypeFromContext()) {
                return visitable;
            }
            if (((ValueNode)visitable).getOrderableVariantType() <= this.variantType) {
                this.hasVariant = true;
            }
        }
        return visitable;
    }
    
    public boolean skipChildren(final Visitable visitable) {
        return false;
    }
    
    public boolean visitChildrenFirst(final Visitable visitable) {
        return false;
    }
    
    public boolean stopTraversal() {
        return this.hasVariant;
    }
    
    public boolean hasVariant() {
        return this.hasVariant;
    }
}
