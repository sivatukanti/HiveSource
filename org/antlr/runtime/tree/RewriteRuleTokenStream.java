// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime.tree;

import org.antlr.runtime.Token;
import java.util.List;

public class RewriteRuleTokenStream extends RewriteRuleElementStream
{
    public RewriteRuleTokenStream(final TreeAdaptor adaptor, final String elementDescription) {
        super(adaptor, elementDescription);
    }
    
    public RewriteRuleTokenStream(final TreeAdaptor adaptor, final String elementDescription, final Object oneElement) {
        super(adaptor, elementDescription, oneElement);
    }
    
    public RewriteRuleTokenStream(final TreeAdaptor adaptor, final String elementDescription, final List elements) {
        super(adaptor, elementDescription, elements);
    }
    
    public Object nextNode() {
        final Token t = (Token)this._next();
        return this.adaptor.create(t);
    }
    
    public Token nextToken() {
        return (Token)this._next();
    }
    
    protected Object toTree(final Object el) {
        return el;
    }
    
    protected Object dup(final Object el) {
        throw new UnsupportedOperationException("dup can't be called for a token stream.");
    }
}
