// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access;

import org.apache.derby.iapi.services.locks.CompatibilitySpace;
import org.apache.derby.iapi.store.access.FileResource;
import org.apache.derby.iapi.store.access.AccessFactory;
import org.apache.derby.iapi.services.daemon.Serviceable;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.store.access.DatabaseInstant;
import org.apache.derby.iapi.store.access.conglomerate.ScanControllerRowSource;
import org.apache.derby.iapi.store.access.SortCostController;
import java.io.Serializable;
import org.apache.derby.iapi.util.ReuseFactory;
import org.apache.derby.iapi.store.access.conglomerate.SortFactory;
import org.apache.derby.iapi.store.access.SortObserver;
import org.apache.derby.iapi.store.access.StoreCostController;
import org.apache.derby.iapi.store.access.GroupFetchScanController;
import org.apache.derby.iapi.store.access.BackingStoreHashtable;
import org.apache.derby.iapi.store.raw.Loggable;
import org.apache.derby.impl.store.access.conglomerate.ConglomerateUtil;
import org.apache.derby.iapi.store.access.RowLocationRetRowSource;
import org.apache.derby.iapi.store.access.conglomerate.MethodFactory;
import org.apache.derby.iapi.store.access.conglomerate.ConglomerateFactory;
import java.util.Properties;
import org.apache.derby.iapi.store.access.ColumnOrdering;
import org.apache.derby.iapi.services.io.Storable;
import org.apache.derby.iapi.store.access.ScanController;
import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.store.access.DynamicCompiledOpenConglomInfo;
import org.apache.derby.iapi.store.access.StaticCompiledOpenConglomInfo;
import org.apache.derby.iapi.store.access.conglomerate.Conglomerate;
import org.apache.derby.iapi.store.raw.LockingPolicy;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.store.access.conglomerate.Sort;
import org.apache.derby.iapi.store.access.SortController;
import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.iapi.store.access.conglomerate.ScanManager;
import org.apache.derby.iapi.error.StandardException;
import java.util.HashMap;
import java.util.ArrayList;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.store.access.conglomerate.TransactionManager;
import org.apache.derby.iapi.store.access.XATransactionController;

public class RAMTransaction implements XATransactionController, TransactionManager
{
    protected Transaction rawtran;
    protected RAMAccessManager accessmanager;
    protected RAMTransactionContext context;
    protected RAMTransaction parent_tran;
    private ArrayList scanControllers;
    private ArrayList conglomerateControllers;
    private ArrayList sorts;
    private ArrayList sortControllers;
    private ArrayList freeSortIds;
    protected HashMap tempCongloms;
    private long nextTempConglomId;
    private boolean alterTableCallMade;
    private int transaction_lock_level;
    
    private final void init(final RAMAccessManager accessmanager, final Transaction rawtran, final RAMTransaction parent_tran) {
        this.rawtran = rawtran;
        this.parent_tran = parent_tran;
        this.accessmanager = accessmanager;
        this.scanControllers = new ArrayList();
        this.conglomerateControllers = new ArrayList();
        this.sorts = null;
        this.freeSortIds = null;
        this.sortControllers = null;
        if (parent_tran != null) {
            this.tempCongloms = parent_tran.tempCongloms;
        }
        else {
            this.tempCongloms = null;
        }
    }
    
    protected RAMTransaction(final RAMAccessManager ramAccessManager, final Transaction transaction, final RAMTransaction ramTransaction) throws StandardException {
        this.nextTempConglomId = -1L;
        this.alterTableCallMade = false;
        this.init(ramAccessManager, transaction, ramTransaction);
    }
    
    RAMTransaction(final RAMAccessManager ramAccessManager, final RAMTransaction ramTransaction, final int n, final byte[] array, final byte[] array2) throws StandardException {
        this.nextTempConglomId = -1L;
        this.alterTableCallMade = false;
        this.init(ramAccessManager, ramTransaction.getRawStoreXact(), null);
        (this.context = ramTransaction.context).setTransaction(this);
        this.rawtran.createXATransactionFromLocalTransaction(n, array, array2);
        ramTransaction.rawtran = null;
    }
    
