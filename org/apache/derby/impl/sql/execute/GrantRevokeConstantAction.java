// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import java.util.List;
import org.apache.derby.iapi.sql.execute.ConstantAction;

class GrantRevokeConstantAction implements ConstantAction
{
    private boolean grant;
    private PrivilegeInfo privileges;
    private List grantees;
    
    GrantRevokeConstantAction(final boolean grant, final PrivilegeInfo privileges, final List grantees) {
        this.grant = grant;
        this.privileges = privileges;
        this.grantees = grantees;
    }
    
    public String toString() {
        return this.grant ? "GRANT" : "REVOKE";
    }
    
    public void executeConstantAction(final Activation activation) throws StandardException {
        this.privileges.executeGrantRevoke(activation, this.grant, this.grantees);
    }
}
