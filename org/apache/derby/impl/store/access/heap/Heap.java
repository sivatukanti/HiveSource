// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.heap;

import java.io.DataInput;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.DataOutput;
import org.apache.derby.iapi.services.io.FormatIdUtil;
import java.io.ObjectOutput;
import org.apache.derby.iapi.store.access.StoreCostController;
import org.apache.derby.iapi.store.access.RowUtil;
import org.apache.derby.iapi.store.access.conglomerate.ScanManager;
import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.impl.store.access.conglomerate.OpenConglomerate;
import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.iapi.store.access.RowLocationRetRowSource;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.impl.store.access.conglomerate.OpenConglomerateScratchSpace;
import org.apache.derby.iapi.store.access.DynamicCompiledOpenConglomInfo;
import org.apache.derby.iapi.services.io.Storable;
import org.apache.derby.iapi.store.access.conglomerate.TransactionManager;
import org.apache.derby.iapi.store.raw.Page;
import org.apache.derby.iapi.store.raw.ContainerHandle;
import org.apache.derby.iapi.store.access.conglomerate.LogicalUndo;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.store.raw.LockingPolicy;
import org.apache.derby.impl.store.access.conglomerate.ConglomerateUtil;
import org.apache.derby.iapi.error.StandardException;
import java.util.Properties;
import org.apache.derby.iapi.store.access.ColumnOrdering;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.services.cache.ClassSize;
import org.apache.derby.iapi.store.raw.ContainerKey;
import org.apache.derby.iapi.store.access.StaticCompiledOpenConglomInfo;
import org.apache.derby.iapi.store.access.conglomerate.Conglomerate;
import org.apache.derby.impl.store.access.conglomerate.GenericConglomerate;

public class Heap extends GenericConglomerate implements Conglomerate, StaticCompiledOpenConglomInfo
{
    protected int conglom_format_id;
    private ContainerKey id;
    int[] format_ids;
    protected int[] collation_ids;
    private boolean hasCollatedTypes;
    private static final int BASE_MEMORY_USAGE;
    private static final int CONTAINER_KEY_MEMORY_USAGE;
    
    public int estimateMemoryUsage() {
        int base_MEMORY_USAGE = Heap.BASE_MEMORY_USAGE;
        if (null != this.id) {
            base_MEMORY_USAGE += Heap.CONTAINER_KEY_MEMORY_USAGE;
        }
        if (null != this.format_ids) {
            base_MEMORY_USAGE += this.format_ids.length * ClassSize.getIntSize();
        }
        return base_MEMORY_USAGE;
    }
    
    protected void create(final Transaction transaction, final int n, final long n2, final DataValueDescriptor[] array, final ColumnOrdering[] array2, final int[] array3, final Properties properties, final int conglom_format_id, final int n3) throws StandardException {
        if (properties != null) {
            final String property = properties.getProperty("derby.storage.minimumRecordSize");
            if (((property == null) ? 12 : Integer.parseInt(property)) < 12) {
                properties.put("derby.storage.minimumRecordSize", Integer.toString(12));
            }
        }
        final long addContainer = transaction.addContainer(n, n2, 0, properties, n3);
        if (addContainer < 0L) {
            throw StandardException.newException("XSCH0.S");
        }
        this.id = new ContainerKey(n, addContainer);
        if (array == null || array.length == 0) {
            throw StandardException.newException("XSCH4.S");
        }
        this.format_ids = ConglomerateUtil.createFormatIds(array);
        this.conglom_format_id = conglom_format_id;
        this.collation_ids = ConglomerateUtil.createCollationIds(this.format_ids.length, array3);
        this.hasCollatedTypes = GenericConglomerate.hasCollatedColumns(this.collation_ids);
        ContainerHandle openContainer = null;
        Page page = null;
        try {
            openContainer = transaction.openContainer(this.id, null, 0x4 | (this.isTemporary() ? 2048 : 0));
            final DataValueDescriptor[] array4 = { this };
            page = openContainer.getPage(1L);
            page.insertAtSlot(0, array4, null, null, (byte)8, 100);
            page.unlatch();
            page = null;
            openContainer.setEstimatedRowCount(0L, 0);
        }
        finally {
            if (openContainer != null) {
                openContainer.close();
            }
            if (page != null) {
                page.unlatch();
            }
        }
    }
    
    public void boot_create(final long n, final DataValueDescriptor[] array) {
        this.id = new ContainerKey(0L, n);
        this.format_ids = ConglomerateUtil.createFormatIds(array);
    }
    
