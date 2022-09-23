// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.error.StandardException;

public class DropSequenceNode extends DDLStatementNode
{
    private TableName dropItem;
    
    public void init(final Object o) throws StandardException {
        this.initAndCheck(this.dropItem = (TableName)o);
    }
    
    public String statementToString() {
        return "DROP ".concat(this.dropItem.getTableName());
    }
    
    public void bindStatement() throws StandardException {
        final DataDictionary dataDictionary = this.getDataDictionary();
        final String relativeName = this.getRelativeName();
        Provider sequenceDescriptor = null;
        final SchemaDescriptor schemaDescriptor = this.getSchemaDescriptor();
        if (schemaDescriptor.getUUID() != null) {
            sequenceDescriptor = dataDictionary.getSequenceDescriptor(schemaDescriptor, relativeName);
        }
        if (sequenceDescriptor == null) {
            throw StandardException.newException("42Y55", this.statementToString(), relativeName);
        }
        this.getCompilerContext().createDependency(sequenceDescriptor);
    }
    
    public ConstantAction makeConstantAction() throws StandardException {
        return this.getGenericConstantActionFactory().getDropSequenceConstantAction(this.getSchemaDescriptor(), this.getRelativeName());
    }
}
