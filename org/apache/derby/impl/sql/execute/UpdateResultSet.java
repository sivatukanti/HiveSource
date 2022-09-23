// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.execute.CursorResultSet;
import java.util.Vector;
import org.apache.derby.iapi.store.access.RowSource;
import org.apache.derby.iapi.store.access.BackingStoreHashtable;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.services.io.StreamStorable;
import org.apache.derby.iapi.sql.execute.TemporaryRowHolder;
import java.util.Properties;
import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.services.loader.GeneratedMethod;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.store.access.ScanController;
import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.iapi.sql.execute.RowChanger;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import org.apache.derby.iapi.sql.ResultDescription;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.store.access.TransactionController;

class UpdateResultSet extends DMLWriteResultSet
{
    private TransactionController tc;
    private ExecRow newBaseRow;
    private ExecRow row;
    private ExecRow deferredSparseRow;
    UpdateConstantAction constants;
    private ResultDescription resultDescription;
    private NoPutResultSet source;
    NoPutResultSet savedSource;
    private RowChanger rowChanger;
    protected ConglomerateController deferredBaseCC;
    protected long[] deferredUniqueCIDs;
    protected boolean[] deferredUniqueCreated;
    protected ConglomerateController[] deferredUniqueCC;
    protected ScanController[] deferredUniqueScans;
    private TemporaryRowHolderImpl deletedRowHolder;
    private TemporaryRowHolderImpl insertedRowHolder;
    private RISetChecker riChecker;
    private TriggerInfo triggerInfo;
    private TriggerEventActivator triggerActivator;
    private boolean updatingReferencedKey;
    private boolean updatingForeignKey;
    private int numOpens;
    private long heapConglom;
    private FKInfo[] fkInfoArray;
    private FormatableBitSet baseRowReadList;
    private GeneratedMethod generationClauses;
    private GeneratedMethod checkGM;
    private int resultWidth;
    private int numberOfBaseColumns;
    private ExecRow deferredTempRow;
    private ExecRow deferredBaseRow;
    private ExecRow oldDeletedRow;
    private ResultDescription triggerResultDescription;
    int lockMode;
    boolean deferred;
    boolean beforeUpdateCopyRequired;
    
    public ResultDescription getResultDescription() {
        return this.resultDescription;
    }
    
    UpdateResultSet(final NoPutResultSet set, final GeneratedMethod generatedMethod, final GeneratedMethod generatedMethod2, final Activation activation) throws StandardException {
        this(set, generatedMethod, generatedMethod2, activation, activation.getConstantAction(), null);
    }
    
    UpdateResultSet(final NoPutResultSet set, final GeneratedMethod generatedMethod, final GeneratedMethod generatedMethod2, final Activation activation, final int n, final int n2) throws StandardException {
        this(set, generatedMethod, generatedMethod2, activation, (ConstantAction)activation.getPreparedStatement().getSavedObject(n), (ResultDescription)activation.getPreparedStatement().getSavedObject(n2));
        this.deferred = true;
    }
    
    UpdateResultSet(final NoPutResultSet source, final GeneratedMethod generationClauses, final GeneratedMethod checkGM, final Activation activation, final ConstantAction constantAction, final ResultDescription resultDescription) throws StandardException {
        super(activation, constantAction);
        this.beforeUpdateCopyRequired = false;
        this.tc = activation.getTransactionController();
        this.source = source;
        this.generationClauses = generationClauses;
        this.checkGM = checkGM;
        this.constants = (UpdateConstantAction)this.constantAction;
        this.fkInfoArray = this.constants.getFKInfo();
        this.triggerInfo = this.constants.getTriggerInfo();
        this.heapConglom = this.constants.conglomId;
        this.baseRowReadList = this.constants.getBaseRowReadList();
        if (resultDescription == null) {
            this.resultDescription = source.getResultDescription();
        }
        else {
            this.resultDescription = resultDescription;
        }
        if (this.fkInfoArray != null) {
            for (int i = 0; i < this.fkInfoArray.length; ++i) {
                if (this.fkInfoArray[i].type == 2) {
                    this.updatingReferencedKey = true;
                }
                else {
                    this.updatingForeignKey = true;
                }
            }
        }
        this.resultWidth = this.resultDescription.getColumnCount();
        this.numberOfBaseColumns = (this.resultWidth - 1) / 2;
        this.newBaseRow = RowUtil.getEmptyValueRow(this.numberOfBaseColumns, this.lcc);
        this.deferred = this.constants.deferred;
        if (this.triggerInfo != null || this.fkInfoArray != null) {
            this.beforeUpdateCopyRequired = true;
        }
    }
    
