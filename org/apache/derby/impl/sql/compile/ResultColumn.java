// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.util.StringUtil;
import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.StringDataValue;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import java.util.List;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptor;
import org.apache.derby.iapi.sql.ResultColumnDescriptor;

public class ResultColumn extends ValueNode implements ResultColumnDescriptor, Comparable
{
    String name;
    String exposedName;
    String tableName;
    String sourceTableName;
    String sourceSchemaName;
    ValueNode expression;
    ColumnDescriptor columnDescriptor;
    boolean isGenerated;
    boolean isGeneratedForUnmatchedColumnInInsert;
    boolean isGroupingColumn;
    boolean isReferenced;
    boolean isRedundant;
    boolean isNameGenerated;
    boolean updated;
    boolean updatableByCursor;
    private boolean defaultColumn;
    private boolean wasDefault;
    private boolean rightOuterJoinUsingClause;
    private JoinNode joinResultSet;
    boolean autoincrementGenerated;
    boolean autoincrement;
    private int resultSetNumber;
    ColumnReference reference;
    private int virtualColumnId;
    
    public ResultColumn() {
        this.joinResultSet = null;
        this.resultSetNumber = -1;
    }
    
    public void init(final Object o, final Object o2) throws StandardException {
        if (o instanceof String || o == null) {
            this.name = (String)o;
            this.exposedName = this.name;
            this.setExpression((ValueNode)o2);
        }
        else if (o instanceof ColumnReference) {
            final ColumnReference reference = (ColumnReference)o;
            this.name = reference.getColumnName();
            this.exposedName = reference.getColumnName();
            this.reference = reference;
            this.setExpression((ValueNode)o2);
        }
        else if (o instanceof ColumnDescriptor) {
            final ColumnDescriptor columnDescriptor = (ColumnDescriptor)o;
            this.name = columnDescriptor.getColumnName();
            this.exposedName = this.name;
            this.setType(columnDescriptor.getType());
            this.columnDescriptor = columnDescriptor;
            this.setExpression((ValueNode)o2);
            this.autoincrement = columnDescriptor.isAutoincrement();
        }
        else {
            this.setType((DataTypeDescriptor)o);
            this.setExpression((ValueNode)o2);
            if (o2 instanceof ColumnReference) {
                this.reference = (ColumnReference)o2;
            }
        }
        if (this.expression != null && this.expression.isInstanceOf(100)) {
            this.defaultColumn = true;
        }
    }
    
    public boolean isRightOuterJoinUsingClause() {
        return this.rightOuterJoinUsingClause;
    }
    
    public void setRightOuterJoinUsingClause(final boolean rightOuterJoinUsingClause) {
        this.rightOuterJoinUsingClause = rightOuterJoinUsingClause;
    }
    
    public JoinNode getJoinResultSet() {
        return this.joinResultSet;
    }
    
    public void setJoinResultset(final JoinNode joinResultSet) {
        this.joinResultSet = joinResultSet;
    }
    
    public boolean isDefaultColumn() {
        return this.defaultColumn;
    }
    
    public void setDefaultColumn(final boolean defaultColumn) {
        this.defaultColumn = defaultColumn;
    }
    
    public boolean wasDefaultColumn() {
        return this.wasDefault;
    }
    
    public void setWasDefaultColumn(final boolean wasDefault) {
        this.wasDefault = wasDefault;
    }
    
    boolean columnNameMatches(final String s) {
        return s.equals(this.exposedName) || s.equals(this.name) || s.equals(this.getSourceColumnName());
    }
    
    String getUnderlyingOrAliasName() {
        if (this.getSourceColumnName() != null) {
            return this.getSourceColumnName();
        }
        if (this.name != null) {
            return this.name;
        }
        return this.exposedName;
    }
    
    String getSourceColumnName() {
        if (this.expression instanceof ColumnReference) {
            return ((ColumnReference)this.expression).getColumnName();
        }
        return null;
    }
    
    public String getName() {
        return this.exposedName;
    }
    
