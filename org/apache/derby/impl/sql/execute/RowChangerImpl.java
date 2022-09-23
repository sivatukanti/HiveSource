// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import java.util.Arrays;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.execute.TemporaryRowHolder;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.dictionary.IndexRowGenerator;
import org.apache.derby.iapi.store.access.StaticCompiledOpenConglomInfo;
import org.apache.derby.iapi.store.access.DynamicCompiledOpenConglomInfo;
import org.apache.derby.iapi.sql.execute.RowChanger;

class RowChangerImpl implements RowChanger
{
    boolean isOpen;
    boolean[] fixOnUpdate;
    long heapConglom;
    DynamicCompiledOpenConglomInfo heapDCOCI;
    StaticCompiledOpenConglomInfo heapSCOCI;
    long[] indexCIDS;
    DynamicCompiledOpenConglomInfo[] indexDCOCIs;
    StaticCompiledOpenConglomInfo[] indexSCOCIs;
    IndexRowGenerator[] irgs;
    private final Activation activation;
    TransactionController tc;
    FormatableBitSet changedColumnBitSet;
    FormatableBitSet baseRowReadList;
    private int[] baseRowReadMap;
    int[] changedColumnIds;
    TemporaryRowHolderImpl rowHolder;
    String[] indexNames;
    private ConglomerateController baseCC;
    private RowLocation baseRowLocation;
    private IndexSetChanger isc;
    private DataValueDescriptor[] sparseRowArray;
    private int[] partialChangedColumnIds;
    
    public RowChangerImpl(final long heapConglom, final StaticCompiledOpenConglomInfo heapSCOCI, final DynamicCompiledOpenConglomInfo heapDCOCI, final IndexRowGenerator[] irgs, final long[] indexCIDS, final StaticCompiledOpenConglomInfo[] indexSCOCIs, final DynamicCompiledOpenConglomInfo[] indexDCOCIs, final int n, final int[] array, final TransactionController tc, final FormatableBitSet baseRowReadList, final int[] baseRowReadMap, final Activation activation) throws StandardException {
        this.isOpen = false;
        this.fixOnUpdate = null;
        this.indexCIDS = null;
        this.irgs = null;
        this.heapConglom = heapConglom;
        this.heapSCOCI = heapSCOCI;
        this.heapDCOCI = heapDCOCI;
        this.irgs = irgs;
        this.indexCIDS = indexCIDS;
        this.indexSCOCIs = indexSCOCIs;
        this.indexDCOCIs = indexDCOCIs;
        this.tc = tc;
        this.baseRowReadList = baseRowReadList;
        this.baseRowReadMap = baseRowReadMap;
        this.activation = activation;
        if (array != null) {
            this.changedColumnIds = (RowUtil.inAscendingOrder(array) ? array : this.sortArray(array));
            this.sparseRowArray = new DataValueDescriptor[this.changedColumnIds[this.changedColumnIds.length - 1] + 1];
            this.changedColumnBitSet = new FormatableBitSet(n);
            for (int i = 0; i < this.changedColumnIds.length; ++i) {
                this.changedColumnBitSet.grow(this.changedColumnIds[i]);
                this.changedColumnBitSet.set(this.changedColumnIds[i] - 1);
            }
            if (baseRowReadList != null) {
                this.partialChangedColumnIds = new int[this.changedColumnIds.length];
                int n2 = 1;
                int j = 0;
                for (int k = 0; k < this.changedColumnIds.length; ++k) {
                    while (j < this.changedColumnIds[k]) {
                        if (baseRowReadList.get(j)) {
                            ++n2;
                        }
                        ++j;
                    }
                    this.partialChangedColumnIds[k] = n2;
                }
            }
        }
    }
    
    public void setRowHolder(final TemporaryRowHolder temporaryRowHolder) {
        this.rowHolder = (TemporaryRowHolderImpl)temporaryRowHolder;
    }
    
    public void setIndexNames(final String[] indexNames) {
        this.indexNames = indexNames;
    }
    
    public void open(final int n) throws StandardException {
        this.open(n, true);
    }
    
    public void open(final int n, final boolean b) throws StandardException {
        if (this.fixOnUpdate == null) {
            this.fixOnUpdate = new boolean[this.irgs.length];
            for (int i = 0; i < this.irgs.length; ++i) {
                this.fixOnUpdate[i] = true;
            }
        }
        this.openForUpdate(this.fixOnUpdate, n, b);
    }
    
