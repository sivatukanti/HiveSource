// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.load;

import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.FileNotFoundException;
import org.apache.derby.iapi.error.PublicAPI;
import org.apache.derby.iapi.error.StandardException;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.io.RandomAccessFile;
import java.io.File;
import java.io.Reader;
import org.apache.derby.iapi.services.io.LimitInputStream;

class ImportLobFile
{
    private ImportFileInputStream lobInputStream;
    private LimitInputStream lobLimitIn;
    private Reader lobReader;
    private String dataCodeset;
    
    ImportLobFile(final File file, final String dataCodeset) throws Exception {
        this.lobInputStream = null;
        this.lobReader = null;
        this.dataCodeset = dataCodeset;
        this.openLobFile(file);
    }
    
    private void openLobFile(final File file) throws Exception {
        RandomAccessFile randomAccessFile;
        try {
            try {
                randomAccessFile = AccessController.doPrivileged((PrivilegedExceptionAction<RandomAccessFile>)new PrivilegedExceptionAction() {
                    public Object run() throws IOException {
                        return new RandomAccessFile(file, "r");
                    }
                });
            }
            catch (PrivilegedActionException ex) {
                throw ex.getException();
            }
        }
        catch (FileNotFoundException ex2) {
            throw PublicAPI.wrapStandardException(StandardException.newException("XIE0P.S", file.getPath()));
        }
        this.lobInputStream = new ImportFileInputStream(randomAccessFile);
        this.lobLimitIn = new LimitInputStream(this.lobInputStream);
    }
    
    public InputStream getBinaryStream(final long n, final long n2) throws IOException {
        this.lobInputStream.seek(n);
        this.lobLimitIn.clearLimit();
        this.lobLimitIn.setLimit((int)n2);
        return this.lobLimitIn;
    }
    
    public String getString(final int n, final int limit) throws IOException {
        this.lobInputStream.seek(n);
        this.lobLimitIn.clearLimit();
        this.lobLimitIn.setLimit(limit);
        this.lobReader = ((this.dataCodeset == null) ? new InputStreamReader(this.lobLimitIn) : new InputStreamReader(this.lobLimitIn, this.dataCodeset));
        final StringBuffer sb = new StringBuffer();
        final char[] str = new char[1024];
        for (int i = this.lobReader.read(str, 0, 1024); i != -1; i = this.lobReader.read(str, 0, 1024)) {
            sb.append(str, 0, i);
        }
        return sb.toString();
    }
    
    public Reader getCharacterStream(final long n, final long n2) throws IOException {
        this.lobInputStream.seek(n);
        this.lobLimitIn.clearLimit();
        this.lobLimitIn.setLimit((int)n2);
        return this.lobReader = ((this.dataCodeset == null) ? new InputStreamReader(this.lobLimitIn) : new InputStreamReader(this.lobLimitIn, this.dataCodeset));
    }
    
    public long getClobDataLength(final long n, final long n2) throws IOException {
        this.lobInputStream.seek(n);
        this.lobLimitIn.clearLimit();
        this.lobLimitIn.setLimit((int)n2);
        this.lobReader = ((this.dataCodeset == null) ? new InputStreamReader(this.lobLimitIn) : new InputStreamReader(this.lobLimitIn, this.dataCodeset));
        final char[] array = new char[1024];
        long n3 = 0L;
        for (int i = this.lobReader.read(array, 0, 1024); i != -1; i = this.lobReader.read(array, 0, 1024)) {
            n3 += i;
        }
        return n3;
    }
    
    public void close() throws IOException {
        if (this.lobReader != null) {
            this.lobReader.close();
        }
        else if (this.lobLimitIn != null) {
            this.lobLimitIn.close();
        }
        else if (this.lobInputStream != null) {
            this.lobInputStream.close();
        }
    }
}
