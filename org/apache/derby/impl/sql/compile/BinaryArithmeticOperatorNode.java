// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import java.util.List;

public final class BinaryArithmeticOperatorNode extends BinaryOperatorNode
{
    public void init(final Object o, final Object o2) {
        super.init(o, o2, "org.apache.derby.iapi.types.NumberDataValue", "org.apache.derby.iapi.types.NumberDataValue");
    }
    
    public void setNodeType(final int nodeType) {
        String operator = null;
        String methodName = null;
        switch (nodeType) {
            case 40: {
                operator = "/";
                methodName = "divide";
                break;
            }
            case 46: {
                operator = "-";
                methodName = "minus";
                break;
            }
            case 48: {
                operator = "+";
                methodName = "plus";
                break;
            }
            case 49: {
                operator = "*";
                methodName = "times";
                break;
            }
            case 194: {
                operator = "mod";
                methodName = "mod";
                break;
            }
        }
        this.setOperator(operator);
        this.setMethodName(methodName);
        super.setNodeType(nodeType);
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        super.bindExpression(list, list2, list3);
        final TypeId typeId = this.leftOperand.getTypeId();
        final TypeId typeId2 = this.rightOperand.getTypeId();
        final DataTypeDescriptor typeServices = this.leftOperand.getTypeServices();
        final DataTypeDescriptor typeServices2 = this.rightOperand.getTypeServices();
        if (typeId.isStringTypeId() && typeId2.isNumericTypeId()) {
            final boolean b = typeServices.isNullable() || typeServices2.isNullable();
            int precision = typeServices2.getPrecision();
            int scale = typeServices2.getScale();
            int maximumWidth = typeServices2.getMaximumWidth();
            if (typeId2.isDecimalTypeId()) {
                final int maximumWidth2 = typeServices.getMaximumWidth();
                precision += 2 * maximumWidth2;
                scale += maximumWidth2;
                maximumWidth = precision + 3;
            }
            this.leftOperand = (ValueNode)this.getNodeFactory().getNode(60, this.leftOperand, new DataTypeDescriptor(typeId2, precision, scale, b, maximumWidth), this.getContextManager());
            ((CastNode)this.leftOperand).bindCastNodeOnly();
        }
        else if (typeId2.isStringTypeId() && typeId.isNumericTypeId()) {
            final boolean b2 = typeServices.isNullable() || typeServices2.isNullable();
            int precision2 = typeServices.getPrecision();
            int scale2 = typeServices.getScale();
            int maximumWidth3 = typeServices.getMaximumWidth();
            if (typeId.isDecimalTypeId()) {
                final int maximumWidth4 = typeServices2.getMaximumWidth();
                precision2 += 2 * maximumWidth4;
                scale2 += maximumWidth4;
                maximumWidth3 = precision2 + 3;
            }
            this.rightOperand = (ValueNode)this.getNodeFactory().getNode(60, this.rightOperand, new DataTypeDescriptor(typeId, precision2, scale2, b2, maximumWidth3), this.getContextManager());
            ((CastNode)this.rightOperand).bindCastNodeOnly();
        }
        this.setType(this.leftOperand.getTypeCompiler().resolveArithmeticOperation(this.leftOperand.getTypeServices(), this.rightOperand.getTypeServices(), this.operator));
        return this;
    }
}
