// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import org.apache.tools.ant.types.EnumeratedAttribute;
import java.util.Locale;
import org.apache.tools.ant.util.StringUtils;
import java.sql.Blob;
import java.sql.ResultSetMetaData;
import java.sql.SQLWarning;
import java.sql.ResultSet;
import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.Enumeration;
import java.util.Iterator;
import java.sql.SQLException;
import java.io.IOException;
import org.apache.tools.ant.util.FileUtils;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.BufferedOutputStream;
import org.apache.tools.ant.types.resources.Appendable;
import java.io.FileOutputStream;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.util.KeepAliveOutputStream;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Resource;
import java.util.Vector;
import java.io.File;
import java.sql.Statement;
import org.apache.tools.ant.types.resources.Union;
import java.sql.Connection;

public class SQLExec extends JDBCTask
{
    private int goodSql;
    private int totalSql;
    private Connection conn;
    private Union resources;
    private Statement statement;
    private File srcFile;
    private String sqlCommand;
    private Vector transactions;
    private String delimiter;
    private String delimiterType;
    private boolean print;
    private boolean showheaders;
    private boolean showtrailers;
    private Resource output;
    private String onError;
    private String encoding;
    private boolean append;
    private boolean keepformat;
    private boolean escapeProcessing;
    private boolean expandProperties;
    private boolean rawBlobs;
    private boolean strictDelimiterMatching;
    private boolean showWarnings;
    private String csvColumnSep;
    private String csvQuoteChar;
    private boolean treatWarningsAsErrors;
    private String errorProperty;
    private String warningProperty;
    private String rowCountProperty;
    
    public SQLExec() {
        this.goodSql = 0;
        this.totalSql = 0;
        this.conn = null;
        this.statement = null;
        this.srcFile = null;
        this.sqlCommand = "";
        this.transactions = new Vector();
        this.delimiter = ";";
        this.delimiterType = "normal";
        this.print = false;
        this.showheaders = true;
        this.showtrailers = true;
        this.output = null;
        this.onError = "abort";
        this.encoding = null;
        this.append = false;
        this.keepformat = false;
        this.escapeProcessing = true;
        this.expandProperties = true;
        this.strictDelimiterMatching = true;
        this.showWarnings = false;
        this.csvColumnSep = ",";
        this.csvQuoteChar = null;
        this.treatWarningsAsErrors = false;
        this.errorProperty = null;
        this.warningProperty = null;
        this.rowCountProperty = null;
    }
    
    public void setSrc(final File srcFile) {
        this.srcFile = srcFile;
    }
    
    public void setExpandProperties(final boolean expandProperties) {
        this.expandProperties = expandProperties;
    }
    
    public boolean getExpandProperties() {
        return this.expandProperties;
    }
    
    public void addText(final String sql) {
        this.sqlCommand += sql;
    }
    
    public void addFileset(final FileSet set) {
        this.add(set);
    }
    
    public void add(final ResourceCollection rc) {
        if (rc == null) {
            throw new BuildException("Cannot add null ResourceCollection");
        }
        synchronized (this) {
            if (this.resources == null) {
                this.resources = new Union();
            }
        }
        this.resources.add(rc);
    }
    
