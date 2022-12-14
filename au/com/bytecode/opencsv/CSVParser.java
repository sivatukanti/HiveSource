// 
// Decompiled by Procyon v0.5.36
// 

package au.com.bytecode.opencsv;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

public class CSVParser
{
    private final char separator;
    private final char quotechar;
    private final char escape;
    private final boolean strictQuotes;
    private String pending;
    private boolean inField;
    private final boolean ignoreLeadingWhiteSpace;
    public static final char DEFAULT_SEPARATOR = ',';
    public static final int INITIAL_READ_SIZE = 128;
    public static final char DEFAULT_QUOTE_CHARACTER = '\"';
    public static final char DEFAULT_ESCAPE_CHARACTER = '\\';
    public static final boolean DEFAULT_STRICT_QUOTES = false;
    public static final boolean DEFAULT_IGNORE_LEADING_WHITESPACE = true;
    public static final char NULL_CHARACTER = '\0';
    
    public CSVParser() {
        this(',', '\"', '\\');
    }
    
    public CSVParser(final char separator) {
        this(separator, '\"', '\\');
    }
    
    public CSVParser(final char separator, final char quotechar) {
        this(separator, quotechar, '\\');
    }
    
    public CSVParser(final char separator, final char quotechar, final char escape) {
        this(separator, quotechar, escape, false);
    }
    
    public CSVParser(final char separator, final char quotechar, final char escape, final boolean strictQuotes) {
        this(separator, quotechar, escape, strictQuotes, true);
    }
    
    public CSVParser(final char separator, final char quotechar, final char escape, final boolean strictQuotes, final boolean ignoreLeadingWhiteSpace) {
        this.inField = false;
        if (this.anyCharactersAreTheSame(separator, quotechar, escape)) {
            throw new UnsupportedOperationException("The separator, quote, and escape characters must be different!");
        }
        if (separator == '\0') {
            throw new UnsupportedOperationException("The separator character must be defined!");
        }
        this.separator = separator;
        this.quotechar = quotechar;
        this.escape = escape;
        this.strictQuotes = strictQuotes;
        this.ignoreLeadingWhiteSpace = ignoreLeadingWhiteSpace;
    }
    
    private boolean anyCharactersAreTheSame(final char separator, final char quotechar, final char escape) {
        return this.isSameCharacter(separator, quotechar) || this.isSameCharacter(separator, escape) || this.isSameCharacter(quotechar, escape);
    }
    
    private boolean isSameCharacter(final char c1, final char c2) {
        return c1 != '\0' && c1 == c2;
    }
    
    public boolean isPending() {
        return this.pending != null;
    }
    
    public String[] parseLineMulti(final String nextLine) throws IOException {
        return this.parseLine(nextLine, true);
    }
    
    public String[] parseLine(final String nextLine) throws IOException {
        return this.parseLine(nextLine, false);
    }
    
    private String[] parseLine(final String nextLine, final boolean multi) throws IOException {
        if (!multi && this.pending != null) {
            this.pending = null;
        }
        if (nextLine != null) {
            final List<String> tokensOnThisLine = new ArrayList<String>();
            StringBuilder sb = new StringBuilder(128);
            boolean inQuotes = false;
            if (this.pending != null) {
                sb.append(this.pending);
                this.pending = null;
                inQuotes = true;
            }
            for (int i = 0; i < nextLine.length(); ++i) {
                final char c = nextLine.charAt(i);
                if (c == this.escape) {
                    if (this.isNextCharacterEscapable(nextLine, inQuotes || this.inField, i)) {
                        sb.append(nextLine.charAt(i + 1));
                        ++i;
                    }
                }
                else if (c == this.quotechar) {
                    if (this.isNextCharacterEscapedQuote(nextLine, inQuotes || this.inField, i)) {
                        sb.append(nextLine.charAt(i + 1));
                        ++i;
                    }
                    else {
                        if (!this.strictQuotes && i > 2 && nextLine.charAt(i - 1) != this.separator && nextLine.length() > i + 1 && nextLine.charAt(i + 1) != this.separator) {
                            if (this.ignoreLeadingWhiteSpace && sb.length() > 0 && this.isAllWhiteSpace(sb)) {
                                sb.setLength(0);
                            }
                            else {
                                sb.append(c);
                            }
                        }
                        inQuotes = !inQuotes;
                    }
                    this.inField = !this.inField;
                }
                else if (c == this.separator && !inQuotes) {
                    tokensOnThisLine.add(sb.toString());
                    sb.setLength(0);
                    this.inField = false;
                }
                else if (!this.strictQuotes || inQuotes) {
                    sb.append(c);
                    this.inField = true;
                }
            }
            if (inQuotes) {
                if (!multi) {
                    throw new IOException("Un-terminated quoted field at end of CSV line");
                }
                sb.append("\n");
                this.pending = sb.toString();
                sb = null;
            }
            if (sb != null) {
                tokensOnThisLine.add(sb.toString());
            }
            return tokensOnThisLine.toArray(new String[tokensOnThisLine.size()]);
        }
        if (this.pending != null) {
            final String s = this.pending;
            this.pending = null;
            return new String[] { s };
        }
        return null;
    }
    
    private boolean isNextCharacterEscapedQuote(final String nextLine, final boolean inQuotes, final int i) {
        return inQuotes && nextLine.length() > i + 1 && nextLine.charAt(i + 1) == this.quotechar;
    }
    
    protected boolean isNextCharacterEscapable(final String nextLine, final boolean inQuotes, final int i) {
        return inQuotes && nextLine.length() > i + 1 && (nextLine.charAt(i + 1) == this.quotechar || nextLine.charAt(i + 1) == this.escape);
    }
    
    protected boolean isAllWhiteSpace(final CharSequence sb) {
        final boolean result = true;
        for (int i = 0; i < sb.length(); ++i) {
            final char c = sb.charAt(i);
            if (!Character.isWhitespace(c)) {
                return false;
            }
        }
        return result;
    }
}
