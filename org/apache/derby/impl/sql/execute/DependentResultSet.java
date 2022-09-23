// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.loader.GeneratedMethod;
import org.apache.derby.iapi.sql.Activation;
import java.util.Properties;
import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.sql.execute.ExecIndexRow;
import java.util.Vector;
import org.apache.derby.iapi.sql.execute.TemporaryRowHolder;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.store.access.DynamicCompiledOpenConglomInfo;
import org.apache.derby.iapi.store.access.StaticCompiledOpenConglomInfo;
import org.apache.derby.iapi.store.access.ScanController;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.iapi.sql.execute.CursorResultSet;

class DependentResultSet extends ScanResultSet implements CursorResultSet
{
    ConglomerateController heapCC;
    RowLocation baseRowLocation;
    ExecRow indexRow;
    IndexRow indexQualifierRow;
    ScanController indexSC;
    StaticCompiledOpenConglomInfo indexScoci;
    DynamicCompiledOpenConglomInfo indexDcoci;
    int numFkColumns;
    boolean isOpen;
    boolean deferred;
    TemporaryRowHolderResultSet source;
    TransactionController tc;
    String parentResultSetId;
    int[] fkColArray;
    RowLocation rowLocation;
    TemporaryRowHolder[] sourceRowHolders;
    TemporaryRowHolderResultSet[] sourceResultSets;
    int[] sourceOpened;
    int sArrayIndex;
    Vector sVector;
    protected ScanController scanController;
    protected boolean scanControllerOpened;
    protected boolean isKeyed;
    protected boolean firstScan;
    protected ExecIndexRow startPosition;
    protected ExecIndexRow stopPosition;
    protected long conglomId;
    protected DynamicCompiledOpenConglomInfo heapDcoci;
    protected StaticCompiledOpenConglomInfo heapScoci;
    protected int startSearchOperator;
    protected int stopSearchOperator;
    protected Qualifier[][] qualifiers;
    public String tableName;
    public String userSuppliedOptimizerOverrides;
    public String indexName;
    protected boolean runTimeStatisticsOn;
    public int rowsPerRead;
    public boolean forUpdate;
    private Properties scanProperties;
    public String startPositionString;
    public String stopPositionString;
    public boolean isConstraint;
    public boolean coarserLock;
    public boolean oneRowScan;
    protected long rowsThisScan;
    ExecRow searchRow;
    
    DependentResultSet(final long conglomId, final StaticCompiledOpenConglomInfo heapScoci, final Activation activation, final int n, final int n2, final GeneratedMethod generatedMethod, final int startSearchOperator, final GeneratedMethod generatedMethod2, final int stopSearchOperator, final boolean b, final Qualifier[][] qualifiers, final String tableName, final String userSuppliedOptimizerOverrides, final String s, final boolean isConstraint, final boolean forUpdate, final int n3, final int n4, final boolean b2, final int n5, final int rowsPerRead, final boolean oneRowScan, final double n6, final double n7, final String parentResultSetId, final long n8, final int n9, final int n10) throws StandardException {
        super(activation, n2, n, n4, b2, 4, n3, n6, n7);
        this.firstScan = true;
        this.searchRow = null;
        this.conglomId = conglomId;
        this.heapScoci = heapScoci;
        this.heapDcoci = activation.getTransactionController().getDynamicCompiledConglomInfo(conglomId);
        this.startSearchOperator = startSearchOperator;
        this.stopSearchOperator = stopSearchOperator;
        this.qualifiers = qualifiers;
        this.tableName = tableName;
        this.userSuppliedOptimizerOverrides = userSuppliedOptimizerOverrides;
        this.indexName = "On Foreign Key";
        this.isConstraint = isConstraint;
        this.forUpdate = forUpdate;
        this.rowsPerRead = rowsPerRead;
        this.oneRowScan = oneRowScan;
        this.runTimeStatisticsOn = (activation != null && activation.getLanguageConnectionContext().getRunTimeStatisticsMode());
        this.tc = activation.getTransactionController();
        this.indexDcoci = this.tc.getDynamicCompiledConglomInfo(n8);
        this.indexScoci = this.tc.getStaticCompiledConglomInfo(n8);
        this.parentResultSetId = parentResultSetId;
        this.fkColArray = (int[])activation.getPreparedStatement().getSavedObject(n9);
        this.rowLocation = (RowLocation)activation.getPreparedStatement().getSavedObject(n10);
        this.numFkColumns = this.fkColArray.length;
        this.indexQualifierRow = new IndexRow(this.numFkColumns);
        this.recordConstructorTime();
    }
    
