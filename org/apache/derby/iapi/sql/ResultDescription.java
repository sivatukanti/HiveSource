// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql;

import java.sql.ResultSetMetaData;

public interface ResultDescription
{
    String getStatementType();
    
    int getColumnCount();
    
    ResultColumnDescriptor[] getColumnInfo();
    
    ResultColumnDescriptor getColumnDescriptor(final int p0);
    
    ResultDescription truncateColumns(final int p0);
    
    void setMetaData(final ResultSetMetaData p0);
    
    ResultSetMetaData getMetaData();
    
    int findColumnInsenstive(final String p0);
}
