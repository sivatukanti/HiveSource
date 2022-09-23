// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.iapi.services.loader.ClassInspector;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import org.apache.derby.iapi.types.TypeId;
import java.util.List;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.error.StandardException;

public class ConditionalNode extends ValueNode
{
    ValueNode testCondition;
    ValueNodeList thenElseList;
    boolean thisIsNullIfNode;
    
    public void init(final Object o, final Object o2, final Object o3) {
        this.testCondition = (ValueNode)o;
        this.thenElseList = (ValueNodeList)o2;
        this.thisIsNullIfNode = (boolean)o3;
    }
    
    public void printSubNodes(final int n) {
    }
    
    private boolean isCastNode(final ValueNode valueNode) {
        return valueNode.getNodeType() == 60;
    }
    
    private boolean isCastToChar(final ValueNode valueNode) throws StandardException {
        return valueNode.getTypeServices().getTypeName().equals("CHAR");
    }
    
    private boolean isNullNode(final ValueNode valueNode) {
        return this.isCastNode(valueNode) && ((CastNode)valueNode).castOperand instanceof UntypedNullConstantNode;
    }
    
    private boolean isConditionalNode(final ValueNode valueNode) {
        return valueNode.getNodeType() == 54;
    }
    
    private boolean shouldCast(final DataTypeDescriptor dataTypeDescriptor, final DataTypeDescriptor dataTypeDescriptor2) throws StandardException {
        return dataTypeDescriptor != null && (dataTypeDescriptor2 == null || !dataTypeDescriptor2.getTypeId().equals(dataTypeDescriptor.getTypeId()));
    }
    
    private DataTypeDescriptor findType(final ValueNodeList list, final FromList list2, final SubqueryList list3, final List list4) throws StandardException {
        final ValueNode bindExpression = ((ValueNode)list.elementAt(0)).bindExpression(list2, list3, list4);
        list.setElementAt(bindExpression, 0);
        final ValueNode bindExpression2 = ((ValueNode)list.elementAt(1)).bindExpression(list2, list3, list4);
        list.setElementAt(bindExpression2, 1);
        final DataTypeDescriptor typeServices = bindExpression.getTypeServices();
        final DataTypeDescriptor typeServices2 = bindExpression2.getTypeServices();
        DataTypeDescriptor dataTypeDescriptor = null;
        if (typeServices != null && !this.isCastNode(bindExpression) && !this.isConditionalNode(bindExpression)) {
            return typeServices;
        }
        if (this.isCastNode(bindExpression) && !this.isCastToChar(bindExpression)) {
            return bindExpression.getTypeServices();
        }
        if (typeServices2 != null && !this.isCastNode(bindExpression2) && !this.isConditionalNode(bindExpression2)) {
            return typeServices2;
        }
        if (this.isCastNode(bindExpression2) && !this.isCastToChar(bindExpression2)) {
            return bindExpression2.getTypeServices();
        }
        if (this.isConditionalNode(bindExpression)) {
            dataTypeDescriptor = this.findType(((ConditionalNode)bindExpression).thenElseList, list2, list3, list4);
        }
        if (dataTypeDescriptor != null) {
            return dataTypeDescriptor;
        }
        if (this.isConditionalNode(bindExpression2)) {
            dataTypeDescriptor = this.findType(((ConditionalNode)bindExpression2).thenElseList, list2, list3, list4);
        }
        if (dataTypeDescriptor != null) {
            return dataTypeDescriptor;
        }
        return null;
    }
    
    private void recastNullNodes(final ValueNodeList list, DataTypeDescriptor nullabilityType, final FromList list2, final SubqueryList list3, final List list4) throws StandardException {
        if (nullabilityType == null) {
            return;
        }
        nullabilityType = nullabilityType.getNullabilityType(true);
        final ValueNode valueNode = (ValueNode)list.elementAt(0);
        final ValueNode valueNode2 = (ValueNode)list.elementAt(1);
        if (this.isNullNode(valueNode) && this.shouldCast(nullabilityType, valueNode.getTypeServices())) {
            list.setElementAt(this.recastNullNode(valueNode, nullabilityType), 0);
            ((ValueNode)list.elementAt(0)).bindExpression(list2, list3, list4);
        }
        else if (this.isConditionalNode(valueNode)) {
            this.recastNullNodes(((ConditionalNode)valueNode).thenElseList, nullabilityType, list2, list3, list4);
        }
        if (this.isNullNode(valueNode2) && this.shouldCast(nullabilityType, valueNode2.getTypeServices())) {
            list.setElementAt(this.recastNullNode(valueNode2, nullabilityType), 1);
            ((ValueNode)list.elementAt(1)).bindExpression(list2, list3, list4);
        }
        else if (this.isConditionalNode(valueNode2)) {
            this.recastNullNodes(((ConditionalNode)valueNode2).thenElseList, nullabilityType, list2, list3, list4);
        }
    }
    
