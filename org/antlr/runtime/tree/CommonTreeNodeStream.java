// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime.tree;

import org.antlr.runtime.misc.IntArray;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.misc.LookaheadStream;

public class CommonTreeNodeStream extends LookaheadStream<Object> implements TreeNodeStream
{
    public static final int DEFAULT_INITIAL_BUFFER_SIZE = 100;
    public static final int INITIAL_CALL_STACK_SIZE = 10;
    protected Object root;
    protected TokenStream tokens;
    TreeAdaptor adaptor;
    protected TreeIterator it;
    protected IntArray calls;
    protected boolean hasNilRoot;
    protected int level;
    
    public CommonTreeNodeStream(final Object tree) {
        this(new CommonTreeAdaptor(), tree);
    }
    
    public CommonTreeNodeStream(final TreeAdaptor adaptor, final Object tree) {
        this.hasNilRoot = false;
        this.level = 0;
        this.root = tree;
        this.adaptor = adaptor;
        this.it = new TreeIterator(adaptor, this.root);
    }
    
    public void reset() {
        super.reset();
        this.it.reset();
        this.hasNilRoot = false;
        this.level = 0;
        if (this.calls != null) {
            this.calls.clear();
        }
    }
    
    public Object nextElement() {
        Object t = this.it.next();
        if (t == this.it.up) {
            --this.level;
            if (this.level == 0 && this.hasNilRoot) {
                return this.it.next();
            }
        }
        else if (t == this.it.down) {
            ++this.level;
        }
        if (this.level == 0 && this.adaptor.isNil(t)) {
            this.hasNilRoot = true;
            t = this.it.next();
            ++this.level;
            t = this.it.next();
        }
        return t;
    }
    
    public boolean isEOF(final Object o) {
        return this.adaptor.getType(o) == -1;
    }
    
    public void setUniqueNavigationNodes(final boolean uniqueNavigationNodes) {
    }
    
    public Object getTreeSource() {
        return this.root;
    }
    
    public String getSourceName() {
        return this.getTokenStream().getSourceName();
    }
    
    public TokenStream getTokenStream() {
        return this.tokens;
    }
    
    public void setTokenStream(final TokenStream tokens) {
        this.tokens = tokens;
    }
    
    public TreeAdaptor getTreeAdaptor() {
        return this.adaptor;
    }
    
    public void setTreeAdaptor(final TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    
    public Object get(final int i) {
        throw new UnsupportedOperationException("Absolute node indexes are meaningless in an unbuffered stream");
    }
    
    public int LA(final int i) {
        return this.adaptor.getType(this.LT(i));
    }
    
    public void push(final int index) {
        if (this.calls == null) {
            this.calls = new IntArray();
        }
        this.calls.push(this.p);
        this.seek(index);
    }
    
    public int pop() {
        final int ret = this.calls.pop();
        this.seek(ret);
        return ret;
    }
    
    public void replaceChildren(final Object parent, final int startChildIndex, final int stopChildIndex, final Object t) {
        if (parent != null) {
            this.adaptor.replaceChildren(parent, startChildIndex, stopChildIndex, t);
        }
    }
    
    public String toString(final Object start, final Object stop) {
        return "n/a";
    }
    
    public String toTokenTypeString() {
        this.reset();
        final StringBuffer buf = new StringBuffer();
        Object o = this.LT(1);
        for (int type = this.adaptor.getType(o); type != -1; type = this.adaptor.getType(o)) {
            buf.append(" ");
            buf.append(type);
            this.consume();
            o = this.LT(1);
        }
        return buf.toString();
    }
}
