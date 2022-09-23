// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.services.loader.GeneratedMethod;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import org.apache.derby.iapi.sql.execute.CursorResultSet;

class RowCountResultSet extends NoPutResultSetImpl implements CursorResultSet
{
    final NoPutResultSet source;
    private final boolean runTimeStatsOn;
    private long offset;
    private long fetchFirst;
    private final GeneratedMethod offsetMethod;
    private final GeneratedMethod fetchFirstMethod;
    private final boolean hasJDBClimitClause;
    private boolean virginal;
    private long rowsFetched;
    
    RowCountResultSet(final NoPutResultSet source, final Activation activation, final int n, final GeneratedMethod offsetMethod, final GeneratedMethod fetchFirstMethod, final boolean hasJDBClimitClause, final double n2, final double n3) throws StandardException {
        super(activation, n, n2, n3);
        this.offsetMethod = offsetMethod;
        this.fetchFirstMethod = fetchFirstMethod;
        this.hasJDBClimitClause = hasJDBClimitClause;
        this.source = source;
        this.virginal = true;
        this.rowsFetched = 0L;
        this.runTimeStatsOn = this.getLanguageConnectionContext().getRunTimeStatisticsMode();
        this.recordConstructorTime();
    }
    
    public void openCore() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        this.source.openCore();
        this.isOpen = true;
        ++this.numOpens;
        this.openTime += this.getElapsedMillis(this.beginTime);
    }
    
    public void reopenCore() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        this.source.reopenCore();
        this.virginal = true;
        this.rowsFetched = 0L;
        this.isOpen = true;
        ++this.numOpens;
        this.openTime += this.getElapsedMillis(this.beginTime);
    }
    
    public ExecRow getNextRowCore() throws StandardException {
        if (this.isXplainOnlyMode()) {
            return null;
        }
        this.beginTime = this.getCurrentTimeMillis();
        ExecRow currentRow;
        if (this.virginal) {
            if (this.offsetMethod != null) {
                final DataValueDescriptor dataValueDescriptor = (DataValueDescriptor)this.offsetMethod.invoke(this.activation);
                if (!dataValueDescriptor.isNotNull().getBoolean()) {
                    throw StandardException.newException("2201Z", "OFFSET");
                }
                this.offset = dataValueDescriptor.getLong();
                if (this.offset < 0L) {
                    throw StandardException.newException("2201X", Long.toString(this.offset));
                }
                this.offset = dataValueDescriptor.getLong();
            }
            else {
                this.offset = 0L;
            }
            if (this.fetchFirstMethod != null) {
                final DataValueDescriptor dataValueDescriptor2 = (DataValueDescriptor)this.fetchFirstMethod.invoke(this.activation);
                if (!dataValueDescriptor2.isNotNull().getBoolean()) {
                    throw StandardException.newException("2201Z", "FETCH FIRST/NEXT");
                }
                this.fetchFirst = dataValueDescriptor2.getLong();
                if (this.hasJDBClimitClause && this.fetchFirst == 0L) {
                    this.fetchFirst = Long.MAX_VALUE;
                }
                if (this.fetchFirst < 1L) {
                    throw StandardException.newException("2201W", Long.toString(this.fetchFirst));
                }
            }
            if (this.offset > 0L) {
                this.virginal = false;
                long offset = this.offset;
                while (true) {
                    currentRow = this.source.getNextRowCore();
                    --offset;
                    if (currentRow == null || offset < 0L) {
                        break;
                    }
                    ++this.rowsFiltered;
                }
            }
            else if (this.fetchFirstMethod != null && this.rowsFetched >= this.fetchFirst) {
                currentRow = null;
            }
            else {
                currentRow = this.source.getNextRowCore();
            }
        }
        else if (this.fetchFirstMethod != null && this.rowsFetched >= this.fetchFirst) {
            currentRow = null;
        }
        else {
            currentRow = this.source.getNextRowCore();
        }
        if (currentRow != null) {
            ++this.rowsFetched;
            ++this.rowsSeen;
        }
        this.setCurrentRow(currentRow);
        if (this.runTimeStatsOn) {
            if (!this.isTopResultSet) {
                this.subqueryTrackingArray = this.activation.getLanguageConnectionContext().getStatementContext().getSubqueryTrackingArray();
            }
            this.nextTime += this.getElapsedMillis(this.beginTime);
        }
        return currentRow;
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
        }
        this.virginal = true;
        this.rowsFetched = 0L;
        this.closeTime += this.getElapsedMillis(this.beginTime);
    }
    
    public void finish() throws StandardException {
        this.source.finish();
        this.finishAndRTS();
    }
    
    public final void clearCurrentRow() {
        this.currentRow = null;
        this.activation.clearCurrentRow(this.resultSetNumber);
        this.source.clearCurrentRow();
    }
    
    public RowLocation getRowLocation() throws StandardException {
        return ((CursorResultSet)this.source).getRowLocation();
    }
    
    public ExecRow getCurrentRow() throws StandardException {
        return ((CursorResultSet)this.source).getCurrentRow();
    }
    
    public boolean isForUpdate() {
        return this.source.isForUpdate();
    }
    
    public ProjectRestrictResultSet getUnderlyingProjectRestrictRS() {
        if (this.source instanceof ProjectRestrictResultSet) {
            return (ProjectRestrictResultSet)this.source;
        }
        return null;
    }
}
