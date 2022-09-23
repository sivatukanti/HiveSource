// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.load;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.apache.derby.iapi.util.IdUtil;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Connection;

class ExportResultSetForObject
{
    private Connection con;
    private String selectQuery;
    private ResultSet rs;
    private int columnCount;
    private String[] columnNames;
    private String[] columnTypes;
    private int[] columnLengths;
    private Statement expStmt;
    private String schemaName;
    private String tableName;
    
    public ExportResultSetForObject(final Connection con, final String schemaName, final String tableName, final String selectQuery) {
        this.expStmt = null;
        this.con = con;
        if (selectQuery == null) {
            this.schemaName = schemaName;
            this.tableName = tableName;
            this.selectQuery = "select * from " + IdUtil.mkQualifiedName(schemaName, tableName);
        }
        else {
            this.selectQuery = selectQuery;
        }
    }
    
    public ResultSet getResultSet() throws SQLException {
        this.rs = null;
        this.expStmt = this.con.createStatement();
        this.rs = this.expStmt.executeQuery(this.selectQuery);
        this.getMetaDataInfo();
        return this.rs;
    }
    
    public int getColumnCount() {
        return this.columnCount;
    }
    
    public String[] getColumnDefinition() {
        return this.columnNames;
    }
    
    public String[] getColumnTypes() {
        return this.columnTypes;
    }
    
    public int[] getColumnLengths() {
        return this.columnLengths;
    }
    
    private void getMetaDataInfo() throws SQLException {
        final ResultSetMetaData metaData = this.rs.getMetaData();
        this.columnCount = metaData.getColumnCount();
        final int columnCount = this.columnCount;
        this.columnNames = new String[columnCount];
        this.columnTypes = new String[columnCount];
        this.columnLengths = new int[columnCount];
        for (int i = 0; i < columnCount; ++i) {
            final int columnType = metaData.getColumnType(i + 1);
            this.columnNames[i] = metaData.getColumnName(i + 1);
            this.columnTypes[i] = metaData.getColumnTypeName(i + 1);
            if (!ColumnInfo.importExportSupportedType(columnType)) {
                throw LoadError.nonSupportedTypeColumn(this.columnNames[i], this.columnTypes[i]);
            }
            this.columnLengths[i] = metaData.getColumnDisplaySize(i + 1);
        }
    }
    
    public void close() throws Exception {
        if (this.expStmt != null) {
            this.expStmt.close();
        }
    }
}