    private QueryTreeNode recastNullNode(final ValueNode valueNode, final DataTypeDescriptor dataTypeDescriptor) throws StandardException {
        return (QueryTreeNode)this.getNodeFactory().getNode(60, ((CastNode)valueNode).castOperand, dataTypeDescriptor, this.getContextManager());
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        final CompilerContext compilerContext = this.getCompilerContext();
        final int orReliability = this.orReliability(16384);
        this.testCondition = this.testCondition.bindExpression(list, list2, list3);
        if (this.thisIsNullIfNode) {
            this.thenElseList.setElementAt((QueryTreeNode)this.getNodeFactory().getNode(60, this.thenElseList.elementAt(0), ((BinaryComparisonOperatorNode)this.testCondition).getLeftOperand().getTypeServices().getNullabilityType(true), this.getContextManager()), 0);
            this.thenElseList.bindExpression(list, list2, list3);
        }
        else {
            this.recastNullNodes(this.thenElseList, this.findType(this.thenElseList, list, list2, list3), list, list2, list3);
        }
        final ValueNode valueNode = (ValueNode)this.thenElseList.elementAt(0);
        final ValueNode valueNode2 = (ValueNode)this.thenElseList.elementAt(1);
        if (this.testCondition.requiresTypeFromContext()) {
            this.testCondition.setType(new DataTypeDescriptor(TypeId.BOOLEAN_ID, true));
        }
        else if (!this.testCondition.getTypeServices().getTypeId().equals(TypeId.BOOLEAN_ID)) {
            throw StandardException.newException("42X88");
        }
        if (this.thenElseList.containsAllParameterNodes()) {
            throw StandardException.newException("42X87", "conditional");
        }
        if (this.thenElseList.containsParameterNode()) {
            DataTypeDescriptor parameterDescriptor;
            if (valueNode.requiresTypeFromContext()) {
                parameterDescriptor = valueNode2.getTypeServices();
            }
            else {
                parameterDescriptor = valueNode.getTypeServices();
            }
            this.thenElseList.setParameterDescriptor(parameterDescriptor);
        }
        final ClassInspector classInspector = this.getClassFactory().getClassInspector();
        if (!valueNode.getTypeServices().comparable(valueNode2.getTypeServices(), false, this.getClassFactory()) && !classInspector.assignableTo(valueNode.getTypeId().getCorrespondingJavaTypeName(), valueNode2.getTypeId().getCorrespondingJavaTypeName()) && !classInspector.assignableTo(valueNode2.getTypeId().getCorrespondingJavaTypeName(), valueNode.getTypeId().getCorrespondingJavaTypeName())) {
            throw StandardException.newException("42X89", valueNode.getTypeId().getSQLTypeName(), valueNode2.getTypeId().getSQLTypeName());
        }
        this.setType(this.thenElseList.getDominantTypeServices());
        final TypeId typeId = this.getTypeId();
        final TypeId typeId2 = ((ValueNode)this.thenElseList.elementAt(0)).getTypeId();
        final TypeId typeId3 = ((ValueNode)this.thenElseList.elementAt(1)).getTypeId();
        if (typeId2.typePrecedence() != typeId.typePrecedence()) {
            this.thenElseList.setElementAt(((ValueNode)this.getNodeFactory().getNode(60, this.thenElseList.elementAt(0), this.getTypeServices(), this.getContextManager())).bindExpression(list, list2, list3), 0);
        }
        else if (typeId3.typePrecedence() != typeId.typePrecedence()) {
            this.thenElseList.setElementAt(((ValueNode)this.getNodeFactory().getNode(60, this.thenElseList.elementAt(1), this.getTypeServices(), this.getContextManager())).bindExpression(list, list2, list3), 1);
        }
        compilerContext.setReliability(orReliability);
        return this;
    }
    
    public ValueNode preprocess(final int n, final FromList list, final SubqueryList list2, final PredicateList list3) throws StandardException {
        this.testCondition = this.testCondition.preprocess(n, list, list2, list3);
        this.thenElseList.preprocess(n, list, list2, list3);
        return this;
    }
    
    public boolean categorize(final JBitSet set, final boolean b) throws StandardException {
        if (b) {
            return false;
        }
        final boolean categorize = this.testCondition.categorize(set, b);
        return this.thenElseList.categorize(set, b) && categorize;
    }
    
    public ValueNode remapColumnReferencesToExpressions() throws StandardException {
        this.testCondition = this.testCondition.remapColumnReferencesToExpressions();
        this.thenElseList = this.thenElseList.remapColumnReferencesToExpressions();
        return this;
    }
    
    public boolean isConstantExpression() {
        return this.testCondition.isConstantExpression() && this.thenElseList.isConstantExpression();
    }
    
    public boolean constantExpression(final PredicateList list) {
        return this.testCondition.constantExpression(list) && this.thenElseList.constantExpression(list);
    }
    
    ValueNode eliminateNots(final boolean b) throws StandardException {
        if (!b) {
            return this;
        }
        final ValueNode valueNode = (ValueNode)this.thenElseList.elementAt(0);
        this.thenElseList.setElementAt(this.thenElseList.elementAt(1), 0);
        this.thenElseList.setElementAt(valueNode, 1);
        return this;
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        this.testCondition.generateExpression(expressionClassBuilder, methodBuilder);
        methodBuilder.cast("org.apache.derby.iapi.types.BooleanDataValue");
        methodBuilder.push(true);
        methodBuilder.callMethod((short)185, null, "equals", "boolean", 1);
        methodBuilder.conditionalIf();
        ((ValueNode)this.thenElseList.elementAt(0)).generateExpression(expressionClassBuilder, methodBuilder);
        methodBuilder.startElseCode();
        ((ValueNode)this.thenElseList.elementAt(1)).generateExpression(expressionClassBuilder, methodBuilder);
        methodBuilder.completeConditional();
    }
    
    void acceptChildren(final Visitor visitor) throws StandardException {
        super.acceptChildren(visitor);
        if (this.testCondition != null) {
            this.testCondition = (ValueNode)this.testCondition.accept(visitor);
        }
        if (this.thenElseList != null) {
            this.thenElseList = (ValueNodeList)this.thenElseList.accept(visitor);
        }
    }
    
    protected boolean isEquivalent(final ValueNode valueNode) throws StandardException {
        if (this.isSameNodeType(valueNode)) {
            final ConditionalNode conditionalNode = (ConditionalNode)valueNode;
            return this.testCondition.isEquivalent(conditionalNode.testCondition) && this.thenElseList.isEquivalent(conditionalNode.thenElseList);
        }
        return false;
    }
}
