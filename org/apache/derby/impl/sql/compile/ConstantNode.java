// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.compiler.LocalField;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import java.util.List;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.types.DataValueDescriptor;

abstract class ConstantNode extends ValueNode
{
    DataValueDescriptor value;
    
    public void init(final Object o, final Object o2, final Object o3) throws StandardException {
        this.setType((TypeId)o, (boolean)o2, (int)o3);
    }
    
    void setValue(final DataValueDescriptor value) {
        this.value = value;
    }
    
    public DataValueDescriptor getValue() {
        return this.value;
    }
    
    public String toString() {
        return "";
    }
    
    public boolean isCloneable() {
        return true;
    }
    
    public ValueNode getClone() {
        return this;
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        return this;
    }
    
    public boolean isConstantExpression() {
        return true;
    }
    
    public boolean constantExpression(final PredicateList list) {
        return true;
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        if (this.isNull()) {
            expressionClassBuilder.generateNull(methodBuilder, this.getTypeCompiler(), this.getTypeServices().getCollationType());
        }
        else {
            this.generateConstant(expressionClassBuilder, methodBuilder);
            expressionClassBuilder.generateDataValue(methodBuilder, this.getTypeCompiler(), this.getTypeServices().getCollationType(), null);
        }
    }
    
    abstract void generateConstant(final ExpressionClassBuilder p0, final MethodBuilder p1) throws StandardException;
    
    boolean isNull() {
        return this.value == null || this.value.isNull();
    }
    
    protected int getOrderableVariantType() {
        return 3;
    }
    
    protected boolean isEquivalent(final ValueNode valueNode) throws StandardException {
        if (this.isSameNodeType(valueNode)) {
            final ConstantNode constantNode = (ConstantNode)valueNode;
            return (constantNode.getValue() == null && this.getValue() == null) || (constantNode.getValue() != null && constantNode.getValue().compare(this.getValue()) == 0);
        }
        return false;
    }
}
