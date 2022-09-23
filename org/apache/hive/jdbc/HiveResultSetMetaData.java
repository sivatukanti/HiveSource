// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.jdbc;

import org.apache.hive.service.cli.Type;
import java.sql.SQLException;
import java.util.List;
import java.sql.ResultSetMetaData;

public class HiveResultSetMetaData implements ResultSetMetaData
{
    private final List<String> columnNames;
    private final List<String> columnTypes;
    private final List<JdbcColumnAttributes> columnAttributes;
    
    public HiveResultSetMetaData(final List<String> columnNames, final List<String> columnTypes, final List<JdbcColumnAttributes> columnAttributes) {
        this.columnNames = columnNames;
        this.columnTypes = columnTypes;
        this.columnAttributes = columnAttributes;
    }
    
    @Override
    public String getCatalogName(final int column) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    private Type getHiveType(final int column) throws SQLException {
        return JdbcColumn.typeStringToHiveType(this.columnTypes.get(this.toZeroIndex(column)));
    }
    
    @Override
    public String getColumnClassName(final int column) throws SQLException {
        return JdbcColumn.columnClassName(this.getHiveType(column), this.columnAttributes.get(this.toZeroIndex(column)));
    }
    
    @Override
    public int getColumnCount() throws SQLException {
        return this.columnNames.size();
    }
    
    @Override
    public int getColumnDisplaySize(final int column) throws SQLException {
        return JdbcColumn.columnDisplaySize(this.getHiveType(column), this.columnAttributes.get(this.toZeroIndex(column)));
    }
    
    @Override
    public String getColumnLabel(final int column) throws SQLException {
        return this.columnNames.get(this.toZeroIndex(column));
    }
    
    @Override
    public String getColumnName(final int column) throws SQLException {
        return this.columnNames.get(this.toZeroIndex(column));
    }
    
    @Override
    public int getColumnType(final int column) throws SQLException {
        final String type = this.columnTypes.get(this.toZeroIndex(column));
        return JdbcColumn.hiveTypeToSqlType(type);
    }
    
    @Override
    public String getColumnTypeName(final int column) throws SQLException {
        return JdbcColumn.getColumnTypeName(this.columnTypes.get(this.toZeroIndex(column)));
    }
    
    @Override
    public int getPrecision(final int column) throws SQLException {
        return JdbcColumn.columnPrecision(this.getHiveType(column), this.columnAttributes.get(this.toZeroIndex(column)));
    }
    
    @Override
    public int getScale(final int column) throws SQLException {
        return JdbcColumn.columnScale(this.getHiveType(column), this.columnAttributes.get(this.toZeroIndex(column)));
    }
    
    @Override
    public String getSchemaName(final int column) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public String getTableName(final int column) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean isAutoIncrement(final int column) throws SQLException {
        return false;
    }
    
    @Override
    public boolean isCaseSensitive(final int column) throws SQLException {
        final String type = this.columnTypes.get(this.toZeroIndex(column));
        return "string".equalsIgnoreCase(type);
    }
    
    @Override
    public boolean isCurrency(final int column) throws SQLException {
        return false;
    }
    
    @Override
    public boolean isDefinitelyWritable(final int column) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public int isNullable(final int column) throws SQLException {
        return 1;
    }
    
    @Override
    public boolean isReadOnly(final int column) throws SQLException {
        return true;
    }
    
    @Override
    public boolean isSearchable(final int column) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean isSigned(final int column) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean isWritable(final int column) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    protected int toZeroIndex(final int column) throws SQLException {
        if (this.columnTypes == null) {
            throw new SQLException("Could not determine column type name for ResultSet");
        }
        if (column < 1 || column > this.columnTypes.size()) {
            throw new SQLException("Invalid column value: " + column);
        }
        return column - 1;
    }
}
