// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.store.access.GroupFetchScanController;

public class RIBulkChecker
{
    private static final int EQUAL = 0;
    private static final int GREATER_THAN = 1;
    private static final int LESS_THAN = -1;
    private FKInfo fkInfo;
    private GroupFetchScanController referencedKeyScan;
    private DataValueDescriptor[][] referencedKeyRowArray;
    private GroupFetchScanController foreignKeyScan;
    private DataValueDescriptor[][] foreignKeyRowArray;
    private ConglomerateController unreferencedCC;
    private int failedCounter;
    private boolean quitOnFirstFailure;
    private int numColumns;
    private int currRefRowIndex;
    private int currFKRowIndex;
    private int lastRefRowIndex;
    private int lastFKRowIndex;
    private ExecRow firstRowToFail;
    
    public RIBulkChecker(final GroupFetchScanController referencedKeyScan, final GroupFetchScanController foreignKeyScan, final ExecRow execRow, final boolean quitOnFirstFailure, final ConglomerateController unreferencedCC, final ExecRow firstRowToFail) {
        this.referencedKeyScan = referencedKeyScan;
        this.foreignKeyScan = foreignKeyScan;
        this.quitOnFirstFailure = quitOnFirstFailure;
        this.unreferencedCC = unreferencedCC;
        this.firstRowToFail = firstRowToFail;
        (this.foreignKeyRowArray = new DataValueDescriptor[16][])[0] = execRow.getRowArrayClone();
        (this.referencedKeyRowArray = new DataValueDescriptor[16][])[0] = execRow.getRowArrayClone();
        this.failedCounter = 0;
        this.numColumns = execRow.getRowArray().length - 1;
        this.currFKRowIndex = -1;
        this.currRefRowIndex = -1;
    }
    
    public int doCheck() throws StandardException {
        DataValueDescriptor[] array = this.getNextRef();
        DataValueDescriptor[] array2;
        while ((array2 = this.getNextFK()) != null) {
            if (!this.anyNull(array2) && array == null) {
                do {
                    this.failure(array2);
                    if (this.quitOnFirstFailure) {
                        return 1;
                    }
                } while ((array2 = this.getNextFK()) != null);
                return this.failedCounter;
            }
            int greaterThan;
            while ((greaterThan = this.greaterThan(array2, array)) == 1) {
                if ((array = this.getNextRef()) == null) {
                    do {
                        this.failure(array2);
                        if (this.quitOnFirstFailure) {
                            return 1;
                        }
                    } while ((array2 = this.getNextFK()) != null);
                    return this.failedCounter;
                }
            }
            if (greaterThan == 0) {
                continue;
            }
            this.failure(array2);
            if (this.quitOnFirstFailure) {
                return 1;
            }
        }
        return this.failedCounter;
    }
    
    private DataValueDescriptor[] getNextFK() throws StandardException {
        if (this.currFKRowIndex > this.lastFKRowIndex || this.currFKRowIndex == -1) {
            final int fetchNextGroup = this.foreignKeyScan.fetchNextGroup(this.foreignKeyRowArray, null);
            if (fetchNextGroup == 0) {
                this.currFKRowIndex = -1;
                return null;
            }
            this.lastFKRowIndex = fetchNextGroup - 1;
            this.currFKRowIndex = 0;
        }
        return this.foreignKeyRowArray[this.currFKRowIndex++];
    }
    
    private DataValueDescriptor[] getNextRef() throws StandardException {
        if (this.currRefRowIndex > this.lastRefRowIndex || this.currRefRowIndex == -1) {
            final int fetchNextGroup = this.referencedKeyScan.fetchNextGroup(this.referencedKeyRowArray, null);
            if (fetchNextGroup == 0) {
                this.currRefRowIndex = -1;
                return null;
            }
            this.lastRefRowIndex = fetchNextGroup - 1;
            this.currRefRowIndex = 0;
        }
        return this.referencedKeyRowArray[this.currRefRowIndex++];
    }
    
    private void failure(final DataValueDescriptor[] rowArray) throws StandardException {
        if (this.failedCounter == 0 && this.firstRowToFail != null) {
            this.firstRowToFail.setRowArray(rowArray);
            this.firstRowToFail.setRowArray(this.firstRowToFail.getRowArrayClone());
        }
        ++this.failedCounter;
        if (this.unreferencedCC != null) {
            this.unreferencedCC.insert(rowArray);
        }
    }
    
    private boolean anyNull(final DataValueDescriptor[] array) throws StandardException {
        for (int i = 0; i < this.numColumns; ++i) {
            if (array[i].isNull()) {
                return true;
            }
        }
        return false;
    }
    
    private int greaterThan(final DataValueDescriptor[] array, final DataValueDescriptor[] array2) throws StandardException {
        if (this.anyNull(array)) {
            return 0;
        }
        for (int i = 0; i < this.numColumns; ++i) {
            final int compare = array[i].compare(array2[i]);
            if (compare == 1) {
                return 1;
            }
            if (compare == -1) {
                return -1;
            }
        }
        return 0;
    }
}
