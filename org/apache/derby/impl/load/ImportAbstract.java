// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.load;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import org.apache.derby.iapi.error.PublicAPI;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.util.StringUtil;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import org.apache.derby.vti.VTITemplate;

abstract class ImportAbstract extends VTITemplate
{
    ControlInfo controlFileReader;
    ImportReadData importReadData;
    String[] columnNames;
    int numberOfColumns;
    int[] columnWidths;
    int lineNumber;
    String[] nextRow;
    ImportResultSetMetaData importResultSetMetaData;
    int noOfColumnsExpected;
    protected boolean lobsInExtFile;
    String tableColumnTypesStr;
    int[] tableColumnTypes;
    String columnTypeNamesString;
    String[] columnTypeNames;
    String udtClassNamesString;
    HashMap udtClasses;
    private boolean wasNull;
    static final String COLUMNNAMEPREFIX = "COLUMN";
    
    ImportAbstract() {
        this.lineNumber = 0;
        this.lobsInExtFile = false;
    }
    
    abstract ImportReadData getImportReadData() throws Exception;
    
    void doAllTheWork() throws Exception {
        this.importReadData = this.getImportReadData();
        this.numberOfColumns = this.importReadData.getNumberOfColumns();
        if (this.numberOfColumns == 0) {
            this.numberOfColumns = this.noOfColumnsExpected;
        }
        this.columnWidths = this.controlFileReader.getColumnWidths();
        this.columnNames = new String[this.numberOfColumns];
        this.loadColumnNames();
        this.nextRow = new String[this.numberOfColumns];
        this.tableColumnTypes = ColumnInfo.getExpectedVtiColumnTypes(this.tableColumnTypesStr, this.numberOfColumns);
        this.columnTypeNames = ColumnInfo.getExpectedColumnTypeNames(this.columnTypeNamesString, this.numberOfColumns);
        this.udtClasses = ColumnInfo.getExpectedUDTClasses(this.udtClassNamesString);
        this.importResultSetMetaData = new ImportResultSetMetaData(this.numberOfColumns, this.columnNames, this.columnWidths, this.tableColumnTypes, this.columnTypeNames, this.udtClasses);
    }
    
    void loadColumnNames() {
        for (int i = 1; i <= this.numberOfColumns; ++i) {
            this.columnNames[i - 1] = "COLUMN" + i;
        }
    }
    
    public ResultSetMetaData getMetaData() {
        return this.importResultSetMetaData;
    }
    
    public int getRow() throws SQLException {
        return this.importReadData.getCurrentRowNumber();
    }
    
    public int getCurrentLineNumber() {
        return this.lineNumber;
    }
    
    public boolean next() throws SQLException {
        try {
            ++this.lineNumber;
            return this.importReadData.readNextRow(this.nextRow);
        }
        catch (Exception ex) {
            throw this.importError(ex);
        }
    }
    
    public void close() throws SQLException {
        try {
            if (this.importReadData != null) {
                this.importReadData.closeStream();
            }
        }
        catch (Exception ex) {
            throw LoadError.unexpectedError(ex);
        }
    }
    
    public boolean wasNull() {
        return this.wasNull;
    }
    
    public String getString(final int n) throws SQLException {
        if (n <= this.numberOfColumns) {
            String clobColumnFromExtFileAsString = this.nextRow[n - 1];
            if (this.isColumnInExtFile(n)) {
                clobColumnFromExtFileAsString = this.importReadData.getClobColumnFromExtFileAsString(clobColumnFromExtFileAsString, n);
            }
            this.wasNull = (clobColumnFromExtFileAsString == null);
            return clobColumnFromExtFileAsString;
        }
        throw LoadError.invalidColumnNumber(this.numberOfColumns);
    }
    
    public Clob getClob(final int n) throws SQLException {
        Clob clobColumnFromExtFile = null;
        if (this.lobsInExtFile) {
            clobColumnFromExtFile = this.importReadData.getClobColumnFromExtFile(this.nextRow[n - 1], n);
        }
        else {
            final String s = this.nextRow[n - 1];
            if (s != null) {
                clobColumnFromExtFile = new ImportClob(s);
            }
        }
        this.wasNull = (clobColumnFromExtFile == null);
        return clobColumnFromExtFile;
    }
    
    public Blob getBlob(final int n) throws SQLException {
        Blob blobColumnFromExtFile = null;
        if (this.lobsInExtFile) {
            blobColumnFromExtFile = this.importReadData.getBlobColumnFromExtFile(this.nextRow[n - 1], n);
        }
        else {
            final String s = this.nextRow[n - 1];
            if (s != null) {
                final byte[] fromHexString = StringUtil.fromHexString(s, 0, s.length());
                if (fromHexString == null) {
                    throw PublicAPI.wrapStandardException(StandardException.newException("XIE0N.S", s));
                }
                blobColumnFromExtFile = new ImportBlob(fromHexString);
            }
        }
        this.wasNull = (blobColumnFromExtFile == null);
        return blobColumnFromExtFile;
    }
    
    public Object getObject(final int n) throws SQLException {
        final byte[] bytes = this.getBytes(n);
        try {
            final Class udtClass = this.importResultSetMetaData.getUDTClass(n);
            final Object object = readObject(bytes);
            if (object != null && !udtClass.isInstance(object)) {
                throw new ClassCastException(object.getClass().getName() + " -> " + udtClass.getName());
            }
            return object;
        }
        catch (Exception ex) {
            throw this.importError(ex);
        }
    }
    
    public static Object readObject(final byte[] buf) throws Exception {
        return new ObjectInputStream(new ByteArrayInputStream(buf)).readObject();
    }
    
    public static Object destringifyObject(final String s) throws Exception {
        return readObject(StringUtil.fromHexString(s, 0, s.length()));
    }
    
    public byte[] getBytes(final int n) throws SQLException {
        final String s = this.nextRow[n - 1];
        this.wasNull = (s == null);
        byte[] fromHexString = null;
        if (s != null) {
            fromHexString = StringUtil.fromHexString(s, 0, s.length());
            if (fromHexString == null) {
                throw PublicAPI.wrapStandardException(StandardException.newException("XIE0N.S", s));
            }
        }
        return fromHexString;
    }
    
    private boolean isColumnInExtFile(final int n) {
        return this.lobsInExtFile && (this.tableColumnTypes[n - 1] == 2004 || this.tableColumnTypes[n - 1] == 2005);
    }
    
    public SQLException importError(final Exception ex) {
        Throwable t = null;
        if (this.importReadData != null) {
            try {
                this.importReadData.closeStream();
            }
            catch (Exception ex2) {
                t = ex2;
            }
        }
        final SQLException unexpectedError = LoadError.unexpectedError(ex);
        if (t != null) {
            unexpectedError.setNextException(LoadError.unexpectedError(t));
        }
        return unexpectedError;
    }
}
