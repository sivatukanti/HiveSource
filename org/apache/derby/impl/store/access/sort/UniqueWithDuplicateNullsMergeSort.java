// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.sort;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataValueDescriptor;

final class UniqueWithDuplicateNullsMergeSort extends MergeSort
{
    protected int compare(final DataValueDescriptor[] array, final DataValueDescriptor[] array2) throws StandardException {
        final int length = this.columnOrdering.length;
        int n = 1;
        int i = 0;
        while (i < length) {
            if (i == length - 1 && n != 0) {
                return 0;
            }
            final int n2 = this.columnOrderingMap[i];
            final int compare;
            if ((compare = array[n2].compare(array2[n2], this.columnOrderingNullsLowMap[i])) != 0) {
                if (this.columnOrderingAscendingMap[i]) {
                    return compare;
                }
                return -compare;
            }
            else {
                if (array[n2].isNull()) {
                    n = 0;
                }
                ++i;
            }
        }
        return 0;
    }
}