    public void openForUpdate(final boolean[] array, final int n, final boolean b) throws StandardException {
        LanguageConnectionContext languageConnectionContext = null;
        if (this.activation != null) {
            languageConnectionContext = this.activation.getLanguageConnectionContext();
        }
        int currentIsolationLevel;
        if (languageConnectionContext == null) {
            currentIsolationLevel = 2;
        }
        else {
            currentIsolationLevel = languageConnectionContext.getCurrentIsolationLevel();
        }
        switch (currentIsolationLevel) {
            case 1: {
                currentIsolationLevel = 1;
                break;
            }
            case 2: {
                currentIsolationLevel = 2;
                break;
            }
            case 3: {
                currentIsolationLevel = 4;
                break;
            }
            case 4: {
                currentIsolationLevel = 5;
                break;
            }
        }
        try {
            if (this.heapSCOCI != null) {
                this.baseCC = this.tc.openCompiledConglomerate(false, 0x4 | (b ? 0 : 128), n, currentIsolationLevel, this.heapSCOCI, this.heapDCOCI);
            }
            else {
                this.baseCC = this.tc.openConglomerate(this.heapConglom, false, 0x4 | (b ? 0 : 128), n, currentIsolationLevel);
            }
        }
        catch (StandardException ex) {
            if (this.activation != null) {
                this.activation.checkStatementValidity();
            }
            throw ex;
        }
        if (this.activation != null) {
            this.activation.checkStatementValidity();
            this.activation.setHeapConglomerateController(this.baseCC);
        }
        if (this.indexCIDS.length != 0) {
            if (this.isc == null) {
                (this.isc = new IndexSetChanger(this.irgs, this.indexCIDS, this.indexSCOCIs, this.indexDCOCIs, this.indexNames, this.baseCC, this.tc, n, this.baseRowReadList, currentIsolationLevel, this.activation)).setRowHolder(this.rowHolder);
            }
            else {
                this.isc.setBaseCC(this.baseCC);
            }
            this.isc.open(array);
            if (this.baseRowLocation == null) {
                this.baseRowLocation = this.baseCC.newRowLocationTemplate();
            }
        }
        this.isOpen = true;
    }
    
    public void insertRow(final ExecRow execRow) throws StandardException {
        if (!this.baseCC.isKeyed()) {
            if (this.isc != null) {
                this.baseCC.insertAndFetchLocation(execRow.getRowArray(), this.baseRowLocation);
                this.isc.insert(execRow, this.baseRowLocation);
            }
            else {
                this.baseCC.insert(execRow.getRowArray());
            }
        }
    }
    
    public void deleteRow(final ExecRow execRow, final RowLocation rowLocation) throws StandardException {
        if (this.isc != null) {
            this.isc.delete(execRow, rowLocation);
        }
        this.baseCC.delete(rowLocation);
    }
    
    public void updateRow(final ExecRow execRow, final ExecRow execRow2, final RowLocation rowLocation) throws StandardException {
        if (this.isc != null) {
            this.isc.update(execRow, execRow2, rowLocation);
        }
        if (this.changedColumnBitSet != null) {
            final DataValueDescriptor[] rowArray = execRow2.getRowArray();
            final int[] array = (this.partialChangedColumnIds == null) ? this.changedColumnIds : this.partialChangedColumnIds;
            int anySetBit = -1;
            for (int i = 0; i < array.length; ++i) {
                final int n = array[i] - 1;
                anySetBit = this.changedColumnBitSet.anySetBit(anySetBit);
                this.sparseRowArray[anySetBit] = rowArray[n];
            }
        }
        else {
            this.sparseRowArray = execRow2.getRowArray();
        }
        this.baseCC.replace(rowLocation, this.sparseRowArray, this.changedColumnBitSet);
    }
    
    public void finish() throws StandardException {
        if (this.isc != null) {
            this.isc.finish();
        }
    }
    
    public void close() throws StandardException {
        if (this.isc != null) {
            this.isc.close();
        }
        if (this.baseCC != null) {
            if (this.activation == null || this.activation.getForUpdateIndexScan() == null) {
                this.baseCC.close();
            }
            this.baseCC = null;
        }
        this.isOpen = false;
        if (this.activation != null) {
            this.activation.clearHeapConglomerateController();
        }
    }
    
    public ConglomerateController getHeapConglomerateController() {
        return this.baseCC;
    }
    
    private int[] sortArray(final int[] array) {
        final int[] a = new int[array.length];
        System.arraycopy(array, 0, a, 0, array.length);
        Arrays.sort(a);
        return a;
    }
    
    public int findSelectedCol(final int n) {
        if (n == -1) {
            return -1;
        }
        final int[] array = (this.partialChangedColumnIds == null) ? this.changedColumnIds : this.partialChangedColumnIds;
        int anySetBit = -1;
        for (int i = 0; i < array.length; ++i) {
            anySetBit = this.changedColumnBitSet.anySetBit(anySetBit);
            if (n == anySetBit + 1) {
                return array[i];
            }
        }
        return -1;
    }
    
    public String toString() {
        return super.toString();
    }
}
