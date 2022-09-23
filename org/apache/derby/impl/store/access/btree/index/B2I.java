// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.btree.index;

import org.apache.derby.iapi.services.cache.ClassSize;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.store.access.StoreCostController;
import org.apache.derby.iapi.store.access.conglomerate.ScanManager;
import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.store.access.StaticCompiledOpenConglomInfo;
import org.apache.derby.iapi.store.access.RowLocationRetRowSource;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.store.raw.LockingPolicy;
import org.apache.derby.impl.store.access.btree.LeafControlRow;
import org.apache.derby.iapi.store.access.DynamicCompiledOpenConglomInfo;
import org.apache.derby.iapi.store.access.conglomerate.LogicalUndo;
import org.apache.derby.iapi.store.raw.ContainerHandle;
import org.apache.derby.impl.store.access.conglomerate.GenericConglomerate;
import org.apache.derby.impl.store.access.conglomerate.ConglomerateUtil;
import java.util.Properties;
import org.apache.derby.iapi.store.access.ColumnOrdering;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.store.access.conglomerate.TransactionManager;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.impl.store.access.btree.BTreeLockingPolicy;
import org.apache.derby.impl.store.access.btree.OpenBTree;
import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.impl.store.access.btree.BTree;

public class B2I extends BTree
{
    private static final String PROPERTY_BASECONGLOMID = "baseConglomerateId";
    private static final String PROPERTY_ROWLOCCOLUMN = "rowLocationColumn";
    static final int FORMAT_NUMBER = 470;
    long baseConglomerateId;
    int rowLocationColumn;
    private static final int BASE_MEMORY_USAGE;
    
    public int estimateMemoryUsage() {
        return B2I.BASE_MEMORY_USAGE;
    }
    
    protected BTreeLockingPolicy getBtreeLockingPolicy(final Transaction transaction, final int n, final int n2, final int n3, final ConglomerateController conglomerateController, final OpenBTree openBTree) throws StandardException {
        BTreeLockingPolicy bTreeLockingPolicy = null;
        if (n == 7) {
            bTreeLockingPolicy = new B2ITableLocking3(transaction, n, transaction.newLockingPolicy(2, n3, true), conglomerateController, openBTree);
        }
        else if (n == 6) {
            if (n3 == 5) {
                bTreeLockingPolicy = new B2IRowLocking3(transaction, n, transaction.newLockingPolicy(1, n3, true), conglomerateController, openBTree);
            }
            else if (n3 == 4) {
                bTreeLockingPolicy = new B2IRowLockingRR(transaction, n, transaction.newLockingPolicy(1, n3, true), conglomerateController, openBTree);
            }
            else if (n3 == 2 || n3 == 3) {
                bTreeLockingPolicy = new B2IRowLocking2(transaction, n, transaction.newLockingPolicy(1, n3, true), conglomerateController, openBTree);
            }
            else if (n3 == 1) {
                bTreeLockingPolicy = new B2IRowLocking1(transaction, n, transaction.newLockingPolicy(1, n3, true), conglomerateController, openBTree);
            }
        }
        return bTreeLockingPolicy;
    }
    
    public final ConglomerateController lockTable(final TransactionManager transactionManager, int n, final int n2, final int n3) throws StandardException {
        n |= 0x40;
        return transactionManager.openConglomerate(this.baseConglomerateId, false, n, n2, n3);
    }
    
    private void traverseRight() {
    }
    
