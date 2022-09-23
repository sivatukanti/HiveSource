// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import org.apache.derby.iapi.error.StandardException;

public class CreateSchemaNode extends DDLStatementNode
{
    private String name;
    private String aid;
    
    public void init(final Object o, final Object o2) throws StandardException {
        this.initAndCheck(null);
        this.name = (String)o;
        this.aid = (String)o2;
    }
    
    public String toString() {
        return "";
    }
    
    public void bindStatement() throws StandardException {
        final CompilerContext compilerContext = this.getCompilerContext();
        if (this.isPrivilegeCollectionRequired()) {
            compilerContext.addRequiredSchemaPriv(this.name, this.aid, 16);
        }
    }
    
    public String statementToString() {
        return "CREATE SCHEMA";
    }
    
    public ConstantAction makeConstantAction() {
        return this.getGenericConstantActionFactory().getCreateSchemaConstantAction(this.name, this.aid);
    }
}
