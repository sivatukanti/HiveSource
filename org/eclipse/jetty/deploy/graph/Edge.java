// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.deploy.graph;

public final class Edge
{
    private Node _from;
    private Node _to;
    
    public Edge(final Node from, final Node to) {
        if (from == null || to == null || from == to) {
            throw new IllegalArgumentException("from " + from + " to " + to);
        }
        this._from = from;
        this._to = to;
    }
    
    @Override
    public int hashCode() {
        return this._from.hashCode() ^ this._to.hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final Edge other = (Edge)obj;
        if (this._from == null) {
            if (other._from != null) {
                return false;
            }
        }
        else if (!this._from.equals(other._from)) {
            return false;
        }
        if (this._to == null) {
            if (other._to != null) {
                return false;
            }
        }
        else if (!this._to.equals(other._to)) {
            return false;
        }
        return true;
    }
    
    public Node getFrom() {
        return this._from;
    }
    
    public Node getTo() {
        return this._to;
    }
    
    @Override
    public String toString() {
        return this._from + "->" + this._to;
    }
}