    public String getSchemaName() throws StandardException {
        if (this.columnDescriptor != null && this.columnDescriptor.getTableDescriptor() != null) {
            return this.columnDescriptor.getTableDescriptor().getSchemaName();
        }
        if (this.expression != null) {
            return this.expression.getSchemaName();
        }
        return null;
    }
    
    public String getTableName() {
        if (this.tableName != null) {
            return this.tableName;
        }
        if (this.columnDescriptor != null && this.columnDescriptor.getTableDescriptor() != null) {
            return this.columnDescriptor.getTableDescriptor().getName();
        }
        return this.expression.getTableName();
    }
    
    public String getSourceTableName() {
        return this.sourceTableName;
    }
    
    public String getSourceSchemaName() {
        return this.sourceSchemaName;
    }
    
    public void clearTableName() {
        if (this.expression instanceof ColumnReference) {
            ((ColumnReference)this.expression).setTableNameNode(null);
        }
    }
    
    public DataTypeDescriptor getType() {
        return this.getTypeServices();
    }
    
    public int getColumnPosition() {
        if (this.columnDescriptor != null) {
            return this.columnDescriptor.getPosition();
        }
        return this.virtualColumnId;
    }
    
    public void setExpression(final ValueNode expression) {
        this.expression = expression;
    }
    
    public ValueNode getExpression() {
        return this.expression;
    }
    
    void setExpressionToNullNode() throws StandardException {
        this.setExpression(this.getNullNode(this.getTypeServices()));
    }
    
    public void setName(final String s) {
        if (this.name == null) {
            this.name = s;
        }
        this.exposedName = s;
    }
    
    public boolean isNameGenerated() {
        return this.isNameGenerated;
    }
    
    public void setNameGenerated(final boolean isNameGenerated) {
        this.isNameGenerated = isNameGenerated;
    }
    
    public void setResultSetNumber(final int resultSetNumber) {
        this.resultSetNumber = resultSetNumber;
    }
    
    public int getResultSetNumber() {
        return this.resultSetNumber;
    }
    
    public void adjustVirtualColumnId(final int n) {
        this.virtualColumnId += n;
    }
    
    public void setVirtualColumnId(final int virtualColumnId) {
        this.virtualColumnId = virtualColumnId;
    }
    
    public int getVirtualColumnId() {
        return this.virtualColumnId;
    }
    
    public void collapseVirtualColumnIdGap(final int n) {
        if (this.columnDescriptor == null && this.virtualColumnId > n) {
            --this.virtualColumnId;
        }
    }
    
    public void guaranteeColumnName() throws StandardException {
        if (this.exposedName == null) {
            this.exposedName = "SQLCol" + this.getCompilerContext().getNextColumnNumber();
            this.isNameGenerated = true;
        }
    }
    
    public String toString() {
        return "";
    }
    
    public void printSubNodes(final int n) {
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        if (this.expression.requiresTypeFromContext() && this.getTypeServices() != null) {
            this.expression.setType(this.getTypeServices());
        }
        if (this.expression.getTableName() == null) {
            list.isJoinColumnForRightOuterJoin(this);
        }
        this.setExpression(this.expression.bindExpression(list, list2, list3));
        if (this.expression instanceof ColumnReference) {
            this.autoincrement = ((ColumnReference)this.expression).getSource().isAutoincrement();
        }
        return this;
    }
    
    void bindResultColumnByPosition(final TableDescriptor tableDescriptor, final int virtualColumnId) throws StandardException {
        final ColumnDescriptor columnDescriptor = tableDescriptor.getColumnDescriptor(virtualColumnId);
        if (columnDescriptor == null) {
            String string = "";
            final String schemaName = tableDescriptor.getSchemaName();
            if (schemaName != null) {
                string = string + schemaName + ".";
            }
            throw StandardException.newException("42X06", string + tableDescriptor.getName());
        }
        this.setColumnDescriptor(tableDescriptor, columnDescriptor);
        this.setVirtualColumnId(virtualColumnId);
    }
    
