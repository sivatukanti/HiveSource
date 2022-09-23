// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.types.TypeId;
import java.util.List;

public class TestConstraintNode extends UnaryLogicalOperatorNode
{
    private String sqlState;
    private String tableName;
    private String constraintName;
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4) {
        super.init(o, "throwExceptionIfFalse");
        this.sqlState = (String)o2;
        this.tableName = (String)o3;
        this.constraintName = (String)o4;
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        this.bindOperand(list, list2, list3);
        if (!this.operand.getTypeServices().getTypeId().isBooleanTypeId()) {
            this.operand = (ValueNode)this.getNodeFactory().getNode(60, this.operand, new DataTypeDescriptor(TypeId.BOOLEAN_ID, true), this.getContextManager());
            ((CastNode)this.operand).bindCastNodeOnly();
        }
        this.setFullTypeInfo();
        return this;
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        this.operand.generateExpression(expressionClassBuilder, methodBuilder);
        methodBuilder.push(this.sqlState);
        methodBuilder.push(this.tableName);
        methodBuilder.push(this.constraintName);
        methodBuilder.callMethod((short)185, "org.apache.derby.iapi.types.BooleanDataValue", "throwExceptionIfFalse", "org.apache.derby.iapi.types.BooleanDataValue", 3);
    }
}
