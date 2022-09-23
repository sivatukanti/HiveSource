// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.execute.ExecRowBuilder;
import org.apache.derby.catalog.types.ReferencedColumnsDescriptorImpl;
import org.apache.derby.impl.sql.GenericPreparedStatement;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.iapi.store.access.StaticCompiledOpenConglomInfo;
import org.apache.derby.iapi.store.access.DynamicCompiledOpenConglomInfo;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.services.loader.GeneratedMethod;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import org.apache.derby.iapi.sql.execute.CursorResultSet;

class IndexRowToBaseRowResultSet extends NoPutResultSetImpl implements CursorResultSet
{
    public NoPutResultSet source;
    private GeneratedMethod restriction;
    public FormatableBitSet accessedHeapCols;
    private FormatableBitSet accessedAllCols;
    public String indexName;
    private int[] indexCols;
    private DynamicCompiledOpenConglomInfo dcoci;
    private StaticCompiledOpenConglomInfo scoci;
    private ConglomerateController baseCC;
    private boolean closeBaseCCHere;
    private boolean forUpdate;
    private DataValueDescriptor[] rowArray;
    RowLocation baseRowLocation;
    boolean copiedFromSource;
    public long restrictionTime;
    
    IndexRowToBaseRowResultSet(final long n, final int n2, final Activation activation, final NoPutResultSet source, final int n3, final int n4, final String indexName, final int n5, final int n6, final int n7, final int n8, final GeneratedMethod restriction, final boolean forUpdate, final double n9, final double n10) throws StandardException {
        super(activation, n4, n9, n10);
        final Object[] savedObjects = ((GenericPreparedStatement)activation.getPreparedStatement()).getSavedObjects();
        this.scoci = (StaticCompiledOpenConglomInfo)savedObjects[n2];
        this.dcoci = this.activation.getTransactionController().getDynamicCompiledConglomInfo(n);
        this.source = source;
        this.indexName = indexName;
        this.forUpdate = forUpdate;
        this.restriction = restriction;
        if (n5 != -1) {
            this.accessedHeapCols = (FormatableBitSet)savedObjects[n5];
        }
        if (n6 != -1) {
            this.accessedAllCols = (FormatableBitSet)savedObjects[n6];
        }
        this.indexCols = ((ReferencedColumnsDescriptorImpl)savedObjects[n8]).getReferencedColumnPositions();
        final ExecRow build = ((ExecRowBuilder)savedObjects[n3]).build(activation.getExecutionFactory());
        this.getCompactRow(build, this.accessedAllCols, false);
        if (this.accessedHeapCols == null) {
            this.rowArray = build.getRowArray();
        }
        else {
            final DataValueDescriptor[] rowArray = build.getRowArray();
            final FormatableBitSet set = (FormatableBitSet)savedObjects[n7];
            final int length = set.getLength();
            this.rowArray = new DataValueDescriptor[length];
            for (int min = Math.min(rowArray.length, length), i = 0; i < min; ++i) {
                if (rowArray[i] != null && set.isSet(i)) {
                    this.rowArray[i] = rowArray[i];
                }
            }
        }
        this.recordConstructorTime();
    }
    
    public void openCore() throws StandardException {
        boolean b = false;
        this.beginTime = this.getCurrentTimeMillis();
        this.source.openCore();
        if (this.source.requiresRelocking()) {
            b = true;
        }
        final TransactionController transactionController = this.activation.getTransactionController();
        int n;
        if (this.forUpdate) {
            n = 4;
        }
        else {
            n = 0;
        }
        final int scanIsolationLevel = this.source.getScanIsolationLevel();
        if (!b) {
            n |= 0x2000;
        }
        if (this.forUpdate) {
            this.baseCC = this.activation.getHeapConglomerateController();
        }
        if (this.baseCC == null) {
            this.baseCC = transactionController.openCompiledConglomerate(this.activation.getResultSetHoldability(), n, 6, scanIsolationLevel, this.scoci, this.dcoci);
            this.closeBaseCCHere = true;
        }
        this.isOpen = true;
        ++this.numOpens;
        this.openTime += this.getElapsedMillis(this.beginTime);
    }
    
