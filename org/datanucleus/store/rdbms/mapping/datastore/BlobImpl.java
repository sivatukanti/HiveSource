// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.datastore;

import java.io.StreamCorruptedException;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.util.NucleusLogger;
import java.io.ObjectInputStream;
import java.sql.SQLException;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Blob;

public class BlobImpl implements Blob
{
    private InputStream stream;
    private int length;
    private byte[] bytes;
    boolean freed;
    
    public BlobImpl(final Object obj) throws IOException {
        this.freed = false;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        this.bytes = baos.toByteArray();
        this.stream = new ByteArrayInputStream(this.bytes);
        this.length = this.bytes.length;
    }
    
    public BlobImpl(final byte[] bytes) {
        this.freed = false;
        this.bytes = bytes;
        this.stream = new ByteArrayInputStream(bytes);
        this.length = bytes.length;
    }
    
    public BlobImpl(final InputStream stream) {
        this.freed = false;
        this.stream = stream;
    }
    
    public Object getObject() throws SQLException {
        if (this.freed) {
            throw new SQLException("free() has been called");
        }
        final ByteArrayInputStream bais = new ByteArrayInputStream(this.bytes);
        try {
            final ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        }
        catch (StreamCorruptedException e) {
            final String msg = "StreamCorruptedException: object is corrupted";
            NucleusLogger.DATASTORE.error(msg);
            throw new NucleusUserException(msg, e).setFatal();
        }
        catch (IOException e2) {
            final String msg = "IOException: error when reading object";
            NucleusLogger.DATASTORE.error(msg);
            throw new NucleusUserException(msg, e2).setFatal();
        }
        catch (ClassNotFoundException e3) {
            final String msg = "ClassNotFoundException: error when creating object";
            NucleusLogger.DATASTORE.error(msg);
            throw new NucleusUserException(msg, e3).setFatal();
        }
    }
    
    @Override
    public long length() throws SQLException {
        if (this.freed) {
            throw new SQLException("free() has been called");
        }
        return this.length;
    }
    
    @Override
    public byte[] getBytes(final long pos, final int length) throws SQLException {
        if (this.freed) {
            throw new SQLException("free() has been called");
        }
        final byte[] bytesToReturn = new byte[length];
        for (int i = 0; i < length; ++i) {
            bytesToReturn[i] = this.bytes[(int)pos + i];
        }
        return bytesToReturn;
    }
    
    @Override
    public int setBytes(final long value, final byte[] bytes, final int pos, final int length) throws SQLException {
        if (this.freed) {
            throw new SQLException("free() has been called");
        }
        return -1;
    }
    
    @Override
    public void truncate(final long value) throws SQLException {
        if (this.freed) {
            throw new SQLException("free() has been called");
        }
    }
    
    @Override
    public int setBytes(final long value, final byte[] bytes) throws SQLException {
        if (this.freed) {
            throw new SQLException("free() has been called");
        }
        return -1;
    }
    
    @Override
    public InputStream getBinaryStream() throws SQLException {
        if (this.freed) {
            throw new SQLException("free() has been called");
        }
        return this.stream;
    }
    
    @Override
    public InputStream getBinaryStream(final long pos, final long length) throws SQLException {
        if (this.freed) {
            throw new SQLException("free() has been called");
        }
        return this.stream;
    }
    
    @Override
    public OutputStream setBinaryStream(final long value) throws SQLException {
        if (this.freed) {
            throw new SQLException("free() has been called");
        }
        return null;
    }
    
    @Override
    public void free() throws SQLException {
        if (this.freed) {
            return;
        }
        this.bytes = null;
        if (this.stream != null) {
            try {
                this.stream.close();
            }
            catch (IOException ex) {}
        }
        this.freed = true;
    }
    
    @Override
    public long position(final byte[] pattern, final long start) throws SQLException {
        if (this.freed) {
            throw new SQLException("free() has been called");
        }
        throw new UnsupportedOperationException("[BlobImpl.position] may not be called");
    }
    
    @Override
    public long position(final Blob pattern, final long start) throws SQLException {
        if (this.freed) {
            throw new SQLException("free() has been called");
        }
        throw new UnsupportedOperationException("[BlobImpl.position] may not be called");
    }
}
