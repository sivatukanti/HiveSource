// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.conglomerate;

import org.apache.derby.iapi.types.SQLLongint;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.store.raw.Transaction;

public final class TemplateRow
{
    private TemplateRow() {
    }
    
    private static DataValueDescriptor[] allocate_objects(final Transaction transaction, final int n, final FormatableBitSet set, final int[] array, final int[] array2) throws StandardException {
        final DataValueDescriptor[] array3 = new DataValueDescriptor[n];
        final int n2 = (set == null) ? array.length : set.size();
        final DataValueFactory dataValueFactory = transaction.getDataValueFactory();
        for (int i = 0; i < n2; ++i) {
            if (set == null || set.get(i)) {
                array3[i] = dataValueFactory.getNull(array[i], array2[i]);
            }
        }
        return array3;
    }
    
    public static DataValueDescriptor[] newU8Row(final int n) {
        final DataValueDescriptor[] array = new DataValueDescriptor[n];
        for (int i = 0; i < array.length; ++i) {
            array[i] = new SQLLongint(Long.MIN_VALUE);
        }
        return array;
    }
    
    public static DataValueDescriptor[] newRow(final DataValueDescriptor[] array) throws StandardException {
        final DataValueDescriptor[] array2 = new DataValueDescriptor[array.length];
        int length = array.length;
        while (length-- > 0) {
            array2[length] = array[length].getNewNull();
        }
        return array2;
    }
    
    public static DataValueDescriptor[] newRow(final Transaction transaction, final FormatableBitSet set, final int[] array, final int[] array2) throws StandardException {
        return allocate_objects(transaction, array.length, set, array, array2);
    }
    
    public static DataValueDescriptor[] newBranchRow(final Transaction transaction, final int[] array, final int[] array2, final DataValueDescriptor dataValueDescriptor) throws StandardException {
        final DataValueDescriptor[] allocate_objects = allocate_objects(transaction, array.length + 1, null, array, array2);
        allocate_objects[array.length] = dataValueDescriptor;
        return allocate_objects;
    }
    
    public static boolean checkColumnTypes(final DataValueFactory dataValueFactory, final int[] array, final int[] array2, final DataValueDescriptor[] array3) throws StandardException {
        boolean b = true;
        final int length = array3.length;
        if (array.length != array3.length) {
            b = false;
        }
        return b;
    }
}
