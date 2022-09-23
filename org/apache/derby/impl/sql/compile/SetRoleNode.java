// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.execute.ConstantAction;

public class SetRoleNode extends MiscellaneousStatementNode
{
    private String name;
    private int type;
    
    public void init(final Object o, final Object o2) {
        this.name = (String)o;
        if (o2 != null) {
            this.type = (int)o2;
        }
    }
    
    public String toString() {
        return "";
    }
    
    public String statementToString() {
        return "SET ROLE";
    }
    
    public ConstantAction makeConstantAction() throws StandardException {
        return this.getGenericConstantActionFactory().getSetRoleConstantAction(this.name, this.type);
    }
    
    public void generate(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        if (this.type == 1) {
            this.generateParameterValueSet(activationClassBuilder);
        }
        activationClassBuilder.pushGetResultSetFactoryExpression(methodBuilder);
        activationClassBuilder.pushThisAsActivation(methodBuilder);
        methodBuilder.callMethod((short)185, null, "getMiscResultSet", "org.apache.derby.iapi.sql.ResultSet", 1);
    }
    
    private void generateParameterValueSet(final ActivationClassBuilder activationClassBuilder) throws StandardException {
        ParameterNode.generateParameterValueSet(activationClassBuilder, 1, this.getCompilerContext().getParameterList());
    }
    
    int activationKind() {
        if (this.type == 1) {
            return 2;
        }
        return 0;
    }
    
    public boolean isAtomic() {
        return false;
    }
}
