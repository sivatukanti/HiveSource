// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.types.TypeId;
import java.util.List;

public final class LengthOperatorNode extends UnaryOperatorNode
{
    private int parameterType;
    private int parameterWidth;
    
    public void setNodeType(final int nodeType) {
        String operator = null;
        String methodName = null;
        if (nodeType == 23) {
            operator = "char_length";
            methodName = "charLength";
            this.parameterType = 12;
            this.parameterWidth = 32672;
        }
        this.setOperator(operator);
        this.setMethodName(methodName);
        super.setNodeType(nodeType);
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        this.bindOperand(list, list2, list3);
        final TypeId typeId = this.operand.getTypeId();
        switch (typeId.getJDBCTypeId()) {
            case -4:
            case -3:
            case -2:
            case -1:
            case 1:
            case 12:
            case 2004:
            case 2005: {
                this.setType(new DataTypeDescriptor(TypeId.INTEGER_ID, this.operand.getTypeServices().isNullable()));
                return this;
            }
            default: {
                throw StandardException.newException("42X25", this.getOperatorString(), typeId.getSQLTypeName());
            }
        }
    }
    
    void bindParameter() throws StandardException {
        this.operand.setType(DataTypeDescriptor.getBuiltInDataTypeDescriptor(this.parameterType, true, this.parameterWidth));
    }
    
    public String getReceiverInterfaceName() {
        return "org.apache.derby.iapi.types.ConcatableDataValue";
    }
}
