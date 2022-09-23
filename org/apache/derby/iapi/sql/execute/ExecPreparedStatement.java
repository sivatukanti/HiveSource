// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.execute;

import java.util.List;
import org.apache.derby.iapi.sql.ResultColumnDescriptor;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.loader.GeneratedClass;
import org.apache.derby.iapi.sql.PreparedStatement;

public interface ExecPreparedStatement extends PreparedStatement
{
    void setSource(final String p0);
    
    ConstantAction getConstantAction();
    
    Object getSavedObject(final int p0);
    
    Object[] getSavedObjects();
    
    Object getCursorInfo();
    
    GeneratedClass getActivationClass() throws StandardException;
    
    boolean upToDate(final GeneratedClass p0) throws StandardException;
    
    void finish(final LanguageConnectionContext p0);
    
    boolean needsSavepoint();
    
    ExecPreparedStatement getClone() throws StandardException;
    
    int getUpdateMode();
    
    ExecCursorTableReference getTargetTable();
    
    ResultColumnDescriptor[] getTargetColumns();
    
    String[] getUpdateColumns();
    
    void setValid();
    
    void setSPSAction();
    
    List getRequiredPermissionsList();
    
    int incrementExecutionCount();
    
    long getInitialRowCount(final int p0, final long p1);
    
    void setStalePlanCheckInterval(final int p0);
    
    int getStalePlanCheckInterval();
}
