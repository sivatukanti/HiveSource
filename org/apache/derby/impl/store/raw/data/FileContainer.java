// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.store.raw.Page;
import org.apache.derby.iapi.util.InterruptDetectedException;
import org.apache.derby.iapi.util.InterruptStatus;
import org.apache.derby.iapi.util.Matchable;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.services.property.PersistentSet;
import org.apache.derby.iapi.services.property.PropertyUtil;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.store.access.AccessFactory;
import java.io.OutputStream;
import org.apache.derby.iapi.services.io.FormatIdOutputStream;
import org.apache.derby.iapi.services.io.ArrayOutputStream;
import java.io.DataInput;
import org.apache.derby.io.StorageRandomAccessFile;
import org.apache.derby.iapi.util.StringUtil;
import org.apache.derby.iapi.store.raw.PageKey;
import org.apache.derby.iapi.services.io.ArrayInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.derby.iapi.util.ByteArray;
import org.apache.derby.iapi.store.raw.ContainerKey;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.SpaceInfo;
import java.util.zip.CRC32;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.iapi.services.cache.CacheManager;
import org.apache.derby.iapi.services.io.TypedFormat;
import org.apache.derby.iapi.services.cache.Cacheable;

abstract class FileContainer extends BaseContainer implements Cacheable, TypedFormat
{
    protected static final int formatIdInteger = 116;
    protected final CacheManager pageCache;
    protected final CacheManager containerCache;
    protected final BaseDataFileFactory dataFactory;
    protected int pageSize;
    protected int spareSpace;
    protected int minimumRecordSize;
    protected short initialPages;
    protected boolean canUpdate;
    private int PreAllocThreshold;
    private int PreAllocSize;
    private boolean bulkIncreaseContainerSize;
    private static final int PRE_ALLOC_THRESHOLD = 8;
    private static final int MIN_PRE_ALLOC_SIZE = 1;
    private static final int DEFAULT_PRE_ALLOC_SIZE = 8;
    private static final int MAX_PRE_ALLOC_SIZE = 1000;
    protected long firstAllocPageNumber;
    protected long firstAllocPageOffset;
    protected long containerVersion;
    protected long estimatedRowCount;
    protected LogInstant lastLogInstant;
    private long reusableRecordIdSequenceNumber;
    private long[] lastInsertedPage;
    private int lastInsertedPage_index;
    private long lastUnfilledPage;
    private long lastAllocatedPage;
    private long estimatedPageCount;
    protected boolean preDirty;
    protected boolean isDirty;
    protected AllocationCache allocCache;
    byte[] containerInfo;
    private CRC32 checksum;
    private byte[] encryptionBuffer;
    private static final int CONTAINER_FORMAT_ID_SIZE = 4;
    protected static final int CHECKSUM_SIZE = 8;
    protected static final int CONTAINER_INFO_SIZE = 80;
    public static final long FIRST_ALLOC_PAGE_NUMBER = 0L;
    public static final long FIRST_ALLOC_PAGE_OFFSET = 0L;
    private static final int FILE_DROPPED = 1;
    private static final int FILE_COMMITTED_DROP = 2;
    private static final int FILE_REUSABLE_RECORDID = 8;
    protected static final String SPACE_TRACE;
    
    public int getTypeFormatId() {
        return 116;
    }
    
    FileContainer(final BaseDataFileFactory dataFactory) {
        this.dataFactory = dataFactory;
        this.pageCache = dataFactory.getPageCache();
        this.containerCache = dataFactory.getContainerCache();
        this.initContainerHeader(true);
    }
    
    public SpaceInfo getSpaceInfo(final BaseContainerHandle baseContainerHandle) throws StandardException {
        final SpaceInformation allPageCounts;
        synchronized (this.allocCache) {
            allPageCounts = this.allocCache.getAllPageCounts(baseContainerHandle, this.firstAllocPageNumber);
        }
        allPageCounts.setPageSize(this.pageSize);
        return allPageCounts;
    }
    
    public Cacheable setIdentity(final Object o) throws StandardException {
        final ContainerKey containerKey = (ContainerKey)o;
        if (containerKey.getSegmentId() == -1L) {
            return new TempRAFContainer(this.dataFactory).setIdent(containerKey);
        }
        return this.setIdent(containerKey);
    }
    
    protected Cacheable setIdent(final ContainerKey containerKey) throws StandardException {
        final boolean openContainer = this.openContainer(containerKey);
        this.initializeLastInsertedPage(1);
        this.lastUnfilledPage = -1L;
        this.lastAllocatedPage = -1L;
        this.estimatedPageCount = -1L;
        if (openContainer) {
            this.fillInIdentity(containerKey);
            return this;
        }
        return null;
    }
    
    public Cacheable createIdentity(final Object o, final Object o2) throws StandardException {
        final ContainerKey containerKey = (ContainerKey)o;
        if (containerKey.getSegmentId() == -1L) {
            return new TempRAFContainer(this.dataFactory).createIdent(containerKey, o2);
        }
        return this.createIdent(containerKey, o2);
    }
    
    protected Cacheable createIdent(final ContainerKey containerKey, final Object o) throws StandardException {
        if (o != this) {
            this.initContainerHeader(true);
            if (o != null && o instanceof ByteArray) {
                this.createInfoFromLog((ByteArray)o);
            }
            else {
                this.createInfoFromProp((Properties)o);
            }
        }
        else {
            this.initContainerHeader(false);
        }
        if (this.initialPages > 1) {
            this.PreAllocThreshold = 0;
            this.PreAllocSize = this.initialPages;
            this.bulkIncreaseContainerSize = true;
        }
        else {
            this.PreAllocThreshold = 8;
        }
        this.createContainer(containerKey);
        this.setDirty(true);
        this.fillInIdentity(containerKey);
        return this;
    }
    
