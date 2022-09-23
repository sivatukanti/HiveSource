// 
// Decompiled by Procyon v0.5.36
// 

package au.com.bytecode.opencsv;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;
import java.io.PrintWriter;
import java.io.Writer;
import java.io.Closeable;

public class CSVWriter implements Closeable
{
    public static final int INITIAL_STRING_SIZE = 128;
    private Writer rawWriter;
    private PrintWriter pw;
    private char separator;
    private char quotechar;
    private char escapechar;
    private String lineEnd;
    public static final char DEFAULT_ESCAPE_CHARACTER = '\"';
    public static final char DEFAULT_SEPARATOR = ',';
    public static final char DEFAULT_QUOTE_CHARACTER = '\"';
    public static final char NO_QUOTE_CHARACTER = '\0';
    public static final char NO_ESCAPE_CHARACTER = '\0';
    public static final String DEFAULT_LINE_END = "\n";
    private ResultSetHelper resultService;
    
    public CSVWriter(final Writer writer) {
        this(writer, ',');
    }
    
    public CSVWriter(final Writer writer, final char separator) {
        this(writer, separator, '\"');
    }
    
    public CSVWriter(final Writer writer, final char separator, final char quotechar) {
        this(writer, separator, quotechar, '\"');
    }
    
    public CSVWriter(final Writer writer, final char separator, final char quotechar, final char escapechar) {
        this(writer, separator, quotechar, escapechar, "\n");
    }
    
    public CSVWriter(final Writer writer, final char separator, final char quotechar, final String lineEnd) {
        this(writer, separator, quotechar, '\"', lineEnd);
    }
    
    public CSVWriter(final Writer writer, final char separator, final char quotechar, final char escapechar, final String lineEnd) {
        this.resultService = new ResultSetHelperService();
        this.rawWriter = writer;
        this.pw = new PrintWriter(writer);
        this.separator = separator;
        this.quotechar = quotechar;
        this.escapechar = escapechar;
        this.lineEnd = lineEnd;
    }
    
    public void writeAll(final List<String[]> allLines) {
        for (final String[] line : allLines) {
            this.writeNext(line);
        }
    }
    
    protected void writeColumnNames(final ResultSet rs) throws SQLException {
        this.writeNext(this.resultService.getColumnNames(rs));
    }
    
    public void writeAll(final ResultSet rs, final boolean includeColumnNames) throws SQLException, IOException {
        if (includeColumnNames) {
            this.writeColumnNames(rs);
        }
        while (rs.next()) {
            this.writeNext(this.resultService.getColumnValues(rs));
        }
    }
    
    public void writeNext(final String[] nextLine) {
        if (nextLine == null) {
            return;
        }
        final StringBuilder sb = new StringBuilder(128);
        for (int i = 0; i < nextLine.length; ++i) {
            if (i != 0) {
                sb.append(this.separator);
            }
            final String nextElement = nextLine[i];
            if (nextElement != null) {
                if (this.quotechar != '\0') {
                    sb.append(this.quotechar);
                }
                sb.append((CharSequence)(this.stringContainsSpecialCharacters(nextElement) ? this.processLine(nextElement) : nextElement));
                if (this.quotechar != '\0') {
                    sb.append(this.quotechar);
                }
            }
        }
        sb.append(this.lineEnd);
        this.pw.write(sb.toString());
    }
    
    private boolean stringContainsSpecialCharacters(final String line) {
        return line.indexOf(this.quotechar) != -1 || line.indexOf(this.escapechar) != -1;
    }
    
    protected StringBuilder processLine(final String nextElement) {
        final StringBuilder sb = new StringBuilder(128);
        for (int j = 0; j < nextElement.length(); ++j) {
            final char nextChar = nextElement.charAt(j);
            if (this.escapechar != '\0' && nextChar == this.quotechar) {
                sb.append(this.escapechar).append(nextChar);
            }
            else if (this.escapechar != '\0' && nextChar == this.escapechar) {
                sb.append(this.escapechar).append(nextChar);
            }
            else {
                sb.append(nextChar);
            }
        }
        return sb;
    }
    
    public void flush() throws IOException {
        this.pw.flush();
    }
    
    public void close() throws IOException {
        this.flush();
        this.pw.close();
        this.rawWriter.close();
    }
    
    public boolean checkError() {
        return this.pw.checkError();
    }
    
    public void setResultService(final ResultSetHelper resultService) {
        this.resultService = resultService;
    }
}
