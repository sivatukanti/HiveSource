// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.iapi.sql.dictionary.TriggerDescriptor;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;

public class DropTriggerNode extends DDLStatementNode
{
    private TableDescriptor td;
    
    public String statementToString() {
        return "DROP TRIGGER";
    }
    
    public void bindStatement() throws StandardException {
        final CompilerContext compilerContext = this.getCompilerContext();
        final DataDictionary dataDictionary = this.getDataDictionary();
        final SchemaDescriptor schemaDescriptor = this.getSchemaDescriptor();
        TriggerDescriptor triggerDescriptor = null;
        if (schemaDescriptor.getUUID() != null) {
            triggerDescriptor = dataDictionary.getTriggerDescriptor(this.getRelativeName(), schemaDescriptor);
        }
        if (triggerDescriptor == null) {
            throw StandardException.newException("42X94", "TRIGGER", this.getFullName());
        }
        compilerContext.createDependency(this.td = triggerDescriptor.getTableDescriptor());
        compilerContext.createDependency(triggerDescriptor);
    }
    
    public ConstantAction makeConstantAction() throws StandardException {
        return this.getGenericConstantActionFactory().getDropTriggerConstantAction(this.getSchemaDescriptor(), this.getRelativeName(), this.td.getUUID());
    }
}
