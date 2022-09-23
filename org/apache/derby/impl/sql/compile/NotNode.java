// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.compiler.LocalField;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.error.StandardException;

public final class NotNode extends UnaryLogicalOperatorNode
{
    public void init(final Object o) {
        super.init(o, "not");
    }
    
    ValueNode eliminateNots(final boolean b) throws StandardException {
        return this.operand.eliminateNots(!b);
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        final String interfaceName = this.getTypeCompiler().interfaceName();
        final LocalField fieldDeclaration = expressionClassBuilder.newFieldDeclaration(2, interfaceName);
        this.operand.generateExpression(expressionClassBuilder, methodBuilder);
        methodBuilder.upCast("org.apache.derby.iapi.types.DataValueDescriptor");
        methodBuilder.dup();
        methodBuilder.push(false);
        expressionClassBuilder.generateDataValue(methodBuilder, this.getTypeCompiler(), this.getTypeServices().getCollationType(), fieldDeclaration);
        methodBuilder.upCast("org.apache.derby.iapi.types.DataValueDescriptor");
        methodBuilder.callMethod((short)185, null, "equals", interfaceName, 2);
    }
}
