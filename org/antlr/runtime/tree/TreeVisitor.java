// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime.tree;

public class TreeVisitor
{
    protected TreeAdaptor adaptor;
    
    public TreeVisitor(final TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    
    public TreeVisitor() {
        this(new CommonTreeAdaptor());
    }
    
    public Object visit(Object t, final TreeVisitorAction action) {
        final boolean isNil = this.adaptor.isNil(t);
        if (action != null && !isNil) {
            t = action.pre(t);
        }
        for (int i = 0; i < this.adaptor.getChildCount(t); ++i) {
            final Object child = this.adaptor.getChild(t, i);
            final Object visitResult = this.visit(child, action);
            final Object childAfterVisit = this.adaptor.getChild(t, i);
            if (visitResult != childAfterVisit) {
                this.adaptor.setChild(t, i, visitResult);
            }
        }
        if (action != null && !isNil) {
            t = action.post(t);
        }
        return t;
    }
}
