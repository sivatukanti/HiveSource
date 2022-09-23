// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.compiler.LocalField;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.sql.compile.Optimizable;
import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.error.StandardException;

public final class InListOperatorNode extends BinaryListOperatorNode
{
    private boolean isOrdered;
    private boolean sortDescending;
    
    public void init(final Object o, final Object o2) {
        this.init(o, o2, "IN", "in");
    }
    
    public String toString() {
        return "";
    }
    
    protected InListOperatorNode shallowCopy() throws StandardException {
        final InListOperatorNode inListOperatorNode = (InListOperatorNode)this.getNodeFactory().getNode(55, this.leftOperand, this.rightOperandList, this.getContextManager());
        inListOperatorNode.copyFields(this);
        if (this.isOrdered) {
            inListOperatorNode.markAsOrdered();
        }
        if (this.sortDescending) {
            inListOperatorNode.markSortDescending();
        }
        return inListOperatorNode;
    }
    
    public ValueNode preprocess(final int n, final FromList list, final SubqueryList list2, final PredicateList list3) throws StandardException {
        super.preprocess(n, list, list2, list3);
        if (this.rightOperandList.size() == 1) {
            final BinaryComparisonOperatorNode binaryComparisonOperatorNode = (BinaryComparisonOperatorNode)this.getNodeFactory().getNode(41, this.leftOperand, this.rightOperandList.elementAt(0), Boolean.FALSE, this.getContextManager());
            binaryComparisonOperatorNode.bindComparisonOperator();
            return binaryComparisonOperatorNode;
        }
        final DataTypeDescriptor dominantType = this.getDominantType();
        final int typePrecedence = dominantType.getTypeId().typePrecedence();
        if (this.leftOperand.getTypeServices().getTypeId().typePrecedence() != typePrecedence && !this.rightOperandList.allSamePrecendence(typePrecedence)) {
            final CastNode leftOperand = (CastNode)this.getNodeFactory().getNode(60, this.leftOperand, dominantType, this.getContextManager());
            leftOperand.bindCastNodeOnly();
            this.leftOperand = leftOperand;
        }
        if (this.leftOperand instanceof ColumnReference && this.rightOperandList.containsOnlyConstantAndParamNodes()) {
            if (this.rightOperandList.containsAllConstantNodes()) {
                final DataValueDescriptor null = dominantType.getNull();
                this.rightOperandList.sortInAscendingOrder(null);
                this.isOrdered = true;
                final ValueNode valueNode = (ValueNode)this.rightOperandList.elementAt(0);
                if (null.equals(((ConstantNode)valueNode).getValue(), ((ConstantNode)this.rightOperandList.elementAt(this.rightOperandList.size() - 1)).getValue()).equals(true)) {
                    final BinaryComparisonOperatorNode binaryComparisonOperatorNode2 = (BinaryComparisonOperatorNode)this.getNodeFactory().getNode(41, this.leftOperand, valueNode, Boolean.FALSE, this.getContextManager());
                    binaryComparisonOperatorNode2.bindComparisonOperator();
                    return binaryComparisonOperatorNode2;
                }
            }
            final ValueNode valueToGenerate = (ValueNode)this.rightOperandList.elementAt(0);
            final ParameterNode parameterNode = (ParameterNode)this.getNodeFactory().getNode(88, new Integer(0), null, this.getContextManager());
            parameterNode.setType(valueToGenerate.getTypeServices());
            parameterNode.setValueToGenerate(valueToGenerate);
            final BinaryComparisonOperatorNode binaryComparisonOperatorNode3 = (BinaryComparisonOperatorNode)this.getNodeFactory().getNode(41, this.leftOperand, parameterNode, this, Boolean.FALSE, this.getContextManager());
            binaryComparisonOperatorNode3.bindComparisonOperator();
            return binaryComparisonOperatorNode3;
        }
        return this;
    }
    