    public void clearIdentity() {
        this.closeContainer();
        this.initializeLastInsertedPage(1);
        this.lastUnfilledPage = -1L;
        this.lastAllocatedPage = -1L;
        this.canUpdate = false;
        super.clearIdentity();
    }
    
    public boolean isDirty() {
        synchronized (this) {
            return this.isDirty;
        }
    }
    
    public void preDirty(final boolean b) {
        synchronized (this) {
            if (b) {
                this.preDirty = true;
            }
            else {
                this.preDirty = false;
                this.notifyAll();
            }
        }
    }
    
    protected void setDirty(final boolean isDirty) {
        synchronized (this) {
            this.preDirty = false;
            this.isDirty = isDirty;
            this.notifyAll();
        }
    }
    
    abstract void createContainer(final ContainerKey p0) throws StandardException;
    
    abstract boolean openContainer(final ContainerKey p0) throws StandardException;
    
    abstract void closeContainer();
    
    protected void dropContainer(final LogInstant logInstant, final boolean droppedState) {
        synchronized (this) {
            this.setDroppedState(droppedState);
            this.setDirty(true);
            this.bumpContainerVersion(logInstant);
        }
    }
    
    protected final void bumpContainerVersion(final LogInstant lastLogInstant) {
        this.lastLogInstant = lastLogInstant;
        ++this.containerVersion;
    }
    
    protected long getContainerVersion() {
        synchronized (this) {
            return this.containerVersion;
        }
    }
    
    public void getContainerProperties(final Properties properties) throws StandardException {
        if (properties.getProperty("derby.storage.pageSize") != null) {
            properties.put("derby.storage.pageSize", Integer.toString(this.pageSize));
        }
        if (properties.getProperty("derby.storage.minimumRecordSize") != null) {
            properties.put("derby.storage.minimumRecordSize", Integer.toString(this.minimumRecordSize));
        }
        if (properties.getProperty("derby.storage.pageReservedSpace") != null) {
            properties.put("derby.storage.pageReservedSpace", Integer.toString(this.spareSpace));
        }
        if (properties.getProperty("derby.storage.reusableRecordId") != null) {
            properties.put("derby.storage.reusableRecordId", new Boolean(this.isReusableRecordId()).toString());
        }
        if (properties.getProperty("derby.storage.initialPages") != null) {
            properties.put("derby.storage.initialPages", Integer.toString(this.initialPages));
        }
    }
    
    protected void readHeader(final byte[] array) throws IOException, StandardException {
        AllocPage.ReadContainerInfo(this.containerInfo, array);
        this.readHeaderFromArray(this.containerInfo);
    }
    
    private void initContainerHeader(final boolean b) {
        if (this.containerInfo == null) {
            this.containerInfo = new byte[80];
        }
        if (this.checksum == null) {
            this.checksum = new CRC32();
        }
        else {
            this.checksum.reset();
        }
        if (this.allocCache == null) {
            this.allocCache = new AllocationCache();
        }
        else {
            this.allocCache.reset();
        }
        if (b) {
            this.pageSize = 0;
            this.spareSpace = 0;
            this.minimumRecordSize = 0;
        }
        this.initialPages = 1;
        this.firstAllocPageNumber = -1L;
        this.firstAllocPageOffset = -1L;
        this.containerVersion = 0L;
        this.estimatedRowCount = 0L;
        this.reusableRecordIdSequenceNumber = 0L;
        this.setDroppedState(false);
        this.setCommittedDropState(false);
        this.setReusableRecordIdState(false);
        this.lastLogInstant = null;
        this.initializeLastInsertedPage(1);
        this.lastUnfilledPage = -1L;
        this.lastAllocatedPage = -1L;
        this.estimatedPageCount = -1L;
        this.PreAllocThreshold = 8;
        this.PreAllocSize = 8;
        this.bulkIncreaseContainerSize = false;
    }
    
    private void readHeaderFromArray(final byte[] b) throws StandardException, IOException {
        final ArrayInputStream arrayInputStream = new ArrayInputStream(b);
        arrayInputStream.setLimit(80);
        final int int1 = arrayInputStream.readInt();
        if (int1 != 116) {
            throw StandardException.newException("XSDB2.D", this.getIdentity(), new Long(int1));
        }
        final int int2 = arrayInputStream.readInt();
        this.pageSize = arrayInputStream.readInt();
        this.spareSpace = arrayInputStream.readInt();
        this.minimumRecordSize = arrayInputStream.readInt();
        this.initialPages = arrayInputStream.readShort();
        this.PreAllocSize = arrayInputStream.readShort();
        this.firstAllocPageNumber = arrayInputStream.readLong();
        this.firstAllocPageOffset = arrayInputStream.readLong();
        this.containerVersion = arrayInputStream.readLong();
        this.estimatedRowCount = arrayInputStream.readLong();
        this.reusableRecordIdSequenceNumber = arrayInputStream.readLong();
        this.lastLogInstant = null;
        if (this.PreAllocSize == 0) {
            this.PreAllocSize = 8;
        }
        arrayInputStream.readLong();
        if (this.initialPages == 0) {
            this.initialPages = 1;
        }
        this.PreAllocThreshold = 8;
        final long long1 = arrayInputStream.readLong();
        this.checksum.reset();
        this.checksum.update(b, 0, 72);
        if (long1 != this.checksum.getValue()) {
            throw this.dataFactory.markCorrupt(StandardException.newException("XSDG2.D", new PageKey(this.identity, 0L), new Long(this.checksum.getValue()), new Long(long1), StringUtil.hexDump(b)));
        }
        this.allocCache.reset();
        this.setDroppedState((int2 & 0x1) != 0x0);
        this.setCommittedDropState((int2 & 0x2) != 0x0);
        this.setReusableRecordIdState((int2 & 0x8) != 0x0);
    }
    
    protected void writeHeader(final Object o, final byte[] array) throws StandardException, IOException {
        this.writeHeaderToArray(this.containerInfo);
        try {
            AllocPage.WriteContainerInfo(this.containerInfo, array, false);
        }
        catch (StandardException ex) {
            throw StandardException.newException("XSDBC.D", ex, o);
        }
    }
    
