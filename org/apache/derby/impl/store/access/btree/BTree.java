// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.btree;

import java.io.DataOutput;
import java.io.ObjectOutput;
import java.io.IOException;
import java.io.DataInput;
import org.apache.derby.iapi.services.io.FormatIdUtil;
import java.io.ObjectInput;
import org.apache.derby.iapi.store.access.StaticCompiledOpenConglomInfo;
import org.apache.derby.iapi.store.raw.LockingPolicy;
import org.apache.derby.impl.store.access.conglomerate.OpenConglomerateScratchSpace;
import org.apache.derby.iapi.store.access.DynamicCompiledOpenConglomInfo;
import org.apache.derby.iapi.store.access.RowLocationRetRowSource;
import org.apache.derby.impl.store.access.conglomerate.ConglomerateUtil;
import java.util.Properties;
import org.apache.derby.iapi.services.io.Storable;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.impl.store.access.conglomerate.TemplateRow;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.store.access.conglomerate.TransactionManager;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.store.raw.ContainerKey;
import org.apache.derby.impl.store.access.conglomerate.GenericConglomerate;

public abstract class BTree extends GenericConglomerate
{
    public static final long ROOTPAGEID = 1L;
    public static final String PROPERTY_MAX_ROWS_PER_PAGE_PARAMETER;
    public static final String PROPERTY_ALLOWDUPLICATES = "allowDuplicates";
    public static final String PROPERTY_NKEYFIELDS = "nKeyFields";
    public static final String PROPERTY_NUNIQUECOLUMNS = "nUniqueColumns";
    public static final String PROPERTY_PARENTLINKS = "maintainParentLinks";
    public static final String PROPERTY_UNIQUE_WITH_DUPLICATE_NULLS = "uniqueWithDuplicateNulls";
    protected ContainerKey id;
    protected int nKeyFields;
    int nUniqueColumns;
    boolean allowDuplicates;
    boolean maintainParentLinks;
    boolean uniqueWithDuplicateNulls;
    static int maxRowsPerPage;
    protected int conglom_format_id;
    protected int[] format_ids;
    protected boolean[] ascDescInfo;
    protected int[] collation_ids;
    protected boolean hasCollatedTypes;
    
    public BTree() {
        this.uniqueWithDuplicateNulls = false;
    }
    
    protected abstract BTreeLockingPolicy getBtreeLockingPolicy(final Transaction p0, final int p1, final int p2, final int p3, final ConglomerateController p4, final OpenBTree p5) throws StandardException;
    
    public abstract ConglomerateController lockTable(final TransactionManager p0, final int p1, final int p2, final int p3) throws StandardException;
    
    final DataValueDescriptor[] createBranchTemplate(final Transaction transaction, final DataValueDescriptor dataValueDescriptor) throws StandardException {
        return TemplateRow.newBranchRow(transaction, this.format_ids, this.collation_ids, dataValueDescriptor);
    }
    
    public final DataValueDescriptor[] createTemplate(final Transaction transaction) throws StandardException {
        return TemplateRow.newRow(transaction, null, this.format_ids, this.collation_ids);
    }
    
    public final boolean isUnique() {
        return this.nKeyFields != this.nUniqueColumns;
    }
    
    public void setUniqueWithDuplicateNulls(final boolean uniqueWithDuplicateNulls) {
        this.uniqueWithDuplicateNulls = uniqueWithDuplicateNulls;
    }
    
    public boolean isUniqueWithDuplicateNulls() {
        return this.uniqueWithDuplicateNulls;
    }
    
    public void addColumn(final TransactionManager transactionManager, final int n, final Storable storable, final int n2) throws StandardException {
        throw StandardException.newException("XSCB3.S");
    }
    
    public final ContainerKey getId() {
        return this.id;
    }
    
