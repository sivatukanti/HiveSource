// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.tree.xpath;

import org.apache.commons.configuration2.tree.NodeHandler;
import org.apache.commons.jxpath.ri.model.NodePointer;
import java.util.Locale;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointerFactory;

public class ConfigurationNodePointerFactory implements NodePointerFactory
{
    public static final int CONFIGURATION_NODE_POINTER_FACTORY_ORDER = 200;
    
    public int getOrder() {
        return 200;
    }
    
    public NodePointer createNodePointer(final QName name, final Object bean, final Locale locale) {
        if (bean instanceof NodeWrapper) {
            final NodeWrapper<?> wrapper = (NodeWrapper<?>)bean;
            return new ConfigurationNodePointer<Object>(wrapper.getNode(), locale, wrapper.getNodeHandler());
        }
        return null;
    }
    
    public NodePointer createNodePointer(final NodePointer parent, final QName name, final Object bean) {
        if (bean instanceof NodeWrapper) {
            final NodeWrapper<?> wrapper = (NodeWrapper<?>)bean;
            return new ConfigurationNodePointer<Object>((ConfigurationNodePointer<?>)parent, wrapper.getNode(), wrapper.getNodeHandler());
        }
        return null;
    }
    
    public static <T> Object wrapNode(final T node, final NodeHandler<T> handler) {
        return new NodeWrapper(node, (NodeHandler<Object>)handler);
    }
    
    static class NodeWrapper<T>
    {
        private final T node;
        private final NodeHandler<T> nodeHandler;
        
        public NodeWrapper(final T nd, final NodeHandler<T> handler) {
            this.node = nd;
            this.nodeHandler = handler;
        }
        
        public T getNode() {
            return this.node;
        }
        
        public NodeHandler<T> getNodeHandler() {
            return this.nodeHandler;
        }
    }
}