    public void bindResultColumnByName(final TableDescriptor tableDescriptor, final int virtualColumnId) throws StandardException {
        final ColumnDescriptor columnDescriptor = tableDescriptor.getColumnDescriptor(this.exposedName);
        if (columnDescriptor == null) {
            String string = "";
            final String schemaName = tableDescriptor.getSchemaName();
            if (schemaName != null) {
                string = string + schemaName + ".";
            }
            throw StandardException.newException("42X14", this.exposedName, string + tableDescriptor.getName());
        }
        this.setColumnDescriptor(tableDescriptor, columnDescriptor);
        this.setVirtualColumnId(virtualColumnId);
        if (this.isPrivilegeCollectionRequired()) {
            this.getCompilerContext().addRequiredColumnPriv(columnDescriptor);
        }
    }
    
    public void typeUntypedNullExpression(final ResultColumn resultColumn) throws StandardException {
        if (resultColumn.getTypeId() == null) {
            throw StandardException.newException("42X07");
        }
        if (this.expression instanceof UntypedNullConstantNode) {
            this.setExpression(this.getNullNode(resultColumn.getTypeServices()));
        }
        else if (this.expression instanceof ColumnReference && this.expression.getTypeServices() == null) {
            this.expression.setType(resultColumn.getType());
        }
    }
    
    void setColumnDescriptor(final TableDescriptor tableDescriptor, final ColumnDescriptor columnDescriptor) throws StandardException {
        this.setType(columnDescriptor.getType());
        this.columnDescriptor = columnDescriptor;
        if (this.reference != null && this.reference.getTableName() != null && !tableDescriptor.getName().equals(this.reference.getTableName())) {
            throw StandardException.newException("42X55", tableDescriptor.getName(), this.reference.getTableName());
        }
    }
    
    public void bindResultColumnToExpression() throws StandardException {
        this.setType(this.expression.getTypeServices());
        if (this.expression instanceof ColumnReference) {
            final ColumnReference columnReference = (ColumnReference)this.expression;
            this.tableName = columnReference.getTableName();
            this.sourceTableName = columnReference.getSourceTableName();
            this.sourceSchemaName = columnReference.getSourceSchemaName();
        }
    }
    
    public void setSourceTableName(final String sourceTableName) {
        this.sourceTableName = sourceTableName;
    }
    
    public void setSourceSchemaName(final String sourceSchemaName) {
        this.sourceSchemaName = sourceSchemaName;
    }
    
    public ValueNode preprocess(final int n, final FromList list, final SubqueryList list2, final PredicateList list3) throws StandardException {
        if (this.expression == null) {
            return this;
        }
        this.setExpression(this.expression.preprocess(n, list, list2, list3));
        return this;
    }
    
    public void checkStorableExpression(final ResultColumn resultColumn) throws StandardException {
        this.checkStorableExpression((ValueNode)resultColumn);
    }
    
    private void checkStorableExpression(final ValueNode valueNode) throws StandardException {
        final TypeId typeId = valueNode.getTypeId();
        if (!this.getTypeCompiler().storable(typeId, this.getClassFactory())) {
            throw StandardException.newException("42821", this.getTypeId().getSQLTypeName(), typeId.getSQLTypeName());
        }
    }
    
    public void checkStorableExpression() throws StandardException {
        this.checkStorableExpression(this.getExpression());
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        this.expression.generateExpression(expressionClassBuilder, methodBuilder);
    }
    
    public void generateHolder(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        expressionClassBuilder.generateNull(methodBuilder, this.getTypeCompiler(), this.getTypeServices().getCollationType());
        methodBuilder.upCast("org.apache.derby.iapi.types.DataValueDescriptor");
    }
    
    boolean columnTypeAndLengthMatch() throws StandardException {
        if (this.getExpression().requiresTypeFromContext()) {
            return false;
        }
        if (this.getTypeId().isXMLTypeId()) {
            return false;
        }
        final DataTypeDescriptor typeServices = this.getExpression().getTypeServices();
        return this.getTypeServices().isExactTypeAndLengthMatch(typeServices) && (this.getTypeServices().isNullable() || !typeServices.isNullable());
    }
    
