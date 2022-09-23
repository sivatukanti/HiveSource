// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import java.util.List;

public abstract class BinaryComparisonOperatorNode extends BinaryOperatorNode
{
    private boolean forQueryRewrite;
    private boolean betweenSelectivity;
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5) {
        this.forQueryRewrite = (boolean)o5;
        super.init(o, o2, o3, o4, "org.apache.derby.iapi.types.DataValueDescriptor", "org.apache.derby.iapi.types.DataValueDescriptor");
    }
    
    public void setForQueryRewrite(final boolean forQueryRewrite) {
        this.forQueryRewrite = forQueryRewrite;
    }
    
    public boolean getForQueryRewrite() {
        return this.forQueryRewrite;
    }
    
    void setBetweenSelectivity() {
        this.betweenSelectivity = true;
    }
    
    boolean getBetweenSelectivity() {
        return this.betweenSelectivity;
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        super.bindExpression(list, list2, list3);
        this.leftOperand.getTypeCompiler();
        this.rightOperand.getTypeCompiler();
        final TypeId typeId = this.leftOperand.getTypeId();
        final TypeId typeId2 = this.rightOperand.getTypeId();
        if (!typeId.isStringTypeId() && typeId2.isStringTypeId()) {
            this.rightOperand = (ValueNode)this.getNodeFactory().getNode(60, this.rightOperand, new DataTypeDescriptor(typeId2, true, this.rightOperand.getTypeServices().getMaximumWidth()), this.getContextManager());
            ((CastNode)this.rightOperand).bindCastNodeOnly();
        }
        else if (!typeId2.isStringTypeId() && typeId.isStringTypeId()) {
            this.leftOperand = (ValueNode)this.getNodeFactory().getNode(60, this.leftOperand, new DataTypeDescriptor(typeId, true, this.leftOperand.getTypeServices().getMaximumWidth()), this.getContextManager());
            ((CastNode)this.leftOperand).bindCastNodeOnly();
        }
        this.bindComparisonOperator();
        return this;
    }
    
    public void bindComparisonOperator() throws StandardException {
        this.leftOperand.getTypeId();
        this.rightOperand.getTypeId();
        if (!this.leftOperand.getTypeServices().comparable(this.rightOperand.getTypeServices(), this.operator.equals("=") || this.operator.equals("<>"), this.getClassFactory()) && !this.forQueryRewrite) {
            throw StandardException.newException("42818", this.leftOperand.getTypeServices().getSQLTypeNameWithCollation(), this.rightOperand.getTypeServices().getSQLTypeNameWithCollation());
        }
        this.setType(new DataTypeDescriptor(TypeId.BOOLEAN_ID, this.leftOperand.getTypeServices().isNullable() || this.rightOperand.getTypeServices().isNullable()));
    }
    
    public ValueNode preprocess(final int n, final FromList list, final SubqueryList list2, final PredicateList list3) throws StandardException {
        this.leftOperand = this.leftOperand.preprocess(n, list, list2, list3);
        if (this.rightOperand instanceof SubqueryNode && !((SubqueryNode)this.rightOperand).getPreprocessed()) {
            ((SubqueryNode)this.rightOperand).setParentComparisonOperator(this);
            return this.rightOperand.preprocess(n, list, list2, list3);
        }
        this.rightOperand = this.rightOperand.preprocess(n, list, list2, list3);
        return this;
    }
    
    ValueNode eliminateNots(final boolean b) throws StandardException {
        if (!b) {
            return this;
        }
        return this.getNegation(this.leftOperand, this.rightOperand);
    }
    
    abstract BinaryOperatorNode getNegation(final ValueNode p0, final ValueNode p1) throws StandardException;
    
    abstract BinaryOperatorNode getSwappedEquivalent() throws StandardException;
    
    public ValueNode changeToCNF(final boolean b) throws StandardException {
        if (b && this.rightOperand instanceof SubqueryNode) {
            this.rightOperand = this.rightOperand.changeToCNF(b);
        }
        return this;
    }
    
    public ValueNode genSQLJavaSQLTree() throws StandardException {
        if (this.leftOperand.getTypeId().userType()) {
            if (this.leftOperand.getTypeServices().comparable(this.leftOperand.getTypeServices(), false, this.getClassFactory())) {
                return this;
            }
            this.leftOperand = this.leftOperand.genSQLJavaSQLTree();
        }
        if (this.rightOperand.getTypeId().userType()) {
            if (this.rightOperand.getTypeServices().comparable(this.rightOperand.getTypeServices(), false, this.getClassFactory())) {
                return this;
            }
            this.rightOperand = this.rightOperand.genSQLJavaSQLTree();
        }
        return this;
    }
}
