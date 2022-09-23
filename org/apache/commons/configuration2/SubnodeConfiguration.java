// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2;

import org.apache.commons.configuration2.tree.InMemoryNodeModelSupport;
import org.apache.commons.configuration2.tree.NodeKeyResolver;
import org.apache.commons.configuration2.tree.InMemoryNodeModel;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.commons.configuration2.tree.NodeModel;
import org.apache.commons.configuration2.tree.TrackedNodeModel;
import org.apache.commons.configuration2.tree.NodeSelector;

public class SubnodeConfiguration extends BaseHierarchicalConfiguration
{
    private final BaseHierarchicalConfiguration parent;
    private final NodeSelector rootSelector;
    
    public SubnodeConfiguration(final BaseHierarchicalConfiguration parent, final TrackedNodeModel model) {
        super(model);
        if (parent == null) {
            throw new IllegalArgumentException("Parent configuration must not be null!");
        }
        if (model == null) {
            throw new IllegalArgumentException("Node model must not be null!");
        }
        this.parent = parent;
        this.rootSelector = model.getSelector();
    }
    
    public BaseHierarchicalConfiguration getParent() {
        return this.parent;
    }
    
    public NodeSelector getRootSelector() {
        return this.rootSelector;
    }
    
    public void close() {
        this.getTrackedModel().close();
    }
    
    @Override
    public InMemoryNodeModel getNodeModel() {
        return this.getParent().getNodeModel();
    }
    
    @Override
    protected NodeModel<ImmutableNode> cloneNodeModel() {
        final InMemoryNodeModel parentModel = (InMemoryNodeModel)this.getParent().getModel();
        parentModel.trackNode(this.getRootSelector(), this.getParent());
        return new TrackedNodeModel(this.getParent(), this.getRootSelector(), true);
    }
    
    @Override
    protected NodeSelector getSubConfigurationNodeSelector(final String key) {
        return this.getRootSelector().subSelector(key);
    }
    
    @Override
    protected InMemoryNodeModel getSubConfigurationParentModel() {
        return this.getTrackedModel().getParentModel();
    }
    
    private TrackedNodeModel getTrackedModel() {
        return (TrackedNodeModel)this.getModel();
    }
}
