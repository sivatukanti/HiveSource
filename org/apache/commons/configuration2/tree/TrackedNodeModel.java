// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.tree;

import java.util.List;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

public class TrackedNodeModel implements NodeModel<ImmutableNode>
{
    private final InMemoryNodeModelSupport parentModelSupport;
    private final NodeSelector selector;
    private final boolean releaseTrackedNodeOnFinalize;
    private final AtomicBoolean closed;
    
    public TrackedNodeModel(final InMemoryNodeModelSupport modelSupport, final NodeSelector sel, final boolean untrackOnFinalize) {
        if (modelSupport == null) {
            throw new IllegalArgumentException("Underlying model support must not be null!");
        }
        if (sel == null) {
            throw new IllegalArgumentException("Selector must not be null!");
        }
        this.parentModelSupport = modelSupport;
        this.selector = sel;
        this.releaseTrackedNodeOnFinalize = untrackOnFinalize;
        this.closed = new AtomicBoolean();
    }
    
    public InMemoryNodeModelSupport getParentModelSupport() {
        return this.parentModelSupport;
    }
    
    public InMemoryNodeModel getParentModel() {
        return this.getParentModelSupport().getNodeModel();
    }
    
    public NodeSelector getSelector() {
        return this.selector;
    }
    
    public boolean isReleaseTrackedNodeOnFinalize() {
        return this.releaseTrackedNodeOnFinalize;
    }
    
    @Override
    public void setRootNode(final ImmutableNode newRoot) {
        this.getParentModel().replaceTrackedNode(this.getSelector(), newRoot);
    }
    
    @Override
    public NodeHandler<ImmutableNode> getNodeHandler() {
        return this.getParentModel().getTrackedNodeHandler(this.getSelector());
    }
    
    @Override
    public void addProperty(final String key, final Iterable<?> values, final NodeKeyResolver<ImmutableNode> resolver) {
        this.getParentModel().addProperty(key, this.getSelector(), values, resolver);
    }
    
    @Override
    public void addNodes(final String key, final Collection<? extends ImmutableNode> nodes, final NodeKeyResolver<ImmutableNode> resolver) {
        this.getParentModel().addNodes(key, this.getSelector(), nodes, resolver);
    }
    
    @Override
    public void setProperty(final String key, final Object value, final NodeKeyResolver<ImmutableNode> resolver) {
        this.getParentModel().setProperty(key, this.getSelector(), value, resolver);
    }
    
    @Override
    public List<QueryResult<ImmutableNode>> clearTree(final String key, final NodeKeyResolver<ImmutableNode> resolver) {
        return this.getParentModel().clearTree(key, this.getSelector(), resolver);
    }
    
    @Override
    public void clearProperty(final String key, final NodeKeyResolver<ImmutableNode> resolver) {
        this.getParentModel().clearProperty(key, this.getSelector(), resolver);
    }
    
    @Override
    public void clear(final NodeKeyResolver<ImmutableNode> resolver) {
        this.getParentModel().clearTree(null, this.getSelector(), resolver);
    }
    
    @Override
    public ImmutableNode getInMemoryRepresentation() {
        return this.getNodeHandler().getRootNode();
    }
    
    public void close() {
        if (this.closed.compareAndSet(false, true)) {
            this.getParentModel().untrackNode(this.getSelector());
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        if (this.isReleaseTrackedNodeOnFinalize()) {
            this.close();
        }
        super.finalize();
    }
}
