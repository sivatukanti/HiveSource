// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.tree;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class NodeCombiner
{
    protected static final NodeHandler<ImmutableNode> HANDLER;
    private final Set<String> listNodes;
    
    public NodeCombiner() {
        this.listNodes = new HashSet<String>();
    }
    
    public void addListNode(final String nodeName) {
        this.listNodes.add(nodeName);
    }
    
    public Set<String> getListNodes() {
        return Collections.unmodifiableSet((Set<? extends String>)this.listNodes);
    }
    
    public boolean isListNode(final ImmutableNode node) {
        return this.listNodes.contains(node.getNodeName());
    }
    
    public abstract ImmutableNode combine(final ImmutableNode p0, final ImmutableNode p1);
    
    private static NodeHandler<ImmutableNode> createNodeHandler() {
        return new AbstractImmutableNodeHandler() {
            @Override
            public ImmutableNode getParent(final ImmutableNode node) {
                return null;
            }
            
            @Override
            public ImmutableNode getRootNode() {
                return null;
            }
        };
    }
    
    static {
        HANDLER = createNodeHandler();
    }
}
