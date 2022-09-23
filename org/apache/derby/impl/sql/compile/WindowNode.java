// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.error.StandardException;

public abstract class WindowNode extends QueryTreeNode
{
    private String windowName;
    
    public void init(final Object o) throws StandardException {
        this.windowName = (String)o;
    }
    
    public String getName() {
        return this.windowName;
    }
}
