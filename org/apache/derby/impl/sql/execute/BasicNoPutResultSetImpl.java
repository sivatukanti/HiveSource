// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.util.InterruptStatus;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.iapi.sql.ResultSet;
import java.sql.Timestamp;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.ResultDescription;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.conn.StatementContext;
import java.sql.SQLWarning;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;

abstract class BasicNoPutResultSetImpl implements NoPutResultSet
{
    protected boolean isOpen;
    protected boolean finished;
    protected ExecRow currentRow;
    protected boolean isTopResultSet;
    private SQLWarning warnings;
    public int numOpens;
    public int rowsSeen;
    public int rowsFiltered;
    protected long startExecutionTime;
    protected long endExecutionTime;
    public long beginTime;
    public long constructorTime;
    public long openTime;
    public long nextTime;
    public long closeTime;
    public double optimizerEstimatedRowCount;
    public double optimizerEstimatedCost;
    private StatementContext statementContext;
    public NoPutResultSet[] subqueryTrackingArray;
    ExecRow compactRow;
    protected final Activation activation;
    private final boolean statisticsTimingOn;
    ResultDescription resultDescription;
    private transient TransactionController tc;
    private int[] baseColumnMap;
    
    BasicNoPutResultSetImpl(final ResultDescription resultDescription, final Activation activation, final double optimizerEstimatedRowCount, final double optimizerEstimatedCost) {
        this.activation = activation;
        final boolean statisticsTiming = this.getLanguageConnectionContext().getStatisticsTiming();
        this.statisticsTimingOn = statisticsTiming;
        if (statisticsTiming) {
            final long currentTimeMillis = this.getCurrentTimeMillis();
            this.startExecutionTime = currentTimeMillis;
            this.beginTime = currentTimeMillis;
        }
        this.resultDescription = resultDescription;
        this.optimizerEstimatedRowCount = optimizerEstimatedRowCount;
        this.optimizerEstimatedCost = optimizerEstimatedCost;
    }
    
    protected final void recordConstructorTime() {
        if (this.statisticsTimingOn) {
            this.constructorTime = this.getElapsedMillis(this.beginTime);
        }
    }
    
    public final Activation getActivation() {
        return this.activation;
    }
    
    protected final boolean isXplainOnlyMode() {
        final LanguageConnectionContext languageConnectionContext = this.getLanguageConnectionContext();
        return languageConnectionContext.getRunTimeStatisticsMode() && languageConnectionContext.getXplainOnlyMode();
    }
    
    public void reopenCore() throws StandardException {
        this.close();
        this.openCore();
    }
    
    public abstract ExecRow getNextRowCore() throws StandardException;
    
    public int getPointOfAttachment() {
        return -1;
    }
    
    public void markAsTopResultSet() {
        this.isTopResultSet = true;
    }
    
    public int getScanIsolationLevel() {
        return 0;
    }
    
    public double getEstimatedRowCount() {
        return this.optimizerEstimatedRowCount;
    }
    
    public boolean requiresRelocking() {
        return false;
    }
    
    public final void open() throws StandardException {
        this.finished = false;
        this.attachStatementContext();
        try {
            this.openCore();
        }
        catch (StandardException ex) {
            this.activation.checkStatementValidity();
            throw ex;
        }
        this.activation.checkStatementValidity();
    }
    
    public ExecRow getAbsoluteRow(final int n) throws StandardException {
        if (!this.isOpen) {
            throw StandardException.newException("XCL16.S", "absolute");
        }
        this.attachStatementContext();
        return null;
    }
    
    public ExecRow getRelativeRow(final int n) throws StandardException {
        if (!this.isOpen) {
            throw StandardException.newException("XCL16.S", "relative");
        }
        this.attachStatementContext();
        return null;
    }
    
    public ExecRow setBeforeFirstRow() throws StandardException {
        if (!this.isOpen) {
            throw StandardException.newException("XCL16.S", "first");
        }
        return null;
    }
    
    public boolean checkRowPosition(final int n) throws StandardException {
        return false;
    }
    
