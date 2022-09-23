// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.sql.compile.Optimizable;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataValueDescriptor;

public final class IsNullNode extends UnaryComparisonOperatorNode implements RelationalOperator
{
    private DataValueDescriptor nullValue;
    
    public void setNodeType(final int nodeType) {
        String operator;
        String methodName;
        if (nodeType == 25) {
            operator = "is null";
            methodName = "isNullOp";
        }
        else {
            operator = "is not null";
            methodName = "isNotNull";
        }
        this.setOperator(operator);
        this.setMethodName(methodName);
        super.setNodeType(nodeType);
    }
    
    UnaryOperatorNode getNegation(final ValueNode valueNode) throws StandardException {
        if (this.isNullNode()) {
            this.setNodeType(24);
        }
        else {
            this.setNodeType(25);
        }
        return this;
    }
    
    void bindParameter() throws StandardException {
        this.operand.setType(new DataTypeDescriptor(TypeId.getBuiltInTypeId(12), true));
    }
    
    public boolean usefulStartKey(final Optimizable optimizable) {
        return this.isNullNode();
    }
    
    public boolean usefulStopKey(final Optimizable optimizable) {
        return this.isNullNode();
    }
    
    public int getStartOperator(final Optimizable optimizable) {
        return 1;
    }
    
    public int getStopOperator(final Optimizable optimizable) {
        return -1;
    }
    
    public void generateOperator(final MethodBuilder methodBuilder, final Optimizable optimizable) {
        methodBuilder.push(2);
    }
    
    public void generateNegate(final MethodBuilder methodBuilder, final Optimizable optimizable) {
        methodBuilder.push(this.isNotNullNode());
    }
    
    public int getOperator() {
        int n;
        if (this.isNullNode()) {
            n = 7;
        }
        else {
            n = 8;
        }
        return n;
    }
    
    public boolean compareWithKnownConstant(final Optimizable optimizable, final boolean b) {
        return true;
    }
    
    public DataValueDescriptor getCompareValue(final Optimizable optimizable) throws StandardException {
        if (this.nullValue == null) {
            this.nullValue = this.operand.getTypeServices().getNull();
        }
        return this.nullValue;
    }
    
    public boolean equalsComparisonWithConstantExpression(final Optimizable optimizable) {
        boolean b = false;
        if (this.isNotNullNode()) {
            return false;
        }
        if (this.operand instanceof ColumnReference) {
            final int tableNumber = ((ColumnReference)this.operand).getTableNumber();
            if (optimizable.hasTableNumber() && optimizable.getTableNumber() == tableNumber) {
                b = true;
            }
        }
        return b;
    }
    
    public RelationalOperator getTransitiveSearchClause(final ColumnReference columnReference) throws StandardException {
        return (RelationalOperator)this.getNodeFactory().getNode(this.getNodeType(), columnReference, this.getContextManager());
    }
    
    public String getReceiverInterfaceName() {
        return "org.apache.derby.iapi.types.DataValueDescriptor";
    }
    
    public double selectivity(final Optimizable optimizable) {
        if (this.isNullNode()) {
            return 0.1;
        }
        return 0.9;
    }
    
    private boolean isNullNode() {
        return this.getNodeType() == 25;
    }
    
    private boolean isNotNullNode() {
        return this.getNodeType() == 24;
    }
    
    public boolean isRelationalOperator() {
        return true;
    }
    
    public boolean optimizableEqualityNode(final Optimizable optimizable, final int n, final boolean b) {
        return this.isNullNode() && b && this.getColumnOperand(optimizable, n) != null;
    }
}
