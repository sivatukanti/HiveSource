// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import org.apache.derby.iapi.error.StandardException;

public class DropRoleNode extends DDLStatementNode
{
    private String roleName;
    
    public void init(final Object o) throws StandardException {
        this.initAndCheck(null);
        this.roleName = (String)o;
    }
    
    public void bindStatement() throws StandardException {
        final CompilerContext compilerContext = this.getCompilerContext();
        if (this.isPrivilegeCollectionRequired()) {
            compilerContext.addRequiredRolePriv(this.roleName, 20);
        }
    }
    
    public String toString() {
        return "";
    }
    
    public String statementToString() {
        return "DROP ROLE";
    }
    
    public ConstantAction makeConstantAction() throws StandardException {
        return this.getGenericConstantActionFactory().getDropRoleConstantAction(this.roleName);
    }
}
