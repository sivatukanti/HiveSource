// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.error.StandardException;
import java.util.List;

public abstract class BinaryListOperatorNode extends ValueNode
{
    String methodName;
    String operator;
    String leftInterfaceType;
    String rightInterfaceType;
    ValueNode receiver;
    ValueNode leftOperand;
    ValueNodeList rightOperandList;
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4) {
        this.leftOperand = (ValueNode)o;
        this.rightOperandList = (ValueNodeList)o2;
        this.operator = (String)o3;
        this.methodName = (String)o4;
    }
    
    public String toString() {
        return "";
    }
    
    public void printSubNodes(final int n) {
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        this.leftOperand = this.leftOperand.bindExpression(list, list2, list3);
        this.rightOperandList.bindExpression(list, list2, list3);
        if (this.leftOperand.requiresTypeFromContext()) {
            if (this.rightOperandList.containsAllParameterNodes()) {
                throw StandardException.newException("42X35", this.operator);
            }
            this.leftOperand.setType(this.rightOperandList.getTypeServices());
        }
        if (this.rightOperandList.containsParameterNode()) {
            this.rightOperandList.setParameterDescriptor(this.leftOperand.getTypeServices());
        }
        if (this.leftOperand.getTypeId().userType()) {
            this.leftOperand = this.leftOperand.genSQLJavaSQLTree();
        }
        this.rightOperandList.genSQLJavaSQLTrees();
        this.bindComparisonOperator();
        return this;
    }
    
    public void bindComparisonOperator() throws StandardException {
        this.rightOperandList.comparable(this.leftOperand);
        this.setType(new DataTypeDescriptor(TypeId.BOOLEAN_ID, this.leftOperand.getTypeServices().isNullable() || this.rightOperandList.isNullable()));
    }
    
    public ValueNode preprocess(final int n, final FromList list, final SubqueryList list2, final PredicateList list3) throws StandardException {
        this.leftOperand = this.leftOperand.preprocess(n, list, list2, list3);
        this.rightOperandList.preprocess(n, list, list2, list3);
        return this;
    }
    
    public void setLeftOperand(final ValueNode leftOperand) {
        this.leftOperand = leftOperand;
    }
    
    public ValueNode getLeftOperand() {
        return this.leftOperand;
    }
    
    public void setRightOperandList(final ValueNodeList rightOperandList) {
        this.rightOperandList = rightOperandList;
    }
    
    public ValueNodeList getRightOperandList() {
        return this.rightOperandList;
    }
    
    public boolean categorize(final JBitSet set, final boolean b) throws StandardException {
        final boolean categorize = this.leftOperand.categorize(set, b);
        return this.rightOperandList.categorize(set, b) && categorize;
    }
    
    public ValueNode remapColumnReferencesToExpressions() throws StandardException {
        this.leftOperand = this.leftOperand.remapColumnReferencesToExpressions();
        this.rightOperandList.remapColumnReferencesToExpressions();
        return this;
    }
    
    public boolean isConstantExpression() {
        return this.leftOperand.isConstantExpression() && this.rightOperandList.isConstantExpression();
    }
    
    public boolean constantExpression(final PredicateList list) {
        return this.leftOperand.constantExpression(list) && this.rightOperandList.constantExpression(list);
    }
    
    protected int getOrderableVariantType() throws StandardException {
        return Math.min(this.leftOperand.getOrderableVariantType(), this.rightOperandList.getOrderableVariantType());
    }
    
    void acceptChildren(final Visitor visitor) throws StandardException {
        super.acceptChildren(visitor);
        if (this.leftOperand != null) {
            this.leftOperand = (ValueNode)this.leftOperand.accept(visitor);
        }
        if (this.rightOperandList != null) {
            this.rightOperandList = (ValueNodeList)this.rightOperandList.accept(visitor);
        }
    }
    
    protected boolean isEquivalent(final ValueNode valueNode) throws StandardException {
        if (!this.isSameNodeType(valueNode)) {
            return false;
        }
        final BinaryListOperatorNode binaryListOperatorNode = (BinaryListOperatorNode)valueNode;
        return this.operator.equals(binaryListOperatorNode.operator) && this.leftOperand.isEquivalent(binaryListOperatorNode.getLeftOperand()) && this.rightOperandList.isEquivalent(binaryListOperatorNode.rightOperandList);
    }
}
