// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.compile.Visitable;
import org.apache.derby.iapi.sql.compile.Visitor;

public class ReplaceWindowFuncCallsWithCRVisitor implements Visitor
{
    private ResultColumnList rcl;
    private Class skipOverClass;
    private int tableNumber;
    
    public ReplaceWindowFuncCallsWithCRVisitor(final ResultColumnList rcl, final int tableNumber, final Class skipOverClass) {
        this.rcl = rcl;
        this.tableNumber = tableNumber;
        this.skipOverClass = skipOverClass;
    }
    
    public Visitable visit(Visitable replaceCallsWithColumnReferences) throws StandardException {
        if (replaceCallsWithColumnReferences instanceof WindowFunctionNode) {
            replaceCallsWithColumnReferences = ((WindowFunctionNode)replaceCallsWithColumnReferences).replaceCallsWithColumnReferences(this.rcl, this.tableNumber);
        }
        return replaceCallsWithColumnReferences;
    }
    
    public boolean skipChildren(final Visitable visitable) {
        return this.skipOverClass != null && this.skipOverClass.isInstance(visitable);
    }
    
    public boolean visitChildrenFirst(final Visitable visitable) {
        return false;
    }
    
    public boolean stopTraversal() {
        return false;
    }
}
