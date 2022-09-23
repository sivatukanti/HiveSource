// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.iapi.error.StandardException;
import java.util.List;

public class GrantRoleNode extends DDLStatementNode
{
    private List roles;
    private List grantees;
    
    public void init(final Object o, final Object o2) throws StandardException {
        this.initAndCheck(null);
        this.roles = (List)o;
        this.grantees = (List)o2;
    }
    
    public ConstantAction makeConstantAction() throws StandardException {
        return this.getGenericConstantActionFactory().getGrantRoleConstantAction(this.roles, this.grantees);
    }
    
    public String toString() {
        return "";
    }
    
    public String statementToString() {
        return "GRANT role";
    }
}