    RAMTransaction() {
        this.nextTempConglomId = -1L;
        this.alterTableCallMade = false;
    }
    
    protected void closeControllers(final boolean b) throws StandardException {
        if (!this.scanControllers.isEmpty()) {
            for (int i = this.scanControllers.size() - 1; i >= 0; --i) {
                if (((ScanManager)this.scanControllers.get(i)).closeForEndTransaction(b)) {}
            }
            if (b) {
                this.scanControllers.clear();
            }
        }
        if (!this.conglomerateControllers.isEmpty()) {
            for (int j = this.conglomerateControllers.size() - 1; j >= 0; --j) {
                if (((ConglomerateController)this.conglomerateControllers.get(j)).closeForEndTransaction(b)) {}
            }
            if (b) {
                this.conglomerateControllers.clear();
            }
        }
        if (this.sortControllers != null && !this.sortControllers.isEmpty() && b) {
            for (int k = this.sortControllers.size() - 1; k >= 0; --k) {
                ((SortController)this.sortControllers.get(k)).completedInserts();
            }
            this.sortControllers.clear();
        }
        if (this.sorts != null && !this.sorts.isEmpty() && b) {
            for (int l = this.sorts.size() - 1; l >= 0; --l) {
                final Sort sort = this.sorts.get(l);
                if (sort != null) {
                    sort.drop(this);
                }
            }
            this.sorts.clear();
            this.freeSortIds.clear();
        }
    }
    
    private LockingPolicy determine_locking_policy(final int n, final int n2) {
        LockingPolicy lockingPolicy;
        if (this.accessmanager.getSystemLockLevel() == 7 || n == 7) {
            lockingPolicy = this.accessmanager.table_level_policy[n2];
        }
        else {
            lockingPolicy = this.accessmanager.record_level_policy[n2];
        }
        return lockingPolicy;
    }
    
    private int determine_lock_level(final int n) {
        int n2;
        if (this.accessmanager.getSystemLockLevel() == 7 || n == 7) {
            n2 = 7;
        }
        else {
            n2 = 6;
        }
        return n2;
    }
    
    private Conglomerate findExistingConglomerate(final long value) throws StandardException {
        final Conglomerate conglomerate = this.findConglomerate(value);
        if (conglomerate == null) {
            throw StandardException.newException("XSAI2.S", new Long(value));
        }
        return conglomerate;
    }
    
    private Conglomerate findConglomerate(final long value) throws StandardException {
        Conglomerate conglomCacheFind = null;
        if (value >= 0L) {
            conglomCacheFind = this.accessmanager.conglomCacheFind(this, value);
        }
        else if (this.tempCongloms != null) {
            conglomCacheFind = this.tempCongloms.get(new Long(value));
        }
        return conglomCacheFind;
    }
    
    void setContext(final RAMTransactionContext context) {
        this.context = context;
    }
    
    private ConglomerateController openConglomerate(final Conglomerate conglomerate, final boolean b, final int n, final int n2, final int n3, final StaticCompiledOpenConglomInfo staticCompiledOpenConglomInfo, final DynamicCompiledOpenConglomInfo dynamicCompiledOpenConglomInfo) throws StandardException {
        final ConglomerateController open = conglomerate.open(this, this.rawtran, b, n, this.determine_lock_level(n2), this.determine_locking_policy(n2, n3), staticCompiledOpenConglomInfo, dynamicCompiledOpenConglomInfo);
        this.conglomerateControllers.add(open);
        return open;
    }
    
    private ScanController openScan(final Conglomerate conglomerate, final boolean b, final int n, final int n2, final int n3, final FormatableBitSet set, final DataValueDescriptor[] array, final int n4, final Qualifier[][] array2, final DataValueDescriptor[] array3, final int n5, final StaticCompiledOpenConglomInfo staticCompiledOpenConglomInfo, final DynamicCompiledOpenConglomInfo dynamicCompiledOpenConglomInfo) throws StandardException {
        final ScanManager openScan = conglomerate.openScan(this, this.rawtran, b, n, this.determine_lock_level(n2), this.determine_locking_policy(n2, n3), n3, set, array, n4, array2, array3, n5, staticCompiledOpenConglomInfo, dynamicCompiledOpenConglomInfo);
        this.scanControllers.add(openScan);
        return openScan;
    }
    