    boolean columnTypeAndLengthMatch(final ResultColumn resultColumn) throws StandardException {
        final ValueNode expression = resultColumn.getExpression();
        final DataTypeDescriptor typeServices = this.getTypeServices();
        DataTypeDescriptor dataTypeDescriptor = resultColumn.getTypeServices();
        if ((expression != null && expression.requiresTypeFromContext()) || this.expression.requiresTypeFromContext()) {
            return false;
        }
        if (typeServices.getTypeId().isXMLTypeId()) {
            return false;
        }
        if (!typeServices.getTypeId().equals(dataTypeDescriptor.getTypeId())) {
            if (expression instanceof ConstantNode) {
                final ConstantNode constantNode = (ConstantNode)resultColumn.getExpression();
                final DataValueDescriptor value = constantNode.getValue();
                final DataValueDescriptor convertConstant = this.convertConstant(typeServices.getTypeId(), typeServices.getMaximumWidth(), value);
                if (value != convertConstant && value instanceof StringDataValue == convertConstant instanceof StringDataValue) {
                    constantNode.setValue(convertConstant);
                    constantNode.setType(this.getTypeServices());
                    resultColumn.bindResultColumnToExpression();
                    dataTypeDescriptor = resultColumn.getType();
                }
                if (convertConstant instanceof StringDataValue) {
                    constantNode.setCollationInfo(typeServices);
                    constantNode.setValue(((StringDataValue)convertConstant).getValue(this.getDataValueFactory().getCharacterCollator(constantNode.getTypeServices().getCollationType())));
                }
            }
            if (!typeServices.getTypeId().equals(dataTypeDescriptor.getTypeId())) {
                return false;
            }
        }
        return typeServices.getPrecision() == dataTypeDescriptor.getPrecision() && typeServices.getScale() == dataTypeDescriptor.getScale() && typeServices.getMaximumWidth() == dataTypeDescriptor.getMaximumWidth() && (typeServices.isNullable() || (!dataTypeDescriptor.isNullable() && !resultColumn.isGeneratedForUnmatchedColumnInInsert()));
    }
    
    public boolean isGenerated() {
        return this.isGenerated;
    }
    
    public boolean isGeneratedForUnmatchedColumnInInsert() {
        return this.isGeneratedForUnmatchedColumnInInsert;
    }
    
    public void markGenerated() {
        this.isGenerated = true;
        this.isReferenced = true;
    }
    
    public void markGeneratedForUnmatchedColumnInInsert() {
        this.isGeneratedForUnmatchedColumnInInsert = true;
        this.isReferenced = true;
    }
    
    public boolean isReferenced() {
        return this.isReferenced;
    }
    
    public void setReferenced() {
        this.isReferenced = true;
    }
    
    void pullVirtualIsReferenced() {
        if (this.isReferenced()) {
            return;
        }
        ResultColumn sourceColumn;
        for (ValueNode valueNode = this.expression; valueNode != null && valueNode instanceof VirtualColumnNode; valueNode = sourceColumn.getExpression()) {
            sourceColumn = ((VirtualColumnNode)valueNode).getSourceColumn();
            if (sourceColumn.isReferenced()) {
                this.setReferenced();
                return;
            }
        }
    }
    
    public void setUnreferenced() {
        this.isReferenced = false;
    }
    
    void markAllRCsInChainReferenced() {
        this.setReferenced();
        ResultColumn sourceColumn;
        for (ValueNode valueNode = this.expression; valueNode instanceof VirtualColumnNode; valueNode = sourceColumn.getExpression()) {
            sourceColumn = ((VirtualColumnNode)valueNode).getSourceColumn();
            sourceColumn.setReferenced();
        }
    }
    
    public boolean isRedundant() {
        return this.isRedundant;
    }
    
    public void setRedundant() {
        this.isRedundant = true;
    }
    
    public void markAsGroupingColumn() {
        this.isGroupingColumn = true;
    }
    
