// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.store.raw.xact.RawTransaction;
import org.apache.derby.iapi.store.raw.Loggable;
import org.apache.derby.iapi.services.daemon.Serviceable;
import org.apache.derby.io.StorageFile;
import java.io.OutputStream;
import java.io.IOException;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.error.StandardException;
import java.io.InputStream;
import org.apache.derby.iapi.store.access.FileResource;

class RFResource implements FileResource
{
    private final BaseDataFileFactory factory;
    
    RFResource(final BaseDataFileFactory factory) {
        this.factory = factory;
    }
    
    public long add(final String s, final InputStream inputStream) throws StandardException {
        OutputStream outputStream = null;
        if (this.factory.isReadOnly()) {
            throw StandardException.newException("XSDFB.S");
        }
        final long nextId = this.factory.getNextId();
        try {
            final StorageFile asFile = this.getAsFile(s, nextId);
            if (asFile.exists()) {
                throw StandardException.newException("XSDF0.S", asFile);
            }
            this.factory.getRawStoreFactory().getXactFactory().findUserTransaction(this.factory.getRawStoreFactory(), ContextService.getFactory().getCurrentContextManager(), "UserTransaction").blockBackup(true);
            final StorageFile parentDir = asFile.getParentDir();
            final StorageFile parentDir2 = parentDir.getParentDir();
            final boolean exists = parentDir2.exists();
            if (!parentDir.exists()) {
                if (!parentDir.mkdirs()) {
                    throw StandardException.newException("XSDF3.S", parentDir);
                }
                parentDir.limitAccessToOwner();
                if (!exists) {
                    parentDir2.limitAccessToOwner();
                }
            }
            outputStream = asFile.getOutputStream();
            final byte[] array = new byte[4096];
            this.factory.writeInProgress();
            try {
                int read;
                while ((read = inputStream.read(array)) != -1) {
                    outputStream.write(array, 0, read);
                }
                this.factory.writableStorageFactory.sync(outputStream, false);
            }
            finally {
                this.factory.writeFinished();
            }
        }
        catch (IOException ex) {
            throw StandardException.newException("XSDFF.S", ex);
        }
        finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            }
            catch (IOException ex2) {}
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            catch (IOException ex3) {}
        }
        return nextId;
    }
    
    public void removeJarDir(final String s) throws StandardException {
        if (this.factory.isReadOnly()) {
            throw StandardException.newException("XSDFB.S");
        }
        this.factory.getRawStoreFactory().getXactFactory().findUserTransaction(this.factory.getRawStoreFactory(), ContextService.getFactory().getCurrentContextManager(), "UserTransaction").addPostCommitWork(new RemoveFile(this.factory.storageFactory.newStorageFile(s)));
    }
    
    public void remove(final String s, final long n) throws StandardException {
        if (this.factory.isReadOnly()) {
            throw StandardException.newException("XSDFB.S");
        }
        final RawTransaction userTransaction = this.factory.getRawStoreFactory().getXactFactory().findUserTransaction(this.factory.getRawStoreFactory(), ContextService.getFactory().getCurrentContextManager(), "UserTransaction");
        userTransaction.blockBackup(true);
        userTransaction.logAndDo(new RemoveFileOperation(s, n, true));
        userTransaction.addPostCommitWork(new RemoveFile(this.getAsFile(s, n)));
    }
    
    public long replace(final String s, final long n, final InputStream inputStream) throws StandardException {
        if (this.factory.isReadOnly()) {
            throw StandardException.newException("XSDFB.S");
        }
        this.remove(s, n);
        return this.add(s, inputStream);
    }
    
    public StorageFile getAsFile(final String s, final long n) {
        return this.factory.storageFactory.newStorageFile(this.factory.getVersionedName(s, n));
    }
    
    public char getSeparatorChar() {
        return this.factory.storageFactory.getSeparator();
    }
}
