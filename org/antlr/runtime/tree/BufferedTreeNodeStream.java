// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime.tree;

import java.util.Iterator;
import java.util.ArrayList;
import org.antlr.runtime.misc.IntArray;
import org.antlr.runtime.TokenStream;
import java.util.List;

public class BufferedTreeNodeStream implements TreeNodeStream
{
    public static final int DEFAULT_INITIAL_BUFFER_SIZE = 100;
    public static final int INITIAL_CALL_STACK_SIZE = 10;
    protected Object down;
    protected Object up;
    protected Object eof;
    protected List nodes;
    protected Object root;
    protected TokenStream tokens;
    TreeAdaptor adaptor;
    protected boolean uniqueNavigationNodes;
    protected int p;
    protected int lastMarker;
    protected IntArray calls;
    
    public BufferedTreeNodeStream(final Object tree) {
        this(new CommonTreeAdaptor(), tree);
    }
    
    public BufferedTreeNodeStream(final TreeAdaptor adaptor, final Object tree) {
        this(adaptor, tree, 100);
    }
    
    public BufferedTreeNodeStream(final TreeAdaptor adaptor, final Object tree, final int initialBufferSize) {
        this.uniqueNavigationNodes = false;
        this.p = -1;
        this.root = tree;
        this.adaptor = adaptor;
        this.nodes = new ArrayList(initialBufferSize);
        this.down = adaptor.create(2, "DOWN");
        this.up = adaptor.create(3, "UP");
        this.eof = adaptor.create(-1, "EOF");
    }
    
    protected void fillBuffer() {
        this.fillBuffer(this.root);
        this.p = 0;
    }
    
    public void fillBuffer(final Object t) {
        final boolean nil = this.adaptor.isNil(t);
        if (!nil) {
            this.nodes.add(t);
        }
        final int n = this.adaptor.getChildCount(t);
        if (!nil && n > 0) {
            this.addNavigationNode(2);
        }
        for (int c = 0; c < n; ++c) {
            final Object child = this.adaptor.getChild(t, c);
            this.fillBuffer(child);
        }
        if (!nil && n > 0) {
            this.addNavigationNode(3);
        }
    }
    
    protected int getNodeIndex(final Object node) {
        if (this.p == -1) {
            this.fillBuffer();
        }
        for (int i = 0; i < this.nodes.size(); ++i) {
            final Object t = this.nodes.get(i);
            if (t == node) {
                return i;
            }
        }
        return -1;
    }
    
    protected void addNavigationNode(final int ttype) {
        Object navNode = null;
        if (ttype == 2) {
            if (this.hasUniqueNavigationNodes()) {
                navNode = this.adaptor.create(2, "DOWN");
            }
            else {
                navNode = this.down;
            }
        }
        else if (this.hasUniqueNavigationNodes()) {
            navNode = this.adaptor.create(3, "UP");
        }
        else {
            navNode = this.up;
        }
        this.nodes.add(navNode);
    }
    
    public Object get(final int i) {
        if (this.p == -1) {
            this.fillBuffer();
        }
        return this.nodes.get(i);
    }
    
    public Object LT(final int k) {
        if (this.p == -1) {
            this.fillBuffer();
        }
        if (k == 0) {
            return null;
        }
        if (k < 0) {
            return this.LB(-k);
        }
        if (this.p + k - 1 >= this.nodes.size()) {
            return this.eof;
        }
        return this.nodes.get(this.p + k - 1);
    }
    
    public Object getCurrentSymbol() {
        return this.LT(1);
    }
    
