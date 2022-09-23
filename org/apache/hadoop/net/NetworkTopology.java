// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.net;

import org.slf4j.LoggerFactory;
import java.util.Collections;
import com.google.common.collect.Lists;
import java.util.TreeMap;
import java.util.Iterator;
import com.google.common.base.Preconditions;
import com.google.common.annotations.VisibleForTesting;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.conf.Configuration;
import java.util.Random;
import java.util.concurrent.locks.ReadWriteLock;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Unstable
public class NetworkTopology
{
    public static final String DEFAULT_RACK = "/default-rack";
    public static final Logger LOG;
    private static final char PATH_SEPARATOR = '/';
    private static final String PATH_SEPARATOR_STR = "/";
    private static final String ROOT = "/";
    InnerNode.Factory factory;
    InnerNode clusterMap;
    private int depthOfAllLeaves;
    protected int numOfRacks;
    private boolean clusterEverBeenMultiRack;
    protected ReadWriteLock netlock;
    private static final Random r;
    
    public static NetworkTopology getInstance(final Configuration conf) {
        return getInstance(conf, InnerNodeImpl.FACTORY);
    }
    
    public static NetworkTopology getInstance(final Configuration conf, final InnerNode.Factory factory) {
        final NetworkTopology nt = ReflectionUtils.newInstance(conf.getClass("net.topology.impl", NetworkTopology.class, NetworkTopology.class), conf);
        return nt.init(factory);
    }
    
    protected NetworkTopology init(final InnerNode.Factory factory) {
        if (!factory.equals(this.factory)) {
            this.factory = factory;
            this.clusterMap = factory.newInnerNode("");
        }
        return this;
    }
    
    public NetworkTopology() {
        this.depthOfAllLeaves = -1;
        this.numOfRacks = 0;
        this.clusterEverBeenMultiRack = false;
        this.netlock = new ReentrantReadWriteLock(true);
        this.factory = InnerNodeImpl.FACTORY;
        this.clusterMap = this.factory.newInnerNode("");
    }
    
    public void add(final Node node) {
        if (node == null) {
            return;
        }
        final int newDepth = NodeBase.locationToDepth(node.getNetworkLocation()) + 1;
        this.netlock.writeLock().lock();
        try {
            if (node instanceof InnerNode) {
                throw new IllegalArgumentException("Not allow to add an inner node: " + NodeBase.getPath(node));
            }
            if (this.depthOfAllLeaves != -1 && this.depthOfAllLeaves != newDepth) {
                NetworkTopology.LOG.error("Error: can't add leaf node {} at depth {} to topology:{}\n", NodeBase.getPath(node), newDepth, this);
                throw new InvalidTopologyException("Failed to add " + NodeBase.getPath(node) + ": You cannot have a rack and a non-rack node at the same level of the network topology.");
            }
            final Node rack = this.getNodeForNetworkLocation(node);
            if (rack != null && !(rack instanceof InnerNode)) {
                throw new IllegalArgumentException("Unexpected data node " + node.toString() + " at an illegal network location");
            }
            if (this.clusterMap.add(node)) {
                NetworkTopology.LOG.info("Adding a new node: " + NodeBase.getPath(node));
                if (rack == null) {
                    this.incrementRacks();
                }
                if (!(node instanceof InnerNode) && this.depthOfAllLeaves == -1) {
                    this.depthOfAllLeaves = node.getLevel();
                }
            }
            NetworkTopology.LOG.debug("NetworkTopology became:\n{}", this);
        }
        finally {
            this.netlock.writeLock().unlock();
        }
    }
    
    protected void incrementRacks() {
        ++this.numOfRacks;
        if (!this.clusterEverBeenMultiRack && this.numOfRacks > 1) {
            this.clusterEverBeenMultiRack = true;
        }
    }
    
    protected Node getNodeForNetworkLocation(final Node node) {
        return this.getNode(node.getNetworkLocation());
    }
    