    public Transaction createTransaction() {
        final Transaction t = new Transaction();
        this.transactions.addElement(t);
        return t;
    }
    
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }
    
    public void setDelimiter(final String delimiter) {
        this.delimiter = delimiter;
    }
    
    public void setDelimiterType(final DelimiterType delimiterType) {
        this.delimiterType = delimiterType.getValue();
    }
    
    public void setPrint(final boolean print) {
        this.print = print;
    }
    
    public void setShowheaders(final boolean showheaders) {
        this.showheaders = showheaders;
    }
    
    public void setShowtrailers(final boolean showtrailers) {
        this.showtrailers = showtrailers;
    }
    
    public void setOutput(final File output) {
        this.setOutput(new FileResource(this.getProject(), output));
    }
    
    public void setOutput(final Resource output) {
        this.output = output;
    }
    
    public void setAppend(final boolean append) {
        this.append = append;
    }
    
    public void setOnerror(final OnError action) {
        this.onError = action.getValue();
    }
    
    public void setKeepformat(final boolean keepformat) {
        this.keepformat = keepformat;
    }
    
    public void setEscapeProcessing(final boolean enable) {
        this.escapeProcessing = enable;
    }
    
    public void setRawBlobs(final boolean rawBlobs) {
        this.rawBlobs = rawBlobs;
    }
    
    public void setStrictDelimiterMatching(final boolean b) {
        this.strictDelimiterMatching = b;
    }
    
    public void setShowWarnings(final boolean b) {
        this.showWarnings = b;
    }
    
    public void setTreatWarningsAsErrors(final boolean b) {
        this.treatWarningsAsErrors = b;
    }
    
    public void setCsvColumnSeparator(final String s) {
        this.csvColumnSep = s;
    }
    
    public void setCsvQuoteCharacter(final String s) {
        if (s != null && s.length() > 1) {
            throw new BuildException("The quote character must be a single character.");
        }
        this.csvQuoteChar = s;
    }
    
    public void setErrorProperty(final String errorProperty) {
        this.errorProperty = errorProperty;
    }
    
    public void setWarningProperty(final String warningProperty) {
        this.warningProperty = warningProperty;
    }
    
    public void setRowCountProperty(final String rowCountProperty) {
        this.rowCountProperty = rowCountProperty;
    }
    
    @Override
    public void execute() throws BuildException {
        final Vector savedTransaction = (Vector)this.transactions.clone();
        final String savedSqlCommand = this.sqlCommand;
        this.sqlCommand = this.sqlCommand.trim();
        try {
            if (this.srcFile == null && this.sqlCommand.length() == 0 && this.resources == null && this.transactions.size() == 0) {
                throw new BuildException("Source file or resource collection, transactions or sql statement must be set!", this.getLocation());
            }
            if (this.srcFile != null && !this.srcFile.isFile()) {
                throw new BuildException("Source file " + this.srcFile + " is not a file!", this.getLocation());
            }
            if (this.resources != null) {
                for (final Resource r : this.resources) {
                    final Transaction t = this.createTransaction();
                    t.setSrcResource(r);
                }
            }
            final Transaction t2 = this.createTransaction();
            t2.setSrc(this.srcFile);
            t2.addText(this.sqlCommand);
            if (this.getConnection() == null) {
                return;
            }
            try {
                PrintStream out = KeepAliveOutputStream.wrapSystemOut();
                try {
                    if (this.output != null) {
                        this.log("Opening PrintStream to output Resource " + this.output, 3);
                        OutputStream os = null;
                        final FileProvider fp = this.output.as(FileProvider.class);
                        if (fp != null) {
                            os = new FileOutputStream(fp.getFile(), this.append);
                        }
                        else {
                            if (this.append) {
                                final Appendable a = this.output.as(Appendable.class);
                                if (a != null) {
                                    os = a.getAppendOutputStream();
                                }
                            }
                            if (os == null) {
                                os = this.output.getOutputStream();
                                if (this.append) {
                                    this.log("Ignoring append=true for non-appendable resource " + this.output, 1);
                                }
                            }
                        }
                        out = new PrintStream(new BufferedOutputStream(os));
                    }
                    final Enumeration e = this.transactions.elements();
                    while (e.hasMoreElements()) {
                        e.nextElement().runTransaction(out);
                        if (!this.isAutocommit()) {
                            this.log("Committing transaction", 3);
                            this.getConnection().commit();
                        }
                    }
                }
                finally {
                    FileUtils.close(out);
                }
            }
            catch (IOException e2) {
                this.closeQuietly();
                this.setErrorProperty();
                if (this.onError.equals("abort")) {
                    throw new BuildException(e2, this.getLocation());
                }
            }
            catch (SQLException e3) {
                this.closeQuietly();
                this.setErrorProperty();
                if (this.onError.equals("abort")) {
                    throw new BuildException(e3, this.getLocation());
                }
            }
            finally {
                try {
                    if (this.getStatement() != null) {
                        this.getStatement().close();
                    }
                }
                catch (SQLException ex) {}
                try {
                    if (this.getConnection() != null) {
                        this.getConnection().close();
                    }
                }
                catch (SQLException ex2) {}
            }
            this.log(this.goodSql + " of " + this.totalSql + " SQL statements executed successfully");
        }
        finally {
            this.transactions = savedTransaction;
            this.sqlCommand = savedSqlCommand;
        }
    }
    
    protected void runStatements(final Reader reader, final PrintStream out) throws SQLException, IOException {
        final StringBuffer sql = new StringBuffer();
        final BufferedReader in = new BufferedReader(reader);
        String line;
        while ((line = in.readLine()) != null) {
            if (!this.keepformat) {
                line = line.trim();
            }
            if (this.expandProperties) {
                line = this.getProject().replaceProperties(line);
            }
            if (!this.keepformat) {
                if (line.startsWith("//")) {
                    continue;
                }
                if (line.startsWith("--")) {
                    continue;
                }
                final StringTokenizer st = new StringTokenizer(line);
                if (st.hasMoreTokens()) {
                    final String token = st.nextToken();
                    if ("REM".equalsIgnoreCase(token)) {
                        continue;
                    }
                }
            }
            sql.append(this.keepformat ? "\n" : " ").append(line);
            if (!this.keepformat && line.indexOf("--") >= 0) {
                sql.append("\n");
            }
            final int lastDelimPos = this.lastDelimiterPosition(sql, line);
            if (lastDelimPos > -1) {
                this.execSQL(sql.substring(0, lastDelimPos), out);
                sql.replace(0, sql.length(), "");
            }
        }
        if (sql.length() > 0) {
            this.execSQL(sql.toString(), out);
        }
    }
    
    protected void execSQL(final String sql, final PrintStream out) throws SQLException {
        if ("".equals(sql.trim())) {
            return;
        }
        ResultSet resultSet = null;
        try {
            ++this.totalSql;
            this.log("SQL: " + sql, 3);
            int updateCount = 0;
            int updateCountTotal = 0;
            boolean ret = this.getStatement().execute(sql);
            updateCount = this.getStatement().getUpdateCount();
            do {
                if (updateCount != -1) {
                    updateCountTotal += updateCount;
                }
                if (ret) {
                    resultSet = this.getStatement().getResultSet();
                    this.printWarnings(resultSet.getWarnings(), false);
                    resultSet.clearWarnings();
                    if (this.print) {
                        this.printResults(resultSet, out);
                    }
                }
                ret = this.getStatement().getMoreResults();
                updateCount = this.getStatement().getUpdateCount();
            } while (ret || updateCount != -1);
            this.printWarnings(this.getStatement().getWarnings(), false);
            this.getStatement().clearWarnings();
            this.log(updateCountTotal + " rows affected", 3);
            if (updateCountTotal != -1) {
                this.setRowCountProperty(updateCountTotal);
            }
            if (this.print && this.showtrailers) {
                out.println(updateCountTotal + " rows affected");
            }
            final SQLWarning warning = this.getConnection().getWarnings();
            this.printWarnings(warning, true);
            this.getConnection().clearWarnings();
            ++this.goodSql;
        }
        catch (SQLException e) {
            this.log("Failed to execute: " + sql, 0);
            this.setErrorProperty();
            if (!this.onError.equals("abort")) {
                this.log(e.toString(), 0);
            }
            if (!this.onError.equals("continue")) {
                throw e;
            }
        }
        finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                }
                catch (SQLException ex) {}
            }
        }
    }
    
    @Deprecated
    protected void printResults(final PrintStream out) throws SQLException {
        final ResultSet rs = this.getStatement().getResultSet();
        try {
            this.printResults(rs, out);
        }
        finally {
            if (rs != null) {
                rs.close();
            }
        }
    }
    
    protected void printResults(final ResultSet rs, final PrintStream out) throws SQLException {
        if (rs != null) {
            this.log("Processing new result set.", 3);
            final ResultSetMetaData md = rs.getMetaData();
            final int columnCount = md.getColumnCount();
            if (columnCount > 0) {
                if (this.showheaders) {
                    out.print(md.getColumnName(1));
                    for (int col = 2; col <= columnCount; ++col) {
                        out.print(this.csvColumnSep);
                        out.print(this.maybeQuote(md.getColumnName(col)));
                    }
                    out.println();
                }
                while (rs.next()) {
                    this.printValue(rs, 1, out);
                    for (int col = 2; col <= columnCount; ++col) {
                        out.print(this.csvColumnSep);
                        this.printValue(rs, col, out);
                    }
                    out.println();
                    this.printWarnings(rs.getWarnings(), false);
                }
            }
        }
        out.println();
    }
    
    private void printValue(final ResultSet rs, final int col, final PrintStream out) throws SQLException {
        if (this.rawBlobs && rs.getMetaData().getColumnType(col) == 2004) {
            final Blob blob = rs.getBlob(col);
            if (blob != null) {
                new StreamPumper(rs.getBlob(col).getBinaryStream(), out).run();
            }
        }
        else {
            out.print(this.maybeQuote(rs.getString(col)));
        }
    }
    
    private String maybeQuote(final String s) {
        if (this.csvQuoteChar == null || s == null || (s.indexOf(this.csvColumnSep) == -1 && s.indexOf(this.csvQuoteChar) == -1)) {
            return s;
        }
        final StringBuffer sb = new StringBuffer(this.csvQuoteChar);
        final int len = s.length();
        final char q = this.csvQuoteChar.charAt(0);
        for (int i = 0; i < len; ++i) {
            final char c = s.charAt(i);
            if (c == q) {
                sb.append(q);
            }
            sb.append(c);
        }
        return sb.append(this.csvQuoteChar).toString();
    }
    
    private void closeQuietly() {
        if (!this.isAutocommit() && this.getConnection() != null && this.onError.equals("abort")) {
            try {
                this.getConnection().rollback();
            }
            catch (SQLException ex) {}
        }
    }
    
    @Override
    protected Connection getConnection() {
        if (this.conn == null) {
            this.conn = super.getConnection();
            if (!this.isValidRdbms(this.conn)) {
                this.conn = null;
            }
        }
        return this.conn;
    }
    
    protected Statement getStatement() throws SQLException {
        if (this.statement == null) {
            (this.statement = this.getConnection().createStatement()).setEscapeProcessing(this.escapeProcessing);
        }
        return this.statement;
    }
    
    public int lastDelimiterPosition(final StringBuffer buf, final String currentLine) {
        if (this.strictDelimiterMatching) {
            if ((this.delimiterType.equals("normal") && StringUtils.endsWith(buf, this.delimiter)) || (this.delimiterType.equals("row") && currentLine.equals(this.delimiter))) {
                return buf.length() - this.delimiter.length();
            }
            return -1;
        }
        else {
            final String d = this.delimiter.trim().toLowerCase(Locale.ENGLISH);
            if (!this.delimiterType.equals("normal")) {
                return currentLine.trim().toLowerCase(Locale.ENGLISH).equals(d) ? (buf.length() - currentLine.length()) : -1;
            }
            int endIndex = this.delimiter.length() - 1;
            int bufferIndex;
            for (bufferIndex = buf.length() - 1; bufferIndex >= 0 && Character.isWhitespace(buf.charAt(bufferIndex)); --bufferIndex) {}
            if (bufferIndex < endIndex) {
                return -1;
            }
            while (endIndex >= 0) {
                if (buf.substring(bufferIndex, bufferIndex + 1).toLowerCase(Locale.ENGLISH).charAt(0) != d.charAt(endIndex)) {
                    return -1;
                }
                --bufferIndex;
                --endIndex;
            }
            return bufferIndex + 1;
        }
    }
    
    private void printWarnings(SQLWarning warning, final boolean force) throws SQLException {
        final SQLWarning initialWarning = warning;
        if (this.showWarnings || force) {
            while (warning != null) {
                this.log(warning + " sql warning", this.showWarnings ? 1 : 3);
                warning = warning.getNextWarning();
            }
        }
        if (initialWarning != null) {
            this.setWarningProperty();
        }
        if (this.treatWarningsAsErrors && initialWarning != null) {
            throw initialWarning;
        }
    }
    
    protected final void setErrorProperty() {
        this.setProperty(this.errorProperty, "true");
    }
    
    protected final void setWarningProperty() {
        this.setProperty(this.warningProperty, "true");
    }
    
    protected final void setRowCountProperty(final int rowCount) {
        this.setProperty(this.rowCountProperty, Integer.toString(rowCount));
    }
    
    private void setProperty(final String name, final String value) {
        if (name != null) {
            this.getProject().setNewProperty(name, value);
        }
    }
    
    public static class DelimiterType extends EnumeratedAttribute
    {
        public static final String NORMAL = "normal";
        public static final String ROW = "row";
        
        @Override
        public String[] getValues() {
            return new String[] { "normal", "row" };
        }
    }
    
    public static class OnError extends EnumeratedAttribute
    {
        @Override
        public String[] getValues() {
            return new String[] { "continue", "stop", "abort" };
        }
    }
    
    public class Transaction
    {
        private Resource tSrcResource;
        private String tSqlCommand;
        
        public Transaction() {
            this.tSrcResource = null;
            this.tSqlCommand = "";
        }
        
        public void setSrc(final File src) {
            if (src != null) {
                this.setSrcResource(new FileResource(src));
            }
        }
        
        public void setSrcResource(final Resource src) {
            if (this.tSrcResource != null) {
                throw new BuildException("only one resource per transaction");
            }
            this.tSrcResource = src;
        }
        
        public void addText(final String sql) {
            if (sql != null) {
                this.tSqlCommand += sql;
            }
        }
        
        public void addConfigured(final ResourceCollection a) {
            if (a.size() != 1) {
                throw new BuildException("only single argument resource collections are supported.");
            }
            this.setSrcResource(a.iterator().next());
        }
        
        private void runTransaction(final PrintStream out) throws IOException, SQLException {
            if (this.tSqlCommand.length() != 0) {
                SQLExec.this.log("Executing commands", 2);
                SQLExec.this.runStatements(new StringReader(this.tSqlCommand), out);
            }
            if (this.tSrcResource != null) {
                SQLExec.this.log("Executing resource: " + this.tSrcResource.toString(), 2);
                InputStream is = null;
                Reader reader = null;
                try {
                    is = this.tSrcResource.getInputStream();
                    reader = ((SQLExec.this.encoding == null) ? new InputStreamReader(is) : new InputStreamReader(is, SQLExec.this.encoding));
                    SQLExec.this.runStatements(reader, out);
                }
                finally {
                    FileUtils.close(is);
                    FileUtils.close(reader);
                }
            }
        }
    }
}
