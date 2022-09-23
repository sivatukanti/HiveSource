// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.Visitor;
import java.util.Iterator;
import org.apache.derby.iapi.sql.dictionary.TriggerDescriptor;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.dictionary.ReferencedKeyConstraintDescriptor;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.dictionary.ForeignKeyConstraintDescriptor;
import java.util.ArrayList;
import org.apache.derby.iapi.sql.dictionary.ConstraintDescriptor;
import java.util.List;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.sql.depend.Dependent;
import org.apache.derby.iapi.sql.compile.NodeFactory;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.catalog.DefaultInfo;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptor;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import java.util.Set;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptorList;
import org.apache.derby.iapi.sql.dictionary.GenericDescriptorList;
import org.apache.derby.iapi.sql.dictionary.ConstraintDescriptorList;
import org.apache.derby.iapi.sql.dictionary.IndexRowGenerator;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.impl.sql.execute.TriggerInfo;
import org.apache.derby.impl.sql.execute.FKInfo;

abstract class DMLModStatementNode extends DMLStatementNode
{
    protected FromVTI targetVTI;
    protected TableName targetTableName;
    protected ResultColumnList resultColumnList;
    protected int lockMode;
    protected FKInfo[] fkInfo;
    protected TriggerInfo triggerInfo;
    public TableDescriptor targetTableDescriptor;
    public IndexRowGenerator[] indicesToMaintain;
    public long[] indexConglomerateNumbers;
    public String[] indexNames;
    protected ConstraintDescriptorList relevantCdl;
    protected GenericDescriptorList relevantTriggers;
    private boolean requiresDeferredProcessing;
    private int statementType;
    private boolean bound;
    private ValueNode checkConstraints;
    protected String[] fkTableNames;
    protected int[] fkRefActions;
    protected ColumnDescriptorList[] fkColDescriptors;
    protected long[] fkIndexConglomNumbers;
    protected boolean isDependentTable;
    protected int[][] fkColArrays;
    protected TableName synonymTableName;
    Set dependentTables;
    
    public void init(final Object o) {
        super.init(o);
        this.statementType = this.getStatementType();
    }
    
    public void init(final Object o, final Object o2) {
        super.init(o);
        this.statementType = (int)o2;
    }
    
    void setTarget(final QueryTreeNode queryTreeNode) {
        if (queryTreeNode instanceof TableName) {
            this.targetTableName = (TableName)queryTreeNode;
        }
        else {
            (this.targetVTI = (FromVTI)queryTreeNode).setTarget();
        }
    }
    
    protected void generateCodeForTemporaryTable(final ActivationClassBuilder activationClassBuilder) throws StandardException {
        if (this.targetTableDescriptor != null && this.targetTableDescriptor.getTableType() == 3 && this.targetTableDescriptor.isOnRollbackDeleteRows()) {
            final MethodBuilder executeMethod = activationClassBuilder.getExecuteMethod();
            executeMethod.pushThis();
            executeMethod.callMethod((short)185, "org.apache.derby.iapi.sql.Activation", "getLanguageConnectionContext", "org.apache.derby.iapi.sql.conn.LanguageConnectionContext", 0);
            executeMethod.push(this.targetTableDescriptor.getName());
            executeMethod.callMethod((short)185, null, "markTempTableAsModifiedInUnitOfWork", "void", 1);
            executeMethod.endStatement();
        }
    }
    
