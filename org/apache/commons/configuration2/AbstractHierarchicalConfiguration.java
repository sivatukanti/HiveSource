// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2;

import java.util.LinkedHashSet;
import java.util.Stack;
import java.util.Set;
import org.apache.commons.configuration2.tree.ConfigurationNodeVisitorAdapter;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.apache.commons.configuration2.sync.Synchronizer;
import org.apache.commons.configuration2.sync.NoOpSynchronizer;
import org.apache.commons.configuration2.tree.ConfigurationNodeVisitor;
import org.apache.commons.configuration2.tree.NodeTreeWalker;
import java.util.Map;
import java.util.HashMap;
import org.apache.commons.configuration2.tree.NodeUpdateData;
import org.apache.commons.configuration2.tree.NodeAddData;
import java.util.LinkedList;
import org.apache.commons.configuration2.event.ConfigurationEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.configuration2.tree.QueryResult;
import java.util.ArrayList;
import org.apache.commons.configuration2.tree.DefaultExpressionEngine;
import org.apache.commons.configuration2.tree.NodeHandler;
import org.apache.commons.configuration2.tree.ExpressionEngine;
import org.apache.commons.configuration2.tree.NodeModel;
import org.apache.commons.configuration2.tree.NodeKeyResolver;

public abstract class AbstractHierarchicalConfiguration<T> extends AbstractConfiguration implements Cloneable, NodeKeyResolver<T>, HierarchicalConfiguration<T>
{
    private NodeModel<T> model;
    private ExpressionEngine expressionEngine;
    
    protected AbstractHierarchicalConfiguration(final NodeModel<T> nodeModel) {
        this.model = nodeModel;
    }
    
    @Override
    public final String getRootElementName() {
        this.beginRead(false);
        try {
            return this.getRootElementNameInternal();
        }
        finally {
            this.endRead();
        }
    }
    
    protected String getRootElementNameInternal() {
        final NodeHandler<T> nodeHandler = this.getModel().getNodeHandler();
        return nodeHandler.nodeName(nodeHandler.getRootNode());
    }
    
    @Override
    public NodeModel<T> getNodeModel() {
        this.beginRead(false);
        try {
            return this.getModel();
        }
        finally {
            this.endRead();
        }
    }
    
    @Override
    public ExpressionEngine getExpressionEngine() {
        return (this.expressionEngine != null) ? this.expressionEngine : DefaultExpressionEngine.INSTANCE;
    }
    
    @Override
    public void setExpressionEngine(final ExpressionEngine expressionEngine) {
        this.expressionEngine = expressionEngine;
    }
    
    @Override
    protected Object getPropertyInternal(final String key) {
        final List<QueryResult<T>> results = this.fetchNodeList(key);
        if (results.isEmpty()) {
            return null;
        }
        final NodeHandler<T> handler = this.getModel().getNodeHandler();
        final List<Object> list = new ArrayList<Object>();
        for (final QueryResult<T> result : results) {
            final Object value = this.valueFromResult(result, handler);
            if (value != null) {
                list.add(value);
            }
        }
        if (list.size() < 1) {
            return null;
        }
        return (list.size() == 1) ? list.get(0) : list;
    }
    
    @Override
    protected void addPropertyInternal(final String key, final Object obj) {
        this.addPropertyToModel(key, this.getListDelimiterHandler().parse(obj));
    }
    
    @Override
    protected void addPropertyDirect(final String key, final Object value) {
        this.addPropertyToModel(key, Collections.singleton(value));
    }
    
    private void addPropertyToModel(final String key, final Iterable<?> values) {
        this.getModel().addProperty(key, values, this);
    }
    
