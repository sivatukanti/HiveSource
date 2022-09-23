// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.tree;

import java.util.Iterator;
import org.apache.commons.lang3.StringUtils;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class DefaultExpressionEngine implements ExpressionEngine
{
    public static final DefaultExpressionEngine INSTANCE;
    private final DefaultExpressionEngineSymbols symbols;
    private final NodeMatcher<String> nameMatcher;
    
    public DefaultExpressionEngine(final DefaultExpressionEngineSymbols syms) {
        this(syms, null);
    }
    
    public DefaultExpressionEngine(final DefaultExpressionEngineSymbols syms, final NodeMatcher<String> nodeNameMatcher) {
        if (syms == null) {
            throw new IllegalArgumentException("Symbols must not be null!");
        }
        this.symbols = syms;
        this.nameMatcher = ((nodeNameMatcher != null) ? nodeNameMatcher : NodeNameMatchers.EQUALS);
    }
    
    public DefaultExpressionEngineSymbols getSymbols() {
        return this.symbols;
    }
    
    @Override
    public <T> List<QueryResult<T>> query(final T root, final String key, final NodeHandler<T> handler) {
        final List<QueryResult<T>> results = new LinkedList<QueryResult<T>>();
        this.findNodesForKey(new DefaultConfigurationKey(this, key).iterator(), root, results, handler);
        return results;
    }
    
    @Override
    public <T> String nodeKey(final T node, final String parentKey, final NodeHandler<T> handler) {
        if (parentKey == null) {
            return "";
        }
        final DefaultConfigurationKey key = new DefaultConfigurationKey(this, parentKey);
        key.append(handler.nodeName(node), true);
        return key.toString();
    }
    
    @Override
    public String attributeKey(final String parentKey, final String attributeName) {
        final DefaultConfigurationKey key = new DefaultConfigurationKey(this, parentKey);
        key.appendAttribute(attributeName);
        return key.toString();
    }
    
    @Override
    public <T> String canonicalKey(final T node, final String parentKey, final NodeHandler<T> handler) {
        final String nodeName = handler.nodeName(node);
        final T parent = handler.getParent(node);
        final DefaultConfigurationKey key = new DefaultConfigurationKey(this, parentKey);
        key.append(StringUtils.defaultString(nodeName));
        if (parent != null) {
            key.appendIndex(this.determineIndex(node, parent, nodeName, handler));
        }
        return key.toString();
    }
    
    @Override
    public <T> NodeAddData<T> prepareAdd(final T root, final String key, final NodeHandler<T> handler) {
        final DefaultConfigurationKey.KeyIterator it = new DefaultConfigurationKey(this, key).iterator();
        if (!it.hasNext()) {
            throw new IllegalArgumentException("Key for add operation must be defined!");
        }
        final T parent = this.findLastPathNode(it, root, handler);
        final List<String> pathNodes = new LinkedList<String>();
        while (it.hasNext()) {
            if (!it.isPropertyKey()) {
                throw new IllegalArgumentException("Invalid key for add operation: " + key + " (Attribute key in the middle.)");
            }
            pathNodes.add(it.currentKey());
            it.next();
        }
        return new NodeAddData<T>(parent, it.currentKey(), !it.isPropertyKey(), pathNodes);
    }
    
    protected <T> void findNodesForKey(final DefaultConfigurationKey.KeyIterator keyPart, final T node, final Collection<QueryResult<T>> results, final NodeHandler<T> handler) {
        if (!keyPart.hasNext()) {
            results.add(QueryResult.createNodeResult(node));
        }
        else {
            final String key = keyPart.nextKey(false);
            if (keyPart.isPropertyKey()) {
                this.processSubNodes(keyPart, (List<T>)this.findChildNodesByName((NodeHandler<T>)handler, (T)node, key), results, handler);
            }
            if (keyPart.isAttribute() && !keyPart.hasNext() && handler.getAttributeValue(node, key) != null) {
                results.add(QueryResult.createAttributeResult(node, key));
            }
        }
    }
    
    protected <T> T findLastPathNode(final DefaultConfigurationKey.KeyIterator keyIt, final T node, final NodeHandler<T> handler) {
        final String keyPart = keyIt.nextKey(false);
        if (!keyIt.hasNext()) {
            return node;
        }
        if (!keyIt.isPropertyKey()) {
            throw new IllegalArgumentException("Invalid path for add operation: Attribute key in the middle!");
        }
        final int idx = keyIt.hasIndex() ? keyIt.getIndex() : (handler.getMatchingChildrenCount(node, this.nameMatcher, keyPart) - 1);
        if (idx < 0 || idx >= handler.getMatchingChildrenCount(node, this.nameMatcher, keyPart)) {
            return node;
        }
        return (T)this.findLastPathNode(keyIt, this.findChildNodesByName(handler, node, keyPart).get(idx), (NodeHandler<Object>)handler);
    }
    
    private <T> void processSubNodes(final DefaultConfigurationKey.KeyIterator keyPart, final List<T> subNodes, final Collection<QueryResult<T>> nodes, final NodeHandler<T> handler) {
        if (keyPart.hasIndex()) {
            if (keyPart.getIndex() >= 0 && keyPart.getIndex() < subNodes.size()) {
                this.findNodesForKey((DefaultConfigurationKey.KeyIterator)keyPart.clone(), subNodes.get(keyPart.getIndex()), nodes, handler);
            }
        }
        else {
            for (final T node : subNodes) {
                this.findNodesForKey((DefaultConfigurationKey.KeyIterator)keyPart.clone(), node, nodes, handler);
            }
        }
    }
    
    private <T> int determineIndex(final T node, final T parent, final String nodeName, final NodeHandler<T> handler) {
        return this.findChildNodesByName(handler, parent, nodeName).indexOf(node);
    }
    
    private <T> List<T> findChildNodesByName(final NodeHandler<T> handler, final T parent, final String nodeName) {
        return handler.getMatchingChildren(parent, this.nameMatcher, nodeName);
    }
    
    static {
        INSTANCE = new DefaultExpressionEngine(DefaultExpressionEngineSymbols.DEFAULT_SYMBOLS);
    }
}
