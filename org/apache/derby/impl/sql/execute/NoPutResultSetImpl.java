// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.execute.RowChanger;
import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.iapi.sql.execute.ExecIndexRow;
import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.execute.RunTimeStatistics;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.ResultSet;
import org.apache.derby.iapi.sql.ResultDescription;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.execute.TargetResultSet;
import org.apache.derby.iapi.sql.execute.ExecRow;

abstract class NoPutResultSetImpl extends BasicNoPutResultSetImpl
{
    public final int resultSetNumber;
    private boolean needsRowLocation;
    protected ExecRow clonedExecRow;
    protected TargetResultSet targetResultSet;
    protected int[] checkNullCols;
    protected int cncLen;
    
    NoPutResultSetImpl(final Activation activation, final int resultSetNumber, final double n, final double n2) {
        super(null, activation, n, n2);
        this.resultSetNumber = resultSetNumber;
    }
    
    public ResultDescription getResultDescription() {
        return this.activation.getResultDescription();
    }
    
    public String getCursorName() {
        String s = this.activation.getCursorName();
        if (s == null && this.isForUpdate()) {
            this.activation.setCursorName(this.activation.getLanguageConnectionContext().getUniqueCursorName());
            s = this.activation.getCursorName();
        }
        return s;
    }
    
    public int resultSetNumber() {
        return this.resultSetNumber;
    }
    
    public void close() throws StandardException {
        if (!this.isOpen) {
            return;
        }
        if (this.isTopResultSet) {
            final LanguageConnectionContext languageConnectionContext = this.getLanguageConnectionContext();
            if (languageConnectionContext.getRunTimeStatisticsMode() && !languageConnectionContext.getStatementContext().getStatementWasInvalidated()) {
                this.endExecutionTime = this.getCurrentTimeMillis();
                final ExecutionFactory executionFactory = languageConnectionContext.getLanguageConnectionFactory().getExecutionFactory();
                final RunTimeStatistics runTimeStatistics = executionFactory.getResultSetStatisticsFactory().getRunTimeStatistics(this.activation, this, this.subqueryTrackingArray);
                languageConnectionContext.setRunTimeStatisticsObject(runTimeStatistics);
                executionFactory.getXPLAINFactory().getXPLAINVisitor().doXPLAIN(runTimeStatistics, this.activation);
            }
            for (int n = (this.subqueryTrackingArray == null) ? 0 : this.subqueryTrackingArray.length, i = 0; i < n; ++i) {
                if (this.subqueryTrackingArray[i] != null) {
                    if (!this.subqueryTrackingArray[i].isClosed()) {
                        this.subqueryTrackingArray[i].close();
                    }
                }
            }
        }
        this.isOpen = false;
    }
    
    public void setTargetResultSet(final TargetResultSet targetResultSet) {
        this.targetResultSet = targetResultSet;
    }
    
    public void setNeedsRowLocation(final boolean needsRowLocation) {
        this.needsRowLocation = needsRowLocation;
    }
    
    public FormatableBitSet getValidColumns() {
        return null;
    }
    
    public DataValueDescriptor[] getNextRowFromRowSource() throws StandardException {
        final ExecRow nextRowCore = this.getNextRowCore();
        if (nextRowCore != null) {
            this.clonedExecRow = this.targetResultSet.preprocessSourceRow(nextRowCore);
            return nextRowCore.getRowArray();
        }
        return null;
    }
    
    public boolean needsToClone() {
        return true;
    }
    
    public void closeRowSource() {
    }
    
    public boolean needsRowLocation() {
        return this.needsRowLocation;
    }
    
    public void rowLocation(final RowLocation rowLocation) throws StandardException {
        this.targetResultSet.changedRow(this.clonedExecRow, rowLocation);
    }
    
    protected void clearOrderableCache(final Qualifier[][] array) throws StandardException {
        if (array != null) {
            for (int i = 0; i < array.length; ++i) {
                for (int j = 0; j < array[i].length; ++j) {
                    final Qualifier qualifier = array[i][j];
                    qualifier.clearOrderableCache();
                    if (((GenericQualifier)qualifier).variantType != 0) {
                        qualifier.getOrderable();
                    }
                }
            }
        }
    }
    
