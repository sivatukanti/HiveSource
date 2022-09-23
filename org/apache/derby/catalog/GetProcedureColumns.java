// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.catalog;

import org.apache.derby.impl.jdbc.EmbedResultSetMetaData;
import org.apache.derby.iapi.types.DataTypeUtilities;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;
import org.apache.derby.iapi.sql.ResultColumnDescriptor;
import org.apache.derby.catalog.types.RoutineAliasInfo;
import org.apache.derby.vti.VTITemplate;

public class GetProcedureColumns extends VTITemplate
{
    private boolean isProcedure;
    private boolean isFunction;
    private int rowCount;
    private int returnedTableColumnCount;
    private TypeDescriptor tableFunctionReturnType;
    private RoutineAliasInfo procedure;
    private int paramCursor;
    private short method_count;
    private short param_number;
    private TypeDescriptor sqlType;
    private String columnName;
    private short columnType;
    private final short nullable;
    private static final ResultColumnDescriptor[] columnInfo;
    private static final ResultSetMetaData metadata;
    
    private int translate(final int n) {
        if (!this.isFunction) {
            return n;
        }
        switch (n) {
            case 0: {
                return 0;
            }
            case 1: {
                return 1;
            }
            case 2: {
                return 2;
            }
            case 4: {
                return 3;
            }
            case 5: {
                return 4;
            }
            default: {
                return 0;
            }
        }
    }
    
    public ResultSetMetaData getMetaData() {
        return GetProcedureColumns.metadata;
    }
    
    public GetProcedureColumns(final AliasInfo aliasInfo, final String s) throws SQLException {
        int paramCursor = -2;
        if (aliasInfo != null) {
            this.isProcedure = s.equals("P");
            this.isFunction = s.equals("F");
            this.procedure = (RoutineAliasInfo)aliasInfo;
            this.method_count = (short)this.procedure.getParameterCount();
            this.rowCount = this.procedure.getParameterCount();
            if (this.procedure.isTableFunction()) {
                this.tableFunctionReturnType = this.procedure.getReturnType();
                this.returnedTableColumnCount = this.tableFunctionReturnType.getRowColumnNames().length;
                this.rowCount += this.returnedTableColumnCount;
                paramCursor = -1;
            }
        }
        if (s == null) {
            this.nullable = 0;
            return;
        }
        if (this.isFunction) {
            this.nullable = 1;
            this.sqlType = this.procedure.getReturnType();
            this.columnName = "";
            this.columnType = 4;
            this.paramCursor = paramCursor;
            return;
        }
        this.nullable = 1;
        this.paramCursor = -1;
    }
    
    public boolean next() throws SQLException {
        if (++this.paramCursor >= this.rowCount) {
            return false;
        }
        if (this.procedure.isTableFunction() && this.paramCursor >= this.procedure.getParameterCount()) {
            final int n = this.paramCursor - this.procedure.getParameterCount();
            this.sqlType = this.tableFunctionReturnType.getRowTypes()[n];
            this.columnName = this.tableFunctionReturnType.getRowColumnNames()[n];
            this.columnType = 5;
        }
        else if (this.paramCursor > -1) {
            this.sqlType = this.procedure.getParameterTypes()[this.paramCursor];
            this.columnName = this.procedure.getParameterNames()[this.paramCursor];
            this.columnType = (short)this.translate(this.procedure.getParameterModes()[this.paramCursor]);
        }
        this.param_number = (short)this.paramCursor;
        return true;
    }
    
    public String getString(final int n) throws SQLException {
        switch (n) {
            case 1: {
                return this.columnName;
            }
            case 4: {
                return this.sqlType.getTypeName();
            }
            case 10: {
                return null;
            }
            default: {
                return super.getString(n);
            }
        }
    }
    
    public int getInt(final int n) throws SQLException {
        switch (n) {
            case 3: {
                if (this.sqlType != null) {
                    return this.sqlType.getJDBCTypeId();
                }
                return 2000;
            }
            case 5: {
                if (this.sqlType == null) {
                    return 0;
                }
                final int jdbcTypeId = this.sqlType.getJDBCTypeId();
                if (DataTypeDescriptor.isNumericType(jdbcTypeId)) {
                    return this.sqlType.getPrecision();
                }
                if (jdbcTypeId == 91 || jdbcTypeId == 92 || jdbcTypeId == 93) {
                    return DataTypeUtilities.getColumnDisplaySize(jdbcTypeId, -1);
                }
                return this.sqlType.getMaximumWidth();
            }
            case 6: {
                if (this.sqlType != null) {
                    return this.sqlType.getMaximumWidthInBytes();
                }
                return 0;
            }
            default: {
                return super.getInt(n);
            }
        }
    }
    
    public short getShort(final int n) throws SQLException {
        switch (n) {
            case 2: {
                return this.columnType;
            }
            case 7: {
                if (this.sqlType != null) {
                    return (short)this.sqlType.getScale();
                }
                return 0;
            }
            case 8: {
                if (this.sqlType == null) {
                    return 0;
                }
                final int jdbcTypeId = this.sqlType.getJDBCTypeId();
                if (jdbcTypeId == 7 || jdbcTypeId == 6 || jdbcTypeId == 8) {
                    return 2;
                }
                return 10;
            }
            case 9: {
                return this.nullable;
            }
            case 11: {
                return this.method_count;
            }
            case 12: {
                return this.param_number;
            }
            default: {
                return super.getShort(n);
            }
        }
    }
    
    public void close() {
    }
    
    static {
        columnInfo = new ResultColumnDescriptor[] { EmbedResultSetMetaData.getResultColumnDescriptor("COLUMN_NAME", 12, false, 128), EmbedResultSetMetaData.getResultColumnDescriptor("COLUMN_TYPE", 5, false), EmbedResultSetMetaData.getResultColumnDescriptor("DATA_TYPE", 4, false), EmbedResultSetMetaData.getResultColumnDescriptor("TYPE_NAME", 12, false, 22), EmbedResultSetMetaData.getResultColumnDescriptor("PRECISION", 4, false), EmbedResultSetMetaData.getResultColumnDescriptor("LENGTH", 4, false), EmbedResultSetMetaData.getResultColumnDescriptor("SCALE", 5, false), EmbedResultSetMetaData.getResultColumnDescriptor("RADIX", 5, false), EmbedResultSetMetaData.getResultColumnDescriptor("NULLABLE", 5, false), EmbedResultSetMetaData.getResultColumnDescriptor("REMARKS", 12, true, 22), EmbedResultSetMetaData.getResultColumnDescriptor("METHOD_ID", 5, false), EmbedResultSetMetaData.getResultColumnDescriptor("PARAMETER_ID", 5, false) };
        metadata = new EmbedResultSetMetaData(GetProcedureColumns.columnInfo);
    }
}