    public void reopenCore() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        this.source.reopenCore();
        ++this.numOpens;
        this.openTime += this.getElapsedMillis(this.beginTime);
    }
    
    public ExecRow getNextRowCore() throws StandardException {
        if (this.isXplainOnlyMode()) {
            return null;
        }
        boolean b = false;
        final long n = 0L;
        this.beginTime = this.getCurrentTimeMillis();
        if (!this.isOpen) {
            throw StandardException.newException("XCL16.S", "next");
        }
        ExecRow nextRowCore;
        ExecRow currentRow;
        do {
            nextRowCore = this.source.getNextRowCore();
            if (nextRowCore != null) {
                this.baseRowLocation = (RowLocation)nextRowCore.getColumn(nextRowCore.nColumns());
                final boolean fetch = this.baseCC.fetch(this.baseRowLocation, this.rowArray, this.accessedHeapCols);
                if (fetch) {
                    if (!this.copiedFromSource) {
                        this.copiedFromSource = true;
                        for (int i = 0; i < this.indexCols.length; ++i) {
                            if (this.indexCols[i] != -1) {
                                this.compactRow.setColumn(i + 1, nextRowCore.getColumn(this.indexCols[i] + 1));
                            }
                        }
                    }
                    this.setCurrentRow(this.compactRow);
                    final DataValueDescriptor dataValueDescriptor = (DataValueDescriptor)((this.restriction == null) ? null : this.restriction.invoke(this.activation));
                    this.restrictionTime += this.getElapsedMillis(n);
                    b = (dataValueDescriptor == null || (!dataValueDescriptor.isNull() && dataValueDescriptor.getBoolean()));
                }
                if (!b || !fetch) {
                    ++this.rowsFiltered;
                    this.clearCurrentRow();
                    this.baseRowLocation = null;
                }
                else {
                    this.currentRow = this.compactRow;
                }
                ++this.rowsSeen;
                currentRow = this.currentRow;
            }
            else {
                this.clearCurrentRow();
                this.baseRowLocation = null;
                currentRow = null;
            }
        } while (nextRowCore != null && !b);
        this.nextTime += this.getElapsedMillis(this.beginTime);
        return currentRow;
    }
    
    public void close() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        if (this.isOpen) {
            this.clearCurrentRow();
            if (this.closeBaseCCHere && this.baseCC != null) {
                this.baseCC.close();
            }
            this.baseCC = null;
            this.source.close();
            super.close();
        }
        this.closeTime += this.getElapsedMillis(this.beginTime);
    }
    
    public long getTimeSpent(final int n) {
        final long n2 = this.constructorTime + this.openTime + this.nextTime + this.closeTime;
        if (n == 0) {
            return n2 - this.source.getTimeSpent(1);
        }
        return n2;
    }
    
    public RowLocation getRowLocation() throws StandardException {
        return this.baseRowLocation;
    }
    
    public void positionScanAtRowLocation(final RowLocation baseRowLocation) throws StandardException {
        this.baseRowLocation = baseRowLocation;
        this.source.positionScanAtRowLocation(baseRowLocation);
    }
    
    public ExecRow getCurrentRow() throws StandardException {
        if (this.currentRow == null) {
            return null;
        }
        final ExecRow valueRow = this.activation.getExecutionFactory().getValueRow(this.indexCols.length);
        valueRow.setRowArray(this.rowArray);
        if (this.baseCC.fetch(this.baseRowLocation, this.rowArray, null)) {
            this.setCurrentRow(valueRow);
        }
        else {
            this.clearCurrentRow();
        }
        return this.currentRow;
    }
    
    public boolean isForUpdate() {
        return this.source.isForUpdate();
    }
}