    protected void writeHeader(final Object o, final StorageRandomAccessFile storageRandomAccessFile, final boolean b, final byte[] array) throws IOException, StandardException {
        this.writeHeaderToArray(this.containerInfo);
        try {
            AllocPage.WriteContainerInfo(this.containerInfo, array, b);
        }
        catch (StandardException ex) {
            throw StandardException.newException("XSDBC.D", ex, o);
        }
        this.dataFactory.flush(this.lastLogInstant);
        if (this.lastLogInstant != null) {
            this.lastLogInstant = null;
        }
        this.dataFactory.writeInProgress();
        try {
            this.writeAtOffset(storageRandomAccessFile, array, 0L);
        }
        finally {
            this.dataFactory.writeFinished();
        }
    }
    
    void writeAtOffset(final StorageRandomAccessFile storageRandomAccessFile, final byte[] array, final long n) throws IOException, StandardException {
        storageRandomAccessFile.seek(n);
        storageRandomAccessFile.write(array);
    }
    
    protected byte[] getEmbryonicPage(final DataInput dataInput) throws IOException, StandardException {
        final byte[] array = new byte[204];
        dataInput.readFully(array);
        return array;
    }
    
    byte[] getEmbryonicPage(final StorageRandomAccessFile storageRandomAccessFile, final long n) throws IOException, StandardException {
        storageRandomAccessFile.seek(n);
        return this.getEmbryonicPage(storageRandomAccessFile);
    }
    
    private void writeHeaderToArray(final byte[] b) throws IOException {
        final ArrayOutputStream arrayOutputStream = new ArrayOutputStream(b);
        final FormatIdOutputStream formatIdOutputStream = new FormatIdOutputStream(arrayOutputStream);
        int v = 0;
        if (this.getDroppedState()) {
            v |= 0x1;
        }
        if (this.getCommittedDropState()) {
            v |= 0x2;
        }
        if (this.isReusableRecordId()) {
            v |= 0x8;
        }
        arrayOutputStream.setPosition(0);
        arrayOutputStream.setLimit(80);
        formatIdOutputStream.writeInt(116);
        formatIdOutputStream.writeInt(v);
        formatIdOutputStream.writeInt(this.pageSize);
        formatIdOutputStream.writeInt(this.spareSpace);
        formatIdOutputStream.writeInt(this.minimumRecordSize);
        formatIdOutputStream.writeShort(this.initialPages);
        formatIdOutputStream.writeShort(this.PreAllocSize);
        formatIdOutputStream.writeLong(this.firstAllocPageNumber);
        formatIdOutputStream.writeLong(this.firstAllocPageOffset);
        formatIdOutputStream.writeLong(this.containerVersion);
        formatIdOutputStream.writeLong(this.estimatedRowCount);
        formatIdOutputStream.writeLong(this.reusableRecordIdSequenceNumber);
        formatIdOutputStream.writeLong(0L);
        this.checksum.reset();
        this.checksum.update(b, 0, 72);
        formatIdOutputStream.writeLong(this.checksum.getValue());
        arrayOutputStream.clearLimit();
    }
    
    protected ByteArray logCreateContainerInfo() throws StandardException {
        final byte[] array = new byte[80];
        try {
            this.writeHeaderToArray(array);
        }
        catch (IOException ex) {
            throw StandardException.newException("XSDA4.S", ex);
        }
        return new ByteArray(array);
    }
    
    private void createInfoFromLog(final ByteArray byteArray) throws StandardException {
        final ArrayInputStream arrayInputStream = new ArrayInputStream(byteArray.getArray());
        int int2;
        try {
            arrayInputStream.setLimit(80);
            final int int1 = arrayInputStream.readInt();
            if (int1 != 116) {
                throw StandardException.newException("XSDB2.D", this.getIdentity(), new Long(int1));
            }
            int2 = arrayInputStream.readInt();
            this.pageSize = arrayInputStream.readInt();
            this.spareSpace = arrayInputStream.readInt();
            this.minimumRecordSize = arrayInputStream.readInt();
            this.initialPages = arrayInputStream.readShort();
        }
        catch (IOException ex) {
            throw StandardException.newException("XSDA4.S", ex);
        }
        this.setReusableRecordIdState((int2 & 0x8) != 0x0);
    }
    
    private void createInfoFromProp(final Properties properties) throws StandardException {
        final AccessFactory accessFactory = (AccessFactory)Monitor.getServiceModule(this.dataFactory, "org.apache.derby.iapi.store.access.AccessFactory");
        final TransactionController transactionController = (accessFactory == null) ? null : accessFactory.getTransaction(ContextService.getFactory().getCurrentContextManager());
        this.pageSize = PropertyUtil.getServiceInt(transactionController, properties, "derby.storage.pageSize", 4096, 32768, 4096);
        if (this.pageSize != 4096 && this.pageSize != 8192 && this.pageSize != 16384 && this.pageSize != 32768) {
            this.pageSize = 4096;
        }
        this.spareSpace = PropertyUtil.getServiceInt(transactionController, properties, "derby.storage.pageReservedSpace", 0, 100, 20);
        this.PreAllocSize = PropertyUtil.getServiceInt(transactionController, properties, "derby.storage.pagePerAllocate", 1, 1000, 8);
        if (properties == null) {
            this.minimumRecordSize = PropertyUtil.getServiceInt(transactionController, "derby.storage.minimumRecordSize", 12, this.pageSize * (1 - this.spareSpace / 100) - 100, 12);
        }
        else {
            this.minimumRecordSize = PropertyUtil.getServiceInt(transactionController, properties, "derby.storage.minimumRecordSize", 1, this.pageSize * (1 - this.spareSpace / 100) - 100, 12);
        }
        if (properties != null) {
            final String property = properties.getProperty("derby.storage.reusableRecordId");
            if (property != null) {
                this.setReusableRecordIdState(new Boolean(property));
            }
            final String property2 = properties.getProperty("derby.storage.initialPages");
            if (property2 != null) {
                this.initialPages = Short.parseShort(property2);
                if (this.initialPages > 1 && this.initialPages > 1000) {
                    this.initialPages = 1000;
                }
            }
        }
    }
    
