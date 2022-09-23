// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.execute.RowChanger;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.catalog.types.ReferencedColumnsDescriptorImpl;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.services.loader.GeneratedMethod;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import org.apache.derby.iapi.sql.execute.CursorResultSet;

class ProjectRestrictResultSet extends NoPutResultSetImpl implements CursorResultSet
{
    public long restrictionTime;
    public long projectionTime;
    final NoPutResultSet source;
    public GeneratedMethod constantRestriction;
    public GeneratedMethod restriction;
    public boolean doesProjection;
    private GeneratedMethod projection;
    private int[] projectMapping;
    private boolean[] cloneMap;
    private boolean runTimeStatsOn;
    private ExecRow mappedResultRow;
    public boolean reuseResult;
    private boolean shortCircuitOpen;
    private ExecRow projRow;
    
    ProjectRestrictResultSet(final NoPutResultSet source, final Activation activation, final GeneratedMethod restriction, final GeneratedMethod projection, final int n, final GeneratedMethod constantRestriction, final int n2, final int n3, final boolean reuseResult, final boolean doesProjection, final double n4, final double n5) throws StandardException {
        super(activation, n, n4, n5);
        this.source = source;
        this.restriction = restriction;
        this.projection = projection;
        this.constantRestriction = constantRestriction;
        this.projectMapping = ((ReferencedColumnsDescriptorImpl)activation.getPreparedStatement().getSavedObject(n2)).getReferencedColumnPositions();
        this.reuseResult = reuseResult;
        this.doesProjection = doesProjection;
        if (this.projection == null) {
            this.mappedResultRow = this.activation.getExecutionFactory().getValueRow(this.projectMapping.length);
        }
        this.cloneMap = (boolean[])activation.getPreparedStatement().getSavedObject(n3);
        this.runTimeStatsOn = this.getLanguageConnectionContext().getRunTimeStatisticsMode();
        this.recordConstructorTime();
    }
    
    public void openCore() throws StandardException {
        int n = 1;
        this.beginTime = this.getCurrentTimeMillis();
        if (this.constantRestriction != null) {
            final DataValueDescriptor dataValueDescriptor = (DataValueDescriptor)this.constantRestriction.invoke(this.activation);
            n = ((dataValueDescriptor == null || (!dataValueDescriptor.isNull() && dataValueDescriptor.getBoolean())) ? 1 : 0);
        }
        if (n != 0) {
            this.source.openCore();
        }
        else {
            this.shortCircuitOpen = true;
        }
        this.isOpen = true;
        ++this.numOpens;
        this.openTime += this.getElapsedMillis(this.beginTime);
    }
    
    public void reopenCore() throws StandardException {
        int n = 1;
        this.beginTime = this.getCurrentTimeMillis();
        if (this.constantRestriction != null) {
            final DataValueDescriptor dataValueDescriptor = (DataValueDescriptor)this.constantRestriction.invoke(this.activation);
            n = ((dataValueDescriptor == null || (!dataValueDescriptor.isNull() && dataValueDescriptor.getBoolean())) ? 1 : 0);
        }
        if (n != 0) {
            this.source.reopenCore();
        }
        else {
            this.shortCircuitOpen = true;
        }
        this.isOpen = true;
        ++this.numOpens;
        this.openTime += this.getElapsedMillis(this.beginTime);
    }
    
    public ExecRow getNextRowCore() throws StandardException {
        if (this.isXplainOnlyMode()) {
            return null;
        }
        ExecRow doProjection = null;
        int n = 0;
        if (this.shortCircuitOpen) {
            return doProjection;
        }
        this.beginTime = this.getCurrentTimeMillis();
        ExecRow nextRowCore;
        do {
            nextRowCore = this.source.getNextRowCore();
            if (nextRowCore != null) {
                final long currentTimeMillis = this.getCurrentTimeMillis();
                if (this.restriction == null) {
                    n = 1;
                }
                else {
                    this.setCurrentRow(nextRowCore);
                    final DataValueDescriptor dataValueDescriptor = (DataValueDescriptor)this.restriction.invoke(this.activation);
                    this.restrictionTime += this.getElapsedMillis(currentTimeMillis);
                    n = ((!dataValueDescriptor.isNull() && dataValueDescriptor.getBoolean()) ? 1 : 0);
                    if (n == 0) {
                        ++this.rowsFiltered;
                    }
                }
                ++this.rowsSeen;
            }
        } while (nextRowCore != null && n == 0);
        if (nextRowCore != null) {
            final long currentTimeMillis2 = this.getCurrentTimeMillis();
            doProjection = this.doProjection(nextRowCore);
            this.projectionTime += this.getElapsedMillis(currentTimeMillis2);
        }
        else {
            this.clearCurrentRow();
        }
        this.currentRow = doProjection;
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
        if (this.shortCircuitOpen) {
            this.shortCircuitOpen = false;
            this.source.close();
            super.close();
            return;
        }
        this.beginTime = this.getCurrentTimeMillis();
        if (this.isOpen) {
            this.clearCurrentRow();
            this.source.close();
            super.close();
        }
        this.closeTime += this.getElapsedMillis(this.beginTime);
    }
    
    public void finish() throws StandardException {
        this.source.finish();
        this.finishAndRTS();
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
            final DataValueDescriptor dataValueDescriptor = (DataValueDescriptor)((this.restriction == null) ? null : this.restriction.invoke(this.activation));
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
            this.setCurrentRow(this.projRow);
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
                DataValueDescriptor dataValueDescriptor = execRow.getColumn(this.projectMapping[i]);
                if (this.cloneMap[i] && dataValueDescriptor.hasStream()) {
                    dataValueDescriptor = dataValueDescriptor.cloneValue(false);
                }
                mappedResultRow.setColumn(i + 1, dataValueDescriptor);
            }
        }
        this.setCurrentRow(mappedResultRow);
        if (this.reuseResult) {
            this.projRow = mappedResultRow;
        }
        return mappedResultRow;
    }
    
    public ExecRow doBaseRowProjection(final ExecRow execRow) throws StandardException {
        ExecRow execRow2;
        if (this.source instanceof ProjectRestrictResultSet) {
            execRow2 = ((ProjectRestrictResultSet)this.source).doBaseRowProjection(execRow);
        }
        else {
            execRow2 = execRow.getNewNullRow();
            execRow2.setRowArray(execRow.getRowArray());
        }
        return this.doProjection(execRow2);
    }
    
    public int[] getBaseProjectMapping() {
        int[] projectMapping;
        if (this.source instanceof ProjectRestrictResultSet) {
            projectMapping = new int[this.projectMapping.length];
            final int[] baseProjectMapping = ((ProjectRestrictResultSet)this.source).getBaseProjectMapping();
            for (int i = 0; i < this.projectMapping.length; ++i) {
                if (this.projectMapping[i] > 0) {
                    projectMapping[i] = baseProjectMapping[this.projectMapping[i] - 1];
                }
            }
        }
        else {
            projectMapping = this.projectMapping;
        }
        return projectMapping;
    }
    
    public boolean isForUpdate() {
        return this.source.isForUpdate();
    }
    
    public void updateRow(final ExecRow execRow, final RowChanger rowChanger) throws StandardException {
        this.source.updateRow(execRow, rowChanger);
    }
    
    public void markRowAsDeleted() throws StandardException {
        this.source.markRowAsDeleted();
    }
}
