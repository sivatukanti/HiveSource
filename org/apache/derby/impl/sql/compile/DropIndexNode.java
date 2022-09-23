// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.iapi.sql.dictionary.ConstraintDescriptor;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;

public class DropIndexNode extends DDLStatementNode
{
    private ConglomerateDescriptor cd;
    private TableDescriptor td;
    
    public String statementToString() {
        return "DROP INDEX";
    }
    
    public void bindStatement() throws StandardException {
        final CompilerContext compilerContext = this.getCompilerContext();
        final DataDictionary dataDictionary = this.getDataDictionary();
        final SchemaDescriptor schemaDescriptor = this.getSchemaDescriptor();
        if (schemaDescriptor.getUUID() != null) {
            this.cd = dataDictionary.getConglomerateDescriptor(this.getRelativeName(), schemaDescriptor, false);
        }
        if (this.cd == null) {
            throw StandardException.newException("42X65", this.getFullName());
        }
        this.td = this.getTableDescriptor(this.cd.getTableID());
        if (this.cd.isConstraint()) {
            final ConstraintDescriptor constraintDescriptor = dataDictionary.getConstraintDescriptor(this.td, this.cd.getUUID());
            if (constraintDescriptor != null) {
                throw StandardException.newException("42X84", this.getFullName(), constraintDescriptor.getConstraintName());
            }
        }
        compilerContext.createDependency(this.td);
        compilerContext.createDependency(this.cd);
    }
    
    public ConstantAction makeConstantAction() throws StandardException {
        return this.getGenericConstantActionFactory().getDropIndexConstantAction(this.getFullName(), this.getRelativeName(), this.getRelativeName(), this.getSchemaDescriptor().getSchemaName(), this.td.getUUID(), this.td.getHeapConglomerateId());
    }
}
