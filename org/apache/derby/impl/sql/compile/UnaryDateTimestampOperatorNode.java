// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.types.DateTimeDataValue;
import java.util.List;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataTypeDescriptor;

public class UnaryDateTimestampOperatorNode extends UnaryOperatorNode
{
    private static final String TIMESTAMP_METHOD_NAME = "getTimestamp";
    private static final String DATE_METHOD_NAME = "getDate";
    
    public void init(final Object o, final Object o2) throws StandardException {
        this.setType((DataTypeDescriptor)o2);
        switch (this.getTypeServices().getJDBCTypeId()) {
            case 91: {
                super.init(o, "date", "getDate");
                break;
            }
            case 93: {
                super.init(o, "timestamp", "getTimestamp");
                break;
            }
            default: {
                super.init(o);
                break;
            }
        }
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        boolean b = false;
        this.bindOperand(list, list2, list3);
        switch (this.operand.getTypeServices().getJDBCTypeId()) {
            case -6:
            case -5:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 8: {
                if ("getTimestamp".equals(this.methodName)) {
                    this.invalidOperandType();
                }
                break;
            }
            case 1:
            case 12: {
                break;
            }
            case 91: {
                if ("getTimestamp".equals(this.methodName)) {
                    this.invalidOperandType();
                }
                b = true;
                break;
            }
            case 0: {
                break;
            }
            case 93: {
                if ("getTimestamp".equals(this.methodName)) {
                    b = true;
                    break;
                }
                break;
            }
            default: {
                this.invalidOperandType();
                break;
            }
        }
        if (this.operand instanceof ConstantNode) {
            final DataValueFactory dataValueFactory = this.getLanguageConnectionContext().getDataValueFactory();
            final DataValueDescriptor value = ((ConstantNode)this.operand).getValue();
            DateTimeDataValue dateTimeDataValue;
            if (value.isNull()) {
                dateTimeDataValue = ("getTimestamp".equals(this.methodName) ? dataValueFactory.getNullTimestamp(null) : dataValueFactory.getNullDate(null));
            }
            else {
                dateTimeDataValue = ("getTimestamp".equals(this.methodName) ? dataValueFactory.getTimestamp(value) : dataValueFactory.getDate(value));
            }
            return (ValueNode)this.getNodeFactory().getNode(76, dateTimeDataValue, this.getContextManager());
        }
        if (b) {
            return this.operand;
        }
        return this;
    }
    
    private void invalidOperandType() throws StandardException {
        throw StandardException.newException("42X25", this.getOperatorString(), this.getOperand().getTypeServices().getSQLstring());
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        expressionClassBuilder.pushDataValueFactory(methodBuilder);
        this.operand.generateExpression(expressionClassBuilder, methodBuilder);
        methodBuilder.cast("org.apache.derby.iapi.types.DataValueDescriptor");
        methodBuilder.callMethod((short)185, null, this.methodName, this.getTypeCompiler().interfaceName(), 1);
    }
}