    protected boolean canUpdate() {
        return this.canUpdate;
    }
    
    protected void deallocatePage(final BaseContainerHandle baseContainerHandle, final BasePage basePage) throws StandardException {
        this.deallocatePagenum(baseContainerHandle, basePage.getPageNumber());
        basePage.deallocatePage();
    }
    
    private void deallocatePagenum(final BaseContainerHandle baseContainerHandle, final long n) throws StandardException {
        synchronized (this.allocCache) {
            final long allocPageNumber = this.allocCache.getAllocPageNumber(baseContainerHandle, n, this.firstAllocPageNumber);
            final AllocPage allocPage = (AllocPage)baseContainerHandle.getAllocPage(allocPageNumber);
            if (allocPage == null) {
                throw StandardException.newException("XSDF6.S", new PageKey(this.identity, allocPageNumber));
            }
            try {
                this.allocCache.invalidate(allocPage, allocPageNumber);
                allocPage.deallocatePage(baseContainerHandle, n);
            }
            finally {
                allocPage.unlatch();
            }
        }
        if (n <= this.lastAllocatedPage) {
            this.lastAllocatedPage = n - 1L;
        }
    }
    
    protected void compressContainer(final RawTransaction rawTransaction, final BaseContainerHandle baseContainerHandle) throws StandardException {
        AllocPage allocPage = null;
        AllocPage allocPage2 = null;
        if (this.firstAllocPageNumber == -1L) {
            return;
        }
        this.dataFactory.getRawStoreFactory().checkpoint();
        rawTransaction.blockBackup(true);
        try {
            synchronized (this.allocCache) {
                long nextAllocPageNumber;
                for (allocPage = (AllocPage)baseContainerHandle.getAllocPage(this.firstAllocPageNumber); !allocPage.isLast(); allocPage = (AllocPage)baseContainerHandle.getAllocPage(nextAllocPageNumber)) {
                    if (allocPage2 != null) {
                        allocPage2.unlatch();
                    }
                    allocPage2 = allocPage;
                    allocPage = null;
                    nextAllocPageNumber = allocPage2.getNextAllocPageNumber();
                    allocPage2.getNextAllocPageOffset();
                }
                this.allocCache.invalidate();
                this.lastUnfilledPage = -1L;
                this.lastAllocatedPage = -1L;
                allocPage.compress(rawTransaction, this);
            }
        }
        finally {
            if (allocPage != null) {
                allocPage.unlatch();
            }
            if (allocPage2 != null) {
                allocPage2.unlatch();
            }
            this.flushAll();
            this.pageCache.discard(this.identity);
        }
    }
    
    public final long getReusableRecordIdSequenceNumber() {
        synchronized (this) {
            return this.reusableRecordIdSequenceNumber;
        }
    }
    
    protected final void incrementReusableRecordIdSequenceNumber() {
        final boolean readOnly = this.dataFactory.isReadOnly();
        synchronized (this) {
            ++this.reusableRecordIdSequenceNumber;
            if (!readOnly) {
                this.isDirty = true;
            }
        }
    }
    