    void rejectParameter() throws StandardException {
        if (this.expression != null && this.expression.isParameterNode()) {
            throw StandardException.newException("42X34");
        }
    }
    
    public int compareTo(final Object o) {
        return this.getColumnPosition() - ((ResultColumn)o).getColumnPosition();
    }
    
    void markUpdated() {
        this.updated = true;
    }
    
    void markUpdatableByCursor() {
        this.updatableByCursor = true;
    }
    
    boolean updated() {
        return this.updated;
    }
    
    public boolean updatableByCursor() {
        return this.updatableByCursor;
    }
    
    ResultColumn cloneMe() throws StandardException {
        ValueNode expression;
        if (this.expression instanceof ColumnReference) {
            expression = ((ColumnReference)this.expression).getClone();
        }
        else {
            expression = this.expression;
        }
        ResultColumn resultColumn;
        if (this.columnDescriptor != null) {
            resultColumn = (ResultColumn)this.getNodeFactory().getNode(80, this.columnDescriptor, this.expression, this.getContextManager());
            resultColumn.setExpression(expression);
        }
        else {
            resultColumn = (ResultColumn)this.getNodeFactory().getNode(80, this.getName(), expression, this.getContextManager());
        }
        resultColumn.setVirtualColumnId(this.getVirtualColumnId());
        resultColumn.setName(this.getName());
        resultColumn.setType(this.getTypeServices());
        resultColumn.setNameGenerated(this.isNameGenerated());
        resultColumn.setSourceTableName(this.getSourceTableName());
        resultColumn.setSourceSchemaName(this.getSourceSchemaName());
        if (this.isGeneratedForUnmatchedColumnInInsert()) {
            resultColumn.markGeneratedForUnmatchedColumnInInsert();
        }
        if (this.isReferenced()) {
            resultColumn.setReferenced();
        }
        if (this.updated()) {
            resultColumn.markUpdated();
        }
        if (this.updatableByCursor()) {
            resultColumn.markUpdatableByCursor();
        }
        if (this.isAutoincrementGenerated()) {
            resultColumn.setAutoincrementGenerated();
        }
        if (this.isAutoincrement()) {
            resultColumn.setAutoincrement();
        }
        if (this.isGroupingColumn()) {
            resultColumn.markAsGroupingColumn();
        }
        if (this.isRightOuterJoinUsingClause()) {
            resultColumn.setRightOuterJoinUsingClause(true);
        }
        if (this.getJoinResultSet() != null) {
            resultColumn.setJoinResultset(this.getJoinResultSet());
        }
        if (this.isGenerated()) {
            resultColumn.markGenerated();
        }
        return resultColumn;
    }
    
    public int getMaximumColumnSize() {
        return this.getTypeServices().getTypeId().getApproximateLengthInBytes(this.getTypeServices());
    }
    
    public DataTypeDescriptor getTypeServices() {
        final DataTypeDescriptor typeServices = super.getTypeServices();
        if (typeServices != null) {
            return typeServices;
        }
        if (this.getExpression() != null) {
            return this.getExpression().getTypeServices();
        }
        return null;
    }
    
    protected int getOrderableVariantType() throws StandardException {
        int orderableVariantType;
        if (this.isAutoincrementGenerated()) {
            orderableVariantType = 0;
        }
        else if (this.expression != null) {
            orderableVariantType = this.expression.getOrderableVariantType();
        }
        else {
            orderableVariantType = 3;
        }
        switch (orderableVariantType) {
            case 0: {
                return 0;
            }
            case 1:
            case 2: {
                return 1;
            }
            default: {
                return 3;
            }
        }
    }
    
    void acceptChildren(final Visitor visitor) throws StandardException {
        super.acceptChildren(visitor);
        if (this.expression != null) {
            this.setExpression((ValueNode)this.expression.accept(visitor));
        }
    }
    
    public boolean foundInList(final String[] array) {
        return this.foundString(array, this.name);
    }
    