    public List<Node> getDatanodesInRack(String loc) {
        this.netlock.readLock().lock();
        try {
            loc = NodeBase.normalize(loc);
            if (!"".equals(loc)) {
                loc = loc.substring(1);
            }
            final InnerNode rack = (InnerNode)this.clusterMap.getLoc(loc);
            if (rack == null) {
                return null;
            }
            return new ArrayList<Node>(rack.getChildren());
        }
        finally {
            this.netlock.readLock().unlock();
        }
    }
    
    public void remove(final Node node) {
        if (node == null) {
            return;
        }
        if (node instanceof InnerNode) {
            throw new IllegalArgumentException("Not allow to remove an inner node: " + NodeBase.getPath(node));
        }
        NetworkTopology.LOG.info("Removing a node: " + NodeBase.getPath(node));
        this.netlock.writeLock().lock();
        try {
            if (this.clusterMap.remove(node)) {
                final InnerNode rack = (InnerNode)this.getNode(node.getNetworkLocation());
                if (rack == null) {
                    --this.numOfRacks;
                }
            }
            NetworkTopology.LOG.debug("NetworkTopology became:\n{}", this);
        }
        finally {
            this.netlock.writeLock().unlock();
        }
    }
    
    public boolean contains(final Node node) {
        if (node == null) {
            return false;
        }
        this.netlock.readLock().lock();
        try {
            Node parent = node.getParent();
            for (int level = node.getLevel(); parent != null && level > 0; parent = parent.getParent(), --level) {
                if (parent == this.clusterMap) {
                    return true;
                }
            }
        }
        finally {
            this.netlock.readLock().unlock();
        }
        return false;
    }
    
    public Node getNode(String loc) {
        this.netlock.readLock().lock();
        try {
            loc = NodeBase.normalize(loc);
            if (!"".equals(loc)) {
                loc = loc.substring(1);
            }
            return this.clusterMap.getLoc(loc);
        }
        finally {
            this.netlock.readLock().unlock();
        }
    }
    
    public boolean hasClusterEverBeenMultiRack() {
        return this.clusterEverBeenMultiRack;
    }
    
    public String getRack(final String loc) {
        return loc;
    }
    
    public int getNumOfRacks() {
        this.netlock.readLock().lock();
        try {
            return this.numOfRacks;
        }
        finally {
            this.netlock.readLock().unlock();
        }
    }
    
    public int getNumOfLeaves() {
        this.netlock.readLock().lock();
        try {
            return this.clusterMap.getNumOfLeaves();
        }
        finally {
            this.netlock.readLock().unlock();
        }
    }
    
    public int getDistance(final Node node1, final Node node2) {
        if ((node1 != null && node1.equals(node2)) || (node1 == null && node2 == null)) {
            return 0;
        }
        if (node1 == null || node2 == null) {
            NetworkTopology.LOG.warn("One of the nodes is a null pointer");
            return Integer.MAX_VALUE;
        }
        Node n1 = node1;
        Node n2 = node2;
        int dis = 0;
        this.netlock.readLock().lock();
        try {
            int level1;
            int level2;
            for (level1 = node1.getLevel(), level2 = node2.getLevel(); n1 != null && level1 > level2; n1 = n1.getParent(), --level1, ++dis) {}
            while (n2 != null && level2 > level1) {
                n2 = n2.getParent();
                --level2;
                ++dis;
            }
            while (n1 != null && n2 != null && n1.getParent() != n2.getParent()) {
                n1 = n1.getParent();
                n2 = n2.getParent();
                dis += 2;
            }
        }
        finally {
            this.netlock.readLock().unlock();
        }
        if (n1 == null) {
            NetworkTopology.LOG.warn("The cluster does not contain node: " + NodeBase.getPath(node1));
            return Integer.MAX_VALUE;
        }
        if (n2 == null) {
            NetworkTopology.LOG.warn("The cluster does not contain node: " + NodeBase.getPath(node2));
            return Integer.MAX_VALUE;
        }
        return dis + 2;
    }
    
