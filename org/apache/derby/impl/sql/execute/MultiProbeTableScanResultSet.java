// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.execute.ExecIndexRow;
import java.util.Collections;
import java.util.Arrays;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.services.loader.GeneratedMethod;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.store.access.StaticCompiledOpenConglomInfo;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.sql.execute.CursorResultSet;

class MultiProbeTableScanResultSet extends TableScanResultSet implements CursorResultSet
{
    protected DataValueDescriptor[] probeValues;
    protected DataValueDescriptor[] origProbeValues;
    protected int probeValIndex;
    private int sortRequired;
    private boolean skipNextScan;
    
    MultiProbeTableScanResultSet(final long n, final StaticCompiledOpenConglomInfo staticCompiledOpenConglomInfo, final Activation activation, final int n2, final int n3, final GeneratedMethod generatedMethod, final int n4, final GeneratedMethod generatedMethod2, final int n5, final boolean b, final Qualifier[][] array, final DataValueDescriptor[] origProbeValues, final int sortRequired, final String s, final String s2, final String s3, final boolean b2, final boolean b3, final int n6, final int n7, final int n8, final boolean b4, final int n9, final boolean b5, final double n10, final double n11) throws StandardException {
        super(n, staticCompiledOpenConglomInfo, activation, n2, n3, generatedMethod, n4, generatedMethod2, n5, b, array, s, s2, s3, b2, b3, n6, n7, n8, b4, n9, 1, b5, n10, n11);
        this.origProbeValues = origProbeValues;
        this.sortRequired = sortRequired;
    }
    
    public void openCore() throws StandardException {
        if (this.sortRequired == 3) {
            this.probeValues = this.origProbeValues;
        }
        else {
            final DataValueDescriptor[] probeValues = new DataValueDescriptor[this.origProbeValues.length];
            for (int i = 0; i < probeValues.length; ++i) {
                probeValues[i] = this.origProbeValues[i].cloneValue(false);
            }
            if (this.sortRequired == 1) {
                Arrays.sort(probeValues);
            }
            else {
                Arrays.sort(probeValues, Collections.reverseOrder());
            }
            this.probeValues = probeValues;
        }
        this.probeValIndex = 0;
        super.openCore();
    }
    
    public void reopenCore() throws StandardException {
        this.reopenCore(false);
    }
    
    private void reopenCore(final boolean b) throws StandardException {
        if (!b) {
            this.probeValIndex = 0;
        }
        super.reopenCore();
    }
    
    protected void reopenScanController() throws StandardException {
        final long rowsThisScan = this.rowsThisScan;
        super.reopenScanController();
        this.rowsThisScan = rowsThisScan;
    }
    
    void initStartAndStopKey() throws StandardException {
        super.initStartAndStopKey();
        if (this.probeValIndex == 0) {
            this.rowsThisScan = 0L;
        }
        final DataValueDescriptor[] rowArray = this.startPosition.getRowArray();
        final DataValueDescriptor[] rowArray2 = this.stopPosition.getRowArray();
        final DataValueDescriptor nextProbeValue = this.getNextProbeValue();
        if (nextProbeValue != null) {
            rowArray[0] = nextProbeValue;
            if (!this.sameStartStopPosition) {
                rowArray2[0] = rowArray[0];
            }
        }
        this.skipNextScan = (nextProbeValue == null);
    }
    
    protected boolean skipScan(final ExecIndexRow execIndexRow, final ExecIndexRow execIndexRow2) throws StandardException {
        return this.skipNextScan || super.skipScan(execIndexRow, execIndexRow2);
    }
    
    public ExecRow getNextRowCore() throws StandardException {
        if (this.isXplainOnlyMode()) {
            return null;
        }
        this.checkCancellationFlag();
        ExecRow execRow;
        for (execRow = super.getNextRowCore(); execRow == null && this.moreInListVals(); execRow = super.getNextRowCore()) {
            this.reopenCore(true);
        }
        return execRow;
    }
    
    public void close() throws StandardException {
        super.close();
    }
    
    private boolean moreInListVals() {
        return this.probeValIndex < this.probeValues.length;
    }
    
    private DataValueDescriptor getNextProbeValue() {
        int probeValIndex;
        for (probeValIndex = this.probeValIndex; probeValIndex > 0 && probeValIndex < this.probeValues.length && this.probeValues[this.probeValIndex - 1].equals(this.probeValues[probeValIndex]); ++probeValIndex) {}
        this.probeValIndex = probeValIndex;
        if (this.probeValIndex < this.probeValues.length) {
            return this.probeValues[this.probeValIndex++];
        }
        return null;
    }
}
