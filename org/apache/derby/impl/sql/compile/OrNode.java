// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.error.StandardException;
import java.util.List;

public class OrNode extends BinaryLogicalOperatorNode
{
    private boolean firstOr;
    
    public void init(final Object o, final Object o2) {
        super.init(o, o2, "or");
        this.shortCircuitValue = true;
    }
    
    void setFirstOr() {
        this.firstOr = true;
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        super.bindExpression(list, list2, list3);
        this.postBindFixup();
        return this;
    }
    
    public ValueNode preprocess(final int n, final FromList list, final SubqueryList list2, final PredicateList list3) throws StandardException {
        super.preprocess(n, list, list2, list3);
        if (this.firstOr) {
            int n2 = 1;
            ColumnReference columnReference = null;
            int n3 = -1;
            int n4 = -1;
            ValueNode rightOperand;
            for (rightOperand = this; rightOperand instanceof OrNode; rightOperand = ((OrNode)rightOperand).getRightOperand()) {
                final ValueNode leftOperand = ((OrNode)rightOperand).getLeftOperand();
                if (!leftOperand.isRelationalOperator()) {
                    n2 = ((leftOperand instanceof BinaryRelationalOperatorNode) ? 1 : 0);
                    if (n2 == 0) {
                        break;
                    }
                }
                if (((RelationalOperator)leftOperand).getOperator() != 1) {
                    n2 = 0;
                    break;
                }
                final BinaryRelationalOperatorNode binaryRelationalOperatorNode = (BinaryRelationalOperatorNode)leftOperand;
                if (binaryRelationalOperatorNode.getLeftOperand() instanceof ColumnReference) {
                    columnReference = (ColumnReference)binaryRelationalOperatorNode.getLeftOperand();
                    if (n4 == -1) {
                        n4 = columnReference.getTableNumber();
                        n3 = columnReference.getColumnNumber();
                    }
                    else if (n4 != columnReference.getTableNumber() || n3 != columnReference.getColumnNumber()) {
                        n2 = 0;
                        break;
                    }
                }
                else {
                    if (!(binaryRelationalOperatorNode.getRightOperand() instanceof ColumnReference)) {
                        n2 = 0;
                        break;
                    }
                    columnReference = (ColumnReference)binaryRelationalOperatorNode.getRightOperand();
                    if (n4 == -1) {
                        n4 = columnReference.getTableNumber();
                        n3 = columnReference.getColumnNumber();
                    }
                    else if (n4 != columnReference.getTableNumber() || n3 != columnReference.getColumnNumber()) {
                        n2 = 0;
                        break;
                    }
                }
            }
            if (n2 != 0 && rightOperand.isBooleanFalse()) {
                final ValueNodeList list4 = (ValueNodeList)this.getNodeFactory().getNode(15, this.getContextManager());
                for (ValueNode rightOperand2 = this; rightOperand2 instanceof OrNode; rightOperand2 = ((OrNode)rightOperand2).getRightOperand()) {
                    final BinaryRelationalOperatorNode binaryRelationalOperatorNode2 = (BinaryRelationalOperatorNode)((OrNode)rightOperand2).getLeftOperand();
                    if (binaryRelationalOperatorNode2.isInListProbeNode()) {
                        list4.destructiveAppend(binaryRelationalOperatorNode2.getInListOp().getRightOperandList());
                    }
                    else if (binaryRelationalOperatorNode2.getLeftOperand() instanceof ColumnReference) {
                        list4.addValueNode(binaryRelationalOperatorNode2.getRightOperand());
                    }
                    else {
                        list4.addValueNode(binaryRelationalOperatorNode2.getLeftOperand());
                    }
                }
                final InListOperatorNode inListOperatorNode = (InListOperatorNode)this.getNodeFactory().getNode(55, columnReference, list4, this.getContextManager());
                inListOperatorNode.setType(this.getTypeServices());
                return inListOperatorNode.preprocess(n, list, list2, list3);
            }
        }
        return this;
    }
    
    ValueNode eliminateNots(final boolean b) throws StandardException {
        this.leftOperand = this.leftOperand.eliminateNots(b);
        this.rightOperand = this.rightOperand.eliminateNots(b);
        if (!b) {
            return this;
        }
        final AndNode andNode = (AndNode)this.getNodeFactory().getNode(39, this.leftOperand, this.rightOperand, this.getContextManager());
        andNode.setType(this.getTypeServices());
        return andNode;
    }
    
    public ValueNode changeToCNF(final boolean b) throws StandardException {
        OrNode orNode = this;
        if (this.rightOperand instanceof AndNode) {
            this.rightOperand = (ValueNode)this.getNodeFactory().getNode(52, this.rightOperand, this.getNodeFactory().getNode(38, Boolean.FALSE, this.getContextManager()), this.getContextManager());
            ((OrNode)this.rightOperand).postBindFixup();
        }
        while (orNode.getRightOperand() instanceof OrNode) {
            orNode = (OrNode)orNode.getRightOperand();
        }
        if (!orNode.getRightOperand().isBooleanFalse()) {
            orNode.setRightOperand((ValueNode)this.getNodeFactory().getNode(52, orNode.getRightOperand(), this.getNodeFactory().getNode(38, Boolean.FALSE, this.getContextManager()), this.getContextManager()));
            ((OrNode)orNode.getRightOperand()).postBindFixup();
        }
        while (this.leftOperand instanceof OrNode) {
            final ValueNode leftOperand = ((OrNode)this.leftOperand).getLeftOperand();
            final OrNode orNode2 = (OrNode)this.leftOperand;
            final OrNode rightOperand = (OrNode)this.leftOperand;
            final ValueNode rightOperand2 = this.rightOperand;
            this.leftOperand = leftOperand;
            ((BinaryOperatorNode)(this.rightOperand = rightOperand)).setLeftOperand(orNode2.getRightOperand());
            rightOperand.setRightOperand(rightOperand2);
        }
        this.leftOperand = this.leftOperand.changeToCNF(false);
        this.rightOperand = this.rightOperand.changeToCNF(false);
        return this;
    }
    
    public boolean verifyChangeToCNF() {
        return true;
    }
    
    void postBindFixup() throws StandardException {
        this.setType(this.resolveLogicalBinaryOperator(this.leftOperand.getTypeServices(), this.rightOperand.getTypeServices()));
    }
}
