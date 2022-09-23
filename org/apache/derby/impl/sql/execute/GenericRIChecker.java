// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import java.util.Enumeration;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.store.access.ScanController;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.error.StandardException;
import java.util.Hashtable;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.store.access.StaticCompiledOpenConglomInfo;
import org.apache.derby.iapi.store.access.DynamicCompiledOpenConglomInfo;

public abstract class GenericRIChecker
{
    protected FKInfo fkInfo;
    protected DynamicCompiledOpenConglomInfo[] fkDcocis;
    protected StaticCompiledOpenConglomInfo[] fkScocis;
    protected DynamicCompiledOpenConglomInfo refDcoci;
    protected StaticCompiledOpenConglomInfo refScoci;
    protected TransactionController tc;
    private Hashtable scanControllers;
    private int numColumns;
    private IndexRow indexQualifierRow;
    
    GenericRIChecker(final TransactionController tc, final FKInfo fkInfo) throws StandardException {
        this.fkInfo = fkInfo;
        this.tc = tc;
        this.scanControllers = new Hashtable();
        this.numColumns = this.fkInfo.colArray.length;
        this.indexQualifierRow = new IndexRow(this.numColumns);
        this.fkDcocis = new DynamicCompiledOpenConglomInfo[this.fkInfo.fkConglomNumbers.length];
        this.fkScocis = new StaticCompiledOpenConglomInfo[this.fkInfo.fkConglomNumbers.length];
        for (int i = 0; i < this.fkInfo.fkConglomNumbers.length; ++i) {
            this.fkDcocis[i] = tc.getDynamicCompiledConglomInfo(this.fkInfo.fkConglomNumbers[i]);
            this.fkScocis[i] = tc.getStaticCompiledConglomInfo(this.fkInfo.fkConglomNumbers[i]);
        }
        this.refDcoci = tc.getDynamicCompiledConglomInfo(this.fkInfo.refConglomNumber);
        this.refScoci = tc.getStaticCompiledConglomInfo(this.fkInfo.refConglomNumber);
    }
    
    abstract void doCheck(final ExecRow p0, final boolean p1) throws StandardException;
    
    public void doCheck(final ExecRow execRow) throws StandardException {
        this.doCheck(execRow, false);
    }
    
    protected ScanController getScanController(final long value, final StaticCompiledOpenConglomInfo staticCompiledOpenConglomInfo, final DynamicCompiledOpenConglomInfo dynamicCompiledOpenConglomInfo, final ExecRow execRow) throws StandardException {
        final int riCheckIsolationLevel = this.getRICheckIsolationLevel();
        final Long n = new Long(value);
        ScanController openCompiledScan;
        if ((openCompiledScan = this.scanControllers.get(n)) == null) {
            this.setupQualifierRow(execRow);
            openCompiledScan = this.tc.openCompiledScan(false, 0, 6, riCheckIsolationLevel, null, this.indexQualifierRow.getRowArray(), 1, null, this.indexQualifierRow.getRowArray(), -1, staticCompiledOpenConglomInfo, dynamicCompiledOpenConglomInfo);
            this.scanControllers.put(n, openCompiledScan);
        }
        else {
            this.setupQualifierRow(execRow);
            openCompiledScan.reopenScan(this.indexQualifierRow.getRowArray(), 1, null, this.indexQualifierRow.getRowArray(), -1);
        }
        return openCompiledScan;
    }
    
    private void setupQualifierRow(final ExecRow execRow) {
        final DataValueDescriptor[] rowArray = this.indexQualifierRow.getRowArray();
        final DataValueDescriptor[] rowArray2 = execRow.getRowArray();
        for (int i = 0; i < this.numColumns; ++i) {
            rowArray[i] = rowArray2[this.fkInfo.colArray[i] - 1];
        }
    }
    
    boolean isAnyFieldNull(final ExecRow execRow) {
        final DataValueDescriptor[] rowArray = execRow.getRowArray();
        for (int i = 0; i < this.numColumns; ++i) {
            if (rowArray[this.fkInfo.colArray[i] - 1].isNull()) {
                return true;
            }
        }
        return false;
    }
    
    void close() throws StandardException {
        final Enumeration<ScanController> elements = this.scanControllers.elements();
        while (elements.hasMoreElements()) {
            elements.nextElement().close();
        }
        this.scanControllers.clear();
    }
    
    int getRICheckIsolationLevel() {
        return 3;
    }
}
