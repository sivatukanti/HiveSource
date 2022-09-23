// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.sql.compile.NodeFactory;

public class BetweenOperatorNode extends BinaryListOperatorNode
{
    public void init(final Object o, final Object o2) {
        super.init(o, o2, "BETWEEN", null);
    }
    
    ValueNode eliminateNots(final boolean b) throws StandardException {
        if (!b) {
            return this;
        }
        final NodeFactory nodeFactory = this.getNodeFactory();
        final ContextManager contextManager = this.getContextManager();
        final BinaryComparisonOperatorNode binaryComparisonOperatorNode = (BinaryComparisonOperatorNode)nodeFactory.getNode(45, this.leftOperand, this.rightOperandList.elementAt(0), Boolean.FALSE, contextManager);
        binaryComparisonOperatorNode.bindComparisonOperator();
        final BinaryComparisonOperatorNode binaryComparisonOperatorNode2 = (BinaryComparisonOperatorNode)nodeFactory.getNode(43, (this.leftOperand instanceof ColumnReference) ? this.leftOperand.getClone() : this.leftOperand, this.rightOperandList.elementAt(1), Boolean.FALSE, contextManager);
        binaryComparisonOperatorNode2.bindComparisonOperator();
        final OrNode orNode = (OrNode)nodeFactory.getNode(52, binaryComparisonOperatorNode, binaryComparisonOperatorNode2, contextManager);
        orNode.postBindFixup();
        binaryComparisonOperatorNode.setBetweenSelectivity();
        binaryComparisonOperatorNode2.setBetweenSelectivity();
        return orNode;
    }
    
    public ValueNode preprocess(final int n, final FromList list, final SubqueryList list2, final PredicateList list3) throws StandardException {
        super.preprocess(n, list, list2, list3);
        if (!(this.leftOperand instanceof ColumnReference)) {
            return this;
        }
        final ValueNode clone = this.leftOperand.getClone();
        final NodeFactory nodeFactory = this.getNodeFactory();
        final ContextManager contextManager = this.getContextManager();
        final QueryTreeNode queryTreeNode = (QueryTreeNode)nodeFactory.getNode(38, Boolean.TRUE, contextManager);
        final BinaryComparisonOperatorNode binaryComparisonOperatorNode = (BinaryComparisonOperatorNode)nodeFactory.getNode(44, clone, this.rightOperandList.elementAt(1), Boolean.FALSE, contextManager);
        binaryComparisonOperatorNode.bindComparisonOperator();
        final AndNode andNode = (AndNode)nodeFactory.getNode(39, binaryComparisonOperatorNode, queryTreeNode, contextManager);
        andNode.postBindFixup();
        final BinaryComparisonOperatorNode binaryComparisonOperatorNode2 = (BinaryComparisonOperatorNode)nodeFactory.getNode(42, this.leftOperand, this.rightOperandList.elementAt(0), Boolean.FALSE, contextManager);
        binaryComparisonOperatorNode2.bindComparisonOperator();
        final AndNode andNode2 = (AndNode)nodeFactory.getNode(39, binaryComparisonOperatorNode2, andNode, contextManager);
        andNode2.postBindFixup();
        binaryComparisonOperatorNode.setBetweenSelectivity();
        binaryComparisonOperatorNode2.setBetweenSelectivity();
        return andNode2;
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        final NodeFactory nodeFactory = this.getNodeFactory();
        final ContextManager contextManager = this.getContextManager();
        final BinaryComparisonOperatorNode binaryComparisonOperatorNode = (BinaryComparisonOperatorNode)nodeFactory.getNode(42, this.leftOperand, this.rightOperandList.elementAt(0), Boolean.FALSE, contextManager);
        binaryComparisonOperatorNode.bindComparisonOperator();
        final BinaryComparisonOperatorNode binaryComparisonOperatorNode2 = (BinaryComparisonOperatorNode)nodeFactory.getNode(44, this.leftOperand, this.rightOperandList.elementAt(1), Boolean.FALSE, contextManager);
        binaryComparisonOperatorNode2.bindComparisonOperator();
        final AndNode andNode = (AndNode)nodeFactory.getNode(39, binaryComparisonOperatorNode, binaryComparisonOperatorNode2, contextManager);
        andNode.postBindFixup();
        andNode.generateExpression(expressionClassBuilder, methodBuilder);
    }
}