    protected void invalidateConglomerateCache() throws StandardException {
        if (this.alterTableCallMade) {
            this.accessmanager.conglomCacheInvalidate();
            this.alterTableCallMade = false;
        }
    }
    
    public void addColumnToConglomerate(final long value, final int n, final Storable storable, final int n2) throws StandardException {
        final boolean b = value < 0L;
        final Conglomerate conglomerate = this.findConglomerate(value);
        if (conglomerate == null) {
            throw StandardException.newException("XSAM2.S", new Long(value));
        }
        final ConglomerateController open = conglomerate.open(this, this.rawtran, false, 4, 7, this.accessmanager.table_level_policy[5], null, null);
        conglomerate.addColumn(this, n, storable, n2);
        if (!b) {
            this.alterTableCallMade = true;
        }
        open.close();
    }
    
    public StaticCompiledOpenConglomInfo getStaticCompiledConglomInfo(final long n) throws StandardException {
        return this.findExistingConglomerate(n).getStaticCompiledConglomInfo(this, n);
    }
    
    public DynamicCompiledOpenConglomInfo getDynamicCompiledConglomInfo(final long n) throws StandardException {
        return this.findExistingConglomerate(n).getDynamicCompiledConglomInfo();
    }
    
    private final int countCreatedSorts() {
        int n = 0;
        if (this.sorts != null) {
            for (int i = 0; i < this.sorts.size(); ++i) {
                if (this.sorts.get(i) != null) {
                    ++n;
                }
            }
        }
        return n;
    }
    
    public int countOpens(final int n) throws StandardException {
        int n2 = -1;
        switch (n) {
            case 1: {
                n2 = this.conglomerateControllers.size();
                break;
            }
            case 2: {
                n2 = this.scanControllers.size();
                break;
            }
            case 3: {
                n2 = this.countCreatedSorts();
                break;
            }
            case 4: {
                n2 = ((this.sortControllers != null) ? this.sortControllers.size() : 0);
                break;
            }
            case 5: {
                n2 = this.conglomerateControllers.size() + this.scanControllers.size() + ((this.sortControllers != null) ? this.sortControllers.size() : 0) + this.countCreatedSorts();
                break;
            }
        }
        return n2;
    }
    
    public long createConglomerate(final String s, final DataValueDescriptor[] array, final ColumnOrdering[] array2, final int[] array3, final Properties properties, final int n) throws StandardException {
        final MethodFactory methodFactoryByImpl = this.accessmanager.findMethodFactoryByImpl(s);
        if (methodFactoryByImpl == null || !(methodFactoryByImpl instanceof ConglomerateFactory)) {
            throw StandardException.newException("XSAM3.S", s);
        }
        final ConglomerateFactory conglomerateFactory = (ConglomerateFactory)methodFactoryByImpl;
        int n2;
        long nextConglomId;
        if ((n & 0x1) == 0x1) {
            n2 = -1;
            nextConglomId = 0L;
        }
        else {
            n2 = 0;
            nextConglomId = this.accessmanager.getNextConglomId(conglomerateFactory.getConglomerateFactoryId());
        }
        final Conglomerate conglomerate = conglomerateFactory.createConglomerate(this, n2, nextConglomId, array, array2, array3, properties, n);
        long containerid;
        if ((n & 0x1) == 0x1) {
            containerid = this.nextTempConglomId--;
            if (this.tempCongloms == null) {
                this.tempCongloms = new HashMap();
            }
            this.tempCongloms.put(new Long(containerid), conglomerate);
        }
        else {
            containerid = conglomerate.getContainerid();
            this.accessmanager.conglomCacheAddEntry(containerid, conglomerate);
        }
        return containerid;
    }
    
