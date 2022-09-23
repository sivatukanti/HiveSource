// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2;

import org.apache.commons.lang3.ObjectUtils;
import java.util.HashMap;
import java.util.LinkedList;
import org.apache.commons.configuration2.tree.ReferenceNodeHandler;
import org.apache.commons.configuration2.tree.ConfigurationNodeVisitorAdapter;
import org.apache.commons.configuration2.tree.ConfigurationNodeVisitor;
import org.apache.commons.configuration2.tree.NodeTreeWalker;
import org.apache.commons.configuration2.event.Event;
import java.util.Collections;
import org.apache.commons.configuration2.tree.NodeHandler;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import java.util.ArrayList;
import org.apache.commons.configuration2.tree.NodeKeyResolver;
import org.apache.commons.configuration2.tree.TrackedNodeModel;
import org.apache.commons.configuration2.tree.NodeSelector;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import org.apache.commons.configuration2.tree.QueryResult;
import java.util.Collection;
import org.apache.commons.configuration2.interpol.ConfigurationInterpolator;
import org.apache.commons.configuration2.tree.InMemoryNodeModel;
import org.apache.commons.configuration2.tree.NodeModel;
import org.apache.commons.configuration2.event.ConfigurationEvent;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.tree.InMemoryNodeModelSupport;
import org.apache.commons.configuration2.tree.ImmutableNode;

public class BaseHierarchicalConfiguration extends AbstractHierarchicalConfiguration<ImmutableNode> implements Cloneable, InMemoryNodeModelSupport
{
    private final EventListener<ConfigurationEvent> changeListener;
    
    public BaseHierarchicalConfiguration() {
        this((HierarchicalConfiguration<ImmutableNode>)null);
    }
    
    public BaseHierarchicalConfiguration(final HierarchicalConfiguration<ImmutableNode> c) {
        this(createNodeModel(c));
    }
    
    protected BaseHierarchicalConfiguration(final NodeModel<ImmutableNode> model) {
        super(model);
        this.changeListener = this.createChangeListener();
    }
    
    @Override
    public InMemoryNodeModel getNodeModel() {
        return (InMemoryNodeModel)super.getNodeModel();
    }
    
    @Override
    public Configuration subset(final String prefix) {
        this.beginRead(false);
        try {
            final List<QueryResult<ImmutableNode>> results = this.fetchNodeList(prefix);
            if (results.isEmpty()) {
                return new BaseHierarchicalConfiguration();
            }
            final BaseHierarchicalConfiguration parent = this;
            final BaseHierarchicalConfiguration result = new BaseHierarchicalConfiguration() {
                @Override
                protected Object interpolate(final Object value) {
                    return parent.interpolate(value);
                }
                
                @Override
                public ConfigurationInterpolator getInterpolator() {
                    return parent.getInterpolator();
                }
            };
            result.getModel().setRootNode(this.createSubsetRootNode(results));
            if (result.isEmpty()) {
                return new BaseHierarchicalConfiguration();
            }
            result.setSynchronizer(this.getSynchronizer());
            return result;
        }
        finally {
            this.endRead();
        }
    }
    
    private ImmutableNode createSubsetRootNode(final Collection<QueryResult<ImmutableNode>> results) {
        final ImmutableNode.Builder builder = new ImmutableNode.Builder();
        Object value = null;
        int valueCount = 0;
        for (final QueryResult<ImmutableNode> result : results) {
            if (result.isAttributeResult()) {
                builder.addAttribute(result.getAttributeName(), result.getAttributeValue(this.getModel().getNodeHandler()));
            }
            else {
                if (result.getNode().getValue() != null) {
                    value = result.getNode().getValue();
                    ++valueCount;
                }
                builder.addChildren(result.getNode().getChildren());
                builder.addAttributes(result.getNode().getAttributes());
            }
        }
        if (valueCount == 1) {
            builder.value(value);
        }
        return builder.create();
    }
    
    @Override
    public HierarchicalConfiguration<ImmutableNode> configurationAt(final String key, final boolean supportUpdates) {
        this.beginRead(false);
        try {
            return supportUpdates ? this.createConnectedSubConfiguration(key) : this.createIndependentSubConfiguration(key);
        }
        finally {
            this.endRead();
        }
    }
    
    protected InMemoryNodeModel getSubConfigurationParentModel() {
        return (InMemoryNodeModel)this.getModel();
    }
    
    protected NodeSelector getSubConfigurationNodeSelector(final String key) {
        return new NodeSelector(key);
    }
    
    protected SubnodeConfiguration createSubConfigurationForTrackedNode(final NodeSelector selector, final InMemoryNodeModelSupport parentModelSupport) {
        final SubnodeConfiguration subConfig = new SubnodeConfiguration(this, new TrackedNodeModel(parentModelSupport, selector, true));
        this.initSubConfigurationForThisParent(subConfig);
        return subConfig;
    }
    
