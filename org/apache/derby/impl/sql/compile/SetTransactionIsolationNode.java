// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.compiler.MethodBuilder;

public class SetTransactionIsolationNode extends TransactionStatementNode
{
    private int isolationLevel;
    
    public void init(final Object o) {
        this.isolationLevel = (int)o;
    }
    
    public String toString() {
        return "";
    }
    
    public String statementToString() {
        return "SET TRANSACTION ISOLATION";
    }
    
    public void generate(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        activationClassBuilder.pushGetResultSetFactoryExpression(methodBuilder);
        activationClassBuilder.pushThisAsActivation(methodBuilder);
        methodBuilder.callMethod((short)185, null, "getSetTransactionResultSet", "org.apache.derby.iapi.sql.ResultSet", 1);
    }
    
    public ConstantAction makeConstantAction() throws StandardException {
        return this.getGenericConstantActionFactory().getSetTransactionIsolationConstantAction(this.isolationLevel);
    }
}
