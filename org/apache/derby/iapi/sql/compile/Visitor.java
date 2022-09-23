// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.compile;

import org.apache.derby.iapi.error.StandardException;

public interface Visitor
{
    Visitable visit(final Visitable p0) throws StandardException;
    
    boolean visitChildrenFirst(final Visitable p0);
    
    boolean stopTraversal();
    
    boolean skipChildren(final Visitable p0) throws StandardException;
}