    protected void initSubConfigurationForThisParent(final SubnodeConfiguration subConfig) {
        this.initSubConfiguration(subConfig);
        subConfig.addEventListener(ConfigurationEvent.ANY, this.changeListener);
    }
    
    private BaseHierarchicalConfiguration createConnectedSubConfiguration(final String key) {
        final NodeSelector selector = this.getSubConfigurationNodeSelector(key);
        this.getSubConfigurationParentModel().trackNode(selector, this);
        return this.createSubConfigurationForTrackedNode(selector, this);
    }
    
    private List<HierarchicalConfiguration<ImmutableNode>> createConnectedSubConfigurations(final InMemoryNodeModelSupport parentModelSupport, final Collection<NodeSelector> selectors) {
        final List<HierarchicalConfiguration<ImmutableNode>> configs = new ArrayList<HierarchicalConfiguration<ImmutableNode>>(selectors.size());
        for (final NodeSelector selector : selectors) {
            configs.add(this.createSubConfigurationForTrackedNode(selector, parentModelSupport));
        }
        return configs;
    }
    
    private BaseHierarchicalConfiguration createIndependentSubConfiguration(final String key) {
        final List<ImmutableNode> targetNodes = this.fetchFilteredNodeResults(key);
        if (targetNodes.size() != 1) {
            throw new ConfigurationRuntimeException("Passed in key must select exactly one node: " + key);
        }
        final BaseHierarchicalConfiguration sub = new BaseHierarchicalConfiguration(new InMemoryNodeModel(targetNodes.get(0)));
        this.initSubConfiguration(sub);
        return sub;
    }
    
    private BaseHierarchicalConfiguration createIndependentSubConfigurationForNode(final ImmutableNode node) {
        final BaseHierarchicalConfiguration sub = new BaseHierarchicalConfiguration(new InMemoryNodeModel(node));
        this.initSubConfiguration(sub);
        return sub;
    }
    
    private List<ImmutableNode> fetchFilteredNodeResults(final String key) {
        final NodeHandler<ImmutableNode> handler = this.getModel().getNodeHandler();
        return this.resolveNodeKey(handler.getRootNode(), key, handler);
    }
    
    @Override
    public ImmutableHierarchicalConfiguration immutableConfigurationAt(final String key, final boolean supportUpdates) {
        return ConfigurationUtils.unmodifiableConfiguration(this.configurationAt(key, supportUpdates));
    }
    
    @Override
    public HierarchicalConfiguration<ImmutableNode> configurationAt(final String key) {
        return this.configurationAt(key, false);
    }
    
    @Override
    public ImmutableHierarchicalConfiguration immutableConfigurationAt(final String key) {
        return ConfigurationUtils.unmodifiableConfiguration(this.configurationAt(key));
    }
    
    @Override
    public List<HierarchicalConfiguration<ImmutableNode>> configurationsAt(final String key) {
        this.beginRead(false);
        List<ImmutableNode> nodes;
        try {
            nodes = this.fetchFilteredNodeResults(key);
        }
        finally {
            this.endRead();
        }
        final List<HierarchicalConfiguration<ImmutableNode>> results = new ArrayList<HierarchicalConfiguration<ImmutableNode>>(nodes.size());
        for (final ImmutableNode node : nodes) {
            final BaseHierarchicalConfiguration sub = this.createIndependentSubConfigurationForNode(node);
            results.add(sub);
        }
        return results;
    }
    
    @Override
    public List<HierarchicalConfiguration<ImmutableNode>> configurationsAt(final String key, final boolean supportUpdates) {
        if (!supportUpdates) {
            return this.configurationsAt(key);
        }
        this.beginRead(false);
        InMemoryNodeModel parentModel;
        try {
            parentModel = this.getSubConfigurationParentModel();
        }
        finally {
            this.endRead();
        }
        final Collection<NodeSelector> selectors = parentModel.selectAndTrackNodes(key, this);
        return this.createConnectedSubConfigurations(this, selectors);
    }
    
    @Override
    public List<ImmutableHierarchicalConfiguration> immutableConfigurationsAt(final String key) {
        return toImmutable(this.configurationsAt(key));
    }
    
    @Override
    public List<HierarchicalConfiguration<ImmutableNode>> childConfigurationsAt(final String key) {
        this.beginRead(false);
        List<ImmutableNode> nodes;
        try {
            nodes = this.fetchFilteredNodeResults(key);
        }
        finally {
            this.endRead();
        }
        if (nodes.size() != 1) {
            return Collections.emptyList();
        }
        final ImmutableNode parent = nodes.get(0);
        final List<HierarchicalConfiguration<ImmutableNode>> subs = new ArrayList<HierarchicalConfiguration<ImmutableNode>>(parent.getChildren().size());
        for (final ImmutableNode node : parent.getChildren()) {
            subs.add(this.createIndependentSubConfigurationForNode(node));
        }
        return subs;
    }
    
