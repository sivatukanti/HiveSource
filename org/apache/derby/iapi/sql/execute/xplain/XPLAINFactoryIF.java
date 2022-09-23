// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.execute.xplain;

import org.apache.derby.iapi.error.StandardException;

public interface XPLAINFactoryIF
{
    public static final String MODULE = "org.apache.derby.iapi.sql.execute.xplain.XPLAINFactoryIF";
    
    XPLAINVisitor getXPLAINVisitor() throws StandardException;
    
    void freeResources();
}
