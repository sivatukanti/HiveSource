// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.sort;

import org.apache.derby.iapi.store.access.SortInfo;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataValueDescriptor;
import java.util.Vector;
import org.apache.derby.iapi.store.access.conglomerate.TransactionManager;
import org.apache.derby.iapi.store.access.SortController;

final class MergeInserter implements SortController
{
    private MergeSort sort;
    private TransactionManager tran;
    private Vector mergeRuns;
    private SortBuffer sortBuffer;
    private long beginMemoryUsage;
    private boolean avoidMergeRun;
    private int runSize;
    private int totalRunSize;
    String stat_sortType;
    int stat_numRowsInput;
    int stat_numRowsOutput;
    int stat_numMergeRuns;
    Vector stat_mergeRunsSize;
    
    public void insert(final DataValueDescriptor[] array) throws StandardException {
        this.sort.checkColumnTypes(array);
        final int insert = this.sortBuffer.insert(array);
        ++this.stat_numRowsInput;
        if (insert != 1) {
            ++this.stat_numRowsOutput;
        }
        if (insert == 2) {
            if (this.avoidMergeRun) {
                final Runtime runtime = Runtime.getRuntime();
                final long freeMemory = runtime.freeMemory();
                final long totalMemory = runtime.totalMemory();
                final long beginMemoryUsage = totalMemory - freeMemory;
                final long n = beginMemoryUsage - this.beginMemoryUsage;
                if (n < 0L) {
                    this.beginMemoryUsage = beginMemoryUsage;
                }
                if (n < 0L || 2L * n < (n + freeMemory) / 2L || (2L * n < 1048576L && totalMemory < 5242880L)) {
                    this.sortBuffer.grow(100);
                    if (this.sortBuffer.insert(array) != 2) {
                        return;
                    }
                }
                this.avoidMergeRun = false;
            }
            this.stat_sortType = "external";
            final long mergeRun = this.sort.createMergeRun(this.tran, this.sortBuffer);
            if (this.mergeRuns == null) {
                this.mergeRuns = new Vector();
            }
            this.mergeRuns.addElement(new Long(mergeRun));
            ++this.stat_numMergeRuns;
            this.runSize = this.stat_numRowsInput - this.totalRunSize - 1;
            this.totalRunSize += this.runSize;
            this.stat_mergeRunsSize.addElement(new Integer(this.runSize));
            this.sortBuffer.insert(array);
        }
    }
    
    public void completedInserts() {
        if (this.sort != null) {
            this.sort.doneInserting(this, this.sortBuffer, this.mergeRuns);
        }
        if (this.stat_sortType == "external") {
            ++this.stat_numMergeRuns;
            this.stat_mergeRunsSize.addElement(new Integer(this.stat_numRowsInput - this.totalRunSize));
        }
        this.tran.closeMe(this);
        this.sort = null;
        this.tran = null;
        this.mergeRuns = null;
        this.sortBuffer = null;
    }
    
    public SortInfo getSortInfo() throws StandardException {
        return new MergeSortInfo(this);
    }
    
    boolean initialize(final MergeSort sort, final TransactionManager tran) {
        final Runtime runtime = Runtime.getRuntime();
        this.beginMemoryUsage = runtime.totalMemory() - runtime.freeMemory();
        this.avoidMergeRun = true;
        this.stat_sortType = "internal";
        this.stat_numMergeRuns = 0;
        this.stat_numRowsInput = 0;
        this.stat_numRowsOutput = 0;
        this.stat_mergeRunsSize = new Vector();
        this.runSize = 0;
        this.totalRunSize = 0;
        this.sort = sort;
        this.tran = tran;
        this.sortBuffer = new SortBuffer(sort);
        return this.sortBuffer.init();
    }
}
