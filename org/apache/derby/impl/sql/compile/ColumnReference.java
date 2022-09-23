// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.iapi.sql.compile.NodeFactory;
import java.util.List;
import org.apache.derby.iapi.error.StandardException;
import java.util.ArrayList;

public class ColumnReference extends ValueNode
{
    String columnName;
    TableName tableName;
    private int tableNumber;
    private int columnNumber;
    private ResultColumn source;
    ResultColumn origSource;
    private String origName;
    int origTableNumber;
    int origColumnNumber;
    private int tableNumberBeforeFlattening;
    private int columnNumberBeforeFlattening;
    private boolean replacesAggregate;
    private boolean replacesWindowFunctionCall;
    private int nestingLevel;
    private int sourceLevel;
    private boolean scoped;
    private ArrayList remaps;
    
    public ColumnReference() {
        this.origTableNumber = -1;
        this.origColumnNumber = -1;
        this.tableNumberBeforeFlattening = -1;
        this.columnNumberBeforeFlattening = -1;
        this.nestingLevel = -1;
        this.sourceLevel = -1;
    }
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4) {
        this.columnName = (String)o;
        this.tableName = (TableName)o2;
        this.setBeginOffset((int)o3);
        this.setEndOffset((int)o4);
        this.tableNumber = -1;
        this.remaps = null;
    }
    
    public void init(final Object o, final Object o2) {
        this.columnName = (String)o;
        this.tableName = (TableName)o2;
        this.tableNumber = -1;
        this.remaps = null;
    }
    
    public String toString() {
        return "";
    }
    
    public void printSubNodes(final int n) {
    }
    
    boolean getCorrelated() {
        return this.sourceLevel != this.nestingLevel;
    }
    
    void setNestingLevel(final int nestingLevel) {
        this.nestingLevel = nestingLevel;
    }
    
    private int getNestingLevel() {
        return this.nestingLevel;
    }
    
    void setSourceLevel(final int sourceLevel) {
        this.sourceLevel = sourceLevel;
    }
    
    int getSourceLevel() {
        return this.sourceLevel;
    }
    
    public void markGeneratedToReplaceAggregate() {
        this.replacesAggregate = true;
    }
    
    public void markGeneratedToReplaceWindowFunctionCall() {
        this.replacesWindowFunctionCall = true;
    }
    
    public boolean getGeneratedToReplaceAggregate() {
        return this.replacesAggregate;
    }
    
    public boolean getGeneratedToReplaceWindowFunctionCall() {
        return this.replacesWindowFunctionCall;
    }
    
    public ValueNode getClone() throws StandardException {
        final ColumnReference columnReference = (ColumnReference)this.getNodeFactory().getNode(62, this.columnName, this.tableName, this.getContextManager());
        columnReference.copyFields(this);
        return columnReference;
    }
    
    public void copyFields(final ColumnReference columnReference) throws StandardException {
        super.copyFields(columnReference);
        this.tableName = columnReference.getTableNameNode();
        this.tableNumber = columnReference.getTableNumber();
        this.columnNumber = columnReference.getColumnNumber();
        this.source = columnReference.getSource();
        this.nestingLevel = columnReference.getNestingLevel();
        this.sourceLevel = columnReference.getSourceLevel();
        this.replacesAggregate = columnReference.getGeneratedToReplaceAggregate();
        this.replacesWindowFunctionCall = columnReference.getGeneratedToReplaceWindowFunctionCall();
        this.scoped = columnReference.isScoped();
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        if (list.size() == 0) {
            throw StandardException.newException("42X15", this.columnName);
        }
        if (list.bindColumnReference(this) == null) {
            throw StandardException.newException("42X04", this.getSQLColumnName());
        }
        return this;
    }
    
    public String getSQLColumnName() {
        if (this.tableName == null) {
            return this.columnName;
        }
        return this.tableName.toString() + "." + this.columnName;
    }
    
    public String getColumnName() {
        return this.columnName;
    }
    
    public int getTableNumber() {
        return this.tableNumber;
    }
    
    public void setTableNumber(final int tableNumber) {
        this.tableNumber = tableNumber;
    }
    
    public String getTableName() {
        return (this.tableName != null) ? this.tableName.getTableName() : null;
    }
    
    public String getSourceTableName() {
        return (this.source != null) ? this.source.getTableName() : null;
    }
    
    public String getSourceSchemaName() throws StandardException {
        return (this.source != null) ? this.source.getSchemaName() : null;
    }
    
    public boolean updatableByCursor() {
        return this.source != null && this.source.updatableByCursor();
    }
    
    public TableName getTableNameNode() {
        return this.tableName;
    }
    
    public void setTableNameNode(final TableName tableName) {
        this.tableName = tableName;
    }
    
    public int getColumnNumber() {
        return this.columnNumber;
    }
    
    public void setColumnNumber(final int columnNumber) {
        this.columnNumber = columnNumber;
    }
    
    public ResultColumn getSource() {
        return this.source;
    }
    
    public void setSource(final ResultColumn source) {
        this.source = source;
    }
    
    public ValueNode putAndsOnTop() throws StandardException {
        final NodeFactory nodeFactory = this.getNodeFactory();
        final BooleanConstantNode booleanConstantNode = (BooleanConstantNode)nodeFactory.getNode(38, Boolean.TRUE, this.getContextManager());
        final BinaryComparisonOperatorNode binaryComparisonOperatorNode = (BinaryComparisonOperatorNode)nodeFactory.getNode(41, this, booleanConstantNode, Boolean.FALSE, this.getContextManager());
        binaryComparisonOperatorNode.bindComparisonOperator();
        final ValueNode valueNode = (ValueNode)nodeFactory.getNode(39, binaryComparisonOperatorNode, booleanConstantNode, this.getContextManager());
        ((AndNode)valueNode).postBindFixup();
        return valueNode;
    }
    
    public boolean categorize(final JBitSet set, final boolean b) {
        set.set(this.tableNumber);
        return !this.replacesAggregate && !this.replacesWindowFunctionCall && (this.source.getExpression() instanceof ColumnReference || this.source.getExpression() instanceof VirtualColumnNode || this.source.getExpression() instanceof ConstantNode);
    }
    
    public void remapColumnReferences() {
        final ValueNode expression = this.source.getExpression();
        if (!(expression instanceof VirtualColumnNode) && !(expression instanceof ColumnReference)) {
            return;
        }
        if (this.scoped && this.origSource != null) {
            if (this.remaps == null) {
                this.remaps = new ArrayList();
            }
            this.remaps.add(new RemapInfo(this.columnNumber, this.tableNumber, this.columnName, this.source));
        }
        else {
            this.origSource = this.source;
            this.origName = this.columnName;
            this.origColumnNumber = this.columnNumber;
            this.origTableNumber = this.tableNumber;
        }
        this.source = this.getSourceResultColumn();
        this.columnName = this.source.getName();
        this.columnNumber = ((this.source.getExpression() instanceof VirtualColumnNode) ? this.source.getVirtualColumnId() : this.source.getColumnPosition());
        if (this.source.getExpression() instanceof ColumnReference) {
            this.tableNumber = ((ColumnReference)this.source.getExpression()).getTableNumber();
        }
    }
    
    public void unRemapColumnReferences() {
        if (this.origSource == null) {
            return;
        }
        if (this.remaps == null || this.remaps.size() == 0) {
            this.source = this.origSource;
            this.origSource = null;
            this.columnName = this.origName;
            this.origName = null;
            this.tableNumber = this.origTableNumber;
            this.columnNumber = this.origColumnNumber;
        }
        else {
            final RemapInfo remapInfo = this.remaps.remove(this.remaps.size() - 1);
            this.source = remapInfo.getSource();
            this.columnName = remapInfo.getColumnName();
            this.tableNumber = remapInfo.getTableNumber();
            this.columnNumber = remapInfo.getColumnNumber();
            if (this.remaps.size() == 0) {
                this.remaps = null;
            }
        }
    }
    
    protected boolean hasBeenRemapped() {
        return this.origSource != null;
    }
    
    public ResultColumn getSourceResultColumn() {
        return this.source.getExpression().getSourceResultColumn();
    }
    
    public ValueNode remapColumnReferencesToExpressions() throws StandardException {
        ResultColumn source = this.source;
        if (!this.source.isRedundant()) {
            return this;
        }
        ResultColumn sourceResultColumn;
        for (ResultColumn source2 = this.source; source2 != null && source2.isRedundant(); source2 = sourceResultColumn) {
            sourceResultColumn = source2.getExpression().getSourceResultColumn();
            if (sourceResultColumn != null && sourceResultColumn.isRedundant()) {
                source = sourceResultColumn;
            }
        }
        if (source.getExpression() instanceof VirtualColumnNode) {
            final ResultSetNode sourceResultSet = ((VirtualColumnNode)source.getExpression()).getSourceResultSet();
            if (sourceResultSet instanceof FromTable) {
                final FromTable fromTable = (FromTable)sourceResultSet;
                final ResultColumnList resultColumns = fromTable.getResultColumns();
                if (this.tableNumberBeforeFlattening == -1) {
                    this.tableNumberBeforeFlattening = this.tableNumber;
                    this.columnNumberBeforeFlattening = this.columnNumber;
                }
                ResultColumn resultColumn = resultColumns.getResultColumn(this.tableNumberBeforeFlattening, this.columnNumberBeforeFlattening, this.columnName);
                if (resultColumn == null) {
                    resultColumn = resultColumns.getResultColumn(this.columnName);
                }
                this.tableNumber = fromTable.getTableNumber();
                this.columnNumber = ((resultColumn.getExpression() instanceof VirtualColumnNode) ? resultColumn.getVirtualColumnId() : resultColumn.getColumnPosition());
            }
            this.source = source.getExpression().getSourceResultColumn();
            return this;
        }
        return source.getExpression().getClone();
    }
    
    void getTablesReferenced(final JBitSet set) {
        if (set.size() < this.tableNumber) {
            set.grow(this.tableNumber);
        }
        if (this.tableNumber != -1) {
            set.set(this.tableNumber);
        }
    }
    
    public boolean isCloneable() {
        return true;
    }
    
    public boolean constantExpression(final PredicateList list) {
        return list.constantColumn(this);
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        final int resultSetNumber = this.source.getResultSetNumber();
        if (this.source.isRedundant()) {
            this.source.generateExpression(expressionClassBuilder, methodBuilder);
            return;
        }
        expressionClassBuilder.pushColumnReference(methodBuilder, resultSetNumber, this.source.getVirtualColumnId());
        methodBuilder.cast(this.getTypeCompiler().interfaceName());
    }
    
    public String getSchemaName() {
        return (this.tableName != null) ? this.tableName.getSchemaName() : null;
    }
    
    protected int getOrderableVariantType() {
        return 1;
    }
    
    boolean pointsToColumnReference() {
        return this.source.getExpression() instanceof ColumnReference;
    }
    
    public DataTypeDescriptor getTypeServices() {
        if (this.source == null) {
            return super.getTypeServices();
        }
        return this.source.getTypeServices();
    }
    
    protected ResultSetNode getSourceResultSet(final int[] array) throws StandardException {
        if (this.source == null) {
            return null;
        }
        ResultColumn resultColumn = this.getSource();
        ValueNode valueNode = resultColumn.getExpression();
        array[0] = this.getColumnNumber();
        while (valueNode != null && (resultColumn.isRedundant() || valueNode instanceof ColumnReference)) {
            if (valueNode instanceof ColumnReference) {
                array[0] = ((ColumnReference)valueNode).getColumnNumber();
                resultColumn = ((ColumnReference)valueNode).getSource();
            }
            while (resultColumn.isRedundant()) {
                final ValueNode expression = resultColumn.getExpression();
                if (expression instanceof VirtualColumnNode) {
                    resultColumn = expression.getSourceResultColumn();
                }
                else {
                    if (!(expression instanceof ColumnReference)) {
                        break;
                    }
                    array[0] = ((ColumnReference)expression).getColumnNumber();
                    resultColumn = ((ColumnReference)expression).getSource();
                }
            }
            valueNode = resultColumn.getExpression();
        }
        if (valueNode != null && valueNode instanceof VirtualColumnNode) {
            return ((VirtualColumnNode)valueNode).getSourceResultSet();
        }
        array[0] = -1;
        return null;
    }
    
    protected boolean isEquivalent(final ValueNode valueNode) throws StandardException {
        if (!this.isSameNodeType(valueNode)) {
            return false;
        }
        final ColumnReference columnReference = (ColumnReference)valueNode;
        return this.tableNumber == columnReference.tableNumber && this.columnName.equals(columnReference.getColumnName());
    }
    
    protected void markAsScoped() {
        this.scoped = true;
    }
    
    protected boolean isScoped() {
        return this.scoped;
    }
    
    private class RemapInfo
    {
        int colNum;
        int tableNum;
        String colName;
        ResultColumn source;
        
        RemapInfo(final int colNum, final int tableNum, final String colName, final ResultColumn source) {
            this.colNum = colNum;
            this.tableNum = tableNum;
            this.colName = colName;
            this.source = source;
        }
        
        int getColumnNumber() {
            return this.colNum;
        }
        
        int getTableNumber() {
            return this.tableNum;
        }
        
        String getColumnName() {
            return this.colName;
        }
        
        ResultColumn getSource() {
            return this.source;
        }
        
        void setColNumber(final int colNum) {
            this.colNum = colNum;
        }
        
        void setTableNumber(final int tableNum) {
            this.tableNum = tableNum;
        }
        
        void setColName(final String colName) {
            this.colName = colName;
        }
        
        void setSource(final ResultColumn source) {
            this.source = source;
        }
    }
}
