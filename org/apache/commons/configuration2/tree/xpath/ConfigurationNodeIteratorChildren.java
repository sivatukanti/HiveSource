// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.tree.xpath;

import java.util.Iterator;
import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import org.apache.commons.jxpath.ri.QName;
import java.util.Collections;
import org.apache.commons.jxpath.ri.compiler.NodeTypeTest;
import org.apache.commons.jxpath.ri.compiler.NodeNameTest;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import java.util.List;

class ConfigurationNodeIteratorChildren<T> extends ConfigurationNodeIteratorBase<T>
{
    private final List<T> subNodes;
    
    public ConfigurationNodeIteratorChildren(final ConfigurationNodePointer<T> parent, final NodeTest nodeTest, final boolean reverse, final ConfigurationNodePointer<T> startsWith) {
        super(parent, reverse);
        final T root = parent.getConfigurationNode();
        this.subNodes = this.createSubNodeList(root, nodeTest);
        if (startsWith != null) {
            this.setStartOffset(this.findStartIndex(this.subNodes, startsWith.getConfigurationNode()));
        }
        else if (reverse) {
            this.setStartOffset(this.size());
        }
    }
    
    @Override
    protected NodePointer createNodePointer(final int position) {
        return new ConfigurationNodePointer<Object>(this.getParent(), this.subNodes.get(position), this.getNodeHandler());
    }
    
    @Override
    protected int size() {
        return this.subNodes.size();
    }
    
    private List<T> createSubNodeList(final T node, final NodeTest test) {
        if (test == null) {
            return this.getNodeHandler().getChildren(node);
        }
        if (test instanceof NodeNameTest) {
            final NodeNameTest nameTest = (NodeNameTest)test;
            final QName name = nameTest.getNodeName();
            return nameTest.isWildcard() ? this.createSubNodeListForWildcardName(node, name) : this.createSubNodeListForName(node, name);
        }
        if (test instanceof NodeTypeTest) {
            final NodeTypeTest typeTest = (NodeTypeTest)test;
            if (typeTest.getNodeType() == 1 || typeTest.getNodeType() == 2) {
                return this.getNodeHandler().getChildren(node);
            }
        }
        return Collections.emptyList();
    }
    
    private List<T> createSubNodeListForName(final T node, final QName name) {
        final String compareName = ConfigurationNodeIteratorBase.qualifiedName(name);
        final List<T> result = new ArrayList<T>();
        for (final T child : this.getNodeHandler().getChildren(node)) {
            if (StringUtils.equals(compareName, this.getNodeHandler().nodeName(child))) {
                result.add(child);
            }
        }
        return result;
    }
    
    private List<T> createSubNodeListForWildcardName(final T node, final QName name) {
        final List<T> children = this.getNodeHandler().getChildren(node);
        if (name.getPrefix() == null) {
            return children;
        }
        final List<T> prefixChildren = new ArrayList<T>(children.size());
        final String prefix = ConfigurationNodeIteratorBase.prefixName(name.getPrefix(), null);
        for (final T child : children) {
            if (StringUtils.startsWith(this.getNodeHandler().nodeName(child), prefix)) {
                prefixChildren.add(child);
            }
        }
        return prefixChildren;
    }
    
    private int findStartIndex(final List<T> children, final T startNode) {
        int index = 0;
        for (final T child : children) {
            if (child == startNode) {
                return index;
            }
            ++index;
        }
        return -1;
    }
}
