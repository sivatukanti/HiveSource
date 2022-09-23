// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.sql.compile.Optimizable;
import org.apache.derby.iapi.util.JBitSet;

public class BinaryRelationalOperatorNode extends BinaryComparisonOperatorNode implements RelationalOperator
{
    private int operatorType;
    private BaseTableNumbersVisitor btnVis;
    JBitSet optBaseTables;
    JBitSet valNodeBaseTables;
    private InListOperatorNode inListProbeSource;
    protected static final int LEFT = -1;
    protected static final int NEITHER = 0;
    protected static final int RIGHT = 1;
    
    public BinaryRelationalOperatorNode() {
        this.inListProbeSource = null;
    }
    
    public void init(final Object o, final Object o2, final Object o3) {
        String s = "";
        String s2 = "";
        switch (this.getNodeType()) {
            case 41: {
                s = "equals";
                s2 = "=";
                this.operatorType = 1;
                break;
            }
            case 42: {
                s = "greaterOrEquals";
                s2 = ">=";
                this.operatorType = 4;
                break;
            }
            case 43: {
                s = "greaterThan";
                s2 = ">";
                this.operatorType = 3;
                break;
            }
            case 44: {
                s = "lessOrEquals";
                s2 = "<=";
                this.operatorType = 6;
                break;
            }
            case 45: {
                s = "lessThan";
                s2 = "<";
                this.operatorType = 5;
                break;
            }
            case 47: {
                s = "notEquals";
                s2 = "<>";
                this.operatorType = 2;
                break;
            }
        }
        super.init(o, o2, s2, s, o3);
        this.btnVis = null;
    }
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4) {
        this.init(o, o2, o4);
        this.inListProbeSource = (InListOperatorNode)o3;
    }
    
    protected InListOperatorNode getInListOp() {
        if (this.inListProbeSource != null) {
            this.inListProbeSource.setLeftOperand(this.leftOperand);
        }
        return this.inListProbeSource;
    }
    
    public ColumnReference getColumnOperand(final Optimizable optimizable, final int n) {
        final FromTable fromTable = (FromTable)optimizable;
        boolean b = true;
        if (this.leftOperand instanceof ColumnReference) {
            final ColumnReference columnReference = (ColumnReference)this.leftOperand;
            if (this.valNodeReferencesOptTable(columnReference, fromTable, false, b) && columnReference.getSource().getColumnPosition() == n) {
                return columnReference;
            }
            b = false;
        }
        if (this.rightOperand instanceof ColumnReference) {
            final ColumnReference columnReference2 = (ColumnReference)this.rightOperand;
            if (this.valNodeReferencesOptTable(columnReference2, fromTable, false, b) && columnReference2.getSource().getColumnPosition() == n) {
                return columnReference2;
            }
        }
        return null;
    }
    
    public ColumnReference getColumnOperand(final Optimizable optimizable) {
        boolean b = true;
        if (this.leftOperand instanceof ColumnReference) {
            final ColumnReference columnReference = (ColumnReference)this.leftOperand;
            if (this.valNodeReferencesOptTable(columnReference, (FromTable)optimizable, false, b)) {
                return columnReference;
            }
            b = false;
        }
        if (this.rightOperand instanceof ColumnReference) {
            final ColumnReference columnReference2 = (ColumnReference)this.rightOperand;
            if (this.valNodeReferencesOptTable(columnReference2, (FromTable)optimizable, false, b)) {
                return columnReference2;
            }
        }
        return null;
    }
    
    public ValueNode getExpressionOperand(final int n, final int n2, final FromTable fromTable) {
        boolean b = true;
        if (this.leftOperand instanceof ColumnReference) {
            final ColumnReference columnReference = (ColumnReference)this.leftOperand;
            if (this.valNodeReferencesOptTable(columnReference, fromTable, false, b) && columnReference.getSource().getColumnPosition() == n2) {
                return this.rightOperand;
            }
            b = false;
        }
        if (this.rightOperand instanceof ColumnReference) {
            final ColumnReference columnReference2 = (ColumnReference)this.rightOperand;
            if (this.valNodeReferencesOptTable(columnReference2, fromTable, false, b) && columnReference2.getSource().getColumnPosition() == n2) {
                return this.leftOperand;
            }
        }
        return null;
    }
    
    public ValueNode getOperand(final ColumnReference columnReference, final int n, final boolean b) {
        this.initBaseTableVisitor(n, true);
        try {
            this.btnVis.setTableMap(this.optBaseTables);
            columnReference.accept(this.btnVis);
            this.btnVis.setTableMap(this.valNodeBaseTables);
            if (this.leftOperand instanceof ColumnReference) {
                final ColumnReference columnReference2 = (ColumnReference)this.leftOperand;
                columnReference2.accept(this.btnVis);
                this.valNodeBaseTables.and(this.optBaseTables);
                if (this.valNodeBaseTables.getFirstSetBit() != -1 && columnReference2.getSource().getColumnPosition() == columnReference.getColumnNumber()) {
                    if (b) {
                        return this.rightOperand;
                    }
                    return this.leftOperand;
                }
            }
            if (this.rightOperand instanceof ColumnReference) {
                this.valNodeBaseTables.clearAll();
                final ColumnReference columnReference3 = (ColumnReference)this.rightOperand;
                columnReference3.accept(this.btnVis);
                this.valNodeBaseTables.and(this.optBaseTables);
                if (this.valNodeBaseTables.getFirstSetBit() != -1 && columnReference3.getSource().getColumnPosition() == columnReference.getColumnNumber()) {
                    if (b) {
                        return this.leftOperand;
                    }
                    return this.rightOperand;
                }
            }
        }
        catch (StandardException ex) {}
        return null;
    }
    
    public void generateExpressionOperand(final Optimizable optimizable, final int n, final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        final FromBaseTable fromBaseTable = (FromBaseTable)optimizable;
        this.getExpressionOperand(fromBaseTable.getTableNumber(), n, fromBaseTable).generateExpression(expressionClassBuilder, methodBuilder);
    }
    
    public boolean selfComparison(final ColumnReference columnReference) throws StandardException {
        ValueNode valueNode;
        if (this.leftOperand == columnReference) {
            valueNode = this.rightOperand;
        }
        else if (this.rightOperand == columnReference) {
            valueNode = this.leftOperand;
        }
        else {
            valueNode = null;
        }
        return valueNode.getTablesReferenced().get(columnReference.getTableNumber());
    }
    
    public boolean usefulStartKey(final Optimizable optimizable) {
        final int columnOnOneSide = this.columnOnOneSide(optimizable);
        return columnOnOneSide != 0 && this.usefulStartKey(columnOnOneSide == -1);
    }
    
    protected boolean keyColumnOnLeft(final Optimizable optimizable) {
        boolean b = false;
        if (this.leftOperand instanceof ColumnReference && this.valNodeReferencesOptTable(this.leftOperand, (FromTable)optimizable, false, true)) {
            b = true;
        }
        return b;
    }
    
    protected int columnOnOneSide(final Optimizable optimizable) {
        boolean b = true;
        if (this.leftOperand instanceof ColumnReference) {
            if (this.valNodeReferencesOptTable(this.leftOperand, (FromTable)optimizable, false, b)) {
                return -1;
            }
            b = false;
        }
        if (this.rightOperand instanceof ColumnReference && this.valNodeReferencesOptTable(this.rightOperand, (FromTable)optimizable, false, b)) {
            return 1;
        }
        return 0;
    }
    
    public boolean usefulStopKey(final Optimizable optimizable) {
        final int columnOnOneSide = this.columnOnOneSide(optimizable);
        return columnOnOneSide != 0 && this.usefulStopKey(columnOnOneSide == -1);
    }
    
    public void generateAbsoluteColumnId(final MethodBuilder methodBuilder, final Optimizable optimizable) {
        methodBuilder.push(this.getAbsoluteColumnPosition(optimizable));
    }
    
    public void generateRelativeColumnId(final MethodBuilder methodBuilder, final Optimizable optimizable) {
        methodBuilder.push(optimizable.convertAbsoluteToRelativeColumnPosition(this.getAbsoluteColumnPosition(optimizable)));
    }
    
    private int getAbsoluteColumnPosition(final Optimizable optimizable) {
        ColumnReference columnReference;
        if (this.keyColumnOnLeft(optimizable)) {
            columnReference = (ColumnReference)this.leftOperand;
        }
        else {
            columnReference = (ColumnReference)this.rightOperand;
        }
        final ConglomerateDescriptor conglomerateDescriptor = optimizable.getTrulyTheBestAccessPath().getConglomerateDescriptor();
        int n = columnReference.getSource().getColumnPosition();
        if (conglomerateDescriptor != null && conglomerateDescriptor.isIndex()) {
            n = conglomerateDescriptor.getIndexDescriptor().getKeyColumnPosition(n);
        }
        return n - 1;
    }
    
    public void generateQualMethod(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder, final Optimizable optimizable) throws StandardException {
        final MethodBuilder userExprFun = expressionClassBuilder.newUserExprFun();
        if (this.keyColumnOnLeft(optimizable)) {
            this.rightOperand.generateExpression(expressionClassBuilder, userExprFun);
        }
        else {
            this.leftOperand.generateExpression(expressionClassBuilder, userExprFun);
        }
        userExprFun.methodReturn();
        userExprFun.complete();
        expressionClassBuilder.pushMethodReference(methodBuilder, userExprFun);
    }
    
    public void generateOrderedNulls(final MethodBuilder methodBuilder) {
        methodBuilder.push(false);
    }
    
    public boolean orderedNulls() {
        return false;
    }
    
    public boolean isQualifier(final Optimizable optimizable, final boolean b) throws StandardException {
        if (this.isInListProbeNode()) {
            return false;
        }
        ValueNode valueNode = null;
        int n = 0;
        boolean b2 = true;
        final FromTable fromTable = (FromTable)optimizable;
        if (this.leftOperand instanceof ColumnReference) {
            if (this.valNodeReferencesOptTable(this.leftOperand, fromTable, b, b2)) {
                valueNode = this.rightOperand;
                n = 1;
            }
            b2 = false;
        }
        if (n == 0 && this.rightOperand instanceof ColumnReference && this.valNodeReferencesOptTable(this.rightOperand, fromTable, b, b2)) {
            valueNode = this.leftOperand;
            n = 1;
        }
        return n != 0 && !this.valNodeReferencesOptTable(valueNode, fromTable, b, true);
    }
    
    public int getOrderableVariantType(final Optimizable optimizable) throws StandardException {
        if (this.keyColumnOnLeft(optimizable)) {
            return this.rightOperand.getOrderableVariantType();
        }
        return this.leftOperand.getOrderableVariantType();
    }
    
    public boolean compareWithKnownConstant(final Optimizable optimizable, final boolean b) {
        final ValueNode valueNode = this.keyColumnOnLeft(optimizable) ? this.rightOperand : this.leftOperand;
        if (b) {
            return valueNode instanceof ConstantNode || (valueNode.requiresTypeFromContext() && ((ParameterNode)valueNode).getDefaultValue() != null);
        }
        return valueNode instanceof ConstantNode;
    }
    
    public DataValueDescriptor getCompareValue(final Optimizable optimizable) throws StandardException {
        final ValueNode valueNode = this.keyColumnOnLeft(optimizable) ? this.rightOperand : this.leftOperand;
        if (valueNode instanceof ConstantNode) {
            return ((ConstantNode)valueNode).getValue();
        }
        if (valueNode.requiresTypeFromContext()) {
            ParameterNode parameterOperand;
            if (valueNode instanceof UnaryOperatorNode) {
                parameterOperand = ((UnaryOperatorNode)valueNode).getParameterOperand();
            }
            else {
                parameterOperand = (ParameterNode)valueNode;
            }
            return parameterOperand.getDefaultValue();
        }
        return null;
    }
    
    protected double booleanSelectivity(final Optimizable optimizable) throws StandardException {
        TypeId typeId = null;
        double n = -1.0;
        final int columnOnOneSide = this.columnOnOneSide(optimizable);
        if (columnOnOneSide == -1) {
            typeId = this.leftOperand.getTypeId();
        }
        else if (columnOnOneSide == 1) {
            typeId = this.rightOperand.getTypeId();
        }
        if (typeId != null && (typeId.getJDBCTypeId() == -7 || typeId.getJDBCTypeId() == 16)) {
            n = 0.5;
        }
        return n;
    }
    
    public String getReceiverInterfaceName() {
        return "org.apache.derby.iapi.types.DataValueDescriptor";
    }
    
    ValueNode evaluateConstantExpressions() throws StandardException {
        if (this.leftOperand instanceof ConstantNode && this.rightOperand instanceof ConstantNode) {
            final ConstantNode constantNode = (ConstantNode)this.leftOperand;
            final ConstantNode constantNode2 = (ConstantNode)this.rightOperand;
            final DataValueDescriptor value = constantNode.getValue();
            final DataValueDescriptor value2 = constantNode2.getValue();
            if (!value.isNull() && !value2.isNull()) {
                final int compare = value.compare(value2);
                switch (this.operatorType) {
                    case 1: {
                        return this.newBool(compare == 0);
                    }
                    case 2: {
                        return this.newBool(compare != 0);
                    }
                    case 3: {
                        return this.newBool(compare > 0);
                    }
                    case 4: {
                        return this.newBool(compare >= 0);
                    }
                    case 5: {
                        return this.newBool(compare < 0);
                    }
                    case 6: {
                        return this.newBool(compare <= 0);
                    }
                }
            }
        }
        return this;
    }
    
    private ValueNode newBool(final boolean b) throws StandardException {
        return (ValueNode)this.getNodeFactory().getNode(38, b, this.getContextManager());
    }
    
    BinaryOperatorNode getNegation(final ValueNode valueNode, final ValueNode valueNode2) throws StandardException {
        final BinaryOperatorNode binaryOperatorNode = (BinaryOperatorNode)this.getNodeFactory().getNode(this.getNegationNode(), valueNode, valueNode2, Boolean.FALSE, this.getContextManager());
        binaryOperatorNode.setType(this.getTypeServices());
        return binaryOperatorNode;
    }
    
    private int getNegationNode() {
        switch (this.getNodeType()) {
            case 41: {
                return 47;
            }
            case 42: {
                return 45;
            }
            case 43: {
                return 44;
            }
            case 45: {
                return 42;
            }
            case 44: {
                return 43;
            }
            case 47: {
                return 41;
            }
            default: {
                return -1;
            }
        }
    }
    
    BinaryOperatorNode getSwappedEquivalent() throws StandardException {
        final BinaryOperatorNode binaryOperatorNode = (BinaryOperatorNode)this.getNodeFactory().getNode(this.getNodeTypeForSwap(), this.rightOperand, this.leftOperand, Boolean.FALSE, this.getContextManager());
        binaryOperatorNode.setType(this.getTypeServices());
        return binaryOperatorNode;
    }
    
    private int getNodeTypeForSwap() {
        switch (this.getNodeType()) {
            case 41: {
                return 41;
            }
            case 42: {
                return 44;
            }
            case 43: {
                return 45;
            }
            case 45: {
                return 43;
            }
            case 44: {
                return 42;
            }
            case 47: {
                return 47;
            }
            default: {
                return -1;
            }
        }
    }
    
    protected boolean usefulStartKey(final boolean b) {
        switch (this.operatorType) {
            case 1: {
                return true;
            }
            case 2: {
                return false;
            }
            case 3:
            case 4: {
                return b;
            }
            case 5:
            case 6: {
                return !b;
            }
            default: {
                return false;
            }
        }
    }
    
    protected boolean usefulStopKey(final boolean b) {
        switch (this.operatorType) {
            case 1: {
                return true;
            }
            case 2: {
                return false;
            }
            case 3:
            case 4: {
                return !b;
            }
            case 5:
            case 6: {
                return b;
            }
            default: {
                return false;
            }
        }
    }
    
    public int getStartOperator(final Optimizable optimizable) {
        switch (this.operatorType) {
            case 1:
            case 4:
            case 6: {
                return 1;
            }
            case 3:
            case 5: {
                return -1;
            }
            case 2: {
                return 0;
            }
            default: {
                return 0;
            }
        }
    }
    
    public int getStopOperator(final Optimizable optimizable) {
        switch (this.operatorType) {
            case 1:
            case 4:
            case 6: {
                return -1;
            }
            case 3:
            case 5: {
                return 1;
            }
            case 2: {
                return 0;
            }
            default: {
                return 0;
            }
        }
    }
    
    public void generateOperator(final MethodBuilder methodBuilder, final Optimizable optimizable) {
        switch (this.operatorType) {
            case 1: {
                methodBuilder.push(2);
                break;
            }
            case 2: {
                methodBuilder.push(2);
                break;
            }
            case 4:
            case 5: {
                methodBuilder.push(this.keyColumnOnLeft(optimizable) ? 1 : 3);
                break;
            }
            case 3:
            case 6: {
                methodBuilder.push(this.keyColumnOnLeft(optimizable) ? 3 : 1);
                break;
            }
        }
    }
    
    public void generateNegate(final MethodBuilder methodBuilder, final Optimizable optimizable) {
        switch (this.operatorType) {
            case 1: {
                methodBuilder.push(false);
                break;
            }
            case 2: {
                methodBuilder.push(true);
                break;
            }
            case 5:
            case 6: {
                methodBuilder.push(!this.keyColumnOnLeft(optimizable));
                break;
            }
            case 3:
            case 4: {
                methodBuilder.push(this.keyColumnOnLeft(optimizable));
                break;
            }
        }
    }
    
    public int getOperator() {
        return this.operatorType;
    }
    
    public double selectivity(final Optimizable optimizable) throws StandardException {
        final double booleanSelectivity = this.booleanSelectivity(optimizable);
        if (booleanSelectivity >= 0.0) {
            return booleanSelectivity;
        }
        switch (this.operatorType) {
            case 1: {
                return 0.1;
            }
            case 2:
            case 4:
            case 5:
            case 6: {
                if (this.getBetweenSelectivity()) {
                    return 0.5;
                }
                return 0.33;
            }
            case 3: {
                return 0.33;
            }
            default: {
                return 0.0;
            }
        }
    }
    
    public RelationalOperator getTransitiveSearchClause(final ColumnReference columnReference) throws StandardException {
        return (RelationalOperator)this.getNodeFactory().getNode(this.getNodeType(), columnReference, this.rightOperand, Boolean.FALSE, this.getContextManager());
    }
    
    public boolean equalsComparisonWithConstantExpression(final Optimizable optimizable) {
        if (this.operatorType != 1) {
            return false;
        }
        boolean b = false;
        final int columnOnOneSide = this.columnOnOneSide(optimizable);
        if (columnOnOneSide == -1) {
            b = this.rightOperand.isConstantExpression();
        }
        else if (columnOnOneSide == 1) {
            b = this.leftOperand.isConstantExpression();
        }
        return b;
    }
    
    public boolean isRelationalOperator() {
        return !this.isInListProbeNode();
    }
    
    public boolean isBinaryEqualsOperatorNode() {
        return !this.isInListProbeNode() && this.operatorType == 1;
    }
    
    public boolean isInListProbeNode() {
        return this.inListProbeSource != null;
    }
    
    public boolean optimizableEqualityNode(final Optimizable optimizable, final int n, final boolean b) throws StandardException {
        if (this.operatorType != 1) {
            return false;
        }
        if (this.isInListProbeNode()) {
            return false;
        }
        final ColumnReference columnOperand = this.getColumnOperand(optimizable, n);
        return columnOperand != null && !this.selfComparison(columnOperand) && !this.implicitVarcharComparison();
    }
    
    private boolean implicitVarcharComparison() throws StandardException {
        final TypeId typeId = this.leftOperand.getTypeId();
        final TypeId typeId2 = this.rightOperand.getTypeId();
        return (typeId.isStringTypeId() && !typeId2.isStringTypeId()) || (typeId2.isStringTypeId() && !typeId.isStringTypeId());
    }
    
    public ValueNode genSQLJavaSQLTree() throws StandardException {
        if (this.operatorType == 1) {
            return this;
        }
        return super.genSQLJavaSQLTree();
    }
    
    public ValueNode getScopedOperand(final int n, final JBitSet set, final ResultSetNode resultSetNode, final int[] array) throws StandardException {
        final ColumnReference columnReference = (ColumnReference)((n == -1) ? this.leftOperand : ((ColumnReference)this.rightOperand));
        final JBitSet set2 = new JBitSet(set.size());
        columnReference.accept(new BaseTableNumbersVisitor(set2));
        if (!set.contains(set2)) {
            return columnReference.getClone();
        }
        ResultColumn resultColumn;
        if (array[0] == -1) {
            final int[] array2 = { -1 };
            resultColumn = resultSetNode.getResultColumns().getResultColumn(array2[0], columnReference.getSourceResultSet(array2), array);
        }
        else {
            resultColumn = resultSetNode.getResultColumns().getResultColumn(array[0]);
        }
        if (resultColumn.getExpression() instanceof ColumnReference) {
            final ColumnReference columnReference2 = (ColumnReference)((ColumnReference)resultColumn.getExpression()).getClone();
            columnReference2.markAsScoped();
            return columnReference2;
        }
        return resultColumn.getExpression();
    }
    
    private boolean valNodeReferencesOptTable(final ValueNode valueNode, final FromTable fromTable, final boolean b, final boolean b2) {
        this.initBaseTableVisitor(fromTable.getReferencedTableMap().size(), b2);
        boolean b3 = false;
        try {
            if (b2) {
                this.buildTableNumList(fromTable, b);
            }
            this.btnVis.setTableMap(this.valNodeBaseTables);
            valueNode.accept(this.btnVis);
            this.valNodeBaseTables.and(this.optBaseTables);
            b3 = (this.valNodeBaseTables.getFirstSetBit() != -1);
        }
        catch (StandardException ex) {}
        return b3;
    }
    
    private void initBaseTableVisitor(final int n, final boolean b) {
        if (this.valNodeBaseTables == null) {
            this.valNodeBaseTables = new JBitSet(n);
        }
        else {
            this.valNodeBaseTables.clearAll();
        }
        if (b) {
            if (this.optBaseTables == null) {
                this.optBaseTables = new JBitSet(n);
            }
            else {
                this.optBaseTables.clearAll();
            }
        }
        if (this.btnVis == null) {
            this.btnVis = new BaseTableNumbersVisitor(this.valNodeBaseTables);
        }
    }
    
    private void buildTableNumList(final FromTable fromTable, final boolean b) throws StandardException {
        if (fromTable.getTableNumber() >= 0) {
            this.optBaseTables.set(fromTable.getTableNumber());
        }
        if (b) {
            return;
        }
        this.optBaseTables.or(fromTable.getReferencedTableMap());
        this.btnVis.setTableMap(this.optBaseTables);
        fromTable.accept(this.btnVis);
    }
}