    public static int getDistanceByPath(final Node node1, final Node node2) {
        if (node1 == null && node2 == null) {
            return 0;
        }
        if (node1 == null || node2 == null) {
            NetworkTopology.LOG.warn("One of the nodes is a null pointer");
            return Integer.MAX_VALUE;
        }
        final String[] paths1 = NodeBase.getPathComponents(node1);
        final String[] paths2 = NodeBase.getPathComponents(node2);
        int dis = 0;
        for (int index = 0, minLevel = Math.min(paths1.length, paths2.length); index < minLevel; ++index) {
            if (!paths1[index].equals(paths2[index])) {
                dis += 2 * (minLevel - index);
                break;
            }
        }
        dis += Math.abs(paths1.length - paths2.length);
        return dis;
    }
    
    public boolean isOnSameRack(final Node node1, final Node node2) {
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
    
    public boolean isNodeGroupAware() {
        return false;
    }
    
    public boolean isOnSameNodeGroup(final Node node1, final Node node2) {
        return false;
    }
    
    protected boolean isSameParents(final Node node1, final Node node2) {
        return node1.getParent() == node2.getParent();
    }
    
    @VisibleForTesting
    void setRandomSeed(final long seed) {
        NetworkTopology.r.setSeed(seed);
    }
    
    public Node chooseRandom(final String scope) {
        return this.chooseRandom(scope, null);
    }
    
    public Node chooseRandom(final String scope, final Collection<Node> excludedNodes) {
        this.netlock.readLock().lock();
        try {
            if (scope.startsWith("~")) {
                return this.chooseRandom("", scope.substring(1), excludedNodes);
            }
            return this.chooseRandom(scope, null, excludedNodes);
        }
        finally {
            this.netlock.readLock().unlock();
        }
    }
    
    protected Node chooseRandom(final String scope, String excludedScope, final Collection<Node> excludedNodes) {
        if (excludedScope != null) {
            if (scope.startsWith(excludedScope)) {
                return null;
            }
            if (!excludedScope.startsWith(scope)) {
                excludedScope = null;
            }
        }
        Node node = this.getNode(scope);
        if (!(node instanceof InnerNode)) {
            return (excludedNodes != null && excludedNodes.contains(node)) ? null : node;
        }
        final InnerNode innerNode = (InnerNode)node;
        int numOfDatanodes = innerNode.getNumOfLeaves();
        if (excludedScope == null) {
            node = null;
        }
        else {
            node = this.getNode(excludedScope);
            if (!(node instanceof InnerNode)) {
                --numOfDatanodes;
            }
            else {
                numOfDatanodes -= ((InnerNode)node).getNumOfLeaves();
            }
        }
        if (numOfDatanodes <= 0) {
            NetworkTopology.LOG.debug("Failed to find datanode (scope=\"{}\" excludedScope=\"{}\"). numOfDatanodes={}", scope, excludedScope, numOfDatanodes);
            return null;
        }
        int availableNodes;
        if (excludedScope == null) {
            availableNodes = this.countNumOfAvailableNodes(scope, excludedNodes);
        }
        else {
            availableNodes = this.countNumOfAvailableNodes("~" + excludedScope, excludedNodes);
        }
        NetworkTopology.LOG.debug("Choosing random from {} available nodes on node {}, scope={}, excludedScope={}, excludeNodes={}. numOfDatanodes={}.", availableNodes, innerNode, scope, excludedScope, excludedNodes, numOfDatanodes);
        Node ret = null;
        if (availableNodes > 0) {
            ret = this.chooseRandom(innerNode, node, excludedNodes, numOfDatanodes, availableNodes);
        }
        NetworkTopology.LOG.debug("chooseRandom returning {}", ret);
        return ret;
    }
    
    private Node chooseRandom(final InnerNode parentNode, final Node excludedScopeNode, final Collection<Node> excludedNodes, final int totalInScopeNodes, final int availableNodes) {
        Preconditions.checkArgument(totalInScopeNodes >= availableNodes && availableNodes > 0, (Object)String.format("%d should >= %d, and both should be positive.", totalInScopeNodes, availableNodes));
        if (excludedNodes == null || excludedNodes.isEmpty()) {
            final int index = NetworkTopology.r.nextInt(totalInScopeNodes);
            return parentNode.getLeaf(index, excludedScopeNode);
        }
        int nthValidToReturn = NetworkTopology.r.nextInt(availableNodes);
        NetworkTopology.LOG.debug("nthValidToReturn is {}", (Object)nthValidToReturn);
        Node ret = parentNode.getLeaf(NetworkTopology.r.nextInt(totalInScopeNodes), excludedScopeNode);
        if (!excludedNodes.contains(ret)) {
            NetworkTopology.LOG.debug("Chosen node {} from first random", ret);
            return ret;
        }
        ret = null;
        Node lastValidNode = null;
        for (int i = 0; i < totalInScopeNodes; ++i) {
            ret = parentNode.getLeaf(i, excludedScopeNode);
            if (!excludedNodes.contains(ret)) {
                if (nthValidToReturn == 0) {
                    break;
                }
                --nthValidToReturn;
                lastValidNode = ret;
            }
            else {
                NetworkTopology.LOG.debug("Node {} is excluded, continuing.", ret);
                ret = null;
            }
        }
        if (ret == null && lastValidNode != null) {
            NetworkTopology.LOG.error("BUG: Found lastValidNode {} but not nth valid node. parentNode={}, excludedScopeNode={}, excludedNodes={}, totalInScopeNodes={}, availableNodes={}, nthValidToReturn={}.", lastValidNode, parentNode, excludedScopeNode, excludedNodes, totalInScopeNodes, availableNodes, nthValidToReturn);
            ret = lastValidNode;
        }
        return ret;
    }
    
    public List<Node> getLeaves(final String scope) {
        final Node node = this.getNode(scope);
        final List<Node> leafNodes = new ArrayList<Node>();
        if (!(node instanceof InnerNode)) {
            leafNodes.add(node);
        }
        else {
            final InnerNode innerNode = (InnerNode)node;
            for (int i = 0; i < innerNode.getNumOfLeaves(); ++i) {
                leafNodes.add(innerNode.getLeaf(i, null));
            }
        }
        return leafNodes;
    }
    
    @VisibleForTesting
    public int countNumOfAvailableNodes(String scope, final Collection<Node> excludedNodes) {
        boolean isExcluded = false;
        if (scope.startsWith("~")) {
            isExcluded = true;
            scope = scope.substring(1);
        }
        scope = NodeBase.normalize(scope);
        int excludedCountInScope = 0;
        int excludedCountOffScope = 0;
        this.netlock.readLock().lock();
        try {
            if (excludedNodes != null) {
                for (Node node : excludedNodes) {
                    node = this.getNode(NodeBase.getPath(node));
                    if (node == null) {
                        continue;
                    }
                    if ((NodeBase.getPath(node) + "/").startsWith(scope + "/")) {
                        ++excludedCountInScope;
                    }
                    else {
                        ++excludedCountOffScope;
                    }
                }
            }
            final Node n = this.getNode(scope);
            int scopeNodeCount = 0;
            if (n != null) {
                ++scopeNodeCount;
            }
            if (n instanceof InnerNode) {
                scopeNodeCount = ((InnerNode)n).getNumOfLeaves();
            }
            if (isExcluded) {
                return this.clusterMap.getNumOfLeaves() - scopeNodeCount - excludedCountOffScope;
            }
            return scopeNodeCount - excludedCountInScope;
        }
        finally {
            this.netlock.readLock().unlock();
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder tree = new StringBuilder();
        tree.append("Number of racks: ");
        tree.append(this.numOfRacks);
        tree.append("\n");
        final int numOfLeaves = this.getNumOfLeaves();
        tree.append("Expected number of leaves:");
        tree.append(numOfLeaves);
        tree.append("\n");
        for (int i = 0; i < numOfLeaves; ++i) {
            tree.append(NodeBase.getPath(this.clusterMap.getLeaf(i, null)));
            tree.append("\n");
        }
        return tree.toString();
    }
    
    public static String getFirstHalf(final String networkLocation) {
        final int index = networkLocation.lastIndexOf("/");
        return networkLocation.substring(0, index);
    }
    
    public static String getLastHalf(final String networkLocation) {
        final int index = networkLocation.lastIndexOf("/");
        return networkLocation.substring(index);
    }
    
    protected int getWeight(final Node reader, final Node node) {
        int weight = Integer.MAX_VALUE;
        if (reader != null && node != null) {
            if (reader.equals(node)) {
                return 0;
            }
            final int maxReaderLevel = reader.getLevel();
            final int maxNodeLevel = node.getLevel();
            final int currentLevelToCompare = (maxReaderLevel > maxNodeLevel) ? maxNodeLevel : maxReaderLevel;
            Node r = reader;
            Node n = node;
            for (weight = 0; r != null && r.getLevel() > currentLevelToCompare; r = r.getParent(), ++weight) {}
            while (n != null && n.getLevel() > currentLevelToCompare) {
                n = n.getParent();
                ++weight;
            }
            while (r != null && n != null && !r.equals(n)) {
                r = r.getParent();
                n = n.getParent();
                weight += 2;
            }
        }
        return weight;
    }
    
    private static int getWeightUsingNetworkLocation(final Node reader, final Node node) {
        int weight = Integer.MAX_VALUE;
        if (reader != null && node != null) {
            final String readerPath = normalizeNetworkLocationPath(reader.getNetworkLocation());
            final String nodePath = normalizeNetworkLocationPath(node.getNetworkLocation());
            if (readerPath.equals(nodePath)) {
                if (reader.getName().equals(node.getName())) {
                    weight = 0;
                }
                else {
                    weight = 2;
                }
            }
            else {
                String[] readerPathToken;
                String[] nodePathToken;
                int maxLevelToCompare;
                int currentLevel;
                for (readerPathToken = readerPath.split("/"), nodePathToken = nodePath.split("/"), maxLevelToCompare = ((readerPathToken.length > nodePathToken.length) ? nodePathToken.length : readerPathToken.length), currentLevel = 1; currentLevel < maxLevelToCompare && readerPathToken[currentLevel].equals(nodePathToken[currentLevel]); ++currentLevel) {}
                weight = readerPathToken.length - currentLevel + (nodePathToken.length - currentLevel);
            }
        }
        return weight;
    }
    
    private static String normalizeNetworkLocationPath(final String path) {
        if (path == null || path.length() == 0) {
            return "/";
        }
        if (path.charAt(0) != '/') {
            throw new IllegalArgumentException("Network Locationpath doesn't start with /: " + path);
        }
        final int len = path.length();
        if (path.charAt(len - 1) == '/') {
            return path.substring(0, len - 1);
        }
        return path;
    }
    
    public void sortByDistance(final Node reader, final Node[] nodes, final int activeLen) {
        this.sortByDistance(reader, nodes, activeLen, false);
    }
    
    public void sortByDistanceUsingNetworkLocation(final Node reader, final Node[] nodes, final int activeLen) {
        this.sortByDistance(reader, nodes, activeLen, true);
    }
    
    private void sortByDistance(final Node reader, final Node[] nodes, final int activeLen, final boolean nonDataNodeReader) {
        final int[] weights = new int[activeLen];
        for (int i = 0; i < activeLen; ++i) {
            if (nonDataNodeReader) {
                weights[i] = getWeightUsingNetworkLocation(reader, nodes[i]);
            }
            else {
                weights[i] = this.getWeight(reader, nodes[i]);
            }
        }
        final TreeMap<Integer, List<Node>> tree = new TreeMap<Integer, List<Node>>();
        for (int j = 0; j < activeLen; ++j) {
            final int weight = weights[j];
            final Node node = nodes[j];
            List<Node> list = tree.get(weight);
            if (list == null) {
                list = (List<Node>)Lists.newArrayListWithExpectedSize(1);
                tree.put(weight, list);
            }
            list.add(node);
        }
        int idx = 0;
        for (final List<Node> list2 : tree.values()) {
            if (list2 != null) {
                Collections.shuffle(list2, NetworkTopology.r);
                for (final Node n : list2) {
                    nodes[idx] = n;
                    ++idx;
                }
            }
        }
        Preconditions.checkState(idx == activeLen, (Object)"Sorted the wrong number of nodes!");
    }
    
    static {
        LOG = LoggerFactory.getLogger(NetworkTopology.class);
        r = new Random();
    }
    
    public static class InvalidTopologyException extends RuntimeException
    {
        private static final long serialVersionUID = 1L;
        
        public InvalidTopologyException(final String msg) {
            super(msg);
        }
    }
}
