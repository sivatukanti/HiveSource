// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.vti;

import java.sql.SQLException;
import java.sql.ResultSetMetaData;

public abstract class VTIMetaDataTemplate implements ResultSetMetaData
{
    public boolean isAutoIncrement(final int n) throws SQLException {
        throw new SQLException("isAutoIncrement");
    }
    
    public boolean isCaseSensitive(final int n) throws SQLException {
        throw new SQLException("isCaseSensitive");
    }
    
    public boolean isSearchable(final int n) throws SQLException {
        throw new SQLException("isSearchable");
    }
    
    public boolean isCurrency(final int n) throws SQLException {
        throw new SQLException("isCurrency");
    }
    
    public int isNullable(final int n) throws SQLException {
        throw new SQLException("isNullable");
    }
    
    public boolean isSigned(final int n) throws SQLException {
        throw new SQLException("isSigned");
    }
    
    public int getColumnDisplaySize(final int n) throws SQLException {
        throw new SQLException("getColumnDisplaySize");
    }
    
    public String getColumnLabel(final int n) throws SQLException {
        throw new SQLException("getColumnLabel");
    }
    
    public String getColumnName(final int n) throws SQLException {
        throw new SQLException("getColumnName");
    }
    
    public String getSchemaName(final int n) throws SQLException {
        throw new SQLException("getSchemaName");
    }
    
    public int getPrecision(final int n) throws SQLException {
        throw new SQLException("getPrecision");
    }
    
    public int getScale(final int n) throws SQLException {
        throw new SQLException("getScale");
    }
    
    public String getTableName(final int n) throws SQLException {
        throw new SQLException("getTableName");
    }
    
    public String getCatalogName(final int n) throws SQLException {
        throw new SQLException("getCatalogName");
    }
    
    public String getColumnTypeName(final int n) throws SQLException {
        throw new SQLException("getColumnTypeName");
    }
    
    public boolean isReadOnly(final int n) throws SQLException {
        return true;
    }
    
    public boolean isWritable(final int n) throws SQLException {
        return false;
    }
    
    public boolean isDefinitelyWritable(final int n) throws SQLException {
        return false;
    }
    
    public String getColumnClassName(final int n) throws SQLException {
        throw new SQLException("getColumnClassName");
    }
}
