// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime.tree;

import java.util.List;

public class RewriteRuleNodeStream extends RewriteRuleElementStream
{
    public RewriteRuleNodeStream(final TreeAdaptor adaptor, final String elementDescription) {
        super(adaptor, elementDescription);
    }
    
    public RewriteRuleNodeStream(final TreeAdaptor adaptor, final String elementDescription, final Object oneElement) {
        super(adaptor, elementDescription, oneElement);
    }
    
    public RewriteRuleNodeStream(final TreeAdaptor adaptor, final String elementDescription, final List elements) {
        super(adaptor, elementDescription, elements);
    }
    
    public Object nextNode() {
        return this._next();
    }
    
    protected Object toTree(final Object el) {
        return this.adaptor.dupNode(el);
    }
    
    protected Object dup(final Object el) {
        throw new UnsupportedOperationException("dup can't be called for a node stream.");
    }
}