    public final void setCurrentRow(final ExecRow currentRow) {
        this.activation.setCurrentRow(currentRow, this.resultSetNumber);
        this.currentRow = currentRow;
    }
    
    public void clearCurrentRow() {
        this.currentRow = null;
        this.activation.clearCurrentRow(this.resultSetNumber);
    }
    
    public boolean isForUpdate() {
        return false;
    }
    
    protected boolean skipScan(final ExecIndexRow execIndexRow, final ExecIndexRow execIndexRow2) throws StandardException {
        final int n = (execIndexRow == null) ? 0 : execIndexRow.nColumns();
        final int n2 = (execIndexRow2 == null) ? 0 : execIndexRow2.nColumns();
        boolean b = false;
        int n3 = n2;
        if (n > n2) {
            b = true;
            n3 = n;
        }
        if (n3 == 0) {
            return false;
        }
        if (this.checkNullCols == null || this.checkNullCols.length < n3) {
            this.checkNullCols = new int[n3];
        }
        this.cncLen = 0;
        boolean b2 = false;
        for (int i = 0; i < n; ++i) {
            if (!execIndexRow.areNullsOrdered(i)) {
                if (b) {
                    this.checkNullCols[this.cncLen++] = i + 1;
                }
                if (execIndexRow.getColumn(i + 1).isNull()) {
                    b2 = true;
                    if (!b) {
                        break;
                    }
                }
            }
        }
        if (b && b2) {
            return true;
        }
        for (int j = 0; j < n2; ++j) {
            if (!execIndexRow2.areNullsOrdered(j)) {
                if (!b) {
                    this.checkNullCols[this.cncLen++] = j + 1;
                }
                if (!b2) {
                    if (execIndexRow2.getColumn(j + 1).isNull()) {
                        b2 = true;
                        if (b) {
                            break;
                        }
                    }
                }
            }
        }
        return b2;
    }
    
    protected boolean skipRow(final ExecRow execRow) throws StandardException {
        for (int i = 0; i < this.cncLen; ++i) {
            if (execRow.getColumn(this.checkNullCols[i]).isNull()) {
                return true;
            }
        }
        return false;
    }
    
    public static String printQualifiers(final Qualifier[][] array) {
        final String s = "";
        String string = "";
        if (array == null) {
            return s + MessageService.getTextMessage("42Z37.U");
        }
        for (int i = 0; i < array.length; ++i) {
            for (int j = 0; j < array[i].length; ++j) {
                final Qualifier qualifier = array[i][j];
                final String string2 = s + string + MessageService.getTextMessage("42Z48.U", String.valueOf(i), String.valueOf(j)) + ": " + qualifier.getColumnId() + "\n";
                final int operator = qualifier.getOperator();
                String string3 = null;
                switch (operator) {
                    case 2: {
                        string3 = "=";
                        break;
                    }
                    case 3: {
                        string3 = "<=";
                        break;
                    }
                    case 1: {
                        string3 = "<";
                        break;
                    }
                    default: {
                        string3 = "unknown value (" + operator + ")";
                        break;
                    }
                }
                string = string2 + s + MessageService.getTextMessage("42Z43.U") + ": " + string3 + "\n" + s + MessageService.getTextMessage("42Z44.U") + ": " + qualifier.getOrderedNulls() + "\n" + s + MessageService.getTextMessage("42Z45.U") + ": " + qualifier.getUnknownRV() + "\n" + s + MessageService.getTextMessage("42Z46.U") + ": " + qualifier.negateCompareResult() + "\n";
            }
        }
        return string;
    }
    
    public void updateRow(final ExecRow execRow, final RowChanger rowChanger) throws StandardException {
    }
    
    public void markRowAsDeleted() throws StandardException {
    }
    
    public void positionScanAtRowLocation(final RowLocation rowLocation) throws StandardException {
    }
}
