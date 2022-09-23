// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.error.StandardException;
import java.util.List;

abstract class BinaryLogicalOperatorNode extends BinaryOperatorNode
{
    boolean shortCircuitValue;
    
    public void init(final Object o, final Object o2, final Object o3) {
        super.init(o, o2, o3, o3, "org.apache.derby.iapi.types.BooleanDataValue", "org.apache.derby.iapi.types.BooleanDataValue");
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        if (this.leftOperand.isParameterNode() || this.rightOperand.isParameterNode()) {
            throw StandardException.newException("42X19.S.2");
        }
        super.bindExpression(list, list2, list3);
        return this;
    }
    
    boolean verifyEliminateNots() {
        return true;
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        this.leftOperand.generateExpression(expressionClassBuilder, methodBuilder);
        methodBuilder.dup();
        methodBuilder.push(this.shortCircuitValue);
        methodBuilder.callMethod((short)185, null, "equals", "boolean", 1);
        methodBuilder.conditionalIf();
        methodBuilder.callMethod((short)185, null, "getImmutable", "org.apache.derby.iapi.types.BooleanDataValue", 0);
        methodBuilder.startElseCode();
        this.rightOperand.generateExpression(expressionClassBuilder, methodBuilder);
        methodBuilder.upCast("org.apache.derby.iapi.types.BooleanDataValue");
        methodBuilder.callMethod((short)185, null, this.methodName, "org.apache.derby.iapi.types.BooleanDataValue", 1);
        methodBuilder.completeConditional();
    }
    
    DataTypeDescriptor resolveLogicalBinaryOperator(final DataTypeDescriptor dataTypeDescriptor, final DataTypeDescriptor dataTypeDescriptor2) throws StandardException {
        if (!dataTypeDescriptor.getTypeId().isBooleanTypeId() || !dataTypeDescriptor2.getTypeId().isBooleanTypeId()) {
            throw StandardException.newException("42Y94");
        }
        return dataTypeDescriptor.getNullabilityType(dataTypeDescriptor.isNullable() || dataTypeDescriptor2.isNullable());
    }
}