    public int getRowNumber() {
        return 0;
    }
    
    public ExecRow getFirstRow() throws StandardException {
        if (!this.isOpen) {
            throw StandardException.newException("XCL16.S", "first");
        }
        this.attachStatementContext();
        return null;
    }
    
    public final ExecRow getNextRow() throws StandardException {
        if (this.isXplainOnlyMode()) {
            return null;
        }
        if (!this.isOpen) {
            throw StandardException.newException("XCL16.S", "next");
        }
        this.attachStatementContext();
        return this.getNextRowCore();
    }
    
    public ExecRow getPreviousRow() throws StandardException {
        if (!this.isOpen) {
            throw StandardException.newException("XCL16.S", "previous");
        }
        this.attachStatementContext();
        return null;
    }
    
    public ExecRow getLastRow() throws StandardException {
        if (!this.isOpen) {
            throw StandardException.newException("XCL16.S", "last");
        }
        this.attachStatementContext();
        return null;
    }
    
    public ExecRow setAfterLastRow() throws StandardException {
        if (!this.isOpen) {
            throw StandardException.newException("XCL16.S", "last");
        }
        return null;
    }
    
    public boolean returnsRows() {
        return true;
    }
    
    public final long modifiedRowCount() {
        return 0L;
    }
    
    public void cleanUp() throws StandardException {
        if (this.isOpen) {
            this.close();
        }
    }
    
    public boolean isClosed() {
        return !this.isOpen;
    }
    
    public void finish() throws StandardException {
        this.finishAndRTS();
    }
    
    protected final void finishAndRTS() throws StandardException {
        if (!this.finished) {
            if (!this.isClosed()) {
                this.close();
            }
            this.finished = true;
            if (this.isTopResultSet && this.activation.isSingleExecution()) {
                this.activation.close();
            }
        }
    }
    
    public ResultDescription getResultDescription() {
        return this.resultDescription;
    }
    
    public long getExecuteTime() {
        return this.getTimeSpent(1);
    }
    
    public Timestamp getBeginExecutionTimestamp() {
        if (this.startExecutionTime == 0L) {
            return null;
        }
        return new Timestamp(this.startExecutionTime);
    }
    
    public Timestamp getEndExecutionTimestamp() {
        if (this.endExecutionTime == 0L) {
            return null;
        }
        return new Timestamp(this.endExecutionTime);
    }
    
    public final NoPutResultSet[] getSubqueryTrackingArray(final int n) {
        if (this.subqueryTrackingArray == null) {
            this.subqueryTrackingArray = new NoPutResultSet[n];
        }
        return this.subqueryTrackingArray;
    }
    
    protected final long getCurrentTimeMillis() {
        if (this.statisticsTimingOn) {
            return System.currentTimeMillis();
        }
        return 0L;
    }
    
    public ResultSet getAutoGeneratedKeysResultset() {
        return null;
    }
    
    protected final long getElapsedMillis(final long n) {
        if (this.statisticsTimingOn) {
            return System.currentTimeMillis() - n;
        }
        return 0L;
    }
    
    protected final String dumpTimeStats(final String str, final String s) {
        return str + MessageService.getTextMessage("42Z30.U") + " " + this.getTimeSpent(0) + "\n" + str + MessageService.getTextMessage("42Z31.U") + " " + this.getTimeSpent(1) + "\n" + str + MessageService.getTextMessage("42Z32.U") + "\n" + s + MessageService.getTextMessage("42Z33.U") + " " + this.constructorTime + "\n" + s + MessageService.getTextMessage("42Z34.U") + " " + this.openTime + "\n" + s + MessageService.getTextMessage("42Z35.U") + " " + this.nextTime + "\n" + s + MessageService.getTextMessage("42Z36.U") + " " + this.closeTime;
    }
    