    public long createAndLoadConglomerate(final String s, final DataValueDescriptor[] array, final ColumnOrdering[] array2, final int[] array3, final Properties properties, final int n, final RowLocationRetRowSource rowLocationRetRowSource, final long[] array4) throws StandardException {
        return this.recreateAndLoadConglomerate(s, true, array, array2, array3, properties, n, 0L, rowLocationRetRowSource, array4);
    }
    
    public long recreateAndLoadConglomerate(final String s, final boolean b, final DataValueDescriptor[] array, final ColumnOrdering[] array2, final int[] array3, final Properties properties, final int n, final long n2, final RowLocationRetRowSource rowLocationRetRowSource, final long[] array4) throws StandardException {
        long conglomerate = this.createConglomerate(s, array, array2, array3, properties, n);
        final long loadConglomerate = this.loadConglomerate(conglomerate, true, rowLocationRetRowSource);
        if (array4 != null) {
            array4[0] = loadConglomerate;
        }
        if (!b && loadConglomerate == 0L) {
            this.dropConglomerate(conglomerate);
            conglomerate = n2;
        }
        return conglomerate;
    }
    
    public String debugOpened() throws StandardException {
        return null;
    }
    
    public boolean conglomerateExists(final long n) throws StandardException {
        return this.findConglomerate(n) != null;
    }
    
    public void dropConglomerate(final long value) throws StandardException {
        this.findExistingConglomerate(value).drop(this);
        if (value < 0L) {
            if (this.tempCongloms != null) {
                this.tempCongloms.remove(new Long(value));
            }
        }
        else {
            this.accessmanager.conglomCacheRemoveEntry(value);
        }
    }
    
    public boolean fetchMaxOnBtree(final long n, final int n2, final int n3, final int n4, final FormatableBitSet set, final DataValueDescriptor[] array) throws StandardException {
        return this.findExistingConglomerate(n).fetchMaxOnBTree(this, this.rawtran, n, n2, n3, this.determine_locking_policy(n3, n4), n4, set, array);
    }
    
    public Properties getUserCreateConglomPropList() {
        return ConglomerateUtil.createUserRawStorePropertySet(null);
    }
    
    public boolean isIdle() {
        return this.rawtran.isIdle();
    }
    
    public boolean isGlobal() {
        return this.rawtran.getGlobalId() != null;
    }
    
    public boolean isPristine() {
        return this.rawtran.isPristine();
    }
    
    public Object createXATransactionFromLocalTransaction(final int n, final byte[] array, final byte[] array2) throws StandardException {
        this.getRawStoreXact().createXATransactionFromLocalTransaction(n, array, array2);
        return this;
    }
    
    public long loadConglomerate(final long n, final boolean b, final RowLocationRetRowSource rowLocationRetRowSource) throws StandardException {
        return this.findExistingConglomerate(n).load(this, b, rowLocationRetRowSource);
    }
    
    public void loadConglomerate(final long n, final RowLocationRetRowSource rowLocationRetRowSource) throws StandardException {
        this.loadConglomerate(n, false, rowLocationRetRowSource);
    }
    
    public void logAndDo(final Loggable loggable) throws StandardException {
        this.rawtran.logAndDo(loggable);
    }
    
    public ConglomerateController openCompiledConglomerate(final boolean b, final int n, final int n2, final int n3, final StaticCompiledOpenConglomInfo staticCompiledOpenConglomInfo, final DynamicCompiledOpenConglomInfo dynamicCompiledOpenConglomInfo) throws StandardException {
        return this.openConglomerate((Conglomerate)staticCompiledOpenConglomInfo.getConglom(), b, n, n2, n3, staticCompiledOpenConglomInfo, dynamicCompiledOpenConglomInfo);
    }
    
    public ConglomerateController openConglomerate(final long n, final boolean b, final int n2, final int n3, final int n4) throws StandardException {
        return this.openConglomerate(this.findExistingConglomerate(n), b, n2, n3, n4, null, null);
    }
    
    public long findConglomid(final long n) throws StandardException {
        return n;
    }
    
    public long findContainerid(final long n) throws StandardException {
        return n;
    }
    
