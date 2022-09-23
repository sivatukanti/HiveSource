// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql;

import java.sql.SQLWarning;
import java.sql.Timestamp;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.depend.Dependent;

public interface PreparedStatement extends Dependent
{
    boolean upToDate() throws StandardException;
    
    void rePrepare(final LanguageConnectionContext p0) throws StandardException;
    
    Activation getActivation(final LanguageConnectionContext p0, final boolean p1) throws StandardException;
    
    ResultSet execute(final Activation p0, final boolean p1, final long p2) throws StandardException;
    
    ResultSet executeSubStatement(final Activation p0, final Activation p1, final boolean p2, final long p3) throws StandardException;
    
    ResultSet executeSubStatement(final LanguageConnectionContext p0, final boolean p1, final long p2) throws StandardException;
    
    ResultDescription getResultDescription();
    
    boolean referencesSessionSchema();
    
    DataTypeDescriptor[] getParameterTypes();
    
    String getSource();
    
    String getSPSName();
    
    long getCompileTimeInMillis();
    
    long getParseTimeInMillis();
    
    long getBindTimeInMillis();
    
    long getOptimizeTimeInMillis();
    
    long getGenerateTimeInMillis();
    
    Timestamp getBeginCompileTimestamp();
    
    Timestamp getEndCompileTimestamp();
    
    boolean isAtomic();
    
    SQLWarning getCompileTimeWarnings();
    
    long getVersionCounter();
}
