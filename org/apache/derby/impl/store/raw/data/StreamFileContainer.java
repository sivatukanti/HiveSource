// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.util.ReuseFactory;
import java.io.FileNotFoundException;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.io.InvalidClassException;
import java.io.EOFException;
import java.io.Externalizable;
import java.io.ObjectOutput;
import org.apache.derby.iapi.services.io.StreamStorable;
import org.apache.derby.iapi.services.io.Storable;
import org.apache.derby.iapi.services.io.CompressedNumber;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.store.access.RowSource;
import org.apache.derby.iapi.store.raw.StreamContainerHandle;
import org.apache.derby.iapi.services.property.PersistentSet;
import org.apache.derby.iapi.services.property.PropertyUtil;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.store.access.AccessFactory;
import java.io.IOException;
import java.io.ObjectInput;
import org.apache.derby.iapi.store.raw.data.DataFactory;
import org.apache.derby.iapi.error.StandardException;
import java.util.Properties;
import org.apache.derby.iapi.services.io.FormatIdInputStream;
import org.apache.derby.iapi.services.io.LimitInputStream;
import java.io.BufferedInputStream;
import java.io.InputStream;
import org.apache.derby.iapi.services.io.FormatIdOutputStream;
import org.apache.derby.iapi.services.io.DynamicByteArrayOutputStream;
import java.io.OutputStream;
import org.apache.derby.io.StorageFile;
import org.apache.derby.iapi.store.raw.ContainerKey;
import java.security.PrivilegedExceptionAction;
import org.apache.derby.iapi.services.io.TypedFormat;

public class StreamFileContainer implements TypedFormat, PrivilegedExceptionAction
{
    protected static int formatIdInteger;
    protected static final int LARGE_SLOT_SIZE = 4;
    protected static final int MIN_BUFFER_SIZE = 1024;
    protected static final int FIELD_STATUS;
    protected static final int FIELD_HEADER_SIZE;
    protected ContainerKey identity;
    private BaseDataFileFactory dataFactory;
    private int bufferSize;
    private StorageFile file;
    private OutputStream fileOut;
    private DynamicByteArrayOutputStream out;
    private FormatIdOutputStream logicalDataOut;
    private InputStream fileIn;
    private BufferedInputStream bufferedIn;
    private DecryptInputStream decryptIn;
    private LimitInputStream limitIn;
    private FormatIdInputStream logicalDataIn;
    private StoredRecordHeader recordHeader;
    private byte[] ciphertext;
    private byte[] zeroBytes;
    private static final int STORAGE_FILE_EXISTS_ACTION = 1;
    private static final int STORAGE_FILE_DELETE_ACTION = 2;
    private static final int STORAGE_FILE_MKDIRS_ACTION = 3;
    private static final int STORAGE_FILE_GET_OUTPUT_STREAM_ACTION = 4;
    private static final int STORAGE_FILE_GET_INPUT_STREAM_ACTION = 5;
    private int actionCode;
    private StorageFile actionStorageFile;
    
    StreamFileContainer(final ContainerKey identity, final BaseDataFileFactory dataFactory) {
        this.identity = identity;
        this.dataFactory = dataFactory;
    }
    
    StreamFileContainer(final ContainerKey identity, final BaseDataFileFactory dataFactory, final Properties properties) throws StandardException {
        this.identity = identity;
        this.dataFactory = dataFactory;
        try {
            this.file = this.getFileName(identity, true, false);
            if (this.privExists(this.file)) {
                throw StandardException.newException("XSDF0.S", this.file);
            }
            this.getContainerProperties(properties);
        }
        catch (SecurityException ex) {
            throw StandardException.newException("XSDF1.S", ex, this.file);
        }
    }
    
    protected StreamFileContainer open(final boolean b) throws StandardException {
        this.file = this.getFileName(this.identity, false, true);
        if (!this.privExists(this.file)) {
            return null;
        }
        try {
            if (b) {
                return null;
            }
            this.fileIn = this.privGetInputStream(this.file);
            if (this.dataFactory.databaseEncrypted()) {
                this.decryptIn = new DecryptInputStream(this.fileIn, new MemByteHolder(16384), this.dataFactory);
                this.limitIn = new LimitInputStream(this.decryptIn);
            }
            else {
                this.bufferedIn = new BufferedInputStream(this.fileIn, 16384);
                this.limitIn = new LimitInputStream(this.bufferedIn);
            }
            this.logicalDataIn = new FormatIdInputStream(this.limitIn);
            (this.recordHeader = new StoredRecordHeader()).read(this.logicalDataIn);
        }
        catch (IOException ex) {
            throw StandardException.newException("XSDF1.S", ex, this.file);
        }
        return this;
    }
    
