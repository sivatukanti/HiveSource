// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.diag;

import org.apache.derby.impl.jdbc.EmbedResultSetMetaData;
import org.apache.derby.iapi.util.StringUtil;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.sql.Timestamp;
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
import java.util.Hashtable;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.derby.vti.VTITemplate;

public class StatementDuration extends VTITemplate
{
    private boolean gotFile;
    private InputStreamReader inputFileStreamReader;
    private InputStream inputStream;
    private BufferedReader bufferedReader;
    private String inputFileName;
    private Hashtable hashTable;
    private String line;
    private int endTimestampIndex;
    private int threadIndex;
    private int xidIndex;
    private int lccidIndex;
    private String[] currentRow;
    private static final String END_TIMESTAMP = " Thread";
    private static final String BEGIN_THREAD_STRING = "[";
    private static final String END_THREAD_STRING = "]";
    private static final String BEGIN_XID_STRING = "= ";
    private static final String END_XID_STRING = ")";
    private static final String BEGIN_EXECUTING_STRING = "Executing prepared";
    private static final String END_EXECUTING_STRING = " :End prepared";
    private static final ResultColumnDescriptor[] columnInfo;
    private static final ResultSetMetaData metadata;
    
    public StatementDuration() throws StandardException {
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
    
    public StatementDuration(final String inputFileName) throws StandardException {
        DiagUtil.checkAccess();
        this.inputFileName = inputFileName;
    }
    
    public ResultSetMetaData getMetaData() {
        return StatementDuration.metadata;
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
            this.hashTable = new Hashtable();
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
            if (this.endTimestampIndex == -1 || this.threadIndex == -1 || this.xidIndex == -1) {
                continue;
            }
            final String[] value = new String[6];
            for (int i = 1; i <= 5; ++i) {
                value[i - 1] = this.setupColumn(i);
            }
            final String[] put = this.hashTable.put(value[3], value);
            if (put == null) {
                continue;
            }
            this.currentRow = put;
            final Timestamp stringToTimestamp = this.stringToTimestamp(value[0]);
            final long n = stringToTimestamp.getTime() + stringToTimestamp.getNanos() / 1000000;
            final Timestamp stringToTimestamp2 = this.stringToTimestamp(this.currentRow[0]);
            this.currentRow[5] = Long.toString(n - (stringToTimestamp2.getTime() + stringToTimestamp2.getNanos() / 1000000));
            return true;
        }
    }
    
    private Timestamp stringToTimestamp(final String s) throws SQLException {
        final String trim = s.trim();
        if (!Character.isDigit(trim.charAt(trim.length() - 1))) {
            return Timestamp.valueOf(trim.substring(0, trim.length() - 4));
        }
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        try {
            return new Timestamp(simpleDateFormat.parse(trim).getTime());
        }
        catch (Exception ex) {
            throw new SQLException(ex.getMessage());
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
        return this.currentRow[n - 1];
    }
    
    private String setupColumn(final int n) throws SQLException {
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
                final StringBuffer sb = new StringBuffer(64);
                if (this.line.indexOf("Executing prepared") == -1) {
                    sb.append(this.line.substring(this.line.indexOf(")", this.lccidIndex) + 3));
                }
                else {
                    int i = this.line.indexOf(" :End prepared", this.lccidIndex);
                    if (i == -1) {
                        sb.append(this.line.substring(this.line.indexOf(")", this.lccidIndex) + 3));
                    }
                    else {
                        sb.append(this.line.substring(this.line.indexOf(")", this.lccidIndex) + 3, i));
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
                return null;
            }
        }
    }
    
    public boolean wasNull() {
        return false;
    }
    
    static {
        columnInfo = new ResultColumnDescriptor[] { EmbedResultSetMetaData.getResultColumnDescriptor("TS", 12, false, 29), EmbedResultSetMetaData.getResultColumnDescriptor("THREADID", 12, false, 80), EmbedResultSetMetaData.getResultColumnDescriptor("XID", 12, false, 15), EmbedResultSetMetaData.getResultColumnDescriptor("LCCID", 12, false, 10), EmbedResultSetMetaData.getResultColumnDescriptor("LOGTEXT", 12, true, 32672), EmbedResultSetMetaData.getResultColumnDescriptor("DURATION", 12, false, 10) };
        metadata = new EmbedResultSetMetaData(StatementDuration.columnInfo);
    }
}
