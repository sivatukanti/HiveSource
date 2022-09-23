// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.tree.xpath;

import org.apache.commons.jxpath.ri.model.NodePointerFactory;
import org.apache.commons.jxpath.ri.JXPathContextReferenceImpl;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;
import java.util.LinkedList;
import org.apache.commons.configuration2.tree.NodeAddData;
import org.apache.commons.jxpath.JXPathContext;
import java.util.Collections;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.configuration2.tree.QueryResult;
import java.util.List;
import org.apache.commons.configuration2.tree.NodeHandler;
import org.apache.commons.configuration2.tree.ExpressionEngine;

public class XPathExpressionEngine implements ExpressionEngine
{
    static final String PATH_DELIMITER = "/";
    static final String ATTR_DELIMITER = "@";
    private static final String NODE_PATH_DELIMITERS = "/@";
    private static final String SPACE = " ";
    private static final int BUF_SIZE = 128;
    private static final char START_INDEX = '[';
    private static final char END_INDEX = ']';
    private final XPathContextFactory contextFactory;
    
    public XPathExpressionEngine() {
        this(new XPathContextFactory());
    }
    
    XPathExpressionEngine(final XPathContextFactory factory) {
        this.contextFactory = factory;
    }
    
    @Override
    public <T> List<QueryResult<T>> query(final T root, final String key, final NodeHandler<T> handler) {
        if (StringUtils.isEmpty(key)) {
            final QueryResult<T> result = createResult(root);
            return Collections.singletonList(result);
        }
        final JXPathContext context = this.createContext(root, handler);
        List<?> results = (List<?>)context.selectNodes(key);
        if (results == null) {
            results = Collections.emptyList();
        }
        return convertResults(results);
    }
    
    @Override
    public <T> String nodeKey(final T node, final String parentKey, final NodeHandler<T> handler) {
        if (parentKey == null) {
            return "";
        }
        if (handler.nodeName(node) == null) {
            return parentKey;
        }
        final StringBuilder buf = new StringBuilder(parentKey.length() + handler.nodeName(node).length() + "/".length());
        if (parentKey.length() > 0) {
            buf.append(parentKey);
            buf.append("/");
        }
        buf.append(handler.nodeName(node));
        return buf.toString();
    }
    
    @Override
    public String attributeKey(final String parentKey, final String attributeName) {
        final StringBuilder buf = new StringBuilder(StringUtils.length(parentKey) + StringUtils.length(attributeName) + "/".length() + "@".length());
        if (StringUtils.isNotEmpty(parentKey)) {
            buf.append(parentKey).append("/");
        }
        buf.append("@").append(attributeName);
        return buf.toString();
    }
    
    @Override
    public <T> String canonicalKey(final T node, final String parentKey, final NodeHandler<T> handler) {
        final T parent = handler.getParent(node);
        if (parent == null) {
            return StringUtils.defaultString(parentKey);
        }
        final StringBuilder buf = new StringBuilder(128);
        if (StringUtils.isNotEmpty(parentKey)) {
            buf.append(parentKey).append("/");
        }
        buf.append(handler.nodeName(node));
        buf.append('[');
        buf.append(determineIndex(parent, node, handler));
        buf.append(']');
        return buf.toString();
    }
    
    @Override
    public <T> NodeAddData<T> prepareAdd(final T root, final String key, final NodeHandler<T> handler) {
        if (key == null) {
            throw new IllegalArgumentException("prepareAdd: key must not be null!");
        }
        String addKey = key;
        int index = findKeySeparator(addKey);
        if (index < 0) {
            addKey = this.generateKeyForAdd(root, addKey, handler);
            index = findKeySeparator(addKey);
        }
        else if (index >= addKey.length() - 1) {
            invalidPath(addKey, " new node path must not be empty.");
        }
        final List<QueryResult<T>> nodes = this.query(root, addKey.substring(0, index).trim(), handler);
        if (nodes.size() != 1) {
            throw new IllegalArgumentException("prepareAdd: key '" + key + "' must select exactly one target node!");
        }
        return this.createNodeAddData(addKey.substring(index).trim(), nodes.get(0));
    }
    
    private <T> JXPathContext createContext(final T root, final NodeHandler<T> handler) {
        return this.getContextFactory().createContext(root, handler);
    }
    
     <T> NodeAddData<T> createNodeAddData(final String path, final QueryResult<T> parentNodeResult) {
        if (parentNodeResult.isAttributeResult()) {
            invalidPath(path, " cannot add properties to an attribute.");
        }
        final List<String> pathNodes = new LinkedList<String>();
        String lastComponent = null;
        boolean attr = false;
        boolean first = true;
        final StringTokenizer tok = new StringTokenizer(path, "/@", true);
        while (tok.hasMoreTokens()) {
            final String token = tok.nextToken();
            if ("/".equals(token)) {
                if (attr) {
                    invalidPath(path, " contains an attribute delimiter at a disallowed position.");
                }
                if (lastComponent == null) {
                    invalidPath(path, " contains a '/' at a disallowed position.");
                }
                pathNodes.add(lastComponent);
                lastComponent = null;
            }
            else if ("@".equals(token)) {
                if (attr) {
                    invalidPath(path, " contains multiple attribute delimiters.");
                }
                if (lastComponent == null && !first) {
                    invalidPath(path, " contains an attribute delimiter at a disallowed position.");
                }
                if (lastComponent != null) {
                    pathNodes.add(lastComponent);
                }
                attr = true;
                lastComponent = null;
            }
            else {
                lastComponent = token;
            }
            first = false;
        }
        if (lastComponent == null) {
            invalidPath(path, "contains no components.");
        }
        return new NodeAddData<T>(parentNodeResult.getNode(), lastComponent, attr, pathNodes);
    }
    
    XPathContextFactory getContextFactory() {
        return this.contextFactory;
    }
    
    private <T> String generateKeyForAdd(final T root, final String key, final NodeHandler<T> handler) {
        for (int pos = key.lastIndexOf("/", key.length()); pos >= 0; pos = key.lastIndexOf("/", pos - 1)) {
            final String keyExisting = key.substring(0, pos);
            if (!this.query(root, keyExisting, handler).isEmpty()) {
                final StringBuilder buf = new StringBuilder(key.length() + 1);
                buf.append(keyExisting).append(" ");
                buf.append(key.substring(pos + 1));
                return buf.toString();
            }
        }
        return " " + key;
    }
    
    private static <T> int determineIndex(final T parent, final T child, final NodeHandler<T> handler) {
        return handler.getChildren(parent, handler.nodeName(child)).indexOf(child) + 1;
    }
    
    private static void invalidPath(final String path, final String msg) {
        throw new IllegalArgumentException("Invalid node path: \"" + path + "\" " + msg);
    }
    
    private static int findKeySeparator(final String key) {
        int index;
        for (index = key.length() - 1; index >= 0 && !Character.isWhitespace(key.charAt(index)); --index) {}
        return index;
    }
    
    private static <T> List<QueryResult<T>> convertResults(final List<?> results) {
        final List<QueryResult<T>> queryResults = new ArrayList<QueryResult<T>>(results.size());
        for (final Object res : results) {
            final QueryResult<T> queryResult = createResult(res);
            queryResults.add(queryResult);
        }
        return queryResults;
    }
    
    private static <T> QueryResult<T> createResult(final Object resObj) {
        if (resObj instanceof QueryResult) {
            return (QueryResult<T>)resObj;
        }
        return QueryResult.createNodeResult(resObj);
    }
    
    static {
        JXPathContextReferenceImpl.addNodePointerFactory((NodePointerFactory)new ConfigurationNodePointerFactory());
    }
}