    protected void close() {
        try {
            if (this.fileIn != null) {
                this.fileIn.close();
                this.fileIn = null;
                if (this.dataFactory.databaseEncrypted()) {
                    this.decryptIn.close();
                    this.decryptIn = null;
                }
                else {
                    this.bufferedIn.close();
                    this.bufferedIn = null;
                }
                this.logicalDataIn.close();
                this.logicalDataIn = null;
            }
            if (this.fileOut != null) {
                this.fileOut.close();
                this.logicalDataOut.close();
                this.fileOut = null;
                this.logicalDataOut = null;
                this.out = null;
            }
        }
        catch (IOException ex) {}
    }
    
    public int getTypeFormatId() {
        return 290;
    }
    
    public void getContainerProperties(final Properties properties) throws StandardException {
        final AccessFactory accessFactory = (AccessFactory)Monitor.getServiceModule(this.dataFactory, "org.apache.derby.iapi.store.access.AccessFactory");
        this.bufferSize = PropertyUtil.getServiceInt((accessFactory == null) ? null : accessFactory.getTransaction(ContextService.getFactory().getCurrentContextManager()), properties, "derby.storage.streamFileBufferSize", 1024, Integer.MAX_VALUE, 16384);
    }
    
    public ContainerKey getIdentity() {
        return this.identity;
    }
    
    protected boolean use(final StreamContainerHandle streamContainerHandle) throws StandardException {
        return true;
    }
    
    public void load(final RowSource rowSource) throws StandardException {
        this.out = new DynamicByteArrayOutputStream(this.bufferSize);
        this.logicalDataOut = new FormatIdOutputStream(this.out);
        final boolean databaseEncrypted = this.dataFactory.databaseEncrypted();
        if (databaseEncrypted) {
            if (this.zeroBytes == null) {
                this.zeroBytes = new byte[this.dataFactory.getEncryptionBlockSize() - 1];
            }
            this.out.write(this.zeroBytes, 0, this.dataFactory.getEncryptionBlockSize() - 1);
        }
        try {
            this.fileOut = this.privGetOutputStream(this.file);
            final FormatableBitSet validColumns = rowSource.getValidColumns();
            DataValueDescriptor[] array = rowSource.getNextRowFromRowSource();
            int length = 0;
            if (validColumns != null) {
                for (int i = validColumns.getLength() - 1; i >= 0; --i) {
                    if (validColumns.isSet(i)) {
                        length = i + 1;
                        break;
                    }
                }
            }
            else {
                length = array.length;
            }
            (this.recordHeader = new StoredRecordHeader(0, length)).write(this.out);
            final int n = (validColumns == null) ? 0 : validColumns.getLength();
            while (array != null) {
                int n2 = -1;
                for (int j = 0; j < length; ++j) {
                    if (validColumns == null) {
                        ++n2;
                        this.writeColumn(array[n2]);
                    }
                    else if (n > j && validColumns.isSet(j)) {
                        ++n2;
                        this.writeColumn(array[n2]);
                    }
                    else {
                        this.writeColumn(null);
                    }
                    if (this.out.getUsed() >= this.bufferSize || this.bufferSize - this.out.getUsed() < 1024) {
                        this.writeToFile();
                    }
                }
                array = rowSource.getNextRowFromRowSource();
            }
            if (databaseEncrypted) {
                if (this.out.getUsed() > this.dataFactory.getEncryptionBlockSize() - 1) {
                    this.writeToFile();
                }
            }
            else if (this.out.getUsed() > 0) {
                this.writeToFile();
            }
        }
        catch (IOException ex) {
            throw StandardException.newException("XSDA4.S", ex);
        }
        finally {
            this.close();
        }
    }
    
    private void writeToFile() throws StandardException {
        try {
            if (this.dataFactory.databaseEncrypted()) {
                final int n = this.out.getUsed() - (this.dataFactory.getEncryptionBlockSize() - 1);
                final int n2 = n % this.dataFactory.getEncryptionBlockSize();
                final int n3 = (n2 == 0) ? 0 : (this.dataFactory.getEncryptionBlockSize() - n2);
                final int n4 = (n2 == 0) ? (this.dataFactory.getEncryptionBlockSize() - 1) : (n2 - 1);
                final int len = n + n3;
                if (n <= 0) {
                    return;
                }
                if (this.ciphertext == null) {
                    this.ciphertext = new byte[len];
                }
                else if (this.ciphertext.length < len) {
                    this.ciphertext = new byte[len];
                }
                this.dataFactory.encrypt(this.out.getByteArray(), n4, len, this.ciphertext, 0, false);
                CompressedNumber.writeInt(this.fileOut, n);
                this.dataFactory.writeInProgress();
                try {
                    this.fileOut.write(this.ciphertext, 0, len);
                }
                finally {
                    this.dataFactory.writeFinished();
                }
                this.out.reset();
                if (this.dataFactory.databaseEncrypted()) {
                    if (this.zeroBytes == null) {
                        this.zeroBytes = new byte[this.dataFactory.getEncryptionBlockSize() - 1];
                    }
                    this.out.write(this.zeroBytes, 0, this.dataFactory.getEncryptionBlockSize() - 1);
                }
            }
            else {
                if (this.out.getUsed() == 0) {
                    return;
                }
                this.dataFactory.writeInProgress();
                try {
                    this.fileOut.write(this.out.getByteArray(), 0, this.out.getUsed());
                }
                finally {
                    this.dataFactory.writeFinished();
                }
                this.out.reset();
            }
        }
        catch (IOException ex) {
            throw StandardException.newException("XSDA4.S", ex);
        }
    }
    
