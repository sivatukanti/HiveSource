// 
// Decompiled by Procyon v0.5.36
// 

package au.com.bytecode.opencsv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.Closeable;

public class CSVReader implements Closeable
{
    private BufferedReader br;
    private boolean hasNext;
    private CSVParser parser;
    private int skipLines;
    private boolean linesSkiped;
    public static final int DEFAULT_SKIP_LINES = 0;
    
    public CSVReader(final Reader reader) {
        this(reader, ',', '\"', '\\');
    }
    
    public CSVReader(final Reader reader, final char separator) {
        this(reader, separator, '\"', '\\');
    }
    
    public CSVReader(final Reader reader, final char separator, final char quotechar) {
        this(reader, separator, quotechar, '\\', 0, false);
    }
    
    public CSVReader(final Reader reader, final char separator, final char quotechar, final boolean strictQuotes) {
        this(reader, separator, quotechar, '\\', 0, strictQuotes);
    }
    
    public CSVReader(final Reader reader, final char separator, final char quotechar, final char escape) {
        this(reader, separator, quotechar, escape, 0, false);
    }
    
    public CSVReader(final Reader reader, final char separator, final char quotechar, final int line) {
        this(reader, separator, quotechar, '\\', line, false);
    }
    
    public CSVReader(final Reader reader, final char separator, final char quotechar, final char escape, final int line) {
        this(reader, separator, quotechar, escape, line, false);
    }
    
    public CSVReader(final Reader reader, final char separator, final char quotechar, final char escape, final int line, final boolean strictQuotes) {
        this(reader, separator, quotechar, escape, line, strictQuotes, true);
    }
    
    public CSVReader(final Reader reader, final char separator, final char quotechar, final char escape, final int line, final boolean strictQuotes, final boolean ignoreLeadingWhiteSpace) {
        this.hasNext = true;
        this.br = new BufferedReader(reader);
        this.parser = new CSVParser(separator, quotechar, escape, strictQuotes, ignoreLeadingWhiteSpace);
        this.skipLines = line;
    }
    
    public List<String[]> readAll() throws IOException {
        final List<String[]> allElements = new ArrayList<String[]>();
        while (this.hasNext) {
            final String[] nextLineAsTokens = this.readNext();
            if (nextLineAsTokens != null) {
                allElements.add(nextLineAsTokens);
            }
        }
        return allElements;
    }
    
    public String[] readNext() throws IOException {
        String[] result = null;
        do {
            final String nextLine = this.getNextLine();
            if (!this.hasNext) {
                return result;
            }
            final String[] r = this.parser.parseLineMulti(nextLine);
            if (r.length <= 0) {
                continue;
            }
            if (result == null) {
                result = r;
            }
            else {
                final String[] t = new String[result.length + r.length];
                System.arraycopy(result, 0, t, 0, result.length);
                System.arraycopy(r, 0, t, result.length, r.length);
                result = t;
            }
        } while (this.parser.isPending());
        return result;
    }
    
    private String getNextLine() throws IOException {
        if (!this.linesSkiped) {
            for (int i = 0; i < this.skipLines; ++i) {
                this.br.readLine();
            }
            this.linesSkiped = true;
        }
        final String nextLine = this.br.readLine();
        if (nextLine == null) {
            this.hasNext = false;
        }
        return this.hasNext ? nextLine : null;
    }
    
    public void close() throws IOException {
        this.br.close();
    }
}