    void verifyTargetTable() throws StandardException {
        final DataDictionary dataDictionary = this.getDataDictionary();
        if (this.targetTableName != null) {
            this.targetTableDescriptor = this.getTableDescriptor(this.targetTableName.getTableName(), this.getSchemaDescriptor(this.targetTableName.getSchemaName()));
            if (this.targetTableDescriptor == null) {
                final TableName resolveTableToSynonym = this.resolveTableToSynonym(this.targetTableName);
                if (resolveTableToSynonym == null) {
                    throw StandardException.newException("42X05", this.targetTableName);
                }
                this.synonymTableName = this.targetTableName;
                this.targetTableName = resolveTableToSynonym;
                this.targetTableDescriptor = this.getTableDescriptor(resolveTableToSynonym.getTableName(), this.getSchemaDescriptor(this.targetTableName.getSchemaName()));
                if (this.targetTableDescriptor == null) {
                    throw StandardException.newException("42X05", this.targetTableName);
                }
            }
            switch (this.targetTableDescriptor.getTableType()) {
                case 2: {
                    throw StandardException.newException("42Y24", this.targetTableName);
                }
                case 1:
                case 5: {
                    throw StandardException.newException("42Y25", this.targetTableName);
                }
                default: {
                    this.targetTableDescriptor = this.lockTableForCompilation(this.targetTableDescriptor);
                    this.getCompilerContext().createDependency(this.targetTableDescriptor);
                    break;
                }
            }
        }
        else {
            final FromList list = new FromList();
            this.targetVTI = (FromVTI)this.targetVTI.bindNonVTITables(dataDictionary, list);
            this.targetVTI = (FromVTI)this.targetVTI.bindVTITables(list);
        }
    }
    
    public boolean isAtomic() {
        return true;
    }
    
    public SchemaDescriptor getSchemaDescriptor() throws StandardException {
        return this.getSchemaDescriptor(this.targetTableName.getSchemaName());
    }
    
    public static int[] getReadColMap(final int n, final FormatableBitSet set) {
        if (set == null) {
            return null;
        }
        int n2 = 0;
        final int[] array = new int[n];
        final int size = set.size();
        for (int i = 0; i < array.length; ++i) {
            if (size > i && set.get(i + 1)) {
                array[i] = n2++;
            }
            else {
                array[i] = -1;
            }
        }
        return array;
    }
    
    protected void getResultColumnList() throws StandardException {
        if (this.targetVTI == null) {
            this.getResultColumnList(null);
        }
        else {
            this.resultColumnList = this.targetVTI.getResultColumns();
        }
    }
    
    protected FromBaseTable getResultColumnList(final ResultColumnList list) throws StandardException {
        final FromBaseTable fromBaseTable = (FromBaseTable)this.getNodeFactory().getNode(135, (this.synonymTableName != null) ? this.synonymTableName : this.targetTableName, null, null, null, this.getContextManager());
        fromBaseTable.bindNonVTITables(this.getDataDictionary(), (FromList)this.getNodeFactory().getNode(37, this.getNodeFactory().doJoinOrderOptimization(), this.getContextManager()));
        this.getResultColumnList(fromBaseTable, list);
        return fromBaseTable;
    }
    
    private void getResultColumnList(final FromBaseTable fromBaseTable, final ResultColumnList list) throws StandardException {
        if (list == null) {
            (this.resultColumnList = fromBaseTable.getAllResultColumns(null)).bindResultColumnsByPosition(this.targetTableDescriptor);
        }
        else {
            (this.resultColumnList = fromBaseTable.getResultColumnsForList(null, list, fromBaseTable.getTableNameField())).bindResultColumnsByName(this.targetTableDescriptor, this);
        }
    }
    
