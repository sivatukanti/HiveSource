// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.compile.Visitor;
import java.util.Collection;
import java.util.ArrayList;

abstract class QueryTreeNodeVector extends QueryTreeNode
{
    private final ArrayList v;
    
    QueryTreeNodeVector() {
        this.v = new ArrayList();
    }
    
    public final int size() {
        return this.v.size();
    }
    
    final QueryTreeNode elementAt(final int index) {
        return this.v.get(index);
    }
    
    final void addElement(final QueryTreeNode e) {
        this.v.add(e);
    }
    
    final void removeElementAt(final int index) {
        this.v.remove(index);
    }
    
    final void removeElement(final QueryTreeNode o) {
        this.v.remove(o);
    }
    
    final Object remove(final int index) {
        return this.v.remove(index);
    }
    
    final int indexOf(final QueryTreeNode o) {
        return this.v.indexOf(o);
    }
    
    final void setElementAt(final QueryTreeNode element, final int index) {
        this.v.set(index, element);
    }
    
    void destructiveAppend(final QueryTreeNodeVector queryTreeNodeVector) {
        this.nondestructiveAppend(queryTreeNodeVector);
        queryTreeNodeVector.removeAllElements();
    }
    
    void nondestructiveAppend(final QueryTreeNodeVector queryTreeNodeVector) {
        this.v.addAll(queryTreeNodeVector.v);
    }
    
    final void removeAllElements() {
        this.v.clear();
    }
    
    final void insertElementAt(final QueryTreeNode element, final int index) {
        this.v.add(index, element);
    }
    
    public void printSubNodes(final int n) {
    }
    
    void acceptChildren(final Visitor visitor) throws StandardException {
        super.acceptChildren(visitor);
        for (int size = this.size(), i = 0; i < size; ++i) {
            this.setElementAt((QueryTreeNode)this.elementAt(i).accept(visitor), i);
        }
    }
}