    public void create(final TransactionManager transactionManager, final int n, final long n2, final DataValueDescriptor[] array, final ColumnOrdering[] array2, final int[] array3, final Properties properties, final int n3) throws StandardException {
        final Transaction rawStoreXact = transactionManager.getRawStoreXact();
        if (properties == null) {
            throw StandardException.newException("XSCB2.S", "baseConglomerateId");
        }
        final String property = properties.getProperty("baseConglomerateId");
        if (property == null) {
            throw StandardException.newException("XSCB2.S", "baseConglomerateId");
        }
        this.baseConglomerateId = Long.parseLong(property);
        final String property2 = properties.getProperty("rowLocationColumn");
        if (property2 == null) {
            throw StandardException.newException("XSCB2.S", "baseConglomerateId");
        }
        this.rowLocationColumn = Integer.parseInt(property2);
        this.ascDescInfo = new boolean[array.length];
        for (int i = 0; i < this.ascDescInfo.length; ++i) {
            if (array2 != null && i < array2.length) {
                this.ascDescInfo[i] = array2[i].getIsAscending();
            }
            else {
                this.ascDescInfo[i] = true;
            }
        }
        this.collation_ids = ConglomerateUtil.createCollationIds(array.length, array3);
        this.hasCollatedTypes = GenericConglomerate.hasCollatedColumns(this.collation_ids);
        super.create(rawStoreXact, n, n2, array, properties, this.getTypeFormatId(), n3);
        final ConglomerateController openConglomerate = transactionManager.openConglomerate(this.baseConglomerateId, false, 64, 7, 5);
        final OpenBTree openBTree = new OpenBTree();
        openBTree.init(transactionManager, transactionManager, null, rawStoreXact, false, 4, 7, new B2ITableLocking3(rawStoreXact, 7, rawStoreXact.newLockingPolicy(2, 5, true), openConglomerate, openBTree), this, null, null);
        LeafControlRow.initEmptyBtree(openBTree);
        openBTree.close();
        openConglomerate.close();
    }
    
    public boolean fetchMaxOnBTree(final TransactionManager transactionManager, final Transaction transaction, final long n, final int n2, final int n3, final LockingPolicy lockingPolicy, final int n4, final FormatableBitSet set, final DataValueDescriptor[] array) throws StandardException {
        final B2IMaxScan b2IMaxScan = new B2IMaxScan();
        b2IMaxScan.init(transactionManager, transaction, n2, n3, lockingPolicy, n4, true, set, this, new B2IUndo());
        final boolean fetchMax = b2IMaxScan.fetchMax(array);
        b2IMaxScan.close();
        return fetchMax;
    }
    
    public long load(final TransactionManager transactionManager, final boolean b, final RowLocationRetRowSource rowLocationRetRowSource) throws StandardException {
        long load = 0L;
        final B2IController b2IController = new B2IController();
        try {
            int n = 4;
            if (b) {
                n |= 0x3;
            }
            b2IController.init(transactionManager, transactionManager.getRawStoreXact(), false, n, 7, transactionManager.getRawStoreXact().newLockingPolicy(2, 5, true), true, this, new B2IUndo(), null, null);
            load = b2IController.load(transactionManager, b, rowLocationRetRowSource);
        }
        finally {
            b2IController.close();
        }
        return load;
    }
    
    public ConglomerateController open(final TransactionManager transactionManager, final Transaction transaction, final boolean b, final int n, final int n2, final LockingPolicy lockingPolicy, final StaticCompiledOpenConglomInfo staticCompiledOpenConglomInfo, final DynamicCompiledOpenConglomInfo dynamicCompiledOpenConglomInfo) throws StandardException {
        final B2IController b2IController = new B2IController();
        b2IController.init(transactionManager, transaction, b, n, n2, lockingPolicy, true, this, new B2IUndo(), (B2IStaticCompiledInfo)staticCompiledOpenConglomInfo, dynamicCompiledOpenConglomInfo);
        return b2IController;
    }
    
    public ScanManager openScan(final TransactionManager transactionManager, final Transaction transaction, final boolean b, final int n, final int n2, final LockingPolicy lockingPolicy, final int n3, final FormatableBitSet set, final DataValueDescriptor[] array, final int n4, final Qualifier[][] array2, final DataValueDescriptor[] array3, final int n5, final StaticCompiledOpenConglomInfo staticCompiledOpenConglomInfo, final DynamicCompiledOpenConglomInfo dynamicCompiledOpenConglomInfo) throws StandardException {
        final B2IForwardScan b2IForwardScan = new B2IForwardScan();
        b2IForwardScan.init(transactionManager, transaction, b, n, n2, lockingPolicy, n3, true, set, array, n4, array2, array3, n5, this, new B2IUndo(), (B2IStaticCompiledInfo)staticCompiledOpenConglomInfo, dynamicCompiledOpenConglomInfo);
        return b2IForwardScan;
    }
    
