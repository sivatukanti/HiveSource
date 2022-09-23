// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.error.StandardException;
import java.util.List;

public class AndNode extends BinaryLogicalOperatorNode
{
    public void init(final Object o, final Object o2) {
        super.init(o, o2, "and");
        this.shortCircuitValue = false;
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        super.bindExpression(list, list2, list3);
        this.postBindFixup();
        return this;
    }
    
    public ValueNode preprocess(final int n, final FromList list, final SubqueryList list2, final PredicateList list3) throws StandardException {
        if (this.leftOperand instanceof OrNode) {
            ((OrNode)this.leftOperand).setFirstOr();
        }
        this.leftOperand = this.leftOperand.preprocess(n, list, list2, list3);
        if (this.leftOperand instanceof AndNode) {
            this.changeToCNF(false);
        }
        this.rightOperand = this.rightOperand.preprocess(n, list, list2, list3);
        return this;
    }
    
    ValueNode eliminateNots(final boolean b) throws StandardException {
        this.leftOperand = this.leftOperand.eliminateNots(b);
        this.rightOperand = this.rightOperand.eliminateNots(b);
        if (!b) {
            return this;
        }
        final ValueNode valueNode = (ValueNode)this.getNodeFactory().getNode(52, this.leftOperand, this.rightOperand, this.getContextManager());
        valueNode.setType(this.getTypeServices());
        return valueNode;
    }
    
    public ValueNode putAndsOnTop() throws StandardException {
        this.rightOperand = this.rightOperand.putAndsOnTop();
        return this;
    }
    
    public boolean verifyPutAndsOnTop() {
        return true;
    }
    
    public ValueNode changeToCNF(final boolean b) throws StandardException {
        if (!(this.rightOperand instanceof AndNode) && !this.rightOperand.isBooleanTrue()) {
            this.setRightOperand((ValueNode)this.getNodeFactory().getNode(39, this.getRightOperand(), this.getNodeFactory().getNode(38, Boolean.TRUE, this.getContextManager()), this.getContextManager()));
            ((AndNode)this.getRightOperand()).postBindFixup();
        }
        while (this.leftOperand instanceof AndNode) {
            final ValueNode leftOperand = ((AndNode)this.leftOperand).getLeftOperand();
            final AndNode andNode = (AndNode)this.leftOperand;
            final AndNode rightOperand = (AndNode)this.leftOperand;
            final ValueNode rightOperand2 = this.rightOperand;
            this.leftOperand = leftOperand;
            ((BinaryOperatorNode)(this.rightOperand = rightOperand)).setLeftOperand(andNode.getRightOperand());
            rightOperand.setRightOperand(rightOperand2);
        }
        this.leftOperand = this.leftOperand.changeToCNF(b);
        this.rightOperand = this.rightOperand.changeToCNF(b);
        return this;
    }
    
    public boolean verifyChangeToCNF() {
        return true;
    }
    
    void postBindFixup() throws StandardException {
        this.setType(this.resolveLogicalBinaryOperator(this.leftOperand.getTypeServices(), this.rightOperand.getTypeServices()));
    }
}
