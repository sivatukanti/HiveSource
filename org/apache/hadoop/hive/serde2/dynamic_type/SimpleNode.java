// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.dynamic_type;

public class SimpleNode implements Node
{
    protected Node parent;
    protected Node[] children;
    protected int id;
    protected thrift_grammar parser;
    
    public SimpleNode(final int i) {
        this.id = i;
    }
    
    public SimpleNode(final thrift_grammar p, final int i) {
        this(i);
        this.parser = p;
    }
    
    @Override
    public void jjtOpen() {
    }
    
    @Override
    public void jjtClose() {
    }
    
    @Override
    public void jjtSetParent(final Node n) {
        this.parent = n;
    }
    
    @Override
    public Node jjtGetParent() {
        return this.parent;
    }
    
    @Override
    public void jjtAddChild(final Node n, final int i) {
        if (this.children == null) {
            this.children = new Node[i + 1];
        }
        else if (i >= this.children.length) {
            final Node[] c = new Node[i + 1];
            System.arraycopy(this.children, 0, c, 0, this.children.length);
            this.children = c;
        }
        this.children[i] = n;
    }
    
    @Override
    public Node jjtGetChild(final int i) {
        return this.children[i];
    }
    
    @Override
    public int jjtGetNumChildren() {
        return (this.children == null) ? 0 : this.children.length;
    }
    
    @Override
    public String toString() {
        return thrift_grammarTreeConstants.jjtNodeName[this.id];
    }
    
    public String toString(final String prefix) {
        return prefix + this.toString();
    }
    
    public void dump(final String prefix) {
        System.out.println(this.toString(prefix));
        if (this.children != null) {
            for (int i = 0; i < this.children.length; ++i) {
                final SimpleNode n = (SimpleNode)this.children[i];
                if (n != null) {
                    n.dump(prefix + " ");
                }
            }
        }
    }
}
