// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.iapi.error.StandardException;
import java.util.HashMap;
import java.util.List;

public class RevokeNode extends DDLStatementNode
{
    private PrivilegeNode privileges;
    private List grantees;
    
    public String toString() {
        return "";
    }
    
    public String statementToString() {
        return "REVOKE";
    }
    
    public void init(final Object o, final Object o2) {
        this.privileges = (PrivilegeNode)o;
        this.grantees = (List)o2;
    }
    
    public void bindStatement() throws StandardException {
        this.privileges = (PrivilegeNode)this.privileges.bind(new HashMap(), this.grantees, false);
    }
    
    public ConstantAction makeConstantAction() throws StandardException {
        return this.getGenericConstantActionFactory().getRevokeConstantAction(this.privileges.makePrivilegeInfo(), this.grantees);
    }
}
