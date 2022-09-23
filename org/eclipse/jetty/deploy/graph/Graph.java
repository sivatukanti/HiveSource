// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.deploy.graph;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;

public class Graph
{
    private Set<Node> _nodes;
    private Set<Edge> _edges;
    
    public Graph() {
        this._nodes = new HashSet<Node>();
        this._edges = new HashSet<Edge>();
    }
    
    public void addEdge(Edge edge) {
        Node fromNode = this.getNodeByName(edge.getFrom().getName());
        if (fromNode == null) {
            this.addNode(fromNode = edge.getFrom());
        }
        Node toNode = this.getNodeByName(edge.getTo().getName());
        if (toNode == null) {
            this.addNode(toNode = edge.getTo());
        }
        if (edge.getFrom() != fromNode || edge.getTo() != toNode) {
            edge = new Edge(fromNode, toNode);
        }
        this._edges.add(edge);
    }
    
    public void addEdge(final String from, final String to) {
        Node fromNode = this.getNodeByName(from);
        if (fromNode == null) {
            fromNode = new Node(from);
            this.addNode(fromNode);
        }
        Node toNode = this.getNodeByName(to);
        if (toNode == null) {
            toNode = new Node(to);
            this.addNode(toNode);
        }
        this.addEdge(fromNode, toNode);
    }
    
    private void addEdge(final Node fromNode, final Node toNode) {
        final Edge edge = new Edge(fromNode, toNode);
        this.addEdge(edge);
    }
    
    public void addNode(final Node node) {
        this._nodes.add(node);
    }
    
    public void insertNode(final Edge edge, final String nodeName) {
        Node node = this.getNodeByName(nodeName);
        if (node == null) {
            node = new Node(nodeName);
        }
        this.insertNode(edge, node);
    }
    
    public void insertNode(final Edge edge, final Node node) {
        this.removeEdge(edge);
        this.addNode(node);
        this.addEdge(edge.getFrom(), node);
        this.addEdge(node, edge.getTo());
    }
    
    public Set<Edge> findEdges(final Node node) {
        final Set<Edge> fromedges = new HashSet<Edge>();
        for (final Edge edge : this._edges) {
            if (edge.getFrom() == node || edge.getTo() == node) {
                fromedges.add(edge);
            }
        }
        return fromedges;
    }
    
    public Set<Edge> findEdgesFrom(final Node from) {
        final Set<Edge> fromedges = new HashSet<Edge>();
        for (final Edge edge : this._edges) {
            if (edge.getFrom() == from) {
                fromedges.add(edge);
            }
        }
        return fromedges;
    }
    
    public Path getPath(final String nodeNameOrigin, final String nodeNameDest) {
        if (nodeNameOrigin.equals(nodeNameDest)) {
            return new Path();
        }
        final Node from = this.getNodeByName(nodeNameOrigin);
        final Node to = this.getNodeByName(nodeNameDest);
        return this.getPath(from, to);
    }
    
    public Path getPath(final Node from, final Node to) {
        if (from == to) {
            return new Path();
        }
        final Path path = this.breadthFirst(from, to, new CopyOnWriteArrayList<Path>(), new HashSet<Edge>());
        return path;
    }
    
    private Path breadthFirst(final Node from, final Node destination, final CopyOnWriteArrayList<Path> paths, final Set<Edge> seen) {
        boolean edgesAdded = false;
        if (paths.size() == 0) {
            paths.add(new Path());
        }
        for (final Path path : paths) {
            final Set<Edge> next = this.findEdgesFrom((path.nodes() == 0) ? from : path.lastNode());
            if (next.size() == 0) {
                continue;
            }
            int splits = 0;
            for (final Edge edge : next) {
                if (seen.contains(edge)) {
                    continue;
                }
                seen.add(edge);
                final Path nextPath = (++splits == next.size()) ? path : path.forkPath();
                nextPath.add(edge);
                if (destination.equals(edge.getTo())) {
                    return nextPath;
                }
                edgesAdded = true;
                if (nextPath == path) {
                    continue;
                }
                paths.add(nextPath);
            }
        }
        if (edgesAdded) {
            return this.breadthFirst(from, destination, paths, seen);
        }
        return null;
    }
    
    public Set<Edge> getEdges() {
        return this._edges;
    }
    
    public Node getNodeByName(final String name) {
        for (final Node node : this._nodes) {
            if (node.getName().equals(name)) {
                return node;
            }
        }
        return null;
    }
    
    public Set<Node> getNodes() {
        return this._nodes;
    }
    
    public void removeEdge(final Edge edge) {
        this._edges.remove(edge);
    }
    
    public void removeEdge(final String fromNodeName, final String toNodeName) {
        final Node fromNode = this.getNodeByName(fromNodeName);
        final Node toNode = this.getNodeByName(toNodeName);
        final Edge edge = new Edge(fromNode, toNode);
        this.removeEdge(edge);
    }
    
    public void removeNode(final Node node) {
        this._nodes.remove(node);
    }
    
    public void setEdges(final Set<Edge> edges) {
        this._edges = edges;
    }
    
    public void setNodes(final Set<Node> nodes) {
        this._nodes = nodes;
    }
}
