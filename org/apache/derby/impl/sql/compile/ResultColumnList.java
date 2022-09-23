// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.NodeFactory;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.catalog.types.DefaultInfoImpl;
import java.util.Map;
import java.util.HashMap;
import java.sql.ResultSetMetaData;
import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptorList;
import java.util.Arrays;
import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.util.ReuseFactory;
import org.apache.derby.iapi.sql.ResultColumnDescriptor;
import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.sql.execute.ExecRowBuilder;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.store.access.StoreCostController;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.services.compiler.LocalField;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.catalog.UUID;
import org.apache.derby.catalog.DefaultInfo;
import org.apache.derby.iapi.types.DataValueDescriptor;
import java.util.HashSet;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import java.util.List;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptor;
import org.apache.derby.iapi.error.StandardException;

public class ResultColumnList extends QueryTreeNodeVector
{
    protected boolean indexRow;
    protected long conglomerateId;
    int orderBySelect;
    protected boolean forUpdate;
    private boolean countMismatchAllowed;
    private int initialListSize;
    
    public ResultColumnList() {
        this.orderBySelect = 0;
        this.initialListSize = 0;
    }
    
    public void addResultColumn(final ResultColumn resultColumn) {
        resultColumn.setVirtualColumnId(this.size() + 1);
        this.addElement(resultColumn);
    }
    
    public void appendResultColumns(final ResultColumnList list, final boolean b) {
        int virtualColumnId = this.size() + 1;
        for (int size = list.size(), i = 0; i < size; ++i) {
            ((ResultColumn)list.elementAt(i)).setVirtualColumnId(virtualColumnId);
            ++virtualColumnId;
        }
        if (b) {
            this.destructiveAppend(list);
        }
        else {
            this.nondestructiveAppend(list);
        }
    }
    
    public ResultColumn getResultColumn(final int n) {
        if (n <= this.size()) {
            final ResultColumn resultColumn = (ResultColumn)this.elementAt(n - 1);
            if (resultColumn.getColumnPosition() == n) {
                return resultColumn;
            }
        }
        for (int size = this.size(), i = 0; i < size; ++i) {
            final ResultColumn resultColumn2 = (ResultColumn)this.elementAt(i);
            if (resultColumn2.getColumnPosition() == n) {
                return resultColumn2;
            }
        }
        return null;
    }
    
    public ResultColumn getResultColumn(final int n, final ResultSetNode resultSetNode, final int[] array) throws StandardException {
        if (n == -1) {
            return null;
        }
        final int[] array2 = { -1 };
        for (int i = this.size() - 1; i >= 0; --i) {
            final ResultColumn resultColumn = (ResultColumn)this.elementAt(i);
            if (resultColumn.getExpression() instanceof ColumnReference) {
                if (resultSetNode == ((ColumnReference)resultColumn.getExpression()).getSourceResultSet(array2) && array2[0] == n) {
                    array[0] = i + 1;
                    return resultColumn;
                }
            }
        }
        return null;
    }
    
    public ResultColumn getOrderByColumn(final int n) {
        if (n == 0) {
            return null;
        }
        return this.getResultColumn(n);
    }
    
    public ResultColumn getResultColumn(final String s) {
        return this.getResultColumn(s, true);
    }
    
    public ResultColumn getResultColumn(final String s, final boolean b) {
        for (int size = this.size(), i = 0; i < size; ++i) {
            final ResultColumn resultColumn = (ResultColumn)this.elementAt(i);
            if (s.equals(resultColumn.getName())) {
                if (b) {
                    resultColumn.setReferenced();
                }
                return resultColumn;
            }
        }
        return null;
    }
    
    public ResultColumn getResultColumn(final int n, final int n2, final String s) {
        for (int size = this.size(), i = 0; i < size; ++i) {
            ResultColumn resultColumn2;
            final ResultColumn resultColumn = resultColumn2 = (ResultColumn)this.elementAt(i);
            while (resultColumn2 != null) {
                final ValueNode expression = resultColumn2.getExpression();
                if (expression instanceof VirtualColumnNode) {
                    final VirtualColumnNode virtualColumnNode = (VirtualColumnNode)expression;
                    final ResultSetNode sourceResultSet = virtualColumnNode.getSourceResultSet();
                    if (sourceResultSet instanceof FromTable) {
                        if (((FromTable)sourceResultSet).getTableNumber() == n) {
                            final ColumnDescriptor tableColumnDescriptor = resultColumn2.getTableColumnDescriptor();
                            if ((tableColumnDescriptor != null && tableColumnDescriptor.getPosition() == n2) || virtualColumnNode.getSourceColumn().getColumnPosition() == n2) {
                                if (s.equals(virtualColumnNode.getSourceColumn().getName())) {
                                    resultColumn.setReferenced();
                                    return resultColumn;
                                }
                                return null;
                            }
                            else {
                                resultColumn2 = virtualColumnNode.getSourceColumn();
                            }
                        }
                        else {
                            resultColumn2 = virtualColumnNode.getSourceColumn();
                        }
                    }
                    else {
                        resultColumn2 = null;
                    }
                }
                else if (expression instanceof ColumnReference) {
                    final ColumnReference columnReference = (ColumnReference)expression;
                    if (columnReference.getTableNumber() == n && columnReference.getColumnNumber() == n2) {
                        resultColumn.setReferenced();
                        return resultColumn;
                    }
                    resultColumn2 = null;
                }
                else {
                    resultColumn2 = null;
                }
            }
        }
        return null;
    }
    
    public ResultColumn getResultColumn(final String s, final String s2) {
        for (int size = this.size(), i = 0; i < size; ++i) {
            final ResultColumn resultColumn = (ResultColumn)this.elementAt(i);
            if (s != null) {
                if (resultColumn.getTableName() == null) {
                    continue;
                }
                if (!s.equals(resultColumn.getTableName())) {
                    continue;
                }
            }
            if (s2.equals(resultColumn.getName())) {
                resultColumn.setReferenced();
                return resultColumn;
            }
        }
        return null;
    }
    
    public ResultColumn getAtMostOneResultColumn(final ColumnReference columnReference, final String s, final boolean b) throws StandardException {
        final int size = this.size();
        ResultColumn resultColumn = null;
        final String columnName = columnReference.getColumnName();
        for (int i = 0; i < size; ++i) {
            final ResultColumn resultColumn2 = (ResultColumn)this.elementAt(i);
            if (columnName.equals(resultColumn2.getName())) {
                if (!resultColumn2.isGenerated() || b) {
                    if (resultColumn != null) {
                        throw StandardException.newException("42Y34", columnName, s);
                    }
                    resultColumn2.setReferenced();
                    resultColumn = resultColumn2;
                }
            }
        }
        return resultColumn;
    }
    
    public ResultColumn getOrderByColumnToBind(final String s, final TableName tableName, final int n, final OrderByColumn orderByColumn) throws StandardException {
        final int size = this.size();
        ResultColumn resultColumn = null;
        for (int i = 0; i < size; ++i) {
            final ResultColumn resultColumn2 = (ResultColumn)this.elementAt(i);
            boolean b;
            if (tableName != null) {
                final ValueNode expression = resultColumn2.getExpression();
                if (!(expression instanceof ColumnReference)) {
                    continue;
                }
                final ColumnReference columnReference = (ColumnReference)expression;
                if (!tableName.equals(columnReference.getTableNameNode()) && n != columnReference.getTableNumber()) {
                    continue;
                }
                b = s.equals(resultColumn2.getSourceColumnName());
            }
            else {
                b = resultColumn2.columnNameMatches(s);
            }
            if (b) {
                if (resultColumn == null) {
                    resultColumn = resultColumn2;
                }
                else {
                    if (!resultColumn.isEquivalent(resultColumn2)) {
                        throw StandardException.newException("42X79", s);
                    }
                    if (i >= size - this.orderBySelect) {
                        this.removeElement(resultColumn2);
                        this.decOrderBySelect();
                        orderByColumn.clearAddedColumnOffset();
                        this.collapseVirtualColumnIdGap(resultColumn2.getColumnPosition());
                        break;
                    }
                }
            }
        }
        return resultColumn;
    }
    
