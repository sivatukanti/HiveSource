// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.services.io.Storable;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.UserDataValue;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.execute.ExecAggregator;
import org.apache.derby.iapi.services.loader.ClassFactory;

class GenericAggregator
{
    private final AggregatorInfo aggInfo;
    int aggregatorColumnId;
    private int inputColumnId;
    private int resultColumnId;
    private final ClassFactory cf;
    private ExecAggregator cachedAggregator;
    
    GenericAggregator(final AggregatorInfo aggInfo, final ClassFactory cf) {
        this.aggInfo = aggInfo;
        this.aggregatorColumnId = aggInfo.getAggregatorColNum();
        this.inputColumnId = aggInfo.getInputColNum();
        this.resultColumnId = aggInfo.getOutputColNum();
        this.cf = cf;
    }
    
    void initialize(final ExecRow execRow) throws StandardException {
        final UserDataValue userDataValue = (UserDataValue)execRow.getColumn(this.aggregatorColumnId + 1);
        if (userDataValue.getObject() == null) {
            userDataValue.setValue(this.getAggregatorInstance());
        }
    }
    
    void accumulate(final ExecRow execRow, final ExecRow execRow2) throws StandardException {
        this.accumulate(execRow.getColumn(this.inputColumnId + 1), execRow2.getColumn(this.aggregatorColumnId + 1));
    }
    
    void accumulate(final Object[] array, final Object[] array2) throws StandardException {
        this.accumulate((DataValueDescriptor)array[this.inputColumnId], (DataValueDescriptor)array2[this.aggregatorColumnId]);
    }
    
    void accumulate(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        ExecAggregator aggregatorInstance = (ExecAggregator)dataValueDescriptor2.getObject();
        if (aggregatorInstance == null) {
            aggregatorInstance = this.getAggregatorInstance();
        }
        aggregatorInstance.accumulate(dataValueDescriptor, this);
    }
    
    void merge(final ExecRow execRow, final ExecRow execRow2) throws StandardException {
        this.merge(execRow.getColumn(this.aggregatorColumnId + 1), execRow2.getColumn(this.aggregatorColumnId + 1));
    }
    
    void merge(final Object[] array, final Object[] array2) throws StandardException {
        this.merge((Storable)array[this.aggregatorColumnId], (Storable)array2[this.aggregatorColumnId]);
    }
    
    boolean finish(final ExecRow execRow) throws StandardException {
        final DataValueDescriptor column = execRow.getColumn(this.resultColumnId + 1);
        ExecAggregator aggregatorInstance = (ExecAggregator)execRow.getColumn(this.aggregatorColumnId + 1).getObject();
        if (aggregatorInstance == null) {
            aggregatorInstance = this.getAggregatorInstance();
        }
        final DataValueDescriptor result = aggregatorInstance.getResult();
        if (result == null) {
            column.setToNull();
        }
        else {
            column.setValue(result);
        }
        return aggregatorInstance.didEliminateNulls();
    }
    
    ExecAggregator getAggregatorInstance() throws StandardException {
        if (this.cachedAggregator == null) {
            try {
                final ExecAggregator aggregator = this.cf.loadApplicationClass(this.aggInfo.getAggregatorClassName()).newInstance();
                (this.cachedAggregator = aggregator).setup(this.cf, this.aggInfo.getAggregateName(), this.aggInfo.getResultDescription().getColumnInfo()[0].getType());
                return aggregator;
            }
            catch (Exception ex) {
                throw StandardException.unexpectedUserException(ex);
            }
        }
        return this.cachedAggregator.newAggregator();
    }
    
    int getColumnId() {
        return this.aggregatorColumnId;
    }
    
    DataValueDescriptor getInputColumnValue(final ExecRow execRow) throws StandardException {
        return execRow.getColumn(this.inputColumnId + 1);
    }
    
    void merge(final Storable storable, final Storable storable2) throws StandardException {
        ((ExecAggregator)((UserDataValue)storable2).getObject()).merge((ExecAggregator)((UserDataValue)storable).getObject());
    }
    
    AggregatorInfo getAggregatorInfo() {
        return this.aggInfo;
    }
}
