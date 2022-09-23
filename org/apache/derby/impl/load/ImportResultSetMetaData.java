// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.load;

import java.sql.SQLException;
import java.util.HashMap;
import org.apache.derby.vti.VTIMetaDataTemplate;

class ImportResultSetMetaData extends VTIMetaDataTemplate
{
    private final int numberOfColumns;
    private final String[] columnNames;
    private final int[] columnWidths;
    private final int[] tableColumnTypes;
    private final String[] columnTypeNames;
    private final HashMap udtClasses;
    
    public ImportResultSetMetaData(final int numberOfColumns, final String[] columnNames, final int[] columnWidths, final int[] tableColumnTypes, final String[] columnTypeNames, final HashMap udtClasses) {
        this.numberOfColumns = numberOfColumns;
        this.columnNames = columnNames;
        this.columnWidths = columnWidths;
        this.tableColumnTypes = tableColumnTypes;
        this.columnTypeNames = columnTypeNames;
        this.udtClasses = udtClasses;
    }
    
    public int getColumnCount() {
        return this.numberOfColumns;
    }
    
    public String getColumnName(final int n) {
        return this.columnNames[n - 1];
    }
    
    public int getColumnType(final int n) {
        int n2 = 0;
        switch (this.tableColumnTypes[n - 1]) {
            case 2004: {
                n2 = 2004;
                break;
            }
            case 2005: {
                n2 = 2005;
                break;
            }
            case -4: {
                n2 = -4;
                break;
            }
            case -3: {
                n2 = -3;
                break;
            }
            case -2: {
                n2 = -2;
                break;
            }
            case 2000: {
                n2 = 2000;
                break;
            }
            default: {
                n2 = 12;
                break;
            }
        }
        return n2;
    }
    
    public int isNullable(final int n) {
        return 2;
    }
    
    public int getColumnDisplaySize(final int n) {
        if (this.columnWidths == null) {
            return 32672;
        }
        return this.columnWidths[n - 1];
    }
    
    public String getColumnTypeName(final int n) throws SQLException {
        return this.columnTypeNames[n - 1];
    }
    
    public Class getUDTClass(final int n) throws SQLException {
        this.getColumnName(n);
        return this.udtClasses.get(this.getColumnName(n));
    }
}
