// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.error.StandardException;

public class DropViewNode extends DDLStatementNode
{
    public void init(final Object o) throws StandardException {
        this.initAndCheck(o);
    }
    
    public String statementToString() {
        return "DROP VIEW";
    }
    
    public void bindStatement() throws StandardException {
        final DataDictionary dataDictionary = this.getDataDictionary();
        final CompilerContext compilerContext = this.getCompilerContext();
        final TableDescriptor tableDescriptor = dataDictionary.getTableDescriptor(this.getRelativeName(), this.getSchemaDescriptor(), this.getLanguageConnectionContext().getTransactionCompile());
        if (tableDescriptor != null) {
            compilerContext.createDependency(tableDescriptor);
        }
    }
    
    public ConstantAction makeConstantAction() throws StandardException {
        return this.getGenericConstantActionFactory().getDropViewConstantAction(this.getFullName(), this.getRelativeName(), this.getSchemaDescriptor());
    }
}
