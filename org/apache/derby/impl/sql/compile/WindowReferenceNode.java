// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.error.StandardException;

public final class WindowReferenceNode extends WindowNode
{
    public void init(final Object o) throws StandardException {
        super.init(o);
    }
    
    public String toString() {
        return "referenced window: " + this.getName() + "\n" + super.toString();
    }
}
