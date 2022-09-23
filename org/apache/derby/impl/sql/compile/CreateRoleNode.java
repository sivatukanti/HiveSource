// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import org.apache.derby.iapi.error.StandardException;

public class CreateRoleNode extends DDLStatementNode
{
    private String name;
    
    public void init(final Object o) throws StandardException {
        this.initAndCheck(null);
        this.name = (String)o;
    }
    
    public String toString() {
        return "";
    }
    
    public void bindStatement() throws StandardException {
        final CompilerContext compilerContext = this.getCompilerContext();
        if (this.isPrivilegeCollectionRequired()) {
            compilerContext.addRequiredRolePriv(this.name, 19);
        }
    }
    
    public String statementToString() {
        return "CREATE ROLE";
    }
    
    public ConstantAction makeConstantAction() {
        return this.getGenericConstantActionFactory().getCreateRoleConstantAction(this.name);
    }
}
