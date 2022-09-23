// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.compiler.MethodBuilder;

abstract class MiscellaneousStatementNode extends StatementNode
{
    int activationKind() {
        return 0;
    }
    
    public void generate(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        activationClassBuilder.pushGetResultSetFactoryExpression(methodBuilder);
        activationClassBuilder.pushThisAsActivation(methodBuilder);
        methodBuilder.callMethod((short)185, null, "getMiscResultSet", "org.apache.derby.iapi.sql.ResultSet", 1);
    }
    
    public boolean needsSavepoint() {
        return false;
    }
}
