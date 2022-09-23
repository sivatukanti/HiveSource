// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.error.StandardException;
import java.util.List;

public class SimpleStringOperatorNode extends UnaryOperatorNode
{
    public void init(final Object o, final Object o2) {
        super.init(o, o2, o2);
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        this.bindOperand(list, list2, list3);
        TypeId typeId = this.operand.getTypeId();
        switch (typeId.getJDBCTypeId()) {
            case -1:
            case 1:
            case 12:
            case 2005: {
                break;
            }
            case 1111:
            case 2000: {
                throw StandardException.newException("42X25", this.methodName, typeId.getSQLTypeName());
            }
            default: {
                (this.operand = (ValueNode)this.getNodeFactory().getNode(60, this.operand, DataTypeDescriptor.getBuiltInDataTypeDescriptor(12, true, this.operand.getTypeCompiler().getCastToCharWidth(this.operand.getTypeServices())), this.getContextManager())).setCollationUsingCompilationSchema();
                ((CastNode)this.operand).bindCastNodeOnly();
                typeId = this.operand.getTypeId();
                break;
            }
        }
        this.setType(new DataTypeDescriptor(typeId, this.operand.getTypeServices().isNullable(), this.operand.getTypeCompiler().getCastToCharWidth(this.operand.getTypeServices())));
        this.setCollationInfo(this.operand.getTypeServices());
        return this;
    }
    
    void bindParameter() throws StandardException {
        this.operand.setType(DataTypeDescriptor.getBuiltInDataTypeDescriptor(12));
        this.operand.setCollationUsingCompilationSchema();
    }
    
    public String getReceiverInterfaceName() {
        return "org.apache.derby.iapi.types.StringDataValue";
    }
}