    public BackingStoreHashtable createBackingStoreHashtableFromScan(final long n, final int n2, final int n3, final int n4, final FormatableBitSet set, final DataValueDescriptor[] array, final int n5, final Qualifier[][] array2, final DataValueDescriptor[] array3, final int n6, final long n7, final int[] array4, final boolean b, final long n8, final long n9, final int n10, final float n11, final boolean b2, final boolean b3, final boolean b4) throws StandardException {
        return new BackingStoreHashTableFromScan(this, n, n2, n3, n4, set, array, n5, array2, array3, n6, n7, array4, b, n8, n9, n10, n11, b2, b3, b4);
    }
    
    public GroupFetchScanController openGroupFetchScan(final long n, final boolean b, final int n2, final int n3, final int n4, final FormatableBitSet set, final DataValueDescriptor[] array, final int n5, final Qualifier[][] array2, final DataValueDescriptor[] array3, final int n6) throws StandardException {
        final ScanManager openScan = this.findExistingConglomerate(n).openScan(this, this.rawtran, b, n2, this.determine_lock_level(n3), this.determine_locking_policy(n3, n4), n4, set, array, n5, array2, array3, n6, null, null);
        this.scanControllers.add(openScan);
        return openScan;
    }
    
    public void purgeConglomerate(final long n) throws StandardException {
        this.findExistingConglomerate(n).purgeConglomerate(this, this.rawtran);
    }
    
    public void compressConglomerate(final long n) throws StandardException {
        this.findExistingConglomerate(n).compressConglomerate(this, this.rawtran);
    }
    
    public GroupFetchScanController defragmentConglomerate(final long n, final boolean b, final boolean b2, final int n2, final int n3, final int n4) throws StandardException {
        final ScanManager defragmentConglomerate = this.findExistingConglomerate(n).defragmentConglomerate(this, this.rawtran, b2, n2, this.determine_lock_level(n3), this.determine_locking_policy(n3, n4), n4);
        this.scanControllers.add(defragmentConglomerate);
        return defragmentConglomerate;
    }
    
    public ScanController openScan(final long n, final boolean b, final int n2, final int n3, final int n4, final FormatableBitSet set, final DataValueDescriptor[] array, final int n5, final Qualifier[][] array2, final DataValueDescriptor[] array3, final int n6) throws StandardException {
        return this.openScan(this.findExistingConglomerate(n), b, n2, n3, n4, set, array, n5, array2, array3, n6, null, null);
    }
    
    public ScanController openCompiledScan(final boolean b, final int n, final int n2, final int n3, final FormatableBitSet set, final DataValueDescriptor[] array, final int n4, final Qualifier[][] array2, final DataValueDescriptor[] array3, final int n5, final StaticCompiledOpenConglomInfo staticCompiledOpenConglomInfo, final DynamicCompiledOpenConglomInfo dynamicCompiledOpenConglomInfo) throws StandardException {
        return this.openScan((Conglomerate)staticCompiledOpenConglomInfo.getConglom(), b, n, n2, n3, set, array, n4, array2, array3, n5, staticCompiledOpenConglomInfo, dynamicCompiledOpenConglomInfo);
    }
    
    public StoreCostController openStoreCost(final long n) throws StandardException {
        return this.findExistingConglomerate(n).openStoreCost(this, this.rawtran);
    }
    
    public long createSort(final Properties properties, final DataValueDescriptor[] array, final ColumnOrdering[] array2, final SortObserver sortObserver, final boolean b, final long n, final int n2) throws StandardException {
        String property = null;
        if (properties != null) {
            property = properties.getProperty("implType");
        }
        if (property == null) {
            property = "sort external";
        }
        final MethodFactory methodFactoryByImpl = this.accessmanager.findMethodFactoryByImpl(property);
        if (methodFactoryByImpl == null || !(methodFactoryByImpl instanceof SortFactory)) {
            throw StandardException.newException("XSAM0.S", property);
        }
        final Sort sort = ((SortFactory)methodFactoryByImpl).createSort(this, 0, properties, array, array2, sortObserver, b, n, n2);
        if (this.sorts == null) {
            this.sorts = new ArrayList();
            this.freeSortIds = new ArrayList();
        }
        int index;
        if (this.freeSortIds.isEmpty()) {
            index = this.sorts.size();
            this.sorts.add(sort);
        }
        else {
            index = this.freeSortIds.remove(this.freeSortIds.size() - 1);
            this.sorts.set(index, sort);
        }
        return index;
    }
    
