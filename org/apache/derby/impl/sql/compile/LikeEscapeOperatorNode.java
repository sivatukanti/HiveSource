// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import java.util.Arrays;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.types.Like;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.error.StandardException;
import java.util.List;
import org.apache.derby.iapi.util.ReuseFactory;

public final class LikeEscapeOperatorNode extends TernaryOperatorNode
{
    boolean addedEquals;
    String escape;
    
    public void init(final Object o, final Object o2, final Object o3) {
        super.init(o, o2, o3, ReuseFactory.getInteger(3), null);
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        super.bindExpression(list, list2, list3);
        String string = null;
        if (!this.leftOperand.requiresTypeFromContext() && !this.leftOperand.getTypeId().isStringTypeId()) {
            throw StandardException.newException("42884", "LIKE", "FUNCTION");
        }
        if (this.rightOperand != null && !this.rightOperand.requiresTypeFromContext() && !this.rightOperand.getTypeId().isStringTypeId()) {
            throw StandardException.newException("42884", "LIKE", "FUNCTION");
        }
        if (this.receiver.requiresTypeFromContext()) {
            this.receiver.setType(new DataTypeDescriptor(TypeId.getBuiltInTypeId(12), true));
            if (!this.leftOperand.requiresTypeFromContext()) {
                this.receiver.setCollationInfo(this.leftOperand.getTypeServices());
            }
            else if (this.rightOperand != null && !this.rightOperand.requiresTypeFromContext()) {
                this.receiver.setCollationInfo(this.rightOperand.getTypeServices());
            }
            else {
                this.receiver.setCollationUsingCompilationSchema();
            }
        }
        if (this.leftOperand.requiresTypeFromContext()) {
            if (this.receiver.getTypeId().isStringTypeId()) {
                this.leftOperand.setType(this.receiver.getTypeServices());
            }
            else {
                this.leftOperand.setType(new DataTypeDescriptor(TypeId.getBuiltInTypeId(12), true));
            }
            this.leftOperand.setCollationInfo(this.receiver.getTypeServices());
        }
        if (this.rightOperand != null && this.rightOperand.requiresTypeFromContext()) {
            if (this.receiver.getTypeId().isStringTypeId()) {
                this.rightOperand.setType(this.receiver.getTypeServices());
            }
            else {
                this.rightOperand.setType(new DataTypeDescriptor(TypeId.getBuiltInTypeId(12), true));
            }
            this.rightOperand.setCollationInfo(this.receiver.getTypeServices());
        }
        this.bindToBuiltIn();
        this.receiver.getTypeCompiler();
        this.leftOperand.getTypeCompiler();
        if (!this.receiver.getTypeId().isStringTypeId()) {
            throw StandardException.newException("42884", "LIKE", "FUNCTION");
        }
        if (!this.leftOperand.getTypeId().isStringTypeId()) {
            (this.leftOperand = this.castArgToString(this.leftOperand)).getTypeCompiler();
        }
        if (this.rightOperand != null) {
            this.rightOperand = this.castArgToString(this.rightOperand);
        }
        final boolean b = this.leftOperand instanceof CharConstantNode;
        if (b) {
            string = ((CharConstantNode)this.leftOperand).getString();
        }
        int n = (this.rightOperand instanceof CharConstantNode) ? 1 : 0;
        if (n != 0) {
            this.escape = ((CharConstantNode)this.rightOperand).getString();
            if (this.escape.length() != 1) {
                throw StandardException.newException("22019", this.escape);
            }
        }
        else if (this.rightOperand == null) {
            n = 1;
        }
        if (!this.receiver.getTypeServices().compareCollationInfo(this.leftOperand.getTypeServices())) {
            throw StandardException.newException("42ZA2", this.receiver.getTypeServices().getSQLstring(), this.receiver.getTypeServices().getCollationName(), this.leftOperand.getTypeServices().getSQLstring(), this.leftOperand.getTypeServices().getCollationName());
        }
        if (this.receiver instanceof ColumnReference && b && n != 0 && Like.isOptimizable(string)) {
            Object stripEscapesNoPatternChars = null;
            if (this.escape != null) {
                stripEscapesNoPatternChars = Like.stripEscapesNoPatternChars(string, this.escape.charAt(0));
            }
            else if (string.indexOf(95) == -1 && string.indexOf(37) == -1) {
                stripEscapesNoPatternChars = string;
            }
            if (stripEscapesNoPatternChars != null) {
                final ValueNode clone = this.receiver.getClone();
                this.addedEquals = true;
                final BinaryComparisonOperatorNode binaryComparisonOperatorNode = (BinaryComparisonOperatorNode)this.getNodeFactory().getNode(41, clone, this.getNodeFactory().getNode(61, stripEscapesNoPatternChars, this.getContextManager()), Boolean.FALSE, this.getContextManager());
                binaryComparisonOperatorNode.setForQueryRewrite(true);
                final AndNode andNode = (AndNode)this.getNodeFactory().getNode(39, this, binaryComparisonOperatorNode.bindExpression(list, list2, list3), this.getContextManager());
                this.finishBindExpr();
                andNode.postBindFixup();
                return andNode;
            }
        }
        this.finishBindExpr();
        return this;
    }
    
