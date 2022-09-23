// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import org.apache.derby.iapi.util.ReuseFactory;
import org.apache.derby.iapi.services.io.FileUtil;
import java.security.PrivilegedAction;
import java.io.File;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import org.apache.derby.iapi.util.InterruptDetectedException;
import java.io.IOException;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.util.Matchable;
import org.apache.derby.iapi.util.InterruptStatus;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.io.StorageFile;
import org.apache.derby.iapi.store.raw.ContainerKey;
import org.apache.derby.io.StorageRandomAccessFile;
import java.security.PrivilegedExceptionAction;

class RAFContainer extends FileContainer implements PrivilegedExceptionAction
{
    protected StorageRandomAccessFile fileData;
    protected boolean needsSync;
    private int actionCode;
    private static final int GET_FILE_NAME_ACTION = 1;
    private static final int CREATE_CONTAINER_ACTION = 2;
    private static final int REMOVE_FILE_ACTION = 3;
    private static final int OPEN_CONTAINER_ACTION = 4;
    private static final int STUBBIFY_ACTION = 5;
    private static final int GET_RANDOM_ACCESS_FILE_ACTION = 7;
    private static final int REOPEN_CONTAINER_ACTION = 8;
    private ContainerKey actionIdentity;
    private boolean actionStub;
    private boolean actionErrorOK;
    private boolean actionTryAlternatePath;
    private StorageFile actionFile;
    private LogInstant actionInstant;
    private boolean inBackup;
    private boolean inRemove;
    private String fileName;
    
    RAFContainer(final BaseDataFileFactory baseDataFileFactory) {
        super(baseDataFileFactory);
        this.inBackup = false;
        this.inRemove = false;
    }
    
    public synchronized boolean isDirty() {
        return super.isDirty() || this.needsSync;
    }
    
    protected void removeContainer(final LogInstant logInstant, final boolean b) throws StandardException {
        try {
            synchronized (this) {
                this.inRemove = true;
                while (this.inBackup) {
                    try {
                        this.wait();
                    }
                    catch (InterruptedException ex) {
                        InterruptStatus.setInterrupted();
                    }
                }
            }
            this.pageCache.discard(this.identity);
            this.stubbify(logInstant);
        }
        finally {
            synchronized (this) {
                this.inRemove = false;
                this.notifyAll();
            }
        }
    }
    
    void closeContainer() {
        if (this.fileData != null) {
            try {
                this.fileData.close();
            }
            catch (IOException ex) {}
            finally {
                this.fileData = null;
            }
        }
    }
    
    protected void readPage(final long n, final byte[] array) throws IOException, StandardException {
        final long n2 = n * this.pageSize;
        synchronized (this) {
            this.fileData.seek(n2);
            this.fileData.readFully(array, 0, this.pageSize);
        }
        if (this.dataFactory.databaseEncrypted() && n != 0L) {
            this.decryptPage(array, this.pageSize);
        }
    }
    
    protected void writePage(final long n, final byte[] array, final boolean b) throws IOException, StandardException {
        synchronized (this) {
            if (this.getCommittedDropState()) {
                return;
            }
            final long n2 = n * this.pageSize;
            byte[] encryptionBuffer = null;
            if (this.dataFactory.databaseEncrypted() && n != 0L) {
                encryptionBuffer = this.getEncryptionBuffer();
            }
            final byte[] updatePageArray = this.updatePageArray(n, array, encryptionBuffer, false);
            try {
                this.fileData.seek(n2);
                if (this.fileData.getFilePointer() != n2) {
                    this.padFile(this.fileData, n2);
                }
                this.dataFactory.writeInProgress();
                try {
                    this.fileData.write(updatePageArray, 0, this.pageSize);
                }
                finally {
                    this.dataFactory.writeFinished();
                }
            }
            catch (IOException ex) {
                if (!this.padFile(this.fileData, n2)) {
                    throw ex;
                }
                this.fileData.seek(n2);
                this.dataFactory.writeInProgress();
                try {
                    this.fileData.write(updatePageArray, 0, this.pageSize);
                }
                finally {
                    this.dataFactory.writeFinished();
                }
            }
            if (b) {
                this.dataFactory.writeInProgress();
                try {
                    if (!this.dataFactory.dataNotSyncedAtAllocation) {
                        this.fileData.sync();
                    }
                }
                finally {
                    this.dataFactory.writeFinished();
                }
            }
            else {
                this.needsSync = true;
            }
        }
    }
    
