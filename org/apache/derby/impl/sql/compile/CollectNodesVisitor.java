// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.Visitable;
import java.util.ArrayList;
import java.util.List;
import org.apache.derby.iapi.sql.compile.Visitor;

public class CollectNodesVisitor implements Visitor
{
    private final List nodeList;
    private final Class nodeClass;
    private final Class skipOverClass;
    
    public CollectNodesVisitor(final Class clazz) {
        this(clazz, null);
    }
    
    public CollectNodesVisitor(final Class nodeClass, final Class skipOverClass) {
        this.nodeList = new ArrayList();
        this.nodeClass = nodeClass;
        this.skipOverClass = skipOverClass;
    }
    
    public boolean visitChildrenFirst(final Visitable visitable) {
        return false;
    }
    
    public boolean stopTraversal() {
        return false;
    }
    
    public Visitable visit(final Visitable visitable) {
        if (this.nodeClass.isInstance(visitable)) {
            this.nodeList.add(visitable);
        }
        return visitable;
    }
    
    public boolean skipChildren(final Visitable visitable) {
        return this.skipOverClass != null && this.skipOverClass.isInstance(visitable);
    }
    
    public List getList() {
        return this.nodeList;
    }
}
