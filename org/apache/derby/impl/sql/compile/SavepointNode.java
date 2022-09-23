// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.iapi.error.StandardException;

public class SavepointNode extends DDLStatementNode
{
    private String savepointName;
    private int savepointStatementType;
    
    public void init(final Object o, final Object o2) throws StandardException {
        this.initAndCheck(null);
        this.savepointName = (String)o;
        this.savepointStatementType = (int)o2;
    }
    
    public String toString() {
        return "";
    }
    
    public String statementToString() {
        if (this.savepointStatementType == 1) {
            return "SAVEPOINT";
        }
        if (this.savepointStatementType == 2) {
            return "ROLLBACK WORK TO SAVEPOINT";
        }
        return "RELEASE TO SAVEPOINT";
    }
    
    public boolean needsSavepoint() {
        return false;
    }
    
    public ConstantAction makeConstantAction() throws StandardException {
        return this.getGenericConstantActionFactory().getSavepointConstantAction(this.savepointName, this.savepointStatementType);
    }
}