    public void open() throws StandardException {
        this.setup();
        this.collectAffectedRows();
        if (this.deferred) {
            this.runChecker(true);
            this.fireBeforeTriggers();
            this.updateDeferredRows();
            this.rowChanger.finish();
            this.runChecker(false);
            this.fireAfterTriggers();
        }
        else {
            this.rowChanger.finish();
        }
        this.cleanUp();
    }
    
    void setup() throws StandardException {
        super.setup();
        this.lockMode = this.decodeLockMode(this.constants.lockMode);
        final boolean b = this.rowChanger == null;
        this.rowCount = 0L;
        if (this.lcc.getRunTimeStatisticsMode()) {
            this.savedSource = this.source;
        }
        if (b) {
            (this.rowChanger = this.lcc.getLanguageConnectionFactory().getExecutionFactory().getRowChanger(this.heapConglom, this.constants.heapSCOCI, this.heapDCOCI, this.constants.irgs, this.constants.indexCIDS, this.constants.indexSCOCIs, this.indexDCOCIs, this.constants.numColumns, this.tc, this.constants.changedColumnIds, this.constants.getBaseRowReadList(), this.constants.getBaseRowReadMap(), this.constants.getStreamStorableHeapColIds(), this.activation)).setIndexNames(this.constants.indexNames);
        }
        this.rowChanger.open(this.lockMode);
        if (this.numOpens++ == 0) {
            this.source.openCore();
        }
        else {
            this.source.reopenCore();
        }
        if (this.deferred) {
            this.activation.clearIndexScanInfo();
        }
        if (this.fkInfoArray != null) {
            if (this.riChecker == null) {
                this.riChecker = new RISetChecker(this.tc, this.fkInfoArray);
            }
            else {
                this.riChecker.reopen();
            }
        }
        if (this.deferred) {
            if (b) {
                this.deferredTempRow = RowUtil.getEmptyValueRow(this.numberOfBaseColumns + 1, this.lcc);
                this.oldDeletedRow = RowUtil.getEmptyValueRow(this.numberOfBaseColumns, this.lcc);
                this.triggerResultDescription = ((this.resultDescription != null) ? this.resultDescription.truncateColumns(this.numberOfBaseColumns + 1) : null);
            }
            final Properties properties = new Properties();
            this.rowChanger.getHeapConglomerateController().getInternalTablePropertySet(properties);
            if (this.beforeUpdateCopyRequired) {
                this.deletedRowHolder = new TemporaryRowHolderImpl(this.activation, properties, this.triggerResultDescription);
            }
            this.insertedRowHolder = new TemporaryRowHolderImpl(this.activation, properties, this.triggerResultDescription);
            this.rowChanger.setRowHolder(this.insertedRowHolder);
        }
    }
    
    private FormatableBitSet checkStreamCols() {
        final DataValueDescriptor[] rowArray = this.row.getRowArray();
        FormatableBitSet set = null;
        for (int i = 0; i < this.numberOfBaseColumns; ++i) {
            if (rowArray[i + this.numberOfBaseColumns] instanceof StreamStorable) {
                if (set == null) {
                    set = new FormatableBitSet(this.numberOfBaseColumns);
                }
                set.set(i);
            }
        }
        return set;
    }
    
