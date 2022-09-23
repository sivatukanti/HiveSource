// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import java.io.UTFDataFormatException;
import org.apache.derby.iapi.services.i18n.MessageService;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.EOFException;
import org.apache.derby.iapi.error.ExceptionUtil;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import org.apache.derby.io.StorageFile;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.store.raw.data.DataFactory;
import java.security.PrivilegedExceptionAction;
import org.apache.derby.iapi.error.StandardException;
import java.io.IOException;

class LOBStreamControl
{
    private LOBFile tmpFile;
    private byte[] dataBytes;
    private boolean isBytes;
    private final int bufferSize;
    private final EmbedConnection conn;
    private long updateCount;
    private static final int DEFAULT_BUF_SIZE = 4096;
    private static final int MAX_BUF_SIZE = 32768;
    
    LOBStreamControl(final EmbedConnection conn) {
        this.dataBytes = new byte[0];
        this.isBytes = true;
        this.conn = conn;
        this.updateCount = 0L;
        this.bufferSize = 4096;
    }
    
    LOBStreamControl(final EmbedConnection conn, final byte[] array) throws IOException, StandardException {
        this.dataBytes = new byte[0];
        this.isBytes = true;
        this.conn = conn;
        this.updateCount = 0L;
        this.bufferSize = Math.min(Math.max(4096, array.length), 32768);
        this.write(array, 0, array.length, 0L);
    }
    
