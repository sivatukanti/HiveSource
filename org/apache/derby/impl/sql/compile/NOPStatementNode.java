// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.error.StandardException;

public class NOPStatementNode extends StatementNode
{
    public String statementToString() {
        return "NO-OP";
    }
    
    public void bindStatement() throws StandardException {
        throw StandardException.newException("42Z54.U");
    }
    
    int activationKind() {
        return 0;
    }
}