    protected Object LB(final int k) {
        if (k == 0) {
            return null;
        }
        if (this.p - k < 0) {
            return null;
        }
        return this.nodes.get(this.p - k);
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
    
    public boolean hasUniqueNavigationNodes() {
        return this.uniqueNavigationNodes;
    }
    
    public void setUniqueNavigationNodes(final boolean uniqueNavigationNodes) {
        this.uniqueNavigationNodes = uniqueNavigationNodes;
    }
    
    public void consume() {
        if (this.p == -1) {
            this.fillBuffer();
        }
        ++this.p;
    }
    
    public int LA(final int i) {
        return this.adaptor.getType(this.LT(i));
    }
    
    public int mark() {
        if (this.p == -1) {
            this.fillBuffer();
        }
        return this.lastMarker = this.index();
    }
    
    public void release(final int marker) {
    }
    
    public int index() {
        return this.p;
    }
    
    public void rewind(final int marker) {
        this.seek(marker);
    }
    
    public void rewind() {
        this.seek(this.lastMarker);
    }
    
    public void seek(final int index) {
        if (this.p == -1) {
            this.fillBuffer();
        }
        this.p = index;
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
    
    public void reset() {
        this.p = 0;
        this.lastMarker = 0;
        if (this.calls != null) {
            this.calls.clear();
        }
    }
    
    public int size() {
        if (this.p == -1) {
            this.fillBuffer();
        }
        return this.nodes.size();
    }
    
    public Iterator iterator() {
        if (this.p == -1) {
            this.fillBuffer();
        }
        return new StreamIterator();
    }
    
    public void replaceChildren(final Object parent, final int startChildIndex, final int stopChildIndex, final Object t) {
        if (parent != null) {
            this.adaptor.replaceChildren(parent, startChildIndex, stopChildIndex, t);
        }
    }
    
    public String toTokenTypeString() {
        if (this.p == -1) {
            this.fillBuffer();
        }
        final StringBuffer buf = new StringBuffer();
        for (int i = 0; i < this.nodes.size(); ++i) {
            final Object t = this.nodes.get(i);
            buf.append(" ");
            buf.append(this.adaptor.getType(t));
        }
        return buf.toString();
    }
    
    public String toTokenString(final int start, final int stop) {
        if (this.p == -1) {
            this.fillBuffer();
        }
        final StringBuffer buf = new StringBuffer();
        for (int i = start; i < this.nodes.size() && i <= stop; ++i) {
            final Object t = this.nodes.get(i);
            buf.append(" ");
            buf.append(this.adaptor.getToken(t));
        }
        return buf.toString();
    }
    
    public String toString(final Object start, final Object stop) {
        System.out.println("toString");
        if (start == null || stop == null) {
            return null;
        }
        if (this.p == -1) {
            this.fillBuffer();
        }
        if (start instanceof CommonTree) {
            System.out.print("toString: " + ((CommonTree)start).getToken() + ", ");
        }
        else {
            System.out.println(start);
        }
        if (stop instanceof CommonTree) {
            System.out.println(((CommonTree)stop).getToken());
        }
        else {
            System.out.println(stop);
        }
        if (this.tokens != null) {
            final int beginTokenIndex = this.adaptor.getTokenStartIndex(start);
            int endTokenIndex = this.adaptor.getTokenStopIndex(stop);
            if (this.adaptor.getType(stop) == 3) {
                endTokenIndex = this.adaptor.getTokenStopIndex(start);
            }
            else if (this.adaptor.getType(stop) == -1) {
                endTokenIndex = this.size() - 2;
            }
            return this.tokens.toString(beginTokenIndex, endTokenIndex);
        }
        Object t = null;
        int i;
        for (i = 0; i < this.nodes.size(); ++i) {
            t = this.nodes.get(i);
            if (t == start) {
                break;
            }
        }
        final StringBuffer buf = new StringBuffer();
        for (t = this.nodes.get(i); t != stop; t = this.nodes.get(i)) {
            String text = this.adaptor.getText(t);
            if (text == null) {
                text = " " + String.valueOf(this.adaptor.getType(t));
            }
            buf.append(text);
            ++i;
        }
        String text = this.adaptor.getText(stop);
        if (text == null) {
            text = " " + String.valueOf(this.adaptor.getType(stop));
        }
        buf.append(text);
        return buf.toString();
    }
    
    protected class StreamIterator implements Iterator
    {
        int i;
        
        protected StreamIterator() {
            this.i = 0;
        }
        
        public boolean hasNext() {
            return this.i < BufferedTreeNodeStream.this.nodes.size();
        }
        
        public Object next() {
            final int current = this.i;
            ++this.i;
            if (current < BufferedTreeNodeStream.this.nodes.size()) {
                return BufferedTreeNodeStream.this.nodes.get(current);
            }
            return BufferedTreeNodeStream.this.eof;
        }
        
        public void remove() {
            throw new RuntimeException("cannot remove nodes from stream");
        }
    }
}