    void parseAndBindGenerationClauses(final DataDictionary dataDictionary, final TableDescriptor tableDescriptor, final ResultColumnList list, final ResultColumnList list2, final boolean b, final ResultSetNode resultSetNode) throws StandardException {
        final CompilerContext compilerContext = this.getCompilerContext();
        for (int size = list2.size(), i = 0; i < size; ++i) {
            final ResultColumn resultColumn = (ResultColumn)list2.elementAt(i);
            if (!b || resultColumn.updated()) {
                if (resultColumn.hasGenerationClause()) {
                    final ColumnDescriptor tableColumnDescriptor = resultColumn.getTableColumnDescriptor();
                    final DataTypeDescriptor type = tableColumnDescriptor.getType();
                    final DefaultInfo defaultInfo = tableColumnDescriptor.getDefaultInfo();
                    final ValueNode valueNode = (ValueNode)this.getNodeFactory().getNode(60, this.parseGenerationClause(defaultInfo.getDefaultText(), tableDescriptor), type, this.getContextManager());
                    ((CastNode)valueNode).setAssignmentSemantics();
                    compilerContext.pushCompilationSchema(this.getSchemaDescriptor(defaultInfo.getOriginalCurrentSchema(), false));
                    try {
                        bindRowScopedExpression(this.getNodeFactory(), this.getContextManager(), tableDescriptor, list, valueNode);
                    }
                    finally {
                        compilerContext.popCompilationSchema();
                    }
                    final ResultColumn resultColumn2 = (ResultColumn)this.getNodeFactory().getNode(80, valueNode.getTypeServices(), valueNode, this.getContextManager());
                    resultColumn2.setVirtualColumnId(i + 1);
                    resultColumn2.setColumnDescriptor(tableDescriptor, tableColumnDescriptor);
                    list2.setElementAt(resultColumn2, i);
                    if (b) {
                        for (int j = 0; j < list.size(); ++j) {
                            if (resultColumn == list.elementAt(j)) {
                                resultColumn2.setName(resultColumn.getName());
                                resultColumn2.setResultSetNumber(resultSetNode.getResultSetNumber());
                                list.setElementAt(resultColumn2, j);
                            }
                        }
                    }
                }
            }
        }
    }
    
    public ValueNode parseGenerationClause(final String str, final TableDescriptor tableDescriptor) throws StandardException {
        final LanguageConnectionContext languageConnectionContext = this.getLanguageConnectionContext();
        this.getCompilerContext();
        final String string = "SELECT " + str + " FROM " + tableDescriptor.getQualifiedName();
        final CompilerContext pushCompilerContext = languageConnectionContext.pushCompilerContext();
        final ValueNode expression = ((ResultColumn)((CursorNode)pushCompilerContext.getParser().parseStatement(string)).getResultSetNode().getResultColumns().elementAt(0)).getExpression();
        languageConnectionContext.popCompilerContext(pushCompilerContext);
        return expression;
    }
    
    ValueNode bindConstraints(final DataDictionary dataDictionary, final NodeFactory nodeFactory, final TableDescriptor tableDescriptor, final Dependent dependent, final ResultColumnList list, final int[] array, final FormatableBitSet set, final boolean b, final boolean b2) throws StandardException {
        this.bound = true;
        if (this.targetVTI != null) {
            return null;
        }
        final CompilerContext compilerContext = this.getCompilerContext();
        compilerContext.pushCurrentPrivType(-1);
        try {
            this.getAllRelevantConstraints(dataDictionary, tableDescriptor, b, array);
            this.createConstraintDependencies(dataDictionary, this.relevantCdl, dependent);
            this.generateFKInfo(this.relevantCdl, dataDictionary, tableDescriptor, set);
            this.getAllRelevantTriggers(dataDictionary, tableDescriptor, array, b2);
            this.createTriggerDependencies(this.relevantTriggers, dependent);
            this.generateTriggerInfo(this.relevantTriggers, tableDescriptor, array);
            if (b) {
                return null;
            }
            this.checkConstraints = this.generateCheckTree(this.relevantCdl, tableDescriptor);
            if (this.checkConstraints != null) {
                compilerContext.pushCompilationSchema(tableDescriptor.getSchemaDescriptor());
                try {
                    bindRowScopedExpression(nodeFactory, this.getContextManager(), tableDescriptor, list, this.checkConstraints);
                }
                finally {
                    compilerContext.popCompilationSchema();
                }
            }
        }
        finally {
            compilerContext.popCurrentPrivType();
        }
        return this.checkConstraints;
    }
    
    static void bindRowScopedExpression(final NodeFactory nodeFactory, final ContextManager contextManager, final TableDescriptor tableDescriptor, final ResultColumnList list, ValueNode bindExpression) throws StandardException {
        final TableName tableName = QueryTreeNode.makeTableName(nodeFactory, contextManager, tableDescriptor.getSchemaName(), tableDescriptor.getName());
        final FromList list2 = (FromList)nodeFactory.getNode(37, nodeFactory.doJoinOrderOptimization(), contextManager);
        final FromBaseTable fromBaseTable = (FromBaseTable)nodeFactory.getNode(135, tableName, null, list, null, contextManager);
        fromBaseTable.setTableNumber(0);
        list2.addFromTable(fromBaseTable);
        bindExpression = bindExpression.bindExpression(list2, null, null);
    }
    
