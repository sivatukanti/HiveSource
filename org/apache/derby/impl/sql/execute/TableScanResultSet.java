// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.store.access.BackingStoreHashtable;
import java.util.Properties;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.services.loader.GeneratedMethod;
import org.apache.derby.iapi.store.access.StaticCompiledOpenConglomInfo;
import org.apache.derby.iapi.store.access.DynamicCompiledOpenConglomInfo;
import org.apache.derby.iapi.sql.execute.ExecIndexRow;
import org.apache.derby.iapi.store.access.ScanController;
import org.apache.derby.iapi.sql.execute.CursorResultSet;

class TableScanResultSet extends ScanResultSet implements CursorResultSet, Cloneable
{
    protected ScanController scanController;
    protected boolean scanControllerOpened;
    protected boolean isKeyed;
    protected boolean firstScan;
    protected ExecIndexRow startPosition;
    protected ExecIndexRow stopPosition;
    protected long conglomId;
    protected DynamicCompiledOpenConglomInfo dcoci;
    protected StaticCompiledOpenConglomInfo scoci;
    protected GeneratedMethod startKeyGetter;
    protected int startSearchOperator;
    protected GeneratedMethod stopKeyGetter;
    protected int stopSearchOperator;
    public Qualifier[][] qualifiers;
    public String tableName;
    public String userSuppliedOptimizerOverrides;
    public String indexName;
    protected boolean runTimeStatisticsOn;
    protected int[] indexCols;
    public int rowsPerRead;
    public boolean forUpdate;
    final boolean sameStartStopPosition;
    private boolean nextDone;
    private RowLocation rlTemplate;
    private Properties scanProperties;
    public String startPositionString;
    public String stopPositionString;
    public boolean isConstraint;
    public boolean coarserLock;
    public boolean oneRowScan;
    protected long rowsThisScan;
    private long estimatedRowCount;
    protected BackingStoreHashtable past2FutureTbl;
    private boolean qualify;
    private boolean currentRowIsValid;
    private boolean scanRepositioned;
    
    TableScanResultSet(final long conglomId, final StaticCompiledOpenConglomInfo scoci, final Activation activation, final int n, final int n2, final GeneratedMethod startKeyGetter, final int startSearchOperator, final GeneratedMethod stopKeyGetter, final int stopSearchOperator, final boolean sameStartStopPosition, final Qualifier[][] qualifiers, final String tableName, final String userSuppliedOptimizerOverrides, final String indexName, final boolean isConstraint, final boolean forUpdate, final int n3, final int n4, final int n5, final boolean b, final int n6, final int rowsPerRead, final boolean oneRowScan, final double n7, final double n8) throws StandardException {
        super(activation, n2, n, n5, b, n6, n3, n7, n8);
        this.firstScan = true;
        this.conglomId = conglomId;
        this.scoci = scoci;
        this.startKeyGetter = startKeyGetter;
        this.startSearchOperator = startSearchOperator;
        this.stopKeyGetter = stopKeyGetter;
        this.stopSearchOperator = stopSearchOperator;
        this.sameStartStopPosition = sameStartStopPosition;
        this.qualifiers = qualifiers;
        this.tableName = tableName;
        this.userSuppliedOptimizerOverrides = userSuppliedOptimizerOverrides;
        this.indexName = indexName;
        this.isConstraint = isConstraint;
        this.forUpdate = forUpdate;
        this.rowsPerRead = rowsPerRead;
        this.oneRowScan = oneRowScan;
        if (n4 != -1) {
            this.indexCols = (int[])activation.getPreparedStatement().getSavedObject(n4);
        }
        if (this.indexCols != null) {
            activation.setForUpdateIndexScan(this);
        }
        this.runTimeStatisticsOn = (activation != null && activation.getLanguageConnectionContext().getRunTimeStatisticsMode());
        this.qualify = true;
        this.currentRowIsValid = false;
        this.scanRepositioned = false;
        this.recordConstructorTime();
    }
    
