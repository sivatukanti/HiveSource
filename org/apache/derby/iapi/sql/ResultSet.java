// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql;

import java.sql.SQLWarning;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import java.sql.Timestamp;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.error.StandardException;

public interface ResultSet
{
    public static final int CURRENT_RESULTSET_ONLY = 0;
    public static final int ENTIRE_RESULTSET_TREE = 1;
    public static final int ISBEFOREFIRST = 101;
    public static final int ISFIRST = 102;
    public static final int ISLAST = 103;
    public static final int ISAFTERLAST = 104;
    
    boolean returnsRows();
    
    long modifiedRowCount();
    
    ResultDescription getResultDescription();
    
    Activation getActivation();
    
    void open() throws StandardException;
    
    ExecRow getAbsoluteRow(final int p0) throws StandardException;
    
    ExecRow getRelativeRow(final int p0) throws StandardException;
    
    ExecRow setBeforeFirstRow() throws StandardException;
    
    ExecRow getFirstRow() throws StandardException;
    
    ExecRow getNextRow() throws StandardException;
    
    ExecRow getPreviousRow() throws StandardException;
    
    ExecRow getLastRow() throws StandardException;
    
    ExecRow setAfterLastRow() throws StandardException;
    
    void clearCurrentRow();
    
    boolean checkRowPosition(final int p0) throws StandardException;
    
    int getRowNumber();
    
    void close() throws StandardException;
    
    void cleanUp() throws StandardException;
    
    boolean isClosed();
    
    void finish() throws StandardException;
    
    long getExecuteTime();
    
    Timestamp getBeginExecutionTimestamp();
    
    Timestamp getEndExecutionTimestamp();
    
    long getTimeSpent(final int p0);
    
    NoPutResultSet[] getSubqueryTrackingArray(final int p0);
    
    ResultSet getAutoGeneratedKeysResultset();
    
    String getCursorName();
    
    void addWarning(final SQLWarning p0);
    
    SQLWarning getWarnings();
}
