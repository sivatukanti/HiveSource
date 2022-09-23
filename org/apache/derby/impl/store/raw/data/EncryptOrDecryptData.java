// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.util.ReuseFactory;
import java.security.AccessController;
import org.apache.derby.iapi.util.Matchable;
import org.apache.derby.iapi.store.raw.Loggable;
import org.apache.derby.iapi.store.raw.data.RawContainerHandle;
import org.apache.derby.iapi.store.raw.ContainerKey;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;
import org.apache.derby.io.StorageFile;
import org.apache.derby.io.StorageFactory;
import java.security.PrivilegedAction;

public class EncryptOrDecryptData implements PrivilegedAction
{
    private BaseDataFileFactory dataFactory;
    private StorageFactory storageFactory;
    private static final int STORAGE_FILE_EXISTS_ACTION = 1;
    private static final int STORAGE_FILE_DELETE_ACTION = 2;
    private static final int STORAGE_FILE_RENAME_ACTION = 3;
    private int actionCode;
    private StorageFile actionStorageFile;
    private StorageFile actionDestStorageFile;
    
    public EncryptOrDecryptData(final BaseDataFileFactory dataFactory) {
        this.dataFactory = dataFactory;
        this.storageFactory = dataFactory.getStorageFactory();
    }
    
    public void decryptAllContainers(final RawTransaction rawTransaction) throws StandardException {
        this.encryptOrDecryptAllContainers(rawTransaction, false);
    }
    
    public void encryptAllContainers(final RawTransaction rawTransaction) throws StandardException {
        this.encryptOrDecryptAllContainers(rawTransaction, true);
    }
    
    private void encryptOrDecryptAllContainers(final RawTransaction rawTransaction, final boolean b) throws StandardException {
        final String[] containerNames = this.dataFactory.getContainerNames();
        if (containerNames != null) {
            final long n = 0L;
            for (int i = containerNames.length - 1; i >= 0; --i) {
                long long1;
                try {
                    long1 = Long.parseLong(containerNames[i].substring(1, containerNames[i].length() - 4), 16);
                }
                catch (Throwable t) {
                    continue;
                }
                this.encryptOrDecryptContainer(rawTransaction, new ContainerKey(n, long1), b);
            }
        }
    }
    
    private void encryptOrDecryptContainer(final RawTransaction rawTransaction, final ContainerKey containerKey, final boolean b) throws StandardException {
        final RawContainerHandle rawContainerHandle = (RawContainerHandle)rawTransaction.openContainer(containerKey, rawTransaction.newLockingPolicy(2, 5, true), 4);
        rawTransaction.logAndDo(new EncryptContainerOperation(rawContainerHandle));
        this.dataFactory.flush(rawTransaction.getLastLogInstant());
        final String filePath = this.getFilePath(containerKey, false);
        final StorageFile storageFile = this.storageFactory.newStorageFile(filePath);
        rawContainerHandle.encryptOrDecryptContainer(filePath, b);
        rawContainerHandle.close();
        if (!this.dataFactory.getPageCache().discard(containerKey)) {}
        if (!this.dataFactory.getContainerCache().discard(containerKey)) {}
        final StorageFile containerPath = this.dataFactory.getContainerPath(containerKey, false);
        final StorageFile file = this.getFile(containerKey, true);
        if (!this.privRename(containerPath, file)) {
            throw StandardException.newException("XSRS4.S", containerPath, file);
        }
        if (!this.privRename(storageFile, containerPath)) {
            throw StandardException.newException("XSRS4.S", storageFile, containerPath);
        }
    }
    
    private StorageFile getFile(final ContainerKey containerKey, final boolean b) {
        return this.storageFactory.newStorageFile(this.getFilePath(containerKey, b));
    }
    
    private String getFilePath(final ContainerKey containerKey, final boolean b) {
        final StringBuffer sb = new StringBuffer("seg");
        sb.append(containerKey.getSegmentId());
        sb.append(this.storageFactory.getSeparator());
        sb.append(b ? 'o' : 'n');
        sb.append(Long.toHexString(containerKey.getContainerId()));
        sb.append(".dat");
        return sb.toString();
    }
    
    private boolean isOldContainerFile(final String s) {
        return s.startsWith("o") && s.endsWith(".dat");
    }
    
    private StorageFile getFile(final String str) {
        final long lng = 0L;
        final StringBuffer sb = new StringBuffer("seg");
        sb.append(lng);
        sb.append(this.storageFactory.getSeparator());
        sb.append(str);
        return this.storageFactory.newStorageFile(sb.toString());
    }
    
    void restoreContainer(final ContainerKey containerKey) throws StandardException {
        if (!this.dataFactory.getContainerCache().discard(containerKey)) {}
        final StorageFile containerPath = this.dataFactory.getContainerPath(containerKey, false);
        final StorageFile file = this.getFile(containerKey, true);
        final StorageFile file2 = this.getFile(containerKey, false);
        if (this.privExists(file)) {
            if (this.privExists(containerPath) && !this.privRename(containerPath, file2)) {
                throw StandardException.newException("XSRS4.S", containerPath, file2);
            }
            if (!this.privRename(file, containerPath)) {
                throw StandardException.newException("XSRS4.S", file, containerPath);
            }
        }
        if (this.privExists(file2) && !this.privDelete(file2)) {
            throw StandardException.newException("XBM0R.D", file2);
        }
    }
    
    public void removeOldVersionOfContainers() throws StandardException {
        final String[] containerNames = this.dataFactory.getContainerNames();
        if (containerNames != null) {
            for (int i = containerNames.length - 1; i >= 0; --i) {
                if (this.isOldContainerFile(containerNames[i])) {
                    final StorageFile file = this.getFile(containerNames[i]);
                    if (!this.privDelete(file)) {
                        throw StandardException.newException("XSDFJ.S", file);
                    }
                }
            }
        }
    }
    
    private synchronized boolean privExists(final StorageFile actionStorageFile) {
        this.actionCode = 1;
        this.actionStorageFile = actionStorageFile;
        final Boolean doPrivileged = AccessController.doPrivileged((PrivilegedAction<Boolean>)this);
        this.actionStorageFile = null;
        return doPrivileged;
    }
    
    private synchronized boolean privDelete(final StorageFile actionStorageFile) {
        this.actionCode = 2;
        this.actionStorageFile = actionStorageFile;
        final Boolean doPrivileged = AccessController.doPrivileged((PrivilegedAction<Boolean>)this);
        this.actionStorageFile = null;
        return doPrivileged;
    }
    
    private synchronized boolean privRename(final StorageFile actionStorageFile, final StorageFile actionDestStorageFile) {
        this.actionCode = 3;
        this.actionStorageFile = actionStorageFile;
        this.actionDestStorageFile = actionDestStorageFile;
        final Boolean doPrivileged = AccessController.doPrivileged((PrivilegedAction<Boolean>)this);
        this.actionStorageFile = null;
        this.actionDestStorageFile = null;
        return doPrivileged;
    }
    
    public Object run() {
        switch (this.actionCode) {
            case 1: {
                return ReuseFactory.getBoolean(this.actionStorageFile.exists());
            }
            case 2: {
                return ReuseFactory.getBoolean(this.actionStorageFile.delete());
            }
            case 3: {
                return ReuseFactory.getBoolean(this.actionStorageFile.renameTo(this.actionDestStorageFile));
            }
            default: {
                return null;
            }
        }
    }
}