    @Override
    public List<HierarchicalConfiguration<ImmutableNode>> childConfigurationsAt(final String key, final boolean supportUpdates) {
        if (!supportUpdates) {
            return this.childConfigurationsAt(key);
        }
        final InMemoryNodeModel parentModel = this.getSubConfigurationParentModel();
        return this.createConnectedSubConfigurations(this, parentModel.trackChildNodes(key, this));
    }
    
    @Override
    public List<ImmutableHierarchicalConfiguration> immutableChildConfigurationsAt(final String key) {
        return toImmutable(this.childConfigurationsAt(key));
    }
    
    protected void subnodeConfigurationChanged(final ConfigurationEvent event) {
        this.fireEvent(ConfigurationEvent.SUBNODE_CHANGED, null, event, event.isBeforeUpdate());
    }
    
    private void initSubConfiguration(final BaseHierarchicalConfiguration sub) {
        sub.setSynchronizer(this.getSynchronizer());
        sub.setExpressionEngine(this.getExpressionEngine());
        sub.setListDelimiterHandler(this.getListDelimiterHandler());
        sub.setThrowExceptionOnMissing(this.isThrowExceptionOnMissing());
        sub.getInterpolator().setParentInterpolator(this.getInterpolator());
    }
    
    private EventListener<ConfigurationEvent> createChangeListener() {
        return new EventListener<ConfigurationEvent>() {
            @Override
            public void onEvent(final ConfigurationEvent event) {
                BaseHierarchicalConfiguration.this.subnodeConfigurationChanged(event);
            }
        };
    }
    
    @Override
    public Configuration interpolatedConfiguration() {
        final InterpolatedVisitor visitor = new InterpolatedVisitor();
        final NodeHandler<ImmutableNode> handler = this.getModel().getNodeHandler();
        NodeTreeWalker.INSTANCE.walkDFS(handler.getRootNode(), visitor, handler);
        final BaseHierarchicalConfiguration c = (BaseHierarchicalConfiguration)this.clone();
        c.getNodeModel().setRootNode(visitor.getInterpolatedRoot());
        return c;
    }
    
    @Override
    protected NodeModel<ImmutableNode> cloneNodeModel() {
        return new InMemoryNodeModel(this.getModel().getNodeHandler().getRootNode());
    }
    
    private static List<ImmutableHierarchicalConfiguration> toImmutable(final List<? extends HierarchicalConfiguration<?>> subs) {
        final List<ImmutableHierarchicalConfiguration> res = new ArrayList<ImmutableHierarchicalConfiguration>(subs.size());
        for (final HierarchicalConfiguration<?> sub : subs) {
            res.add(ConfigurationUtils.unmodifiableConfiguration(sub));
        }
        return res;
    }
    
    private static NodeModel<ImmutableNode> createNodeModel(final HierarchicalConfiguration<ImmutableNode> c) {
        final ImmutableNode root = (c != null) ? obtainRootNode(c) : null;
        return new InMemoryNodeModel(root);
    }
    
    private static ImmutableNode obtainRootNode(final HierarchicalConfiguration<ImmutableNode> c) {
        return c.getNodeModel().getNodeHandler().getRootNode();
    }
    
    protected abstract static class BuilderVisitor extends ConfigurationNodeVisitorAdapter<ImmutableNode>
    {
        @Override
        public void visitBeforeChildren(final ImmutableNode node, final NodeHandler<ImmutableNode> handler) {
            final ReferenceNodeHandler refHandler = (ReferenceNodeHandler)handler;
            this.updateNode(node, refHandler);
            this.insertNewChildNodes(node, refHandler);
        }
        
        protected abstract void insert(final ImmutableNode p0, final ImmutableNode p1, final ImmutableNode p2, final ImmutableNode p3, final ReferenceNodeHandler p4);
        
        protected abstract void update(final ImmutableNode p0, final Object p1, final ReferenceNodeHandler p2);
        
        private void updateNode(final ImmutableNode node, final ReferenceNodeHandler refHandler) {
            final Object reference = refHandler.getReference(node);
            if (reference != null) {
                this.update(node, reference, refHandler);
            }
        }
        
