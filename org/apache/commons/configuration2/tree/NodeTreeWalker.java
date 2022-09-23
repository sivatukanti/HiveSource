// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.tree;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

public class NodeTreeWalker
{
    public static final NodeTreeWalker INSTANCE;
    
    public <T> void walkDFS(final T root, final ConfigurationNodeVisitor<T> visitor, final NodeHandler<T> handler) {
        if (checkParameters(root, visitor, handler)) {
            dfs(root, (ConfigurationNodeVisitor<Object>)visitor, (NodeHandler<Object>)handler);
        }
    }
    
    public <T> void walkBFS(final T root, final ConfigurationNodeVisitor<T> visitor, final NodeHandler<T> handler) {
        if (checkParameters(root, visitor, handler)) {
            bfs(root, (ConfigurationNodeVisitor<Object>)visitor, (NodeHandler<Object>)handler);
        }
    }
    
    private static <T> void dfs(final T node, final ConfigurationNodeVisitor<T> visitor, final NodeHandler<T> handler) {
        if (!visitor.terminate()) {
            visitor.visitBeforeChildren(node, handler);
            for (final T c : handler.getChildren(node)) {
                dfs(c, (ConfigurationNodeVisitor<Object>)visitor, (NodeHandler<Object>)handler);
            }
            if (!visitor.terminate()) {
                visitor.visitAfterChildren(node, handler);
            }
        }
    }
    
    private static <T> void bfs(final T root, final ConfigurationNodeVisitor<T> visitor, final NodeHandler<T> handler) {
        final List<T> pendingNodes = new LinkedList<T>();
        pendingNodes.add(root);
        boolean cancel = false;
        while (!pendingNodes.isEmpty() && !cancel) {
            final T node = pendingNodes.remove(0);
            visitor.visitBeforeChildren(node, handler);
            cancel = visitor.terminate();
            for (final T c : handler.getChildren(node)) {
                pendingNodes.add(c);
            }
        }
    }
    
    private static <T> boolean checkParameters(final T root, final ConfigurationNodeVisitor<T> visitor, final NodeHandler<T> handler) {
        if (visitor == null) {
            throw new IllegalArgumentException("Visitor must not be null!");
        }
        if (handler == null) {
            throw new IllegalArgumentException("NodeHandler must not be null!");
        }
        return root != null;
    }
    
    static {
        INSTANCE = new NodeTreeWalker();
    }
}
