// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.load;

import java.util.Iterator;
import java.util.Map;
import org.apache.derby.iapi.util.IdUtil;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.StringTokenizer;
import org.apache.derby.iapi.jdbc.EngineConnection;
import java.util.HashMap;
import java.sql.Connection;
import java.util.ArrayList;

class ColumnInfo
{
    private ArrayList vtiColumnNames;
    private ArrayList insertColumnNames;
    private ArrayList columnTypes;
    private ArrayList jdbcColumnTypes;
    private int noOfColumns;
    private ArrayList columnPositions;
    private boolean createolumnNames;
    private int expectedNumberOfCols;
    private Connection conn;
    private String tableName;
    private String schemaName;
    private HashMap udtClassNames;
    
    public ColumnInfo(final Connection conn, String currentSchemaName, final String tableName, final String str, final String str2, final String s) throws SQLException {
        this.createolumnNames = true;
        this.vtiColumnNames = new ArrayList(1);
        this.insertColumnNames = new ArrayList(1);
        this.columnTypes = new ArrayList(1);
        this.jdbcColumnTypes = new ArrayList(1);
        this.udtClassNames = new HashMap();
        this.noOfColumns = 0;
        this.conn = conn;
        if (currentSchemaName == null) {
            currentSchemaName = ((EngineConnection)conn).getCurrentSchemaName();
        }
        this.schemaName = currentSchemaName;
        this.tableName = tableName;
        if (str != null) {
            final StringTokenizer stringTokenizer = new StringTokenizer(str, ",");
            while (stringTokenizer.hasMoreTokens()) {
                final String trim = stringTokenizer.nextToken().trim();
                if (!this.initializeColumnInfo(trim)) {
                    if (this.tableExists()) {
                        throw LoadError.invalidColumnName(trim);
                    }
                    throw LoadError.tableNotFound((this.schemaName != null) ? (this.schemaName + "." + this.tableName) : this.tableName);
                }
            }
        }
        else if (!this.initializeColumnInfo(null)) {
            throw LoadError.tableNotFound((this.schemaName != null) ? (this.schemaName + "." + this.tableName) : this.tableName);
        }
        if (str2 != null) {
            final StringTokenizer stringTokenizer2 = new StringTokenizer(str2, ",");
            while (stringTokenizer2.hasMoreTokens()) {
                final String trim2 = stringTokenizer2.nextToken().trim();
                this.vtiColumnNames.add(s + trim2);
                final int intValue = new Integer(trim2);
                if (intValue > this.expectedNumberOfCols) {
                    this.expectedNumberOfCols = intValue;
                }
            }
        }
        if (this.vtiColumnNames.size() < 1) {
            for (int i = 1; i <= this.noOfColumns; ++i) {
                this.vtiColumnNames.add(s + i);
            }
            this.expectedNumberOfCols = this.noOfColumns;
        }
    }
    
    private boolean initializeColumnInfo(final String s) throws SQLException {
        final DatabaseMetaData metaData = this.conn.getMetaData();
        final ResultSet columns = metaData.getColumns(null, this.schemaName, this.tableName, s);
        boolean b = false;
        while (columns.next()) {
            final String string = columns.getString(4);
            final short short1 = columns.getShort(5);
            final String string2 = columns.getString(6);
            final int int1 = columns.getInt(7);
            final int int2 = columns.getInt(9);
            columns.getInt(10);
            b = true;
            if (!importExportSupportedType(short1)) {
                columns.close();
                throw LoadError.nonSupportedTypeColumn(string, string2);
            }
            this.insertColumnNames.add(string);
            this.columnTypes.add(string2 + this.getTypeOption(string2, int1, int1, int2));
            this.jdbcColumnTypes.add(new Integer(short1));
            ++this.noOfColumns;
            if (short1 != 2000) {
                continue;
            }
            this.udtClassNames.put("COLUMN" + this.noOfColumns, this.getUDTClassName(metaData, string2));
        }
        columns.close();
        return b;
    }
    
    private String getUDTClassName(final DatabaseMetaData databaseMetaData, final String s) throws SQLException {
        String string = null;
        try {
            if (s.charAt(0) != '\"') {
                return s;
            }
            final String[] multiPartSQLIdentifier = IdUtil.parseMultiPartSQLIdentifier(s);
            final ResultSet udTs = databaseMetaData.getUDTs(null, multiPartSQLIdentifier[0], multiPartSQLIdentifier[1], new int[] { 2000 });
            if (udTs.next()) {
                string = udTs.getString(4);
            }
            udTs.close();
        }
        catch (Exception ex) {
            throw LoadError.unexpectedError(ex);
        }
        if (string == null) {
            string = "???";
        }
        return string;
    }
    
    public static final boolean importExportSupportedType(final int n) {
        return n != -7 && n != 1111 && n != 2009;
    }
    
