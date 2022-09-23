// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.execute.ConstantAction;

class SetTransactionIsolationConstantAction implements ConstantAction
{
    private final int isolationLevel;
    
    SetTransactionIsolationConstantAction(final int isolationLevel) {
        this.isolationLevel = isolationLevel;
    }
    
    public String toString() {
        return "SET TRANSACTION ISOLATION LEVEL = " + this.isolationLevel;
    }
    
    public void executeConstantAction(final Activation activation) throws StandardException {
        activation.getLanguageConnectionContext().setIsolationLevel(this.isolationLevel);
    }
}
