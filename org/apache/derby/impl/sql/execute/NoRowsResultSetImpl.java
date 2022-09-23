// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import java.sql.SQLWarning;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.services.loader.GeneratedMethod;
import org.apache.derby.iapi.services.i18n.MessageService;
import java.sql.Timestamp;
import org.apache.derby.iapi.sql.execute.RunTimeStatistics;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.ResultDescription;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.conn.StatementContext;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.ResultSet;

abstract class NoRowsResultSetImpl implements ResultSet
{
    final Activation activation;
    private NoPutResultSet[] subqueryTrackingArray;
    private final boolean statisticsTimingOn;
    private boolean isOpen;
    final LanguageConnectionContext lcc;
    protected long beginTime;
    protected long endTime;
    protected long beginExecutionTime;
    protected long endExecutionTime;
    private int firstColumn;
    private int[] generatedColumnPositions;
    private DataValueDescriptor[] normalizedGeneratedValues;
    
    NoRowsResultSetImpl(final Activation activation) {
        this.firstColumn = -1;
        this.activation = activation;
        this.lcc = activation.getLanguageConnectionContext();
        this.statisticsTimingOn = this.lcc.getStatisticsTiming();
        this.beginTime = this.getCurrentTimeMillis();
        this.beginExecutionTime = this.beginTime;
    }
    
    void setup() throws StandardException {
        this.isOpen = true;
        final StatementContext statementContext = this.lcc.getStatementContext();
        statementContext.setTopResultSet(this, this.subqueryTrackingArray);
        if (this.subqueryTrackingArray == null) {
            this.subqueryTrackingArray = statementContext.getSubqueryTrackingArray();
        }
    }
    
    public final boolean returnsRows() {
        return false;
    }
    
    public long modifiedRowCount() {
        return 0L;
    }
    
    public ResultDescription getResultDescription() {
        return null;
    }
    
    public final Activation getActivation() {
        return this.activation;
    }
    
    public final ExecRow getAbsoluteRow(final int n) throws StandardException {
        throw StandardException.newException("XCL01.S", "absolute");
    }
    
    public final ExecRow getRelativeRow(final int n) throws StandardException {
        throw StandardException.newException("XCL01.S", "relative");
    }
    
    public final ExecRow setBeforeFirstRow() throws StandardException {
        throw StandardException.newException("XCL01.S", "beforeFirst");
    }
    
    public final ExecRow getFirstRow() throws StandardException {
        throw StandardException.newException("XCL01.S", "first");
    }
    
    public final ExecRow getNextRow() throws StandardException {
        throw StandardException.newException("XCL01.S", "next");
    }
    
    public final ExecRow getPreviousRow() throws StandardException {
        throw StandardException.newException("XCL01.S", "previous");
    }
    
    public final ExecRow getLastRow() throws StandardException {
        throw StandardException.newException("XCL01.S", "last");
    }
    
    public final ExecRow setAfterLastRow() throws StandardException {
        throw StandardException.newException("XCL01.S", "afterLast");
    }
    
    public final void clearCurrentRow() {
    }
    
    public final boolean checkRowPosition(final int n) {
        return false;
    }
    
    public final int getRowNumber() {
        return 0;
    }
    
    public void close() throws StandardException {
        if (!this.isOpen) {
            return;
        }
        if (this.lcc.getRunTimeStatisticsMode() && !this.doesCommit() && !this.activation.isClosed() && !this.lcc.getStatementContext().getStatementWasInvalidated()) {
            this.endExecutionTime = this.getCurrentTimeMillis();
            final RunTimeStatistics runTimeStatistics = this.lcc.getLanguageConnectionFactory().getExecutionFactory().getResultSetStatisticsFactory().getRunTimeStatistics(this.activation, this, this.subqueryTrackingArray);
            this.lcc.setRunTimeStatisticsObject(runTimeStatistics);
            this.lcc.getLanguageConnectionFactory().getExecutionFactory().getXPLAINFactory().getXPLAINVisitor().doXPLAIN(runTimeStatistics, this.activation);
        }
        for (int n = (this.subqueryTrackingArray == null) ? 0 : this.subqueryTrackingArray.length, i = 0; i < n; ++i) {
            if (this.subqueryTrackingArray[i] != null) {
                if (!this.subqueryTrackingArray[i].isClosed()) {
                    this.subqueryTrackingArray[i].close();
                }
            }
        }
        this.isOpen = false;
        if (this.activation.isSingleExecution()) {
            this.activation.close();
        }
    }
    
    public boolean isClosed() {
        return !this.isOpen;
    }
    
    public void finish() throws StandardException {
    }
    
    public long getExecuteTime() {
        return this.endTime - this.beginTime;
    }
    