    private void collapseVirtualColumnIdGap(final int n) {
        for (int i = 0; i < this.size(); ++i) {
            ((ResultColumn)this.elementAt(i)).collapseVirtualColumnIdGap(n);
        }
    }
    
    public ResultColumn findResultColumnForOrderBy(final String s, final TableName tableName) throws StandardException {
        final int size = this.size();
        ResultColumn resultColumn = null;
        for (int i = 0; i < size; ++i) {
            final ResultColumn resultColumn2 = (ResultColumn)this.elementAt(i);
            boolean b;
            if (tableName != null) {
                final ValueNode expression = resultColumn2.getExpression();
                if (expression == null) {
                    continue;
                }
                if (!(expression instanceof ColumnReference)) {
                    continue;
                }
                if (!tableName.equals(((ColumnReference)expression).getTableNameNode())) {
                    continue;
                }
                b = s.equals(resultColumn2.getSourceColumnName());
            }
            else {
                b = resultColumn2.columnNameMatches(s);
            }
            if (b) {
                if (resultColumn == null) {
                    resultColumn = resultColumn2;
                }
                else {
                    if (!resultColumn.isEquivalent(resultColumn2)) {
                        throw StandardException.newException("42X79", s);
                    }
                    if (i >= size - this.orderBySelect) {}
                }
            }
        }
        return resultColumn;
    }
    
    void copyResultColumnNames(final ResultColumnList list) {
        for (int n = this.countMismatchAllowed ? list.visibleSize() : this.visibleSize(), i = 0; i < n; ++i) {
            final ResultColumn resultColumn = (ResultColumn)this.elementAt(i);
            final ResultColumn resultColumn2 = (ResultColumn)list.elementAt(i);
            resultColumn.setName(resultColumn2.getName());
            resultColumn.setNameGenerated(resultColumn2.isNameGenerated());
        }
    }
    
    public void bindExpressions(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        this.expandAllsAndNameColumns(list);
        for (int size = this.size(), i = 0; i < size; ++i) {
            this.setElementAt(((ResultColumn)this.elementAt(i)).bindExpression(list, list2, list3), i);
        }
    }
    
