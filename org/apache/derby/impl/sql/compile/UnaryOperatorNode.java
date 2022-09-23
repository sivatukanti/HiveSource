// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.services.compiler.LocalField;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import java.util.List;
import org.apache.derby.iapi.error.StandardException;

public class UnaryOperatorNode extends OperatorNode
{
    String operator;
    String methodName;
    private int operatorType;
    String resultInterfaceType;
    String receiverInterfaceType;
    ValueNode operand;
    public static final int XMLPARSE_OP = 0;
    public static final int XMLSERIALIZE_OP = 1;
    static final String[] UnaryOperators;
    static final String[] UnaryMethodNames;
    static final String[] UnaryResultTypes;
    static final String[] UnaryArgTypes;
    private Object[] additionalArgs;
    
    public void init(final Object o, final Object o2, final Object o3) {
        this.operand = (ValueNode)o;
        if (o2 instanceof String) {
            this.operator = (String)o2;
            this.methodName = (String)o3;
            this.operatorType = -1;
        }
        else {
            this.operatorType = (int)o2;
            this.operator = UnaryOperatorNode.UnaryOperators[this.operatorType];
            this.methodName = UnaryOperatorNode.UnaryMethodNames[this.operatorType];
            this.resultInterfaceType = UnaryOperatorNode.UnaryResultTypes[this.operatorType];
            this.receiverInterfaceType = UnaryOperatorNode.UnaryArgTypes[this.operatorType];
            this.additionalArgs = (Object[])o3;
        }
    }
    
    public void init(final Object o) {
        this.operand = (ValueNode)o;
        this.operatorType = -1;
    }
    
    void setOperator(final String operator) {
        this.operator = operator;
        this.operatorType = -1;
    }
    
    String getOperatorString() {
        return this.operator;
    }
    
    void setMethodName(final String methodName) {
        this.methodName = methodName;
        this.operatorType = -1;
    }
    
    public String toString() {
        return "";
    }
    
    public void printSubNodes(final int n) {
    }
    
    public ValueNode getOperand() {
        return this.operand;
    }
    
    public ParameterNode getParameterOperand() throws StandardException {
        if (!this.requiresTypeFromContext()) {
            return null;
        }
        UnaryOperatorNode unaryOperatorNode;
        for (unaryOperatorNode = this; !(unaryOperatorNode.getOperand() instanceof ParameterNode); unaryOperatorNode = (UnaryOperatorNode)unaryOperatorNode.getOperand()) {}
        return (ParameterNode)unaryOperatorNode.getOperand();
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        this.bindOperand(list, list2, list3);
        if (this.operatorType == 0) {
            this.bindXMLParse();
        }
        else if (this.operatorType == 1) {
            this.bindXMLSerialize();
        }
        return this;
    }
    
    protected void bindOperand(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        this.operand = this.operand.bindExpression(list, list2, list3);
        if (this.operand.requiresTypeFromContext()) {
            this.bindParameter();
            if (this.operand.getTypeServices() == null) {
                return;
            }
        }
        if (!(this.operand instanceof UntypedNullConstantNode) && this.operand.getTypeId().userType() && !(this instanceof IsNullNode)) {
            this.operand = this.operand.genSQLJavaSQLTree();
        }
    }
    
    private void bindXMLParse() throws StandardException {
        final TypeId typeId = this.operand.getTypeId();
        if (typeId != null) {
            switch (typeId.getJDBCTypeId()) {
                case -1:
                case 1:
                case 12:
                case 2005: {
                    break;
                }
                default: {
                    throw StandardException.newException("42X25", this.methodName, typeId.getSQLTypeName());
                }
            }
        }
        this.setType(DataTypeDescriptor.getBuiltInDataTypeDescriptor(2009));
    }
    
    private void bindXMLSerialize() throws StandardException {
        final TypeId typeId = this.operand.getTypeId();
        if (typeId != null && !typeId.isXMLTypeId()) {
            throw StandardException.newException("42X25", this.methodName, typeId.getSQLTypeName());
        }
        final DataTypeDescriptor type = (DataTypeDescriptor)this.additionalArgs[0];
        final TypeId typeId2 = type.getTypeId();
        switch (typeId2.getJDBCTypeId()) {
            case -1:
            case 1:
            case 12:
            case 2005: {
                this.setType(type);
                this.setCollationUsingCompilationSchema();
            }
            default: {
                throw StandardException.newException("42Z73", typeId2.getSQLTypeName());
            }
        }
    }
    