    public void addColumn(final TransactionManager transactionManager, final int n, final Storable storable, final int n2) throws StandardException {
        ContainerHandle openContainer = null;
        Page page = null;
        final Transaction rawStoreXact = transactionManager.getRawStoreXact();
        try {
            openContainer = rawStoreXact.openContainer(this.id, rawStoreXact.newLockingPolicy(2, 5, true), 0x4 | (this.isTemporary() ? 2048 : 0));
            if (n != this.format_ids.length) {
                throw StandardException.newException("XSCH5.S", new Long(n), new Long(this.format_ids.length));
            }
            final int[] format_ids = this.format_ids;
            System.arraycopy(format_ids, 0, this.format_ids = new int[format_ids.length + 1], 0, format_ids.length);
            this.format_ids[format_ids.length] = storable.getTypeFormatId();
            final int[] collation_ids = this.collation_ids;
            System.arraycopy(collation_ids, 0, this.collation_ids = new int[collation_ids.length + 1], 0, collation_ids.length);
            this.collation_ids[collation_ids.length] = n2;
            final DataValueDescriptor[] array = { this };
            page = openContainer.getPage(1L);
            page.updateAtSlot(0, array, null);
            page.unlatch();
            page = null;
        }
        finally {
            if (openContainer != null) {
                openContainer.close();
            }
            if (page != null) {
                page.unlatch();
            }
        }
    }
    
    public void drop(final TransactionManager transactionManager) throws StandardException {
        transactionManager.getRawStoreXact().dropContainer(this.id);
    }
    
    public boolean fetchMaxOnBTree(final TransactionManager transactionManager, final Transaction transaction, final long n, final int n2, final int n3, final LockingPolicy lockingPolicy, final int n4, final FormatableBitSet set, final DataValueDescriptor[] array) throws StandardException {
        throw StandardException.newException("XSCH8.S");
    }
    
    public final ContainerKey getId() {
        return this.id;
    }
    
    public final long getContainerid() {
        return this.id.getContainerId();
    }
    
    public DynamicCompiledOpenConglomInfo getDynamicCompiledConglomInfo() throws StandardException {
        return new OpenConglomerateScratchSpace(this.format_ids, this.collation_ids, this.hasCollatedTypes);
    }
    
    public StaticCompiledOpenConglomInfo getStaticCompiledConglomInfo(final TransactionController transactionController, final long n) throws StandardException {
        return this;
    }
    
    public boolean isTemporary() {
        return this.id.getSegmentId() == -1L;
    }
    
    public long load(final TransactionManager transactionManager, final boolean b, final RowLocationRetRowSource rowLocationRetRowSource) throws StandardException {
        long load = 0L;
        final HeapController heapController = new HeapController();
        try {
            load = heapController.load(transactionManager, this, b, rowLocationRetRowSource);
        }
        finally {
            heapController.close();
        }
        return load;
    }
    
    public ConglomerateController open(final TransactionManager transactionManager, final Transaction transaction, final boolean b, final int n, final int n2, final LockingPolicy lockingPolicy, final StaticCompiledOpenConglomInfo staticCompiledOpenConglomInfo, final DynamicCompiledOpenConglomInfo dynamicCompiledOpenConglomInfo) throws StandardException {
        final OpenHeap openHeap = new OpenHeap();
        if (openHeap.init(null, this, this.format_ids, this.collation_ids, transactionManager, transaction, b, n, n2, lockingPolicy, dynamicCompiledOpenConglomInfo) == null) {
            throw StandardException.newException("XSCH1.S", new Long(this.id.getContainerId()).toString());
        }
        final HeapController heapController = new HeapController();
        heapController.init(openHeap);
        return heapController;
    }
    
    public ScanManager openScan(final TransactionManager transactionManager, final Transaction transaction, final boolean b, final int n, final int n2, final LockingPolicy lockingPolicy, final int n3, final FormatableBitSet set, final DataValueDescriptor[] array, final int n4, final Qualifier[][] array2, final DataValueDescriptor[] array3, final int n5, final StaticCompiledOpenConglomInfo staticCompiledOpenConglomInfo, final DynamicCompiledOpenConglomInfo dynamicCompiledOpenConglomInfo) throws StandardException {
        if (!RowUtil.isRowEmpty(array) || !RowUtil.isRowEmpty(array3)) {
            throw StandardException.newException("XSCH8.S");
        }
        final OpenHeap openHeap = new OpenHeap();
        if (openHeap.init(null, this, this.format_ids, this.collation_ids, transactionManager, transaction, b, n, n2, lockingPolicy, dynamicCompiledOpenConglomInfo) == null) {
            throw StandardException.newException("XSCH1.S", new Long(this.id.getContainerId()));
        }
        final HeapScan heapScan = new HeapScan();
        heapScan.init(openHeap, set, array, n4, array2, array3, n5);
        return heapScan;
    }
    
