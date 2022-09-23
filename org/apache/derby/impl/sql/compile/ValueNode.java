// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.Optimizable;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.iapi.sql.compile.NodeFactory;
import java.util.List;
import org.apache.derby.iapi.sql.compile.TypeCompiler;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.types.DataTypeDescriptor;

public abstract class ValueNode extends QueryTreeNode
{
    private DataTypeDescriptor dataTypeServices;
    boolean transformed;
    
    public ValueNode() {
    }
    
    final void setType(final TypeId typeId, final boolean b, final int n) throws StandardException {
        this.setType(new DataTypeDescriptor(typeId, b, n));
    }
    
    final void setType(final TypeId typeId, final int n, final int n2, final boolean b, final int n3) throws StandardException {
        this.setType(new DataTypeDescriptor(typeId, n, n2, b, n3));
    }
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5) throws StandardException {
        this.setType(new DataTypeDescriptor((TypeId)o, (int)o2, (int)o3, (boolean)o4, (int)o5));
    }
    
    ValueNode(final Object o, final Object o2, final Object o3, final Object o4) throws StandardException {
        this.setType(new DataTypeDescriptor((TypeId)o2, (boolean)o3, (int)o4));
    }
    
    public String toString() {
        return "";
    }
    
    public DataTypeDescriptor getTypeServices() {
        return this.dataTypeServices;
    }
    
    public void setNullability(final boolean b) throws StandardException {
        this.setType(this.getTypeServices().getNullabilityType(b));
    }
    
    public void setCollationInfo(final DataTypeDescriptor dataTypeDescriptor) throws StandardException {
        this.setCollationInfo(dataTypeDescriptor.getCollationType(), dataTypeDescriptor.getCollationDerivation());
    }
    
    public void setCollationInfo(final int n, final int n2) throws StandardException {
        this.setType(this.getTypeServices().getCollatedType(n, n2));
    }
    
    public TypeId getTypeId() throws StandardException {
        final DataTypeDescriptor typeServices = this.getTypeServices();
        if (typeServices != null) {
            return typeServices.getTypeId();
        }
        return null;
    }
    
    protected final DataValueFactory getDataValueFactory() {
        return this.getLanguageConnectionContext().getDataValueFactory();
    }
    
    public final TypeCompiler getTypeCompiler() throws StandardException {
        return this.getTypeCompiler(this.getTypeId());
    }
    
    public void setType(DataTypeDescriptor bindUserType) throws StandardException {
        if (bindUserType != null) {
            bindUserType = this.bindUserType(bindUserType);
        }
        if ((this.dataTypeServices = bindUserType) != null) {
            this.createTypeDependency(bindUserType);
        }
    }
    
    protected final void setCollationUsingCompilationSchema() throws StandardException {
        this.setCollationUsingCompilationSchema(1);
    }
    
    protected final void setCollationUsingCompilationSchema(final int n) throws StandardException {
        this.setCollationInfo(this.getSchemaDescriptor(null, false).getCollationType(), n);
    }
    
    public ResultColumn getSourceResultColumn() {
        return null;
    }
    
    void setTransformed() {
        this.transformed = true;
    }
    
    boolean getTransformed() {
        return this.transformed;
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        return this.bindExpression(list, list2, list3, false);
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3, final boolean b) throws StandardException {
        return this;
    }
    
    public ValueNode genSQLJavaSQLTree() throws StandardException {
        final JavaValueNode javaValueNode = (JavaValueNode)this.getNodeFactory().getNode(28, this, this.getContextManager());
        final ValueNode valueNode = (ValueNode)this.getNodeFactory().getNode(36, javaValueNode, this.getContextManager());
        DataTypeDescriptor type;
        if (this.getTypeServices() != null && this.getTypeId().userType()) {
            type = this.getTypeServices();
        }
        else {
            type = DataTypeDescriptor.getSQLDataTypeDescriptor(javaValueNode.getJavaTypeName());
        }
        valueNode.setType(type);
        return valueNode;
    }
    
    public ValueNode preprocess(final int n, final FromList list, final SubqueryList list2, final PredicateList list3) throws StandardException {
        return this;
    }
    
    ValueNode evaluateConstantExpressions() throws StandardException {
        return this;
    }
    
    ValueNode eliminateNots(final boolean b) throws StandardException {
        if (!b) {
            return this;
        }
        return this.genEqualsFalseTree();
    }
    
    public ValueNode genEqualsFalseTree() throws StandardException {
        final NodeFactory nodeFactory = this.getNodeFactory();
        final BinaryRelationalOperatorNode binaryRelationalOperatorNode = (BinaryRelationalOperatorNode)nodeFactory.getNode(41, this, nodeFactory.getNode(38, Boolean.FALSE, this.getContextManager()), Boolean.FALSE, this.getContextManager());
        binaryRelationalOperatorNode.setType(new DataTypeDescriptor(TypeId.BOOLEAN_ID, this.getTypeServices().isNullable()));
        return binaryRelationalOperatorNode;
    }
    
    ValueNode genIsNullTree(final boolean b) throws StandardException {
        final IsNullNode isNullNode = (IsNullNode)this.getNodeFactory().getNode(b ? 24 : 25, this, this.getContextManager());
        isNullNode.setType(new DataTypeDescriptor(TypeId.BOOLEAN_ID, false));
        return isNullNode;
    }
    
    boolean verifyEliminateNots() {
        return true;
    }
    
    public ValueNode putAndsOnTop() throws StandardException {
        final NodeFactory nodeFactory = this.getNodeFactory();
        final AndNode andNode = (AndNode)nodeFactory.getNode(39, this, nodeFactory.getNode(38, Boolean.TRUE, this.getContextManager()), this.getContextManager());
        andNode.postBindFixup();
        return andNode;
    }
    
    public boolean verifyPutAndsOnTop() {
        return true;
    }
    
    public ValueNode changeToCNF(final boolean b) throws StandardException {
        return this;
    }
    
    public boolean verifyChangeToCNF() {
        return true;
    }
    
    public boolean categorize(final JBitSet set, final boolean b) throws StandardException {
        return true;
    }
    
    public String getSchemaName() throws StandardException {
        return null;
    }
    
    public String getTableName() {
        return null;
    }
    
    public boolean updatableByCursor() {
        return false;
    }
    
    public String getColumnName() {
        return null;
    }
    
    JBitSet getTablesReferenced() throws StandardException {
        final ReferencedTablesVisitor referencedTablesVisitor = new ReferencedTablesVisitor(new JBitSet(0));
        this.accept(referencedTablesVisitor);
        return referencedTablesVisitor.getTableMap();
    }
    
    public boolean isCloneable() {
        return false;
    }
    
    public ValueNode getClone() throws StandardException {
        return null;
    }
    
    public void copyFields(final ValueNode valueNode) throws StandardException {
        this.dataTypeServices = valueNode.getTypeServices();
    }
    
    public ValueNode remapColumnReferencesToExpressions() throws StandardException {
        return this;
    }
    
    public boolean isConstantExpression() {
        return false;
    }
    
    public boolean constantExpression(final PredicateList list) {
        return false;
    }
    
    protected int getOrderableVariantType() throws StandardException {
        return 0;
    }
    
    public ValueNode checkIsBoolean() throws StandardException {
        ValueNode genSQLJavaSQLTree = this;
        TypeId typeId = genSQLJavaSQLTree.getTypeId();
        if (typeId.userType()) {
            genSQLJavaSQLTree = genSQLJavaSQLTree.genSQLJavaSQLTree();
            typeId = genSQLJavaSQLTree.getTypeId();
        }
        if (!typeId.equals(TypeId.BOOLEAN_ID)) {
            throw StandardException.newException("42X19.S.1", typeId.getSQLTypeName());
        }
        return genSQLJavaSQLTree;
    }
    
    Object getConstantValueAsObject() throws StandardException {
        return null;
    }
    
    protected final void generate(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        this.generateExpression(activationClassBuilder, methodBuilder);
    }
    
    public void generateFilter(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        this.generateExpression(expressionClassBuilder, methodBuilder);
    }
    
    public double selectivity(final Optimizable optimizable) throws StandardException {
        if (this.transformed) {
            return 1.0;
        }
        return 0.5;
    }
    
    void checkTopPredicatesForEqualsConditions(final int n, final boolean[] array, final int[] array2, final JBitSet[] array3, final boolean b) throws StandardException {
        for (ValueNode rightOperand = this; rightOperand instanceof AndNode; rightOperand = ((AndNode)rightOperand).getRightOperand()) {
            final AndNode andNode = (AndNode)rightOperand;
            if (andNode.getLeftOperand().isRelationalOperator()) {
                if (((RelationalOperator)andNode.getLeftOperand()).getOperator() == 1) {
                    final BinaryRelationalOperatorNode binaryRelationalOperatorNode = (BinaryRelationalOperatorNode)andNode.getLeftOperand();
                    final ValueNode leftOperand = binaryRelationalOperatorNode.getLeftOperand();
                    final ValueNode rightOperand2 = binaryRelationalOperatorNode.getRightOperand();
                    int i = 0;
                    if (b) {
                        while (i < array2.length) {
                            if (array2[i] == n) {
                                break;
                            }
                            ++i;
                        }
                    }
                    else {
                        i = -1;
                    }
                    if (leftOperand instanceof ColumnReference && ((ColumnReference)leftOperand).getTableNumber() == n) {
                        this.updateMaps(array3, array, array2, n, i, rightOperand2, leftOperand);
                    }
                    else if (rightOperand2 instanceof ColumnReference && ((ColumnReference)rightOperand2).getTableNumber() == n) {
                        this.updateMaps(array3, array, array2, n, i, leftOperand, rightOperand2);
                    }
                }
            }
        }
    }
    
    boolean isBooleanTrue() {
        return false;
    }
    
    boolean isBooleanFalse() {
        return false;
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
    }
    
    private void updateMaps(final JBitSet[] array, final boolean[] array2, final int[] array3, final int n, final int n2, final ValueNode valueNode, final ValueNode valueNode2) throws StandardException {
        if (valueNode instanceof ConstantNode || valueNode.requiresTypeFromContext()) {
            this.setValueCols(array, array2, ((ColumnReference)valueNode2).getColumnNumber(), n2);
        }
        else if (valueNode instanceof ColumnReference && ((ColumnReference)valueNode).getTableNumber() != n) {
            final int tableNumber = ((ColumnReference)valueNode).getTableNumber();
            int n3 = 0;
            final int columnNumber = ((ColumnReference)valueNode2).getColumnNumber();
            while (n3 < array3.length && tableNumber != array3[n3]) {
                ++n3;
            }
            if (n3 == array3.length) {
                this.setValueCols(array, array2, columnNumber, n2);
            }
            else if (array != null) {
                array[n3].set(columnNumber);
            }
        }
        else {
            final JBitSet tablesReferenced = valueNode.getTablesReferenced();
            int n4 = 0;
            final int columnNumber2 = ((ColumnReference)valueNode2).getColumnNumber();
            while (n4 < array3.length && !tablesReferenced.get(array3[n4])) {
                ++n4;
            }
            if (n4 == array3.length) {
                this.setValueCols(array, array2, columnNumber2, n2);
            }
            else if (array != null && !tablesReferenced.get(n)) {
                array[n4].set(columnNumber2);
            }
        }
    }
    
    private void setValueCols(final JBitSet[] array, final boolean[] array2, final int n, final int n2) {
        if (array2 != null) {
            array2[n] = true;
        }
        if (array != null) {
            if (n2 == -1) {
                for (int i = 0; i < array.length; ++i) {
                    array[i].set(n);
                }
            }
            else {
                array[n2].set(n);
            }
        }
    }
    
    public boolean isRelationalOperator() {
        return false;
    }
    
    public boolean isBinaryEqualsOperatorNode() {
        return false;
    }
    
    public boolean isInListProbeNode() {
        return false;
    }
    
    public boolean optimizableEqualityNode(final Optimizable optimizable, final int n, final boolean b) throws StandardException {
        return false;
    }
    
    public boolean requiresTypeFromContext() {
        return false;
    }
    
    public boolean isParameterNode() {
        return false;
    }
    
    protected abstract boolean isEquivalent(final ValueNode p0) throws StandardException;
    
    protected final boolean isSameNodeType(final ValueNode valueNode) {
        return valueNode != null && valueNode.getNodeType() == this.getNodeType();
    }
}