    private void objectifyStream(final ExecRow execRow, final FormatableBitSet set) throws StandardException {
        final DataValueDescriptor[] rowArray = execRow.getRowArray();
        for (int i = 0; i < this.numberOfBaseColumns; ++i) {
            if (rowArray[i] != null && set.get(i)) {
                ((StreamStorable)rowArray[i]).loadStream();
            }
        }
    }
    
    public boolean collectAffectedRows() throws StandardException {
        boolean b = false;
        this.row = this.getNextRowCore(this.source);
        if (this.row != null) {
            b = true;
        }
        else {
            this.activation.addWarning(StandardException.newWarning("02000"));
        }
        final TableScanResultSet set = (TableScanResultSet)this.activation.getForUpdateIndexScan();
        final boolean b2 = set != null;
        final FormatableBitSet set2 = (this.deferred && b && !this.constants.singleRowSource) ? this.checkStreamCols() : null;
        final boolean b3 = set2 != null;
        while (this.row != null) {
            this.evaluateGenerationClauses(this.generationClauses, this.activation, this.source, this.row, true);
            if (this.deferred) {
                if (this.triggerInfo == null) {
                    NoRowsResultSetImpl.evaluateCheckConstraints(this.checkGM, this.activation);
                }
                RowUtil.copyRefColumns(this.deferredTempRow, this.row, this.numberOfBaseColumns, this.numberOfBaseColumns + 1);
                if (b3) {
                    this.objectifyStream(this.deferredTempRow, set2);
                }
                this.insertedRowHolder.insert(this.deferredTempRow);
                if (this.beforeUpdateCopyRequired) {
                    RowUtil.copyRefColumns(this.oldDeletedRow, this.row, this.numberOfBaseColumns);
                    this.deletedRowHolder.insert(this.oldDeletedRow);
                }
                if (this.deferredBaseRow == null) {
                    RowUtil.copyCloneColumns(this.deferredBaseRow = RowUtil.getEmptyValueRow(this.numberOfBaseColumns, this.lcc), this.row, this.numberOfBaseColumns);
                    this.deferredSparseRow = this.makeDeferredSparseRow(this.deferredBaseRow, this.baseRowReadList, this.lcc);
                }
            }
            else {
                NoRowsResultSetImpl.evaluateCheckConstraints(this.checkGM, this.activation);
                final RowLocation rowLocation = (RowLocation)this.row.getColumn(this.resultWidth).getObject();
                RowUtil.copyRefColumns(this.newBaseRow, this.row, this.numberOfBaseColumns, this.numberOfBaseColumns);
                if (this.riChecker != null) {
                    this.riChecker.doFKCheck(this.newBaseRow);
                }
                this.source.updateRow(this.newBaseRow, this.rowChanger);
                this.rowChanger.updateRow(this.row, this.newBaseRow, rowLocation);
                if (b2) {
                    this.notifyForUpdateCursor(this.row.getRowArray(), this.newBaseRow.getRowArray(), rowLocation, set);
                }
            }
            ++this.rowCount;
            if (this.constants.singleRowSource) {
                this.row = null;
            }
            else {
                this.row = this.getNextRowCore(this.source);
            }
        }
        return b;
    }
    