        private void insertNewChildNodes(final ImmutableNode node, final ReferenceNodeHandler refHandler) {
            final Collection<ImmutableNode> subNodes = new LinkedList<ImmutableNode>(refHandler.getChildren(node));
            final Iterator<ImmutableNode> children = subNodes.iterator();
            ImmutableNode nd = null;
            while (children.hasNext()) {
                ImmutableNode sibling1;
                do {
                    sibling1 = nd;
                    nd = children.next();
                } while (refHandler.getReference(nd) != null && children.hasNext());
                if (refHandler.getReference(nd) == null) {
                    final List<ImmutableNode> newNodes = new LinkedList<ImmutableNode>();
                    newNodes.add(nd);
                    while (children.hasNext()) {
                        nd = children.next();
                        if (refHandler.getReference(nd) != null) {
                            break;
                        }
                        newNodes.add(nd);
                    }
                    final ImmutableNode sibling2 = (refHandler.getReference(nd) == null) ? null : nd;
                    for (final ImmutableNode insertNode : newNodes) {
                        if (refHandler.getReference(insertNode) == null) {
                            this.insert(insertNode, node, sibling1, sibling2, refHandler);
                            sibling1 = insertNode;
                        }
                    }
                }
            }
        }
    }
    
    private class InterpolatedVisitor extends ConfigurationNodeVisitorAdapter<ImmutableNode>
    {
        private final List<ImmutableNode.Builder> builderStack;
        private ImmutableNode interpolatedRoot;
        
        public InterpolatedVisitor() {
            this.builderStack = new LinkedList<ImmutableNode.Builder>();
        }
        
        public ImmutableNode getInterpolatedRoot() {
            return this.interpolatedRoot;
        }
        
        @Override
        public void visitBeforeChildren(final ImmutableNode node, final NodeHandler<ImmutableNode> handler) {
            if (this.isLeafNode(node, handler)) {
                this.handleLeafNode(node, handler);
            }
            else {
                final ImmutableNode.Builder builder = new ImmutableNode.Builder(handler.getChildrenCount(node, null)).name(handler.nodeName(node)).value(BaseHierarchicalConfiguration.this.interpolate(handler.getValue(node))).addAttributes(this.interpolateAttributes(node, handler));
                this.push(builder);
            }
        }
        
        @Override
        public void visitAfterChildren(final ImmutableNode node, final NodeHandler<ImmutableNode> handler) {
            if (!this.isLeafNode(node, handler)) {
                final ImmutableNode newNode = this.pop().create();
                this.storeInterpolatedNode(newNode);
            }
        }
        
        private void push(final ImmutableNode.Builder builder) {
            this.builderStack.add(0, builder);
        }
        
        private ImmutableNode.Builder pop() {
            return this.builderStack.remove(0);
        }
        
        private ImmutableNode.Builder peek() {
            return this.builderStack.get(0);
        }
        
        private boolean isLeafNode(final ImmutableNode node, final NodeHandler<ImmutableNode> handler) {
            return handler.getChildren(node).isEmpty();
        }
        
        private void handleLeafNode(final ImmutableNode node, final NodeHandler<ImmutableNode> handler) {
            final Object value = BaseHierarchicalConfiguration.this.interpolate(node.getValue());
            final Map<String, Object> interpolatedAttributes = new HashMap<String, Object>();
            final boolean attributeChanged = this.interpolateAttributes(node, handler, interpolatedAttributes);
            final ImmutableNode newNode = (this.valueChanged(value, handler.getValue(node)) || attributeChanged) ? new ImmutableNode.Builder().name(handler.nodeName(node)).value(value).addAttributes(interpolatedAttributes).create() : node;
            this.storeInterpolatedNode(newNode);
        }
        
        private void storeInterpolatedNode(final ImmutableNode node) {
            if (this.builderStack.isEmpty()) {
                this.interpolatedRoot = node;
            }
            else {
                this.peek().addChild(node);
            }
        }
        
        private boolean interpolateAttributes(final ImmutableNode node, final NodeHandler<ImmutableNode> handler, final Map<String, Object> interpolatedAttributes) {
            boolean attributeChanged = false;
            for (final String attr : handler.getAttributes(node)) {
                final Object attrValue = BaseHierarchicalConfiguration.this.interpolate(handler.getAttributeValue(node, attr));
                if (this.valueChanged(attrValue, handler.getAttributeValue(node, attr))) {
                    attributeChanged = true;
                }
                interpolatedAttributes.put(attr, attrValue);
            }
            return attributeChanged;
        }
        
        private Map<String, Object> interpolateAttributes(final ImmutableNode node, final NodeHandler<ImmutableNode> handler) {
            final Map<String, Object> attributes = new HashMap<String, Object>();
            this.interpolateAttributes(node, handler, attributes);
            return attributes;
        }
        
        private boolean valueChanged(final Object interpolatedValue, final Object value) {
            return ObjectUtils.notEqual(interpolatedValue, value);
        }
    }
}
