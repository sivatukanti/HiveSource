// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime.tree;

import java.util.List;

public class RewriteRuleSubtreeStream extends RewriteRuleElementStream
{
    public RewriteRuleSubtreeStream(final TreeAdaptor adaptor, final String elementDescription) {
        super(adaptor, elementDescription);
    }
    
    public RewriteRuleSubtreeStream(final TreeAdaptor adaptor, final String elementDescription, final Object oneElement) {
        super(adaptor, elementDescription, oneElement);
    }
    
    public RewriteRuleSubtreeStream(final TreeAdaptor adaptor, final String elementDescription, final List elements) {
        super(adaptor, elementDescription, elements);
    }
    
    public Object nextNode() {
        final int n = this.size();
        if (this.dirty || (this.cursor >= n && n == 1)) {
            final Object el = this._next();
            return this.adaptor.dupNode(el);
        }
        Object tree;
        for (tree = this._next(); this.adaptor.isNil(tree) && this.adaptor.getChildCount(tree) == 1; tree = this.adaptor.getChild(tree, 0)) {}
        final Object el2 = this.adaptor.dupNode(tree);
        return el2;
    }
    
    protected Object dup(final Object el) {
        return this.adaptor.dupTree(el);
    }
}
