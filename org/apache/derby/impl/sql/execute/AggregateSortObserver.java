// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.UserDataValue;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.sql.execute.ExecRow;

public class AggregateSortObserver extends BasicSortObserver
{
    protected GenericAggregator[] aggsToProcess;
    protected GenericAggregator[] aggsToInitialize;
    private int firstAggregatorColumn;
    
    public AggregateSortObserver(final boolean b, final GenericAggregator[] aggsToProcess, final GenericAggregator[] aggsToInitialize, final ExecRow execRow) {
        super(b, false, execRow, true);
        this.aggsToProcess = aggsToProcess;
        this.aggsToInitialize = aggsToInitialize;
        if (aggsToInitialize.length > 0) {
            this.firstAggregatorColumn = aggsToInitialize[0].aggregatorColumnId;
        }
    }
    
    public DataValueDescriptor[] insertNonDuplicateKey(final DataValueDescriptor[] array) throws StandardException {
        final DataValueDescriptor[] insertNonDuplicateKey = super.insertNonDuplicateKey(array);
        if (this.aggsToInitialize.length > 0 && insertNonDuplicateKey[this.firstAggregatorColumn].isNull()) {
            for (int i = 0; i < this.aggsToInitialize.length; ++i) {
                final GenericAggregator genericAggregator = this.aggsToInitialize[i];
                ((UserDataValue)insertNonDuplicateKey[genericAggregator.aggregatorColumnId]).setValue(genericAggregator.getAggregatorInstance());
                genericAggregator.accumulate(insertNonDuplicateKey, insertNonDuplicateKey);
            }
        }
        return insertNonDuplicateKey;
    }
    
    public DataValueDescriptor[] insertDuplicateKey(final DataValueDescriptor[] array, final DataValueDescriptor[] array2) throws StandardException {
        if (this.aggsToProcess.length == 0) {
            return null;
        }
        for (int i = 0; i < this.aggsToProcess.length; ++i) {
            final GenericAggregator genericAggregator = this.aggsToProcess[i];
            if (array[genericAggregator.getColumnId()].isNull()) {
                genericAggregator.accumulate(array, array2);
            }
            else {
                genericAggregator.merge(array, array2);
            }
        }
        return null;
    }
}