    protected BasePage newPage(final BaseContainerHandle baseContainerHandle, RawTransaction transaction, final BaseContainerHandle baseContainerHandle2, final boolean b) throws StandardException {
        final boolean b2 = transaction != null;
        if (!b2) {
            transaction = baseContainerHandle.getTransaction();
        }
        long nextFreePageNumber = -1L;
        int n = 0;
        int n2 = 120;
        long lastAllocatedPage = this.lastAllocatedPage;
        AllocPage allocPageForAdd = null;
        BasePage basePage = null;
        try {
            int i;
            do {
                i = 0;
                synchronized (this.allocCache) {
                    try {
                        allocPageForAdd = this.findAllocPageForAdd(baseContainerHandle2, transaction, lastAllocatedPage);
                    }
                    catch (InterruptDetectedException ex) {
                        if (--n2 > 0) {
                            this.firstAllocPageNumber = -1L;
                            i = 1;
                            try {
                                Thread.sleep(500L);
                            }
                            catch (InterruptedException ex4) {
                                InterruptStatus.setInterrupted();
                            }
                            continue;
                        }
                        throw StandardException.newException("XSDG9.D", ex);
                    }
                    this.allocCache.invalidate(allocPageForAdd, allocPageForAdd.getPageNumber());
                }
                nextFreePageNumber = allocPageForAdd.nextFreePageNumber(lastAllocatedPage);
                final long lastPagenum = allocPageForAdd.getLastPagenum();
                final long lastPreallocPagenum = allocPageForAdd.getLastPreallocPagenum();
                final boolean b3 = nextFreePageNumber <= lastPagenum;
                final PageKey pageKey = new PageKey(this.identity, nextFreePageNumber);
                if (b3) {
                    if (!this.getDeallocLock(baseContainerHandle2, BasePage.MakeRecordHandle(pageKey, 2), false, true)) {
                        if (n == 0) {
                            lastAllocatedPage = -1L;
                            this.lastAllocatedPage = nextFreePageNumber;
                        }
                        else {
                            lastAllocatedPage = nextFreePageNumber;
                        }
                        ++n;
                        allocPageForAdd.unlatch();
                        allocPageForAdd = null;
                        i = 1;
                    }
                    else {
                        this.lastAllocatedPage = nextFreePageNumber;
                    }
                }
                else if (n > 0) {
                    this.lastAllocatedPage = -1L;
                }
                else {
                    this.lastAllocatedPage = nextFreePageNumber;
                }
                if (i != 0) {
                    continue;
                }
                final boolean b4 = (baseContainerHandle2.getMode() & 0x1) == 0x1;
                if (!b4 && (this.bulkIncreaseContainerSize || (nextFreePageNumber > lastPreallocPagenum && nextFreePageNumber > this.PreAllocThreshold))) {
                    allocPageForAdd.preAllocatePage(this, this.PreAllocThreshold, this.PreAllocSize);
                }
                final PageCreationArgs pageCreationArgs = new PageCreationArgs(117, (nextFreePageNumber <= allocPageForAdd.getLastPreallocPagenum()) ? 0 : (b4 ? 0 : 1), this.pageSize, this.spareSpace, this.minimumRecordSize, 0);
                final long n3 = nextFreePageNumber * this.pageSize;
                try {
                    basePage = this.initPage(baseContainerHandle2, pageKey, pageCreationArgs, n3, b3, b);
                }
                catch (StandardException ex2) {
                    this.allocCache.dumpAllocationCache();
                    throw ex2;
                }
                allocPageForAdd.addPage(this, nextFreePageNumber, transaction, baseContainerHandle);
                if (!b2) {
                    continue;
                }
                basePage.unlatch();
                basePage = null;
                basePage = (BasePage)this.pageCache.find(pageKey);
                basePage = this.latchPage(baseContainerHandle, basePage, false);
                if (basePage != null && basePage.recordCount() == 0 && basePage.getPageStatus() == 1) {
                    continue;
                }
                i = 1;
                if (basePage != null) {
                    basePage.unlatch();
                    basePage = null;
                }
                allocPageForAdd.unlatch();
                allocPageForAdd = null;
            } while (i == 1);
        }
        catch (StandardException ex3) {
            if (basePage != null) {
                basePage.unlatch();
            }
            basePage = null;
            throw ex3;
        }
        finally {
            if (!b2 && allocPageForAdd != null) {
                allocPageForAdd.unlatch();
            }
        }
        if (this.bulkIncreaseContainerSize) {
            this.bulkIncreaseContainerSize = false;
            this.PreAllocSize = 8;
        }
        if (!b && basePage != null) {
            this.setLastInsertedPage(nextFreePageNumber);
        }
        if (this.estimatedPageCount >= 0L) {
            ++this.estimatedPageCount;
        }
        if (!this.identity.equals(basePage.getPageId().getContainerId())) {
            throw StandardException.newException("XSDAC.S", this.identity, basePage.getPageId().getContainerId());
        }
        return basePage;
    }
    
    protected void clearPreallocThreshold() {
        this.PreAllocThreshold = 0;
    }
    
    protected void prepareForBulkLoad(final BaseContainerHandle baseContainerHandle, final int n) {
        this.clearPreallocThreshold();
        final AllocPage lastAllocPage = this.findLastAllocPage(baseContainerHandle, baseContainerHandle.getTransaction());
        if (lastAllocPage != null) {
            lastAllocPage.preAllocatePage(this, 0, n);
            lastAllocPage.unlatch();
        }
    }
    
    private boolean pageValid(final BaseContainerHandle baseContainerHandle, final long n) throws StandardException {
        boolean b = false;
        int n2 = 120;
        boolean b2;
        do {
            b2 = true;
            synchronized (this.allocCache) {
                try {
                    if (n > this.allocCache.getLastPageNumber(baseContainerHandle, this.firstAllocPageNumber) || this.allocCache.getPageStatus(baseContainerHandle, n, this.firstAllocPageNumber) != 0) {
                        continue;
                    }
                    b = true;
                }
                catch (InterruptDetectedException ex) {
                    if (--n2 <= 0) {
                        throw StandardException.newException("XSDG9.D", ex);
                    }
                    b2 = false;
                    try {
                        Thread.sleep(500L);
                    }
                    catch (InterruptedException ex2) {
                        InterruptStatus.setInterrupted();
                    }
                }
            }
        } while (!b2);
        return b;
    }
    
    protected long getLastPageNumber(final BaseContainerHandle baseContainerHandle) throws StandardException {
        long lastPageNumber;
        synchronized (this.allocCache) {
            if (this.firstAllocPageNumber == -1L) {
                lastPageNumber = -1L;
            }
            else {
                lastPageNumber = this.allocCache.getLastPageNumber(baseContainerHandle, this.firstAllocPageNumber);
            }
        }
        return lastPageNumber;
    }
    
    private AllocPage findAllocPageForAdd(final BaseContainerHandle baseContainerHandle, final RawTransaction rawTransaction, final long n) throws StandardException {
        AllocPage allocPage = null;
        AllocPage allocPage2 = null;
        boolean b = false;
        try {
            if (this.firstAllocPageNumber == -1L) {
                allocPage = this.makeAllocPage(rawTransaction, baseContainerHandle, 0L, 0L, 80);
            }
            else {
                allocPage = (AllocPage)baseContainerHandle.getAllocPage(this.firstAllocPageNumber);
            }
            if (!allocPage.canAddFreePage(n)) {
                boolean b2 = false;
                while (!allocPage.isLast()) {
                    final long nextAllocPageNumber = allocPage.getNextAllocPageNumber();
                    allocPage.getNextAllocPageOffset();
                    allocPage.unlatch();
                    allocPage = null;
                    allocPage = (AllocPage)baseContainerHandle.getAllocPage(nextAllocPageNumber);
                    if (allocPage.canAddFreePage(n)) {
                        b2 = true;
                        break;
                    }
                }
                if (!b2) {
                    allocPage2 = allocPage;
                    allocPage = null;
                    final long n3;
                    final long n2 = n3 = allocPage2.getMaxPagenum() + 1L;
                    allocPage = this.makeAllocPage(rawTransaction, baseContainerHandle, n2, n3, 0);
                    allocPage2.chainNewAllocPage(baseContainerHandle, n2, n3);
                    allocPage2.unlatch();
                    allocPage2 = null;
                }
            }
            b = true;
        }
        finally {
            if (!b) {
                if (allocPage2 != null) {
                    allocPage2.unlatch();
                }
                if (allocPage != null) {
                    allocPage.unlatch();
                }
                allocPage = null;
            }
        }
        return allocPage;
    }
    
