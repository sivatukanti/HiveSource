// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.load;

import java.io.OutputStream;
import org.apache.derby.iapi.services.io.LimitInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Blob;

class ImportBlob implements Blob
{
    private ImportLobFile lobFile;
    private long blobPosition;
    private long blobLength;
    private byte[] blobData;
    
    public ImportBlob(final ImportLobFile lobFile, final long blobPosition, final long blobLength) {
        this.blobData = null;
        this.lobFile = lobFile;
        this.blobPosition = blobPosition;
        this.blobLength = blobLength;
    }
    
    public ImportBlob(final byte[] blobData) {
        this.blobData = null;
        this.blobData = blobData;
        this.blobLength = blobData.length;
    }
    
    public long length() throws SQLException {
        return this.blobLength;
    }
    
    public InputStream getBinaryStream() throws SQLException {
        try {
            if (this.blobData != null) {
                final LimitInputStream limitInputStream = new LimitInputStream(new ByteArrayInputStream(this.blobData));
                limitInputStream.setLimit((int)this.blobLength);
                return limitInputStream;
            }
            return this.lobFile.getBinaryStream(this.blobPosition, this.blobLength);
        }
        catch (Exception ex) {
            throw LoadError.unexpectedError(ex);
        }
    }
    
    public byte[] getBytes(final long n, final int n2) throws SQLException {
        throw LoadError.unexpectedError(new Exception("Method not implemented"));
    }
    
    public long position(final byte[] array, final long n) throws SQLException {
        throw LoadError.unexpectedError(new Exception("Method not implemented"));
    }
    
    public long position(final Blob blob, final long n) throws SQLException {
        throw LoadError.unexpectedError(new Exception("Method not implemented"));
    }
    
    public int setBytes(final long n, final byte[] array) throws SQLException {
        throw LoadError.unexpectedError(new Exception("Method not implemented"));
    }
    
    public int setBytes(final long n, final byte[] array, final int n2, final int n3) throws SQLException {
        throw LoadError.unexpectedError(new Exception("Method not implemented"));
    }
    
    public OutputStream setBinaryStream(final long n) throws SQLException {
        throw LoadError.unexpectedError(new Exception("Method not implemented"));
    }
    
    public void truncate(final long n) throws SQLException {
        throw LoadError.unexpectedError(new Exception("Method not implemented"));
    }
}
