// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.execute.CursorResultSet;
import java.util.Vector;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.sql.execute.TemporaryRowHolder;
import java.util.Properties;
import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.execute.RowChanger;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import org.apache.derby.iapi.sql.ResultDescription;
import org.apache.derby.iapi.store.access.TransactionController;

class DeleteResultSet extends DMLWriteResultSet
{
    private TransactionController tc;
    DeleteConstantAction constants;
    protected ResultDescription resultDescription;
    protected NoPutResultSet source;
    NoPutResultSet savedSource;
    int numIndexes;
    protected RowChanger rc;
    private ExecRow row;
    protected ConglomerateController deferredBaseCC;
    protected TemporaryRowHolderImpl rowHolder;
    private int numOpens;
    private boolean firstExecute;
    private FormatableBitSet baseRowReadList;
    private int rlColumnNumber;
    protected FKInfo[] fkInfoArray;
    private TriggerInfo triggerInfo;
    private RISetChecker fkChecker;
    private TriggerEventActivator triggerActivator;
    private boolean noTriggersOrFks;
    ExecRow deferredSparseRow;
    ExecRow deferredBaseRow;
    int lockMode;
    protected boolean cascadeDelete;
    ExecRow deferredRLRow;
    int numberOfBaseColumns;
    
    public ResultDescription getResultDescription() {
        return this.resultDescription;
    }
    
    DeleteResultSet(final NoPutResultSet set, final Activation activation) throws StandardException {
        this(set, activation.getConstantAction(), activation);
    }
    
    DeleteResultSet(final NoPutResultSet source, final ConstantAction constantAction, final Activation activation) throws StandardException {
        super(activation, constantAction);
        this.deferredRLRow = null;
        this.numberOfBaseColumns = 0;
        this.source = source;
        this.tc = activation.getTransactionController();
        this.constants = (DeleteConstantAction)this.constantAction;
        this.fkInfoArray = this.constants.getFKInfo();
        this.triggerInfo = this.constants.getTriggerInfo();
        this.noTriggersOrFks = (this.fkInfoArray == null && this.triggerInfo == null);
        this.baseRowReadList = this.constants.getBaseRowReadList();
        if (source != null) {
            this.resultDescription = source.getResultDescription();
        }
        else {
            this.resultDescription = this.constants.resultDescription;
        }
    }
    
    public void open() throws StandardException {
        this.setup();
        if (!this.collectAffectedRows()) {
            this.activation.addWarning(StandardException.newWarning("02000"));
        }
        if (this.constants.deferred) {
            this.runFkChecker(true);
            this.fireBeforeTriggers();
            this.deleteDeferredRows();
            this.runFkChecker(false);
            this.rc.finish();
            this.fireAfterTriggers();
        }
        if (this.lcc.getRunTimeStatisticsMode()) {
            this.savedSource = this.source;
        }
        this.cleanUp();
        this.endTime = this.getCurrentTimeMillis();
    }
    
    void setup() throws StandardException {
        super.setup();
        this.firstExecute = (this.rc == null);
        try {
            if (this.numOpens++ == 0) {
                this.source.openCore();
            }
            else {
                this.source.reopenCore();
            }
        }
        catch (StandardException ex) {
            this.activation.checkStatementValidity();
            throw ex;
        }
        this.activation.checkStatementValidity();
        if (this.firstExecute) {
            this.rc = this.lcc.getLanguageConnectionFactory().getExecutionFactory().getRowChanger(this.constants.conglomId, this.constants.heapSCOCI, this.heapDCOCI, this.constants.irgs, this.constants.indexCIDS, this.constants.indexSCOCIs, this.indexDCOCIs, this.constants.numColumns, this.tc, null, this.baseRowReadList, this.constants.getBaseRowReadMap(), this.constants.getStreamStorableHeapColIds(), this.activation);
        }
        this.lockMode = this.decodeLockMode(this.constants.lockMode);
        this.rc.open(this.lockMode);
        if (this.constants.deferred || this.cascadeDelete) {
            this.activation.clearIndexScanInfo();
        }
        this.rowCount = 0L;
        if (!this.cascadeDelete) {
            this.row = this.getNextRowCore(this.source);
        }
        if (this.resultDescription == null) {
            this.numberOfBaseColumns = ((this.row == null) ? 0 : this.row.nColumns());
        }
        else {
            this.numberOfBaseColumns = this.resultDescription.getColumnCount();
        }
        this.numIndexes = this.constants.irgs.length;
        if (this.constants.deferred || this.cascadeDelete) {
            final Properties properties = new Properties();
            this.rc.getHeapConglomerateController().getInternalTablePropertySet(properties);
            this.deferredRLRow = RowUtil.getEmptyValueRow(1, this.lcc);
            this.rlColumnNumber = (this.noTriggersOrFks ? 1 : this.numberOfBaseColumns);
            if (this.cascadeDelete) {
                this.rowHolder = new TemporaryRowHolderImpl(this.activation, properties, (this.resultDescription != null) ? this.resultDescription.truncateColumns(this.rlColumnNumber) : null, false);
            }
            else {
                this.rowHolder = new TemporaryRowHolderImpl(this.activation, properties, (this.resultDescription != null) ? this.resultDescription.truncateColumns(this.rlColumnNumber) : null);
            }
            this.rc.setRowHolder(this.rowHolder);
        }
        if (this.fkInfoArray != null) {
            if (this.fkChecker == null) {
                this.fkChecker = new RISetChecker(this.tc, this.fkInfoArray);
            }
            else {
                this.fkChecker.reopen();
            }
        }
    }
    
