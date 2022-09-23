// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.compile;

import org.apache.derby.iapi.error.StandardException;

public interface Parser
{
    Visitable parseStatement(final String p0, final Object[] p1) throws StandardException;
    
    Visitable parseStatement(final String p0) throws StandardException;
    
    String getSQLtext();
}
