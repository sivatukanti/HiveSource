// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.store.access.KeyHasher;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.store.access.RowSource;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.io.FormatableArrayHolder;
import org.apache.derby.iapi.services.io.FormatableIntHolder;
import org.apache.derby.catalog.types.ReferencedColumnsDescriptorImpl;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.store.access.BackingStoreHashtable;
import java.util.List;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.services.loader.GeneratedMethod;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import java.util.Properties;
import org.apache.derby.iapi.sql.execute.CursorResultSet;

class HashTableResultSet extends NoPutResultSetImpl implements CursorResultSet
{
    public long restrictionTime;
    public long projectionTime;
    public int hashtableSize;
    public Properties scanProperties;
    public NoPutResultSet source;
    public GeneratedMethod singleTableRestriction;
    public Qualifier[][] nextQualifiers;
    private GeneratedMethod projection;
    private int[] projectMapping;
    private boolean runTimeStatsOn;
    private ExecRow mappedResultRow;
    public boolean reuseResult;
    public int[] keyColumns;
    private boolean removeDuplicates;
    private long maxInMemoryRowCount;
    private int initialCapacity;
    private float loadFactor;
    private boolean skipNullKeyColumns;
    private boolean firstNext;
    private int numFetchedOnNext;
    private int entryVectorSize;
    private List entryVector;
    private boolean hashTableBuilt;
    private boolean firstIntoHashtable;
    private ExecRow nextCandidate;
    private ExecRow projRow;
    private BackingStoreHashtable ht;
    
    HashTableResultSet(final NoPutResultSet source, final Activation activation, final GeneratedMethod singleTableRestriction, final Qualifier[][] nextQualifiers, final GeneratedMethod projection, final int n, final int n2, final boolean reuseResult, final int n3, final boolean removeDuplicates, final long maxInMemoryRowCount, final int initialCapacity, final float loadFactor, final boolean skipNullKeyColumns, final double n4, final double n5) throws StandardException {
        super(activation, n, n4, n5);
        this.firstNext = true;
        this.firstIntoHashtable = true;
        this.source = source;
        this.singleTableRestriction = singleTableRestriction;
        this.nextQualifiers = nextQualifiers;
        this.projection = projection;
        this.projectMapping = ((ReferencedColumnsDescriptorImpl)activation.getPreparedStatement().getSavedObject(n2)).getReferencedColumnPositions();
        final FormatableIntHolder[] array = (FormatableIntHolder[])((FormatableArrayHolder)activation.getPreparedStatement().getSavedObject(n3)).getArray(FormatableIntHolder.class);
        this.keyColumns = new int[array.length];
        for (int i = 0; i < array.length; ++i) {
            this.keyColumns[i] = array[i].getInt();
        }
        this.reuseResult = reuseResult;
        this.removeDuplicates = removeDuplicates;
        this.maxInMemoryRowCount = maxInMemoryRowCount;
        this.initialCapacity = initialCapacity;
        this.loadFactor = loadFactor;
        this.skipNullKeyColumns = skipNullKeyColumns;
        if (this.projection == null) {
            this.mappedResultRow = this.activation.getExecutionFactory().getValueRow(this.projectMapping.length);
        }
        this.runTimeStatsOn = this.getLanguageConnectionContext().getRunTimeStatisticsMode();
        this.recordConstructorTime();
    }
    
