// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import org.apache.derby.iapi.error.StandardException;
import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.io.Serializable;
import java.sql.Blob;

public class HarmonySerialBlob implements Blob, Serializable, Cloneable
{
    private static final long serialVersionUID = -8144641928112860441L;
    private byte[] buf;
    private Blob blob;
    private long len;
    private long origLen;
    
    public HarmonySerialBlob(final Blob blob) throws SQLException {
        if (blob == null) {
            throw new IllegalArgumentException();
        }
        this.blob = blob;
        this.buf = blob.getBytes(1L, (int)blob.length());
        this.len = this.buf.length;
        this.origLen = this.len;
    }
    
    public HarmonySerialBlob(final byte[] array) {
        this.buf = new byte[array.length];
        this.len = array.length;
        this.origLen = this.len;
        System.arraycopy(array, 0, this.buf, 0, (int)this.len);
    }
    
    public InputStream getBinaryStream() throws SQLException {
        return new ByteArrayInputStream(this.buf);
    }
    
    public byte[] getBytes(final long value, int value2) throws SQLException {
        if (value < 1L || value > this.len) {
            throw makeSQLException("XJ070.S", new Object[] { new Long(value) });
        }
        if (value2 < 0) {
            throw makeSQLException("XJ071.S", new Object[] { new Integer(value2) });
        }
        if (value2 > this.len - value + 1L) {
            value2 = (int)(this.len - value + 1L);
        }
        final byte[] array = new byte[value2];
        System.arraycopy(this.buf, (int)value - 1, array, 0, value2);
        return array;
    }
    
    public long length() throws SQLException {
        return this.len;
    }
    
    public long position(final Blob blob, final long n) throws SQLException {
        return this.position(blob.getBytes(1L, (int)blob.length()), n);
    }
    
    public long position(final byte[] array, final long n) throws SQLException {
        if (n < 1L || this.len - (n - 1L) < array.length) {
            return -1L;
        }
        for (int n2 = (int)(n - 1L); n2 <= this.len - array.length; ++n2) {
            if (this.match(this.buf, n2, array)) {
                return n2 + 1;
            }
        }
        return -1L;
    }
    
    private boolean match(final byte[] array, int n, final byte[] array2) {
        int i = 0;
        while (i < array2.length) {
            if (array[n++] != array2[i++]) {
                return false;
            }
        }
        return true;
    }
    
    public OutputStream setBinaryStream(final long binaryStream) throws SQLException {
        if (this.blob == null) {
            throw new IllegalStateException();
        }
        final OutputStream setBinaryStream = this.blob.setBinaryStream(binaryStream);
        if (setBinaryStream == null) {
            throw new IllegalStateException();
        }
        return setBinaryStream;
    }
    
    public int setBytes(final long n, final byte[] array) throws SQLException {
        return this.setBytes(n, array, 0, array.length);
    }
    
    public int setBytes(final long value, final byte[] array, final int value2, final int n) throws SQLException {
        if (value < 1L || n < 0 || value > this.len - n + 1L) {
            throw makeSQLException("XJ070.S", new Object[] { new Long(value) });
        }
        if (value2 < 0 || n < 0 || value2 > array.length - n) {
            throw makeSQLException("XJ078.S", new Object[] { new Integer(value2) });
        }
        System.arraycopy(array, value2, this.buf, (int)value - 1, n);
        return n;
    }
    
    public void truncate(final long len) throws SQLException {
        if (len > this.len) {
            throw makeSQLException("XJ079.S", new Object[] { new Long(this.len) });
        }
        this.buf = this.getBytes(1L, (int)len);
        this.len = len;
    }
    
    public void free() throws SQLException {
        throw new UnsupportedOperationException("Not supported");
    }
    
    public InputStream getBinaryStream(final long value, final long n) throws SQLException {
        if (this.len < 0L) {
            throw makeSQLException("XJ071.S", new Object[] { new Long(this.len) });
        }
        if (n < 0L) {
            throw makeSQLException("XJ071.S", new Object[] { new Long(n) });
        }
        if (value < 1L || value + n > this.len) {
            throw makeSQLException("XJ087.S", new Object[] { new Long(value), new Long(n) });
        }
        return new ByteArrayInputStream(this.buf, (int)(value - 1L), (int)n);
    }
    
    public static SQLException makeSQLException(final String s, final Object[] array) {
        final StandardException exception = StandardException.newException(s, array);
        return new SQLException(exception.getMessage(), exception.getSQLState());
    }
}