    private void init(final byte[] array, final long n) throws IOException, StandardException {
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction() {
                public Object run() throws IOException, StandardException {
                    final DataFactory dataFactory = (DataFactory)Monitor.findServiceModule(Monitor.findService("org.apache.derby.database.Database", LOBStreamControl.this.conn.getDBName()), "org.apache.derby.iapi.store.raw.data.DataFactory");
                    final StorageFile temporaryFile = dataFactory.getStorageFactory().createTemporaryFile("lob", null);
                    if (dataFactory.databaseEncrypted()) {
                        LOBStreamControl.this.tmpFile = new EncryptedLOBFile(temporaryFile, dataFactory);
                    }
                    else {
                        LOBStreamControl.this.tmpFile = new LOBFile(temporaryFile);
                    }
                    LOBStreamControl.this.conn.addLobFile(LOBStreamControl.this.tmpFile);
                    return null;
                }
            });
        }
        catch (PrivilegedActionException ex) {
            final Exception exception = ex.getException();
            if (exception instanceof StandardException) {
                throw (StandardException)exception;
            }
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw Util.newIOException(exception);
        }
        this.isBytes = false;
        if (n != 0L) {
            this.write(array, 0, (int)n, 0L);
        }
        this.dataBytes = null;
    }
    
    private long updateData(final byte[] array, final int n, final int n2, final long n3) throws StandardException {
        if (this.dataBytes == null) {
            if ((int)n3 == 0) {
                System.arraycopy(array, n, this.dataBytes = new byte[n2], (int)n3, n2);
                return n2;
            }
            throw StandardException.newException("XJ076.S", new Long(n3));
        }
        else {
            if (n3 > this.dataBytes.length) {
                throw StandardException.newException("XJ076.S", new Long(n3));
            }
            if (n3 + n2 < this.dataBytes.length) {
                System.arraycopy(array, n, this.dataBytes, (int)n3, n2);
            }
            else {
                final byte[] dataBytes = new byte[n2 + (int)n3];
                System.arraycopy(this.dataBytes, 0, dataBytes, 0, (int)n3);
                System.arraycopy(array, n, dataBytes, (int)n3, n2);
                this.dataBytes = dataBytes;
            }
            return n3 + n2;
        }
    }
    
    private void isValidPostion(final long n) throws IOException, StandardException {
        if (n < 0L) {
            throw StandardException.newException("XJ071.S", new Long(n + 1L));
        }
        if (n > 2147483647L) {
            throw StandardException.newException("XJ076.S", new Long(n + 1L));
        }
        if (this.isBytes) {
            if (this.dataBytes == null) {
                if (n != 0L) {
                    throw StandardException.newException("XJ076.S", new Long(n + 1L));
                }
            }
            else if (this.dataBytes.length < n) {
                throw StandardException.newException("XJ076.S", new Long(n + 1L));
            }
        }
        else if (n > this.tmpFile.length()) {
            throw StandardException.newException("XJ076.S", new Long(n + 1L));
        }
    }
    
    private void isValidOffset(final int value, final int n) throws StandardException {
        if (value < 0 || value > n) {
            throw StandardException.newException("XJ078.S", new Integer(value));
        }
    }
    
    synchronized long write(final int n, final long n2) throws IOException, StandardException {
        this.isValidPostion(n2);
        ++this.updateCount;
        if (this.isBytes) {
            if (n2 < this.bufferSize) {
                this.updateData(new byte[] { (byte)n }, 0, 1, n2);
                return n2 + 1L;
            }
            this.init(this.dataBytes, n2);
        }
        this.tmpFile.seek(n2);
        this.tmpFile.write(n);
        return this.tmpFile.getFilePointer();
    }
    
    synchronized long write(final byte[] array, final int n, final int n2, final long n3) throws IOException, StandardException {
        this.isValidPostion(n3);
        try {
            this.isValidOffset(n, array.length);
        }
        catch (StandardException ex) {
            if (ex.getSQLState().equals(ExceptionUtil.getSQLStateFromIdentifier("XJ078.S"))) {
                throw new ArrayIndexOutOfBoundsException(ex.getMessage());
            }
            throw ex;
        }
        ++this.updateCount;
        if (this.isBytes) {
            if (n3 + n2 <= this.bufferSize) {
                return this.updateData(array, n, n2, n3);
            }
            this.init(this.dataBytes, n3);
        }
        this.tmpFile.seek(n3);
        this.tmpFile.write(array, n, n2);
        return this.tmpFile.getFilePointer();
    }
    
    synchronized int read(final long n) throws IOException, StandardException {
        this.isValidPostion(n);
        if (this.isBytes) {
            if (this.dataBytes.length == n) {
                return -1;
            }
            return this.dataBytes[(int)n] & 0xFF;
        }
        else {
            if (this.tmpFile.getFilePointer() != n) {
                this.tmpFile.seek(n);
            }
            try {
                return this.tmpFile.readByte() & 0xFF;
            }
            catch (EOFException ex) {
                return -1;
            }
        }
    }
    
    private int readBytes(final byte[] array, final int n, final int n2, final long n3) {
        if (n3 >= this.dataBytes.length) {
            return -1;
        }
        final int n4 = this.dataBytes.length - (int)n3;
        final int n5 = (n2 > n4) ? n4 : n2;
        System.arraycopy(this.dataBytes, (int)n3, array, n, n5);
        return n5;
    }
    
    synchronized int read(final byte[] array, final int n, final int n2, final long n3) throws IOException, StandardException {
        this.isValidPostion(n3);
        this.isValidOffset(n, array.length);
        if (this.isBytes) {
            return this.readBytes(array, n, n2, n3);
        }
        this.tmpFile.seek(n3);
        return this.tmpFile.read(array, n, n2);
    }
    
    InputStream getInputStream(final long n) {
        return new LOBInputStream(this, n);
    }
    
    OutputStream getOutputStream(final long n) {
        return new LOBOutputStream(this, n);
    }
    
    long getLength() throws IOException {
        if (this.isBytes) {
            return this.dataBytes.length;
        }
        return this.tmpFile.length();
    }
    
    synchronized void truncate(final long length) throws IOException, StandardException {
        this.isValidPostion(length);
        if (this.isBytes) {
            final byte[] dataBytes = new byte[(int)length];
            System.arraycopy(this.dataBytes, 0, dataBytes, 0, (int)length);
            this.dataBytes = dataBytes;
        }
        else if (length < this.bufferSize) {
            this.read(this.dataBytes = new byte[(int)length], 0, this.dataBytes.length, 0L);
            this.isBytes = true;
            this.releaseTempFile(this.tmpFile);
            this.tmpFile = null;
        }
        else {
            this.tmpFile.setLength(length);
        }
    }
    
    synchronized void copyData(final InputStream inputStream, final long value) throws IOException, StandardException {
        final byte[] b = new byte[this.bufferSize];
        long value2 = 0L;
        while (value2 < value) {
            final int read = inputStream.read(b, 0, (int)Math.min(value - value2, this.bufferSize));
            if (read == -1) {
                if (value != Long.MAX_VALUE) {
                    throw new EOFException(MessageService.getTextMessage("I029", new Long(value), new Long(value2)));
                }
                break;
            }
            else {
                this.write(b, 0, read, value2);
                value2 += read;
            }
        }
        final long length = this.getLength();
        if (value == Long.MAX_VALUE && length > 2L) {
            final byte[] array = new byte[3];
            this.read(array, 0, 3, length - 3L);
            if ((array[0] & 0xFF) == 0xE0 && (array[1] & 0xFF) == 0x0 && (array[2] & 0xFF) == 0x0) {
                this.truncate(length - 3L);
            }
        }
    }
    
    synchronized long copyUtf8Data(final InputStream inputStream, final long value) throws IOException, StandardException {
        long n = 0L;
        int i = 0;
        int n2 = 0;
        final byte[] b = new byte[this.bufferSize];
        while (n < value) {
            final int read = inputStream.read(b, 0, (int)Math.min(b.length, value - n));
            if (read == -1) {
                break;
            }
            while (i < read) {
                final int j = b[i] & 0xFF;
                if ((j & 0x80) == 0x0) {
                    ++i;
                }
                else if ((j & 0x60) == 0x40) {
                    i += 2;
                }
                else {
                    if ((j & 0x70) != 0x60) {
                        throw new UTFDataFormatException("Invalid UTF-8 encoding: " + Integer.toHexString(j) + ", charCount=" + n + ", offset=" + i);
                    }
                    i += 3;
                }
                ++n;
            }
            i -= read;
            this.write(b, 0, read, n2);
            n2 += read;
        }
        final long length = this.getLength();
        if (length > 2L) {
            final byte[] array = new byte[3];
            this.read(array, 0, 3, length - 3L);
            if ((array[0] & 0xFF) == 0xE0 && (array[1] & 0xFF) == 0x0 && (array[2] & 0xFF) == 0x0) {
                this.truncate(length - 3L);
                --n;
            }
        }
        if (value != Long.MAX_VALUE && n != value) {
            throw new EOFException(MessageService.getTextMessage("I029", new Long(value), new Long(n)));
        }
        return n;
    }
    
    protected void finalize() throws Throwable {
        this.free();
    }
    
    private void deleteFile(final StorageFile storageFile) throws IOException {
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction() {
                public Object run() throws IOException {
                    storageFile.delete();
                    return null;
                }
            });
        }
        catch (PrivilegedActionException ex) {
            final Exception exception = ex.getException();
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            if (exception instanceof RuntimeException) {
                throw (RuntimeException)exception;
            }
            throw Util.newIOException(exception);
        }
    }
    
    void free() throws IOException {
        this.dataBytes = null;
        if (this.tmpFile != null) {
            this.releaseTempFile(this.tmpFile);
            this.tmpFile = null;
        }
    }
    
    private void releaseTempFile(final LOBFile lobFile) throws IOException {
        this.conn.removeLobFile(lobFile);
        lobFile.close();
        this.deleteFile(lobFile.getStorageFile());
    }
    
    synchronized long replaceBytes(final byte[] array, final long n, final long n2) throws IOException, StandardException {
        final long length = this.getLength();
        if (this.isBytes) {
            final long n3 = length - n2 + n + array.length;
            if (n3 > this.bufferSize) {
                final byte[] dataBytes = this.dataBytes;
                this.init(dataBytes, n);
                this.write(array, 0, array.length, this.getLength());
                if (n2 < length) {
                    this.write(dataBytes, (int)n2, (int)(length - n2), this.getLength());
                }
            }
            else {
                final byte[] dataBytes2 = new byte[(int)n3];
                System.arraycopy(this.dataBytes, 0, dataBytes2, 0, (int)n);
                System.arraycopy(array, 0, dataBytes2, (int)n, array.length);
                if (n2 < length) {
                    System.arraycopy(this.dataBytes, (int)n2, dataBytes2, (int)(n + array.length), (int)(length - n2));
                }
                this.dataBytes = dataBytes2;
            }
        }
        else {
            final byte[] array2 = new byte[0];
            final LOBFile tmpFile = this.tmpFile;
            this.init(array2, 0L);
            final byte[] array3 = new byte[1024];
            long b = n;
            tmpFile.seek(0L);
            while (b != 0L) {
                final int read = tmpFile.read(array3, 0, (int)Math.min(1024L, b));
                if (read == -1) {
                    break;
                }
                this.tmpFile.write(array3, 0, read);
                b -= read;
            }
            this.tmpFile.write(array);
            tmpFile.seek(n2);
            if (n2 < length) {
                while (true) {
                    final int read2 = tmpFile.read(array3, 0, 1024);
                    if (read2 == -1) {
                        break;
                    }
                    this.tmpFile.write(array3, 0, read2);
                }
            }
            this.releaseTempFile(tmpFile);
        }
        ++this.updateCount;
        return n + array.length;
    }
    
    long getUpdateCount() {
        return this.updateCount;
    }
}
