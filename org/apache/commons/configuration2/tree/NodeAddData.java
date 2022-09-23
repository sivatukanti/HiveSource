// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import java.util.List;

public class NodeAddData<T>
{
    private final T parent;
    private final List<String> pathNodes;
    private final String newNodeName;
    private final boolean attribute;
    
    public NodeAddData(final T parentNode, final String newName, final boolean isAttr, final Collection<String> intermediateNodes) {
        this.parent = parentNode;
        this.newNodeName = newName;
        this.attribute = isAttr;
        this.pathNodes = createPathNodes(intermediateNodes);
    }
    
    public boolean isAttribute() {
        return this.attribute;
    }
    
    public String getNewNodeName() {
        return this.newNodeName;
    }
    
    public T getParent() {
        return this.parent;
    }
    
    public List<String> getPathNodes() {
        return this.pathNodes;
    }
    
    private static List<String> createPathNodes(final Collection<String> intermediateNodes) {
        if (intermediateNodes == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(intermediateNodes));
    }
}
