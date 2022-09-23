// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.net;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Unstable
public class NetworkTopologyWithNodeGroup extends NetworkTopology
{
    public static final String DEFAULT_NODEGROUP = "/default-nodegroup";
    
    public NetworkTopologyWithNodeGroup() {
        this.clusterMap = new InnerNodeWithNodeGroup("");
    }
    
    @Override
    protected Node getNodeForNetworkLocation(final Node node) {
        if ("/default-rack".equals(node.getNetworkLocation())) {
            node.setNetworkLocation(node.getNetworkLocation() + "/default-nodegroup");
        }
        Node nodeGroup = this.getNode(node.getNetworkLocation());
        if (nodeGroup == null) {
            nodeGroup = new InnerNodeWithNodeGroup(node.getNetworkLocation());
        }
        return this.getNode(nodeGroup.getNetworkLocation());
    }
    
    @Override
    public String getRack(String loc) {
        this.netlock.readLock().lock();
        try {
            loc = NodeBase.normalize(loc);
            final Node locNode = this.getNode(loc);
            if (!(locNode instanceof InnerNodeWithNodeGroup)) {
                return loc;
            }
            final InnerNodeWithNodeGroup node = (InnerNodeWithNodeGroup)locNode;
            if (node.isRack()) {
                return loc;
            }
            if (node.isNodeGroup()) {
                return node.getNetworkLocation();
            }
            return null;
        }
        finally {
            this.netlock.readLock().unlock();
        }
    }
    
    public String getNodeGroup(String loc) {
        this.netlock.readLock().lock();
        try {
            loc = NodeBase.normalize(loc);
            final Node locNode = this.getNode(loc);
            if (!(locNode instanceof InnerNodeWithNodeGroup)) {
                return loc;
            }
            final InnerNodeWithNodeGroup node = (InnerNodeWithNodeGroup)locNode;
            if (node.isNodeGroup()) {
                return loc;
            }
            if (node.isRack()) {
                return null;
            }
            if (node.getNetworkLocation() != null && !node.getNetworkLocation().isEmpty()) {
                return this.getNodeGroup(node.getNetworkLocation());
            }
            return "";
        }
        finally {
            this.netlock.readLock().unlock();
        }
    }
    
    @Override
    public boolean isOnSameRack(final Node node1, final Node node2) {
        if (node1 == null || node2 == null || node1.getParent() == null || node2.getParent() == null) {
            return false;
        }
        this.netlock.readLock().lock();
        try {
            return this.isSameParents(node1.getParent(), node2.getParent());
        }
        finally {
            this.netlock.readLock().unlock();
        }
    }
    
    @Override
    public boolean isOnSameNodeGroup(final Node node1, final Node node2) {
        if (node1 == null || node2 == null) {
            return false;
        }
        this.netlock.readLock().lock();
        try {
            return this.isSameParents(node1, node2);
        }
        finally {
            this.netlock.readLock().unlock();
        }
    }
    
    @Override
    public boolean isNodeGroupAware() {
        return true;
    }
    
    @Override
    public void add(final Node node) {
        if (node == null) {
            return;
        }
        if (node instanceof InnerNode) {
            throw new IllegalArgumentException("Not allow to add an inner node: " + NodeBase.getPath(node));
        }
        this.netlock.writeLock().lock();
        try {
            Node rack = null;
            if ("/default-rack".equals(node.getNetworkLocation())) {
                node.setNetworkLocation(node.getNetworkLocation() + "/default-nodegroup");
            }
            Node nodeGroup = this.getNode(node.getNetworkLocation());
            if (nodeGroup == null) {
                nodeGroup = new InnerNodeWithNodeGroup(node.getNetworkLocation());
            }
            rack = this.getNode(nodeGroup.getNetworkLocation());
            if (rack != null && (!(rack instanceof InnerNode) || rack.getParent() == null)) {
                throw new IllegalArgumentException("Unexpected data node " + node.toString() + " at an illegal network location");
            }
            if (this.clusterMap.add(node)) {
                NetworkTopologyWithNodeGroup.LOG.info("Adding a new node: " + NodeBase.getPath(node));
                if (rack == null) {
                    this.incrementRacks();
                }
            }
            if (NetworkTopologyWithNodeGroup.LOG.isDebugEnabled()) {
                NetworkTopologyWithNodeGroup.LOG.debug("NetworkTopology became:\n" + this.toString());
            }
        }
        finally {
            this.netlock.writeLock().unlock();
        }
    }
    
    @Override
    public void remove(final Node node) {
        if (node == null) {
            return;
        }
        if (node instanceof InnerNode) {
            throw new IllegalArgumentException("Not allow to remove an inner node: " + NodeBase.getPath(node));
        }
        NetworkTopologyWithNodeGroup.LOG.info("Removing a node: " + NodeBase.getPath(node));
        this.netlock.writeLock().lock();
        try {
            if (this.clusterMap.remove(node)) {
                Node nodeGroup = this.getNode(node.getNetworkLocation());
                if (nodeGroup == null) {
                    nodeGroup = this.factory.newInnerNode(node.getNetworkLocation());
                }
                final InnerNode rack = (InnerNode)this.getNode(nodeGroup.getNetworkLocation());
                if (rack == null) {
                    --this.numOfRacks;
                }
            }
            if (NetworkTopologyWithNodeGroup.LOG.isDebugEnabled()) {
                NetworkTopologyWithNodeGroup.LOG.debug("NetworkTopology became:\n" + this.toString());
            }
        }
        finally {
            this.netlock.writeLock().unlock();
        }
    }
    
    @Override
    protected int getWeight(final Node reader, final Node node) {
        int weight = 3;
        if (reader != null) {
            if (reader.equals(node)) {
                weight = 0;
            }
            else if (this.isOnSameNodeGroup(reader, node)) {
                weight = 1;
            }
            else if (this.isOnSameRack(reader, node)) {
                weight = 2;
            }
        }
        return weight;
    }
    
    @Override
    public void sortByDistance(Node reader, final Node[] nodes, final int activeLen) {
        if (reader != null && !this.contains(reader)) {
            final Node nodeGroup = this.getNode(reader.getNetworkLocation());
            if (nodeGroup == null || !(nodeGroup instanceof InnerNode)) {
                return;
            }
            final InnerNode parentNode = (InnerNode)nodeGroup;
            reader = parentNode.getLeaf(0, null);
        }
        super.sortByDistance(reader, nodes, activeLen);
    }
    
    static class InnerNodeWithNodeGroup extends InnerNodeImpl
    {
        public InnerNodeWithNodeGroup(final String path) {
            super(path);
        }
        
        @Override
        public boolean isRack() {
            if (this.getChildren().isEmpty()) {
                return false;
            }
            final Node firstChild = this.getChildren().get(0);
            if (firstChild instanceof InnerNode) {
                final Node firstGrandChild = ((InnerNode)firstChild).getChildren().get(0);
                return !(firstGrandChild instanceof InnerNode);
            }
            return false;
        }
        
        boolean isNodeGroup() {
            if (this.getChildren().isEmpty()) {
                return true;
            }
            final Node firstChild = this.getChildren().get(0);
            return !(firstChild instanceof InnerNode);
        }
    }
}