    private String getTypeOption(final String s, final int i, final int j, final int k) {
        if ((s.equals("CHAR") || s.equals("BLOB") || s.equals("CLOB") || s.equals("VARCHAR")) && i != 0) {
            return "(" + i + ")";
        }
        if (s.equals("FLOAT") && j != 0) {
            return "(" + j + ")";
        }
        if (s.equals("DECIMAL") || s.equals("NUMERIC")) {
            if (j != 0 && k == 0) {
                return "(" + j + ")";
            }
            if (j != 0 && k != 0) {
                return "(" + j + "," + k + ")";
            }
            if (j == 0 && k != 0) {
                return "(" + k + ")";
            }
        }
        if ((s.equals("DECIMAL") || s.equals("NUMERIC")) && k != 0) {
            return "(" + k + ")";
        }
        return "";
    }
    
    public String getColumnTypeNames() throws Exception {
        return ExportAbstract.stringifyObject(this.columnTypes);
    }
    
    public String getUDTClassNames() throws Exception {
        return ExportAbstract.stringifyObject(this.udtClassNames);
    }
    
    public String getColumnNamesWithCasts() {
        final StringBuffer sb = new StringBuffer();
        int n = 1;
        for (int size = this.vtiColumnNames.size(), n2 = 0; n2 < this.noOfColumns && n2 < size; ++n2) {
            if (n == 0) {
                sb.append(", ");
            }
            else {
                n = 0;
            }
            final String str = this.columnTypes.get(n2);
            final String s = this.vtiColumnNames.get(n2);
            if (str.startsWith("SMALLINT") || str.startsWith("INTEGER") || str.startsWith("DECIMAL") || str.startsWith("BIGINT") || str.startsWith("NUMERIC")) {
                sb.append(" cast(" + s + " AS " + str + ") ");
            }
            else if (str.startsWith("DOUBLE")) {
                sb.append(" DOUBLE(" + s + ") ");
            }
            else if (str.startsWith("REAL")) {
                sb.append("cast( DOUBLE(" + s + ") " + " AS " + "REAL" + ") ");
            }
            else {
                sb.append(" " + s + " ");
            }
        }
        if (n != 0) {
            return " * ";
        }
        return sb.toString();
    }
    
    public String getInsertColumnNames() {
        final StringBuffer sb = new StringBuffer();
        int n = 1;
        for (int i = 0; i < this.noOfColumns; ++i) {
            if (n == 0) {
                sb.append(", ");
            }
            else {
                n = 0;
            }
            sb.append(IdUtil.normalToDelimited((String)this.insertColumnNames.get(i)));
        }
        if (n != 0) {
            return null;
        }
        return sb.toString();
    }
    
    public int getExpectedNumberOfColumnsInFile() {
        return this.expectedNumberOfCols;
    }
    
    private boolean tableExists() throws SQLException {
        final ResultSet tables = this.conn.getMetaData().getTables(null, this.schemaName, this.tableName, null);
        boolean b = false;
        if (tables.next()) {
            b = true;
        }
        tables.close();
        return b;
    }
    
    public String getExpectedVtiColumnTypesAsString() {
        final StringBuffer sb = new StringBuffer();
        int n = 1;
        for (int n2 = 0; n2 < this.noOfColumns && n2 < this.vtiColumnNames.size(); ++n2) {
            if (n != 0) {
                n = 0;
            }
            else {
                sb.append(",");
            }
            sb.append(this.vtiColumnNames.get(n2) + ":" + this.jdbcColumnTypes.get(n2));
        }
        if (n != 0) {
            return null;
        }
        return sb.toString();
    }
    
    public static int[] getExpectedVtiColumnTypes(final String str, final int n) {
        final int[] array = new int[n];
        for (int i = 0; i < n; ++i) {
            array[i] = 12;
        }
        final StringTokenizer stringTokenizer = new StringTokenizer(str, ",");
        while (stringTokenizer.hasMoreTokens()) {
            final String trim = stringTokenizer.nextToken().trim();
            final int index = trim.indexOf(":");
            final int intValue = new Integer(trim.substring(6, index));
            final int intValue2 = new Integer(trim.substring(index + 1));
            if (intValue <= n) {
                array[intValue - 1] = intValue2;
            }
        }
        return array;
    }
    
    public static String[] getExpectedColumnTypeNames(final String s, final int n) throws Exception {
        final ArrayList list = (ArrayList)ImportAbstract.destringifyObject(s);
        final String[] a = new String[list.size()];
        list.toArray(a);
        return a;
    }
    
    public static HashMap getExpectedUDTClasses(final String s) throws Exception {
        final HashMap deserializeHashMap = deserializeHashMap(s);
        if (deserializeHashMap == null) {
            return null;
        }
        final HashMap<String, Class<?>> hashMap = new HashMap<String, Class<?>>();
        for (final Map.Entry<String, V> entry : deserializeHashMap.entrySet()) {
            hashMap.put(entry.getKey(), Class.forName(entry.getValue()));
        }
        return hashMap;
    }
    
    public static HashMap deserializeHashMap(final String s) throws Exception {
        if (s == null) {
            return null;
        }
        return (HashMap)ImportAbstract.destringifyObject(s);
    }
}