    protected byte[] updatePageArray(final long n, final byte[] array, final byte[] array2, final boolean b) throws StandardException, IOException {
        if (n == 0L) {
            this.writeHeader(this.getIdentity(), array);
            return array;
        }
        if (array2 != null && (this.dataFactory.databaseEncrypted() || b)) {
            return this.encryptPage(array, this.pageSize, array2, b);
        }
        return array;
    }
    
    private boolean padFile(final StorageRandomAccessFile storageRandomAccessFile, final long n) throws IOException, StandardException {
        long length = storageRandomAccessFile.length();
        if (length >= n) {
            return false;
        }
        final byte[] array = new byte[this.pageSize];
        storageRandomAccessFile.seek(length);
        while (length < n) {
            this.dataFactory.writeInProgress();
            try {
                long n2 = n - length;
                if (n2 > this.pageSize) {
                    n2 = this.pageSize;
                }
                storageRandomAccessFile.write(array, 0, (int)n2);
            }
            finally {
                this.dataFactory.writeFinished();
            }
            length += this.pageSize;
        }
        return true;
    }
    
    public void clean(final boolean b) throws StandardException {
        boolean b2 = false;
        int i = 0;
        int n = 120;
        while (i == 0) {
            i = 1;
            synchronized (this) {
                if (this.getCommittedDropState()) {
                    this.clearDirty();
                    return;
                }
                while (this.preDirty) {
                    b2 = true;
                    try {
                        this.wait();
                    }
                    catch (InterruptedException ex3) {
                        InterruptStatus.setInterrupted();
                    }
                }
                if (b2 && this.getCommittedDropState()) {
                    this.clearDirty();
                    return;
                }
                if (b) {
                    continue;
                }
                if (!this.isDirty()) {
                    continue;
                }
                try {
                    this.writeRAFHeader(this.getIdentity(), this.fileData, false, true);
                    this.clearDirty();
                }
                catch (InterruptDetectedException ex) {
                    if (--n <= 0) {
                        throw StandardException.newException("XSDG9.D", ex);
                    }
                    i = 0;
                    try {
                        Thread.sleep(500L);
                    }
                    catch (InterruptedException ex4) {
                        InterruptStatus.setInterrupted();
                    }
                }
                catch (IOException ex2) {
                    throw this.dataFactory.markCorrupt(StandardException.newException("XSDG3.D", ex2, (this.getIdentity() != null) ? this.getIdentity().toString() : "unknown", "clean", this.fileName));
                }
            }
        }
    }
    
    private void clearDirty() {
        this.isDirty = false;
        this.needsSync = false;
    }
    
    protected int preAllocate(final long n, final int n2) {
        int doPreAllocatePages = this.doPreAllocatePages(n, n2);
        if (doPreAllocatePages > 0) {
            synchronized (this) {
                boolean b = false;
                try {
                    this.dataFactory.writeInProgress();
                    b = true;
                    if (!this.dataFactory.dataNotSyncedAtAllocation) {
                        this.fileData.sync();
                    }
                }
                catch (IOException ex) {
                    doPreAllocatePages = 0;
                }
                catch (StandardException ex2) {
                    doPreAllocatePages = 0;
                }
                finally {
                    if (b) {
                        this.dataFactory.writeFinished();
                    }
                }
            }
        }
        return doPreAllocatePages;
    }
    
    protected void truncatePages(final long n) throws StandardException {
        synchronized (this) {
            boolean b = false;
            try {
                this.dataFactory.writeInProgress();
                b = true;
                this.fileData.setLength((n + 1L) * this.pageSize);
            }
            catch (IOException ex) {}
            catch (StandardException ex2) {}
            finally {
                if (b) {
                    this.dataFactory.writeFinished();
                }
            }
        }
    }
    