    protected boolean hasCheckConstraints(final DataDictionary dataDictionary, final TableDescriptor tableDescriptor) throws StandardException {
        final ConstraintDescriptorList constraintDescriptors = dataDictionary.getConstraintDescriptors(tableDescriptor);
        return constraintDescriptors != null && constraintDescriptors.getSubList(4).size() > 0;
    }
    
    protected boolean hasGenerationClauses(final TableDescriptor tableDescriptor) throws StandardException {
        return tableDescriptor.getGeneratedColumns().size() > 0;
    }
    
    private ValueNode generateCheckTree(final ConstraintDescriptorList list, final TableDescriptor tableDescriptor) throws StandardException {
        final ConstraintDescriptorList subList = list.getSubList(4);
        final int size = subList.size();
        ValueNode valueNode = null;
        for (int i = 0; i < size; ++i) {
            final ConstraintDescriptor element = subList.elementAt(i);
            final TestConstraintNode testConstraintNode = (TestConstraintNode)this.getNodeFactory().getNode(1, this.parseCheckConstraint(element.getConstraintText(), tableDescriptor), "23513", tableDescriptor.getQualifiedName(), element.getConstraintName(), this.getContextManager());
            if (valueNode == null) {
                valueNode = testConstraintNode;
            }
            else {
                valueNode = (ValueNode)this.getNodeFactory().getNode(39, testConstraintNode, valueNode, this.getContextManager());
            }
        }
        return valueNode;
    }
    
