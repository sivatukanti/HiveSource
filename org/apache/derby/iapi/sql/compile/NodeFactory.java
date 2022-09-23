// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.compile;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.context.ContextManager;

public abstract class NodeFactory
{
    public static final String MODULE = "org.apache.derby.iapi.sql.compile.NodeFactory";
    
    public abstract Boolean doJoinOrderOptimization();
    
    public abstract Node getNode(final int p0, final ContextManager p1) throws StandardException;
    
    public final Node getNode(final int n, final Object o, final ContextManager contextManager) throws StandardException {
        final Node node = this.getNode(n, contextManager);
        node.init(o);
        return node;
    }
    
    public final Node getNode(final int n, final Object o, final Object o2, final ContextManager contextManager) throws StandardException {
        final Node node = this.getNode(n, contextManager);
        node.init(o, o2);
        return node;
    }
    
    public final Node getNode(final int n, final Object o, final Object o2, final Object o3, final ContextManager contextManager) throws StandardException {
        final Node node = this.getNode(n, contextManager);
        node.init(o, o2, o3);
        return node;
    }
    
    public Node getNode(final int n, final Object o, final Object o2, final Object o3, final Object o4, final ContextManager contextManager) throws StandardException {
        final Node node = this.getNode(n, contextManager);
        node.init(o, o2, o3, o4);
        return node;
    }
    
    public Node getNode(final int n, final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final ContextManager contextManager) throws StandardException {
        final Node node = this.getNode(n, contextManager);
        node.init(o, o2, o3, o4, o5);
        return node;
    }
    
    public final Node getNode(final int n, final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final ContextManager contextManager) throws StandardException {
        final Node node = this.getNode(n, contextManager);
        node.init(o, o2, o3, o4, o5, o6);
        return node;
    }
    
    public final Node getNode(final int n, final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final ContextManager contextManager) throws StandardException {
        final Node node = this.getNode(n, contextManager);
        node.init(o, o2, o3, o4, o5, o6, o7);
        return node;
    }
    
    public final Node getNode(final int n, final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8, final ContextManager contextManager) throws StandardException {
        final Node node = this.getNode(n, contextManager);
        node.init(o, o2, o3, o4, o5, o6, o7, o8);
        return node;
    }
    
    public final Node getNode(final int n, final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8, final Object o9, final ContextManager contextManager) throws StandardException {
        final Node node = this.getNode(n, contextManager);
        node.init(o, o2, o3, o4, o5, o6, o7, o8, o9);
        return node;
    }
    
    public final Node getNode(final int n, final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8, final Object o9, final Object o10, final ContextManager contextManager) throws StandardException {
        final Node node = this.getNode(n, contextManager);
        node.init(o, o2, o3, o4, o5, o6, o7, o8, o9, o10);
        return node;
    }
    
    public final Node getNode(final int n, final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8, final Object o9, final Object o10, final Object o11, final ContextManager contextManager) throws StandardException {
        final Node node = this.getNode(n, contextManager);
        node.init(o, o2, o3, o4, o5, o6, o7, o8, o9, o10, o11);
        return node;
    }
    
    public final Node getNode(final int n, final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8, final Object o9, final Object o10, final Object o11, final Object o12, final ContextManager contextManager) throws StandardException {
        final Node node = this.getNode(n, contextManager);
        node.init(o, o2, o3, o4, o5, o6, o7, o8, o9, o10, o11, o12);
        return node;
    }
    
    public final Node getNode(final int n, final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8, final Object o9, final Object o10, final Object o11, final Object o12, final Object o13, final ContextManager contextManager) throws StandardException {
        final Node node = this.getNode(n, contextManager);
        node.init(o, o2, o3, o4, o5, o6, o7, o8, o9, o10, o11, o12, o13);
        return node;
    }
    
    public final Node getNode(final int n, final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8, final Object o9, final Object o10, final Object o11, final Object o12, final Object o13, final Object o14, final ContextManager contextManager) throws StandardException {
        final Node node = this.getNode(n, contextManager);
        node.init(o, o2, o3, o4, o5, o6, o7, o8, o9, o10, o11, o12, o13, o14);
        return node;
    }
}
