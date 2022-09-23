// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.tree.xpath;

import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.configuration2.tree.NodeHandler;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.NodeIterator;

abstract class ConfigurationNodeIteratorBase<T> implements NodeIterator
{
    private static final String PREFIX_SEPARATOR = ":";
    private static final String FMT_NAMESPACE = "%s:%s";
    private final ConfigurationNodePointer<T> parent;
    private int position;
    private int startOffset;
    private final boolean reverse;
    
    protected ConfigurationNodeIteratorBase(final ConfigurationNodePointer<T> parent, final boolean reverse) {
        this.parent = parent;
        this.reverse = reverse;
    }
    
    public int getPosition() {
        return this.position;
    }
    
    public boolean setPosition(final int pos) {
        this.position = pos;
        return pos >= 1 && pos <= this.getMaxPosition();
    }
    
    public NodePointer getNodePointer() {
        if (this.getPosition() < 1 && !this.setPosition(1)) {
            return null;
        }
        return this.createNodePointer(this.positionToIndex(this.getPosition()));
    }
    
    protected ConfigurationNodePointer<T> getParent() {
        return this.parent;
    }
    
    protected NodeHandler<T> getNodeHandler() {
        return this.getParent().getNodeHandler();
    }
    
    protected int getStartOffset() {
        return this.startOffset;
    }
    
    protected void setStartOffset(final int startOffset) {
        this.startOffset = startOffset;
        if (this.reverse) {
            --this.startOffset;
        }
        else {
            ++this.startOffset;
        }
    }
    
    protected int getMaxPosition() {
        return this.reverse ? (this.getStartOffset() + 1) : (this.size() - this.getStartOffset());
    }
    
    protected int positionToIndex(final int pos) {
        return (this.reverse ? (1 - pos) : (pos - 1)) + this.getStartOffset();
    }
    
    protected abstract NodePointer createNodePointer(final int p0);
    
    protected abstract int size();
    
    protected static String prefixName(final String prefix, final String name) {
        return String.format("%s:%s", prefix, StringUtils.defaultString(name));
    }
    
    protected static String qualifiedName(final QName name) {
        return (name.getPrefix() == null) ? name.getName() : prefixName(name.getPrefix(), name.getName());
    }
}