    void verifyOrderable() throws StandardException {
        if (!this.getTypeId().orderable(this.getClassFactory())) {
            throw StandardException.newException("X0X67.S", this.getTypeId().getSQLTypeName());
        }
    }
    
    ColumnDescriptor getTableColumnDescriptor() {
        return this.columnDescriptor;
    }
    
    public boolean isAutoincrementGenerated() {
        return this.autoincrementGenerated;
    }
    
    public void setAutoincrementGenerated() {
        this.autoincrementGenerated = true;
    }
    
    public void resetAutoincrementGenerated() {
        this.autoincrementGenerated = false;
    }
    
    public boolean isAutoincrement() {
        return this.autoincrement;
    }
    
    public void setAutoincrement() {
        this.autoincrement = true;
    }
    
    public boolean isGroupingColumn() {
        return this.isGroupingColumn;
    }
    
    private DataValueDescriptor convertConstant(final TypeId typeId, final int n, final DataValueDescriptor dataValueDescriptor) throws StandardException {
        final int typeFormatId = typeId.getTypeFormatId();
        final DataValueFactory dataValueFactory = this.getDataValueFactory();
        switch (typeFormatId) {
            default: {
                return dataValueDescriptor;
            }
            case 13: {
                final String string = dataValueDescriptor.getString();
                final int length = string.length();
                if (length <= n && typeFormatId == 13) {
                    return dataValueFactory.getVarcharDataValue(string);
                }
                for (int i = n; i < length; ++i) {
                    if (string.charAt(i) != ' ') {
                        Object o = null;
                        if (typeFormatId == 13) {
                            o = "VARCHAR";
                        }
                        throw StandardException.newException("22001", o, StringUtil.formatForPrint(string), String.valueOf(n));
                    }
                }
                if (typeFormatId == 13) {
                    return dataValueFactory.getVarcharDataValue(string.substring(0, n));
                }
                return dataValueFactory.getLongvarcharDataValue(dataValueDescriptor.getString());
            }
            case 230: {
                return dataValueFactory.getLongvarcharDataValue(dataValueDescriptor.getString());
            }
        }
    }
    
    public TableName getTableNameObject() {
        return null;
    }
    
    public ColumnReference getReference() {
        return this.reference;
    }
    
    public BaseColumnNode getBaseColumnNode() {
        ValueNode valueNode = this.expression;
        while (true) {
            if (valueNode instanceof ResultColumn) {
                valueNode = ((ResultColumn)valueNode).expression;
            }
            else if (valueNode instanceof ColumnReference) {
                valueNode = ((ColumnReference)valueNode).getSource();
            }
            else {
                if (!(valueNode instanceof VirtualColumnNode)) {
                    break;
                }
                valueNode = ((VirtualColumnNode)valueNode).getSourceColumn();
            }
        }
        if (valueNode instanceof BaseColumnNode) {
            return (BaseColumnNode)valueNode;
        }
        return null;
    }
    
    public int getTableNumber() throws StandardException {
        if (this.expression instanceof ColumnReference) {
            return ((ColumnReference)this.expression).getTableNumber();
        }
        if (!(this.expression instanceof VirtualColumnNode)) {
            return -1;
        }
        final VirtualColumnNode virtualColumnNode = (VirtualColumnNode)this.expression;
        if (virtualColumnNode.getSourceResultSet() instanceof FromBaseTable) {
            return ((FromBaseTable)virtualColumnNode.getSourceResultSet()).getTableNumber();
        }
        return virtualColumnNode.getSourceColumn().getTableNumber();
    }
    
    public boolean isEquivalent(final ValueNode valueNode) throws StandardException {
        if (valueNode.getNodeType() == this.getNodeType()) {
            final ResultColumn resultColumn = (ResultColumn)valueNode;
            if (this.expression != null) {
                return this.expression.isEquivalent(resultColumn.expression);
            }
        }
        return false;
    }
    
    public boolean hasGenerationClause() {
        return this.columnDescriptor != null && this.columnDescriptor.hasGenerationClause();
    }
}