    protected void attachStatementContext() throws StandardException {
        if (this.isTopResultSet) {
            if (this.statementContext == null || !this.statementContext.onStack()) {
                this.statementContext = this.getLanguageConnectionContext().getStatementContext();
            }
            this.statementContext.setTopResultSet(this, this.subqueryTrackingArray);
            if (this.subqueryTrackingArray == null) {
                this.subqueryTrackingArray = this.statementContext.getSubqueryTrackingArray();
            }
            this.statementContext.setActivation(this.activation);
        }
    }
    
    protected final LanguageConnectionContext getLanguageConnectionContext() {
        return this.getActivation().getLanguageConnectionContext();
    }
    
    public int resultSetNumber() {
        return 0;
    }
    
    final ExecutionFactory getExecutionFactory() {
        return this.activation.getExecutionFactory();
    }
    
    final TransactionController getTransactionController() {
        if (this.tc == null) {
            this.tc = this.getLanguageConnectionContext().getTransactionExecute();
        }
        return this.tc;
    }
    
    protected ExecRow getCompactRow(final ExecRow compactRow, final FormatableBitSet set, final boolean b) throws StandardException {
        final int nColumns = compactRow.nColumns();
        if (set == null) {
            this.compactRow = compactRow;
            this.baseColumnMap = new int[nColumns];
            for (int i = 0; i < this.baseColumnMap.length; ++i) {
                this.baseColumnMap[i] = i;
            }
        }
        else {
            final int numBitsSet = set.getNumBitsSet();
            this.baseColumnMap = new int[numBitsSet];
            if (this.compactRow == null) {
                final ExecutionFactory executionFactory = this.getLanguageConnectionContext().getLanguageConnectionFactory().getExecutionFactory();
                if (b) {
                    this.compactRow = executionFactory.getIndexableRow(numBitsSet);
                }
                else {
                    this.compactRow = executionFactory.getValueRow(numBitsSet);
                }
            }
            int n = 0;
            for (int j = set.anySetBit(); j != -1; j = set.anySetBit(j)) {
                if (j >= nColumns) {
                    break;
                }
                final DataValueDescriptor column = compactRow.getColumn(j + 1);
                if (column != null) {
                    this.compactRow.setColumn(n + 1, column);
                }
                this.baseColumnMap[n] = j;
                ++n;
            }
        }
        return this.compactRow;
    }
    
    protected ExecRow setCompactRow(final ExecRow execRow, final ExecRow execRow2) {
        ExecRow execRow3;
        if (this.baseColumnMap == null) {
            execRow3 = execRow;
        }
        else {
            execRow3 = execRow2;
            this.setCompatRow(execRow2, execRow.getRowArray());
        }
        return execRow3;
    }
    
    protected final void setCompatRow(final ExecRow execRow, final DataValueDescriptor[] array) {
        final DataValueDescriptor[] rowArray = execRow.getRowArray();
        final int[] baseColumnMap = this.baseColumnMap;
        for (int i = 0; i < baseColumnMap.length; ++i) {
            rowArray[i] = array[baseColumnMap[i]];
        }
    }
    
    public boolean isForUpdate() {
        return false;
    }
    
    public void checkCancellationFlag() throws StandardException {
        final LanguageConnectionContext languageConnectionContext = this.getLanguageConnectionContext();
        final StatementContext statementContext = languageConnectionContext.getStatementContext();
        if (statementContext == null) {
            return;
        }
        InterruptStatus.throwIf(languageConnectionContext);
        if (statementContext.isCancelled()) {
            throw StandardException.newException("XCL52.S");
        }
    }
    
    public final void addWarning(final SQLWarning sqlWarning) {
        if (this.isTopResultSet) {
            if (this.warnings == null) {
                this.warnings = sqlWarning;
            }
            else {
                this.warnings.setNextWarning(sqlWarning);
            }
            return;
        }
        if (this.activation != null) {
            final ResultSet resultSet = this.activation.getResultSet();
            if (resultSet != null) {
                resultSet.addWarning(sqlWarning);
            }
        }
    }
    
    public final SQLWarning getWarnings() {
        final SQLWarning warnings = this.warnings;
        this.warnings = null;
        return warnings;
    }
}
