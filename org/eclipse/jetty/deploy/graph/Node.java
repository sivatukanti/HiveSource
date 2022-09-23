// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.deploy.graph;

public final class Node
{
    private final String _name;
    
    public Node(final String name) {
        assert name != null;
        this._name = name;
    }
    
    public String getName() {
        return this._name;
    }
    
    @Override
    public String toString() {
        return "Node[" + this._name + "]";
    }
    
    @Override
    public int hashCode() {
        return this._name.hashCode();
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
        final Node other = (Node)obj;
        if (this._name == null) {
            if (other._name != null) {
                return false;
            }
        }
        else if (!this._name.equals(other._name)) {
            return false;
        }
        return true;
    }
}