    private void generateFKInfo(final ConstraintDescriptorList list, final DataDictionary dataDictionary, final TableDescriptor tableDescriptor, final FormatableBitSet set) throws StandardException {
        final ArrayList list2 = new ArrayList<FKInfo>();
        final ConstraintDescriptorList activeConstraintDescriptors = dataDictionary.getActiveConstraintDescriptors(list);
        final int[] rowMap = this.getRowMap(set, tableDescriptor);
        final ArrayList<String> list3 = new ArrayList<String>(1);
        final ArrayList<Long> list4 = new ArrayList<Long>(1);
        final ArrayList list5 = new ArrayList<Integer>(1);
        final ArrayList<ArrayList<ColumnDescriptor>> list6 = new ArrayList<ArrayList<ColumnDescriptor>>(1);
        final ArrayList<int[]> list7 = new ArrayList<int[]>(1);
        for (int size = activeConstraintDescriptors.size(), i = 0; i < size; ++i) {
            final ConstraintDescriptor element = activeConstraintDescriptors.elementAt(i);
            int n;
            ReferencedKeyConstraintDescriptor referencedConstraint;
            UUID[] array;
            long[] array2;
            String[] array3;
            boolean[] array4;
            int[] array5;
            if (element instanceof ForeignKeyConstraintDescriptor) {
                n = 1;
                referencedConstraint = ((ForeignKeyConstraintDescriptor)element).getReferencedConstraint();
                array = new UUID[] { null };
                array2 = new long[] { 0L };
                array3 = new String[] { null };
                array4 = new boolean[] { false };
                array5 = new int[] { 0 };
                this.fkSetupArrays(dataDictionary, (ForeignKeyConstraintDescriptor)element, 0, array, array2, array3, array4, array5);
                array3[0] = element.getConstraintName();
            }
            else {
                if (!(element instanceof ReferencedKeyConstraintDescriptor)) {
                    continue;
                }
                referencedConstraint = (ReferencedKeyConstraintDescriptor)element;
                n = 2;
                final ConstraintDescriptorList activeConstraintDescriptors2 = dataDictionary.getActiveConstraintDescriptors(((ReferencedKeyConstraintDescriptor)element).getForeignKeyConstraints(1));
                final int size2 = activeConstraintDescriptors2.size();
                if (size2 == 0) {
                    continue;
                }
                array = new UUID[size2];
                array3 = new String[size2];
                array2 = new long[size2];
                array4 = new boolean[size2];
                array5 = new int[size2];
                final int[] remapReferencedColumns = this.remapReferencedColumns(element, rowMap);
                for (int j = 0; j < size2; ++j) {
                    final ForeignKeyConstraintDescriptor foreignKeyConstraintDescriptor = (ForeignKeyConstraintDescriptor)activeConstraintDescriptors2.elementAt(j);
                    this.fkSetupArrays(dataDictionary, foreignKeyConstraintDescriptor, j, array, array2, array3, array4, array5);
                    if (array5[j] == 0 || array5[j] == 3) {
                        final TableDescriptor tableDescriptor2 = foreignKeyConstraintDescriptor.getTableDescriptor();
                        list3.add(tableDescriptor2.getSchemaName() + "." + tableDescriptor2.getName());
                        list5.add(new Integer(array5[j]));
                        final int[] referencedColumns = foreignKeyConstraintDescriptor.getReferencedColumns();
                        final ColumnDescriptorList columnDescriptorList = tableDescriptor2.getColumnDescriptorList();
                        final ColumnDescriptorList e = new ColumnDescriptorList();
                        for (int k = 0; k < referencedColumns.length; ++k) {
                            e.add(columnDescriptorList.elementAt(referencedColumns[k] - 1));
                        }
                        list6.add(e);
                        list4.add(new Long(array2[j]));
                        list7.add(remapReferencedColumns);
                    }
                }
            }
            final TableDescriptor tableDescriptor3 = referencedConstraint.getTableDescriptor();
            final UUID indexId = referencedConstraint.getIndexId();
            final ConglomerateDescriptor conglomerateDescriptor = tableDescriptor3.getConglomerateDescriptor(indexId);
            final TableDescriptor tableDescriptor4 = element.getTableDescriptor();
            list2.add(new FKInfo(array3, tableDescriptor4.getName(), this.statementType, n, indexId, conglomerateDescriptor.getConglomerateNumber(), array, array2, array4, this.remapReferencedColumns(element, rowMap), dataDictionary.getRowLocationTemplate(this.getLanguageConnectionContext(), tableDescriptor4), array5));
        }
        if (!list2.isEmpty()) {
            this.fkInfo = list2.toArray(new FKInfo[list2.size()]);
        }
        final int size3 = list5.size();
        if (size3 > 0) {
            this.fkTableNames = new String[size3];
            this.fkRefActions = new int[size3];
            this.fkColDescriptors = new ColumnDescriptorList[size3];
            this.fkIndexConglomNumbers = new long[size3];
            this.fkColArrays = new int[size3][];
            for (int l = 0; l < size3; ++l) {
                this.fkTableNames[l] = list3.get(l);
                this.fkRefActions[l] = list5.get(l);
                this.fkColDescriptors[l] = (ColumnDescriptorList)list6.get(l);
                this.fkIndexConglomNumbers[l] = list4.get(l);
                this.fkColArrays[l] = list7.get(l);
            }
        }
    }
    
    private void fkSetupArrays(final DataDictionary dataDictionary, final ForeignKeyConstraintDescriptor foreignKeyConstraintDescriptor, final int n, final UUID[] array, final long[] array2, final String[] array3, final boolean[] array4, final int[] array5) throws StandardException {
        array3[n] = foreignKeyConstraintDescriptor.getConstraintName();
        array[n] = foreignKeyConstraintDescriptor.getIndexId();
        array2[n] = foreignKeyConstraintDescriptor.getIndexConglomerateDescriptor(dataDictionary).getConglomerateNumber();
        array4[n] = foreignKeyConstraintDescriptor.isSelfReferencingFK();
        if (this.statementType == 4) {
            array5[n] = foreignKeyConstraintDescriptor.getRaDeleteRule();
        }
        else if (this.statementType == 3) {
            array5[n] = foreignKeyConstraintDescriptor.getRaUpdateRule();
        }
    }
    
    private void generateTriggerInfo(final GenericDescriptorList list, final TableDescriptor tableDescriptor, final int[] array) throws StandardException {
        if (list != null && list.size() > 0) {
            this.triggerInfo = new TriggerInfo(tableDescriptor, array, list);
        }
    }
    
