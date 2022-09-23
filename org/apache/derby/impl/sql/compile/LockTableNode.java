// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;

public class LockTableNode extends MiscellaneousStatementNode
{
    private TableName tableName;
    private boolean exclusiveMode;
    private long conglomerateNumber;
    private TableDescriptor lockTableDescriptor;
    
    public void init(final Object o, final Object o2) {
        this.tableName = (TableName)o;
        this.exclusiveMode = (boolean)o2;
    }
    
    public String toString() {
        return "";
    }
    
    public String statementToString() {
        return "LOCK TABLE";
    }
    
    public void bindStatement() throws StandardException {
        final CompilerContext compilerContext = this.getCompilerContext();
        this.getDataDictionary();
        final String schemaName = this.tableName.getSchemaName();
        final SchemaDescriptor schemaDescriptor = this.getSchemaDescriptor(schemaName);
        if (schemaDescriptor.isSystemSchema()) {
            throw StandardException.newException("42X62", this.statementToString(), schemaName);
        }
        this.lockTableDescriptor = this.getTableDescriptor(this.tableName.getTableName(), schemaDescriptor);
        if (this.lockTableDescriptor == null) {
            final TableName resolveTableToSynonym = this.resolveTableToSynonym(this.tableName);
            if (resolveTableToSynonym == null) {
                throw StandardException.newException("42X05", this.tableName);
            }
            this.tableName = resolveTableToSynonym;
            this.lockTableDescriptor = this.getTableDescriptor(resolveTableToSynonym.getTableName(), this.getSchemaDescriptor(this.tableName.getSchemaName()));
            if (this.lockTableDescriptor == null) {
                throw StandardException.newException("42X05", this.tableName);
            }
        }
        if (this.lockTableDescriptor.getTableType() == 3) {
            throw StandardException.newException("42995");
        }
        this.conglomerateNumber = this.lockTableDescriptor.getHeapConglomerateId();
        final ConglomerateDescriptor conglomerateDescriptor = this.lockTableDescriptor.getConglomerateDescriptor(this.conglomerateNumber);
        compilerContext.createDependency(this.lockTableDescriptor);
        compilerContext.createDependency(conglomerateDescriptor);
        if (this.isPrivilegeCollectionRequired()) {
            compilerContext.pushCurrentPrivType(0);
            compilerContext.addRequiredTablePriv(this.lockTableDescriptor);
            compilerContext.popCurrentPrivType();
        }
    }
    
    public boolean referencesSessionSchema() throws StandardException {
        return this.isSessionSchema(this.lockTableDescriptor.getSchemaName());
    }
    
    public ConstantAction makeConstantAction() throws StandardException {
        return this.getGenericConstantActionFactory().getLockTableConstantAction(this.tableName.getFullTableName(), this.conglomerateNumber, this.exclusiveMode);
    }
}
