// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.sort;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataValueDescriptor;

class SortBuffer
{
    public static final int INSERT_OK = 0;
    public static final int INSERT_DUPLICATE = 1;
    public static final int INSERT_FULL = 2;
    private MergeSort sort;
    private NodeAllocator allocator;
    private Node head;
    private int height;
    private DataValueDescriptor[] deletedKey;
    private boolean subtreeShrunk;
    private int nextAux;
    private int lastAux;
    
    void setNextAux(final int nextAux) {
        this.nextAux = nextAux;
    }
    
    int getLastAux() {
        return this.lastAux;
    }
    
    SortBuffer(final MergeSort sort) {
        this.allocator = null;
        this.head = null;
        this.height = 0;
        this.sort = sort;
    }
    
    boolean init() {
        this.allocator = new NodeAllocator();
        boolean b;
        if (this.sort.sortBufferMin > 0) {
            b = this.allocator.init(this.sort.sortBufferMin, this.sort.sortBufferMax);
        }
        else {
            b = this.allocator.init(this.sort.sortBufferMax);
        }
        if (!b) {
            this.allocator = null;
            return false;
        }
        this.reset();
        return true;
    }
    
    void reset() {
        this.allocator.reset();
        this.head = this.allocator.newNode();
        this.height = 0;
    }
    
    void close() {
        if (this.allocator != null) {
            this.allocator.close();
        }
        this.allocator = null;
        this.height = 0;
        this.head = null;
    }
    
    void grow(final int n) {
        if (n > 0) {
            this.allocator.grow(n);
        }
    }
    
    int capacity() {
        if (this.allocator == null) {
            return 0;
        }
        return this.allocator.capacity() - 1;
    }
    
    int insert(DataValueDescriptor[] key) throws StandardException {
        if (this.head.rightLink == null) {
            if (this.sort.sortObserver != null && (key = this.sort.sortObserver.insertNonDuplicateKey(key)) == null) {
                return 1;
            }
            final Node node = this.allocator.newNode();
            node.key = key;
            node.aux = this.nextAux;
            this.head.rightLink = node;
            this.height = 1;
            return 0;
        }
        else {
            Node head = this.head;
            Node rightLink;
            Node node2 = rightLink = this.head.rightLink;
            Node node5;
            while (true) {
                final int compare = this.sort.compare(key, rightLink.key);
                if (compare == 0) {
                    if (this.sort.sortObserver != null && (key = this.sort.sortObserver.insertDuplicateKey(key, rightLink.key)) == null) {
                        return 1;
                    }
                    final Node node3 = this.allocator.newNode();
                    if (node3 == null) {
                        return 2;
                    }
                    node3.aux = this.nextAux;
                    node3.key = key;
                    node3.dupChain = rightLink.dupChain;
                    rightLink.dupChain = node3;
                    return 0;
                }
                else {
                    Node node4;
                    if (compare < 0) {
                        node4 = rightLink.leftLink;
                        if (node4 == null) {
                            node5 = this.allocator.newNode();
                            if (node5 == null) {
                                return 2;
                            }
                            node5.aux = this.nextAux;
                            rightLink.leftLink = node5;
                            break;
                        }
                    }
                    else {
                        node4 = rightLink.rightLink;
                        if (node4 == null) {
                            node5 = this.allocator.newNode();
                            if (node5 == null) {
                                return 2;
                            }
                            node5.aux = this.nextAux;
                            rightLink.rightLink = node5;
                            break;
                        }
                    }
                    if (node4.balance != 0) {
                        head = rightLink;
                        node2 = node4;
                    }
                    rightLink = node4;
                }
            }
            if (this.sort.sortObserver != null && (key = this.sort.sortObserver.insertNonDuplicateKey(key)) == null) {
                return 1;
            }
            node5.key = key;
            final int compare2 = this.sort.compare(key, node2.key);
            Node node7;
            Node node6;
            if (compare2 < 0) {
                node6 = (node7 = node2.leftLink);
            }
            else {
                node6 = (node7 = node2.rightLink);
            }
            while (node6 != node5) {
                if (this.sort.compare(key, node6.key) < 0) {
                    node6.balance = -1;
                    node6 = node6.leftLink;
                }
                else {
                    node6.balance = 1;
                    node6 = node6.rightLink;
                }
            }
            final int n = (compare2 > 0) ? 1 : ((compare2 == 0) ? 0 : -1);
            if (node2.balance == 0) {
                node2.balance = n;
                ++this.height;
                return 0;
            }
            if (node2.balance == -n) {
                return node2.balance = 0;
            }
            Node link;
            if (node7.balance == n) {
                link = node7;
                node2.setLink(n, node7.link(-n));
                node7.setLink(-n, node2);
                node2.balance = 0;
                node7.balance = 0;
            }
            else {
                link = node7.link(-n);
                node7.setLink(-n, link.link(n));
                link.setLink(n, node7);
                node2.setLink(n, link.link(-n));
                link.setLink(-n, node2);
                if (link.balance == n) {
                    node2.balance = -n;
                    node7.balance = 0;
                }
                else if (link.balance == 0) {
                    node2.balance = 0;
                    node7.balance = 0;
                }
                else {
                    node2.balance = 0;
                    node7.balance = n;
                }
                link.balance = 0;
            }
            if (node2 == head.rightLink) {
                head.rightLink = link;
            }
            else {
                head.leftLink = link;
            }
            return 0;
        }
    }
    
