// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.tree;

class TrackedNodeHandler extends AbstractImmutableNodeHandler
{
    private final ImmutableNode rootNode;
    private final NodeHandler<ImmutableNode> parentHandler;
    
    public TrackedNodeHandler(final ImmutableNode root, final NodeHandler<ImmutableNode> handler) {
        this.rootNode = root;
        this.parentHandler = handler;
    }
    
    public NodeHandler<ImmutableNode> getParentHandler() {
        return this.parentHandler;
    }
    
    @Override
    public ImmutableNode getParent(final ImmutableNode node) {
        return this.getParentHandler().getParent(node);
    }
    
    @Override
    public ImmutableNode getRootNode() {
        return this.rootNode;
    }
}