    public void create(final Transaction transaction, final int n, final long n2, final DataValueDescriptor[] array, final Properties properties, final int conglom_format_id, final int n3) throws StandardException {
        if (properties == null) {
            throw StandardException.newException("XSCB2.S", "nKeyFields");
        }
        this.allowDuplicates = Boolean.valueOf(properties.getProperty("allowDuplicates", "false"));
        final String property = properties.getProperty("nKeyFields");
        if (property == null) {
            throw StandardException.newException("XSCB2.S", "nKeyFields");
        }
        this.nKeyFields = Integer.parseInt(property);
        final String property2 = properties.getProperty("nUniqueColumns");
        if (property2 == null) {
            throw StandardException.newException("XSCB2.S", "nUniqueColumns");
        }
        this.nUniqueColumns = Integer.parseInt(property2);
        this.uniqueWithDuplicateNulls = new Boolean(properties.getProperty("uniqueWithDuplicateNulls", "false"));
        this.maintainParentLinks = Boolean.valueOf(properties.getProperty("maintainParentLinks", "true"));
        this.format_ids = ConglomerateUtil.createFormatIds(array);
        this.conglom_format_id = conglom_format_id;
        properties.put("derby.storage.pageReservedSpace", "0");
        properties.put("derby.storage.minimumRecordSize", "1");
        properties.put("derby.storage.reusableRecordId", "true");
        final long addContainer = transaction.addContainer(n, n2, 0, properties, n3);
        if (addContainer <= 0L) {
            throw StandardException.newException("XSCB0.S");
        }
        this.id = new ContainerKey(n, addContainer);
    }
    
    public abstract void drop(final TransactionManager p0) throws StandardException;
    
    public abstract long load(final TransactionManager p0, final boolean p1, final RowLocationRetRowSource p2) throws StandardException;
    
    public long getContainerid() {
        return this.id.getContainerId();
    }
    
    public DynamicCompiledOpenConglomInfo getDynamicCompiledConglomInfo() throws StandardException {
        return new OpenConglomerateScratchSpace(this.format_ids, this.collation_ids, this.hasCollatedTypes);
    }
    
    public boolean isTemporary() {
        return this.id.getSegmentId() == -1L;
    }
    
    public abstract ConglomerateController open(final TransactionManager p0, final Transaction p1, final boolean p2, final int p3, final int p4, final LockingPolicy p5, final StaticCompiledOpenConglomInfo p6, final DynamicCompiledOpenConglomInfo p7) throws StandardException;
    
    public boolean isNull() {
        return this.id == null;
    }
    
    public void restoreToNull() {
        this.id = null;
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.conglom_format_id = FormatIdUtil.readFormatIdInteger(objectInput);
        final long long1 = objectInput.readLong();
        final int int1 = objectInput.readInt();
        this.nKeyFields = objectInput.readInt();
        this.nUniqueColumns = objectInput.readInt();
        this.allowDuplicates = objectInput.readBoolean();
        this.maintainParentLinks = objectInput.readBoolean();
        this.format_ids = ConglomerateUtil.readFormatIdArray(this.nKeyFields, objectInput);
        this.id = new ContainerKey(int1, long1);
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        FormatIdUtil.writeFormatIdInteger(objectOutput, this.conglom_format_id);
        objectOutput.writeLong(this.id.getContainerId());
        objectOutput.writeInt((int)this.id.getSegmentId());
        objectOutput.writeInt(this.nKeyFields);
        objectOutput.writeInt(this.nUniqueColumns);
        objectOutput.writeBoolean(this.allowDuplicates);
        objectOutput.writeBoolean(this.maintainParentLinks);
        ConglomerateUtil.writeFormatIdArray(this.format_ids, objectOutput);
    }
    
    public String toString() {
        return super.toString();
    }
    
    static {
        PROPERTY_MAX_ROWS_PER_PAGE_PARAMETER = null;
        BTree.maxRowsPerPage = Integer.MAX_VALUE;
    }
}