    public FKInfo[] getFKInfo() {
        return this.fkInfo;
    }
    
    public TriggerInfo getTriggerInfo() {
        return this.triggerInfo;
    }
    
    public ValueNode getCheckConstraints() {
        return this.checkConstraints;
    }
    
    private void createTriggerDependencies(final GenericDescriptorList list, final Dependent dependent) throws StandardException {
        final CompilerContext compilerContext = this.getCompilerContext();
        for (final TriggerDescriptor triggerDescriptor : list) {
            if (dependent == null) {
                compilerContext.createDependency(triggerDescriptor);
            }
            else {
                compilerContext.createDependency(dependent, triggerDescriptor);
            }
        }
    }
    
    protected GenericDescriptorList getAllRelevantTriggers(final DataDictionary dataDictionary, final TableDescriptor tableDescriptor, final int[] array, final boolean b) throws StandardException {
        if (this.relevantTriggers != null) {
            return this.relevantTriggers;
        }
        this.relevantTriggers = new GenericDescriptorList();
        if (!b) {
            return this.relevantTriggers;
        }
        tableDescriptor.getAllRelevantTriggers(this.statementType, array, this.relevantTriggers);
        this.adjustDeferredFlag(this.relevantTriggers.size() > 0);
        return this.relevantTriggers;
    }
    
    protected void adjustDeferredFlag(final boolean requiresDeferredProcessing) {
        if (!this.requiresDeferredProcessing) {
            this.requiresDeferredProcessing = requiresDeferredProcessing;
        }
    }
    
    private void createConstraintDependencies(final DataDictionary dataDictionary, final ConstraintDescriptorList list, final Dependent dependent) throws StandardException {
        final CompilerContext compilerContext = this.getCompilerContext();
        for (int size = list.size(), i = 0; i < size; ++i) {
            final ConstraintDescriptor element = list.elementAt(i);
            if (dependent == null) {
                compilerContext.createDependency(element);
            }
            else {
                compilerContext.createDependency(dependent, element);
            }
            if (element instanceof ReferencedKeyConstraintDescriptor) {
                final ConstraintDescriptorList activeConstraintDescriptors = dataDictionary.getActiveConstraintDescriptors(((ReferencedKeyConstraintDescriptor)element).getForeignKeyConstraints(1));
                for (int size2 = activeConstraintDescriptors.size(), j = 0; j < size2; ++j) {
                    final ConstraintDescriptor element2 = activeConstraintDescriptors.elementAt(j);
                    if (dependent == null) {
                        compilerContext.createDependency(element2);
                        compilerContext.createDependency(element2.getTableDescriptor());
                    }
                    else {
                        compilerContext.createDependency(dependent, element2);
                        compilerContext.createDependency(dependent, element2.getTableDescriptor());
                    }
                }
            }
            else if (element instanceof ForeignKeyConstraintDescriptor) {
                final ForeignKeyConstraintDescriptor foreignKeyConstraintDescriptor = (ForeignKeyConstraintDescriptor)element;
                if (dependent == null) {
                    compilerContext.createDependency(foreignKeyConstraintDescriptor.getReferencedConstraint().getTableDescriptor());
                }
                else {
                    compilerContext.createDependency(dependent, foreignKeyConstraintDescriptor.getReferencedConstraint().getTableDescriptor());
                }
            }
        }
    }
    
    protected ConstraintDescriptorList getAllRelevantConstraints(final DataDictionary dataDictionary, final TableDescriptor tableDescriptor, final boolean b, final int[] array) throws StandardException {
        if (this.relevantCdl != null) {
            return this.relevantCdl;
        }
        final boolean[] array2 = { false };
        this.relevantCdl = new ConstraintDescriptorList();
        array2[0] = this.requiresDeferredProcessing;
        tableDescriptor.getAllRelevantConstraints(this.statementType, b, array, array2, this.relevantCdl);
        this.adjustDeferredFlag(array2[0]);
        return this.relevantCdl;
    }
    