    private DataTypeDescriptor getDominantType() {
        DataTypeDescriptor dataTypeDescriptor = this.leftOperand.getTypeServices();
        if (!this.rightOperandList.allSamePrecendence(dataTypeDescriptor.getTypeId().typePrecedence())) {
            final ClassFactory classFactory = this.getClassFactory();
            for (int size = this.rightOperandList.size(), i = 0; i < size; ++i) {
                dataTypeDescriptor = dataTypeDescriptor.getDominantType(((ValueNode)this.rightOperandList.elementAt(i)).getTypeServices(), classFactory);
            }
        }
        return dataTypeDescriptor;
    }
    
    ValueNode eliminateNots(final boolean b) throws StandardException {
        final int size = this.rightOperandList.size();
        if (!b) {
            return this;
        }
        final BinaryComparisonOperatorNode binaryComparisonOperatorNode = (BinaryComparisonOperatorNode)this.getNodeFactory().getNode(47, (this.leftOperand instanceof ColumnReference) ? this.leftOperand.getClone() : this.leftOperand, this.rightOperandList.elementAt(0), Boolean.FALSE, this.getContextManager());
        binaryComparisonOperatorNode.bindComparisonOperator();
        BinaryOperatorNode binaryOperatorNode = binaryComparisonOperatorNode;
        for (int i = 1; i < size; ++i) {
            final BinaryComparisonOperatorNode binaryComparisonOperatorNode2 = (BinaryComparisonOperatorNode)this.getNodeFactory().getNode(47, (this.leftOperand instanceof ColumnReference) ? this.leftOperand.getClone() : this.leftOperand, this.rightOperandList.elementAt(i), Boolean.FALSE, this.getContextManager());
            binaryComparisonOperatorNode2.bindComparisonOperator();
            final AndNode andNode = (AndNode)this.getNodeFactory().getNode(39, binaryOperatorNode, binaryComparisonOperatorNode2, this.getContextManager());
            andNode.postBindFixup();
            binaryOperatorNode = andNode;
        }
        return binaryOperatorNode;
    }
    
    public boolean selfReference(final ColumnReference columnReference) throws StandardException {
        for (int size = this.rightOperandList.size(), i = 0; i < size; ++i) {
            if (((ValueNode)this.rightOperandList.elementAt(i)).getTablesReferenced().get(columnReference.getTableNumber())) {
                return true;
            }
        }
        return false;
    }
    
    public double selectivity(final Optimizable optimizable) {
        return 0.3;
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        this.rightOperandList.size();
        final String s = "org.apache.derby.iapi.types.DataValueDescriptor";
        final String s2 = "org.apache.derby.iapi.types.DataValueDescriptor";
        this.receiver = this.leftOperand;
        final String interfaceName = this.getTypeCompiler().interfaceName();
        final LocalField generateListAsArray = this.generateListAsArray(expressionClassBuilder, methodBuilder);
        this.leftOperand.generateExpression(expressionClassBuilder, methodBuilder);
        methodBuilder.dup();
        methodBuilder.upCast(s2);
        methodBuilder.getField(generateListAsArray);
        methodBuilder.push(this.isOrdered);
        methodBuilder.callMethod((short)185, s, this.methodName, interfaceName, 3);
    }
    
