// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.datastore;

import java.io.Writer;
import java.io.Reader;
import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.sql.SQLException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.Clob;

public class ClobImpl implements Clob
{
    private String string;
    private long length;
    private StringReader reader;
    private InputStream inputStream;
    boolean freed;
    
    public ClobImpl(final String string) throws IOException {
        this.freed = false;
        if (string == null) {
            throw new IllegalArgumentException("String cannot be null");
        }
        this.string = string;
        this.length = string.length();
    }
    
    @Override
    public long length() throws SQLException {
        if (this.freed) {
            throw new SQLException("free() has been called");
        }
        return this.length;
    }
    
    @Override
    public void truncate(final long len) throws SQLException {
        if (this.freed) {
            throw new SQLException("free() has been called");
        }
        throw new UnsupportedOperationException();
    }
    
    @Override
    public InputStream getAsciiStream() throws SQLException {
        if (this.freed) {
            throw new SQLException("free() has been called");
        }
        if (this.inputStream == null) {
            this.inputStream = new ByteArrayInputStream(this.string.getBytes());
        }
        return this.inputStream;
    }
    
    @Override
    public OutputStream setAsciiStream(final long pos) throws SQLException {
        if (this.freed) {
            throw new SQLException("free() has been called");
        }
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Reader getCharacterStream() throws SQLException {
        if (this.freed) {
            throw new SQLException("free() has been called");
        }
        if (this.reader == null) {
            this.reader = new StringReader(this.string);
        }
        return this.reader;
    }
    
    @Override
    public Writer setCharacterStream(final long pos) throws SQLException {
        if (this.freed) {
            throw new SQLException("free() has been called");
        }
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void free() throws SQLException {
        if (this.freed) {
            return;
        }
        this.string = null;
        if (this.reader != null) {
            this.reader.close();
        }
        if (this.inputStream != null) {
            try {
                this.inputStream.close();
            }
            catch (IOException ex) {}
        }
        this.freed = true;
    }
    
    @Override
    public Reader getCharacterStream(final long pos, final long length) throws SQLException {
        if (this.freed) {
            throw new SQLException("free() has been called");
        }
        if (this.reader == null) {
            this.reader = new StringReader(this.string);
        }
        return this.reader;
    }
    
    @Override
    public String getSubString(final long pos, final int length) throws SQLException {
        if (this.freed) {
            throw new SQLException("free() has been called");
        }
        if (pos > 2147483647L) {
            throw new IllegalArgumentException("Initial position cannot be larger than 2147483647");
        }
        if (pos + length - 1L > this.length()) {
            throw new IndexOutOfBoundsException("The requested substring is greater than the actual length of the Clob String.");
        }
        return this.string.substring((int)pos - 1, (int)pos + length - 1);
    }
    
    @Override
    public int setString(final long pos, final String str) throws SQLException {
        if (this.freed) {
            throw new SQLException("free() has been called");
        }
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int setString(final long pos, final String str, final int offset, final int len) throws SQLException {
        if (this.freed) {
            throw new SQLException("free() has been called");
        }
        throw new UnsupportedOperationException();
    }
    
    @Override
    public long position(final String searchstr, final long start) throws SQLException {
        if (this.freed) {
            throw new SQLException("free() has been called");
        }
        throw new UnsupportedOperationException();
    }
    
    @Override
    public long position(final Clob searchstr, final long start) throws SQLException {
        if (this.freed) {
            throw new SQLException("free() has been called");
        }
        throw new UnsupportedOperationException();
    }
}