    public void purgeConglomerate(final TransactionManager transactionManager, final Transaction transaction) throws StandardException {
        OpenConglomerate openConglomerate = null;
        HeapController heapController = null;
        TransactionManager transactionManager2 = null;
        try {
            openConglomerate = new OpenHeap();
            if (openConglomerate.init(null, this, this.format_ids, this.collation_ids, transactionManager, transaction, false, 4, 6, null, null) == null) {
                throw StandardException.newException("XSCH1.S", new Long(this.id.getContainerId()));
            }
            transactionManager2 = (TransactionManager)transactionManager.startNestedUserTransaction(false, true);
            final OpenHeap openHeap = new OpenHeap();
            if (openHeap.init(null, this, this.format_ids, this.collation_ids, transactionManager2, transactionManager2.getRawStoreXact(), true, 4, 6, transactionManager2.getRawStoreXact().newLockingPolicy(1, 4, true), null) == null) {
                throw StandardException.newException("XSCH1.S", new Long(this.id.getContainerId()).toString());
            }
            heapController = new HeapController();
            heapController.init(openHeap);
            long pageNumber;
            for (Page page = openHeap.getContainer().getFirstPage(); page != null; page = openHeap.getContainer().getNextPage(pageNumber)) {
                pageNumber = page.getPageNumber();
                if (heapController.purgeCommittedDeletes(page)) {
                    openHeap.getXactMgr().commitNoSync(1);
                    heapController.closeForEndTransaction(false);
                    openHeap.reopen();
                }
                else {
                    page.unlatch();
                }
            }
        }
        finally {
            if (openConglomerate != null) {
                openConglomerate.close();
            }
            if (heapController != null) {
                heapController.close();
            }
            if (transactionManager2 != null) {
                transactionManager2.commitNoSync(1);
                transactionManager2.destroy();
            }
        }
    }
    
    public void compressConglomerate(final TransactionManager transactionManager, final Transaction transaction) throws StandardException {
        OpenConglomerate openConglomerate = null;
        try {
            openConglomerate = new OpenHeap();
            if (openConglomerate.init(null, this, this.format_ids, this.collation_ids, transactionManager, transaction, false, 4, 7, transaction.newLockingPolicy(2, 4, true), null) == null) {
                throw StandardException.newException("XSCH1.S", new Long(this.id.getContainerId()));
            }
            new HeapController().init(openConglomerate);
            openConglomerate.getContainer().compressContainer();
        }
        finally {
            if (openConglomerate != null) {
                openConglomerate.close();
            }
        }
    }
    
    public ScanManager defragmentConglomerate(final TransactionManager transactionManager, final Transaction transaction, final boolean b, final int n, final int n2, final LockingPolicy lockingPolicy, final int n3) throws StandardException {
        final OpenHeap openHeap = new OpenHeap();
        if (openHeap.init(null, this, this.format_ids, this.collation_ids, transactionManager, transaction, b, n, n2, transaction.newLockingPolicy(1, 4, true), null) == null) {
            throw StandardException.newException("XSCH1.S", new Long(this.id.getContainerId()));
        }
        final HeapCompressScan heapCompressScan = new HeapCompressScan();
        heapCompressScan.init(openHeap, null, null, 0, null, null, 0);
        return heapCompressScan;
    }
    
    public StoreCostController openStoreCost(final TransactionManager transactionManager, final Transaction transaction) throws StandardException {
        final OpenHeap openHeap = new OpenHeap();
        if (openHeap.init(null, this, this.format_ids, this.collation_ids, transactionManager, transaction, false, 8, 7, null, null) == null) {
            throw StandardException.newException("XSCH1.S", new Long(this.id.getContainerId()));
        }
        final HeapCostController heapCostController = new HeapCostController();
        heapCostController.init(openHeap);
        return heapCostController;
    }
    
    public String toString() {
        return (this.id == null) ? "null" : this.id.toString();
    }
    
    public DataValueDescriptor getConglom() {
        return this;
    }
    
    public int getTypeFormatId() {
        return 467;
    }
    
    public boolean isNull() {
        return this.id == null;
    }
    
    public void restoreToNull() {
        this.id = null;
    }
    
    protected void writeExternal_v10_2(final ObjectOutput objectOutput) throws IOException {
        FormatIdUtil.writeFormatIdInteger(objectOutput, this.conglom_format_id);
        objectOutput.writeInt((int)this.id.getSegmentId());
        objectOutput.writeLong(this.id.getContainerId());
        objectOutput.writeInt(this.format_ids.length);
        ConglomerateUtil.writeFormatIdArray(this.format_ids, objectOutput);
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        this.writeExternal_v10_2(objectOutput);
        if (this.conglom_format_id == 467) {
            ConglomerateUtil.writeCollationIdArray(this.collation_ids, objectOutput);
        }
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.conglom_format_id = FormatIdUtil.readFormatIdInteger(objectInput);
        this.id = new ContainerKey(objectInput.readInt(), objectInput.readLong());
        this.format_ids = ConglomerateUtil.readFormatIdArray(objectInput.readInt(), objectInput);
        this.collation_ids = new int[this.format_ids.length];
        for (int i = 0; i < this.format_ids.length; ++i) {
            this.collation_ids[i] = 0;
        }
        if (this.conglom_format_id == 467) {
            this.hasCollatedTypes = ConglomerateUtil.readCollationIdArray(this.collation_ids, objectInput);
        }
        else if (this.conglom_format_id != 91) {}
    }
    
    static {
        BASE_MEMORY_USAGE = ClassSize.estimateBaseFromCatalog(Heap.class);
        CONTAINER_KEY_MEMORY_USAGE = ClassSize.estimateBaseFromCatalog(ContainerKey.class);
    }
}
