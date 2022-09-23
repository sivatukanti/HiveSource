// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.net;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class InnerNodeImpl extends NodeBase implements InnerNode
{
    static final Factory FACTORY;
    protected final List<Node> children;
    protected final Map<String, Node> childrenMap;
    protected int numOfLeaves;
    
    protected InnerNodeImpl(final String path) {
        super(path);
        this.children = new ArrayList<Node>();
        this.childrenMap = new HashMap<String, Node>();
    }
    
    protected InnerNodeImpl(final String name, final String location, final InnerNode parent, final int level) {
        super(name, location, parent, level);
        this.children = new ArrayList<Node>();
        this.childrenMap = new HashMap<String, Node>();
    }
    
    @Override
    public List<Node> getChildren() {
        return this.children;
    }
    
    int getNumOfChildren() {
        return this.children.size();
    }
    
    public boolean isRack() {
        if (this.children.isEmpty()) {
            return true;
        }
        final Node firstChild = this.children.get(0);
        return !(firstChild instanceof InnerNode);
    }
    
    public boolean isAncestor(final Node n) {
        return NodeBase.getPath(this).equals("/") || (n.getNetworkLocation() + "/").startsWith(NodeBase.getPath(this) + "/");
    }
    
    public boolean isParent(final Node n) {
        return n.getNetworkLocation().equals(NodeBase.getPath(this));
    }
    
    public String getNextAncestorName(final Node n) {
        if (!this.isAncestor(n)) {
            throw new IllegalArgumentException(this + "is not an ancestor of " + n);
        }
        String name = n.getNetworkLocation().substring(NodeBase.getPath(this).length());
        if (name.charAt(0) == '/') {
            name = name.substring(1);
        }
        final int index = name.indexOf(47);
        if (index != -1) {
            name = name.substring(0, index);
        }
        return name;
    }
    
    @Override
    public boolean add(final Node n) {
        if (!this.isAncestor(n)) {
            throw new IllegalArgumentException(n.getName() + ", which is located at " + n.getNetworkLocation() + ", is not a descendant of " + NodeBase.getPath(this));
        }
        if (this.isParent(n)) {
            n.setParent(this);
            n.setLevel(this.level + 1);
            final Node prev = this.childrenMap.put(n.getName(), n);
            if (prev != null) {
                for (int i = 0; i < this.children.size(); ++i) {
                    if (this.children.get(i).getName().equals(n.getName())) {
                        this.children.set(i, n);
                        return false;
                    }
                }
            }
            this.children.add(n);
            ++this.numOfLeaves;
            return true;
        }
        final String parentName = this.getNextAncestorName(n);
        InnerNode parentNode = this.childrenMap.get(parentName);
        if (parentNode == null) {
            parentNode = this.createParentNode(parentName);
            this.children.add(parentNode);
            this.childrenMap.put(parentNode.getName(), parentNode);
        }
        if (parentNode.add(n)) {
            ++this.numOfLeaves;
            return true;
        }
        return false;
    }
    
    private InnerNodeImpl createParentNode(final String parentName) {
        return new InnerNodeImpl(parentName, NodeBase.getPath(this), this, this.getLevel() + 1);
    }
    
    @Override
    public boolean remove(final Node n) {
        if (!this.isAncestor(n)) {
            throw new IllegalArgumentException(n.getName() + ", which is located at " + n.getNetworkLocation() + ", is not a descendant of " + NodeBase.getPath(this));
        }
        if (this.isParent(n)) {
            if (this.childrenMap.containsKey(n.getName())) {
                for (int i = 0; i < this.children.size(); ++i) {
                    if (this.children.get(i).getName().equals(n.getName())) {
                        this.children.remove(i);
                        this.childrenMap.remove(n.getName());
                        --this.numOfLeaves;
                        n.setParent(null);
                        return true;
                    }
                }
            }
            return false;
        }
        final String parentName = this.getNextAncestorName(n);
        final InnerNodeImpl parentNode = this.childrenMap.get(parentName);
        if (parentNode == null) {
            return false;
        }
        final boolean isRemoved = parentNode.remove(n);
        if (isRemoved) {
            if (parentNode.getNumOfChildren() == 0) {
                for (int j = 0; j < this.children.size(); ++j) {
                    if (this.children.get(j).getName().equals(parentName)) {
                        this.children.remove(j);
                        this.childrenMap.remove(parentName);
                        break;
                    }
                }
            }
            --this.numOfLeaves;
        }
        return isRemoved;
    }
    
    @Override
    public Node getLoc(final String loc) {
        if (loc == null || loc.length() == 0) {
            return this;
        }
        final String[] path = loc.split("/", 2);
        final Node childnode = this.childrenMap.get(path[0]);
        if (childnode == null) {
            return null;
        }
        if (path.length == 1) {
            return childnode;
        }
        if (childnode instanceof InnerNode) {
            return ((InnerNode)childnode).getLoc(path[1]);
        }
        return null;
    }
    
    @Override
    public Node getLeaf(int leafIndex, Node excludedNode) {
        int count = 0;
        final boolean isLeaf = excludedNode == null || !(excludedNode instanceof InnerNode);
        final int numOfExcludedLeaves = isLeaf ? 1 : ((InnerNode)excludedNode).getNumOfLeaves();
        if (!this.isLeafParent()) {
            for (int i = 0; i < this.children.size(); ++i) {
                final InnerNodeImpl child = this.children.get(i);
                if (excludedNode == null || excludedNode != child) {
                    int numOfLeaves = child.getNumOfLeaves();
                    if (excludedNode != null && child.isAncestor(excludedNode)) {
                        numOfLeaves -= numOfExcludedLeaves;
                    }
                    if (count + numOfLeaves > leafIndex) {
                        return child.getLeaf(leafIndex - count, excludedNode);
                    }
                    count += numOfLeaves;
                }
                else {
                    excludedNode = null;
                }
            }
            return null;
        }
        if (isLeaf && excludedNode != null && this.childrenMap.containsKey(excludedNode.getName())) {
            final int excludedIndex = this.children.indexOf(excludedNode);
            if (excludedIndex != -1 && leafIndex >= 0) {
                leafIndex = ((leafIndex >= excludedIndex) ? (leafIndex + 1) : leafIndex);
            }
        }
        if (leafIndex < 0 || leafIndex >= this.getNumOfChildren()) {
            return null;
        }
        return this.children.get(leafIndex);
    }
    
    private boolean isLeafParent() {
        return this.isRack();
    }
    
    @Override
    public int getNumOfLeaves() {
        return this.numOfLeaves;
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    @Override
    public boolean equals(final Object to) {
        return super.equals(to);
    }
    
    static {
        FACTORY = new Factory();
    }
    
    protected static class Factory implements InnerNode.Factory<InnerNodeImpl>
    {
        @Override
        public InnerNodeImpl newInnerNode(final String path) {
            return new InnerNodeImpl(path);
        }
    }
}