    public ScanManager defragmentConglomerate(final TransactionManager transactionManager, final Transaction transaction, final boolean b, final int n, final int n2, final LockingPolicy lockingPolicy, final int n3) throws StandardException {
        throw StandardException.newException("XSCB3.S");
    }
    
    public void purgeConglomerate(final TransactionManager transactionManager, final Transaction transaction) throws StandardException {
    }
    
    public void compressConglomerate(final TransactionManager transactionManager, final Transaction transaction) throws StandardException {
        final B2IController b2IController = new B2IController();
        try {
            b2IController.init(transactionManager, transactionManager.getRawStoreXact(), false, 4, 7, transactionManager.getRawStoreXact().newLockingPolicy(2, 5, true), true, this, new B2IUndo(), null, null);
            b2IController.getContainer().compressContainer();
        }
        finally {
            b2IController.close();
        }
    }
    
    public StoreCostController openStoreCost(final TransactionManager transactionManager, final Transaction transaction) throws StandardException {
        final B2ICostController b2ICostController = new B2ICostController();
        b2ICostController.init(transactionManager, this, transaction);
        return b2ICostController;
    }
    
    public void drop(final TransactionManager transactionManager) throws StandardException {
        final ConglomerateController lockTable = this.lockTable(transactionManager, 4, 7, 4);
        transactionManager.getRawStoreXact().dropContainer(this.id);
        if (lockTable != null) {
            lockTable.close();
        }
    }
    
    public StaticCompiledOpenConglomInfo getStaticCompiledConglomInfo(final TransactionController transactionController, final long n) throws StandardException {
        return new B2IStaticCompiledInfo(transactionController, this);
    }
    
    public int getTypeFormatId() {
        return 470;
    }
    
    public void writeExternal_v10_2(final ObjectOutput objectOutput) throws IOException {
        super.writeExternal(objectOutput);
        objectOutput.writeLong(this.baseConglomerateId);
        objectOutput.writeInt(this.rowLocationColumn);
        final FormatableBitSet set = new FormatableBitSet(this.ascDescInfo.length);
        for (int i = 0; i < this.ascDescInfo.length; ++i) {
            if (this.ascDescInfo[i]) {
                set.set(i);
            }
        }
        set.writeExternal(objectOutput);
    }
    
    public void writeExternal_v10_3(final ObjectOutput objectOutput) throws IOException {
        this.writeExternal_v10_2(objectOutput);
        if (this.conglom_format_id == 466 || this.conglom_format_id == 470) {
            ConglomerateUtil.writeCollationIdArray(this.collation_ids, objectOutput);
        }
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        this.writeExternal_v10_3(objectOutput);
        if (this.conglom_format_id == 470) {
            objectOutput.writeBoolean(this.isUniqueWithDuplicateNulls());
        }
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        this.baseConglomerateId = objectInput.readLong();
        this.rowLocationColumn = objectInput.readInt();
        final FormatableBitSet set = new FormatableBitSet();
        set.readExternal(objectInput);
        this.ascDescInfo = new boolean[set.getLength()];
        for (int i = 0; i < set.getLength(); ++i) {
            this.ascDescInfo[i] = set.isSet(i);
        }
        this.collation_ids = new int[this.format_ids.length];
        for (int j = 0; j < this.format_ids.length; ++j) {
            this.collation_ids[j] = 0;
        }
        this.setUniqueWithDuplicateNulls(false);
        if (this.conglom_format_id == 466 || this.conglom_format_id == 470) {
            this.hasCollatedTypes = ConglomerateUtil.readCollationIdArray(this.collation_ids, objectInput);
        }
        else if (this.conglom_format_id != 388) {}
        if (this.conglom_format_id == 470) {
            this.setUniqueWithDuplicateNulls(objectInput.readBoolean());
        }
    }
    
    static {
        BASE_MEMORY_USAGE = ClassSize.estimateBaseFromCatalog(B2I.class);
    }
}
