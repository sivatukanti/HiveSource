// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.execute.ExecPreparedStatement;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.execute.ExecRowBuilder;

abstract class ScanResultSet extends NoPutResultSetImpl
{
    private final boolean tableLocked;
    private final boolean unspecifiedIsolationLevel;
    private final int suppliedLockMode;
    private boolean isolationLevelNeedsUpdate;
    int lockMode;
    int isolationLevel;
    final ExecRowBuilder resultRowBuilder;
    final ExecRow candidate;
    protected final FormatableBitSet accessedCols;
    
    ScanResultSet(final Activation activation, final int n, final int n2, final int suppliedLockMode, final boolean tableLocked, int currentIsolationLevel, final int n3, final double n4, final double n5) throws StandardException {
        super(activation, n, n4, n5);
        this.tableLocked = tableLocked;
        this.suppliedLockMode = suppliedLockMode;
        if (currentIsolationLevel == 0) {
            this.unspecifiedIsolationLevel = true;
            currentIsolationLevel = this.getLanguageConnectionContext().getCurrentIsolationLevel();
        }
        else {
            this.unspecifiedIsolationLevel = false;
        }
        this.lockMode = this.getLockMode(currentIsolationLevel);
        this.isolationLevel = this.translateLanguageIsolationLevel(currentIsolationLevel);
        final ExecPreparedStatement preparedStatement = activation.getPreparedStatement();
        this.resultRowBuilder = (ExecRowBuilder)preparedStatement.getSavedObject(n2);
        this.candidate = this.resultRowBuilder.build(activation.getExecutionFactory());
        this.accessedCols = ((n3 != -1) ? ((FormatableBitSet)preparedStatement.getSavedObject(n3)) : null);
    }
    
    void initIsolationLevel() {
        if (this.isolationLevelNeedsUpdate) {
            final int currentIsolationLevel = this.getLanguageConnectionContext().getCurrentIsolationLevel();
            this.lockMode = this.getLockMode(currentIsolationLevel);
            this.isolationLevel = this.translateLanguageIsolationLevel(currentIsolationLevel);
            this.isolationLevelNeedsUpdate = false;
        }
    }
    
    private int getLockMode(final int n) {
        if (this.tableLocked || n == 4) {
            return this.suppliedLockMode;
        }
        return 6;
    }
    
    private int translateLanguageIsolationLevel(final int n) {
        switch (n) {
            case 1: {
                return 1;
            }
            case 2: {
                if (!this.canGetInstantaneousLocks()) {
                    return 2;
                }
                return 3;
            }
            case 3: {
                return 4;
            }
            case 4: {
                return 5;
            }
            default: {
                return 0;
            }
        }
    }
    
    abstract boolean canGetInstantaneousLocks();
    
    public int getScanIsolationLevel() {
        return this.isolationLevel;
    }
    
    public void close() throws StandardException {
        this.isolationLevelNeedsUpdate = this.unspecifiedIsolationLevel;
        this.candidate.resetRowArray();
        super.close();
    }
}