    public void dropSort(final long n) throws StandardException {
        final Sort sort = this.sorts.get((int)n);
        if (sort != null) {
            sort.drop(this);
            this.sorts.set((int)n, null);
            this.freeSortIds.add(ReuseFactory.getInteger((int)n));
        }
    }
    
    public Serializable getProperty(final String s) throws StandardException {
        return this.accessmanager.getTransactionalProperties().getProperty(this, s);
    }
    
    public Serializable getPropertyDefault(final String s) throws StandardException {
        return this.accessmanager.getTransactionalProperties().getPropertyDefault(this, s);
    }
    
    public void setProperty(final String s, final Serializable s2, final boolean b) throws StandardException {
        this.accessmanager.getTransactionalProperties().setProperty(this, s, s2, b);
    }
    
    public void setPropertyDefault(final String s, final Serializable s2) throws StandardException {
        this.accessmanager.getTransactionalProperties().setPropertyDefault(this, s, s2);
    }
    
    public boolean propertyDefaultIsVisible(final String s) throws StandardException {
        return this.accessmanager.getTransactionalProperties().propertyDefaultIsVisible(this, s);
    }
    
    public Properties getProperties() throws StandardException {
        return this.accessmanager.getTransactionalProperties().getProperties(this);
    }
    
    public SortController openSort(final long value) throws StandardException {
        final Sort sort;
        if (this.sorts == null || value >= this.sorts.size() || (sort = this.sorts.get((int)value)) == null) {
            throw StandardException.newException("XSAM4.S", new Long(value));
        }
        final SortController open = sort.open(this);
        if (this.sortControllers == null) {
            this.sortControllers = new ArrayList();
        }
        this.sortControllers.add(open);
        return open;
    }
    
    public SortCostController openSortCostController() throws StandardException {
        String s = null;
        if (s == null) {
            s = "sort external";
        }
        final MethodFactory methodFactoryByImpl = this.accessmanager.findMethodFactoryByImpl(s);
        if (methodFactoryByImpl == null || !(methodFactoryByImpl instanceof SortFactory)) {
            throw StandardException.newException("XSAM0.S", s);
        }
        return ((SortFactory)methodFactoryByImpl).openSortCostController();
    }
    
    public ScanController openSortScan(final long value, final boolean b) throws StandardException {
        final Sort sort;
        if (this.sorts == null || value >= this.sorts.size() || (sort = this.sorts.get((int)value)) == null) {
            throw StandardException.newException("XSAM4.S", new Long(value));
        }
        final ScanController openSortScan = sort.openSortScan(this, b);
        this.scanControllers.add(openSortScan);
        return openSortScan;
    }
    
    public RowLocationRetRowSource openSortRowSource(final long value) throws StandardException {
        final Sort sort;
        if (this.sorts == null || value >= this.sorts.size() || (sort = this.sorts.get((int)value)) == null) {
            throw StandardException.newException("XSAM4.S", new Long(value));
        }
        final ScanControllerRowSource openSortRowSource = sort.openSortRowSource(this);
        this.scanControllers.add(openSortRowSource);
        return openSortRowSource;
    }
    
    public void commit() throws StandardException {
        this.closeControllers(false);
        this.rawtran.commit();
        this.alterTableCallMade = false;
    }
    
    public DatabaseInstant commitNoSync(final int n) throws StandardException {
        this.closeControllers(false);
        return this.rawtran.commitNoSync(n);
    }
    
    public void abort() throws StandardException {
        this.invalidateConglomerateCache();
        this.closeControllers(true);
        this.rawtran.abort();
        if (this.parent_tran != null) {
            this.parent_tran.abort();
        }
    }
    
    public ContextManager getContextManager() {
        return this.context.getContextManager();
    }
    
    public int setSavePoint(final String s, final Object o) throws StandardException {
        return this.rawtran.setSavePoint(s, o);
    }
    