    public boolean requiresDeferredProcessing() {
        return this.requiresDeferredProcessing;
    }
    
    public ValueNode parseCheckConstraint(final String str, final TableDescriptor tableDescriptor) throws StandardException {
        final LanguageConnectionContext languageConnectionContext = this.getLanguageConnectionContext();
        this.getCompilerContext();
        final String string = "SELECT * FROM " + tableDescriptor.getQualifiedName() + " WHERE " + str;
        final CompilerContext pushCompilerContext = languageConnectionContext.pushCompilerContext();
        final ValueNode whereClause = ((SelectNode)((CursorNode)pushCompilerContext.getParser().parseStatement(string)).getResultSetNode()).getWhereClause();
        languageConnectionContext.popCompilerContext(pushCompilerContext);
        return whereClause;
    }
    
    public void generateCheckConstraints(final ValueNode valueNode, final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        if (valueNode == null) {
            methodBuilder.pushNull("org.apache.derby.iapi.services.loader.GeneratedMethod");
        }
        else {
            expressionClassBuilder.pushMethodReference(methodBuilder, this.generateCheckConstraints(valueNode, expressionClassBuilder));
        }
    }
    
    public MethodBuilder generateCheckConstraints(final ValueNode valueNode, final ExpressionClassBuilder expressionClassBuilder) throws StandardException {
        final MethodBuilder userExprFun = expressionClassBuilder.newUserExprFun();
        valueNode.generateExpression(expressionClassBuilder, userExprFun);
        userExprFun.methodReturn();
        userExprFun.complete();
        return userExprFun;
    }
    
    public void generateGenerationClauses(final ResultColumnList list, final int n, final boolean b, final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        final int size = list.size();
        boolean b2 = false;
        for (int i = 0; i < size; ++i) {
            if (((ResultColumn)list.elementAt(i)).hasGenerationClause()) {
                b2 = true;
                break;
            }
        }
        if (!b2) {
            methodBuilder.pushNull("org.apache.derby.iapi.services.loader.GeneratedMethod");
        }
        else {
            expressionClassBuilder.pushMethodReference(methodBuilder, this.generateGenerationClauses(list, n, b, expressionClassBuilder));
        }
    }
    
    private MethodBuilder generateGenerationClauses(final ResultColumnList list, final int n, final boolean b, final ExpressionClassBuilder expressionClassBuilder) throws StandardException {
        final MethodBuilder userExprFun = expressionClassBuilder.newUserExprFun();
        userExprFun.pushThis();
        userExprFun.push(n);
        userExprFun.callMethod((short)182, "org.apache.derby.impl.sql.execute.BaseActivation", "getCurrentRow", "org.apache.derby.iapi.sql.Row", 1);
        final int size = list.size();
        int n2 = 0;
        if (b) {
            n2 = (size - 1) / 2;
        }
        for (int i = n2; i < size; ++i) {
            final ResultColumn resultColumn = (ResultColumn)list.elementAt(i);
            if (resultColumn.hasGenerationClause()) {
                userExprFun.dup();
                userExprFun.push(i + 1);
                resultColumn.generateExpression(expressionClassBuilder, userExprFun);
                userExprFun.cast("org.apache.derby.iapi.types.DataValueDescriptor");
                userExprFun.callMethod((short)185, "org.apache.derby.iapi.sql.Row", "setColumn", "void", 2);
            }
        }
        userExprFun.methodReturn();
        userExprFun.complete();
        return userExprFun;
    }
    
    public void optimizeStatement() throws StandardException {
        super.optimizeStatement();
        this.lockMode = 6;
    }
    
    protected void getAffectedIndexes(final TableDescriptor tableDescriptor, final ResultColumnList list, final FormatableBitSet set) throws StandardException {
        final ArrayList list2 = new ArrayList();
        getXAffectedIndexes(tableDescriptor, list, set, list2);
        this.markAffectedIndexes(list2);
    }
    
