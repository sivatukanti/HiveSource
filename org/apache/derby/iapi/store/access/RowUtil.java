// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.access;

import java.util.Hashtable;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.store.raw.FetchDescriptor;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.types.DataValueDescriptor;

public class RowUtil
{
    public static final DataValueDescriptor[] EMPTY_ROW;
    public static final FormatableBitSet EMPTY_ROW_BITSET;
    public static final FetchDescriptor EMPTY_ROW_FETCH_DESCRIPTOR;
    private static final FetchDescriptor[] ROWUTIL_FETCH_DESCRIPTOR_CONSTANTS;
    
    private RowUtil() {
    }
    
    public static DataValueDescriptor getColumn(final DataValueDescriptor[] array, final FormatableBitSet set, final int n) {
        if (set == null) {
            return (n < array.length) ? array[n] : null;
        }
        if (set.getLength() <= n || !set.isSet(n)) {
            return null;
        }
        return (n < array.length) ? array[n] : null;
    }
    
    public static Object getColumn(final Object[] array, final FormatableBitSet set, final int n) {
        if (set == null) {
            return (n < array.length) ? array[n] : null;
        }
        if (set.getLength() <= n || !set.isSet(n)) {
            return null;
        }
        return (n < array.length) ? array[n] : null;
    }
    
    public static FormatableBitSet getQualifierBitSet(final Qualifier[][] array) {
        final FormatableBitSet set = new FormatableBitSet();
        if (array != null) {
            for (int i = 0; i < array.length; ++i) {
                for (int j = 0; j < array[i].length; ++j) {
                    final int columnId = array[i][j].getColumnId();
                    set.grow(columnId + 1);
                    set.set(columnId);
                }
            }
        }
        return set;
    }
    
    public static int getNumberOfColumns(final int n, final FormatableBitSet set) {
        int length = set.getLength();
        if (n > 0 && n < length) {
            length = n;
        }
        int n2 = 0;
        for (int i = 0; i < length; ++i) {
            if (set.isSet(i)) {
                ++n2;
            }
        }
        return n2;
    }
    
    public static boolean isRowEmpty(final DataValueDescriptor[] array) {
        return array == null || array.length == 0;
    }
    
    public static int columnOutOfRange(final DataValueDescriptor[] array, final FormatableBitSet set, final int n) {
        if (set != null) {
            for (int length = set.getLength(), i = n; i < length; ++i) {
                if (set.isSet(i)) {
                    return i;
                }
            }
            return -1;
        }
        if (array.length > n) {
            return n;
        }
        return -1;
    }
    
    public static int nextColumn(final Object[] array, final FormatableBitSet set, int i) {
        if (set != null) {
            while (i < set.getLength()) {
                if (set.isSet(i)) {
                    return i;
                }
                ++i;
            }
            return -1;
        }
        if (array == null) {
            return -1;
        }
        return (i < array.length) ? i : -1;
    }
    
    public static final FetchDescriptor getFetchDescriptorConstant(final int n) {
        if (n < RowUtil.ROWUTIL_FETCH_DESCRIPTOR_CONSTANTS.length) {
            return RowUtil.ROWUTIL_FETCH_DESCRIPTOR_CONSTANTS[n];
        }
        return new FetchDescriptor(n, n);
    }
    
    public static DataValueDescriptor[] newTemplate(final DataValueFactory dataValueFactory, final FormatableBitSet set, final int[] array, final int[] array2) throws StandardException {
        final int length = array.length;
        final DataValueDescriptor[] array3 = new DataValueDescriptor[length];
        final int n = (set == null) ? 0 : set.getLength();
        for (int i = 0; i < length; ++i) {
            if (set != null) {
                if (n <= i) {
                    continue;
                }
                if (!set.isSet(i)) {
                    continue;
                }
            }
            array3[i] = dataValueFactory.getNull(array[i], array2[i]);
        }
        return array3;
    }
    
    public static DataValueDescriptor[] newRowFromTemplate(final DataValueDescriptor[] array) throws StandardException {
        final DataValueDescriptor[] array2 = new DataValueDescriptor[array.length];
        int length = array.length;
        while (length-- > 0) {
            if (array[length] != null) {
                array2[length] = array[length].getNewNull();
            }
        }
        return array2;
    }
    
    public static String toString(final Object[] array) {
        return null;
    }
    
    public static String toString(final Hashtable hashtable) {
        return null;
    }
    
    public static final boolean qualifyRow(final DataValueDescriptor[] array, final Qualifier[][] array2) throws StandardException {
        boolean b = true;
        for (int i = 0; i < array2[0].length; ++i) {
            final Qualifier qualifier = array2[0][i];
            b = array[qualifier.getColumnId()].compare(qualifier.getOperator(), qualifier.getOrderable(), qualifier.getOrderedNulls(), qualifier.getUnknownRV());
            if (qualifier.negateCompareResult()) {
                b = !b;
            }
            if (!b) {
                return false;
            }
        }
        for (int j = 1; j < array2.length; ++j) {
            b = false;
            for (int k = 0; k < array2[j].length; ++k) {
                final Qualifier qualifier2 = array2[j][k];
                qualifier2.getColumnId();
                b = array[qualifier2.getColumnId()].compare(qualifier2.getOperator(), qualifier2.getOrderable(), qualifier2.getOrderedNulls(), qualifier2.getUnknownRV());
                if (qualifier2.negateCompareResult()) {
                    b = !b;
                }
                if (b) {
                    break;
                }
            }
            if (!b) {
                break;
            }
        }
        return b;
    }
    
    static {
        EMPTY_ROW = new DataValueDescriptor[0];
        EMPTY_ROW_BITSET = new FormatableBitSet(0);
        EMPTY_ROW_FETCH_DESCRIPTOR = new FetchDescriptor(0);
        ROWUTIL_FETCH_DESCRIPTOR_CONSTANTS = new FetchDescriptor[] { RowUtil.EMPTY_ROW_FETCH_DESCRIPTOR, new FetchDescriptor(1, 1), new FetchDescriptor(2, 2), new FetchDescriptor(3, 3), new FetchDescriptor(4, 4), new FetchDescriptor(5, 5), new FetchDescriptor(6, 6), new FetchDescriptor(7, 7) };
    }
}