    private ScanController openIndexScanController(final ExecRow execRow) throws StandardException {
        this.setupQualifierRow(execRow);
        return this.indexSC = this.tc.openCompiledScan(false, 4, this.lockMode, this.isolationLevel, null, this.indexQualifierRow.getRowArray(), 1, null, this.indexQualifierRow.getRowArray(), -1, this.indexScoci, this.indexDcoci);
    }
    
    private void reopenIndexScanController(final ExecRow execRow) throws StandardException {
        this.setupQualifierRow(execRow);
        this.indexSC.reopenScan(this.indexQualifierRow.getRowArray(), 1, null, this.indexQualifierRow.getRowArray(), -1);
    }
    
    private void setupQualifierRow(final ExecRow execRow) {
        final DataValueDescriptor[] rowArray = this.indexQualifierRow.getRowArray();
        final DataValueDescriptor[] rowArray2 = execRow.getRowArray();
        for (int i = 0; i < this.numFkColumns; ++i) {
            rowArray[i] = rowArray2[this.fkColArray[i] - 1];
        }
    }
    
    private void openIndexScan(final ExecRow execRow) throws StandardException {
        if (this.indexSC == null) {
            this.indexSC = this.openIndexScanController(execRow);
            (this.indexRow = this.indexQualifierRow.getClone()).setColumn(this.numFkColumns + 1, this.rowLocation.cloneValue(false));
        }
        else {
            this.reopenIndexScanController(execRow);
        }
    }
    
    private ExecRow fetchIndexRow() throws StandardException {
        if (!this.indexSC.fetchNext(this.indexRow.getRowArray())) {
            return null;
        }
        return this.indexRow;
    }
    
    private ExecRow fetchBaseRow() throws StandardException {
        if (this.currentRow == null) {
            this.currentRow = this.getCompactRow(this.candidate, this.accessedCols, this.isKeyed);
        }
        this.baseRowLocation = (RowLocation)this.indexRow.getColumn(this.indexRow.getRowArray().length);
        this.heapCC.fetch(this.baseRowLocation, this.candidate.getRowArray(), this.accessedCols);
        return this.currentRow;
    }
    
    public ExecRow getNextRowCore() throws StandardException {
        if (this.isXplainOnlyMode()) {
            return null;
        }
        this.beginTime = this.getCurrentTimeMillis();
        if (this.searchRow == null && (this.searchRow = this.getNextParentRow()) != null) {
            this.openIndexScan(this.searchRow);
        }
        ExecRow fetchIndexRow = null;
        while (this.searchRow != null) {
            fetchIndexRow = this.fetchIndexRow();
            if (fetchIndexRow != null) {
                break;
            }
            if ((this.searchRow = this.getNextParentRow()) == null) {
                continue;
            }
            this.openIndexScan(this.searchRow);
        }
        this.nextTime += this.getElapsedMillis(this.beginTime);
        if (fetchIndexRow != null) {
            ++this.rowsSeen;
            return this.fetchBaseRow();
        }
        return fetchIndexRow;
    }
    
    private ExecRow getNextParentRow() throws StandardException {
        if (this.sourceOpened[this.sArrayIndex] == 0) {
            (this.source = (TemporaryRowHolderResultSet)this.sourceRowHolders[this.sArrayIndex].getResultSet()).open();
            this.sourceOpened[this.sArrayIndex] = -1;
            this.sourceResultSets[this.sArrayIndex] = this.source;
        }
        if (this.sourceOpened[this.sArrayIndex] == 1) {
            (this.source = this.sourceResultSets[this.sArrayIndex]).reStartScan(this.sourceRowHolders[this.sArrayIndex].getTemporaryConglomId(), this.sourceRowHolders[this.sArrayIndex].getPositionIndexConglomId());
            this.sourceOpened[this.sArrayIndex] = -1;
        }
        if (this.sVector.size() > this.sourceRowHolders.length) {
            this.addNewSources();
        }
        ExecRow execRow;
        for (execRow = this.source.getNextRow(); execRow == null && this.sArrayIndex + 1 < this.sourceRowHolders.length; execRow = this.source.getNextRow()) {
            ++this.sArrayIndex;
            if (this.sourceOpened[this.sArrayIndex] == 0) {
                (this.source = (TemporaryRowHolderResultSet)this.sourceRowHolders[this.sArrayIndex].getResultSet()).open();
                this.sourceOpened[this.sArrayIndex] = -1;
                this.sourceResultSets[this.sArrayIndex] = this.source;
            }
            if (this.sourceOpened[this.sArrayIndex] == 1) {
                (this.source = this.sourceResultSets[this.sArrayIndex]).reStartScan(this.sourceRowHolders[this.sArrayIndex].getTemporaryConglomId(), this.sourceRowHolders[this.sArrayIndex].getPositionIndexConglomId());
                this.sourceOpened[this.sArrayIndex] = -1;
            }
        }
        if (execRow == null) {
            this.sArrayIndex = 0;
            for (int i = 0; i < this.sourceOpened.length; ++i) {
                this.sourceOpened[i] = 1;
            }
        }
        return execRow;
    }
    