    private void notifyForUpdateCursor(final DataValueDescriptor[] array, final DataValueDescriptor[] array2, final RowLocation rowLocation, final TableScanResultSet set) throws StandardException {
        final int[] indexCols = set.indexCols;
        final int[] changedColumnIds = this.constants.changedColumnIds;
        boolean b = false;
        boolean b2 = false;
        boolean b3 = false;
        for (int i = 0; i < indexCols.length; ++i) {
            int n = indexCols[i];
            boolean b4;
            if (n > 0) {
                b4 = true;
            }
            else {
                b4 = false;
                n = -n;
            }
            int j = 0;
            while (j < changedColumnIds.length) {
                if (n == changedColumnIds[j]) {
                    b2 = true;
                    final int[] baseRowReadMap = this.constants.getBaseRowReadMap();
                    int n2;
                    if (baseRowReadMap == null) {
                        n2 = n - 1;
                    }
                    else {
                        n2 = baseRowReadMap[n - 1];
                    }
                    final DataValueDescriptor dataValueDescriptor = array[n2];
                    if ((b4 && dataValueDescriptor.greaterThan(array2[n2], dataValueDescriptor).equals(true)) || (!b4 && dataValueDescriptor.lessThan(array2[n2], dataValueDescriptor).equals(true))) {
                        b = true;
                        break;
                    }
                    if (dataValueDescriptor.equals(array2[n2], dataValueDescriptor).equals(true)) {
                        b2 = false;
                        b3 = true;
                        break;
                    }
                    break;
                }
                else {
                    ++j;
                }
            }
            if (b2) {
                break;
            }
        }
        if (b3 && !b2) {
            b = true;
        }
        if (b) {
            int n3 = this.lcc.getOptimizerFactory().getMaxMemoryPerTable() / 16;
            if (n3 < 100) {
                n3 = 100;
            }
            if (set.past2FutureTbl == null) {
                final double estimatedRowCount = set.getEstimatedRowCount();
                int n4 = 32768;
                if (estimatedRowCount > 0.0) {
                    final double n5 = estimatedRowCount / 0.75 + 1.0;
                    if (n5 < n4) {
                        n4 = (int)n5;
                    }
                }
                if (n3 < n4) {
                    n4 = n3;
                }
                set.past2FutureTbl = new BackingStoreHashtable(this.tc, null, new int[] { 0 }, false, -1L, n3, n4, -1.0f, false, set.getActivation().getResultSetHoldability());
            }
            set.past2FutureTbl.putRow(false, new DataValueDescriptor[] { rowLocation.cloneValue(false) });
        }
    }
    
    void fireBeforeTriggers() throws StandardException {
        if (this.deferred && this.triggerInfo != null) {
            if (this.triggerActivator == null) {
                this.triggerActivator = new TriggerEventActivator(this.lcc, this.tc, this.constants.targetUUID, this.triggerInfo, 1, this.activation, null);
            }
            else {
                this.triggerActivator.reopen();
            }
            this.triggerActivator.notifyEvent(TriggerEvents.BEFORE_UPDATE, this.deletedRowHolder.getResultSet(), this.insertedRowHolder.getResultSet(), this.constants.getBaseRowReadMap());
        }
    }
    
    void fireAfterTriggers() throws StandardException {
        if (this.deferred && this.triggerActivator != null) {
            this.triggerActivator.notifyEvent(TriggerEvents.AFTER_UPDATE, this.deletedRowHolder.getResultSet(), this.insertedRowHolder.getResultSet(), this.constants.getBaseRowReadMap());
        }
    }
    
    void updateDeferredRows() throws StandardException {
        if (this.deferred) {
            final TransactionController tc = this.tc;
            final boolean b = false;
            final TransactionController tc2 = this.tc;
            final int n = 4;
            final TransactionController tc3 = this.tc;
            this.deferredBaseCC = tc.openCompiledConglomerate(b, n | 0x2000, this.lockMode, 5, this.constants.heapSCOCI, this.heapDCOCI);
            final CursorResultSet resultSet = this.insertedRowHolder.getResultSet();
            try {
                final FormatableBitSet shift = RowUtil.shift(this.baseRowReadList, 1);
                resultSet.open();
                ExecRow nextRow;
                while ((nextRow = resultSet.getNextRow()) != null) {
                    if (this.triggerInfo != null) {
                        this.source.setCurrentRow(this.deferredTempRow);
                        NoRowsResultSetImpl.evaluateCheckConstraints(this.checkGM, this.activation);
                    }
                    final RowLocation rowLocation = (RowLocation)nextRow.getColumn(this.numberOfBaseColumns + 1).getObject();
                    this.deferredBaseCC.fetch(rowLocation, this.deferredSparseRow.getRowArray(), shift);
                    RowUtil.copyRefColumns(this.newBaseRow, nextRow, this.numberOfBaseColumns);
                    this.rowChanger.updateRow(this.deferredBaseRow, this.newBaseRow, rowLocation);
                }
            }
            finally {
                this.source.clearCurrentRow();
                resultSet.close();
            }
        }
    }
    