    public Timestamp getBeginExecutionTimestamp() {
        if (this.beginExecutionTime == 0L) {
            return null;
        }
        return new Timestamp(this.beginExecutionTime);
    }
    
    public Timestamp getEndExecutionTimestamp() {
        if (this.endExecutionTime == 0L) {
            return null;
        }
        return new Timestamp(this.endExecutionTime);
    }
    
    public String getQueryPlanText(final int n) {
        return MessageService.getTextMessage("42Z47.U", this.getClass().getName());
    }
    
    public long getTimeSpent(final int n) {
        return 0L;
    }
    
    public final NoPutResultSet[] getSubqueryTrackingArray(final int n) {
        if (this.subqueryTrackingArray == null) {
            this.subqueryTrackingArray = new NoPutResultSet[n];
        }
        return this.subqueryTrackingArray;
    }
    
    public ResultSet getAutoGeneratedKeysResultset() {
        return null;
    }
    
    public String getCursorName() {
        return null;
    }
    
    protected final long getCurrentTimeMillis() {
        if (this.statisticsTimingOn) {
            return System.currentTimeMillis();
        }
        return 0L;
    }
    
    public static void evaluateACheckConstraint(final GeneratedMethod generatedMethod, final String s, final long n, final Activation activation) throws StandardException {
        if (generatedMethod != null) {
            final DataValueDescriptor dataValueDescriptor = (DataValueDescriptor)generatedMethod.invoke(activation);
            if (dataValueDescriptor != null && !dataValueDescriptor.isNull() && !dataValueDescriptor.getBoolean()) {
                final DataDictionary dataDictionary = activation.getLanguageConnectionContext().getDataDictionary();
                throw StandardException.newException("23513", dataDictionary.getTableDescriptor(dataDictionary.getConglomerateDescriptor(n).getTableID()).getQualifiedName(), s);
            }
        }
    }
    
    public void evaluateGenerationClauses(final GeneratedMethod generatedMethod, final Activation activation, final NoPutResultSet set, final ExecRow currentRow, final boolean b) throws StandardException {
        if (generatedMethod != null) {
            final ExecRow currentRow2 = (ExecRow)activation.getCurrentRow(set.resultSetNumber());
            try {
                set.setCurrentRow(currentRow);
                generatedMethod.invoke(activation);
                if (this.firstColumn < 0) {
                    this.firstColumn = NormalizeResultSet.computeStartColumn(b, activation.getResultDescription());
                }
                if (this.generatedColumnPositions == null) {
                    this.setupGeneratedColumns(activation, (ValueRow)currentRow);
                }
                final ResultDescription resultDescription = activation.getResultDescription();
                for (int length = this.generatedColumnPositions.length, i = 0; i < length; ++i) {
                    final int n = this.generatedColumnPositions[i];
                    currentRow.setColumn(n, NormalizeResultSet.normalizeColumn(resultDescription.getColumnDescriptor(n).getType(), currentRow, n, this.normalizedGeneratedValues[i], resultDescription));
                }
            }
            finally {
                if (currentRow2 == null) {
                    set.clearCurrentRow();
                }
                else {
                    set.setCurrentRow(currentRow2);
                }
            }
        }
    }
    
    private void setupGeneratedColumns(final Activation activation, final ValueRow valueRow) throws StandardException {
        final ResultDescription resultDescription = activation.getResultDescription();
        final int columnCount = resultDescription.getColumnCount();
        final ExecRow newNullRow = valueRow.getNewNullRow();
        int n = 0;
        for (int i = 1; i <= columnCount; ++i) {
            if (i >= this.firstColumn) {
                if (resultDescription.getColumnDescriptor(i).hasGenerationClause()) {
                    ++n;
                }
            }
        }
        this.generatedColumnPositions = new int[n];
        this.normalizedGeneratedValues = new DataValueDescriptor[n];
        int n2 = 0;
        for (int j = 1; j <= columnCount; ++j) {
            if (j >= this.firstColumn) {
                if (resultDescription.getColumnDescriptor(j).hasGenerationClause()) {
                    this.generatedColumnPositions[n2] = j;
                    this.normalizedGeneratedValues[n2] = newNullRow.getColumn(j);
                    ++n2;
                }
            }
        }
    }
    
    public static void evaluateCheckConstraints(final GeneratedMethod generatedMethod, final Activation activation) throws StandardException {
        if (generatedMethod != null) {
            generatedMethod.invoke(activation);
        }
    }
    
    public boolean doesCommit() {
        return false;
    }
    
    public void addWarning(final SQLWarning sqlWarning) {
        this.getActivation().addWarning(sqlWarning);
    }
    
    public SQLWarning getWarnings() {
        return null;
    }
}