    public void openCore() throws StandardException {
        final TransactionController transactionController = this.activation.getTransactionController();
        this.initIsolationLevel();
        if (this.dcoci == null) {
            this.dcoci = transactionController.getDynamicCompiledConglomInfo(this.conglomId);
        }
        this.initStartAndStopKey();
        if (this.firstScan) {
            this.openScanController(transactionController);
            this.isKeyed = this.scanController.isKeyed();
        }
        if (this.skipScan(this.startPosition, this.stopPosition)) {
            this.scanControllerOpened = false;
        }
        else if (!this.firstScan) {
            this.openScanController(transactionController);
        }
        if (this.forUpdate && this.isKeyed) {
            this.activation.setIndexScanController(this.scanController);
            this.activation.setIndexConglomerateNumber(this.conglomId);
        }
        this.firstScan = false;
        this.isOpen = true;
        ++this.numOpens;
        this.nextDone = false;
        this.openTime += this.getElapsedMillis(this.beginTime);
    }
    
    void initStartAndStopKey() throws StandardException {
        if (this.startKeyGetter != null) {
            this.startPosition = (ExecIndexRow)this.startKeyGetter.invoke(this.activation);
            if (this.sameStartStopPosition) {
                this.stopPosition = this.startPosition;
            }
        }
        if (this.stopKeyGetter != null) {
            this.stopPosition = (ExecIndexRow)this.stopKeyGetter.invoke(this.activation);
        }
    }
    
    protected void openScanController(TransactionController transactionController) throws StandardException {
        final DataValueDescriptor[] array = (DataValueDescriptor[])((this.startPosition == null) ? null : this.startPosition.getRowArray());
        final DataValueDescriptor[] array2 = (DataValueDescriptor[])((this.stopPosition == null) ? null : this.stopPosition.getRowArray());
        if (this.qualifiers != null) {
            this.clearOrderableCache(this.qualifiers);
        }
        if (transactionController == null) {
            transactionController = this.activation.getTransactionController();
        }
        int n = 0;
        if (this.forUpdate) {
            n = 4;
            if (this.activation.isCursorActivation()) {
                n |= 0x1000;
            }
        }
        this.scanController = transactionController.openCompiledScan(this.activation.getResultSetHoldability(), n, this.lockMode, this.isolationLevel, this.accessedCols, array, this.startSearchOperator, this.qualifiers, array2, this.stopSearchOperator, this.scoci, this.dcoci);
        this.scanControllerOpened = true;
        this.rowsThisScan = 0L;
        this.estimatedRowCount = this.scanController.getEstimatedRowCount();
        this.activation.informOfRowCount(this, this.scanController.getEstimatedRowCount());
    }
    
    protected void reopenScanController() throws StandardException {
        final DataValueDescriptor[] array = (DataValueDescriptor[])((this.startPosition == null) ? null : this.startPosition.getRowArray());
        final DataValueDescriptor[] array2 = (DataValueDescriptor[])((this.stopPosition == null) ? null : this.stopPosition.getRowArray());
        this.rowsThisScan = 0L;
        if (this.qualifiers != null) {
            this.clearOrderableCache(this.qualifiers);
        }
        this.scanController.reopenScan(array, this.startSearchOperator, this.qualifiers, array2, this.stopSearchOperator);
        this.scanControllerOpened = true;
    }
    