    DataValueDescriptor[] removeFirst() {
        if (this.head.rightLink == null) {
            return null;
        }
        this.head.rightLink = this.deleteLeftmost(this.head.rightLink);
        if (this.subtreeShrunk) {
            --this.height;
        }
        return this.deletedKey;
    }
    
    private Node deleteLeftmost(final Node node) {
        if (node.leftLink == null) {
            if (node.dupChain != null) {
                final Node dupChain = node.dupChain;
                this.deletedKey = dupChain.key;
                this.lastAux = dupChain.aux;
                node.dupChain = dupChain.dupChain;
                this.allocator.freeNode(dupChain);
                this.subtreeShrunk = false;
                return node;
            }
            this.deletedKey = node.key;
            this.lastAux = node.aux;
            this.subtreeShrunk = true;
            final Node rightLink = node.rightLink;
            this.allocator.freeNode(node);
            return rightLink;
        }
        else {
            node.leftLink = this.deleteLeftmost(node.leftLink);
            if (!this.subtreeShrunk) {
                return node;
            }
            if (node.balance == 1) {
                return this.rotateRight(node);
            }
            if (node.balance == -1) {
                node.balance = 0;
                this.subtreeShrunk = true;
            }
            else {
                node.balance = 1;
                this.subtreeShrunk = false;
            }
            return node;
        }
    }
    
    private Node rotateRight(final Node node) {
        final Node rightLink = node.rightLink;
        if (rightLink.balance >= 0) {
            node.rightLink = rightLink.leftLink;
            rightLink.leftLink = node;
            if (rightLink.balance == 0) {
                node.balance = 1;
                rightLink.balance = -1;
                this.subtreeShrunk = false;
            }
            else {
                node.balance = 0;
                rightLink.balance = 0;
                this.subtreeShrunk = true;
            }
            return rightLink;
        }
        final Node leftLink = rightLink.leftLink;
        node.rightLink = leftLink.leftLink;
        leftLink.leftLink = node;
        rightLink.leftLink = leftLink.rightLink;
        leftLink.rightLink = rightLink;
        if (leftLink.balance == 1) {
            node.balance = -1;
            rightLink.balance = 0;
        }
        else if (leftLink.balance == -1) {
            node.balance = 0;
            rightLink.balance = 1;
        }
        else {
            node.balance = 0;
            rightLink.balance = 0;
        }
        leftLink.balance = 0;
        this.subtreeShrunk = true;
        return leftLink;
    }
    
    public void check() {
    }
    
    private String checkNode(final Node node) {
        return null;
    }
    
    private int depth(final Node node) {
        int depth = 0;
        int depth2 = 0;
        if (node == null) {
            return 0;
        }
        if (node.leftLink != null) {
            depth = this.depth(node.leftLink);
        }
        if (node.rightLink != null) {
            depth2 = this.depth(node.rightLink);
        }
        if (depth2 > depth) {
            return depth2 + 1;
        }
        return depth + 1;
    }
    
    public void print() {
        final Node rightLink = this.head.rightLink;
        System.out.println("tree height: " + this.height + " root: " + ((rightLink == null) ? -1 : rightLink.id));
        if (rightLink != null) {
            this.printRecursive(rightLink, 0);
        }
    }
    
    private void printRecursive(final Node x, final int n) {
        if (x.rightLink != null) {
            this.printRecursive(x.rightLink, n + 1);
        }
        for (int i = 0; i < n; ++i) {
            System.out.print("       ");
        }
        System.out.println(x);
        if (x.leftLink != null) {
            this.printRecursive(x.leftLink, n + 1);
        }
    }
    
    private void debug(final String s) {
    }
}