    public void bindResultColumnsToExpressions() throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            ((ResultColumn)this.elementAt(i)).bindResultColumnToExpression();
        }
    }
    
    public void bindResultColumnsByName(final TableDescriptor tableDescriptor) throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            ((ResultColumn)this.elementAt(i)).bindResultColumnByName(tableDescriptor, i + 1);
        }
    }
    
    public FormatableBitSet bindResultColumnsByName(final TableDescriptor tableDescriptor, final DMLStatementNode dmlStatementNode) throws StandardException {
        final int size = this.size();
        final FormatableBitSet set = new FormatableBitSet(tableDescriptor.getNumberOfColumns());
        int i = 0;
        while (i < size) {
            final ResultColumn resultColumn = (ResultColumn)this.elementAt(i);
            resultColumn.bindResultColumnByName(tableDescriptor, i + 1);
            final int n = resultColumn.getColumnPosition() - 1;
            if (dmlStatementNode != null && set.isSet(n)) {
                final String name = resultColumn.getName();
                if (dmlStatementNode instanceof UpdateNode) {
                    throw StandardException.newException("42X16", name);
                }
                throw StandardException.newException("42X13", name);
            }
            else {
                set.set(n);
                ++i;
            }
        }
        return set;
    }
    
    public void bindResultColumnsByName(final ResultColumnList list, final FromVTI fromVTI, final DMLStatementNode dmlStatementNode) throws StandardException {
        final int size = this.size();
        final HashSet set = new HashSet<String>(size + 2, 0.999f);
        int i = 0;
        while (i < size) {
            final ResultColumn resultColumn = (ResultColumn)this.elementAt(i);
            final String name = resultColumn.getName();
            if (!set.add(name)) {
                if (dmlStatementNode instanceof UpdateNode) {
                    throw StandardException.newException("42X16", name);
                }
                throw StandardException.newException("42X13", name);
            }
            else {
                final ResultColumn resultColumn2 = list.getResultColumn(null, resultColumn.getName());
                if (resultColumn2 == null) {
                    throw StandardException.newException("42X14", resultColumn.getName(), fromVTI.getMethodCall().getJavaClassName());
                }
                resultColumn.setColumnDescriptor(null, new ColumnDescriptor(resultColumn.getName(), resultColumn2.getVirtualColumnId(), resultColumn2.getType(), null, null, null, null, 0L, 0L));
                resultColumn.setVirtualColumnId(i + 1);
                ++i;
            }
        }
    }
    
    public void bindResultColumnsByPosition(final TableDescriptor tableDescriptor) throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            ((ResultColumn)this.elementAt(i)).bindResultColumnByPosition(tableDescriptor, i + 1);
        }
    }
    
    public void preprocess(final int n, final FromList list, final SubqueryList list2, final PredicateList list3) throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            this.setElementAt(((ResultColumn)this.elementAt(i)).preprocess(n, list, list2, list3), i);
        }
    }
    
    void checkStorableExpressions(final ResultColumnList list) throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            ((ResultColumn)this.elementAt(i)).checkStorableExpression((ResultColumn)list.elementAt(i));
        }
    }
    
    public int[] getStreamStorableColIds(final int n) throws StandardException {
        int n2 = 0;
        final boolean[] array = new boolean[n];
        for (int size = this.size(), i = 0; i < size; ++i) {
            final ResultColumn resultColumn = (ResultColumn)this.elementAt(i);
            if (resultColumn.getTypeId().streamStorable()) {
                array[resultColumn.getTableColumnDescriptor().getPosition() - 1] = true;
            }
        }
        for (int j = 0; j < array.length; ++j) {
            if (array[j]) {
                ++n2;
            }
        }
        if (n2 == 0) {
            return null;
        }
        final int[] array2 = new int[n2];
        int n3 = 0;
        for (int k = 0; k < array.length; ++k) {
            if (array[k]) {
                array2[n3++] = k;
            }
        }
        return array2;
    }
    
    void checkStorableExpressions() throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            ((ResultColumn)this.elementAt(i)).checkStorableExpression();
        }
    }
    
    public void generate(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        this.generateCore(activationClassBuilder, methodBuilder, false);
    }
    
    void generateNulls(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        this.generateCore(activationClassBuilder, methodBuilder, true);
    }
    
    void generateCore(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder, final boolean b) throws StandardException {
        final MethodBuilder userExprFun = expressionClassBuilder.newUserExprFun();
        final LocalField fieldDeclaration = expressionClassBuilder.newFieldDeclaration(2, "org.apache.derby.iapi.sql.execute.ExecRow");
        this.genCreateRow(expressionClassBuilder, fieldDeclaration, "getValueRow", "org.apache.derby.iapi.sql.execute.ExecRow", this.size());
        final int size = this.size();
        final MethodBuilder constructor = expressionClassBuilder.getConstructor();
        for (int i = 0; i < size; ++i) {
            final ResultColumn resultColumn = (ResultColumn)this.elementAt(i);
            if (!b) {
                final ValueNode expression = resultColumn.getExpression();
                if (expression instanceof VirtualColumnNode && !((VirtualColumnNode)expression).getCorrelated()) {
                    continue;
                }
                if (resultColumn.getJoinResultSet() != null) {
                    final ResultColumnList resultColumns = resultColumn.getJoinResultSet().getResultColumns();
                    final int resultSetNumber = resultColumn.getJoinResultSet().getResultSetNumber();
                    int virtualColumnId = -1;
                    int virtualColumnId2 = -1;
                    for (int j = 0; j < resultColumns.size(); ++j) {
                        final ResultColumn resultColumn2 = (ResultColumn)resultColumns.elementAt(j);
                        if (resultColumn2.getName().equals(resultColumn.getUnderlyingOrAliasName())) {
                            if (resultColumn2.isRightOuterJoinUsingClause()) {
                                virtualColumnId = resultColumn2.getVirtualColumnId();
                            }
                            else {
                                virtualColumnId2 = resultColumn2.getVirtualColumnId();
                            }
                        }
                    }
                    userExprFun.getField(fieldDeclaration);
                    userExprFun.push(i + 1);
                    final String interfaceName = this.getTypeCompiler(DataTypeDescriptor.getBuiltInDataTypeDescriptor(16).getTypeId()).interfaceName();
                    final String s = "org.apache.derby.iapi.types.DataValueDescriptor";
                    expressionClassBuilder.pushColumnReference(userExprFun, resultSetNumber, virtualColumnId2);
                    userExprFun.cast(resultColumn.getTypeCompiler().interfaceName());
                    userExprFun.cast(s);
                    userExprFun.callMethod((short)185, null, "isNullOp", interfaceName, 0);
                    userExprFun.cast("org.apache.derby.iapi.types.BooleanDataValue");
                    userExprFun.push(true);
                    userExprFun.callMethod((short)185, null, "equals", "boolean", 1);
                    userExprFun.conditionalIf();
                    expressionClassBuilder.pushColumnReference(userExprFun, resultSetNumber, virtualColumnId);
                    userExprFun.cast(resultColumn.getTypeCompiler().interfaceName());
                    userExprFun.startElseCode();
                    expressionClassBuilder.pushColumnReference(userExprFun, resultSetNumber, virtualColumnId2);
                    userExprFun.cast(resultColumn.getTypeCompiler().interfaceName());
                    userExprFun.completeConditional();
                    userExprFun.cast("org.apache.derby.iapi.types.DataValueDescriptor");
                    userExprFun.callMethod((short)185, "org.apache.derby.iapi.sql.Row", "setColumn", "void", 2);
                    continue;
                }
                if (expression instanceof ColumnReference && !((ColumnReference)expression).getCorrelated()) {
                    continue;
                }
            }
            if (resultColumn.hasGenerationClause()) {
                final ValueNode expression2 = resultColumn.getExpression();
                if (expression2 != null && !(expression2 instanceof VirtualColumnNode)) {
                    continue;
                }
            }
            if (!b && resultColumn.getExpression() instanceof ConstantNode && !((ConstantNode)resultColumn.getExpression()).isNull() && !constructor.statementNumHitLimit(1)) {
                constructor.getField(fieldDeclaration);
                constructor.push(i + 1);
                resultColumn.generateExpression(expressionClassBuilder, constructor);
                constructor.cast("org.apache.derby.iapi.types.DataValueDescriptor");
                constructor.callMethod((short)185, "org.apache.derby.iapi.sql.Row", "setColumn", "void", 2);
            }
            else {
                userExprFun.getField(fieldDeclaration);
                userExprFun.push(i + 1);
                boolean b2 = true;
                if (resultColumn.isAutoincrementGenerated()) {
                    userExprFun.pushThis();
                    userExprFun.push(resultColumn.getColumnPosition());
                    userExprFun.push(resultColumn.getTableColumnDescriptor().getAutoincInc());
                    userExprFun.callMethod((short)182, "org.apache.derby.impl.sql.execute.BaseActivation", "getSetAutoincrementValue", "org.apache.derby.iapi.types.DataValueDescriptor", 2);
                    b2 = false;
                }
                else if (b || (resultColumn.getExpression() instanceof ConstantNode && ((ConstantNode)resultColumn.getExpression()).isNull())) {
                    userExprFun.getField(fieldDeclaration);
                    userExprFun.push(i + 1);
                    userExprFun.callMethod((short)185, "org.apache.derby.iapi.sql.Row", "getColumn", "org.apache.derby.iapi.types.DataValueDescriptor", 1);
                    expressionClassBuilder.generateNullWithExpress(userExprFun, resultColumn.getTypeCompiler(), resultColumn.getTypeServices().getCollationType());
                }
                else {
                    resultColumn.generateExpression(expressionClassBuilder, userExprFun);
                }
                if (b2) {
                    userExprFun.cast("org.apache.derby.iapi.types.DataValueDescriptor");
                }
                userExprFun.callMethod((short)185, "org.apache.derby.iapi.sql.Row", "setColumn", "void", 2);
            }
        }
        userExprFun.getField(fieldDeclaration);
        userExprFun.methodReturn();
        userExprFun.complete();
        expressionClassBuilder.pushMethodReference(methodBuilder, userExprFun);
    }
    
    public ExecRow buildEmptyRow() throws StandardException {
        final int size = this.size();
        final ExecRow valueRow = this.getExecutionFactory().getValueRow(size);
        int n = 1;
        for (int i = 0; i < size; ++i) {
            valueRow.setColumn(n++, ((ResultColumn)this.elementAt(i)).getTypeServices().getNull());
        }
        return valueRow;
    }
    
    public ExecRow buildEmptyIndexRow(final TableDescriptor tableDescriptor, final ConglomerateDescriptor conglomerateDescriptor, final StoreCostController storeCostController, final DataDictionary dataDictionary) throws StandardException {
        final int[] baseColumnPositions = conglomerateDescriptor.getIndexDescriptor().baseColumnPositions();
        final ExecRow valueRow = this.getExecutionFactory().getValueRow(baseColumnPositions.length + 1);
        for (int i = 0; i < baseColumnPositions.length; ++i) {
            valueRow.setColumn(i + 1, tableDescriptor.getColumnDescriptor(baseColumnPositions[i]).getType().getNull());
        }
        valueRow.setColumn(baseColumnPositions.length + 1, storeCostController.newRowLocationTemplate());
        return valueRow;
    }
    
    ExecRowBuilder buildRowTemplate(final FormatableBitSet set, final boolean b) throws StandardException {
        final ExecRowBuilder execRowBuilder = new ExecRowBuilder((set == null) ? this.size() : set.getNumBitsSet(), this.indexRow);
        int anySetBit = (set == null) ? 0 : set.anySetBit();
        for (int i = 0; i < this.size(); ++i) {
            final ResultColumn resultColumn = (ResultColumn)this.elementAt(i);
            final ValueNode expression = resultColumn.getExpression();
            if (expression instanceof CurrentRowLocationNode) {
                execRowBuilder.setColumn(anySetBit + 1, this.newRowLocationTemplate());
            }
            else {
                if (b && expression instanceof VirtualColumnNode) {
                    continue;
                }
                execRowBuilder.setColumn(anySetBit + 1, resultColumn.getType());
            }
            if (set == null) {
                ++anySetBit;
            }
            else {
                anySetBit = set.anySetBit(anySetBit);
            }
        }
        return execRowBuilder;
    }
    
    ExecRowBuilder buildRowTemplate() throws StandardException {
        return this.buildRowTemplate(null, false);
    }
    
    private void genCreateRow(final ExpressionClassBuilder expressionClassBuilder, final LocalField field, final String s, final String s2, final int n) throws StandardException {
        final MethodBuilder constructor = expressionClassBuilder.getConstructor();
        expressionClassBuilder.pushGetExecutionFactoryExpression(constructor);
        constructor.push(n);
        constructor.callMethod((short)185, null, s, s2, 1);
        constructor.setField(field);
        constructor.statementNumHitLimit(1);
    }
    
    private RowLocation newRowLocationTemplate() throws StandardException {
        final LanguageConnectionContext languageConnectionContext = this.getLanguageConnectionContext();
        final ConglomerateController openConglomerate = languageConnectionContext.getTransactionCompile().openConglomerate(this.conglomerateId, false, 0, 6, (languageConnectionContext.getDataDictionary().getCacheMode() == 1) ? 2 : 0);
        try {
            return openConglomerate.newRowLocationTemplate();
        }
        finally {
            openConglomerate.close();
        }
    }
    
    public ResultColumnDescriptor[] makeResultDescriptors() {
        final ResultColumnDescriptor[] array = new ResultColumnDescriptor[this.size()];
        for (int size = this.size(), i = 0; i < size; ++i) {
            array[i] = this.getExecutionFactory().getResultColumnDescriptor((ResultColumnDescriptor)this.elementAt(i));
        }
        return array;
    }
    
    public void expandAllsAndNameColumns(final FromList list) throws StandardException {
        boolean b = false;
        for (int i = 0; i < this.size(); ++i) {
            final ResultColumn resultColumn = (ResultColumn)this.elementAt(i);
            if (resultColumn instanceof AllResultColumn) {
                b = true;
                final TableName tableNameObject = resultColumn.getTableNameObject();
                TableName tableName;
                if (tableNameObject != null) {
                    tableName = this.makeTableName(tableNameObject.getSchemaName(), tableNameObject.getTableName());
                }
                else {
                    tableName = null;
                }
                final ResultColumnList expandAll = list.expandAll(tableName);
                expandAll.nameAllResultColumns();
                this.removeElementAt(i);
                for (int j = 0; j < expandAll.size(); ++j) {
                    this.insertElementAt(expandAll.elementAt(j), i + j);
                }
                i += expandAll.size() - 1;
                this.markInitialSize();
            }
            else {
                resultColumn.guaranteeColumnName();
            }
        }
        if (b) {
            for (int size = this.size(), k = 0; k < size; ++k) {
                ((ResultColumn)this.elementAt(k)).setVirtualColumnId(k + 1);
            }
        }
    }
    
    public void nameAllResultColumns() throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            ((ResultColumn)this.elementAt(i)).guaranteeColumnName();
        }
    }
    
    boolean columnTypesAndLengthsMatch() throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            final ResultColumn resultColumn = (ResultColumn)this.elementAt(i);
            if (!resultColumn.isGenerated()) {
                if (!resultColumn.columnTypeAndLengthMatch()) {
                    return false;
                }
            }
        }
        return true;
    }
    
    boolean columnTypesAndLengthsMatch(final ResultColumnList list) throws StandardException {
        boolean b = true;
        for (int size = this.size(), i = 0; i < size; ++i) {
            final ResultColumn resultColumn = (ResultColumn)this.elementAt(i);
            final ResultColumn resultColumn2 = (ResultColumn)list.elementAt(i);
            if (!resultColumn.isGenerated()) {
                if (!resultColumn2.isGenerated()) {
                    if (!resultColumn.columnTypeAndLengthMatch(resultColumn2)) {
                        b = false;
                    }
                }
            }
        }
        return b;
    }
    
    public boolean nopProjection(final ResultColumnList list) {
        if (this.size() != list.size()) {
            return false;
        }
        for (int size = this.size(), i = 0; i < size; ++i) {
            final ResultColumn resultColumn = (ResultColumn)this.elementAt(i);
            ResultColumn resultColumn2;
            if (resultColumn.getExpression() instanceof VirtualColumnNode) {
                resultColumn2 = ((VirtualColumnNode)resultColumn.getExpression()).getSourceColumn();
            }
            else {
                if (!(resultColumn.getExpression() instanceof ColumnReference)) {
                    return false;
                }
                resultColumn2 = ((ColumnReference)resultColumn.getExpression()).getSource();
            }
            if (resultColumn2 != list.elementAt(i)) {
                return false;
            }
        }
        return true;
    }
    
    public ResultColumnList copyListAndObjects() throws StandardException {
        final ResultColumnList list = (ResultColumnList)this.getNodeFactory().getNode(9, this.getContextManager());
        for (int size = this.size(), i = 0; i < size; ++i) {
            list.addResultColumn(((ResultColumn)this.elementAt(i)).cloneMe());
        }
        list.copyOrderBySelect(this);
        return list;
    }
    
    public void removeOrderByColumns() {
        for (int n = this.size() - 1, i = 0; i < this.orderBySelect; ++i, --n) {
            this.removeElementAt(n);
        }
        this.orderBySelect = 0;
    }
    
    public void genVirtualColumnNodes(final ResultSetNode resultSetNode, final ResultColumnList list) throws StandardException {
        this.genVirtualColumnNodes(resultSetNode, list, true);
    }
    
    public void genVirtualColumnNodes(final ResultSetNode resultSetNode, final ResultColumnList list, final boolean b) throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            final ResultColumn resultColumn = (ResultColumn)this.elementAt(i);
            resultColumn.getTypeServices();
            resultColumn.expression = (ValueNode)this.getNodeFactory().getNode(107, resultSetNode, list.elementAt(i), ReuseFactory.getInteger(i + 1), this.getContextManager());
            if (b) {
                resultColumn.setReferenced();
            }
        }
    }
    
    public void adjustVirtualColumnIds(final int n) {
        for (int size = this.size(), i = 0; i < size; ++i) {
            final ResultColumn resultColumn = (ResultColumn)this.elementAt(i);
            resultColumn.adjustVirtualColumnId(n);
            final VirtualColumnNode virtualColumnNode = (VirtualColumnNode)resultColumn.getExpression();
            virtualColumnNode.columnId += n;
        }
    }
    
    public void doProjection() throws StandardException {
        int n = 0;
        final int size = this.size();
        final ResultColumnList list = new ResultColumnList();
        for (int i = 0; i < size; ++i) {
            final ResultColumn resultColumn = (ResultColumn)this.elementAt(i);
            if (!resultColumn.isReferenced() && resultColumn.getExpression() instanceof VirtualColumnNode && !((VirtualColumnNode)resultColumn.getExpression()).getSourceColumn().isReferenced()) {
                list.addElement(resultColumn);
                ++n;
            }
            else {
                if (n >= 1) {
                    resultColumn.adjustVirtualColumnId(-n);
                }
                resultColumn.setReferenced();
            }
        }
        for (int j = 0; j < list.size(); ++j) {
            this.removeElement(list.elementAt(j));
        }
    }
    
    public String verifyUniqueNames(final boolean b) throws StandardException {
        final int size = this.size();
        final HashSet set = new HashSet<String>(size + 2, 0.999f);
        for (int i = 0; i < size; ++i) {
            final ResultColumn resultColumn = (ResultColumn)this.elementAt(i);
            if (b && resultColumn.isNameGenerated()) {
                throw StandardException.newException("42908");
            }
            final String name = ((ResultColumn)this.elementAt(i)).getName();
            if (!set.add(name)) {
                return name;
            }
        }
        return null;
    }
    
    public void propagateDCLInfo(final ResultColumnList list, final String s) throws StandardException {
        if (list.size() != this.size() && !list.getCountMismatchAllowed() && this.visibleSize() != list.visibleSize()) {
            throw StandardException.newException("42X32", s);
        }
        final String verifyUniqueNames = list.verifyUniqueNames(false);
        if (verifyUniqueNames != null) {
            throw StandardException.newException("42X33", verifyUniqueNames);
        }
        this.copyResultColumnNames(list);
    }
    
    void rejectParameters() throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            ((ResultColumn)this.elementAt(i)).rejectParameter();
        }
    }
    
    void rejectXMLValues() throws StandardException {
        for (int size = this.size(), i = 1; i <= size; ++i) {
            if (i <= this.initialListSize) {
                final ResultColumn resultColumn = this.getResultColumn(i);
                if (resultColumn != null && resultColumn.getType() != null && resultColumn.getType().getTypeId().isXMLTypeId()) {
                    throw StandardException.newException("42Z71");
                }
            }
        }
    }
    
    public void setResultSetNumber(final int resultSetNumber) {
        for (int size = this.size(), i = 0; i < size; ++i) {
            ((ResultColumn)this.elementAt(i)).setResultSetNumber(resultSetNumber);
        }
    }
    
    public void setRedundant() {
        for (int size = this.size(), i = 0; i < size; ++i) {
            ((ResultColumn)this.elementAt(i)).setRedundant();
        }
    }
    
    public void checkColumnUpdateability(final String[] array, final String s) throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            final ResultColumn resultColumn = (ResultColumn)this.elementAt(i);
            if (resultColumn.updated() && !resultColumn.foundInList(array)) {
                throw StandardException.newException("42X31", resultColumn.getName(), s);
            }
        }
    }
    
    public void setUnionResultExpression(final ResultColumnList list, final int tableNumber, final int n, final String s) throws StandardException {
        final TableName tableName = (TableName)this.getNodeFactory().getNode(34, null, null, this.getContextManager());
        this.getContextManager();
        for (int visibleSize = this.visibleSize(), i = 0; i < visibleSize; ++i) {
            final ResultColumn resultColumn = (ResultColumn)this.elementAt(i);
            final ResultColumn resultColumn2 = (ResultColumn)list.elementAt(i);
            final ValueNode expression = resultColumn.getExpression();
            final ValueNode expression2 = resultColumn2.getExpression();
            if (!resultColumn2.isAutoincrementGenerated() && resultColumn.isAutoincrementGenerated()) {
                resultColumn.resetAutoincrementGenerated();
            }
            final TypeId typeId = expression.getTypeId();
            if (typeId != null) {
                final TypeId typeId2 = expression2.getTypeId();
                if (typeId2 != null) {
                    final ClassFactory classFactory = this.getClassFactory();
                    if (!this.unionCompatible(expression, expression2)) {
                        throw StandardException.newException("42X61", typeId.getSQLTypeName(), typeId2.getSQLTypeName(), s);
                    }
                    final DataTypeDescriptor dominantType = expression.getTypeServices().getDominantType(expression2.getTypeServices(), classFactory);
                    final ColumnReference expression3 = (ColumnReference)this.getNodeFactory().getNode(62, resultColumn.getName(), tableName, this.getContextManager());
                    expression3.setType(dominantType);
                    if (expression instanceof ColumnReference) {
                        expression3.copyFields((ColumnReference)expression);
                    }
                    else {
                        expression3.setNestingLevel(n);
                        expression3.setSourceLevel(n);
                    }
                    expression3.setTableNumber(tableNumber);
                    resultColumn.setExpression(expression3);
                    resultColumn.setType(resultColumn.getTypeServices().getDominantType(resultColumn2.getTypeServices(), classFactory));
                    if (resultColumn.getName() != null && !resultColumn.isNameGenerated() && resultColumn2.getName() != null) {
                        if (resultColumn2.isNameGenerated()) {
                            resultColumn.setName(resultColumn2.getName());
                            resultColumn.setNameGenerated(true);
                        }
                        else if (!resultColumn.getName().equals(resultColumn2.getName())) {
                            resultColumn.setName(null);
                            resultColumn.guaranteeColumnName();
                            resultColumn.setNameGenerated(true);
                        }
                    }
                }
            }
        }
    }
    
    private boolean unionCompatible(final ValueNode valueNode, final ValueNode valueNode2) throws StandardException {
        final TypeId typeId = valueNode.getTypeId();
        final TypeId typeId2 = valueNode2.getTypeId();
        final ClassFactory classFactory = this.getClassFactory();
        return (valueNode.getTypeCompiler().storable(typeId2, classFactory) || valueNode2.getTypeCompiler().storable(typeId, classFactory)) && typeId.isBooleanTypeId() == typeId2.isBooleanTypeId();
    }
    
    public boolean isExactTypeAndLengthMatch(final ResultColumnList list) throws StandardException {
        for (int visibleSize = this.visibleSize(), i = 0; i < visibleSize; ++i) {
            if (!((ResultColumn)this.elementAt(i)).getTypeServices().isExactTypeAndLengthMatch(((ResultColumn)list.elementAt(i)).getTypeServices())) {
                return false;
            }
        }
        return true;
    }
    
    public boolean updateOverlaps(final int[] array) {
        for (int size = this.size(), i = 0; i < size; ++i) {
            final ResultColumn resultColumn = (ResultColumn)this.elementAt(i);
            if (resultColumn.updated()) {
                final int columnPosition = resultColumn.getColumnPosition();
                for (int j = 0; j < array.length; ++j) {
                    if (array[j] == columnPosition) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    ResultColumn[] getSortedByPosition() {
        final int size = this.size();
        final ResultColumn[] a = new ResultColumn[size];
        for (int i = 0; i < size; ++i) {
            a[i] = (ResultColumn)this.elementAt(i);
        }
        Arrays.sort(a);
        return a;
    }
    
    public int[] sortMe() {
        final ResultColumn[] sortedByPosition = this.getSortedByPosition();
        final int[] array = new int[sortedByPosition.length];
        for (int i = 0; i < sortedByPosition.length; ++i) {
            array[i] = sortedByPosition[i].getColumnPosition();
        }
        return array;
    }
    
    public ResultColumnList expandToAll(final TableDescriptor tableDescriptor, final TableName tableName) throws StandardException {
        final ResultColumnList list = (ResultColumnList)this.getNodeFactory().getNode(9, this.getContextManager());
        final ResultColumn[] sortedByPosition = this.getSortedByPosition();
        int n = 0;
        final ColumnDescriptorList columnDescriptorList = tableDescriptor.getColumnDescriptorList();
        for (int size = columnDescriptorList.size(), i = 0; i < size; ++i) {
            final ColumnDescriptor element = columnDescriptorList.elementAt(i);
            ResultColumn columnReferenceFromName;
            if (n < sortedByPosition.length && element.getPosition() == sortedByPosition[n].getColumnPosition()) {
                columnReferenceFromName = sortedByPosition[n];
                ++n;
            }
            else {
                columnReferenceFromName = this.makeColumnReferenceFromName(tableName, element.getColumnName());
                columnReferenceFromName.bindResultColumnByPosition(tableDescriptor, element.getPosition());
            }
            list.addResultColumn(columnReferenceFromName);
        }
        return list;
    }
    
    public void bindUntypedNullsToResultColumns(final ResultColumnList list) throws StandardException {
        if (list == null) {
            throw StandardException.newException("42X07");
        }
        for (int size = this.size(), i = 0; i < size; ++i) {
            ((ResultColumn)this.elementAt(i)).typeUntypedNullExpression((ResultColumn)list.elementAt(i));
        }
    }
    
    void markUpdated() {
        for (int size = this.size(), i = 0; i < size; ++i) {
            ((ResultColumn)this.elementAt(i)).markUpdated();
        }
    }
    
    void markUpdatableByCursor() {
        for (int size = this.size(), i = 0; i < size; ++i) {
            if (((ResultColumn)this.elementAt(i)).getSourceTableName() != null) {
                ((ResultColumn)this.elementAt(i)).markUpdatableByCursor();
            }
        }
    }
    
    public String verifyCreateConstraintColumnList(final TableElementList list) {
        for (int size = this.size(), i = 0; i < size; ++i) {
            final String name = ((ResultColumn)this.elementAt(i)).getName();
            if (!list.containsColumnName(name)) {
                return name;
            }
        }
        return null;
    }
    
    public void exportNames(final String[] array) {
        for (int size = this.size(), i = 0; i < size; ++i) {
            array[i] = ((ResultColumn)this.elementAt(i)).getName();
        }
    }
    
    public ResultColumn findParentResultColumn(final ResultColumn resultColumn) {
        ResultColumn resultColumn2 = null;
        for (int size = this.size(), i = 0; i < size; ++i) {
            final ResultColumn resultColumn3 = (ResultColumn)this.elementAt(i);
            if (resultColumn3.getExpression() instanceof ColumnReference) {
                if (((ColumnReference)resultColumn3.getExpression()).getSource() == resultColumn) {
                    resultColumn2 = resultColumn3;
                    break;
                }
            }
            else if (resultColumn3.getExpression() instanceof VirtualColumnNode && ((VirtualColumnNode)resultColumn3.getExpression()).getSourceColumn() == resultColumn) {
                resultColumn2 = resultColumn3;
                break;
            }
        }
        return resultColumn2;
    }
    
    void markUpdated(final ResultColumnList list) {
        for (int size = list.size(), i = 0; i < size; ++i) {
            final ResultColumn resultColumn = this.getResultColumn(((ResultColumn)list.elementAt(i)).getName());
            if (resultColumn != null) {
                resultColumn.markUpdated();
            }
        }
    }
    
    void markColumnsInSelectListUpdatableByCursor(final List list) {
        this.commonCodeForUpdatableByCursor(list, true);
    }
    
    private void commonCodeForUpdatableByCursor(final List list, final boolean b) {
        if (list == null || list.size() == 0) {
            this.markUpdatableByCursor();
        }
        else {
            for (int size = list.size(), i = 0; i < size; ++i) {
                final ResultColumn resultColumn = this.getResultColumn(list.get(i));
                if (resultColumn != null || !b) {
                    resultColumn.markUpdatableByCursor();
                }
            }
        }
    }
    
    void markUpdatableByCursor(final List list) {
        this.commonCodeForUpdatableByCursor(list, false);
    }
    
    boolean updatableByCursor(final int n) {
        return this.getResultColumn(n).updatableByCursor();
    }
    
    public boolean isCloneable() {
        boolean b = true;
        for (int size = this.size(), i = 0; i < size; ++i) {
            if (!((ResultColumn)this.elementAt(i)).getExpression().isCloneable()) {
                b = false;
                break;
            }
        }
        return b;
    }
    
    public void remapColumnReferencesToExpressions() throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            final ResultColumn resultColumn = (ResultColumn)this.elementAt(i);
            if (resultColumn.getExpression() != null) {
                resultColumn.setExpression(resultColumn.getExpression().remapColumnReferencesToExpressions());
            }
        }
    }
    
    void setIndexRow(final long conglomerateId, final boolean forUpdate) {
        this.indexRow = true;
        this.conglomerateId = conglomerateId;
        this.forUpdate = forUpdate;
    }
    
    public boolean hasConsistentTypeInfo() throws StandardException {
        return true;
    }
    
    public boolean containsAllResultColumn() {
        boolean b = false;
        for (int size = this.size(), i = 0; i < size; ++i) {
            if (this.elementAt(i) instanceof AllResultColumn) {
                b = true;
                break;
            }
        }
        return b;
    }
    
    public int countReferencedColumns() {
        int n = 0;
        for (int size = this.size(), i = 0; i < size; ++i) {
            if (((ResultColumn)this.elementAt(i)).isReferenced()) {
                ++n;
            }
        }
        return n;
    }
    
    public void recordColumnReferences(final int[] array, final int n) {
        int n2 = 0;
        for (int size = this.size(), i = 0; i < size; ++i) {
            if (((ResultColumn)this.elementAt(i)).isReferenced()) {
                array[n2++] = i + n;
            }
        }
    }
    
    public int getPosition(final String s, final int n) {
        for (int size = this.size(), i = 0; i < size; ++i) {
            if (s.equals(((ResultColumn)this.elementAt(i)).getName())) {
                return i + n;
            }
        }
        return -1;
    }
    
    public void recordColumnReferences(final boolean[] array, final JBitSet[] array2, final int n) {
        for (int size = this.size(), i = 0; i < size; ++i) {
            final ResultColumn resultColumn = (ResultColumn)this.elementAt(i);
            if (resultColumn.getExpression() instanceof ColumnReference) {
                final int columnNumber = ((ColumnReference)resultColumn.getExpression()).getColumnNumber();
                array[columnNumber] = true;
                array2[n].set(columnNumber);
            }
        }
    }
    
    int allTopCRsFromSameTable() {
        int tableNumber = -1;
        for (int size = this.size(), i = 0; i < size; ++i) {
            final ValueNode expression = ((ResultColumn)this.elementAt(i)).getExpression();
            if (expression instanceof ColumnReference) {
                final ColumnReference columnReference = (ColumnReference)expression;
                if (tableNumber == -1) {
                    tableNumber = columnReference.getTableNumber();
                }
                else if (tableNumber != columnReference.getTableNumber()) {
                    return -1;
                }
            }
        }
        return tableNumber;
    }
    
    public void clearColumnReferences() {
        for (int size = this.size(), i = 0; i < size; ++i) {
            final ResultColumn resultColumn = (ResultColumn)this.elementAt(i);
            if (resultColumn.isReferenced()) {
                resultColumn.setUnreferenced();
            }
        }
    }
    
    public void copyReferencedColumnsToNewList(final ResultColumnList list) {
        for (int size = this.size(), i = 0; i < size; ++i) {
            final ResultColumn resultColumn = (ResultColumn)this.elementAt(i);
            if (resultColumn.isReferenced()) {
                list.addElement(resultColumn);
            }
        }
    }
    
    public void copyColumnsToNewList(final ResultColumnList list, final FormatableBitSet set) {
        for (int size = this.size(), i = 0; i < size; ++i) {
            final ResultColumn resultColumn = (ResultColumn)this.elementAt(i);
            if (set.isSet(resultColumn.getColumnPosition())) {
                list.addElement(resultColumn);
            }
        }
    }
    
    public FormatableBitSet getColumnReferenceMap() {
        final FormatableBitSet set = new FormatableBitSet(this.size());
        for (int size = this.size(), i = 0; i < size; ++i) {
            if (((ResultColumn)this.elementAt(i)).isReferenced()) {
                set.set(i);
            }
        }
        return set;
    }
    
    void pullVirtualIsReferenced() {
        for (int size = this.size(), i = 0; i < size; ++i) {
            ((ResultColumn)this.elementAt(i)).pullVirtualIsReferenced();
        }
    }
    
    public void clearTableNames() {
        for (int size = this.size(), i = 0; i < size; ++i) {
            ((ResultColumn)this.elementAt(i)).clearTableName();
        }
    }
    
    protected void setCountMismatchAllowed(final boolean countMismatchAllowed) {
        this.countMismatchAllowed = countMismatchAllowed;
    }
    
    protected boolean getCountMismatchAllowed() {
        return this.countMismatchAllowed;
    }
    
    public int getTotalColumnSize() {
        int n = 0;
        for (int size = this.size(), i = 0; i < size; ++i) {
            n += ((ResultColumn)this.elementAt(i)).getMaximumColumnSize();
        }
        return n;
    }
    
    public void createListFromResultSetMetaData(final ResultSetMetaData resultSetMetaData, final TableName tableName, final String s) throws StandardException {
        try {
            final int columnCount = resultSetMetaData.getColumnCount();
            if (columnCount <= 0) {
                throw StandardException.newException("42X57", s, String.valueOf(columnCount));
            }
            for (int i = 1; i <= columnCount; ++i) {
                final boolean b = resultSetMetaData.isNullable(i) != 0;
                final int columnType = resultSetMetaData.getColumnType(i);
                TypeId typeId = null;
                switch (columnType) {
                    case 1111:
                    case 2000: {
                        typeId = TypeId.getUserDefinedTypeId(resultSetMetaData.getColumnTypeName(i));
                        break;
                    }
                    default: {
                        typeId = TypeId.getBuiltInTypeId(columnType);
                        break;
                    }
                }
                if (typeId == null) {
                    throw StandardException.newException("42Y23", Integer.toString(i));
                }
                int columnDisplaySize;
                if (typeId.variableLength()) {
                    columnDisplaySize = resultSetMetaData.getColumnDisplaySize(i);
                }
                else if (columnType == -1 || columnType == -4) {
                    columnDisplaySize = Integer.MAX_VALUE;
                }
                else {
                    columnDisplaySize = 0;
                }
                this.addColumn(tableName, resultSetMetaData.getColumnName(i), new DataTypeDescriptor(typeId, typeId.isDecimalTypeId() ? resultSetMetaData.getPrecision(i) : 0, typeId.isDecimalTypeId() ? resultSetMetaData.getScale(i) : 0, b, columnDisplaySize));
            }
        }
        catch (Throwable t) {
            if (t instanceof StandardException) {
                throw (StandardException)t;
            }
            throw StandardException.unexpectedUserException(t);
        }
    }
    
    public ResultColumn addColumn(final TableName tableName, final String s, final DataTypeDescriptor type) throws StandardException {
        final ResultColumn resultColumn = (ResultColumn)this.getNodeFactory().getNode(80, s, this.getNodeFactory().getNode(94, s, tableName, type, this.getContextManager()), this.getContextManager());
        resultColumn.setType(type);
        this.addResultColumn(resultColumn);
        return resultColumn;
    }
    
    public void addRCForRID() throws StandardException {
        final ResultColumn resultColumn = (ResultColumn)this.getNodeFactory().getNode(80, "", this.getNodeFactory().getNode(2, this.getContextManager()), this.getContextManager());
        resultColumn.markGenerated();
        this.addResultColumn(resultColumn);
    }
    
    public void markAllUnreferenced() throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            ((ResultColumn)this.elementAt(i)).setUnreferenced();
        }
    }
    
    boolean allExpressionsAreColumns(final ResultSetNode resultSetNode) {
        for (int size = this.size(), i = 0; i < size; ++i) {
            final ResultColumn resultColumn = (ResultColumn)this.elementAt(i);
            if (resultColumn.isRightOuterJoinUsingClause()) {
                return false;
            }
            final ValueNode expression = resultColumn.getExpression();
            if (!(expression instanceof VirtualColumnNode) && !(expression instanceof ColumnReference)) {
                return false;
            }
            if (expression instanceof VirtualColumnNode) {
                final VirtualColumnNode virtualColumnNode = (VirtualColumnNode)expression;
                if (virtualColumnNode.getSourceResultSet() != resultSetNode) {
                    virtualColumnNode.setCorrelated();
                    return false;
                }
            }
            if (expression instanceof ColumnReference && ((ColumnReference)expression).getCorrelated()) {
                return false;
            }
        }
        return true;
    }
    
    ColumnMapping mapSourceColumns() {
        final int[] array = new int[this.size()];
        final boolean[] array2 = new boolean[this.size()];
        final HashMap hashMap = new HashMap();
        for (int size = this.size(), i = 0; i < size; ++i) {
            final ResultColumn resultColumn = (ResultColumn)this.elementAt(i);
            if (resultColumn.getExpression() instanceof VirtualColumnNode) {
                final VirtualColumnNode virtualColumnNode = (VirtualColumnNode)resultColumn.getExpression();
                if (virtualColumnNode.getCorrelated()) {
                    array[i] = -1;
                }
                else {
                    updateArrays(array, array2, hashMap, virtualColumnNode.getSourceColumn(), i);
                }
            }
            else if (resultColumn.isRightOuterJoinUsingClause()) {
                array[i] = -1;
            }
            else if (resultColumn.getExpression() instanceof ColumnReference) {
                final ColumnReference columnReference = (ColumnReference)resultColumn.getExpression();
                if (columnReference.getCorrelated()) {
                    array[i] = -1;
                }
                else {
                    updateArrays(array, array2, hashMap, columnReference.getSource(), i);
                }
            }
            else {
                array[i] = -1;
            }
        }
        return new ColumnMapping(array, array2);
    }
    
    public void setNullability(final boolean nullability) throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            ((ResultColumn)this.elementAt(i)).setNullability(nullability);
        }
    }
    
    FormatableBitSet getReferencedFormatableBitSet(final boolean b, final boolean b2, final boolean b3) {
        int n = 0;
        final int size = this.size();
        final FormatableBitSet set = new FormatableBitSet(size);
        if (b) {
            if (b2) {
                for (int i = 0; i < size; ++i) {
                    set.set(i);
                }
                return set;
            }
            return null;
        }
        else {
            int j;
            for (j = 0; j < size; ++j) {
                final ResultColumn resultColumn = (ResultColumn)this.elementAt(j);
                if (resultColumn.isReferenced()) {
                    if (!b3 || resultColumn.getExpression() instanceof BaseColumnNode) {
                        set.set(j);
                        ++n;
                    }
                }
            }
            if (n != j || b2) {
                return set;
            }
            return null;
        }
    }
    
    ResultColumnList compactColumns(final boolean b, final boolean b2) throws StandardException {
        int n = 0;
        if (b) {
            return this;
        }
        final ResultColumnList list = (ResultColumnList)this.getNodeFactory().getNode(9, this.getContextManager());
        int size;
        int i;
        for (size = this.size(), i = 0; i < size; ++i) {
            final ResultColumn resultColumn = (ResultColumn)this.elementAt(i);
            if (resultColumn.isReferenced()) {
                list.addResultColumn(resultColumn);
                ++n;
            }
        }
        if (n != i || b2) {
            return list;
        }
        return this;
    }
    
    void removeJoinColumns(final ResultColumnList list) {
        for (int size = list.size(), i = 0; i < size; ++i) {
            final ResultColumn resultColumn = this.getResultColumn(((ResultColumn)list.elementAt(i)).getName());
            if (resultColumn != null) {
                this.removeElement(resultColumn);
            }
        }
    }
    
    ResultColumnList getJoinColumns(final ResultColumnList list) throws StandardException {
        final ResultColumnList list2 = new ResultColumnList();
        for (int size = list.size(), i = 0; i < size; ++i) {
            final String name = ((ResultColumn)list.elementAt(i)).getName();
            final ResultColumn resultColumn = this.getResultColumn(name);
            if (resultColumn == null) {
                throw StandardException.newException("42X04", name);
            }
            list2.addElement(resultColumn);
        }
        return list2;
    }
    
    void resetVirtualColumnIds() {
        for (int size = this.size(), i = 0; i < size; ++i) {
            ((ResultColumn)this.elementAt(i)).setVirtualColumnId(i + 1);
        }
    }
    
    boolean reusableResult() {
        for (int size = this.size(), i = 0; i < size; ++i) {
            final ResultColumn resultColumn = (ResultColumn)this.elementAt(i);
            if (!(resultColumn.getExpression() instanceof ConstantNode) && !(resultColumn.getExpression() instanceof AggregateNode)) {
                return false;
            }
        }
        return true;
    }
    
    public int[] getColumnPositions(final TableDescriptor tableDescriptor) throws StandardException {
        final int size = this.size();
        final int[] array = new int[size];
        for (int i = 0; i < size; ++i) {
            final String name = ((ResultColumn)this.elementAt(i)).getName();
            final ColumnDescriptor columnDescriptor = tableDescriptor.getColumnDescriptor(name);
            if (columnDescriptor == null) {
                throw StandardException.newException("42X14", name, tableDescriptor.getQualifiedName());
            }
            array[i] = columnDescriptor.getPosition();
        }
        return array;
    }
    
    public String[] getColumnNames() {
        final String[] array = new String[this.size()];
        for (int size = this.size(), i = 0; i < size; ++i) {
            array[i] = ((ResultColumn)this.elementAt(i)).getName();
        }
        return array;
    }
    
    void replaceOrForbidDefaults(final TableDescriptor tableDescriptor, final ResultColumnList list, final boolean b) throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            final ResultColumn resultColumn = (ResultColumn)this.elementAt(i);
            if (resultColumn.isDefaultColumn()) {
                if (!b) {
                    throw StandardException.newException("42Y85");
                }
                ColumnDescriptor columnDescriptor = null;
                if (list == null) {
                    columnDescriptor = tableDescriptor.getColumnDescriptor(i + 1);
                }
                else if (i < list.size()) {
                    columnDescriptor = tableDescriptor.getColumnDescriptor(((ResultColumn)list.elementAt(i)).getName());
                }
                if (columnDescriptor == null) {
                    throw StandardException.newException("42X06", tableDescriptor.getQualifiedName());
                }
                if (columnDescriptor.isAutoincrement()) {
                    resultColumn.setAutoincrementGenerated();
                }
                final DefaultInfoImpl defaultInfoImpl = (DefaultInfoImpl)columnDescriptor.getDefaultInfo();
                if (defaultInfoImpl != null && !defaultInfoImpl.isGeneratedColumn()) {
                    this.getCompilerContext().createDependency(columnDescriptor.getDefaultDescriptor(this.getDataDictionary()));
                    resultColumn.setExpression(DefaultNode.parseDefault(defaultInfoImpl.getDefaultText(), this.getLanguageConnectionContext(), this.getCompilerContext()));
                }
                else {
                    resultColumn.setExpression((ValueNode)this.getNodeFactory().getNode(13, this.getContextManager()));
                    resultColumn.setWasDefaultColumn(true);
                }
                resultColumn.setDefaultColumn(false);
            }
        }
    }
    
    void checkForInvalidDefaults() throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            final ResultColumn resultColumn = (ResultColumn)this.elementAt(i);
            if (!resultColumn.isAutoincrementGenerated()) {
                if (resultColumn.isDefaultColumn()) {
                    throw StandardException.newException("42Y85");
                }
            }
        }
    }
    
    void verifyAllOrderable() throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            ((ResultColumn)this.elementAt(i)).verifyOrderable();
        }
    }
    
    public void populate(final TableDescriptor tableDescriptor, final int[] array) throws StandardException {
        if (array == null) {
            return;
        }
        final int length = array.length;
        this.makeTableName(tableDescriptor.getSchemaName(), tableDescriptor.getName());
        for (int i = 0; i < length; ++i) {
            this.addResultColumn(this.makeColumnFromName(tableDescriptor.getColumnDescriptor(array[i]).getColumnName()));
        }
    }
    
    private ResultColumn makeColumnFromName(final String s) throws StandardException {
        return (ResultColumn)this.getNodeFactory().getNode(80, s, null, this.getContextManager());
    }
    
    private ResultColumn makeColumnReferenceFromName(final TableName tableName, final String s) throws StandardException {
        final ContextManager contextManager = this.getContextManager();
        final NodeFactory nodeFactory = this.getNodeFactory();
        return (ResultColumn)nodeFactory.getNode(80, s, nodeFactory.getNode(62, s, tableName, contextManager), contextManager);
    }
    
    public void forbidOverrides(final ResultColumnList list) throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            final ResultColumn resultColumn = (ResultColumn)this.elementAt(i);
            final ResultColumn resultColumn2 = (ResultColumn)((list == null) ? null : list.elementAt(i));
            final ColumnDescriptor tableColumnDescriptor = resultColumn.getTableColumnDescriptor();
            if (tableColumnDescriptor != null && tableColumnDescriptor.hasGenerationClause()) {
                if (resultColumn2 != null && !resultColumn2.hasGenerationClause() && !resultColumn2.wasDefaultColumn()) {
                    throw StandardException.newException("42XA3", resultColumn.getName());
                }
                if (resultColumn2 != null) {
                    resultColumn2.setColumnDescriptor(tableColumnDescriptor.getTableDescriptor(), tableColumnDescriptor);
                }
            }
            if (tableColumnDescriptor != null && tableColumnDescriptor.isAutoincrement()) {
                if (resultColumn2 != null && resultColumn2.isAutoincrementGenerated()) {
                    resultColumn2.setColumnDescriptor(tableColumnDescriptor.getTableDescriptor(), tableColumnDescriptor);
                }
                else if (tableColumnDescriptor.isAutoincAlways()) {
                    throw StandardException.newException("42Z23", resultColumn.getName());
                }
            }
        }
    }
    
    public void incOrderBySelect() {
        ++this.orderBySelect;
    }
    
    private void decOrderBySelect() {
        --this.orderBySelect;
    }
    
    public int getOrderBySelect() {
        return this.orderBySelect;
    }
    
    public void copyOrderBySelect(final ResultColumnList list) {
        this.orderBySelect = list.orderBySelect;
    }
    
    protected void markInitialSize() {
        this.initialListSize = this.size();
    }
    
    private int numGeneratedColumns() {
        int n = 0;
        for (int i = this.size() - 1; i >= 0; --i) {
            if (((ResultColumn)this.elementAt(i)).isGenerated()) {
                ++n;
            }
        }
        return n;
    }
    
    int numGeneratedColumnsForGroupBy() {
        int n = 0;
        for (int i = this.size() - 1; i >= 0; --i) {
            final ResultColumn resultColumn = (ResultColumn)this.elementAt(i);
            if (resultColumn.isGenerated() && resultColumn.isGroupingColumn()) {
                ++n;
            }
        }
        return n;
    }
    
    void removeGeneratedGroupingColumns() {
        for (int i = this.size() - 1; i >= 0; --i) {
            final ResultColumn resultColumn = (ResultColumn)this.elementAt(i);
            if (resultColumn.isGenerated() && resultColumn.isGroupingColumn()) {
                this.removeElementAt(i);
            }
        }
    }
    
    public int visibleSize() {
        return this.size() - this.orderBySelect - this.numGeneratedColumns();
    }
    
    public String toString() {
        return "";
    }
    
    private static boolean streamableType(final ResultColumn resultColumn) {
        final TypeId builtInTypeId = TypeId.getBuiltInTypeId(resultColumn.getType().getTypeName());
        return builtInTypeId != null && builtInTypeId.streamStorable();
    }
    
    private static void updateArrays(final int[] array, final boolean[] array2, final Map map, final ResultColumn resultColumn, final int value) {
        final int virtualColumnId = resultColumn.getVirtualColumnId();
        array[value] = virtualColumnId;
        if (streamableType(resultColumn)) {
            if (map.get(new Integer(virtualColumnId)) != null) {
                array2[value] = true;
            }
            else {
                map.put(new Integer(virtualColumnId), new Integer(value));
            }
        }
    }
    
    public class ColumnMapping
    {
        public final int[] mapArray;
        public final boolean[] cloneMap;
        
        public ColumnMapping(final int[] mapArray, final boolean[] cloneMap) {
            this.mapArray = mapArray;
            this.cloneMap = cloneMap;
        }
    }
}
