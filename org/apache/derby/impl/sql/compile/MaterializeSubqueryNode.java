// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.services.compiler.LocalField;

class MaterializeSubqueryNode extends ResultSetNode
{
    private LocalField lf;
    
    public MaterializeSubqueryNode(final LocalField lf) {
        this.lf = lf;
    }
    
    public void generate(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        activationClassBuilder.pushThisAsActivation(methodBuilder);
        methodBuilder.getField(this.lf);
        methodBuilder.callMethod((short)182, "org.apache.derby.impl.sql.execute.BaseActivation", "materializeResultSetIfPossible", "org.apache.derby.iapi.sql.execute.NoPutResultSet", 1);
    }
    
    void decrementLevel(final int n) {
    }
}
