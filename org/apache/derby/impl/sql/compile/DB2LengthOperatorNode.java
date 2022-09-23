// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.compiler.LocalField;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.error.StandardException;
import java.util.List;

public final class DB2LengthOperatorNode extends UnaryOperatorNode
{
    public void init(final Object o) {
        super.init(o, "length", "getDB2Length");
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        this.bindOperand(list, list2, list3);
        final TypeId typeId = this.operand.getTypeId();
        if (typeId.isXMLTypeId()) {
            throw StandardException.newException("42X25", this.getOperatorString(), typeId.getSQLTypeName());
        }
        this.setType(new DataTypeDescriptor(TypeId.getBuiltInTypeId(4), this.operand.getTypeServices().isNullable()));
        return this;
    }
    
    public String getReceiverInterfaceName() {
        return "org.apache.derby.iapi.types.ConcatableDataValue";
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        if (this.operand == null) {
            return;
        }
        final int constantLength = this.getConstantLength();
        final String interfaceName = this.getTypeCompiler().interfaceName();
        methodBuilder.pushThis();
        this.operand.generateExpression(expressionClassBuilder, methodBuilder);
        methodBuilder.upCast("org.apache.derby.iapi.types.DataValueDescriptor");
        methodBuilder.push(constantLength);
        final LocalField fieldDeclaration = expressionClassBuilder.newFieldDeclaration(2, interfaceName);
        methodBuilder.getField(fieldDeclaration);
        methodBuilder.callMethod((short)182, "org.apache.derby.impl.sql.execute.BaseActivation", this.methodName, interfaceName, 3);
        methodBuilder.putField(fieldDeclaration);
    }
    
    private int getConstantLength() throws StandardException {
        final DataTypeDescriptor typeServices = this.operand.getTypeServices();
        switch (typeServices.getJDBCTypeId()) {
            case -5: {
                return 8;
            }
            case -7:
            case 16: {
                return 1;
            }
            case -2:
            case 1: {
                return typeServices.getMaximumWidth();
            }
            case 91: {
                return 4;
            }
            case 2:
            case 3: {
                return typeServices.getPrecision() / 2 + 1;
            }
            case 8: {
                return 8;
            }
            case 4:
            case 6:
            case 7: {
                return 4;
            }
            case 5: {
                return 2;
            }
            case 92: {
                return 3;
            }
            case 93: {
                return 10;
            }
            case -6: {
                return 1;
            }
            case -4:
            case -3:
            case -1:
            case 12:
            case 2004: {
                return this.getConstantNodeLength();
            }
            default: {
                return -1;
            }
        }
    }
    
    private int getConstantNodeLength() throws StandardException {
        if (this.operand instanceof ConstantNode) {
            return ((ConstantNode)this.operand).getValue().getLength();
        }
        return -1;
    }
}
