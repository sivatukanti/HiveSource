// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.tree.xpath;

import org.apache.commons.jxpath.ri.compiler.NodeTypeTest;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import java.util.Iterator;
import org.apache.commons.jxpath.ri.QName;
import java.util.Locale;
import org.apache.commons.configuration2.tree.NodeHandler;
import org.apache.commons.jxpath.ri.model.NodePointer;

class ConfigurationNodePointer<T> extends NodePointer
{
    private static final long serialVersionUID = -1087475639680007713L;
    private final NodeHandler<T> handler;
    private final T node;
    
    public ConfigurationNodePointer(final T node, final Locale locale, final NodeHandler<T> handler) {
        super((NodePointer)null, locale);
        this.node = node;
        this.handler = handler;
    }
    
    public ConfigurationNodePointer(final ConfigurationNodePointer<T> parent, final T node, final NodeHandler<T> handler) {
        super((NodePointer)parent);
        this.node = node;
        this.handler = handler;
    }
    
    public boolean isLeaf() {
        return this.getNodeHandler().getChildrenCount(this.node, null) < 1;
    }
    
    public boolean isCollection() {
        return false;
    }
    
    public int getLength() {
        return 1;
    }
    
    public boolean isAttribute() {
        return false;
    }
    
    public QName getName() {
        return new QName((String)null, this.getNodeHandler().nodeName(this.node));
    }
    
    public Object getBaseValue() {
        return this.node;
    }
    
    public Object getImmediateNode() {
        return this.node;
    }
    
    public Object getValue() {
        return this.getNodeHandler().getValue(this.node);
    }
    
    public void setValue(final Object value) {
        throw new UnsupportedOperationException("Node value cannot be set!");
    }
    
    public int compareChildNodePointers(final NodePointer pointer1, final NodePointer pointer2) {
        final Object node1 = pointer1.getBaseValue();
        final Object node2 = pointer2.getBaseValue();
        for (final T child : this.getNodeHandler().getChildren(this.node)) {
            if (child == node1) {
                return -1;
            }
            if (child == node2) {
                return 1;
            }
        }
        return 0;
    }
    
    public NodeIterator attributeIterator(final QName name) {
        return (NodeIterator)new ConfigurationNodeIteratorAttribute((ConfigurationNodePointer<Object>)this, name);
    }
    
    public NodeIterator childIterator(final NodeTest test, final boolean reverse, final NodePointer startWith) {
        return (NodeIterator)new ConfigurationNodeIteratorChildren((ConfigurationNodePointer<Object>)this, test, reverse, this.castPointer(startWith));
    }
    
    public boolean testNode(final NodeTest test) {
        return (test instanceof NodeTypeTest && ((NodeTypeTest)test).getNodeType() == 2) || super.testNode(test);
    }
    
    public NodeHandler<T> getNodeHandler() {
        return this.handler;
    }
    
    public T getConfigurationNode() {
        return this.node;
    }
    
    private ConfigurationNodePointer<T> castPointer(final NodePointer p) {
        final ConfigurationNodePointer<T> result = (ConfigurationNodePointer<T>)p;
        return result;
    }
}