    boolean collectAffectedRows() throws StandardException {
        boolean b = false;
        if (this.cascadeDelete) {
            this.row = this.getNextRowCore(this.source);
        }
        while (this.row != null) {
            b = true;
            final DataValueDescriptor column = this.row.getColumn(this.row.nColumns());
            if (this.constants.deferred || this.cascadeDelete) {
                if (this.noTriggersOrFks) {
                    this.deferredRLRow.setColumn(1, column);
                    this.rowHolder.insert(this.deferredRLRow);
                }
                else {
                    this.rowHolder.insert(this.row);
                }
                if (this.deferredBaseRow == null) {
                    RowUtil.copyCloneColumns(this.deferredBaseRow = RowUtil.getEmptyValueRow(this.numberOfBaseColumns - 1, this.lcc), this.row, this.numberOfBaseColumns - 1);
                    this.deferredSparseRow = this.makeDeferredSparseRow(this.deferredBaseRow, this.baseRowReadList, this.lcc);
                }
            }
            else {
                if (this.fkChecker != null) {
                    this.fkChecker.doPKCheck(this.row, false);
                }
                this.rc.deleteRow(this.row, (RowLocation)column.getObject());
                this.source.markRowAsDeleted();
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
    
    void fireBeforeTriggers() throws StandardException {
        if (this.triggerInfo != null) {
            if (this.triggerActivator == null) {
                this.triggerActivator = new TriggerEventActivator(this.lcc, this.tc, this.constants.targetUUID, this.triggerInfo, 2, this.activation, null);
            }
            else {
                this.triggerActivator.reopen();
            }
            this.triggerActivator.notifyEvent(TriggerEvents.BEFORE_DELETE, this.rowHolder.getResultSet(), null, this.constants.getBaseRowReadMap());
            this.triggerActivator.cleanup();
        }
    }
    
    void fireAfterTriggers() throws StandardException {
        if (this.triggerActivator != null) {
            this.triggerActivator.reopen();
            this.triggerActivator.notifyEvent(TriggerEvents.AFTER_DELETE, this.rowHolder.getResultSet(), null, this.constants.getBaseRowReadMap());
            this.triggerActivator.cleanup();
        }
    }
    
    void deleteDeferredRows() throws StandardException {
        final TransactionController tc = this.tc;
        final boolean b = false;
        final TransactionController tc2 = this.tc;
        final int n = 4;
        final TransactionController tc3 = this.tc;
        this.deferredBaseCC = tc.openCompiledConglomerate(b, n | 0x2000, this.lockMode, 5, this.constants.heapSCOCI, this.heapDCOCI);
        final CursorResultSet resultSet = this.rowHolder.getResultSet();
        try {
            final FormatableBitSet shift = RowUtil.shift(this.baseRowReadList, 1);
            resultSet.open();
            ExecRow nextRow;
            while ((nextRow = resultSet.getNextRow()) != null) {
                final RowLocation rowLocation = (RowLocation)nextRow.getColumn(this.rlColumnNumber).getObject();
                final boolean fetch = this.deferredBaseCC.fetch(rowLocation, this.deferredSparseRow.getRowArray(), shift);
                if (this.cascadeDelete && !fetch) {
                    continue;
                }
                this.rc.deleteRow(this.deferredBaseRow, rowLocation);
                this.source.markRowAsDeleted();
            }
        }
        finally {
            resultSet.close();
        }
    }
    
    void runFkChecker(final boolean b) throws StandardException {
        if (this.fkChecker != null) {
            final CursorResultSet resultSet = this.rowHolder.getResultSet();
            try {
                resultSet.open();
                ExecRow nextRow;
                while ((nextRow = resultSet.getNextRow()) != null) {
                    this.fkChecker.doPKCheck(nextRow, b);
                }
            }
            finally {
                resultSet.close();
            }
        }
    }
    
    NoPutResultSet createDependentSource(final RowChanger rowChanger) throws StandardException {
        return null;
    }
    
    public void cleanUp() throws StandardException {
        this.numOpens = 0;
        if (this.source != null) {
            this.source.close();
        }
        if (this.rc != null) {
            this.rc.close();
        }
        if (this.rowHolder != null) {
            this.rowHolder.close();
        }
        if (this.fkChecker != null) {
            this.fkChecker.close();
        }
        if (this.deferredBaseCC != null) {
            this.deferredBaseCC.close();
        }
        this.deferredBaseCC = null;
        if (this.rc != null) {
            this.rc.close();
        }
        super.close();
    }
    
    public void finish() throws StandardException {
        if (this.source != null) {
            this.source.finish();
        }
        super.finish();
    }
}
