// 
// Decompiled by Procyon v0.5.36
// 

package org.json;

import java.io.IOException;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.Reader;

public class JSONTokener
{
    private int index;
    private Reader reader;
    private char lastChar;
    private boolean useLastChar;
    
    public JSONTokener(final Reader in) {
        this.reader = (in.markSupported() ? in : new BufferedReader(in));
        this.useLastChar = false;
        this.index = 0;
    }
    
    public JSONTokener(final String s) {
        this(new StringReader(s));
    }
    
    public void back() throws JSONException {
        if (this.useLastChar || this.index <= 0) {
            throw new JSONException("Stepping back two steps is not supported");
        }
        --this.index;
        this.useLastChar = true;
    }
    
    public static int dehexchar(final char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'A' && c <= 'F') {
            return c - '7';
        }
        if (c >= 'a' && c <= 'f') {
            return c - 'W';
        }
        return -1;
    }
    
    public boolean more() throws JSONException {
        if (this.next() == '\0') {
            return false;
        }
        this.back();
        return true;
    }
    
    public char next() throws JSONException {
        if (this.useLastChar) {
            this.useLastChar = false;
            if (this.lastChar != '\0') {
                ++this.index;
            }
            return this.lastChar;
        }
        int read;
        try {
            read = this.reader.read();
        }
        catch (IOException ex) {
            throw new JSONException(ex);
        }
        if (read <= 0) {
            return this.lastChar = '\0';
        }
        ++this.index;
        return this.lastChar = (char)read;
    }
    
    public char next(final char c) throws JSONException {
        final char next = this.next();
        if (next != c) {
            throw this.syntaxError("Expected '" + c + "' and instead saw '" + next + "'");
        }
        return next;
    }
    
    public String next(final int n) throws JSONException {
        if (n == 0) {
            return "";
        }
        final char[] value = new char[n];
        int n2 = 0;
        if (this.useLastChar) {
            this.useLastChar = false;
            value[0] = this.lastChar;
            n2 = 1;
        }
        try {
            int read;
            while (n2 < n && (read = this.reader.read(value, n2, n - n2)) != -1) {
                n2 += read;
            }
        }
        catch (IOException ex) {
            throw new JSONException(ex);
        }
        this.index += n2;
        if (n2 < n) {
            throw this.syntaxError("Substring bounds error");
        }
        this.lastChar = value[n - 1];
        return new String(value);
    }
    
    public char nextClean() throws JSONException {
        char next;
        do {
            next = this.next();
        } while (next != '\0' && next <= ' ');
        return next;
    }
    
    public String nextString(final char c) throws JSONException {
        final StringBuffer sb = new StringBuffer();
        while (true) {
            final char next = this.next();
            switch (next) {
                case 0:
                case 10:
                case 13: {
                    throw this.syntaxError("Unterminated string");
                }
                case 92: {
                    final char next2 = this.next();
                    switch (next2) {
                        case 98: {
                            sb.append('\b');
                            continue;
                        }
                        case 116: {
                            sb.append('\t');
                            continue;
                        }
                        case 110: {
                            sb.append('\n');
                            continue;
                        }
                        case 102: {
                            sb.append('\f');
                            continue;
                        }
                        case 114: {
                            sb.append('\r');
                            continue;
                        }
                        case 117: {
                            sb.append((char)Integer.parseInt(this.next(4), 16));
                            continue;
                        }
                        case 120: {
                            sb.append((char)Integer.parseInt(this.next(2), 16));
                            continue;
                        }
                        default: {
                            sb.append(next2);
                            continue;
                        }
                    }
                    break;
                }
                default: {
                    if (next == c) {
                        return sb.toString();
                    }
                    sb.append(next);
                    continue;
                }
            }
        }
    }
    
    public String nextTo(final char c) throws JSONException {
        final StringBuffer sb = new StringBuffer();
        char next;
        while (true) {
            next = this.next();
            if (next == c || next == '\0' || next == '\n' || next == '\r') {
                break;
            }
            sb.append(next);
        }
        if (next != '\0') {
            this.back();
        }
        return sb.toString().trim();
    }
    
    public String nextTo(final String s) throws JSONException {
        final StringBuffer sb = new StringBuffer();
        char next;
        while (true) {
            next = this.next();
            if (s.indexOf(next) >= 0 || next == '\0' || next == '\n' || next == '\r') {
                break;
            }
            sb.append(next);
        }
        if (next != '\0') {
            this.back();
        }
        return sb.toString().trim();
    }
    
    public Object nextValue() throws JSONException {
        char c = this.nextClean();
        switch (c) {
            case 34:
            case 39: {
                return this.nextString(c);
            }
            case 123: {
                this.back();
                return new JSONObject(this);
            }
            case 40:
            case 91: {
                this.back();
                return new JSONArray(this);
            }
            default: {
                final StringBuffer sb = new StringBuffer();
                while (c >= ' ' && ",:]}/\\\"[{;=#".indexOf(c) < 0) {
                    sb.append(c);
                    c = this.next();
                }
                this.back();
                final String trim = sb.toString().trim();
                if (trim.equals("")) {
                    throw this.syntaxError("Missing value");
                }
                return JSONObject.stringToValue(trim);
            }
        }
    }
    
    public char skipTo(final char c) throws JSONException {
        char next;
        try {
            final int index = this.index;
            this.reader.mark(Integer.MAX_VALUE);
            do {
                next = this.next();
                if (next == '\0') {
                    this.reader.reset();
                    this.index = index;
                    return next;
                }
            } while (next != c);
        }
        catch (IOException ex) {
            throw new JSONException(ex);
        }
        this.back();
        return next;
    }
    
    public JSONException syntaxError(final String str) {
        return new JSONException(str + this.toString());
    }
    
    @Override
    public String toString() {
        return " at character " + this.index;
    }
}
