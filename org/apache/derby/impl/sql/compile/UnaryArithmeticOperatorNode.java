// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.compiler.MethodBuilder;
import java.util.List;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.types.TypeId;

public class UnaryArithmeticOperatorNode extends UnaryOperatorNode
{
    private static final int UNARY_PLUS = 0;
    private static final int UNARY_MINUS = 1;
    private static final int SQRT = 2;
    private static final int ABSOLUTE = 3;
    private static final String[] UNARY_OPERATORS;
    private static final String[] UNARY_METHODS;
    private int operatorType;
    
    public void init(final Object o) {
        switch (this.getNodeType()) {
            case 30: {
                this.operatorType = 0;
                break;
            }
            case 29: {
                this.operatorType = 1;
                break;
            }
            case 189: {
                this.operatorType = 2;
                break;
            }
            case 188: {
                this.operatorType = 3;
                break;
            }
        }
        this.init(o, UnaryArithmeticOperatorNode.UNARY_OPERATORS[this.operatorType], UnaryArithmeticOperatorNode.UNARY_METHODS[this.operatorType]);
    }
    
    public boolean requiresTypeFromContext() {
        return (this.operatorType == 0 || this.operatorType == 1) && this.operand.requiresTypeFromContext();
    }
    
    public boolean isParameterNode() {
        return (this.operatorType == 0 || this.operatorType == 1) && this.operand.isParameterNode();
    }
    
    void bindParameter() throws StandardException {
        if (this.operatorType == 2 || this.operatorType == 3) {
            this.operand.setType(new DataTypeDescriptor(TypeId.getBuiltInTypeId(8), true));
            return;
        }
        if (this.operatorType == 1 || this.operatorType == 0) {
            return;
        }
        super.bindParameter();
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        if (this.operand.requiresTypeFromContext() && (this.operatorType == 0 || this.operatorType == 1) && this.operand.getTypeServices() == null) {
            return this;
        }
        this.bindOperand(list, list2, list3);
        if (this.operatorType == 2 || this.operatorType == 3) {
            this.bindSQRTABS();
        }
        else if (this.operatorType == 0 || this.operatorType == 1) {
            this.checkOperandIsNumeric(this.operand.getTypeId());
        }
        super.setType(this.operand.getTypeServices());
        return this;
    }
    
    private void checkOperandIsNumeric(final TypeId typeId) throws StandardException {
        if (!typeId.isNumericTypeId()) {
            throw StandardException.newException("42X37", (this.operatorType == 0) ? "+" : "-", typeId.getSQLTypeName());
        }
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        if (this.operatorType == 0) {
            this.operand.generateExpression(expressionClassBuilder, methodBuilder);
        }
        else {
            super.generateExpression(expressionClassBuilder, methodBuilder);
        }
    }
    
    private void bindSQRTABS() throws StandardException {
        final TypeId typeId = this.operand.getTypeId();
        if (typeId.userType()) {
            this.operand = this.operand.genSQLJavaSQLTree();
        }
        final int jdbcTypeId = typeId.getJDBCTypeId();
        if (!typeId.isNumericTypeId()) {
            throw StandardException.newException("42X25", this.getOperatorString(), typeId.getSQLTypeName());
        }
        if (this.operatorType == 2 && jdbcTypeId != 8) {
            this.operand = (ValueNode)this.getNodeFactory().getNode(60, this.operand, new DataTypeDescriptor(TypeId.getBuiltInTypeId(8), true), this.getContextManager());
            ((CastNode)this.operand).bindCastNodeOnly();
        }
    }
    
    public void setType(final DataTypeDescriptor dataTypeDescriptor) throws StandardException {
        if (this.operand.requiresTypeFromContext() && this.operand.getTypeServices() == null) {
            this.checkOperandIsNumeric(dataTypeDescriptor.getTypeId());
            this.operand.setType(dataTypeDescriptor);
        }
        super.setType(dataTypeDescriptor);
    }
    
    static {
        UNARY_OPERATORS = new String[] { "+", "-", "SQRT", "ABS/ABSVAL" };
        UNARY_METHODS = new String[] { "plus", "minus", "sqrt", "absolute" };
    }
}