    private void finishBindExpr() throws StandardException {
        this.bindComparisonOperator();
        boolean b = this.receiver.getTypeServices().isNullable() || this.leftOperand.getTypeServices().isNullable();
        if (this.rightOperand != null) {
            b |= this.rightOperand.getTypeServices().isNullable();
        }
        this.setType(new DataTypeDescriptor(TypeId.BOOLEAN_ID, b));
    }
    
    public void bindComparisonOperator() throws StandardException {
        final TypeId typeId = this.receiver.getTypeId();
        final TypeId typeId2 = this.leftOperand.getTypeId();
        if (!typeId.isStringTypeId()) {
            throw StandardException.newException("42X53", typeId.getSQLTypeName());
        }
        if (!typeId2.isStringTypeId()) {
            throw StandardException.newException("42X53", typeId2.getSQLTypeName());
        }
        if (this.rightOperand != null && !this.rightOperand.getTypeId().isStringTypeId()) {
            throw StandardException.newException("42X53", this.rightOperand.getTypeId().getSQLTypeName());
        }
    }
    
    public ValueNode preprocess(final int n, final FromList list, final SubqueryList list2, final PredicateList list3) throws StandardException {
        int n2 = 0;
        Object greaterEqualString = null;
        Object lessThanString = null;
        super.preprocess(n, list, list2, list3);
        if (this.receiver.getTypeId().getSQLTypeName().equals("CLOB")) {
            return this;
        }
        if (this.addedEquals) {
            return this;
        }
        if (!(this.leftOperand instanceof CharConstantNode) && !this.leftOperand.requiresTypeFromContext()) {
            return this;
        }
        if (!(this.receiver instanceof ColumnReference)) {
            return this;
        }
        if (this.receiver.getTypeServices().getCollationType() != 0) {
            return this;
        }
        if (this.leftOperand instanceof CharConstantNode) {
            final String string = ((CharConstantNode)this.leftOperand).getString();
            if (!Like.isOptimizable(string)) {
                return this;
            }
            final int maximumWidth = this.receiver.getTypeServices().getMaximumWidth();
            if (maximumWidth > 32700) {
                return this;
            }
            greaterEqualString = Like.greaterEqualString(string, this.escape, maximumWidth);
            lessThanString = Like.lessThanString(string, this.escape, maximumWidth);
            n2 = (Like.isLikeComparisonNeeded(string) ? 0 : 1);
        }
        AndNode andNode = null;
        final ValueNode valueNode = (ValueNode)this.getNodeFactory().getNode(38, Boolean.TRUE, this.getContextManager());
        if (lessThanString != null || this.leftOperand.requiresTypeFromContext()) {
            QueryTreeNode setupOptimizeStringFromParameter;
            if (this.leftOperand.requiresTypeFromContext()) {
                setupOptimizeStringFromParameter = this.setupOptimizeStringFromParameter(this.leftOperand, this.rightOperand, "lessThanStringFromParameter", this.receiver.getTypeServices().getMaximumWidth());
            }
            else {
                setupOptimizeStringFromParameter = (QueryTreeNode)this.getNodeFactory().getNode(61, lessThanString, this.getContextManager());
            }
            final BinaryComparisonOperatorNode binaryComparisonOperatorNode = (BinaryComparisonOperatorNode)this.getNodeFactory().getNode(45, this.receiver.getClone(), setupOptimizeStringFromParameter, Boolean.FALSE, this.getContextManager());
            binaryComparisonOperatorNode.setForQueryRewrite(true);
            binaryComparisonOperatorNode.bindComparisonOperator();
            binaryComparisonOperatorNode.setBetweenSelectivity();
            andNode = (AndNode)this.getNodeFactory().getNode(39, binaryComparisonOperatorNode, valueNode, this.getContextManager());
            andNode.postBindFixup();
        }
        ValueNode setupOptimizeStringFromParameter2;
        if (this.leftOperand.requiresTypeFromContext()) {
            setupOptimizeStringFromParameter2 = this.setupOptimizeStringFromParameter(this.leftOperand, this.rightOperand, "greaterEqualStringFromParameter", this.receiver.getTypeServices().getMaximumWidth());
        }
        else {
            setupOptimizeStringFromParameter2 = (ValueNode)this.getNodeFactory().getNode(61, greaterEqualString, this.getContextManager());
        }
        final BinaryComparisonOperatorNode binaryComparisonOperatorNode2 = (BinaryComparisonOperatorNode)this.getNodeFactory().getNode(42, this.receiver.getClone(), setupOptimizeStringFromParameter2, Boolean.FALSE, this.getContextManager());
        binaryComparisonOperatorNode2.setForQueryRewrite(true);
        binaryComparisonOperatorNode2.bindComparisonOperator();
        binaryComparisonOperatorNode2.setBetweenSelectivity();
        AndNode andNode2;
        if (andNode == null) {
            andNode2 = (AndNode)this.getNodeFactory().getNode(39, binaryComparisonOperatorNode2, valueNode, this.getContextManager());
        }
        else {
            andNode2 = (AndNode)this.getNodeFactory().getNode(39, binaryComparisonOperatorNode2, andNode, this.getContextManager());
        }
        andNode2.postBindFixup();
        if (n2 == 0) {
            andNode2 = (AndNode)this.getNodeFactory().getNode(39, this, andNode2, this.getContextManager());
            andNode2.postBindFixup();
        }
        this.setTransformed();
        return andNode2;
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        this.receiver.generateExpression(expressionClassBuilder, methodBuilder);
        methodBuilder.upCast(this.receiverInterfaceType = this.receiver.getTypeCompiler().interfaceName());
        this.leftOperand.generateExpression(expressionClassBuilder, methodBuilder);
        methodBuilder.upCast(this.leftInterfaceType);
        if (this.rightOperand != null) {
            this.rightOperand.generateExpression(expressionClassBuilder, methodBuilder);
            methodBuilder.upCast(this.rightInterfaceType);
        }
        methodBuilder.callMethod((short)185, null, this.methodName, this.resultInterfaceType, (this.rightOperand == null) ? 1 : 2);
    }
    
    private ValueNode setupOptimizeStringFromParameter(final ValueNode valueNode, final ValueNode valueNode2, String string, final int value) throws StandardException {
        if (valueNode2 != null) {
            string += "WithEsc";
        }
        final StaticMethodCallNode staticMethodCallNode = (StaticMethodCallNode)this.getNodeFactory().getNode(85, string, "org.apache.derby.iapi.types.Like", this.getContextManager());
        staticMethodCallNode.internalCall = true;
        final QueryTreeNode queryTreeNode = (QueryTreeNode)this.getNodeFactory().getNode(70, new Integer(value), this.getContextManager());
        staticMethodCallNode.addParms(Arrays.asList((valueNode2 == null) ? new QueryTreeNode[] { valueNode, queryTreeNode } : new QueryTreeNode[] { valueNode, valueNode2, queryTreeNode }));
        final CastNode castNode = (CastNode)this.getNodeFactory().getNode(60, ((ValueNode)this.getNodeFactory().getNode(36, staticMethodCallNode, this.getContextManager())).bindExpression(null, null, null), valueNode.getTypeServices(), this.getContextManager());
        castNode.bindCastNodeOnly();
        return castNode;
    }
}
