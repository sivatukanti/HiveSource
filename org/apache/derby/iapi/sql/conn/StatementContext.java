// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.conn;

import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.depend.Dependency;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import org.apache.derby.iapi.sql.ResultSet;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.ParameterValueSet;
import org.apache.derby.iapi.services.context.Context;

public interface StatementContext extends Context
{
    void setInUse(final boolean p0, final boolean p1, final boolean p2, final String p3, final ParameterValueSet p4, final long p5);
    
    void clearInUse();
    
    void setSavePoint() throws StandardException;
    
    void resetSavePoint() throws StandardException;
    
    void clearSavePoint() throws StandardException;
    
    void setTopResultSet(final ResultSet p0, final NoPutResultSet[] p1) throws StandardException;
    
    void setSubqueryResultSet(final int p0, final NoPutResultSet p1, final int p2) throws StandardException;
    
    NoPutResultSet[] getSubqueryTrackingArray() throws StandardException;
    
    void addDependency(final Dependency p0) throws StandardException;
    
    boolean onStack();
    
    boolean inTrigger();
    
    boolean isAtomic();
    
    boolean inUse();
    
    boolean isForReadOnly();
    
    boolean isCancelled();
    
    void cancel();
    
    String getStatementText();
    
    void setSQLAllowed(final short p0, final boolean p1);
    
    short getSQLAllowed();
    
    void setSystemCode();
    
    boolean getSystemCode();
    
    void setParentRollback();
    
    void setActivation(final Activation p0);
    
    Activation getActivation();
    
    SQLSessionContext getSQLSessionContext();
    
    void setSQLSessionContext(final SQLSessionContext p0);
    
    boolean getStatementWasInvalidated();
}
