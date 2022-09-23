// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import org.apache.derby.impl.sql.GenericColumnDescriptor;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.types.DataTypeUtilities;
import java.sql.SQLException;
import org.apache.derby.iapi.sql.ResultColumnDescriptor;
import java.sql.ResultSetMetaData;

public class EmbedResultSetMetaData implements ResultSetMetaData
{
    private final ResultColumnDescriptor[] columnInfo;
    
    public EmbedResultSetMetaData(final ResultColumnDescriptor[] columnInfo) {
        this.columnInfo = columnInfo;
    }
    
    public final int getColumnCount() {
        return this.columnInfo.length;
    }
    
    public final boolean isAutoIncrement(final int n) throws SQLException {
        this.validColumnNumber(n);
        return this.columnInfo[n - 1].isAutoincrement();
    }
    
    public final boolean isCaseSensitive(final int n) throws SQLException {
        return DataTypeUtilities.isCaseSensitive(this.getColumnTypeDescriptor(n));
    }
    
    public final boolean isSearchable(final int n) throws SQLException {
        this.validColumnNumber(n);
        return true;
    }
    
    public final boolean isCurrency(final int n) throws SQLException {
        return DataTypeUtilities.isCurrency(this.getColumnTypeDescriptor(n));
    }
    
    public final int isNullable(final int n) throws SQLException {
        return DataTypeUtilities.isNullable(this.getColumnTypeDescriptor(n));
    }
    
    public final boolean isSigned(final int n) throws SQLException {
        return DataTypeUtilities.isSigned(this.getColumnTypeDescriptor(n));
    }
    
    public final int getColumnDisplaySize(final int n) throws SQLException {
        return DataTypeUtilities.getColumnDisplaySize(this.getColumnTypeDescriptor(n));
    }
    
    public final String getColumnLabel(final int i) throws SQLException {
        final String name = this.columnInfo[i - 1].getName();
        return (name == null) ? ("Column" + Integer.toString(i)) : name;
    }
    
    public final String getColumnName(final int n) throws SQLException {
        final String name = this.columnInfo[n - 1].getName();
        return (name == null) ? "" : name;
    }
    
    public final String getSchemaName(final int n) throws SQLException {
        final String sourceSchemaName = this.columnInfo[n - 1].getSourceSchemaName();
        return (sourceSchemaName == null) ? "" : sourceSchemaName;
    }
    
    public final int getPrecision(final int n) throws SQLException {
        return DataTypeUtilities.getDigitPrecision(this.getColumnTypeDescriptor(n));
    }
    
    public final int getScale(final int n) throws SQLException {
        return this.getColumnTypeDescriptor(n).getScale();
    }
    
    public final String getTableName(final int n) throws SQLException {
        final String sourceTableName = this.columnInfo[n - 1].getSourceTableName();
        return (sourceTableName == null) ? "" : sourceTableName;
    }
    
    public final String getCatalogName(final int n) throws SQLException {
        this.validColumnNumber(n);
        return "";
    }
    
    public final int getColumnType(final int n) throws SQLException {
        return this.getColumnTypeDescriptor(n).getTypeId().getJDBCTypeId();
    }
    
    public final String getColumnTypeName(final int n) throws SQLException {
        return this.getColumnTypeDescriptor(n).getTypeId().getSQLTypeName();
    }
    
    public final boolean isReadOnly(final int n) throws SQLException {
        this.validColumnNumber(n);
        return false;
    }
    
    public final boolean isWritable(final int n) throws SQLException {
        this.validColumnNumber(n);
        return this.columnInfo[n - 1].updatableByCursor();
    }
    
    public final boolean isDefinitelyWritable(final int n) throws SQLException {
        this.validColumnNumber(n);
        return false;
    }
    
    private void validColumnNumber(final int value) throws SQLException {
        if (value < 1 || value > this.getColumnCount()) {
            throw Util.generateCsSQLException("S0022", new Integer(value));
        }
    }
    
    private DataTypeDescriptor getColumnTypeDescriptor(final int n) throws SQLException {
        this.validColumnNumber(n);
        return this.columnInfo[n - 1].getType();
    }
    
    public final String getColumnClassName(final int n) throws SQLException {
        return this.getColumnTypeDescriptor(n).getTypeId().getResultSetMetaDataTypeName();
    }
    
    public static ResultColumnDescriptor getResultColumnDescriptor(final String s, final int n, final boolean b) {
        return new GenericColumnDescriptor(s, DataTypeDescriptor.getBuiltInDataTypeDescriptor(n, b));
    }
    
    public static ResultColumnDescriptor getResultColumnDescriptor(final String s, final int n, final boolean b, final int n2) {
        return new GenericColumnDescriptor(s, DataTypeDescriptor.getBuiltInDataTypeDescriptor(n, b, n2));
    }
    
    public static ResultColumnDescriptor getResultColumnDescriptor(final String s, final DataTypeDescriptor dataTypeDescriptor) {
        return new GenericColumnDescriptor(s, dataTypeDescriptor);
    }
}