    public void openCore() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        final TransactionController transactionController = this.activation.getTransactionController();
        if (!this.hashTableBuilt) {
            this.source.openCore();
            this.ht = new BackingStoreHashtable(transactionController, this, this.keyColumns, this.removeDuplicates, (int)this.optimizerEstimatedRowCount, this.maxInMemoryRowCount, this.initialCapacity, this.loadFactor, this.skipNullKeyColumns, false);
            if (this.runTimeStatsOn) {
                this.hashtableSize = this.ht.size();
                if (this.scanProperties == null) {
                    this.scanProperties = new Properties();
                }
                try {
                    if (this.ht != null) {
                        this.ht.getAllRuntimeStats(this.scanProperties);
                    }
                }
                catch (StandardException ex) {}
            }
            this.isOpen = true;
            this.hashTableBuilt = true;
        }
        this.resetProbeVariables();
        ++this.numOpens;
        this.openTime += this.getElapsedMillis(this.beginTime);
    }
    
    public void reopenCore() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        this.resetProbeVariables();
        ++this.numOpens;
        this.openTime += this.getElapsedMillis(this.beginTime);
    }
    
    private void resetProbeVariables() throws StandardException {
        this.firstNext = true;
        this.numFetchedOnNext = 0;
        this.entryVector = null;
        this.entryVectorSize = 0;
        if (this.nextQualifiers != null) {
            this.clearOrderableCache(this.nextQualifiers);
        }
    }
    
    public ExecRow getNextRowCore() throws StandardException {
        if (this.isXplainOnlyMode()) {
            return null;
        }
        ExecRow doProjection = null;
        DataValueDescriptor[] array = null;
        this.beginTime = this.getCurrentTimeMillis();
        if (this.isOpen) {
            do {
                if (this.firstNext) {
                    this.firstNext = false;
                    Object o;
                    if (this.keyColumns.length == 1) {
                        o = this.ht.get(this.nextQualifiers[0][0].getOrderable());
                    }
                    else {
                        final KeyHasher keyHasher = new KeyHasher(this.keyColumns.length);
                        for (int i = 0; i < this.keyColumns.length; ++i) {
                            keyHasher.setObject(i, this.nextQualifiers[0][i].getOrderable());
                        }
                        o = this.ht.get(keyHasher);
                    }
                    if (o instanceof List) {
                        this.entryVector = (List)o;
                        this.entryVectorSize = this.entryVector.size();
                        array = this.entryVector.get(0);
                    }
                    else {
                        this.entryVector = null;
                        this.entryVectorSize = 0;
                        array = (DataValueDescriptor[])o;
                    }
                }
                else if (this.numFetchedOnNext < this.entryVectorSize) {
                    array = this.entryVector.get(this.numFetchedOnNext);
                }
                if (array != null) {
                    boolean compare = true;
                    for (int j = 0; j < this.nextQualifiers[0].length; ++j) {
                        final Qualifier qualifier = this.nextQualifiers[0][j];
                        compare = array[qualifier.getColumnId()].compare(qualifier.getOperator(), qualifier.getOrderable(), qualifier.getOrderedNulls(), qualifier.getUnknownRV());
                        if (qualifier.negateCompareResult()) {
                            compare = !compare;
                        }
                        if (!compare) {
                            break;
                        }
                    }
                    if (compare) {
                        for (int k = 0; k < array.length; ++k) {
                            this.nextCandidate.setColumn(k + 1, array[k]);
                        }
                        doProjection = this.doProjection(this.nextCandidate);
                    }
                    else {
                        doProjection = null;
                    }
                    ++this.numFetchedOnNext;
                }
                else {
                    doProjection = null;
                }
            } while (doProjection == null && this.numFetchedOnNext < this.entryVectorSize);
        }
        this.setCurrentRow(doProjection);
        this.nextTime += this.getElapsedMillis(this.beginTime);
        if (this.runTimeStatsOn) {
            if (!this.isTopResultSet) {
                this.subqueryTrackingArray = this.activation.getLanguageConnectionContext().getStatementContext().getSubqueryTrackingArray();
            }
            this.nextTime += this.getElapsedMillis(this.beginTime);
        }
        return doProjection;
    }
    
    public long getTimeSpent(final int n) {
        final long n2 = this.constructorTime + this.openTime + this.nextTime + this.closeTime;
        if (n == 0) {
            return n2 - this.source.getTimeSpent(1);
        }
        return n2;
    }
    
    public void close() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        if (this.isOpen) {
            this.clearCurrentRow();
            this.source.close();
            super.close();
            if (this.hashTableBuilt) {
                this.ht.close();
                this.ht = null;
                this.hashTableBuilt = false;
            }
        }
        this.closeTime += this.getElapsedMillis(this.beginTime);
    }
    
    public RowLocation getRowLocation() throws StandardException {
        return ((CursorResultSet)this.source).getRowLocation();
    }
    
    public ExecRow getCurrentRow() throws StandardException {
        ExecRow doProjection = null;
        boolean b = false;
        if (this.currentRow == null) {
            return null;
        }
        final ExecRow currentRow = ((CursorResultSet)this.source).getCurrentRow();
        if (currentRow != null) {
            this.setCurrentRow(currentRow);
            final DataValueDescriptor dataValueDescriptor = (DataValueDescriptor)((this.singleTableRestriction == null) ? null : this.singleTableRestriction.invoke(this.activation));
            b = (dataValueDescriptor == null || (!dataValueDescriptor.isNull() && dataValueDescriptor.getBoolean()));
        }
        if (currentRow != null && b) {
            doProjection = this.doProjection(currentRow);
        }
        if ((this.currentRow = doProjection) == null) {
            this.clearCurrentRow();
        }
        return this.currentRow;
    }
    
    private ExecRow doProjection(final ExecRow execRow) throws StandardException {
        if (this.reuseResult && this.projRow != null) {
            return this.projRow;
        }
        ExecRow mappedResultRow;
        if (this.projection != null) {
            mappedResultRow = (ExecRow)this.projection.invoke(this.activation);
        }
        else {
            mappedResultRow = this.mappedResultRow;
        }
        for (int i = 0; i < this.projectMapping.length; ++i) {
            if (this.projectMapping[i] != -1) {
                mappedResultRow.setColumn(i + 1, execRow.getColumn(this.projectMapping[i]));
            }
        }
        this.setCurrentRow(mappedResultRow);
        if (this.reuseResult) {
            this.projRow = mappedResultRow;
        }
        return mappedResultRow;
    }
    
    public DataValueDescriptor[] getNextRowFromRowSource() throws StandardException {
        for (ExecRow execRow = this.source.getNextRowCore(); execRow != null; execRow = this.source.getNextRowCore()) {
            ++this.rowsSeen;
            final DataValueDescriptor dataValueDescriptor = (DataValueDescriptor)((this.singleTableRestriction == null) ? null : this.singleTableRestriction.invoke(this.activation));
            if (dataValueDescriptor == null || (!dataValueDescriptor.isNull() && dataValueDescriptor.getBoolean())) {
                if (this.targetResultSet != null) {
                    this.clonedExecRow = this.targetResultSet.preprocessSourceRow(execRow);
                }
                if (this.firstIntoHashtable) {
                    this.nextCandidate = this.activation.getExecutionFactory().getValueRow(execRow.nColumns());
                    this.firstIntoHashtable = false;
                }
                return execRow.getRowArray();
            }
        }
        return null;
    }
    
    public boolean isForUpdate() {
        return this.source != null && this.source.isForUpdate();
    }
}
