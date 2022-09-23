// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.execute.xplain;

import org.apache.derby.iapi.sql.execute.ResultSetStatistics;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.execute.RunTimeStatistics;

public interface XPLAINVisitor
{
    void reset();
    
    void doXPLAIN(final RunTimeStatistics p0, final Activation p1) throws StandardException;
    
    void visit(final ResultSetStatistics p0);
    
    void setNumberOfChildren(final int p0);
}
