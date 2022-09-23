// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime.debug;

import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.TreeAdaptor;
import org.antlr.runtime.tree.TreeNodeStream;

public class DebugTreeNodeStream implements TreeNodeStream
{
    protected DebugEventListener dbg;
    protected TreeAdaptor adaptor;
    protected TreeNodeStream input;
    protected boolean initialStreamState;
    protected int lastMarker;
    
    public DebugTreeNodeStream(final TreeNodeStream input, final DebugEventListener dbg) {
        this.initialStreamState = true;
        this.input = input;
        this.adaptor = input.getTreeAdaptor();
        this.input.setUniqueNavigationNodes(true);
        this.setDebugListener(dbg);
    }
    
    public void setDebugListener(final DebugEventListener dbg) {
        this.dbg = dbg;
    }
    
    public TreeAdaptor getTreeAdaptor() {
        return this.adaptor;
    }
    
    public void consume() {
        final Object node = this.input.LT(1);
        this.input.consume();
        this.dbg.consumeNode(node);
    }
    
    public Object get(final int i) {
        return this.input.get(i);
    }
    
    public Object LT(final int i) {
        final Object node = this.input.LT(i);
        final int ID = this.adaptor.getUniqueID(node);
        final String text = this.adaptor.getText(node);
        final int type = this.adaptor.getType(node);
        this.dbg.LT(i, node);
        return node;
    }
    
    public int LA(final int i) {
        final Object node = this.input.LT(i);
        final int ID = this.adaptor.getUniqueID(node);
        final String text = this.adaptor.getText(node);
        final int type = this.adaptor.getType(node);
        this.dbg.LT(i, node);
        return type;
    }
    
    public int mark() {
        this.lastMarker = this.input.mark();
        this.dbg.mark(this.lastMarker);
        return this.lastMarker;
    }
    
    public int index() {
        return this.input.index();
    }
    
    public void rewind(final int marker) {
        this.dbg.rewind(marker);
        this.input.rewind(marker);
    }
    
    public void rewind() {
        this.dbg.rewind();
        this.input.rewind(this.lastMarker);
    }
    
    public void release(final int marker) {
    }
    
    public void seek(final int index) {
        this.input.seek(index);
    }
    
    public int size() {
        return this.input.size();
    }
    
    public void reset() {
    }
    
    public Object getTreeSource() {
        return this.input;
    }
    
    public String getSourceName() {
        return this.getTokenStream().getSourceName();
    }
    
    public TokenStream getTokenStream() {
        return this.input.getTokenStream();
    }
    
    public void setUniqueNavigationNodes(final boolean uniqueNavigationNodes) {
        this.input.setUniqueNavigationNodes(uniqueNavigationNodes);
    }
    
    public void replaceChildren(final Object parent, final int startChildIndex, final int stopChildIndex, final Object t) {
        this.input.replaceChildren(parent, startChildIndex, stopChildIndex, t);
    }
    
    public String toString(final Object start, final Object stop) {
        return this.input.toString(start, stop);
    }
}