    private void writeRAFHeader(final Object o, final StorageRandomAccessFile storageRandomAccessFile, final boolean b, final boolean b2) throws IOException, StandardException {
        byte[] embryonicPage;
        if (b) {
            embryonicPage = new byte[this.pageSize];
        }
        else {
            embryonicPage = this.getEmbryonicPage(storageRandomAccessFile, 0L);
        }
        this.writeHeader(o, storageRandomAccessFile, b, embryonicPage);
        if (b2) {
            this.dataFactory.writeInProgress();
            try {
                if (!this.dataFactory.dataNotSyncedAtCheckpoint) {
                    storageRandomAccessFile.sync();
                }
            }
            finally {
                this.dataFactory.writeFinished();
            }
        }
    }
    
    protected void flushAll() throws StandardException {
        this.pageCache.clean(this.identity);
        this.clean(false);
    }
    
    synchronized StorageFile getFileName(final ContainerKey actionIdentity, final boolean actionStub, final boolean actionErrorOK, final boolean actionTryAlternatePath) throws StandardException {
        this.actionCode = 1;
        this.actionIdentity = actionIdentity;
        this.actionStub = actionStub;
        this.actionErrorOK = actionErrorOK;
        this.actionTryAlternatePath = actionTryAlternatePath;
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<StorageFile>)this);
        }
        catch (PrivilegedActionException ex) {
            throw (StandardException)ex.getException();
        }
        finally {
            this.actionIdentity = null;
        }
    }
    
    protected StorageFile privGetFileName(final ContainerKey containerKey, final boolean b, final boolean b2, final boolean b3) throws StandardException {
        StorageFile storageFile = this.dataFactory.getContainerPath(containerKey, b);
        if (!storageFile.exists() && b3) {
            storageFile = this.dataFactory.getAlternateContainerPath(containerKey, b);
        }
        if (!storageFile.exists()) {
            final StorageFile parentDir = storageFile.getParentDir();
            if (!parentDir.exists()) {
                synchronized (this.dataFactory) {
                    if (!parentDir.exists()) {
                        if (!parentDir.mkdirs()) {
                            if (b2) {
                                return null;
                            }
                            throw StandardException.newException("XSDF3.S", parentDir);
                        }
                        else {
                            parentDir.limitAccessToOwner();
                        }
                    }
                }
            }
        }
        return storageFile;
    }
    
    synchronized void createContainer(final ContainerKey actionIdentity) throws StandardException {
        this.actionCode = 2;
        this.actionIdentity = actionIdentity;
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)this);
        }
        catch (PrivilegedActionException ex) {
            throw (StandardException)ex.getException();
        }
        finally {
            this.actionIdentity = null;
        }
    }
    
    private void copyFile(final StorageFile storageFile, final File file) throws StandardException {
        if (!AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction() {
            public Object run() {
                return ReuseFactory.getBoolean(FileUtil.copyFile(RAFContainer.this.dataFactory.getStorageFactory(), storageFile, file));
            }
        })) {
            throw StandardException.newException("XSRS5.S", storageFile, file);
        }
    }
    
    private void removeFile(final File file) throws StandardException {
        if (!AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction() {
            public Object run() {
                return ReuseFactory.getBoolean(!file.exists() || file.delete());
            }
        })) {
            throw StandardException.newException("XBM0R.D", file);
        }
    }
    
    synchronized boolean removeFile(final StorageFile actionFile) throws SecurityException, StandardException {
        this.actionCode = 3;
        this.actionFile = actionFile;
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Object>)this) != null;
        }
        catch (PrivilegedActionException ex) {
            throw (StandardException)ex.getException();
        }
        finally {
            this.actionFile = null;
        }
    }
    
    private boolean privRemoveFile(final StorageFile storageFile) throws StandardException {
        this.closeContainer();
        this.dataFactory.writeInProgress();
        try {
            if (storageFile.exists()) {
                return storageFile.delete();
            }
        }
        finally {
            this.dataFactory.writeFinished();
        }
        return true;
    }
    
    synchronized boolean openContainer(final ContainerKey actionIdentity) throws StandardException {
        this.actionCode = 4;
        this.actionIdentity = actionIdentity;
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Object>)this) != null;
        }
        catch (PrivilegedActionException ex) {
            this.closeContainer();
            throw (StandardException)ex.getException();
        }
        catch (RuntimeException ex2) {
            this.closeContainer();
            throw ex2;
        }
        finally {
            this.actionIdentity = null;
        }
    }
    
    protected synchronized void reopenContainer(final ContainerKey actionIdentity) throws StandardException {
        this.actionCode = 8;
        this.actionIdentity = actionIdentity;
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)this);
        }
        catch (PrivilegedActionException ex) {
            this.closeContainer();
            throw (StandardException)ex.getException();
        }
        catch (RuntimeException ex2) {
            this.closeContainer();
            throw ex2;
        }
        finally {
            this.actionIdentity = null;
        }
    }
    
    private synchronized void stubbify(final LogInstant actionInstant) throws StandardException {
        this.setDroppedState(true);
        this.setCommittedDropState(true);
        this.actionIdentity = (ContainerKey)this.getIdentity();
        this.actionInstant = actionInstant;
        this.actionCode = 5;
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)this);
        }
        catch (PrivilegedActionException ex) {
            throw (StandardException)ex.getException();
        }
        finally {
            this.actionIdentity = null;
            this.actionInstant = null;
        }
    }
    
    protected void backupContainer(final BaseContainerHandle baseContainerHandle, final String s) throws StandardException {
        int i = 0;
        File file = null;
        RandomAccessFile randomAccessFile = null;
        boolean b = false;
        BasePage latchedPage = null;
        while (i == 0) {
            try {
                synchronized (this) {
                    while (this.inRemove) {
                        try {
                            this.wait();
                        }
                        catch (InterruptedException ex3) {
                            InterruptStatus.setInterrupted();
                        }
                    }
                    if (this.getCommittedDropState()) {
                        b = true;
                    }
                    this.inBackup = true;
                }
                if (b) {
                    final StorageFile fileName = this.getFileName((ContainerKey)this.getIdentity(), true, false, true);
                    file = new File(s, fileName.getName());
                    this.copyFile(fileName, file);
                }
                else {
                    final long lastPageNumber = this.getLastPageNumber(baseContainerHandle);
                    if (lastPageNumber == -1L) {
                        return;
                    }
                    file = new File(s, this.getFileName((ContainerKey)this.getIdentity(), false, false, true).getName());
                    randomAccessFile = this.getRandomAccessFile(file);
                    byte[] array = null;
                    if (this.dataFactory.databaseEncrypted()) {
                        array = new byte[this.pageSize];
                    }
                    for (long n = 0L; n <= lastPageNumber; ++n) {
                        latchedPage = this.getLatchedPage(baseContainerHandle, n);
                        randomAccessFile.write(this.updatePageArray(n, latchedPage.getPageArray(), array, false), 0, this.pageSize);
                        latchedPage.unlatch();
                        latchedPage = null;
                        synchronized (this) {
                            if (this.inRemove) {
                                break;
                            }
                        }
                    }
                }
                if (!b) {
                    randomAccessFile.getFD().sync();
                    randomAccessFile.close();
                    randomAccessFile = null;
                }
                i = 1;
            }
            catch (IOException ex) {
                throw StandardException.newException("XSDFH.S", ex, file);
            }
            finally {
                synchronized (this) {
                    this.inBackup = false;
                    this.notifyAll();
                }
                if (latchedPage != null) {
                    latchedPage.unlatch();
                    latchedPage = null;
                }
                if (i == 0 && file != null) {
                    if (randomAccessFile != null) {
                        try {
                            randomAccessFile.close();
                            randomAccessFile = null;
                        }
                        catch (IOException ex2) {
                            throw StandardException.newException("XSDFH.S", ex2, file);
                        }
                    }
                    this.removeFile(file);
                }
            }
        }
    }
    
    protected void encryptOrDecryptContainer(final BaseContainerHandle baseContainerHandle, final String s, final boolean b) throws StandardException {
        BasePage latchedPage = null;
        final StorageFile storageFile = this.dataFactory.getStorageFactory().newStorageFile(s);
        StorageRandomAccessFile randomAccessFile = null;
        try {
            final long lastPageNumber = this.getLastPageNumber(baseContainerHandle);
            randomAccessFile = this.getRandomAccessFile(storageFile);
            byte[] array = null;
            if (b) {
                array = new byte[this.pageSize];
            }
            for (long n = 0L; n <= lastPageNumber; ++n) {
                latchedPage = this.getLatchedPage(baseContainerHandle, n);
                randomAccessFile.write(this.updatePageArray(n, latchedPage.getPageArray(), array, true), 0, this.pageSize);
                latchedPage.unlatch();
                latchedPage = null;
            }
            randomAccessFile.sync();
            randomAccessFile.close();
            randomAccessFile = null;
        }
        catch (IOException ex) {
            throw StandardException.newException("XSDG3.D", ex, (this.getIdentity() != null) ? this.getIdentity().toString() : "unknown", b ? "encrypt" : "decrypt", s);
        }
        finally {
            if (latchedPage != null) {
                latchedPage.unlatch();
            }
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                }
                catch (IOException ex2) {
                    throw StandardException.newException("XSDG3.D", ex2, (this.getIdentity() != null) ? this.getIdentity().toString() : "unknown", b ? "encrypt-close" : "decrypt-close", s);
                }
            }
        }
    }
    
    private RandomAccessFile getRandomAccessFile(final File file) throws FileNotFoundException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<RandomAccessFile>)new PrivilegedExceptionAction() {
                public Object run() throws FileNotFoundException {
                    final boolean exists = file.exists();
                    final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                    if (!exists) {
                        FileUtil.limitAccessToOwner(file);
                    }
                    return randomAccessFile;
                }
            });
        }
        catch (PrivilegedActionException ex) {
            throw (FileNotFoundException)ex.getCause();
        }
    }
    
    synchronized StorageRandomAccessFile getRandomAccessFile(final StorageFile actionFile) throws SecurityException, StandardException {
        this.actionCode = 7;
        this.actionFile = actionFile;
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<StorageRandomAccessFile>)this);
        }
        catch (PrivilegedActionException ex) {
            throw (StandardException)ex.getException();
        }
        finally {
            this.actionFile = null;
        }
    }
    
    public Object run() throws StandardException {
        switch (this.actionCode) {
            case 0: {
                return this.privGetFileName(this.actionIdentity, this.actionStub, this.actionErrorOK, this.actionTryAlternatePath);
            }
            case 1: {
                final StorageFile privGetFileName = this.privGetFileName(this.actionIdentity, false, false, false);
                try {
                    if (privGetFileName.exists()) {
                        throw StandardException.newException("XSDF0.S", privGetFileName);
                    }
                }
                catch (SecurityException ex) {
                    throw StandardException.newException("XSDF1.S", ex, privGetFileName);
                }
                try {
                    this.dataFactory.writeInProgress();
                    try {
                        this.fileData = privGetFileName.getRandomAccessFile("rw");
                        privGetFileName.limitAccessToOwner();
                    }
                    finally {
                        this.dataFactory.writeFinished();
                    }
                    this.canUpdate = true;
                    this.writeRAFHeader(this.actionIdentity, this.fileData, true, this.actionIdentity.getSegmentId() != -1L);
                }
                catch (IOException ex2) {
                    this.canUpdate = false;
                    boolean privRemoveFile;
                    try {
                        privRemoveFile = this.privRemoveFile(privGetFileName);
                    }
                    catch (SecurityException ex3) {
                        throw StandardException.newException("XSDF2.S", ex2, privGetFileName, ex3.toString());
                    }
                    if (!privRemoveFile) {
                        throw StandardException.newException("XSDF2.S", ex2, privGetFileName, ex2.toString());
                    }
                    throw StandardException.newException("XSDF1.S", ex2, privGetFileName);
                }
                return null;
            }
            case 2: {
                return this.privRemoveFile(this.actionFile) ? this : null;
            }
            case 3: {
                boolean b = false;
                StorageFile storageFile = this.privGetFileName(this.actionIdentity, false, true, true);
                if (storageFile == null) {
                    return null;
                }
                try {
                    if (!storageFile.exists()) {
                        storageFile = this.privGetFileName(this.actionIdentity, true, true, true);
                        if (!storageFile.exists()) {
                            return null;
                        }
                        b = true;
                    }
                }
                catch (SecurityException ex4) {
                    throw StandardException.newException("XSDA4.S", ex4);
                }
                this.canUpdate = false;
                try {
                    if (!this.dataFactory.isReadOnly() && storageFile.canWrite()) {
                        this.canUpdate = true;
                    }
                }
                catch (SecurityException ex13) {}
                this.fileName = storageFile.toString();
                try {
                    this.fileData = storageFile.getRandomAccessFile(this.canUpdate ? "rw" : "r");
                    this.readHeader(this.getEmbryonicPage(this.fileData, 0L));
                }
                catch (IOException ex5) {
                    if (b) {
                        throw this.dataFactory.markCorrupt(StandardException.newException("XSDG3.D", ex5, (this.getIdentity() != null) ? this.getIdentity().toString() : "unknown", "read", this.fileName));
                    }
                    final StorageFile privGetFileName2 = this.privGetFileName(this.actionIdentity, true, true, true);
                    if (privGetFileName2.exists()) {
                        try {
                            this.privRemoveFile(storageFile);
                            this.fileData = privGetFileName2.getRandomAccessFile(this.canUpdate ? "rw" : "r");
                            this.readHeader(this.getEmbryonicPage(this.fileData, 0L));
                            return this;
                        }
                        catch (IOException ex6) {
                            throw this.dataFactory.markCorrupt(StandardException.newException("XSDG3.D", ex6, (this.getIdentity() != null) ? this.getIdentity().toString() : "unknown", "delete-stub", this.fileName));
                        }
                    }
                    throw this.dataFactory.markCorrupt(StandardException.newException("XSDG3.D", ex5, (this.getIdentity() != null) ? this.getIdentity().toString() : "unknown", "read", this.fileName));
                }
                return this;
            }
            case 7: {
                final StorageFile privGetFileName3 = this.privGetFileName(this.actionIdentity, false, true, true);
                synchronized (this) {
                    try {
                        this.fileData = privGetFileName3.getRandomAccessFile(this.canUpdate ? "rw" : "r");
                    }
                    catch (FileNotFoundException ex7) {
                        throw this.dataFactory.markCorrupt(StandardException.newException("XSDG3.D", ex7, (this.getIdentity() != null) ? this.getIdentity().toString() : "unknown", "read", this.fileName));
                    }
                }
                return this;
            }
            case 4: {
                final StorageFile privGetFileName4 = this.privGetFileName(this.actionIdentity, false, false, true);
                final StorageFile privGetFileName5 = this.privGetFileName(this.actionIdentity, true, false, false);
                StorageRandomAccessFile randomAccessFile = null;
                try {
                    if (!privGetFileName5.exists()) {
                        randomAccessFile = privGetFileName5.getRandomAccessFile("rw");
                        privGetFileName5.limitAccessToOwner();
                        this.writeRAFHeader(this.actionIdentity, randomAccessFile, true, true);
                        randomAccessFile.close();
                        randomAccessFile = null;
                    }
                    this.dataFactory.flush(this.actionInstant);
                    this.privRemoveFile(privGetFileName4);
                }
                catch (SecurityException ex8) {
                    throw StandardException.newException("XSDF4.S", ex8, privGetFileName4, ex8.toString());
                }
                catch (IOException ex10) {
                    try {
                        if (randomAccessFile != null) {
                            randomAccessFile.close();
                            privGetFileName5.delete();
                        }
                        if (this.fileData != null) {
                            this.fileData.close();
                            this.fileData = null;
                        }
                    }
                    catch (IOException ex9) {
                        throw StandardException.newException("XSDF4.S", ex9, privGetFileName4, ex10.toString());
                    }
                    catch (SecurityException ex11) {
                        throw StandardException.newException("XSDF4.S", ex11, privGetFileName4, ex11.toString());
                    }
                }
                this.dataFactory.stubFileToRemoveAfterCheckPoint(privGetFileName5, this.actionInstant, this.getIdentity());
                return null;
            }
            case 6: {
                try {
                    final boolean exists = this.actionFile.exists();
                    final StorageRandomAccessFile randomAccessFile2 = this.actionFile.getRandomAccessFile("rw");
                    if (!exists) {
                        this.actionFile.limitAccessToOwner();
                    }
                    return randomAccessFile2;
                }
                catch (FileNotFoundException ex12) {
                    throw StandardException.newException("XSDF1.S", ex12, this.actionFile.getPath());
                }
                break;
            }
        }
        return null;
    }
}