    private void writeColumn(Object returnStream) throws StandardException, IOException {
        final int field_STATUS = StreamFileContainer.FIELD_STATUS;
        if (returnStream == null) {
            StoredFieldHeader.write(this.out, StoredFieldHeader.setNonexistent(field_STATUS), 0, 4);
            return;
        }
        if (returnStream instanceof Storable && ((Storable)returnStream).isNull()) {
            StoredFieldHeader.write(this.out, StoredFieldHeader.setNull(field_STATUS, true), 0, 4);
            return;
        }
        final int position = this.out.getPosition();
        int n = 0;
        StoredFieldHeader.write(this.out, field_STATUS, n, 4);
        if (returnStream instanceof StreamStorable && ((StreamStorable)returnStream).returnStream() != null) {
            returnStream = ((StreamStorable)returnStream).returnStream();
        }
        if (returnStream instanceof InputStream) {
            final InputStream inputStream = (InputStream)returnStream;
            final byte[] b = new byte[Math.min(Math.max(inputStream.available(), 64), 8192)];
            while (true) {
                final int read = inputStream.read(b);
                if (read == -1) {
                    break;
                }
                n += read;
                this.out.write(b, 0, read);
            }
        }
        else if (returnStream instanceof Storable) {
            ((Storable)returnStream).writeExternal(this.logicalDataOut);
            n = this.out.getPosition() - position - StreamFileContainer.FIELD_HEADER_SIZE;
        }
        else {
            this.logicalDataOut.writeObject(returnStream);
            n = this.out.getPosition() - position - StreamFileContainer.FIELD_HEADER_SIZE;
        }
        final int position2 = this.out.getPosition();
        this.out.setPosition(position);
        StoredFieldHeader.write(this.out, field_STATUS, n, 4);
        if (!StoredFieldHeader.isNull(field_STATUS)) {
            this.out.setPosition(position2);
        }
    }
    
    public boolean fetchNext(final Object[] array) throws StandardException {
        boolean b = false;
        int n = 0;
        try {
            for (final int numberFields = this.recordHeader.getNumberFields(), int n2 = 0, n = 0; n < numberFields && n2 < array.length; ++n) {
                this.limitIn.clearLimit();
                final int status = StoredFieldHeader.readStatus(this.logicalDataIn);
                this.limitIn.setLimit(StoredFieldHeader.readFieldDataLength(this.logicalDataIn, status, 4));
                final Object o = array[n2];
                if (StoredFieldHeader.isNullable(status)) {
                    if (o == null) {
                        throw StandardException.newException("XSDA6.S", Integer.toString(n));
                    }
                    if (!(o instanceof Storable)) {
                        throw StandardException.newException("XSDA6.S", ((Storable)o).getClass().getName());
                    }
                    final Storable storable = (Storable)o;
                    if (StoredFieldHeader.isNull(status)) {
                        storable.restoreToNull();
                        ++n2;
                    }
                    else {
                        b = true;
                        storable.readExternal(this.logicalDataIn);
                        b = false;
                        ++n2;
                    }
                }
                else {
                    if (StoredFieldHeader.isNull(status)) {
                        throw StandardException.newException("XSDA6.S", Integer.toString(n));
                    }
                    final Object o2 = array[n2];
                    if (o2 instanceof Externalizable) {
                        final Externalizable externalizable = (Externalizable)o2;
                        b = true;
                        externalizable.readExternal(this.logicalDataIn);
                        b = false;
                        ++n2;
                    }
                    else {
                        b = true;
                        array[n2] = this.logicalDataIn.readObject();
                        b = false;
                        ++n2;
                    }
                }
            }
        }
        catch (IOException ex) {
            if (b) {
                if (ex instanceof EOFException) {
                    throw StandardException.newException("XSDA7.S", ex, this.logicalDataIn.getErrorInfo());
                }
                throw StandardException.newException("XSDA8.S", ex, this.logicalDataIn.getErrorInfo());
            }
            else {
                if (ex instanceof InvalidClassException) {
                    throw StandardException.newException("XSDA8.S", ex, this.logicalDataIn.getErrorInfo());
                }
                if (ex instanceof EOFException && n == 0) {
                    this.close();
                    return false;
                }
                throw this.dataFactory.markCorrupt(StandardException.newException("XSDB9.D", ex, this.identity));
            }
        }
        catch (ClassNotFoundException ex2) {
            throw StandardException.newException("XSDA9.S", ex2, this.logicalDataIn.getErrorInfo());
        }
        catch (LinkageError linkageError) {
            if (b) {
                throw StandardException.newException("XSDA8.S", linkageError, this.logicalDataIn.getErrorInfo());
            }
            throw linkageError;
        }
        return true;
    }
    