    private AllocPage findLastAllocPage(final BaseContainerHandle baseContainerHandle, final RawTransaction rawTransaction) {
        AllocPage allocPage = null;
        if (this.firstAllocPageNumber == -1L) {
            return null;
        }
        try {
            long nextAllocPageNumber;
            for (allocPage = (AllocPage)baseContainerHandle.getAllocPage(this.firstAllocPageNumber); !allocPage.isLast(); allocPage = null, allocPage = (AllocPage)baseContainerHandle.getAllocPage(nextAllocPageNumber)) {
                nextAllocPageNumber = allocPage.getNextAllocPageNumber();
                allocPage.getNextAllocPageOffset();
                allocPage.unlatch();
            }
        }
        catch (StandardException ex) {
            if (allocPage != null) {
                allocPage.unlatch();
            }
            allocPage = null;
        }
        return allocPage;
    }
    
    private AllocPage makeAllocPage(final RawTransaction rawTransaction, final BaseContainerHandle baseContainerHandle, final long firstAllocPageNumber, final long firstAllocPageOffset, final int n) throws StandardException {
        final PageCreationArgs pageCreationArgs = new PageCreationArgs(118, ((baseContainerHandle.getMode() & 0x1) != 0x1) ? 1 : 0, this.pageSize, 0, this.minimumRecordSize, n);
        if (firstAllocPageNumber == 0L) {
            this.firstAllocPageNumber = firstAllocPageNumber;
            this.firstAllocPageOffset = firstAllocPageOffset;
        }
        return (AllocPage)this.initPage(baseContainerHandle, new PageKey(this.identity, firstAllocPageNumber), pageCreationArgs, firstAllocPageOffset, false, false);
    }
    
    protected BasePage initPage(final BaseContainerHandle baseContainerHandle, final PageKey pageKey, final PageCreationArgs pageCreationArgs, final long n, final boolean b, final boolean b2) throws StandardException {
        Object latchPage = null;
        boolean b3 = true;
        try {
            if (b) {
                latchPage = this.pageCache.find(pageKey);
                if (latchPage == null) {
                    throw StandardException.newException("XSDF8.S", pageKey);
                }
            }
            else {
                latchPage = this.pageCache.create(pageKey, pageCreationArgs);
            }
            b3 = false;
            latchPage = this.latchPage(baseContainerHandle, (BasePage)latchPage, true);
            if (latchPage == null) {
                throw StandardException.newException("XSDF7.S", pageKey);
            }
            int n2 = 0;
            if (b) {
                n2 |= 0x1;
            }
            if (b2) {
                n2 |= 0x2;
            }
            if (b && this.isReusableRecordId()) {
                n2 |= 0x4;
            }
            ((BasePage)latchPage).initPage(n2, n);
            ((BasePage)latchPage).setContainerRowCount(this.estimatedRowCount);
        }
        finally {
            if (b3 && latchPage != null) {
                this.pageCache.release((Cacheable)latchPage);
                latchPage = null;
            }
        }
        return (BasePage)latchPage;
    }
    
    private BasePage getUserPage(final BaseContainerHandle baseContainerHandle, final long n, final boolean b, final boolean b2) throws StandardException {
        if (n < 1L) {
            return null;
        }
        if (this.getCommittedDropState()) {
            return null;
        }
        if (!this.pageValid(baseContainerHandle, n)) {
            return null;
        }
        BasePage basePage = (BasePage)this.pageCache.find(new PageKey(this.identity, n));
        if (basePage == null) {
            return basePage;
        }
        if (this.latchPage(baseContainerHandle, basePage, b2) == null) {
            return null;
        }
        if ((basePage.isOverflowPage() && !b) || basePage.getPageStatus() != 1) {
            basePage.unlatch();
            basePage = null;
        }
        return basePage;
    }
    
    protected void trackUnfilledPage(final long n, final boolean b) {
        if (!this.dataFactory.isReadOnly()) {
            this.allocCache.trackUnfilledPage(n, b);
        }
    }
    
    protected BasePage getPage(final BaseContainerHandle baseContainerHandle, final long n, final boolean b) throws StandardException {
        return this.getUserPage(baseContainerHandle, n, true, b);
    }
    
    protected BasePage getAnyPage(final BaseContainerHandle baseContainerHandle, final long n) throws StandardException {
        if (this.getCommittedDropState()) {
            return null;
        }
        synchronized (this.allocCache) {
            this.allocCache.invalidate();
        }
        return (BasePage)this.pageCache.find(new PageKey(this.identity, n));
    }
    
    protected BasePage reCreatePageForRedoRecovery(final BaseContainerHandle baseContainerHandle, final int n, final long firstAllocPageNumber, final long firstAllocPageOffset) throws StandardException {
        if (!baseContainerHandle.getTransaction().inRollForwardRecovery() && !PropertyUtil.getSystemBoolean("derby.storage.patchInitPageRecoverError")) {
            return null;
        }
        final PageKey pageKey = new PageKey(this.identity, firstAllocPageNumber);
        PageCreationArgs pageCreationArgs;
        if (n == 117) {
            pageCreationArgs = new PageCreationArgs(n, 1, this.pageSize, this.spareSpace, this.minimumRecordSize, 0);
        }
        else {
            if (n != 118) {
                throw StandardException.newException("XSDB1.D", pageKey);
            }
            int n2 = 0;
            if (firstAllocPageNumber == 0L) {
                n2 = 80;
                this.firstAllocPageNumber = firstAllocPageNumber;
                this.firstAllocPageOffset = firstAllocPageOffset;
            }
            pageCreationArgs = new PageCreationArgs(n, 1, this.pageSize, 0, this.minimumRecordSize, n2);
        }
        Object latchPage = null;
        boolean b = true;
        try {
            try {
                latchPage = this.pageCache.create(pageKey, pageCreationArgs);
            }
            catch (StandardException ex) {
                throw StandardException.newException("XSDFI.S", ex, pageKey);
            }
            if (latchPage == null) {
                throw StandardException.newException("XSDFI.S", pageKey);
            }
            b = false;
            latchPage = this.latchPage(baseContainerHandle, (BasePage)latchPage, false);
            if (latchPage == null) {
                throw StandardException.newException("XSDF7.S", pageKey);
            }
        }
        finally {
            if (b && latchPage != null) {
                this.pageCache.release((Cacheable)latchPage);
                latchPage = null;
            }
        }
        return (BasePage)latchPage;
    }
    
