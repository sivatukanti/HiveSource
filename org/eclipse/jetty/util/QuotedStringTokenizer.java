// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.util.Arrays;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class QuotedStringTokenizer extends StringTokenizer
{
    private static final String __delim = "\t\n\r";
    private String _string;
    private String _delim;
    private boolean _returnQuotes;
    private boolean _returnDelimiters;
    private StringBuffer _token;
    private boolean _hasToken;
    private int _i;
    private int _lastStart;
    private boolean _double;
    private boolean _single;
    private static final char[] escapes;
    
    public QuotedStringTokenizer(final String str, final String delim, final boolean returnDelimiters, final boolean returnQuotes) {
        super("");
        this._delim = "\t\n\r";
        this._returnQuotes = false;
        this._returnDelimiters = false;
        this._hasToken = false;
        this._i = 0;
        this._lastStart = 0;
        this._double = true;
        this._single = true;
        this._string = str;
        if (delim != null) {
            this._delim = delim;
        }
        this._returnDelimiters = returnDelimiters;
        this._returnQuotes = returnQuotes;
        if (this._delim.indexOf(39) >= 0 || this._delim.indexOf(34) >= 0) {
            throw new Error("Can't use quotes as delimiters: " + this._delim);
        }
        this._token = new StringBuffer((this._string.length() > 1024) ? 512 : (this._string.length() / 2));
    }
    
    public QuotedStringTokenizer(final String str, final String delim, final boolean returnDelimiters) {
        this(str, delim, returnDelimiters, false);
    }
    
    public QuotedStringTokenizer(final String str, final String delim) {
        this(str, delim, false, false);
    }
    
    public QuotedStringTokenizer(final String str) {
        this(str, null, false, false);
    }
    
    @Override
    public boolean hasMoreTokens() {
        if (this._hasToken) {
            return true;
        }
        this._lastStart = this._i;
        int state = 0;
        boolean escape = false;
        while (this._i < this._string.length()) {
            final char c = this._string.charAt(this._i++);
            switch (state) {
                case 0: {
                    if (this._delim.indexOf(c) >= 0) {
                        if (this._returnDelimiters) {
                            this._token.append(c);
                            return this._hasToken = true;
                        }
                        continue;
                    }
                    else {
                        if (c == '\'' && this._single) {
                            if (this._returnQuotes) {
                                this._token.append(c);
                            }
                            state = 2;
                            continue;
                        }
                        if (c == '\"' && this._double) {
                            if (this._returnQuotes) {
                                this._token.append(c);
                            }
                            state = 3;
                            continue;
                        }
                        this._token.append(c);
                        this._hasToken = true;
                        state = 1;
                        continue;
                    }
                    break;
                }
                case 1: {
                    this._hasToken = true;
                    if (this._delim.indexOf(c) >= 0) {
                        if (this._returnDelimiters) {
                            --this._i;
                        }
                        return this._hasToken;
                    }
                    if (c == '\'' && this._single) {
                        if (this._returnQuotes) {
                            this._token.append(c);
                        }
                        state = 2;
                        continue;
                    }
                    if (c == '\"' && this._double) {
                        if (this._returnQuotes) {
                            this._token.append(c);
                        }
                        state = 3;
                        continue;
                    }
                    this._token.append(c);
                    continue;
                }
                case 2: {
                    this._hasToken = true;
                    if (escape) {
                        escape = false;
                        this._token.append(c);
                        continue;
                    }
                    if (c == '\'') {
                        if (this._returnQuotes) {
                            this._token.append(c);
                        }
                        state = 1;
                        continue;
                    }
                    if (c == '\\') {
                        if (this._returnQuotes) {
                            this._token.append(c);
                        }
                        escape = true;
                        continue;
                    }
                    this._token.append(c);
                    continue;
                }
                case 3: {
                    this._hasToken = true;
                    if (escape) {
                        escape = false;
                        this._token.append(c);
                        continue;
                    }
                    if (c == '\"') {
                        if (this._returnQuotes) {
                            this._token.append(c);
                        }
                        state = 1;
                        continue;
                    }
                    if (c == '\\') {
                        if (this._returnQuotes) {
                            this._token.append(c);
                        }
                        escape = true;
                        continue;
                    }
                    this._token.append(c);
                    continue;
                }
            }
        }
        return this._hasToken;
    }
    
    @Override
    public String nextToken() throws NoSuchElementException {
        if (!this.hasMoreTokens() || this._token == null) {
            throw new NoSuchElementException();
        }
        final String t = this._token.toString();
        this._token.setLength(0);
        this._hasToken = false;
        return t;
    }
    
    @Override
    public String nextToken(final String delim) throws NoSuchElementException {
        this._delim = delim;
        this._i = this._lastStart;
        this._token.setLength(0);
        this._hasToken = false;
        return this.nextToken();
    }
    
    @Override
    public boolean hasMoreElements() {
        return this.hasMoreTokens();
    }
    
    @Override
    public Object nextElement() throws NoSuchElementException {
        return this.nextToken();
    }
    
    @Override
    public int countTokens() {
        return -1;
    }
    
    public static String quoteIfNeeded(final String s, final String delim) {
        if (s == null) {
            return null;
        }
        if (s.length() == 0) {
            return "\"\"";
        }
        for (int i = 0; i < s.length(); ++i) {
            final char c = s.charAt(i);
            if (c == '\\' || c == '\"' || c == '\'' || Character.isWhitespace(c) || delim.indexOf(c) >= 0) {
                final StringBuffer b = new StringBuffer(s.length() + 8);
                quote(b, s);
                return b.toString();
            }
        }
        return s;
    }
    
    public static String quote(final String s) {
        if (s == null) {
            return null;
        }
        if (s.length() == 0) {
            return "\"\"";
        }
        final StringBuffer b = new StringBuffer(s.length() + 8);
        quote(b, s);
        return b.toString();
    }
    
    public static void quoteOnly(final Appendable buffer, final String input) {
        if (input == null) {
            return;
        }
        try {
            buffer.append('\"');
            for (int i = 0; i < input.length(); ++i) {
                final char c = input.charAt(i);
                if (c == '\"' || c == '\\') {
                    buffer.append('\\');
                }
                buffer.append(c);
            }
            buffer.append('\"');
        }
        catch (IOException x) {
            throw new RuntimeException(x);
        }
    }
    
    public static void quote(final Appendable buffer, final String input) {
        if (input == null) {
            return;
        }
        try {
            buffer.append('\"');
            for (int i = 0; i < input.length(); ++i) {
                final char c = input.charAt(i);
                if (c >= ' ') {
                    if (c == '\"' || c == '\\') {
                        buffer.append('\\');
                    }
                    buffer.append(c);
                }
                else {
                    final char escape = QuotedStringTokenizer.escapes[c];
                    if (escape == '\uffff') {
                        buffer.append('\\').append('u').append('0').append('0');
                        if (c < '\u0010') {
                            buffer.append('0');
                        }
                        buffer.append(Integer.toString(c, 16));
                    }
                    else {
                        buffer.append('\\').append(escape);
                    }
                }
            }
            buffer.append('\"');
        }
        catch (IOException x) {
            throw new RuntimeException(x);
        }
    }
    
    public static String unquoteOnly(final String s) {
        return unquoteOnly(s, false);
    }
    
    public static String unquoteOnly(final String s, final boolean lenient) {
        if (s == null) {
            return null;
        }
        if (s.length() < 2) {
            return s;
        }
        final char first = s.charAt(0);
        final char last = s.charAt(s.length() - 1);
        if (first != last || (first != '\"' && first != '\'')) {
            return s;
        }
        final StringBuilder b = new StringBuilder(s.length() - 2);
        boolean escape = false;
        for (int i = 1; i < s.length() - 1; ++i) {
            final char c = s.charAt(i);
            if (escape) {
                escape = false;
                if (lenient && !isValidEscaping(c)) {
                    b.append('\\');
                }
                b.append(c);
            }
            else if (c == '\\') {
                escape = true;
            }
            else {
                b.append(c);
            }
        }
        return b.toString();
    }
    
    public static String unquote(final String s) {
        return unquote(s, false);
    }
    
    public static String unquote(final String s, final boolean lenient) {
        if (s == null) {
            return null;
        }
        if (s.length() < 2) {
            return s;
        }
        final char first = s.charAt(0);
        final char last = s.charAt(s.length() - 1);
        if (first != last || (first != '\"' && first != '\'')) {
            return s;
        }
        final StringBuilder b = new StringBuilder(s.length() - 2);
        boolean escape = false;
        for (int i = 1; i < s.length() - 1; ++i) {
            final char c = s.charAt(i);
            if (escape) {
                escape = false;
                switch (c) {
                    case 'n': {
                        b.append('\n');
                        break;
                    }
                    case 'r': {
                        b.append('\r');
                        break;
                    }
                    case 't': {
                        b.append('\t');
                        break;
                    }
                    case 'f': {
                        b.append('\f');
                        break;
                    }
                    case 'b': {
                        b.append('\b');
                        break;
                    }
                    case '\\': {
                        b.append('\\');
                        break;
                    }
                    case '/': {
                        b.append('/');
                        break;
                    }
                    case '\"': {
                        b.append('\"');
                        break;
                    }
                    case 'u': {
                        b.append((char)((TypeUtil.convertHexDigit((byte)s.charAt(i++)) << 24) + (TypeUtil.convertHexDigit((byte)s.charAt(i++)) << 16) + (TypeUtil.convertHexDigit((byte)s.charAt(i++)) << 8) + TypeUtil.convertHexDigit((byte)s.charAt(i++))));
                        break;
                    }
                    default: {
                        if (lenient && !isValidEscaping(c)) {
                            b.append('\\');
                        }
                        b.append(c);
                        break;
                    }
                }
            }
            else if (c == '\\') {
                escape = true;
            }
            else {
                b.append(c);
            }
        }
        return b.toString();
    }
    
    private static boolean isValidEscaping(final char c) {
        return c == 'n' || c == 'r' || c == 't' || c == 'f' || c == 'b' || c == '\\' || c == '/' || c == '\"' || c == 'u';
    }
    
    public static boolean isQuoted(final String s) {
        return s != null && s.length() > 0 && s.charAt(0) == '\"' && s.charAt(s.length() - 1) == '\"';
    }
    
    public boolean getDouble() {
        return this._double;
    }
    
    public void setDouble(final boolean d) {
        this._double = d;
    }
    
    public boolean getSingle() {
        return this._single;
    }
    
    public void setSingle(final boolean single) {
        this._single = single;
    }
    
    static {
        Arrays.fill(escapes = new char[32], '\uffff');
        QuotedStringTokenizer.escapes[8] = 'b';
        QuotedStringTokenizer.escapes[9] = 't';
        QuotedStringTokenizer.escapes[10] = 'n';
        QuotedStringTokenizer.escapes[12] = 'f';
        QuotedStringTokenizer.escapes[13] = 'r';
    }
}
