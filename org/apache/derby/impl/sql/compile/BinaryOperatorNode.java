// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.iapi.services.compiler.LocalField;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.error.StandardException;
import java.util.List;

public class BinaryOperatorNode extends OperatorNode
{
    String operator;
    String methodName;
    ValueNode receiver;
    public static final int PLUS = 1;
    public static final int MINUS = 2;
    public static final int TIMES = 3;
    public static final int DIVIDE = 4;
    public static final int CONCATENATE = 5;
    public static final int EQ = 6;
    public static final int NE = 7;
    public static final int GT = 8;
    public static final int GE = 9;
    public static final int LT = 10;
    public static final int LE = 11;
    public static final int AND = 12;
    public static final int OR = 13;
    public static final int LIKE = 14;
    ValueNode leftOperand;
    ValueNode rightOperand;
    String leftInterfaceType;
    String rightInterfaceType;
    String resultInterfaceType;
    int operatorType;
    public static final int XMLEXISTS_OP = 0;
    public static final int XMLQUERY_OP = 1;
    static final String[] BinaryOperators;
    static final String[] BinaryMethodNames;
    static final String[] BinaryResultTypes;
    static final String[][] BinaryArgTypes;
    private String xmlQuery;
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6) {
        this.leftOperand = (ValueNode)o;
        this.rightOperand = (ValueNode)o2;
        this.operator = (String)o3;
        this.methodName = (String)o4;
        this.leftInterfaceType = (String)o5;
        this.rightInterfaceType = (String)o6;
        this.operatorType = -1;
    }
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4) {
        this.leftOperand = (ValueNode)o;
        this.rightOperand = (ValueNode)o2;
        this.leftInterfaceType = (String)o3;
        this.rightInterfaceType = (String)o4;
        this.operatorType = -1;
    }
    
    public void init(final Object o, final Object o2, final Object o3) {
        this.leftOperand = (ValueNode)o;
        this.rightOperand = (ValueNode)o2;
        this.operatorType = (int)o3;
        this.operator = BinaryOperatorNode.BinaryOperators[this.operatorType];
        this.methodName = BinaryOperatorNode.BinaryMethodNames[this.operatorType];
        this.leftInterfaceType = BinaryOperatorNode.BinaryArgTypes[this.operatorType][0];
        this.rightInterfaceType = BinaryOperatorNode.BinaryArgTypes[this.operatorType][1];
        this.resultInterfaceType = BinaryOperatorNode.BinaryResultTypes[this.operatorType];
    }
    
    public String toString() {
        return "";
    }
    
    void setOperator(final String operator) {
        this.operator = operator;
        this.operatorType = -1;
    }
    
    void setMethodName(final String methodName) {
        this.methodName = methodName;
        this.operatorType = -1;
    }
    
    public void setLeftRightInterfaceType(final String s) {
        this.leftInterfaceType = s;
        this.rightInterfaceType = s;
        this.operatorType = -1;
    }
    
    public void printSubNodes(final int n) {
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        this.leftOperand = this.leftOperand.bindExpression(list, list2, list3);
        this.rightOperand = this.rightOperand.bindExpression(list, list2, list3);
        if (this.operatorType == 0 || this.operatorType == 1) {
            return this.bindXMLQuery();
        }
        if (this.leftOperand.requiresTypeFromContext()) {
            if (this.rightOperand.requiresTypeFromContext()) {
                throw StandardException.newException("42X35", this.operator);
            }
            this.leftOperand.setType(this.rightOperand.getTypeServices());
        }
        if (this.rightOperand.requiresTypeFromContext()) {
            this.rightOperand.setType(this.leftOperand.getTypeServices());
        }
        return this.genSQLJavaSQLTree();
    }
    
    public ValueNode bindXMLQuery() throws StandardException {
        this.leftOperand.getTypeId();
        final TypeId typeId = this.rightOperand.getTypeId();
        if (!(this.leftOperand instanceof CharConstantNode)) {
            throw StandardException.newException("42Z75");
        }
        this.xmlQuery = ((CharConstantNode)this.leftOperand).getString();
        if (typeId != null && !typeId.isXMLTypeId()) {
            throw StandardException.newException("42Z77", typeId.getSQLTypeName());
        }
        if (this.rightOperand.requiresTypeFromContext()) {
            throw StandardException.newException("42Z70");
        }
        if (this.operatorType == 0) {
            this.setType(new DataTypeDescriptor(TypeId.BOOLEAN_ID, true));
        }
        else {
            this.setType(DataTypeDescriptor.getBuiltInDataTypeDescriptor(2009));
        }
        return this.genSQLJavaSQLTree();
    }
    
    public ValueNode genSQLJavaSQLTree() throws StandardException {
        if (this.leftOperand.getTypeId().userType()) {
            this.leftOperand = this.leftOperand.genSQLJavaSQLTree();
        }
        if (this.rightOperand.getTypeId().userType()) {
            this.rightOperand = this.rightOperand.genSQLJavaSQLTree();
        }
        return this;
    }
    
    public ValueNode preprocess(final int n, final FromList list, final SubqueryList list2, final PredicateList list3) throws StandardException {
        this.leftOperand = this.leftOperand.preprocess(n, list, list2, list3);
        this.rightOperand = this.rightOperand.preprocess(n, list, list2, list3);
        return this;
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        if (this instanceof BinaryRelationalOperatorNode) {
            final InListOperatorNode inListOp = ((BinaryRelationalOperatorNode)this).getInListOp();
            if (inListOp != null) {
                inListOp.generateExpression(expressionClassBuilder, methodBuilder);
                return;
            }
        }
        final boolean b = this.operatorType == 1 || this.operatorType == 0;
        String s;
        int n;
        if (this.leftOperand.getTypeId().typePrecedence() > this.rightOperand.getTypeId().typePrecedence()) {
            this.receiver = this.leftOperand;
            s = ((this.operatorType == -1) ? this.getReceiverInterfaceName() : this.leftInterfaceType);
            this.leftOperand.generateExpression(expressionClassBuilder, methodBuilder);
            methodBuilder.cast(s);
            methodBuilder.dup();
            methodBuilder.cast(this.leftInterfaceType);
            this.rightOperand.generateExpression(expressionClassBuilder, methodBuilder);
            methodBuilder.cast(this.rightInterfaceType);
            n = 2;
        }
        else {
            this.receiver = this.rightOperand;
            s = ((this.operatorType == -1) ? this.getReceiverInterfaceName() : this.rightInterfaceType);
            this.rightOperand.generateExpression(expressionClassBuilder, methodBuilder);
            methodBuilder.cast(s);
            if (b) {
                n = 1;
                OperatorNode.pushSqlXmlUtil(expressionClassBuilder, methodBuilder, this.xmlQuery, this.operator);
            }
            else {
                n = 2;
                methodBuilder.dup();
                methodBuilder.cast(this.rightInterfaceType);
                this.leftOperand.generateExpression(expressionClassBuilder, methodBuilder);
                methodBuilder.cast(this.leftInterfaceType);
                methodBuilder.swap();
            }
        }
        final String s2 = (this.operatorType == -1) ? this.getTypeCompiler().interfaceName() : this.resultInterfaceType;
        final LocalField localField = this.getTypeId().isBooleanTypeId() ? null : expressionClassBuilder.newFieldDeclaration(2, s2);
        if (localField != null) {
            methodBuilder.getField(localField);
            ++n;
            final int jdbcTypeId;
            if (this.getTypeServices() != null && ((jdbcTypeId = this.getTypeServices().getJDBCTypeId()) == 3 || jdbcTypeId == 2) && this.operator.equals("/")) {
                methodBuilder.push(this.getTypeServices().getScale());
                ++n;
            }
        }
        methodBuilder.callMethod((short)185, s, this.methodName, s2, n);
        if (localField != null) {
            if (this.getTypeId().variableLength() && this.getTypeId().isNumericTypeId()) {
                methodBuilder.dup();
                methodBuilder.push(this.getTypeServices().getPrecision());
                methodBuilder.push(this.getTypeServices().getScale());
                methodBuilder.push(true);
                methodBuilder.callMethod((short)185, "org.apache.derby.iapi.types.VariableSizeDataValue", "setWidth", "void", 3);
            }
            methodBuilder.putField(localField);
        }
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
        final boolean categorize = this.leftOperand.categorize(set, b);
        return this.rightOperand.categorize(set, b) && categorize;
    }
    
    public ValueNode remapColumnReferencesToExpressions() throws StandardException {
        this.leftOperand = this.leftOperand.remapColumnReferencesToExpressions();
        this.rightOperand = this.rightOperand.remapColumnReferencesToExpressions();
        return this;
    }
    
    public boolean isConstantExpression() {
        return this.leftOperand.isConstantExpression() && this.rightOperand.isConstantExpression();
    }
    
    public boolean constantExpression(final PredicateList list) {
        return this.leftOperand.constantExpression(list) && this.rightOperand.constantExpression(list);
    }
    
    public String getReceiverInterfaceName() throws StandardException {
        return this.receiver.getTypeCompiler().interfaceName();
    }
    
    protected int getOrderableVariantType() throws StandardException {
        return Math.min(this.leftOperand.getOrderableVariantType(), this.rightOperand.getOrderableVariantType());
    }
    
    void acceptChildren(final Visitor visitor) throws StandardException {
        super.acceptChildren(visitor);
        if (this.leftOperand != null) {
            this.leftOperand = (ValueNode)this.leftOperand.accept(visitor);
        }
        if (this.rightOperand != null) {
            this.rightOperand = (ValueNode)this.rightOperand.accept(visitor);
        }
    }
    
    protected boolean isEquivalent(final ValueNode valueNode) throws StandardException {
        if (!this.isSameNodeType(valueNode)) {
            return false;
        }
        final BinaryOperatorNode binaryOperatorNode = (BinaryOperatorNode)valueNode;
        return this.methodName.equals(binaryOperatorNode.methodName) && this.leftOperand.isEquivalent(binaryOperatorNode.leftOperand) && this.rightOperand.isEquivalent(binaryOperatorNode.rightOperand);
    }
    
    static {
        BinaryOperators = new String[] { "xmlexists", "xmlquery" };
        BinaryMethodNames = new String[] { "XMLExists", "XMLQuery" };
        BinaryResultTypes = new String[] { "org.apache.derby.iapi.types.BooleanDataValue", "org.apache.derby.iapi.types.XMLDataValue" };
        BinaryArgTypes = new String[][] { { "org.apache.derby.iapi.types.StringDataValue", "org.apache.derby.iapi.types.XMLDataValue" }, { "org.apache.derby.iapi.types.StringDataValue", "org.apache.derby.iapi.types.XMLDataValue" } };
    }
}
