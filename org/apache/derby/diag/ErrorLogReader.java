// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.diag;

import org.apache.derby.impl.jdbc.EmbedResultSetMetaData;
import org.apache.derby.iapi.util.StringUtil;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.io.Reader;
import java.io.FileInputStream;
import org.apache.derby.iapi.error.StandardException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.sql.ResultSetMetaData;
import org.apache.derby.iapi.sql.ResultColumnDescriptor;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.derby.vti.VTITemplate;

public class ErrorLogReader extends VTITemplate
{
    private boolean gotFile;
    private InputStreamReader inputFileStreamReader;
    private InputStream inputStream;
    private BufferedReader bufferedReader;
    private String inputFileName;
    private String line;
    private int endTimestampIndex;
    private int threadIndex;
    private int xidIndex;
    private int lccidIndex;
    private int databaseIndex;
    private int drdaidIndex;
    private static final String END_TIMESTAMP = " Thread";
    private static final String PARAMETERS_STRING = "Parameters:";
    private static final String BEGIN_THREAD_STRING = "[";
    private static final String END_THREAD_STRING = "]";
    private static final String BEGIN_XID_STRING = "= ";
    private static final String END_XID_STRING = ")";
    private static final String BEGIN_DATABASE_STRING = "(DATABASE =";
    private static final String END_DATABASE_STRING = ")";
    private static final String BEGIN_DRDAID_STRING = "(DRDAID =";
    private static final String END_DRDAID_STRING = ")";
    private static final String BEGIN_EXECUTING_STRING = "Executing prepared";
    private static final String END_EXECUTING_STRING = " :End prepared";
    private static final ResultColumnDescriptor[] columnInfo;
    private static final ResultSetMetaData metadata;
    
    public ErrorLogReader() throws StandardException {
        DiagUtil.checkAccess();
        final String str = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction() {
            public Object run() {
                return System.getProperty("derby.system.home");
            }
        });
        this.inputFileName = "derby.log";
        if (str != null) {
            this.inputFileName = str + "/" + this.inputFileName;
        }
    }
    
    public ErrorLogReader(final String inputFileName) throws StandardException {
        DiagUtil.checkAccess();
        this.inputFileName = inputFileName;
    }
    
    public ResultSetMetaData getMetaData() {
        return ErrorLogReader.metadata;
    }
    
    public boolean next() throws SQLException {
        if (!this.gotFile) {
            this.gotFile = true;
            try {
                this.inputFileStreamReader = new InputStreamReader(new FileInputStream(this.inputFileName));
                this.bufferedReader = new BufferedReader(this.inputFileStreamReader, 32768);
            }
            catch (FileNotFoundException ex) {
                throw new SQLException(ex.getMessage());
            }
        }
        while (true) {
            try {
                this.line = this.bufferedReader.readLine();
            }
            catch (IOException ex2) {
                throw new SQLException(ex2.getMessage());
            }
            if (this.line == null) {
                return false;
            }
            this.endTimestampIndex = this.line.indexOf(" Thread");
            this.threadIndex = this.line.indexOf("[");
            this.xidIndex = this.line.indexOf("= ");
            this.lccidIndex = this.line.indexOf("= ", this.xidIndex + 1);
            this.databaseIndex = this.line.indexOf("(DATABASE =", this.lccidIndex + 1);
            this.drdaidIndex = this.line.indexOf("(DRDAID =", this.databaseIndex + 1);
            if (this.line.indexOf("Parameters:") != -1) {
                continue;
            }
            if (this.endTimestampIndex != -1 && this.threadIndex != -1 && this.xidIndex != -1 && this.databaseIndex != -1) {
                return true;
            }
        }
    }
    
    public void close() {
        if (this.bufferedReader != null) {
            try {
                this.bufferedReader.close();
                this.inputFileStreamReader.close();
            }
            catch (IOException ex) {}
            finally {
                this.bufferedReader = null;
                this.inputFileStreamReader = null;
            }
        }
    }
    
    public String getString(final int n) throws SQLException {
        switch (n) {
            case 1: {
                return this.line.substring(0, this.endTimestampIndex);
            }
            case 2: {
                return this.line.substring(this.threadIndex + 1, this.line.indexOf("]"));
            }
            case 3: {
                return this.line.substring(this.xidIndex + 2, this.line.indexOf(")", this.xidIndex));
            }
            case 4: {
                return this.line.substring(this.lccidIndex + 2, this.line.indexOf(")", this.lccidIndex));
            }
            case 5: {
                return this.line.substring(this.databaseIndex + "(DATABASE =".length(), this.line.indexOf(")", this.databaseIndex));
            }
            case 6: {
                return this.line.substring(this.drdaidIndex + "(DRDAID =".length(), this.line.indexOf(")", this.drdaidIndex));
            }
            case 7: {
                final StringBuffer sb = new StringBuffer(64);
                if (this.line.indexOf("Executing prepared") == -1) {
                    sb.append(this.line.substring(this.line.indexOf(")", this.drdaidIndex) + 3));
                }
                else {
                    int i = this.line.indexOf(" :End prepared", this.drdaidIndex);
                    if (i == -1) {
                        sb.append(this.line.substring(this.line.indexOf(")", this.drdaidIndex) + 3));
                    }
                    else {
                        sb.append(this.line.substring(this.line.indexOf(")", this.drdaidIndex) + 3, i));
                    }
                    while (i == -1) {
                        try {
                            this.line = this.bufferedReader.readLine();
                        }
                        catch (IOException obj) {
                            throw new SQLException("Error reading file " + obj);
                        }
                        i = this.line.indexOf(" :End prepared");
                        if (i == -1) {
                            sb.append(this.line);
                        }
                        else {
                            sb.append(this.line.substring(0, i));
                        }
                    }
                }
                return StringUtil.truncate(sb.toString(), 32672);
            }
            default: {
                return "";
            }
        }
    }
    
    public boolean wasNull() {
        return false;
    }
    
    static {
        columnInfo = new ResultColumnDescriptor[] { EmbedResultSetMetaData.getResultColumnDescriptor("TS", 12, false, 29), EmbedResultSetMetaData.getResultColumnDescriptor("THREADID", 12, false, 40), EmbedResultSetMetaData.getResultColumnDescriptor("XID", 12, false, 15), EmbedResultSetMetaData.getResultColumnDescriptor("LCCID", 12, false, 15), EmbedResultSetMetaData.getResultColumnDescriptor("DATABASE", 12, false, 128), EmbedResultSetMetaData.getResultColumnDescriptor("DRDAID", 12, true, 50), EmbedResultSetMetaData.getResultColumnDescriptor("LOGTEXT", 12, false, 32672) };
        metadata = new EmbedResultSetMetaData(ErrorLogReader.columnInfo);
    }
}
