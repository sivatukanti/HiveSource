// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.types.RowLocation;
import java.util.Properties;
import org.apache.derby.iapi.store.access.ColumnOrdering;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.store.access.ScanController;
import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import org.apache.derby.iapi.sql.execute.CursorResultSet;

class MaterializedResultSet extends NoPutResultSetImpl implements CursorResultSet
{
    public NoPutResultSet source;
    private ExecRow materializedRowBuffer;
    protected long materializedCID;
    public boolean materializedCreated;
    private boolean fromSource;
    protected ConglomerateController materializedCC;
    protected ScanController materializedScan;
    private TransactionController tc;
    private boolean sourceDrained;
    public long createTCTime;
    public long fetchTCTime;
    
    public MaterializedResultSet(final NoPutResultSet source, final Activation activation, final int n, final double n2, final double n3) throws StandardException {
        super(activation, n, n2, n3);
        this.fromSource = true;
        this.source = source;
        this.tc = activation.getTransactionController();
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
        while (!this.sourceDrained) {
            this.getNextRowFromSource();
        }
        this.fromSource = false;
        if (this.materializedScan != null) {
            this.materializedScan.close();
        }
        if (this.materializedCID != 0L) {
            this.materializedScan = this.tc.openScan(this.materializedCID, false, 0, 7, 5, null, null, 0, null, null, 0);
            this.isOpen = true;
        }
        ++this.numOpens;
        this.openTime += this.getElapsedMillis(this.beginTime);
    }
    
    public ExecRow getNextRowCore() throws StandardException {
        if (this.isXplainOnlyMode()) {
            return null;
        }
        this.beginTime = this.getCurrentTimeMillis();
        if (!this.isOpen) {
            throw StandardException.newException("XCL16.S", "next");
        }
        ExecRow currentRow;
        if (this.fromSource) {
            currentRow = this.getNextRowFromSource();
        }
        else {
            currentRow = this.getNextRowFromTempTable();
        }
        if (currentRow != null) {
            ++this.rowsSeen;
        }
        this.setCurrentRow(currentRow);
        this.nextTime += this.getElapsedMillis(this.beginTime);
        return currentRow;
    }
    
    private ExecRow getNextRowFromSource() throws StandardException {
        if (this.sourceDrained) {
            return null;
        }
        final ExecRow nextRowCore = this.source.getNextRowCore();
        if (nextRowCore != null) {
            final long currentTimeMillis = this.getCurrentTimeMillis();
            if (this.materializedRowBuffer == null) {
                this.materializedRowBuffer = nextRowCore.getClone();
                this.tc = this.activation.getTransactionController();
                this.materializedCID = this.tc.createConglomerate("heap", this.materializedRowBuffer.getRowArray(), null, null, null, 3);
                this.materializedCreated = true;
                this.materializedCC = this.tc.openConglomerate(this.materializedCID, false, 4, 7, 5);
            }
            this.materializedCC.insert(nextRowCore.getRowArray());
            this.createTCTime += this.getElapsedMillis(currentTimeMillis);
        }
        else {
            this.sourceDrained = true;
        }
        return nextRowCore;
    }
    
    private ExecRow getNextRowFromTempTable() throws StandardException {
        final long currentTimeMillis = this.getCurrentTimeMillis();
        if (this.materializedScan != null && this.materializedScan.fetchNext(this.materializedRowBuffer.getRowArray())) {
            this.fetchTCTime += this.getElapsedMillis(currentTimeMillis);
            return this.materializedRowBuffer;
        }
        return null;
    }
    
    public void close() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        if (this.isOpen) {
            this.currentRow = null;
            this.source.close();
            if (this.materializedScan != null) {
                this.materializedScan.close();
            }
            this.materializedScan = null;
            if (this.materializedCC != null) {
                this.materializedCC.close();
            }
            this.materializedCC = null;
            if (this.materializedCreated) {
                this.tc.dropConglomerate(this.materializedCID);
            }
            this.materializedCreated = false;
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
        return ((CursorResultSet)this.source).getRowLocation();
    }
    
    public ExecRow getCurrentRow() {
        return this.currentRow;
    }
}