    void runChecker(final boolean b) throws StandardException {
        if (this.deferred && this.updatingReferencedKey) {
            for (int i = 0; i < this.fkInfoArray.length; ++i) {
                if (this.fkInfoArray[i].type != 1) {
                    final CursorResultSet resultSet = this.deletedRowHolder.getResultSet();
                    try {
                        resultSet.open();
                        ExecRow nextRow;
                        while ((nextRow = resultSet.getNextRow()) != null) {
                            if (!foundRow(nextRow, this.fkInfoArray[i].colArray, this.insertedRowHolder)) {
                                this.riChecker.doRICheck(i, nextRow, b);
                            }
                        }
                    }
                    finally {
                        resultSet.close();
                    }
                }
            }
        }
        if (this.deferred && this.updatingForeignKey) {
            for (int j = 0; j < this.fkInfoArray.length; ++j) {
                if (this.fkInfoArray[j].type != 2) {
                    final CursorResultSet resultSet2 = this.insertedRowHolder.getResultSet();
                    try {
                        resultSet2.open();
                        ExecRow nextRow2;
                        while ((nextRow2 = resultSet2.getNextRow()) != null) {
                            if (!foundRow(nextRow2, this.fkInfoArray[j].colArray, this.deletedRowHolder)) {
                                this.riChecker.doRICheck(j, nextRow2, b);
                            }
                        }
                    }
                    finally {
                        resultSet2.close();
                    }
                }
            }
        }
    }
    
    public static boolean foundRow(final ExecRow execRow, final int[] array, final TemporaryRowHolderImpl temporaryRowHolderImpl) throws StandardException {
        boolean b = false;
        final DataValueDescriptor[] rowArray = execRow.getRowArray();
        final CursorResultSet resultSet = temporaryRowHolderImpl.getResultSet();
        try {
            resultSet.open();
            ExecRow nextRow;
            while ((nextRow = resultSet.getNextRow()) != null) {
                final DataValueDescriptor[] rowArray2 = nextRow.getRowArray();
                int i;
                for (i = 0; i < array.length; ++i) {
                    final DataValueDescriptor dataValueDescriptor = rowArray[array[i] - 1];
                    if (!dataValueDescriptor.equals(rowArray2[array[i] - 1], dataValueDescriptor).getBoolean()) {
                        break;
                    }
                }
                if (i == array.length) {
                    b = true;
                    break;
                }
            }
        }
        finally {
            resultSet.close();
        }
        return b;
    }
    
    public void cleanUp() throws StandardException {
        this.numOpens = 0;
        if (this.source != null) {
            this.source.close();
        }
        if (this.triggerActivator != null) {
            this.triggerActivator.cleanup();
        }
        if (this.rowChanger != null) {
            this.rowChanger.close();
        }
        if (this.deferredBaseCC != null) {
            this.deferredBaseCC.close();
        }
        this.deferredBaseCC = null;
        if (this.insertedRowHolder != null) {
            this.insertedRowHolder.close();
        }
        if (this.deletedRowHolder != null) {
            this.deletedRowHolder.close();
        }
        if (this.riChecker != null) {
            this.riChecker.close();
        }
        super.close();
        this.endTime = this.getCurrentTimeMillis();
    }
    
    void rowChangerFinish() throws StandardException {
        this.rowChanger.finish();
    }
}
