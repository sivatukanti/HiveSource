// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.load;

import org.apache.derby.iapi.util.StringUtil;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import org.apache.derby.iapi.services.io.DynamicByteArrayOutputStream;
import java.sql.ResultSetMetaData;
import java.sql.ResultSet;
import java.sql.Connection;

abstract class ExportAbstract
{
    protected ControlInfo controlFileReader;
    protected ExportResultSetForObject exportResultSetForObject;
    protected ExportWriteDataAbstract exportWriteData;
    protected Connection con;
    protected String entityName;
    protected String schemaName;
    protected String selectStatement;
    protected boolean lobsInExtFile;
    
    ExportAbstract() {
        this.lobsInExtFile = false;
    }
    
    protected ResultSet resultSetForEntity() throws Exception {
        this.exportResultSetForObject = new ExportResultSetForObject(this.con, this.schemaName, this.entityName, this.selectStatement);
        return this.exportResultSetForObject.getResultSet();
    }
    
    private String[] getOneRowAtATime(final ResultSet set, final boolean[] array, final boolean[] array2) throws Exception {
        if (set.next()) {
            final int columnCount = this.exportResultSetForObject.getColumnCount();
            final ResultSetMetaData metaData = set.getMetaData();
            final String[] array3 = new String[columnCount];
            for (int i = 0; i < columnCount; ++i) {
                if (this.lobsInExtFile && (array2[i] || array[i])) {
                    String s;
                    if (array[i]) {
                        s = this.exportWriteData.writeBinaryColumnToExternalFile(set.getBinaryStream(i + 1));
                    }
                    else {
                        s = this.exportWriteData.writeCharColumnToExternalFile(set.getCharacterStream(i + 1));
                    }
                    array3[i] = s;
                }
                else {
                    final int n = i + 1;
                    String s2;
                    if (metaData.getColumnType(n) == 2000) {
                        s2 = stringifyObject(set.getObject(n));
                    }
                    else {
                        s2 = set.getString(n);
                    }
                    array3[i] = s2;
                }
            }
            return array3;
        }
        set.close();
        this.exportResultSetForObject.close();
        return null;
    }
    
    public static String stringifyObject(final Object obj) throws Exception {
        final DynamicByteArrayOutputStream out = new DynamicByteArrayOutputStream();
        new ObjectOutputStream(out).writeObject(obj);
        return StringUtil.toHexString(out.getByteArray(), 0, out.getUsed());
    }
    
    protected ControlInfo getControlFileReader() {
        return this.controlFileReader;
    }
    
    protected abstract ExportWriteDataAbstract getExportWriteData() throws Exception;
    
    protected void doAllTheWork() throws Exception {
        ResultSet resultSetForEntity = null;
        try {
            resultSetForEntity = this.resultSetForEntity();
            if (resultSetForEntity != null) {
                final ResultSetMetaData metaData = resultSetForEntity.getMetaData();
                final int columnCount = metaData.getColumnCount();
                final boolean[] array = new boolean[columnCount];
                final boolean[] array2 = new boolean[columnCount];
                final boolean[] array3 = new boolean[columnCount];
                for (int i = 0; i < columnCount; ++i) {
                    final int columnType = metaData.getColumnType(i + 1);
                    if (columnType == -5 || columnType == 3 || columnType == 8 || columnType == 6 || columnType == 4 || columnType == 2 || columnType == 7 || columnType == 5 || columnType == -6) {
                        array[i] = true;
                    }
                    else {
                        array[i] = false;
                    }
                    if (columnType == 2005) {
                        array2[i] = true;
                    }
                    else {
                        array2[i] = false;
                    }
                    if (columnType == 2004) {
                        array3[i] = true;
                    }
                    else {
                        array3[i] = false;
                    }
                }
                (this.exportWriteData = this.getExportWriteData()).writeColumnDefinitionOptionally(this.exportResultSetForObject.getColumnDefinition(), this.exportResultSetForObject.getColumnTypes());
                this.exportWriteData.setColumnLengths(this.controlFileReader.getColumnWidths());
                for (String[] array4 = this.getOneRowAtATime(resultSetForEntity, array3, array2); array4 != null; array4 = this.getOneRowAtATime(resultSetForEntity, array3, array2)) {
                    this.exportWriteData.writeData(array4, array);
                }
            }
        }
        finally {
            if (this.exportWriteData != null) {
                this.exportWriteData.noMoreRows();
            }
            if (resultSetForEntity != null) {
                resultSetForEntity.close();
            }
        }
    }
}