    protected BasePage getAllocPage(final long n) throws StandardException {
        if (this.getCommittedDropState()) {
            return null;
        }
        return (BasePage)this.pageCache.find(new PageKey(this.identity, n));
    }
    
    protected BasePage getHeadPage(final BaseContainerHandle baseContainerHandle, final long n, final boolean b) throws StandardException {
        return this.getUserPage(baseContainerHandle, n, false, b);
    }
    
    protected BasePage getFirstHeadPage(final BaseContainerHandle baseContainerHandle, final boolean b) throws StandardException {
        return this.getNextHeadPage(baseContainerHandle, 0L, b);
    }
    
    protected BasePage getNextHeadPage(final BaseContainerHandle baseContainerHandle, long n, final boolean b) throws StandardException {
        while (true) {
            final long nextValidPage;
            synchronized (this.allocCache) {
                nextValidPage = this.allocCache.getNextValidPage(baseContainerHandle, n, this.firstAllocPageNumber);
            }
            if (nextValidPage == -1L) {
                return null;
            }
            final BasePage userPage = this.getUserPage(baseContainerHandle, nextValidPage, false, b);
            if (userPage != null) {
                return userPage;
            }
            n = nextValidPage;
        }
    }
    
    private BasePage getInsertablePage(final BaseContainerHandle baseContainerHandle, final long n, final boolean b, final boolean b2) throws StandardException {
        if (n == -1L) {
            return null;
        }
        BasePage userPage = this.getUserPage(baseContainerHandle, n, b2, b);
        if (userPage != null && !userPage.allowInsert()) {
            userPage.unlatch();
            userPage = null;
            this.allocCache.trackUnfilledPage(n, false);
        }
        return userPage;
    }
    
    protected BasePage getPageForCompress(final BaseContainerHandle baseContainerHandle, final int n, final long n2) throws StandardException {
        BasePage basePage = null;
        if ((n & 0x1) == 0x0) {
            final long lastInsertedPage = this.getLastInsertedPage();
            if (lastInsertedPage < n2 && lastInsertedPage != -1L) {
                basePage = this.getInsertablePage(baseContainerHandle, lastInsertedPage, true, false);
                if (basePage == null) {
                    if (lastInsertedPage == this.getLastUnfilledPage()) {
                        this.setLastUnfilledPage(-1L);
                    }
                    if (lastInsertedPage == this.getLastInsertedPage()) {
                        this.setLastInsertedPage(-1L);
                    }
                }
            }
        }
        else {
            long n3 = this.getLastUnfilledPage();
            if (n3 == -1L || n3 >= n2 || n3 == this.getLastInsertedPage()) {
                n3 = this.getUnfilledPageNumber(baseContainerHandle, 0L);
            }
            if (n3 != -1L && n3 < n2) {
                basePage = this.getInsertablePage(baseContainerHandle, n3, true, false);
            }
            if (basePage != null) {
                this.setLastUnfilledPage(n3);
                this.setLastInsertedPage(n3);
            }
        }
        return basePage;
    }
    
    protected BasePage getPageForInsert(final BaseContainerHandle baseContainerHandle, final int n) throws StandardException {
        BasePage basePage = null;
        if ((n & 0x1) == 0x0) {
            long n2 = this.getLastInsertedPage();
            if (n2 != -1L) {
                basePage = this.getInsertablePage(baseContainerHandle, n2, false, false);
                if (basePage == null) {
                    n2 = this.getLastInsertedPage();
                    basePage = this.getInsertablePage(baseContainerHandle, n2, true, false);
                }
            }
            if (basePage == null) {
                if (n2 == this.getLastUnfilledPage()) {
                    this.setLastUnfilledPage(-1L);
                }
                if (n2 == this.getLastInsertedPage()) {
                    this.setLastInsertedPage(-1L);
                }
            }
        }
        else {
            long n3 = this.getLastUnfilledPage();
            if (n3 == -1L || n3 == this.getLastInsertedPage()) {
                n3 = this.getUnfilledPageNumber(baseContainerHandle, n3);
            }
            if (n3 != -1L) {
                basePage = this.getInsertablePage(baseContainerHandle, n3, true, false);
                if (basePage == null) {
                    n3 = this.getUnfilledPageNumber(baseContainerHandle, n3);
                    if (n3 != -1L) {
                        basePage = this.getInsertablePage(baseContainerHandle, n3, true, false);
                    }
                }
            }
            if (basePage != null) {
                this.setLastUnfilledPage(n3);
                this.setLastInsertedPage(n3);
            }
        }
        return basePage;
    }
    
    protected BasePage getLatchedPage(final BaseContainerHandle baseContainerHandle, final long n) throws StandardException {
        return this.latchPage(baseContainerHandle, (BasePage)this.pageCache.find(new PageKey(this.identity, n)), true);
    }
    
    private long getUnfilledPageNumber(final BaseContainerHandle baseContainerHandle, final long n) throws StandardException {
        synchronized (this.allocCache) {
            return this.allocCache.getUnfilledPageNumber(baseContainerHandle, this.firstAllocPageNumber, n);
        }
    }
    
