// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.load;

import org.apache.derby.iapi.util.StringUtil;
import org.apache.derby.iapi.error.PublicAPI;
import org.apache.derby.iapi.error.StandardException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import org.apache.derby.iapi.util.IdUtil;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;

public class Import extends ImportAbstract
{
    private static int _importCounter;
    private static Hashtable _importers;
    private String inputFileName;
    
    public Import(final String inputFileName, final String s, final String s2, final String s3, final int noOfColumnsExpected, final String tableColumnTypesStr, final boolean lobsInExtFile, final int value, final String columnTypeNamesString, final String udtClassNamesString) throws SQLException {
        try {
            this.inputFileName = inputFileName;
            this.noOfColumnsExpected = noOfColumnsExpected;
            this.tableColumnTypesStr = tableColumnTypesStr;
            this.columnTypeNamesString = columnTypeNamesString;
            this.udtClassNamesString = udtClassNamesString;
            (this.controlFileReader = new ControlInfo()).setControlProperties(s2, s, s3);
            this.lobsInExtFile = lobsInExtFile;
            Import._importers.put(new Integer(value), this);
            this.doImport();
        }
        catch (Exception ex) {
            throw this.importError(ex);
        }
    }
    
    private void doImport() throws Exception {
        if (this.inputFileName == null) {
            throw LoadError.dataFileNull();
        }
        this.doAllTheWork();
    }
    
    public static void importTable(final Connection connection, final String s, final String s2, final String s3, final String s4, final String s5, final String s6, final short n, final boolean b) throws SQLException {
        performImport(connection, s, null, null, s2, s3, s4, s5, s6, n, b);
    }
    
    public static void importData(final Connection connection, final String s, final String s2, final String s3, final String s4, final String s5, final String s6, final String s7, final String s8, final short n, final boolean b) throws SQLException {
        performImport(connection, s, s3, s4, s2, s5, s6, s7, s8, n, b);
    }
    
    private static void performImport(final Connection connection, final String s, final String s2, final String s3, final String s4, final String s5, final String s6, final String s7, final String s8, final short n, final boolean b) throws SQLException {
        final Integer key = new Integer(bumpImportCounter());
        try {
            if (connection == null) {
                throw LoadError.connectionNull();
            }
            if (s4 == null) {
                throw LoadError.entityNameMissing();
            }
            final ColumnInfo columnInfo = new ColumnInfo(connection, s, s4, s2, s3, "COLUMN");
            String columnTypeNames;
            String udtClassNames;
            try {
                columnTypeNames = columnInfo.getColumnTypeNames();
                udtClassNames = columnInfo.getUDTClassNames();
            }
            catch (Throwable t) {
                throw formatImportError((Import)Import._importers.get(key), s5, t);
            }
            final StringBuffer sb = new StringBuffer("new ");
            sb.append("org.apache.derby.impl.load.Import");
            sb.append("(");
            sb.append(quoteStringArgument(s5));
            sb.append(",");
            sb.append(quoteStringArgument(s6));
            sb.append(",");
            sb.append(quoteStringArgument(s7));
            sb.append(",");
            sb.append(quoteStringArgument(s8));
            sb.append(", ");
            sb.append(columnInfo.getExpectedNumberOfColumnsInFile());
            sb.append(", ");
            sb.append(quoteStringArgument(columnInfo.getExpectedVtiColumnTypesAsString()));
            sb.append(", ");
            sb.append(b);
            sb.append(", ");
            sb.append((int)key);
            sb.append(", ");
            sb.append(quoteStringArgument(columnTypeNames));
            sb.append(", ");
            sb.append(quoteStringArgument(udtClassNames));
            sb.append(" )");
            final String string = sb.toString();
            final String mkQualifiedName = IdUtil.mkQualifiedName(s, s4);
            String str;
            if (n > 0) {
                str = "replace";
            }
            else {
                str = "bulkInsert";
            }
            final String columnNamesWithCasts = columnInfo.getColumnNamesWithCasts();
            final String insertColumnNames = columnInfo.getInsertColumnNames();
            String string2;
            if (insertColumnNames != null) {
                string2 = "(" + insertColumnNames + ") ";
            }
            else {
                string2 = "";
            }
            final PreparedStatement prepareStatement = connection.prepareStatement("INSERT INTO " + mkQualifiedName + string2 + " --DERBY-PROPERTIES insertMode=" + str + "\n" + " SELECT " + columnNamesWithCasts + " from " + string + " AS importvti");
            final Statement statement = connection.createStatement();
            statement.executeUpdate("LOCK TABLE " + mkQualifiedName + " IN EXCLUSIVE MODE");
            try {
                prepareStatement.executeUpdate();
            }
            catch (Throwable t2) {
                throw formatImportError((Import)Import._importers.get(key), s5, t2);
            }
            statement.close();
            prepareStatement.close();
        }
        finally {
            Import._importers.remove(key);
        }
    }
    
    ImportReadData getImportReadData() throws Exception {
        return new ImportReadData(this.inputFileName, this.controlFileReader);
    }
    
    private static synchronized int bumpImportCounter() {
        return ++Import._importCounter;
    }
    
    private static SQLException formatImportError(final Import import1, final String s, final Throwable cause) {
        int currentLineNumber = -1;
        if (import1 != null) {
            currentLineNumber = import1.getCurrentLineNumber();
        }
        final StandardException exception = StandardException.newException("XIE0R.S", new Integer(currentLineNumber), s, cause.getMessage());
        exception.initCause(cause);
        return PublicAPI.wrapStandardException(exception);
    }
    
    private static String quoteStringArgument(final String s) {
        if (s == null) {
            return "NULL";
        }
        return StringUtil.quoteStringLiteral(s);
    }
    
    static {
        Import._importers = new Hashtable();
    }
}
