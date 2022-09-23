// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.iapi.error.StandardException;

public class DropSchemaNode extends DDLStatementNode
{
    private int dropBehavior;
    private String schemaName;
    
    public void init(final Object o, final Object o2) throws StandardException {
        this.initAndCheck(null);
        this.schemaName = (String)o;
        this.dropBehavior = (int)o2;
    }
    
    public void bindStatement() throws StandardException {
        if (this.getDataDictionary().isSystemSchemaName(this.schemaName)) {
            throw StandardException.newException("42Y67", this.schemaName);
        }
        if (this.isPrivilegeCollectionRequired()) {
            this.getCompilerContext().addRequiredSchemaPriv(this.schemaName, this.getLanguageConnectionContext().getStatementContext().getSQLSessionContext().getCurrentUser(), 18);
        }
    }
    
    public String toString() {
        return "";
    }
    
    public String statementToString() {
        return "DROP SCHEMA";
    }
    
    public ConstantAction makeConstantAction() throws StandardException {
        return this.getGenericConstantActionFactory().getDropSchemaConstantAction(this.schemaName);
    }
}
