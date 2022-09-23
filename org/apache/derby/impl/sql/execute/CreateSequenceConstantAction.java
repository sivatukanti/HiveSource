// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.dictionary.SequenceDescriptor;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.sql.dictionary.DataDescriptorGenerator;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.types.DataTypeDescriptor;

class CreateSequenceConstantAction extends DDLConstantAction
{
    private String _sequenceName;
    private String _schemaName;
    private DataTypeDescriptor _dataType;
    private long _initialValue;
    private long _stepValue;
    private long _maxValue;
    private long _minValue;
    private boolean _cycle;
    
    public CreateSequenceConstantAction(final String schemaName, final String sequenceName, final DataTypeDescriptor dataType, final long initialValue, final long stepValue, final long maxValue, final long minValue, final boolean cycle) {
        this._schemaName = schemaName;
        this._sequenceName = sequenceName;
        this._dataType = dataType;
        this._initialValue = initialValue;
        this._stepValue = stepValue;
        this._maxValue = maxValue;
        this._minValue = minValue;
        this._cycle = cycle;
    }
    
    public void executeConstantAction(final Activation activation) throws StandardException {
        final LanguageConnectionContext languageConnectionContext = activation.getLanguageConnectionContext();
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        final TransactionController transactionExecute = languageConnectionContext.getTransactionExecute();
        final DataDescriptorGenerator dataDescriptorGenerator = dataDictionary.getDataDescriptorGenerator();
        dataDictionary.startWriting(languageConnectionContext);
        final SchemaDescriptor schemaDescriptorForCreate = DDLConstantAction.getSchemaDescriptorForCreate(dataDictionary, activation, this._schemaName);
        final SequenceDescriptor sequenceDescriptor = dataDictionary.getSequenceDescriptor(schemaDescriptorForCreate, this._sequenceName);
        if (sequenceDescriptor != null) {
            throw StandardException.newException("X0Y68.S", sequenceDescriptor.getDescriptorType(), this._sequenceName);
        }
        dataDictionary.addDescriptor(dataDescriptorGenerator.newSequenceDescriptor(schemaDescriptorForCreate, dataDictionary.getUUIDFactory().createUUID(), this._sequenceName, this._dataType, new Long(this._initialValue), this._initialValue, this._minValue, this._maxValue, this._stepValue, this._cycle), null, 20, false, transactionExecute);
    }
    
    public String toString() {
        return "CREATE SEQUENCE " + this._sequenceName;
    }
}