    public long getEstimatedRowCount(final int n) {
        return this.estimatedRowCount;
    }
    
    public void setEstimatedRowCount(final long estimatedRowCount, final int n) {
        final boolean readOnly = this.dataFactory.isReadOnly();
        synchronized (this) {
            this.estimatedRowCount = estimatedRowCount;
            if (!readOnly) {
                this.isDirty = true;
            }
        }
    }
    
    protected void updateEstimatedRowCount(final int n) {
        final boolean readOnly = this.dataFactory.isReadOnly();
        synchronized (this) {
            this.estimatedRowCount += n;
            if (this.estimatedRowCount < 0L) {
                this.estimatedRowCount = 0L;
            }
            if (!readOnly) {
                this.isDirty = true;
            }
        }
    }
    
    public long getEstimatedPageCount(final BaseContainerHandle baseContainerHandle, final int n) throws StandardException {
        if (this.estimatedPageCount < 0L) {
            synchronized (this.allocCache) {
                this.estimatedPageCount = this.allocCache.getEstimatedPageCount(baseContainerHandle, this.firstAllocPageNumber);
            }
        }
        return this.estimatedPageCount;
    }
    
    protected abstract void readPage(final long p0, final byte[] p1) throws IOException, StandardException;
    
    protected abstract void writePage(final long p0, final byte[] p1, final boolean p2) throws IOException, StandardException;
    
    protected void decryptPage(final byte[] array, final int n) throws StandardException {
        synchronized (this) {
            if (this.encryptionBuffer == null || this.encryptionBuffer.length < n) {
                this.encryptionBuffer = new byte[n];
            }
            this.dataFactory.decrypt(array, 0, n, this.encryptionBuffer, 0);
            System.arraycopy(this.encryptionBuffer, 8, array, 0, n - 8);
            System.arraycopy(this.encryptionBuffer, 0, array, n - 8, 8);
        }
    }
    
    protected byte[] encryptPage(final byte[] array, final int n, final byte[] array2, final boolean b) throws StandardException {
        System.arraycopy(array, n - 8, array2, 0, 8);
        System.arraycopy(array, 0, array2, 8, n - 8);
        this.dataFactory.encrypt(array2, 0, n, array2, 0, b);
        return array2;
    }
    
    protected byte[] getEncryptionBuffer() {
        if (this.encryptionBuffer == null || this.encryptionBuffer.length < this.pageSize) {
            this.encryptionBuffer = new byte[this.pageSize];
        }
        return this.encryptionBuffer;
    }
    
    protected abstract int preAllocate(final long p0, final int p1);
    
    protected int doPreAllocatePages(final long n, final int n2) {
        final PageCreationArgs pageCreationArgs = new PageCreationArgs(117, 2, this.pageSize, this.spareSpace, this.minimumRecordSize, 0);
        final StoredPage storedPage = new StoredPage();
        storedPage.setFactory(this.dataFactory);
        boolean b = false;
        int i;
        for (i = 0; i < n2; ++i) {
            final PageKey pageKey = new PageKey(this.identity, n + i + 1L);
            try {
                storedPage.createIdentity(pageKey, pageCreationArgs);
                storedPage.clearIdentity();
            }
            catch (StandardException ex) {
                b = true;
            }
            if (b) {
                break;
            }
        }
        return i;
    }
    
    protected int getPageSize() {
        return this.pageSize;
    }
    
    protected int getSpareSpace() {
        return this.spareSpace;
    }
    
    protected int getMinimumRecordSize() {
        return this.minimumRecordSize;
    }
    
    private synchronized void switchToMultiInsertPageMode(final BaseContainerHandle baseContainerHandle) throws StandardException {
        if (this.lastInsertedPage.length == 1) {
            (this.lastInsertedPage = new long[4])[0] = this.lastInsertedPage[0];
            for (int i = 3; i > 0; --i) {
                final Page addPage = this.addPage(baseContainerHandle, false);
                this.lastInsertedPage[i] = addPage.getPageNumber();
                addPage.unlatch();
            }
        }
    }
    
    private synchronized long getLastInsertedPage() {
        if (this.lastInsertedPage.length == 1) {
            return this.lastInsertedPage[0];
        }
        final long n = this.lastInsertedPage[this.lastInsertedPage_index++];
        if (this.lastInsertedPage_index > this.lastInsertedPage.length - 1) {
            this.lastInsertedPage_index = 0;
        }
        return n;
    }
    
    private synchronized long getLastUnfilledPage() {
        return this.lastUnfilledPage;
    }
    
    private synchronized void initializeLastInsertedPage(final int n) {
        this.lastInsertedPage = new long[n];
        for (int i = this.lastInsertedPage.length - 1; i >= 0; --i) {
            this.lastInsertedPage[i] = -1L;
        }
        this.lastInsertedPage_index = 0;
    }
    
    private synchronized void setLastInsertedPage(final long n) {
        this.lastInsertedPage[this.lastInsertedPage_index] = n;
    }
    
    private synchronized void setLastUnfilledPage(final long lastUnfilledPage) {
        this.lastUnfilledPage = lastUnfilledPage;
    }
    
    protected void letGo(final BaseContainerHandle baseContainerHandle) {
        super.letGo(baseContainerHandle);
        this.containerCache.release(this);
    }
    
    protected BasePage latchPage(final BaseContainerHandle baseContainerHandle, final BasePage basePage, final boolean b) throws StandardException {
        if (basePage == null) {
            return null;
        }
        final BasePage latchPage = super.latchPage(baseContainerHandle, basePage, b);
        if (latchPage == null) {
            this.pageCache.release((Cacheable)basePage);
        }
        return latchPage;
    }
    
    protected abstract void backupContainer(final BaseContainerHandle p0, final String p1) throws StandardException;
    
    static {
        SPACE_TRACE = null;
    }
}
