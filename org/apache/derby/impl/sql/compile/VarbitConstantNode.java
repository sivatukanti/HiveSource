// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.util.ReuseFactory;

public final class VarbitConstantNode extends BitConstantNode
{
    public void init(final Object o) throws StandardException {
        this.init(o, Boolean.TRUE, ReuseFactory.getInteger(0));
    }
}