    @Override
    public final void addNodes(final String key, final Collection<? extends T> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }
        this.beginWrite(false);
        try {
            this.fireEvent(ConfigurationEvent.ADD_NODES, key, nodes, true);
            this.addNodesInternal(key, nodes);
            this.fireEvent(ConfigurationEvent.ADD_NODES, key, nodes, false);
        }
        finally {
            this.endWrite();
        }
    }
    
    protected void addNodesInternal(final String key, final Collection<? extends T> nodes) {
        this.getModel().addNodes(key, nodes, this);
    }
    
    @Override
    protected boolean isEmptyInternal() {
        return !this.nodeDefined(this.getModel().getNodeHandler().getRootNode());
    }
    
    @Override
    protected boolean containsKeyInternal(final String key) {
        return this.getPropertyInternal(key) != null;
    }
    
    @Override
    protected void setPropertyInternal(final String key, final Object value) {
        this.getModel().setProperty(key, value, this);
    }
    
    @Override
    public List<QueryResult<T>> resolveKey(final T root, final String key, final NodeHandler<T> handler) {
        return this.getExpressionEngine().query(root, key, handler);
    }
    
    @Override
    public List<T> resolveNodeKey(final T root, final String key, final NodeHandler<T> handler) {
        final List<QueryResult<T>> results = this.resolveKey(root, key, handler);
        final List<T> targetNodes = new LinkedList<T>();
        for (final QueryResult<T> result : results) {
            if (!result.isAttributeResult()) {
                targetNodes.add(result.getNode());
            }
        }
        return targetNodes;
    }
    
    @Override
    public NodeAddData<T> resolveAddKey(final T root, final String key, final NodeHandler<T> handler) {
        return this.getExpressionEngine().prepareAdd(root, key, handler);
    }
    
    @Override
    public NodeUpdateData<T> resolveUpdateKey(final T root, final String key, final Object newValue, final NodeHandler<T> handler) {
        final Iterator<QueryResult<T>> itNodes = this.fetchNodeList(key).iterator();
        final Iterator<?> itValues = this.getListDelimiterHandler().parse(newValue).iterator();
        final Map<QueryResult<T>, Object> changedValues = new HashMap<QueryResult<T>, Object>();
        Collection<Object> additionalValues = null;
        Collection<QueryResult<T>> removedItems = null;
        while (itNodes.hasNext() && itValues.hasNext()) {
            changedValues.put(itNodes.next(), itValues.next());
        }
        if (itValues.hasNext()) {
            additionalValues = new LinkedList<Object>();
            while (itValues.hasNext()) {
                additionalValues.add(itValues.next());
            }
        }
        if (itNodes.hasNext()) {
            removedItems = new LinkedList<QueryResult<T>>();
            while (itNodes.hasNext()) {
                removedItems.add(itNodes.next());
            }
        }
        return new NodeUpdateData<T>(changedValues, additionalValues, removedItems, key);
    }
    
    @Override
    public String nodeKey(final T node, final Map<T, String> cache, final NodeHandler<T> handler) {
        final List<T> path = new LinkedList<T>();
        T currentNode;
        String key;
        for (currentNode = node, key = cache.get(node); key == null && currentNode != null; currentNode = handler.getParent(currentNode), key = cache.get(currentNode)) {
            path.add(0, currentNode);
        }
        for (final T n : path) {
            final String currentKey = this.getExpressionEngine().canonicalKey(n, key, handler);
            cache.put(n, currentKey);
            key = currentKey;
        }
        return key;
    }
    
    @Override
    protected void clearInternal() {
        this.getModel().clear(this);
    }
    
    @Override
    public final void clearTree(final String key) {
        this.beginWrite(false);
        try {
            this.fireEvent(ConfigurationEvent.CLEAR_TREE, key, null, true);
            final Object nodes = this.clearTreeInternal(key);
            this.fireEvent(ConfigurationEvent.CLEAR_TREE, key, nodes, false);
        }
        finally {
            this.endWrite();
        }
    }
    
    protected Object clearTreeInternal(final String key) {
        return this.getModel().clearTree(key, this);
    }
    
    @Override
    protected void clearPropertyDirect(final String key) {
        this.getModel().clearProperty(key, this);
    }
    
    @Override
    protected int sizeInternal() {
        return this.visitDefinedKeys().getKeyList().size();
    }
    
    @Override
    protected Iterator<String> getKeysInternal() {
        return this.visitDefinedKeys().getKeyList().iterator();
    }
    
    private DefinedKeysVisitor visitDefinedKeys() {
        final DefinedKeysVisitor visitor = new DefinedKeysVisitor();
        final NodeHandler<T> nodeHandler = this.getModel().getNodeHandler();
        NodeTreeWalker.INSTANCE.walkDFS(nodeHandler.getRootNode(), visitor, nodeHandler);
        return visitor;
    }
    
    @Override
    protected Iterator<String> getKeysInternal(final String prefix) {
        final DefinedKeysVisitor visitor = new DefinedKeysVisitor(prefix);
        if (this.containsKey(prefix)) {
            visitor.getKeyList().add(prefix);
        }
        final List<QueryResult<T>> results = this.fetchNodeList(prefix);
        final NodeHandler<T> handler = this.getModel().getNodeHandler();
        for (final QueryResult<T> result : results) {
            if (!result.isAttributeResult()) {
                for (final T c : handler.getChildren(result.getNode())) {
                    NodeTreeWalker.INSTANCE.walkDFS(c, visitor, handler);
                }
                visitor.handleAttributeKeys(prefix, result.getNode(), handler);
            }
        }
        return visitor.getKeyList().iterator();
    }
    
    @Override
    public final int getMaxIndex(final String key) {
        this.beginRead(false);
        try {
            return this.getMaxIndexInternal(key);
        }
        finally {
            this.endRead();
        }
    }
    
    protected int getMaxIndexInternal(final String key) {
        return this.fetchNodeList(key).size() - 1;
    }
    
    public Object clone() {
        this.beginRead(false);
        try {
            final AbstractHierarchicalConfiguration<T> copy = (AbstractHierarchicalConfiguration<T>)super.clone();
            copy.setSynchronizer(NoOpSynchronizer.INSTANCE);
            copy.cloneInterpolator(this);
            copy.setSynchronizer(ConfigurationUtils.cloneSynchronizer(this.getSynchronizer()));
            copy.model = this.cloneNodeModel();
            return copy;
        }
        catch (CloneNotSupportedException cex) {
            throw new ConfigurationRuntimeException(cex);
        }
        finally {
            this.endRead();
        }
    }
    
    protected abstract NodeModel<T> cloneNodeModel();
    
    protected List<QueryResult<T>> fetchNodeList(final String key) {
        final NodeHandler<T> nodeHandler = this.getModel().getNodeHandler();
        return this.resolveKey(nodeHandler.getRootNode(), key, nodeHandler);
    }
    
    protected boolean nodeDefined(final T node) {
        final DefinedVisitor<T> visitor = new DefinedVisitor<T>();
        NodeTreeWalker.INSTANCE.walkBFS(node, visitor, this.getModel().getNodeHandler());
        return visitor.isDefined();
    }
    
    protected NodeModel<T> getModel() {
        return this.model;
    }
    
    private Object valueFromResult(final QueryResult<T> result, final NodeHandler<T> handler) {
        return result.isAttributeResult() ? result.getAttributeValue(handler) : handler.getValue(result.getNode());
    }
    
    private static class DefinedVisitor<T> extends ConfigurationNodeVisitorAdapter<T>
    {
        private boolean defined;
        
        @Override
        public boolean terminate() {
            return this.isDefined();
        }
        
        @Override
        public void visitBeforeChildren(final T node, final NodeHandler<T> handler) {
            this.defined = (handler.getValue(node) != null || !handler.getAttributes(node).isEmpty());
        }
        
        public boolean isDefined() {
            return this.defined;
        }
    }
    
    private class DefinedKeysVisitor extends ConfigurationNodeVisitorAdapter<T>
    {
        private final Set<String> keyList;
        private final Stack<String> parentKeys;
        
        public DefinedKeysVisitor() {
            this.keyList = new LinkedHashSet<String>();
            this.parentKeys = new Stack<String>();
        }
        
        public DefinedKeysVisitor(final AbstractHierarchicalConfiguration abstractHierarchicalConfiguration, final String prefix) {
            this(abstractHierarchicalConfiguration);
            this.parentKeys.push(prefix);
        }
        
        public Set<String> getKeyList() {
            return this.keyList;
        }
        
        @Override
        public void visitAfterChildren(final T node, final NodeHandler<T> handler) {
            this.parentKeys.pop();
        }
        
        @Override
        public void visitBeforeChildren(final T node, final NodeHandler<T> handler) {
            final String parentKey = this.parentKeys.isEmpty() ? null : this.parentKeys.peek();
            final String key = AbstractHierarchicalConfiguration.this.getExpressionEngine().nodeKey(node, parentKey, handler);
            this.parentKeys.push(key);
            if (handler.getValue(node) != null) {
                this.keyList.add(key);
            }
            this.handleAttributeKeys(key, node, handler);
        }
        
        public void handleAttributeKeys(final String parentKey, final T node, final NodeHandler<T> handler) {
            for (final String attr : handler.getAttributes(node)) {
                this.keyList.add(AbstractHierarchicalConfiguration.this.getExpressionEngine().attributeKey(parentKey, attr));
            }
        }
    }
}
