// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.iapi.store.access.StaticCompiledOpenConglomInfo;
import org.apache.derby.iapi.store.access.DynamicCompiledOpenConglomInfo;
import org.apache.derby.iapi.sql.dictionary.IndexRowGenerator;

public class IndexSetChanger
{
    IndexRowGenerator[] irgs;
    long[] indexCIDS;
    private DynamicCompiledOpenConglomInfo[] indexDCOCIs;
    private StaticCompiledOpenConglomInfo[] indexSCOCIs;
    String[] indexNames;
    ConglomerateController baseCC;
    FormatableBitSet baseRowReadMap;
    TransactionController tc;
    TemporaryRowHolderImpl rowHolder;
    IndexChanger[] indexChangers;
    private int lockMode;
    boolean[] fixOnUpdate;
    boolean isOpen;
    private static final int NO_INDEXES = 0;
    private static final int UPDATE_INDEXES = 1;
    private static final int ALL_INDEXES = 2;
    private int whatIsOpen;
    private int isolationLevel;
    private final Activation activation;
    
    public IndexSetChanger(final IndexRowGenerator[] irgs, final long[] indexCIDS, final StaticCompiledOpenConglomInfo[] indexSCOCIs, final DynamicCompiledOpenConglomInfo[] indexDCOCIs, final String[] indexNames, final ConglomerateController baseCC, final TransactionController tc, final int lockMode, final FormatableBitSet baseRowReadMap, final int isolationLevel, final Activation activation) throws StandardException {
        this.isOpen = false;
        this.whatIsOpen = 0;
        this.irgs = irgs;
        this.indexCIDS = indexCIDS;
        this.indexSCOCIs = indexSCOCIs;
        this.indexDCOCIs = indexDCOCIs;
        this.indexNames = indexNames;
        this.baseCC = baseCC;
        this.tc = tc;
        this.lockMode = lockMode;
        this.baseRowReadMap = baseRowReadMap;
        this.isolationLevel = isolationLevel;
        this.activation = activation;
        this.indexChangers = new IndexChanger[irgs.length];
    }
    
    public void open(final boolean[] fixOnUpdate) throws StandardException {
        this.fixOnUpdate = fixOnUpdate;
        this.isOpen = true;
    }
    
    public void setRowHolder(final TemporaryRowHolderImpl rowHolder) {
        this.rowHolder = rowHolder;
    }
    
    private void openIndexes(final int whatIsOpen) throws StandardException {
        if (this.whatIsOpen >= whatIsOpen) {
            return;
        }
        for (int i = 0; i < this.indexChangers.length; ++i) {
            if (whatIsOpen != 1 || this.fixOnUpdate[i]) {
                if (this.indexChangers[i] == null) {
                    (this.indexChangers[i] = new IndexChanger(this.irgs[i], this.indexCIDS[i], (this.indexSCOCIs == null) ? null : this.indexSCOCIs[i], (this.indexDCOCIs == null) ? null : this.indexDCOCIs[i], (this.indexNames == null) ? null : this.indexNames[i], this.baseCC, this.tc, this.lockMode, this.baseRowReadMap, this.isolationLevel, this.activation)).setRowHolder(this.rowHolder);
                }
                else {
                    this.indexChangers[i].setBaseCC(this.baseCC);
                }
                this.indexChangers[i].open();
            }
        }
        this.whatIsOpen = whatIsOpen;
    }
    
    public void delete(final ExecRow execRow, final RowLocation rowLocation) throws StandardException {
        this.openIndexes(2);
        for (int i = 0; i < this.indexChangers.length; ++i) {
            this.indexChangers[i].delete(execRow, rowLocation);
        }
    }
    
    public void insert(final ExecRow execRow, final RowLocation rowLocation) throws StandardException {
        this.openIndexes(2);
        for (int i = 0; i < this.indexChangers.length; ++i) {
            this.indexChangers[i].insert(execRow, rowLocation);
        }
    }
    
    public void update(final ExecRow execRow, final ExecRow execRow2, final RowLocation rowLocation) throws StandardException {
        this.openIndexes(1);
        for (int i = 0; i < this.indexChangers.length; ++i) {
            if (this.fixOnUpdate[i]) {
                this.indexChangers[i].update(execRow, execRow2, rowLocation);
            }
        }
    }
    
    public void setBaseCC(final ConglomerateController conglomerateController) {
        for (int i = 0; i < this.indexChangers.length; ++i) {
            if (this.indexChangers[i] != null) {
                this.indexChangers[i].setBaseCC(conglomerateController);
            }
        }
        this.baseCC = conglomerateController;
    }
    
    public void finish() throws StandardException {
        for (int i = 0; i < this.indexChangers.length; ++i) {
            if (this.indexChangers[i] != null) {
                this.indexChangers[i].finish();
            }
        }
    }
    
    public void close() throws StandardException {
        this.whatIsOpen = 0;
        for (int i = 0; i < this.indexChangers.length; ++i) {
            if (this.indexChangers[i] != null) {
                this.indexChangers[i].close();
            }
        }
        this.fixOnUpdate = null;
        this.isOpen = false;
        this.rowHolder = null;
    }
    
    public String toString() {
        return null;
    }
}
