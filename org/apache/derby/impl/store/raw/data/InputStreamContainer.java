// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import java.io.FilterInputStream;
import org.apache.derby.iapi.services.io.InputStreamUtil;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import java.io.InputStream;
import java.io.DataInput;
import org.apache.derby.iapi.error.StandardException;
import java.io.DataInputStream;
import java.io.IOException;
import org.apache.derby.iapi.store.raw.ContainerKey;
import org.apache.derby.io.StorageFile;

final class InputStreamContainer extends FileContainer
{
    private StorageFile containerPath;
    
    InputStreamContainer(final BaseDataFileFactory baseDataFileFactory) {
        super(baseDataFileFactory);
        this.canUpdate = false;
    }
    
    final boolean openContainer(final ContainerKey containerKey) throws StandardException {
        DataInput dataInput = null;
        try {
            this.containerPath = this.dataFactory.getContainerPath(containerKey, false);
            InputStream in;
            try {
                in = this.containerPath.getInputStream();
            }
            catch (IOException ex2) {
                this.containerPath = this.dataFactory.getContainerPath(containerKey, true);
                try {
                    in = this.getInputStream();
                }
                catch (IOException ex3) {
                    this.containerPath = null;
                    return false;
                }
            }
            dataInput = new DataInputStream(in);
            this.readHeader(this.getEmbryonicPage(dataInput));
            return true;
        }
        catch (IOException ex) {
            throw StandardException.newException("XSDG3.D", ex, new Object[] { this.getIdentity().toString(), "open", containerKey.toString() });
        }
        finally {
            if (dataInput != null) {
                try {
                    ((FilterInputStream)dataInput).close();
                }
                catch (IOException ex4) {}
            }
        }
    }
    
    void closeContainer() {
        this.containerPath = null;
    }
    
    public final void clean(final boolean b) throws StandardException {
    }
    
    protected final int preAllocate(final long n, final int n2) {
        return 0;
    }
    
    protected void truncatePages(final long n) {
    }
    
    void createContainer(final ContainerKey containerKey) throws StandardException {
    }
    
    protected final void removeContainer(final LogInstant logInstant, final boolean b) throws StandardException {
    }
    
    protected final void readPage(final long n, final byte[] array) throws IOException, StandardException {
        this.readPositionedPage(n * this.pageSize, array);
        if (this.dataFactory.databaseEncrypted() && n != 0L) {
            this.decryptPage(array, this.pageSize);
        }
    }
    
    protected void readPositionedPage(final long n, final byte[] array) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = this.getInputStream();
            InputStreamUtil.skipFully(inputStream, n);
            InputStreamUtil.readFully(inputStream, array, 0, this.pageSize);
            inputStream.close();
            inputStream = null;
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (IOException ex) {}
            }
        }
    }
    
    protected final void writePage(final long n, final byte[] array, final boolean b) throws IOException, StandardException {
    }
    
    protected final void flushAll() {
    }
    
    protected InputStream getInputStream() throws IOException {
        return this.containerPath.getInputStream();
    }
    
    protected void backupContainer(final BaseContainerHandle baseContainerHandle, final String s) throws StandardException {
        throw StandardException.newException("XSAI3.S");
    }
    
    protected void encryptOrDecryptContainer(final BaseContainerHandle baseContainerHandle, final String s, final boolean b) throws StandardException {
        throw StandardException.newException("XSAI3.S");
    }
}