    protected LocalField generateListAsArray(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        final int size = this.rightOperandList.size();
        final LocalField fieldDeclaration = expressionClassBuilder.newFieldDeclaration(2, "org.apache.derby.iapi.types.DataValueDescriptor[]");
        final MethodBuilder constructor = expressionClassBuilder.getConstructor();
        constructor.pushNewArray("org.apache.derby.iapi.types.DataValueDescriptor", size);
        constructor.setField(fieldDeclaration);
        int n = 0;
        MethodBuilder generatedFun = null;
        MethodBuilder methodBuilder2 = constructor;
        for (int i = 0; i < size; ++i) {
            MethodBuilder methodBuilder3;
            if (this.rightOperandList.elementAt(i) instanceof ConstantNode) {
                ++n;
                if (methodBuilder2.statementNumHitLimit(1)) {
                    final MethodBuilder generatedFun2 = expressionClassBuilder.newGeneratedFun("void", 2);
                    methodBuilder2.pushThis();
                    methodBuilder2.callMethod((short)182, null, generatedFun2.getName(), "void", 0);
                    if (methodBuilder2 != constructor) {
                        methodBuilder2.methodReturn();
                        methodBuilder2.complete();
                    }
                    methodBuilder2 = generatedFun2;
                }
                methodBuilder3 = methodBuilder2;
            }
            else {
                if (generatedFun == null) {
                    generatedFun = expressionClassBuilder.newGeneratedFun("void", 4);
                }
                methodBuilder3 = generatedFun;
            }
            methodBuilder3.getField(fieldDeclaration);
            ((ValueNode)this.rightOperandList.elementAt(i)).generateExpression(expressionClassBuilder, methodBuilder3);
            methodBuilder3.upCast("org.apache.derby.iapi.types.DataValueDescriptor");
            methodBuilder3.setArrayElement(i);
        }
        if (methodBuilder2 != constructor) {
            methodBuilder2.methodReturn();
            methodBuilder2.complete();
        }
        if (generatedFun != null) {
            generatedFun.methodReturn();
            generatedFun.complete();
            methodBuilder.pushThis();
            methodBuilder.callMethod((short)182, null, generatedFun.getName(), "void", 0);
        }
        return fieldDeclaration;
    }
    
    public void generateStartStopKey(final boolean b, final boolean b2, final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        final int typeFormatId = this.leftOperand.getTypeId().getTypeFormatId();
        final int precision = this.leftOperand.getTypeServices().getPrecision();
        final int scale = this.leftOperand.getTypeServices().getScale();
        final boolean nullable = this.leftOperand.getTypeServices().isNullable();
        final int maximumWidth = this.leftOperand.getTypeServices().getMaximumWidth();
        final int collationType = this.leftOperand.getTypeServices().getCollationType();
        final int collationDerivation = this.leftOperand.getTypeServices().getCollationDerivation();
        final int n = this.leftOperand.getTypeId().isUserDefinedTypeId() ? this.leftOperand.getTypeId().getJDBCTypeId() : -1;
        final int size = this.rightOperandList.size();
        int n2 = 0;
        int n3;
        int n4;
        if (size < 5) {
            n3 = 1;
            n4 = (size - 1) % 4 + 1;
        }
        else {
            n3 = (size - 5) / 3 + 2;
            n4 = (size - 5) % 3 + 1;
        }
        for (int i = 0; i < n3; ++i) {
            for (int n5 = (i == n3 - 1) ? n4 : ((i == 0) ? 4 : 3), j = 0; j < n5; ++j) {
                ((ValueNode)this.rightOperandList.elementAt(n2++)).generateExpression(expressionClassBuilder, methodBuilder);
                methodBuilder.upCast("org.apache.derby.iapi.types.DataValueDescriptor");
            }
            for (int n6 = (i < n3 - 1) ? 0 : ((i == 0) ? (4 - n4) : (3 - n4)), k = 0; k < n6; ++k) {
                methodBuilder.pushNull("org.apache.derby.iapi.types.DataValueDescriptor");
            }
            methodBuilder.push(typeFormatId);
            methodBuilder.push(n);
            methodBuilder.push(precision);
            methodBuilder.push(scale);
            methodBuilder.push(nullable);
            methodBuilder.push(maximumWidth);
            methodBuilder.push(collationType);
            methodBuilder.push(collationDerivation);
            String s;
            if ((b && b2) || (!b && !b2)) {
                s = "minValue";
            }
            else {
                s = "maxValue";
            }
            methodBuilder.callMethod((short)184, "org.apache.derby.impl.sql.execute.BaseExpressionActivation", s, "org.apache.derby.iapi.types.DataValueDescriptor", 12);
        }
    }
    
    protected void markAsOrdered() {
        this.isOrdered = true;
    }
    
    protected void markSortDescending() {
        this.sortDescending = true;
    }
    
    protected boolean isOrdered() {
        return this.isOrdered;
    }
    
    protected boolean sortDescending() {
        return this.sortDescending;
    }
}