    public ConglomerateController openHeapConglomerateController() throws StandardException {
        return this.tc.openCompiledConglomerate(false, 4, this.lockMode, this.isolationLevel, this.heapScoci, this.heapDcoci);
    }
    
    public void close() throws StandardException {
        if (this.runTimeStatisticsOn) {
            this.startPositionString = this.printStartPosition();
            this.stopPositionString = this.printStopPosition();
            this.scanProperties = this.getScanProperties();
        }
        if (this.indexSC != null) {
            this.indexSC.close();
            this.indexSC = null;
        }
        if (this.heapCC != null) {
            this.heapCC.close();
            this.heapCC = null;
        }
        if (this.isOpen) {
            this.source.close();
        }
        this.closeTime += this.getElapsedMillis(this.beginTime);
    }
    
    public void finish() throws StandardException {
        if (this.source != null) {
            this.source.finish();
        }
        this.finishAndRTS();
    }
    
    public void openCore() throws StandardException {
        this.initIsolationLevel();
        this.sVector = this.activation.getParentResultSet(this.parentResultSetId);
        final int size = this.sVector.size();
        this.sourceRowHolders = new TemporaryRowHolder[size];
        this.sourceOpened = new int[size];
        this.sourceResultSets = new TemporaryRowHolderResultSet[size];
        for (int i = 0; i < size; ++i) {
            this.sourceRowHolders[i] = (TemporaryRowHolder)this.sVector.elementAt(i);
            this.sourceOpened[i] = 0;
        }
        this.heapCC = this.openHeapConglomerateController();
        ++this.numOpens;
        this.openTime += this.getElapsedMillis(this.beginTime);
    }
    
    private void addNewSources() {
        final int size = this.sVector.size();
        final TemporaryRowHolder[] sourceRowHolders = new TemporaryRowHolder[size];
        final int[] sourceOpened = new int[size];
        final TemporaryRowHolderResultSet[] sourceResultSets = new TemporaryRowHolderResultSet[size];
        System.arraycopy(this.sourceRowHolders, 0, sourceRowHolders, 0, this.sourceRowHolders.length);
        System.arraycopy(this.sourceOpened, 0, sourceOpened, 0, this.sourceOpened.length);
        System.arraycopy(this.sourceResultSets, 0, sourceResultSets, 0, this.sourceResultSets.length);
        for (int i = this.sourceRowHolders.length; i < size; ++i) {
            sourceRowHolders[i] = (TemporaryRowHolder)this.sVector.elementAt(i);
            sourceOpened[i] = 0;
        }
        this.sourceRowHolders = sourceRowHolders;
        this.sourceOpened = sourceOpened;
        this.sourceResultSets = sourceResultSets;
    }
    
    boolean canGetInstantaneousLocks() {
        return false;
    }
    
    public long getTimeSpent(final int n) {
        return this.constructorTime + this.openTime + this.nextTime + this.closeTime;
    }
    
    public RowLocation getRowLocation() throws StandardException {
        return this.baseRowLocation;
    }
    
    public ExecRow getCurrentRow() throws StandardException {
        return this.currentRow;
    }
    
    public Properties getScanProperties() {
        if (this.scanProperties == null) {
            this.scanProperties = new Properties();
        }
        try {
            if (this.indexSC != null) {
                this.indexSC.getScanInfo().getAllScanInfo(this.scanProperties);
                this.coarserLock = (this.indexSC.isTableLocked() && this.lockMode == 6);
            }
        }
        catch (StandardException ex) {}
        return this.scanProperties;
    }
    
    public String printStartPosition() {
        return this.printPosition(1, this.indexQualifierRow);
    }
    
    public String printStopPosition() {
        return this.printPosition(-1, this.indexQualifierRow);
    }
    
    private String printPosition(final int i, final ExecIndexRow execIndexRow) {
        String str = "";
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
        if (execIndexRow != null) {
            str = str + "\t" + MessageService.getTextMessage("42Z40.U", string, String.valueOf(execIndexRow.nColumns())) + "\n" + "\t" + MessageService.getTextMessage("42Z41.U") + "\n";
            boolean b = false;
            for (int j = 0; j < execIndexRow.nColumns(); ++j) {
                if (execIndexRow.areNullsOrdered(j)) {
                    str = str + j + " ";
                    b = true;
                }
                if (b && j == execIndexRow.nColumns() - 1) {
                    str += "\n";
                }
            }
        }
        return str;
    }
    
    public String printQualifiers() {
        return "" + MessageService.getTextMessage("42Z37.U");
    }
}
