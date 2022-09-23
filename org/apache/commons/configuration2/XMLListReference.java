// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2;

import org.apache.commons.lang3.StringUtils;
import java.util.Iterator;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import java.util.List;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import java.util.ArrayList;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import org.apache.commons.configuration2.tree.ReferenceNodeHandler;
import org.apache.commons.configuration2.tree.ImmutableNode;
import java.util.Map;
import org.w3c.dom.Element;

class XMLListReference
{
    private final Element element;
    
    private XMLListReference(final Element e) {
        this.element = e;
    }
    
    public Element getElement() {
        return this.element;
    }
    
    public static void assignListReference(final Map<ImmutableNode, Object> refs, final ImmutableNode node, final Element elem) {
        if (refs != null) {
            refs.put(node, new XMLListReference(elem));
        }
    }
    
    public static boolean isListNode(final ImmutableNode node, final ReferenceNodeHandler handler) {
        if (hasListReference(node, handler)) {
            return true;
        }
        final ImmutableNode parent = handler.getParent(node);
        if (parent != null) {
            for (int i = 0; i < handler.getChildrenCount(parent, null); ++i) {
                final ImmutableNode child = handler.getChild(parent, i);
                if (hasListReference(child, handler) && nameEquals(node, child)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean isFirstListItem(final ImmutableNode node, final ReferenceNodeHandler handler) {
        final ImmutableNode parent = handler.getParent(node);
        ImmutableNode firstItem = null;
        int idx = 0;
        while (firstItem == null) {
            final ImmutableNode child = handler.getChild(parent, idx);
            if (nameEquals(node, child)) {
                firstItem = child;
            }
            ++idx;
        }
        return firstItem == node;
    }
    
    public static String listValue(final ImmutableNode node, final ReferenceNodeHandler nodeHandler, final ListDelimiterHandler delimiterHandler) {
        final ImmutableNode parent = nodeHandler.getParent(node);
        final List<ImmutableNode> items = nodeHandler.getChildren(parent, node.getNodeName());
        final List<Object> values = new ArrayList<Object>(items.size());
        for (final ImmutableNode n : items) {
            values.add(n.getValue());
        }
        try {
            return String.valueOf(delimiterHandler.escapeList(values, DefaultListDelimiterHandler.NOOP_TRANSFORMER));
        }
        catch (UnsupportedOperationException e) {
            throw new ConfigurationRuntimeException("List handling not supported by the current ListDelimiterHandler! Make sure that the same delimiter handler is used for loading and saving the configuration.", e);
        }
    }
    
    private static boolean hasListReference(final ImmutableNode node, final ReferenceNodeHandler handler) {
        return handler.getReference(node) instanceof XMLListReference;
    }
    
    private static boolean nameEquals(final ImmutableNode n1, final ImmutableNode n2) {
        return StringUtils.equals(n2.getNodeName(), n1.getNodeName());
    }
}
