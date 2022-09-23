// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.load;

import java.io.Writer;
import java.io.OutputStream;
import java.io.InputStream;
import org.apache.derby.iapi.services.io.LimitReader;
import java.io.StringReader;
import java.io.Reader;
import java.sql.SQLException;
import java.io.IOException;
import java.sql.Clob;

class ImportClob implements Clob
{
    private ImportLobFile lobFile;
    private long position;
    private long length;
    private long clobLength;
    private String clobData;
    
    public ImportClob(final ImportLobFile lobFile, final long position, final long length) throws IOException {
        this.clobData = null;
        this.lobFile = lobFile;
        this.position = position;
        this.length = length;
        this.clobLength = lobFile.getClobDataLength(position, length);
    }
    
    public ImportClob(final String clobData) {
        this.clobData = null;
        this.clobData = clobData;
        this.clobLength = clobData.length();
    }
    
    public long length() throws SQLException {
        return this.clobLength;
    }
    
    public Reader getCharacterStream() throws SQLException {
        try {
            if (this.clobData != null) {
                final LimitReader limitReader = new LimitReader(new StringReader(this.clobData));
                limitReader.setLimit((int)this.clobLength);
                return limitReader;
            }
            return this.lobFile.getCharacterStream(this.position, this.length);
        }
        catch (Exception ex) {
            throw LoadError.unexpectedError(ex);
        }
    }
    
    public String getSubString(final long n, final int n2) throws SQLException {
        throw LoadError.unexpectedError(new Exception("Method not implemented"));
    }
    
    public InputStream getAsciiStream() throws SQLException {
        throw LoadError.unexpectedError(new Exception("Method not implemented"));
    }
    
    public long position(final String s, final long n) throws SQLException {
        throw LoadError.unexpectedError(new Exception("Method not implemented"));
    }
    
    public long position(final Clob clob, final long n) throws SQLException {
        throw LoadError.unexpectedError(new Exception("Method not implemented"));
    }
    
    public int setString(final long n, final String s) throws SQLException {
        throw LoadError.unexpectedError(new Exception("Method not implemented"));
    }
    
    public int setString(final long n, final String s, final int n2, final int n3) throws SQLException {
        throw LoadError.unexpectedError(new Exception("Method not implemented"));
    }
    
    public OutputStream setAsciiStream(final long n) throws SQLException {
        throw LoadError.unexpectedError(new Exception("Method not implemented"));
    }
    
    public Writer setCharacterStream(final long n) throws SQLException {
        throw LoadError.unexpectedError(new Exception("Method not implemented"));
    }
    
    public void truncate(final long n) throws SQLException {
        throw LoadError.unexpectedError(new Exception("Method not implemented"));
    }
}
