// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import java.util.List;

public class TimestampOperatorNode extends BinaryOperatorNode
{
    public void init(final Object o, final Object o2) {
        this.leftOperand = (ValueNode)o;
        this.rightOperand = (ValueNode)o2;
        this.operator = "timestamp";
        this.methodName = "getTimestamp";
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        this.leftOperand = this.leftOperand.bindExpression(list, list2, list3);
        this.rightOperand = this.rightOperand.bindExpression(list, list2, list3);
        if (this.leftOperand.requiresTypeFromContext()) {
            this.leftOperand.setType(DataTypeDescriptor.getBuiltInDataTypeDescriptor(91));
        }
        if (this.rightOperand.requiresTypeFromContext()) {
            this.rightOperand.setType(DataTypeDescriptor.getBuiltInDataTypeDescriptor(92));
        }
        final TypeId typeId = this.leftOperand.getTypeId();
        final TypeId typeId2 = this.rightOperand.getTypeId();
        if (!this.leftOperand.requiresTypeFromContext() && !typeId.isStringTypeId() && typeId.getJDBCTypeId() != 91) {
            throw StandardException.newException("42Y95", this.operator, typeId.getSQLTypeName(), typeId2.getSQLTypeName());
        }
        if (!this.rightOperand.requiresTypeFromContext() && !typeId2.isStringTypeId() && typeId2.getJDBCTypeId() != 92) {
            throw StandardException.newException("42Y95", this.operator, typeId.getSQLTypeName(), typeId2.getSQLTypeName());
        }
        this.setType(DataTypeDescriptor.getBuiltInDataTypeDescriptor(93));
        return this.genSQLJavaSQLTree();
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        expressionClassBuilder.pushDataValueFactory(methodBuilder);
        this.leftOperand.generateExpression(expressionClassBuilder, methodBuilder);
        methodBuilder.cast("org.apache.derby.iapi.types.DataValueDescriptor");
        this.rightOperand.generateExpression(expressionClassBuilder, methodBuilder);
        methodBuilder.cast("org.apache.derby.iapi.types.DataValueDescriptor");
        methodBuilder.callMethod((short)185, null, this.methodName, "org.apache.derby.iapi.types.DateTimeDataValue", 2);
    }
}
