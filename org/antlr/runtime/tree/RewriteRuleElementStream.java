// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime.tree;

import java.util.ArrayList;
import java.util.List;

public abstract class RewriteRuleElementStream
{
    protected int cursor;
    protected Object singleElement;
    protected List elements;
    protected boolean dirty;
    protected String elementDescription;
    protected TreeAdaptor adaptor;
    
    public RewriteRuleElementStream(final TreeAdaptor adaptor, final String elementDescription) {
        this.cursor = 0;
        this.dirty = false;
        this.elementDescription = elementDescription;
        this.adaptor = adaptor;
    }
    
    public RewriteRuleElementStream(final TreeAdaptor adaptor, final String elementDescription, final Object oneElement) {
        this(adaptor, elementDescription);
        this.add(oneElement);
    }
    
    public RewriteRuleElementStream(final TreeAdaptor adaptor, final String elementDescription, final List elements) {
        this(adaptor, elementDescription);
        this.singleElement = null;
        this.elements = elements;
    }
    
    public void reset() {
        this.cursor = 0;
        this.dirty = true;
    }
    
    public void add(final Object el) {
        if (el == null) {
            return;
        }
        if (this.elements != null) {
            this.elements.add(el);
            return;
        }
        if (this.singleElement == null) {
            this.singleElement = el;
            return;
        }
        (this.elements = new ArrayList(5)).add(this.singleElement);
        this.singleElement = null;
        this.elements.add(el);
    }
    
    public Object nextTree() {
        final int n = this.size();
        if (this.dirty || (this.cursor >= n && n == 1)) {
            final Object el = this._next();
            return this.dup(el);
        }
        final Object el = this._next();
        return el;
    }
    
    protected Object _next() {
        final int n = this.size();
        if (n == 0) {
            throw new RewriteEmptyStreamException(this.elementDescription);
        }
        if (this.cursor >= n) {
            if (n == 1) {
                return this.toTree(this.singleElement);
            }
            throw new RewriteCardinalityException(this.elementDescription);
        }
        else {
            if (this.singleElement != null) {
                ++this.cursor;
                return this.toTree(this.singleElement);
            }
            final Object o = this.toTree(this.elements.get(this.cursor));
            ++this.cursor;
            return o;
        }
    }
    
    protected abstract Object dup(final Object p0);
    
    protected Object toTree(final Object el) {
        return el;
    }
    
    public boolean hasNext() {
        return (this.singleElement != null && this.cursor < 1) || (this.elements != null && this.cursor < this.elements.size());
    }
    
    public int size() {
        int n = 0;
        if (this.singleElement != null) {
            n = 1;
        }
        if (this.elements != null) {
            return this.elements.size();
        }
        return n;
    }
    
    public String getDescription() {
        return this.elementDescription;
    }
}
