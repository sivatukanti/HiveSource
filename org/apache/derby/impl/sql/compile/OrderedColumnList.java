// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.util.ReuseFactory;
import java.util.HashSet;
import org.apache.derby.impl.sql.execute.IndexColumnOrder;

public abstract class OrderedColumnList extends QueryTreeNodeVector
{
    public IndexColumnOrder[] getColumnOrdering() {
        final int size = this.size();
        IndexColumnOrder[] array = new IndexColumnOrder[size];
        final HashSet<Integer> set = new HashSet<Integer>();
        int n = 0;
        for (int i = 0; i < size; ++i) {
            final OrderedColumn orderedColumn = (OrderedColumn)this.elementAt(i);
            final int n2 = orderedColumn.getColumnPosition() - 1;
            if (set.add(ReuseFactory.getInteger(n2))) {
                array[i] = new IndexColumnOrder(n2, orderedColumn.isAscending(), orderedColumn.isNullsOrderedLow());
                ++n;
            }
        }
        if (n < size) {
            final IndexColumnOrder[] array2 = new IndexColumnOrder[n];
            System.arraycopy(array, 0, array2, 0, n);
            array = array2;
        }
        return array;
    }
}