    public boolean removeContainer() throws StandardException {
        this.close();
        return !this.privExists(this.file) || this.privDelete(this.file);
    }
    
    protected StorageFile getFileName(final ContainerKey containerKey, final boolean b, final boolean b2) throws StandardException {
        if (containerKey.getSegmentId() == -1L) {
            return this.dataFactory.storageFactory.newStorageFile(this.dataFactory.storageFactory.getTempDir(), "T" + containerKey.getContainerId() + ".tmp");
        }
        final StorageFile containerPath = this.dataFactory.getContainerPath(containerKey, false);
        if (!this.privExists(containerPath)) {
            if (!b) {
                return null;
            }
            final StorageFile parentDir = containerPath.getParentDir();
            if (!this.privExists(parentDir)) {
                synchronized (this.dataFactory) {
                    if (!this.privExists(parentDir) && !this.privMkdirs(parentDir)) {
                        if (b2) {
                            return null;
                        }
                        throw StandardException.newException("XSDF3.S", parentDir);
                    }
                }
            }
        }
        return containerPath;
    }
    
    private synchronized boolean privExists(final StorageFile actionStorageFile) {
        this.actionCode = 1;
        this.actionStorageFile = actionStorageFile;
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Boolean>)this);
        }
        catch (PrivilegedActionException ex) {
            return false;
        }
        finally {
            this.actionStorageFile = null;
        }
    }
    
    private synchronized boolean privMkdirs(final StorageFile actionStorageFile) {
        this.actionCode = 3;
        this.actionStorageFile = actionStorageFile;
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Boolean>)this);
        }
        catch (PrivilegedActionException ex) {
            return false;
        }
        finally {
            this.actionStorageFile = null;
        }
    }
    
    private synchronized boolean privDelete(final StorageFile actionStorageFile) {
        this.actionCode = 2;
        this.actionStorageFile = actionStorageFile;
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Boolean>)this);
        }
        catch (PrivilegedActionException ex) {
            return false;
        }
        finally {
            this.actionStorageFile = null;
        }
    }
    
    private synchronized OutputStream privGetOutputStream(final StorageFile actionStorageFile) throws FileNotFoundException {
        this.actionCode = 4;
        this.actionStorageFile = actionStorageFile;
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<OutputStream>)this);
        }
        catch (PrivilegedActionException ex) {
            throw (FileNotFoundException)ex.getException();
        }
        finally {
            this.actionStorageFile = null;
        }
    }
    
    private synchronized InputStream privGetInputStream(final StorageFile actionStorageFile) throws FileNotFoundException {
        this.actionCode = 5;
        this.actionStorageFile = actionStorageFile;
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<InputStream>)this);
        }
        catch (PrivilegedActionException ex) {
            throw (FileNotFoundException)ex.getException();
        }
        finally {
            this.actionStorageFile = null;
        }
    }
    
    public Object run() throws FileNotFoundException {
        switch (this.actionCode) {
            case 1: {
                return ReuseFactory.getBoolean(this.actionStorageFile.exists());
            }
            case 2: {
                return ReuseFactory.getBoolean(this.actionStorageFile.delete());
            }
            case 3: {
                final boolean mkdirs = this.actionStorageFile.mkdirs();
                this.actionStorageFile.limitAccessToOwner();
                return ReuseFactory.getBoolean(mkdirs);
            }
            case 4: {
                return this.actionStorageFile.getOutputStream();
            }
            case 5: {
                return this.actionStorageFile.getInputStream();
            }
            default: {
                return null;
            }
        }
    }
    
    static {
        StreamFileContainer.formatIdInteger = 290;
        FIELD_STATUS = StoredFieldHeader.setFixed(StoredFieldHeader.setInitial(), true);
        FIELD_HEADER_SIZE = StoredFieldHeader.size(StreamFileContainer.FIELD_STATUS, 0, 4);
    }
}
