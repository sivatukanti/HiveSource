// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.sort;

import org.apache.derby.iapi.types.DataValueDescriptor;

final class Node
{
    public int balance;
    public Node leftLink;
    public Node rightLink;
    public DataValueDescriptor[] key;
    public int id;
    public Node dupChain;
    public int aux;
    
    public Node(final int id) {
        this.id = id;
        this.reset();
    }
    
    public void reset() {
        this.balance = 0;
        this.leftLink = null;
        this.rightLink = null;
        this.key = null;
        this.dupChain = null;
        this.aux = 0;
    }
    
    public Node link(final int n) {
        if (n < 0) {
            return this.leftLink;
        }
        return this.rightLink;
    }
    
    public void setLink(final int n, final Node node) {
        if (n < 0) {
            this.leftLink = node;
        }
        else {
            this.rightLink = node;
        }
    }
    
    DataValueDescriptor[] getKey() {
        return this.key;
    }
    
    public String toString() {
        return null;
    }
}
