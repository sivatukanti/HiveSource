// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.deploy.graph;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

public class Path
{
    private final List<Edge> _edges;
    private final List<Node> _nodes;
    
    public Path() {
        this._edges = new CopyOnWriteArrayList<Edge>();
        this._nodes = new CopyOnWriteArrayList<Node>();
    }
    
    public void add(final Edge edge) {
        this._edges.add(edge);
        if (this._nodes.size() == 0) {
            this._nodes.add(edge.getFrom());
        }
        else {
            assert this._nodes.get(this._nodes.size() - 1).equals(edge.getFrom());
        }
        this._nodes.add(edge.getTo());
    }
    
    public Path forkPath() {
        final Path ep = new Path();
        for (final Edge edge : this._edges) {
            ep.add(edge);
        }
        return ep;
    }
    
    public List<Node> getNodes() {
        return this._nodes;
    }
    
    public List<Node> getEdges() {
        return this._nodes;
    }
    
    public Node getNode(final int index) {
        return this._nodes.get(index);
    }
    
    public Node firstNode() {
        if (this._nodes.size() == 0) {
            return null;
        }
        return this._nodes.get(0);
    }
    
    public Node lastNode() {
        if (this._nodes.size() == 0) {
            return null;
        }
        return this._nodes.get(this._nodes.size() - 1);
    }
    
    public int nodes() {
        return this._nodes.size();
    }
    
    public int edges() {
        return this._edges.size();
    }
    
    public boolean isEmpty() {
        return this._edges.isEmpty();
    }
    
    public Edge firstEdge() {
        if (this._edges.size() == 0) {
            return null;
        }
        return this._edges.get(0);
    }
    
    public Edge lastEdge() {
        if (this._edges.size() == 0) {
            return null;
        }
        return this._edges.get(this._edges.size() - 1);
    }
    
    public Edge getEdge(final int index) {
        return this._edges.get(index);
    }
    
    @Override
    public String toString() {
        return super.toString() + this._nodes.toString();
    }
}
