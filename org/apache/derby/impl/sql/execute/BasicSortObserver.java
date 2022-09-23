// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataValueDescriptor;
import java.util.Vector;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.store.access.SortObserver;

public class BasicSortObserver implements SortObserver
{
    protected boolean doClone;
    protected boolean distinct;
    private boolean reuseWrappers;
    private ExecRow execRow;
    private Vector vector;
    
    public BasicSortObserver(final boolean doClone, final boolean distinct, final ExecRow execRow, final boolean reuseWrappers) {
        this.doClone = doClone;
        this.distinct = distinct;
        this.execRow = execRow;
        this.reuseWrappers = reuseWrappers;
        this.vector = new Vector();
    }
    
    public DataValueDescriptor[] insertNonDuplicateKey(final DataValueDescriptor[] array) throws StandardException {
        return this.doClone ? this.getClone(array) : array;
    }
    
    public DataValueDescriptor[] insertDuplicateKey(final DataValueDescriptor[] array, final DataValueDescriptor[] array2) throws StandardException {
        return (DataValueDescriptor[])(this.distinct ? null : (this.doClone ? this.getClone(array) : array));
    }
    
    public void addToFreeList(final DataValueDescriptor[] obj, final int n) {
        if (this.reuseWrappers && this.vector.size() < n) {
            this.vector.addElement(obj);
        }
    }
    
    public DataValueDescriptor[] getArrayClone() throws StandardException {
        final int size = this.vector.size();
        if (size > 0) {
            final DataValueDescriptor[] array = this.vector.elementAt(size - 1);
            this.vector.removeElementAt(size - 1);
            return array;
        }
        return this.execRow.getRowArrayClone();
    }
    
    private DataValueDescriptor[] getClone(final DataValueDescriptor[] array) {
        final DataValueDescriptor[] array2 = new DataValueDescriptor[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = array[i].cloneValue(true);
        }
        return array2;
    }
}