    public int releaseSavePoint(final String s, final Object o) throws StandardException {
        return this.rawtran.releaseSavePoint(s, o);
    }
    
    public int rollbackToSavePoint(final String s, final boolean b, final Object o) throws StandardException {
        if (b) {
            this.closeControllers(true);
        }
        return this.rawtran.rollbackToSavePoint(s, o);
    }
    
    public void destroy() {
        try {
            this.closeControllers(true);
            if (this.rawtran != null) {
                this.rawtran.destroy();
                this.rawtran = null;
            }
            if (this.context != null) {
                this.context.popMe();
            }
            this.context = null;
            this.accessmanager = null;
            this.tempCongloms = null;
        }
        catch (StandardException ex) {
            this.rawtran = null;
            this.context = null;
            this.accessmanager = null;
            this.tempCongloms = null;
        }
    }
    
    public boolean anyoneBlocked() {
        return this.rawtran.anyoneBlocked();
    }
    
    public void xa_commit(final boolean b) throws StandardException {
        this.rawtran.xa_commit(b);
    }
    
    public int xa_prepare() throws StandardException {
        return this.rawtran.xa_prepare();
    }
    
    public void xa_rollback() throws StandardException {
        this.rawtran.xa_rollback();
    }
    
    public void addPostCommitWork(final Serviceable serviceable) {
        this.rawtran.addPostCommitWork(serviceable);
    }
    
    public boolean checkVersion(final int n, final int n2, final String s) throws StandardException {
        return this.accessmanager.getRawStore().checkVersion(n, n2, s);
    }
    
    public void closeMe(final ConglomerateController o) {
        this.conglomerateControllers.remove(o);
    }
    
    public void closeMe(final SortController o) {
        this.sortControllers.remove(o);
    }
    
    public void closeMe(final ScanManager o) {
        this.scanControllers.remove(o);
    }
    
    public AccessFactory getAccessManager() {
        return this.accessmanager;
    }
    
    public TransactionManager getInternalTransaction() throws StandardException {
        final ContextManager contextManager = this.getContextManager();
        final Transaction startInternalTransaction = this.accessmanager.getRawStore().startInternalTransaction(contextManager);
        final RAMTransaction ramTransaction = new RAMTransaction(this.accessmanager, startInternalTransaction, null);
        final RAMTransactionContext ramTransactionContext = new RAMTransactionContext(contextManager, "RAMInternalContext", ramTransaction, true);
        startInternalTransaction.setDefaultLockingPolicy(this.accessmanager.getDefaultLockingPolicy());
        return ramTransaction;
    }
    
    public TransactionController startNestedUserTransaction(final boolean b, final boolean b2) throws StandardException {
        final ContextManager contextManager = this.getContextManager();
        final Transaction transaction = b ? this.accessmanager.getRawStore().startNestedReadOnlyUserTransaction(this.getLockSpace(), contextManager, "nestedReadOnlyUserTransaction") : this.accessmanager.getRawStore().startNestedUpdateUserTransaction(contextManager, "nestedUpdateUserTransaction", b2);
        final RAMTransaction ramTransaction = new RAMTransaction(this.accessmanager, transaction, this);
        final RAMTransactionContext ramTransactionContext = new RAMTransactionContext(contextManager, "RAMChildContext", ramTransaction, true);
        transaction.setDefaultLockingPolicy(this.accessmanager.getDefaultLockingPolicy());
        return ramTransaction;
    }
    
    public Transaction getRawStoreXact() {
        return this.rawtran;
    }
    
    public FileResource getFileHandler() {
        return this.rawtran.getFileHandler();
    }
    
    public CompatibilitySpace getLockSpace() {
        return this.rawtran.getCompatibilitySpace();
    }
    
    public void setNoLockWait(final boolean noLockWait) {
        this.rawtran.setNoLockWait(noLockWait);
    }
    
    public String getTransactionIdString() {
        return this.rawtran.toString();
    }
    
    public String getActiveStateTxIdString() {
        return this.rawtran.getActiveStateTxIdString();
    }
    
    public String toString() {
        return null;
    }
}
