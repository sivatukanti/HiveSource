// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import java.io.Writer;
import java.io.OutputStream;
import java.io.CharArrayReader;
import java.io.InputStream;
import java.io.Reader;
import java.io.IOException;
import java.sql.SQLException;
import java.io.Serializable;
import java.sql.Clob;

public class HarmonySerialClob implements Clob, Serializable, Cloneable
{
    private static final long serialVersionUID = -1662519690087375313L;
    private char[] buf;
    private Clob clob;
    private long len;
    private long origLen;
    
    public HarmonySerialClob(final String s) {
        this(s.toCharArray());
    }
    
    public HarmonySerialClob(final char[] array) {
        this.buf = new char[array.length];
        this.origLen = array.length;
        this.len = this.origLen;
        System.arraycopy(array, 0, this.buf, 0, (int)this.len);
    }
    
    public HarmonySerialClob(final Clob clob) throws SQLException {
        if (clob == null) {
            throw new IllegalArgumentException();
        }
        final Reader characterStream;
        if ((characterStream = clob.getCharacterStream()) == null && clob.getAsciiStream() == null) {
            throw new IllegalArgumentException();
        }
        this.clob = clob;
        this.origLen = clob.length();
        this.len = this.origLen;
        this.buf = new char[(int)this.len];
        try {
            characterStream.read(this.buf);
        }
        catch (IOException cause) {
            final SQLException ex = new SQLException("SerialClob: " + cause.getMessage());
            ex.initCause(cause);
            throw ex;
        }
    }
    
    public long length() throws SQLException {
        this.checkValidation();
        return this.len;
    }
    
    public InputStream getAsciiStream() throws SQLException {
        this.checkValidation();
        if (this.clob == null) {
            throw new IllegalStateException();
        }
        return this.clob.getAsciiStream();
    }
    
    public Reader getCharacterStream() throws SQLException {
        this.checkValidation();
        return new CharArrayReader(this.buf);
    }
    
    public String getSubString(final long value, final int n) throws SQLException {
        this.checkValidation();
        if (n < 0) {
            throw HarmonySerialBlob.makeSQLException("XJ071.S", new Object[] { new Integer(n) });
        }
        if (value < 1L || value > this.len || value + n > this.len + 1L) {
            throw HarmonySerialBlob.makeSQLException("XJ070.S", new Object[] { new Long(value) });
        }
        try {
            return new String(this.buf, (int)(value - 1L), n);
        }
        catch (StringIndexOutOfBoundsException ex) {
            throw new SQLException();
        }
    }
    
    public long position(final Clob clob, final long n) throws SQLException {
        this.checkValidation();
        return this.position(clob.getSubString(1L, (int)clob.length()), n);
    }
    
    public long position(final String s, final long n) throws SQLException {
        this.checkValidation();
        if (n < 1L || this.len - (n - 1L) < s.length()) {
            return -1L;
        }
        final char[] charArray = s.toCharArray();
        for (int n2 = (int)n - 1; n2 < this.len; ++n2) {
            if (this.match(this.buf, n2, charArray)) {
                return n2 + 1;
            }
        }
        return -1L;
    }
    
    private boolean match(final char[] array, int n, final char[] array2) {
        int i = 0;
        while (i < array2.length) {
            if (array[n++] != array2[i++]) {
                return false;
            }
        }
        return true;
    }
    
    public OutputStream setAsciiStream(final long asciiStream) throws SQLException {
        this.checkValidation();
        if (this.clob == null) {
            throw new IllegalStateException();
        }
        final OutputStream setAsciiStream = this.clob.setAsciiStream(asciiStream);
        if (setAsciiStream == null) {
            throw new IllegalStateException();
        }
        return setAsciiStream;
    }
    
    public Writer setCharacterStream(final long characterStream) throws SQLException {
        this.checkValidation();
        if (this.clob == null) {
            throw new IllegalStateException();
        }
        final Writer setCharacterStream = this.clob.setCharacterStream(characterStream);
        if (setCharacterStream == null) {
            throw new IllegalStateException();
        }
        return setCharacterStream;
    }
    
    public int setString(final long n, final String s) throws SQLException {
        this.checkValidation();
        return this.setString(n, s, 0, s.length());
    }
    
    public int setString(final long value, final String s, final int srcBegin, final int n) throws SQLException {
        this.checkValidation();
        if (value < 1L) {
            throw HarmonySerialBlob.makeSQLException("XJ070.S", new Object[] { new Long(value) });
        }
        if (n < 0) {
            throw HarmonySerialBlob.makeSQLException("XJ071.S", null);
        }
        if (value > this.len - n + 1L) {
            throw HarmonySerialBlob.makeSQLException("XJ076.S", null);
        }
        if (srcBegin < 0 || srcBegin > s.length() - n) {
            throw HarmonySerialBlob.makeSQLException("XJ078.S", null);
        }
        if (n > this.len + srcBegin) {
            throw HarmonySerialBlob.makeSQLException("XJ078.S", null);
        }
        s.getChars(srcBegin, srcBegin + n, this.buf, (int)value - 1);
        return n;
    }
    
    public void truncate(final long len) throws SQLException {
        this.checkValidation();
        if (len < 0L) {
            throw HarmonySerialBlob.makeSQLException("XJ071.S", new Object[] { new Long(len) });
        }
        if (len > this.len) {
            throw HarmonySerialBlob.makeSQLException("XJ079.S", new Object[] { new Long(len) });
        }
        final char[] buf = new char[(int)len];
        System.arraycopy(this.buf, 0, buf, 0, (int)len);
        this.buf = buf;
        this.len = len;
    }
    
    public void free() throws SQLException {
        if (this.len != -1L) {
            this.len = -1L;
            this.clob = null;
            this.buf = null;
        }
    }
    
    public Reader getCharacterStream(final long n, final long n2) throws SQLException {
        this.checkValidation();
        return new CharArrayReader(this.buf, (int)n, (int)n2);
    }
    
    private void checkValidation() throws SQLException {
        if (this.len == -1L) {
            throw HarmonySerialBlob.makeSQLException("XJ215.S", null);
        }
    }
}