    public ValueNode preprocess(final int n, final FromList list, final SubqueryList list2, final PredicateList list3) throws StandardException {
        if (this.operand != null) {
            this.operand = this.operand.preprocess(n, list, list2, list3);
        }
        return this;
    }
    
    public boolean categorize(final JBitSet set, final boolean b) throws StandardException {
        return this.operand != null && this.operand.categorize(set, b);
    }
    
    public ValueNode remapColumnReferencesToExpressions() throws StandardException {
        if (this.operand != null) {
            this.operand = this.operand.remapColumnReferencesToExpressions();
        }
        return this;
    }
    
    public boolean isConstantExpression() {
        return this.operand == null || this.operand.isConstantExpression();
    }
    
    public boolean constantExpression(final PredicateList list) {
        return this.operand == null || this.operand.constantExpression(list);
    }
    
    void bindParameter() throws StandardException {
        if (this.operatorType == 0) {
            throw StandardException.newException("42Z79");
        }
        if (this.operatorType == 1) {
            throw StandardException.newException("42Z70");
        }
        if (this.operand.getTypeServices() == null) {
            throw StandardException.newException("42X36", this.operator);
        }
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        final String s = (this.operatorType == -1) ? this.getTypeCompiler().interfaceName() : this.resultInterfaceType;
        final boolean b = !this.getTypeId().isBooleanTypeId();
        final String receiverInterfaceName = this.getReceiverInterfaceName();
        this.operand.generateExpression(expressionClassBuilder, methodBuilder);
        methodBuilder.cast(receiverInterfaceName);
        if (b) {
            final LocalField fieldDeclaration = expressionClassBuilder.newFieldDeclaration(2, s);
            methodBuilder.getField(fieldDeclaration);
            methodBuilder.callMethod((short)185, null, this.methodName, s, 1 + this.addXmlOpMethodParams(expressionClassBuilder, methodBuilder, fieldDeclaration));
            methodBuilder.putField(fieldDeclaration);
        }
        else {
            methodBuilder.callMethod((short)185, null, this.methodName, s, 0);
        }
    }
    
    public String getReceiverInterfaceName() throws StandardException {
        if (this.operatorType != -1) {
            return this.receiverInterfaceType;
        }
        return this.operand.getTypeCompiler().interfaceName();
    }
    
    protected int getOrderableVariantType() throws StandardException {
        return (this.operand != null) ? this.operand.getOrderableVariantType() : 3;
    }
    
    void acceptChildren(final Visitor visitor) throws StandardException {
        super.acceptChildren(visitor);
        if (this.operand != null) {
            this.operand = (ValueNode)this.operand.accept(visitor);
        }
    }
    
    protected int addXmlOpMethodParams(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder, final LocalField field) throws StandardException {
        if (this.operatorType != 0 && this.operatorType != 1) {
            return 0;
        }
        if (this.operatorType == 1) {
            final DataTypeDescriptor dataTypeDescriptor = (DataTypeDescriptor)this.additionalArgs[0];
            methodBuilder.push(dataTypeDescriptor.getJDBCTypeId());
            methodBuilder.push(dataTypeDescriptor.getMaximumWidth());
            methodBuilder.push(this.getSchemaDescriptor(null, false).getCollationType());
            return 3;
        }
        final MethodBuilder constructor = expressionClassBuilder.getConstructor();
        expressionClassBuilder.generateNull(constructor, this.getTypeCompiler(), this.getTypeServices().getCollationType());
        constructor.setField(field);
        methodBuilder.swap();
        methodBuilder.push((boolean)this.additionalArgs[0]);
        OperatorNode.pushSqlXmlUtil(expressionClassBuilder, methodBuilder, null, null);
        return 2;
    }
    
    protected boolean isEquivalent(final ValueNode valueNode) throws StandardException {
        if (this.isSameNodeType(valueNode)) {
            final UnaryOperatorNode unaryOperatorNode = (UnaryOperatorNode)valueNode;
            return this.operator.equals(unaryOperatorNode.operator) && (this.operand == unaryOperatorNode.operand || (this.operand != null && this.operand.isEquivalent(unaryOperatorNode.operand)));
        }
        return false;
    }
    
    static {
        UnaryOperators = new String[] { "xmlparse", "xmlserialize" };
        UnaryMethodNames = new String[] { "XMLParse", "XMLSerialize" };
        UnaryResultTypes = new String[] { "org.apache.derby.iapi.types.XMLDataValue", "org.apache.derby.iapi.types.StringDataValue" };
        UnaryArgTypes = new String[] { "org.apache.derby.iapi.types.StringDataValue", "org.apache.derby.iapi.types.XMLDataValue" };
    }
}
