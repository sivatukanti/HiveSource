// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.util.ReuseFactory;
import org.apache.derby.iapi.sql.compile.TypeCompiler;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.iapi.services.compiler.LocalField;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.error.StandardException;
import java.util.List;

public class TernaryOperatorNode extends OperatorNode
{
    String operator;
    String methodName;
    int operatorType;
    ValueNode receiver;
    ValueNode leftOperand;
    ValueNode rightOperand;
    String resultInterfaceType;
    String receiverInterfaceType;
    String leftInterfaceType;
    String rightInterfaceType;
    int trimType;
    public static final int TRIM = 0;
    public static final int LOCATE = 1;
    public static final int SUBSTRING = 2;
    public static final int LIKE = 3;
    public static final int TIMESTAMPADD = 4;
    public static final int TIMESTAMPDIFF = 5;
    static final String[] TernaryOperators;
    static final String[] TernaryMethodNames;
    static final String[] TernaryResultType;
    static final String[][] TernaryArgType;
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5) {
        this.receiver = (ValueNode)o;
        this.leftOperand = (ValueNode)o2;
        this.rightOperand = (ValueNode)o3;
        this.operatorType = (int)o4;
        this.operator = TernaryOperatorNode.TernaryOperators[this.operatorType];
        this.methodName = TernaryOperatorNode.TernaryMethodNames[this.operatorType];
        this.resultInterfaceType = TernaryOperatorNode.TernaryResultType[this.operatorType];
        this.receiverInterfaceType = TernaryOperatorNode.TernaryArgType[this.operatorType][0];
        this.leftInterfaceType = TernaryOperatorNode.TernaryArgType[this.operatorType][1];
        this.rightInterfaceType = TernaryOperatorNode.TernaryArgType[this.operatorType][2];
        if (o5 != null) {
            this.trimType = (int)o5;
        }
    }
    
    public String toString() {
        return "";
    }
    
    public void printSubNodes(final int n) {
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        this.receiver = this.receiver.bindExpression(list, list2, list3);
        this.leftOperand = this.leftOperand.bindExpression(list, list2, list3);
        if (this.rightOperand != null) {
            this.rightOperand = this.rightOperand.bindExpression(list, list2, list3);
        }
        if (this.operatorType == 0) {
            this.trimBind();
        }
        else if (this.operatorType == 1) {
            this.locateBind();
        }
        else if (this.operatorType == 2) {
            this.substrBind();
        }
        else if (this.operatorType == 4) {
            this.timestampAddBind();
        }
        else if (this.operatorType == 5) {
            this.timestampDiffBind();
        }
        return this;
    }
    
    public ValueNode preprocess(final int n, final FromList list, final SubqueryList list2, final PredicateList list3) throws StandardException {
        this.receiver = this.receiver.preprocess(n, list, list2, list3);
        this.leftOperand = this.leftOperand.preprocess(n, list, list2, list3);
        if (this.rightOperand != null) {
            this.rightOperand = this.rightOperand.preprocess(n, list, list2, list3);
        }
        return this;
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        int n = 0;
        String s = null;
        final LocalField fieldDeclaration = expressionClassBuilder.newFieldDeclaration(2, this.resultInterfaceType);
        this.receiver.generateExpression(expressionClassBuilder, methodBuilder);
        if (this.operatorType == 0) {
            methodBuilder.push(this.trimType);
            this.leftOperand.generateExpression(expressionClassBuilder, methodBuilder);
            methodBuilder.cast(this.leftInterfaceType);
            methodBuilder.getField(fieldDeclaration);
            n = 3;
            s = this.receiverInterfaceType;
        }
        else if (this.operatorType == 1) {
            this.leftOperand.generateExpression(expressionClassBuilder, methodBuilder);
            methodBuilder.upCast(this.leftInterfaceType);
            this.rightOperand.generateExpression(expressionClassBuilder, methodBuilder);
            methodBuilder.upCast(this.rightInterfaceType);
            methodBuilder.getField(fieldDeclaration);
            n = 3;
        }
        else if (this.operatorType == 2) {
            this.leftOperand.generateExpression(expressionClassBuilder, methodBuilder);
            methodBuilder.upCast(this.leftInterfaceType);
            if (this.rightOperand != null) {
                this.rightOperand.generateExpression(expressionClassBuilder, methodBuilder);
                methodBuilder.upCast(this.rightInterfaceType);
            }
            else {
                methodBuilder.pushNull(this.rightInterfaceType);
            }
            methodBuilder.getField(fieldDeclaration);
            methodBuilder.push(this.receiver.getTypeServices().getMaximumWidth());
            n = 4;
            s = this.receiverInterfaceType;
        }
        else if (this.operatorType == 4 || this.operatorType == 5) {
            methodBuilder.push((int)this.leftOperand.getConstantValueAsObject());
            this.rightOperand.generateExpression(expressionClassBuilder, methodBuilder);
            methodBuilder.upCast(TernaryOperatorNode.TernaryArgType[this.operatorType][2]);
            expressionClassBuilder.getCurrentDateExpression(methodBuilder);
            methodBuilder.getField(fieldDeclaration);
            n = 4;
            s = this.receiverInterfaceType;
        }
        methodBuilder.callMethod((short)185, s, this.methodName, this.resultInterfaceType, n);
        methodBuilder.putField(fieldDeclaration);
    }
    
    public void setLeftOperand(final ValueNode leftOperand) {
        this.leftOperand = leftOperand;
    }
    
    public ValueNode getLeftOperand() {
        return this.leftOperand;
    }
    
    public void setRightOperand(final ValueNode rightOperand) {
        this.rightOperand = rightOperand;
    }
    
    public ValueNode getRightOperand() {
        return this.rightOperand;
    }
    
    public boolean categorize(final JBitSet set, final boolean b) throws StandardException {
        final boolean categorize = this.receiver.categorize(set, b);
        boolean b2 = this.leftOperand.categorize(set, b) && categorize;
        if (this.rightOperand != null) {
            b2 = (this.rightOperand.categorize(set, b) && b2);
        }
        return b2;
    }
    
    public ValueNode remapColumnReferencesToExpressions() throws StandardException {
        this.receiver = this.receiver.remapColumnReferencesToExpressions();
        this.leftOperand = this.leftOperand.remapColumnReferencesToExpressions();
        if (this.rightOperand != null) {
            this.rightOperand = this.rightOperand.remapColumnReferencesToExpressions();
        }
        return this;
    }
    
    public boolean isConstantExpression() {
        return this.receiver.isConstantExpression() && this.leftOperand.isConstantExpression() && (this.rightOperand == null || this.rightOperand.isConstantExpression());
    }
    
    public boolean constantExpression(final PredicateList list) {
        return this.receiver.constantExpression(list) && this.leftOperand.constantExpression(list) && (this.rightOperand == null || this.rightOperand.constantExpression(list));
    }
    
    void acceptChildren(final Visitor visitor) throws StandardException {
        super.acceptChildren(visitor);
        if (this.receiver != null) {
            this.receiver = (ValueNode)this.receiver.accept(visitor);
        }
        if (this.leftOperand != null) {
            this.leftOperand = (ValueNode)this.leftOperand.accept(visitor);
        }
        if (this.rightOperand != null) {
            this.rightOperand = (ValueNode)this.rightOperand.accept(visitor);
        }
    }
    
    private ValueNode trimBind() throws StandardException {
        TypeId builtInTypeId = TypeId.getBuiltInTypeId(12);
        if (this.receiver.requiresTypeFromContext()) {
            this.receiver.setType(this.getVarcharDescriptor());
            if (!this.leftOperand.requiresTypeFromContext()) {
                this.receiver.setCollationInfo(this.leftOperand.getTypeServices());
            }
            else {
                this.receiver.setCollationUsingCompilationSchema();
            }
        }
        if (this.leftOperand.requiresTypeFromContext()) {
            this.leftOperand.setType(this.getVarcharDescriptor());
            this.leftOperand.setCollationInfo(this.receiver.getTypeServices());
        }
        this.bindToBuiltIn();
        final TypeId typeId = this.receiver.getTypeId();
        if (typeId.userType()) {
            this.throwBadType("trim", typeId.getSQLTypeName());
        }
        this.receiver = this.castArgToString(this.receiver);
        if (typeId.getTypeFormatId() == 444) {
            builtInTypeId = typeId;
        }
        final TypeId typeId2 = this.leftOperand.getTypeId();
        if (typeId2.userType()) {
            this.throwBadType("trim", typeId2.getSQLTypeName());
        }
        this.leftOperand = this.castArgToString(this.leftOperand);
        this.setResultType(builtInTypeId);
        this.setCollationInfo(this.receiver.getTypeServices());
        return this;
    }
    
    private void setResultType(final TypeId typeId) throws StandardException {
        this.setType(new DataTypeDescriptor(typeId, true, this.receiver.getTypeServices().getMaximumWidth()));
    }
    
    public ValueNode locateBind() throws StandardException {
        if (this.receiver.requiresTypeFromContext()) {
            if (this.leftOperand.requiresTypeFromContext()) {
                this.receiver.setType(this.getVarcharDescriptor());
                this.receiver.setCollationUsingCompilationSchema();
            }
            else if (this.leftOperand.getTypeId().isStringTypeId()) {
                this.receiver.setType(this.leftOperand.getTypeServices());
            }
        }
        if (this.leftOperand.requiresTypeFromContext()) {
            if (this.receiver.requiresTypeFromContext()) {
                this.leftOperand.setType(this.getVarcharDescriptor());
            }
            else if (this.receiver.getTypeId().isStringTypeId()) {
                this.leftOperand.setType(this.receiver.getTypeServices());
            }
            this.leftOperand.setCollationInfo(this.receiver.getTypeServices());
        }
        if (this.rightOperand.requiresTypeFromContext()) {
            this.rightOperand.setType(new DataTypeDescriptor(TypeId.INTEGER_ID, true));
        }
        this.bindToBuiltIn();
        final TypeId typeId = this.leftOperand.getTypeId();
        final TypeId typeId2 = this.rightOperand.getTypeId();
        if (!this.receiver.getTypeId().isStringTypeId() || !typeId.isStringTypeId() || typeId2.getJDBCTypeId() != 4) {
            throw StandardException.newException("42884", "LOCATE", "FUNCTION");
        }
        this.setType(new DataTypeDescriptor(TypeId.INTEGER_ID, this.receiver.getTypeServices().isNullable()));
        return this;
    }
    
    protected ValueNode castArgToString(final ValueNode valueNode) throws StandardException {
        final TypeCompiler typeCompiler = valueNode.getTypeCompiler();
        if (!valueNode.getTypeId().isStringTypeId()) {
            final ValueNode valueNode2 = (ValueNode)this.getNodeFactory().getNode(60, valueNode, DataTypeDescriptor.getBuiltInDataTypeDescriptor(12, true, typeCompiler.getCastToCharWidth(valueNode.getTypeServices())), this.getContextManager());
            valueNode2.setCollationUsingCompilationSchema();
            ((CastNode)valueNode2).bindCastNodeOnly();
            return valueNode2;
        }
        return valueNode;
    }
    
    public ValueNode substrBind() throws StandardException {
        TypeId builtInTypeId = TypeId.getBuiltInTypeId(12);
        if (this.receiver.requiresTypeFromContext()) {
            this.receiver.setType(this.getVarcharDescriptor());
            this.receiver.setCollationUsingCompilationSchema();
        }
        if (this.leftOperand.requiresTypeFromContext()) {
            this.leftOperand.setType(new DataTypeDescriptor(TypeId.INTEGER_ID, true));
        }
        if (this.rightOperand != null && this.rightOperand.requiresTypeFromContext()) {
            this.rightOperand.setType(new DataTypeDescriptor(TypeId.INTEGER_ID, true));
        }
        this.bindToBuiltIn();
        if (!this.leftOperand.getTypeId().isNumericTypeId() || (this.rightOperand != null && !this.rightOperand.getTypeId().isNumericTypeId())) {
            throw StandardException.newException("42884", "SUBSTR", "FUNCTION");
        }
        final TypeId typeId = this.receiver.getTypeId();
        switch (typeId.getJDBCTypeId()) {
            case -1:
            case 1:
            case 12:
            case 2005: {
                break;
            }
            default: {
                this.throwBadType("SUBSTR", typeId.getSQLTypeName());
                break;
            }
        }
        if (typeId.getTypeFormatId() == 444) {
            builtInTypeId = typeId;
        }
        int n = this.receiver.getTypeServices().getMaximumWidth();
        if (this.rightOperand != null && this.rightOperand instanceof ConstantNode && ((ConstantNode)this.rightOperand).getValue().getInt() < n) {
            n = ((ConstantNode)this.rightOperand).getValue().getInt();
        }
        this.setType(new DataTypeDescriptor(builtInTypeId, true, n));
        this.setCollationInfo(this.receiver.getTypeServices());
        return this;
    }
    
    private ValueNode timestampAddBind() throws StandardException {
        if (!this.bindParameter(this.rightOperand, 4)) {
            final int jdbcTypeId = this.rightOperand.getTypeId().getJDBCTypeId();
            if (jdbcTypeId != -6 && jdbcTypeId != 5 && jdbcTypeId != 4 && jdbcTypeId != -5) {
                throw StandardException.newException("42X45", this.rightOperand.getTypeId().getSQLTypeName(), ReuseFactory.getInteger(2), this.operator);
            }
        }
        this.bindDateTimeArg(this.receiver, 3);
        this.setType(DataTypeDescriptor.getBuiltInDataTypeDescriptor(93));
        return this;
    }
    
    private ValueNode timestampDiffBind() throws StandardException {
        this.bindDateTimeArg(this.rightOperand, 2);
        this.bindDateTimeArg(this.receiver, 3);
        this.setType(DataTypeDescriptor.getBuiltInDataTypeDescriptor(-5));
        return this;
    }
    
    private void bindDateTimeArg(final ValueNode valueNode, final int n) throws StandardException {
        if (!this.bindParameter(valueNode, 93) && !valueNode.getTypeId().isDateTimeTimeStampTypeId()) {
            throw StandardException.newException("42X45", valueNode.getTypeId().getSQLTypeName(), ReuseFactory.getInteger(n), this.operator);
        }
    }
    
    private boolean bindParameter(final ValueNode valueNode, final int n) throws StandardException {
        if (valueNode.requiresTypeFromContext() && valueNode.getTypeId() == null) {
            valueNode.setType(new DataTypeDescriptor(TypeId.getBuiltInTypeId(n), true));
            return true;
        }
        return false;
    }
    
    public ValueNode getReceiver() {
        return this.receiver;
    }
    
    private void throwBadType(final String s, final String s2) throws StandardException {
        throw StandardException.newException("42X25", s, s2);
    }
    
    protected void bindToBuiltIn() throws StandardException {
        if (this.receiver.getTypeId().userType()) {
            this.receiver = this.receiver.genSQLJavaSQLTree();
        }
        if (this.leftOperand.getTypeId().userType()) {
            this.leftOperand = this.leftOperand.genSQLJavaSQLTree();
        }
        if (this.rightOperand != null && this.rightOperand.getTypeId().userType()) {
            this.rightOperand = this.rightOperand.genSQLJavaSQLTree();
        }
    }
    
    private DataTypeDescriptor getVarcharDescriptor() {
        return new DataTypeDescriptor(TypeId.getBuiltInTypeId(12), true);
    }
    
    protected boolean isEquivalent(final ValueNode valueNode) throws StandardException {
        if (this.isSameNodeType(valueNode)) {
            final TernaryOperatorNode ternaryOperatorNode = (TernaryOperatorNode)valueNode;
            return ternaryOperatorNode.methodName.equals(this.methodName) && ternaryOperatorNode.receiver.isEquivalent(this.receiver) && ternaryOperatorNode.leftOperand.isEquivalent(this.leftOperand) && ((this.rightOperand == null && ternaryOperatorNode.rightOperand == null) || (ternaryOperatorNode.rightOperand != null && ternaryOperatorNode.rightOperand.isEquivalent(this.rightOperand)));
        }
        return false;
    }
    
    static {
        TernaryOperators = new String[] { "trim", "LOCATE", "substring", "like", "TIMESTAMPADD", "TIMESTAMPDIFF" };
        TernaryMethodNames = new String[] { "ansiTrim", "locate", "substring", "like", "timestampAdd", "timestampDiff" };
        TernaryResultType = new String[] { "org.apache.derby.iapi.types.StringDataValue", "org.apache.derby.iapi.types.NumberDataValue", "org.apache.derby.iapi.types.ConcatableDataValue", "org.apache.derby.iapi.types.BooleanDataValue", "org.apache.derby.iapi.types.DateTimeDataValue", "org.apache.derby.iapi.types.NumberDataValue" };
        TernaryArgType = new String[][] { { "org.apache.derby.iapi.types.StringDataValue", "org.apache.derby.iapi.types.StringDataValue", "java.lang.Integer" }, { "org.apache.derby.iapi.types.StringDataValue", "org.apache.derby.iapi.types.StringDataValue", "org.apache.derby.iapi.types.NumberDataValue" }, { "org.apache.derby.iapi.types.ConcatableDataValue", "org.apache.derby.iapi.types.NumberDataValue", "org.apache.derby.iapi.types.NumberDataValue" }, { "org.apache.derby.iapi.types.DataValueDescriptor", "org.apache.derby.iapi.types.DataValueDescriptor", "org.apache.derby.iapi.types.DataValueDescriptor" }, { "org.apache.derby.iapi.types.DateTimeDataValue", "java.lang.Integer", "org.apache.derby.iapi.types.NumberDataValue" }, { "org.apache.derby.iapi.types.DateTimeDataValue", "java.lang.Integer", "org.apache.derby.iapi.types.DateTimeDataValue" } };
    }
}
