// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.node;

public class ParameterNode extends Node
{
    int position;
    
    public ParameterNode(final NodeType nodeType, final int position) {
        super(nodeType);
        this.position = position;
    }
    
    public ParameterNode(final NodeType nodeType, final Object nodeValue, final int position) {
        super(nodeType, nodeValue);
        this.position = position;
    }
    
    public int getPosition() {
        return this.position;
    }
}