    public void reopenCore() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        this.initStartAndStopKey();
        if (this.skipScan(this.startPosition, this.stopPosition)) {
            this.scanControllerOpened = false;
        }
        else if (this.scanController == null) {
            this.openScanController(null);
        }
        else {
            this.reopenScanController();
        }
        ++this.numOpens;
        this.nextDone = false;
        this.openTime += this.getElapsedMillis(this.beginTime);
    }
    
    public ExecRow getNextRowCore() throws StandardException {
        if (this.isXplainOnlyMode()) {
            return null;
        }
        this.checkCancellationFlag();
        if (this.currentRow == null || this.scanRepositioned) {
            this.currentRow = this.getCompactRow(this.candidate, this.accessedCols, this.isKeyed);
        }
        this.beginTime = this.getCurrentTimeMillis();
        ExecRow currentRow = null;
        if (this.isOpen && !this.nextDone) {
            this.nextDone = this.oneRowScan;
            if (this.scanControllerOpened) {
                boolean fetchNext;
                while (fetchNext = this.scanController.fetchNext(this.candidate.getRowArray())) {
                    ++this.rowsSeen;
                    ++this.rowsThisScan;
                    if (!this.sameStartStopPosition && this.skipRow(this.candidate)) {
                        ++this.rowsFiltered;
                    }
                    else {
                        if (this.past2FutureTbl != null && this.past2FutureTbl.remove(this.currentRow.getColumn(this.currentRow.nColumns())) != null) {
                            continue;
                        }
                        currentRow = this.currentRow;
                        break;
                    }
                }
                if (!fetchNext) {
                    this.setRowCountIfPossible(this.rowsThisScan);
                    this.currentRow = null;
                }
            }
        }
        this.setCurrentRow(currentRow);
        this.currentRowIsValid = true;
        this.scanRepositioned = false;
        this.qualify = true;
        this.nextTime += this.getElapsedMillis(this.beginTime);
        return currentRow;
    }
    
    public void close() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        if (this.isOpen) {
            this.clearCurrentRow();
            if (this.scanController != null) {
                if (this.runTimeStatisticsOn) {
                    this.scanProperties = this.getScanProperties();
                    this.startPositionString = this.printStartPosition();
                    this.stopPositionString = this.printStopPosition();
                }
                this.scanController.close();
                this.scanController = null;
                if (this.forUpdate && this.isKeyed) {
                    this.activation.clearIndexScanInfo();
                }
            }
            this.scanControllerOpened = false;
            this.startPosition = null;
            this.stopPosition = null;
            super.close();
            if (this.indexCols != null) {
                final ConglomerateController heapConglomerateController = this.activation.getHeapConglomerateController();
                if (heapConglomerateController != null) {
                    heapConglomerateController.close();
                    this.activation.clearHeapConglomerateController();
                }
            }
            if (this.past2FutureTbl != null) {
                this.past2FutureTbl.close();
            }
        }
        this.closeTime += this.getElapsedMillis(this.beginTime);
    }
    
    public long getTimeSpent(final int n) {
        final long n2 = this.constructorTime + this.openTime + this.nextTime + this.closeTime;
        if (n == 0) {
            return n2;
        }
        return n2;
    }
    
    public RowLocation getRowLocation() throws StandardException {
        if (!this.isOpen) {
            return null;
        }
        if (!this.scanControllerOpened) {
            return null;
        }
        RowLocation rlTemplate;
        if (this.isKeyed) {
            rlTemplate = (RowLocation)this.currentRow.getColumn(this.currentRow.nColumns());
        }
        else {
            if (this.currentRowIsValid) {
                if (this.rlTemplate == null) {
                    this.rlTemplate = this.scanController.newRowLocationTemplate();
                }
                rlTemplate = this.rlTemplate;
                try {
                    this.scanController.fetchLocation(rlTemplate);
                    return rlTemplate;
                }
                catch (StandardException ex) {
                    if (ex.getMessageId().equals("XSCH7.S")) {
                        throw StandardException.newException("24000");
                    }
                    throw ex;
                }
            }
            rlTemplate = null;
        }
        return rlTemplate;
    }
    
    public ExecRow getCurrentRow() throws StandardException {
        try {
            if (this.currentRow == null || !this.currentRowIsValid || !this.scanControllerOpened || (this.qualify && this.scanController.isCurrentPositionDeleted()) || (this.qualify && !this.scanController.doesCurrentPositionQualify())) {
                return null;
            }
        }
        catch (StandardException ex) {
            if (ex.getMessageId().equals("XSAM5.S")) {
                throw StandardException.newException("24000");
            }
        }
        this.resultRowBuilder.reset(this.candidate);
        this.currentRow = this.getCompactRow(this.candidate, this.accessedCols, this.isKeyed);
        try {
            this.scanController.fetchWithoutQualify(this.candidate.getRowArray());
        }
        catch (StandardException ex2) {
            if (ex2.getMessageId().equals("XSAM6.S")) {
                return null;
            }
            throw ex2;
        }
        this.setCurrentRow(this.candidate);
        return this.currentRow;
    }
    
    public void positionScanAtRowLocation(final RowLocation rowLocation) throws StandardException {
        if (!this.isKeyed) {
            this.currentRowIsValid = this.scanController.positionAtRowLocation(rowLocation);
        }
        this.qualify = false;
        this.scanRepositioned = true;
    }
    
    public String printStartPosition() {
        return this.printPosition(this.startSearchOperator, this.startKeyGetter, this.startPosition);
    }
    
    public String printStopPosition() {
        if (this.sameStartStopPosition) {
            return this.printPosition(this.stopSearchOperator, this.startKeyGetter, this.startPosition);
        }
        return this.printPosition(this.stopSearchOperator, this.stopKeyGetter, this.stopPosition);
    }
    
    private String printPosition(final int i, final GeneratedMethod generatedMethod, ExecIndexRow execIndexRow) {
        final String str = "";
        if (generatedMethod == null) {
            return "\t" + MessageService.getTextMessage("42Z37.U") + "\n";
        }
        if (execIndexRow == null) {
            if (this.numOpens == 0) {
                return "\t" + MessageService.getTextMessage("42Z38.U") + "\n";
            }
            try {
                execIndexRow = (ExecIndexRow)generatedMethod.invoke(this.activation);
            }
            catch (StandardException ex) {
                return "\t" + MessageService.getTextMessage("42Z39.U", ex.toString());
            }
        }
        if (execIndexRow == null) {
            return "\t" + MessageService.getTextMessage("42Z37.U") + "\n";
        }
        String string = null;
        switch (i) {
            case 1: {
                string = ">=";
                break;
            }
            case -1: {
                string = ">";
                break;
            }
            default: {
                string = "unknown value (" + i + ")";
                break;
            }
        }
        String s = str + "\t" + MessageService.getTextMessage("42Z40.U", string, String.valueOf(execIndexRow.nColumns())) + "\n" + "\t" + MessageService.getTextMessage("42Z41.U") + "\n";
        boolean b = false;
        for (int j = 0; j < execIndexRow.nColumns(); ++j) {
            if (execIndexRow.areNullsOrdered(j)) {
                s = s + j + " ";
                b = true;
            }
            if (b && j == execIndexRow.nColumns() - 1) {
                s += "\n";
            }
        }
        return s;
    }
    
    public Properties getScanProperties() {
        if (this.scanProperties == null) {
            this.scanProperties = new Properties();
        }
        try {
            if (this.scanController != null) {
                this.scanController.getScanInfo().getAllScanInfo(this.scanProperties);
                this.coarserLock = (this.scanController.isTableLocked() && this.lockMode == 6);
            }
        }
        catch (StandardException ex) {}
        return this.scanProperties;
    }
    
    public boolean requiresRelocking() {
        return this.isolationLevel == 3;
    }
    
    protected final void setRowCountIfPossible(final long estimatedRowCount) throws StandardException {
        if (!this.scanController.isKeyed() && (this.qualifiers == null || this.qualifiers.length == 0) && !this.forUpdate) {
            long n = estimatedRowCount - this.estimatedRowCount;
            final long n2 = this.estimatedRowCount / 10L;
            if (n < 0L) {
                n = -n;
            }
            if (n > n2) {
                this.scanController.setEstimatedRowCount(estimatedRowCount);
            }
        }
    }
    
    protected boolean canGetInstantaneousLocks() {
        return false;
    }
    
    public boolean isForUpdate() {
        return this.forUpdate;
    }
    
    public Object clone() {
        Object clone = null;
        try {
            clone = super.clone();
        }
        catch (CloneNotSupportedException ex) {}
        return clone;
    }
}
