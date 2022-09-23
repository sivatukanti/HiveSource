// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;

public class DropTableNode extends DDLStatementNode
{
    private long conglomerateNumber;
    private int dropBehavior;
    private TableDescriptor td;
    
    public void init(final Object o, final Object o2) throws StandardException {
        this.initAndCheck(o);
        this.dropBehavior = (int)o2;
    }
    
    public String toString() {
        return "";
    }
    
    public String statementToString() {
        return "DROP TABLE";
    }
    
    public void bindStatement() throws StandardException {
        final CompilerContext compilerContext = this.getCompilerContext();
        this.td = this.getTableDescriptor();
        this.conglomerateNumber = this.td.getHeapConglomerateId();
        final ConglomerateDescriptor conglomerateDescriptor = this.td.getConglomerateDescriptor(this.conglomerateNumber);
        compilerContext.createDependency(this.td);
        compilerContext.createDependency(conglomerateDescriptor);
    }
    
    public boolean referencesSessionSchema() throws StandardException {
        return this.isSessionSchema(this.td.getSchemaDescriptor());
    }
    
    public ConstantAction makeConstantAction() throws StandardException {
        return this.getGenericConstantActionFactory().getDropTableConstantAction(this.getFullName(), this.getRelativeName(), this.getSchemaDescriptor(this.td.getTableType() != 3, true), this.conglomerateNumber, this.td.getUUID(), this.dropBehavior);
    }
}