    static void getXAffectedIndexes(final TableDescriptor tableDescriptor, final ResultColumnList list, final FormatableBitSet set, final List list2) throws StandardException {
        final ConglomerateDescriptor[] conglomerateDescriptors = tableDescriptor.getConglomerateDescriptors();
        final long[] array = new long[conglomerateDescriptors.length - 1];
        int n = 0;
        for (int i = 0; i < conglomerateDescriptors.length; ++i) {
            final ConglomerateDescriptor conglomerateDescriptor = conglomerateDescriptors[i];
            if (conglomerateDescriptor.isIndex()) {
                if (list == null || list.updateOverlaps(conglomerateDescriptor.getIndexDescriptor().baseColumnPositions())) {
                    if (list2 != null) {
                        int n2;
                        for (n2 = 0; n2 < n && array[n2] != conglomerateDescriptor.getConglomerateNumber(); ++n2) {}
                        if (n2 == n) {
                            array[n++] = conglomerateDescriptor.getConglomerateNumber();
                            list2.add(conglomerateDescriptor);
                        }
                    }
                    final int[] baseColumnPositions = conglomerateDescriptor.getIndexDescriptor().baseColumnPositions();
                    if (set != null) {
                        for (int j = 0; j < baseColumnPositions.length; ++j) {
                            set.set(baseColumnPositions[j]);
                        }
                    }
                }
            }
        }
    }
    
    protected void markAffectedIndexes(final List list) throws StandardException {
        final int size = list.size();
        final CompilerContext compilerContext = this.getCompilerContext();
        this.indicesToMaintain = new IndexRowGenerator[size];
        this.indexConglomerateNumbers = new long[size];
        this.indexNames = new String[size];
        for (int i = 0; i < size; ++i) {
            final ConglomerateDescriptor conglomerateDescriptor = list.get(i);
            this.indicesToMaintain[i] = conglomerateDescriptor.getIndexDescriptor();
            this.indexConglomerateNumbers[i] = conglomerateDescriptor.getConglomerateNumber();
            this.indexNames[i] = (conglomerateDescriptor.isConstraint() ? null : conglomerateDescriptor.getConglomerateName());
            compilerContext.createDependency(conglomerateDescriptor);
        }
    }
    
    public String statementToString() {
        return "DML MOD";
    }
    
    private int[] remapReferencedColumns(final ConstraintDescriptor constraintDescriptor, final int[] array) {
        final int[] referencedColumns = constraintDescriptor.getReferencedColumns();
        if (array == null) {
            return referencedColumns;
        }
        final int[] array2 = new int[referencedColumns.length];
        for (int i = 0; i < referencedColumns.length; ++i) {
            array2[i] = array[referencedColumns[i]];
        }
        return array2;
    }
    
    private int[] getRowMap(final FormatableBitSet set, final TableDescriptor tableDescriptor) throws StandardException {
        if (set == null) {
            return null;
        }
        final int maxColumnID = tableDescriptor.getMaxColumnID();
        final int[] array = new int[maxColumnID + 1];
        int n = 1;
        for (int i = 1; i <= maxColumnID; ++i) {
            if (set.get(i)) {
                array[i] = n++;
            }
        }
        return array;
    }
    
    public void setRefActionInfo(final long n, final int[] array, final String s, final boolean b) {
        this.resultSet.setRefActionInfo(n, array, s, b);
    }
    
    public void normalizeSynonymColumns(final ResultColumnList list, final TableName tableNameNode) throws StandardException {
        if (this.synonymTableName == null) {
            return;
        }
        final String tableName = this.synonymTableName.getTableName();
        for (int size = list.size(), i = 0; i < size; ++i) {
            final ColumnReference reference = ((ResultColumn)list.elementAt(i)).getReference();
            if (reference != null) {
                final String tableName2 = reference.getTableName();
                if (tableName2 != null) {
                    if (!tableName.equals(tableName2)) {
                        throw StandardException.newException("42X55", tableName, tableName2);
                    }
                    reference.setTableNameNode(tableNameNode);
                }
            }
        }
    }
    
    public void printSubNodes(final int n) {
    }
    
    void acceptChildren(final Visitor visitor) throws StandardException {
        super.acceptChildren(visitor);
        if (this.targetTableName != null) {
            this.targetTableName.accept(visitor);
        }
    }
}
