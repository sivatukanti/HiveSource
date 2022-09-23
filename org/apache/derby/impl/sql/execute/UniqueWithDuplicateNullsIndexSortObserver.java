// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.sql.execute.ExecRow;

public class UniqueWithDuplicateNullsIndexSortObserver extends BasicSortObserver
{
    private boolean isConstraint;
    private String indexOrConstraintName;
    private String tableName;
    
    public UniqueWithDuplicateNullsIndexSortObserver(final boolean b, final boolean isConstraint, final String indexOrConstraintName, final ExecRow execRow, final boolean b2, final String tableName) {
        super(b, false, execRow, b2);
        this.isConstraint = isConstraint;
        this.indexOrConstraintName = indexOrConstraintName;
        this.tableName = tableName;
    }
    
    public DataValueDescriptor[] insertDuplicateKey(final DataValueDescriptor[] array, final DataValueDescriptor[] array2) throws StandardException {
        for (int i = 0; i < array.length; ++i) {
            if (array[i].isNull()) {
                return super.insertDuplicateKey(array, array2);
            }
        }
        throw StandardException.newException("23505", this.indexOrConstraintName, this.tableName);
    }
}
