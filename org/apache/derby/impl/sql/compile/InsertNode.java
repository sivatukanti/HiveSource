// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.CompilerContext;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.dictionary.IndexLister;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptorList;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptor;
import org.apache.derby.iapi.sql.execute.ExecRowBuilder;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.store.access.StaticCompiledOpenConglomInfo;
import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.iapi.util.StringUtil;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.depend.Dependent;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.util.ReuseFactory;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.impl.sql.execute.FKInfo;
import java.util.Properties;

public final class InsertNode extends DMLModStatementNode
{
    public ResultColumnList targetColumnList;
    public boolean deferred;
    public ValueNode checkConstraints;
    public Properties targetProperties;
    public FKInfo fkInfo;
    protected boolean bulkInsert;
    private boolean bulkInsertReplace;
    private OrderByList orderByList;
    private ValueNode offset;
    private ValueNode fetchFirst;
    private boolean hasJDBClimitClause;
    protected RowLocation[] autoincRowLocation;
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8) {
        super.init(o3, ReuseFactory.getInteger(getStatementType((Properties)o4)));
        this.setTarget((QueryTreeNode)o);
        this.targetColumnList = (ResultColumnList)o2;
        this.targetProperties = (Properties)o4;
        this.orderByList = (OrderByList)o5;
        this.offset = (ValueNode)o6;
        this.fetchFirst = (ValueNode)o7;
        this.hasJDBClimitClause = (o8 != null && (boolean)o8);
        this.getResultSetNode().setInsertSource();
    }
    
    public String toString() {
        return "";
    }
    
    public String statementToString() {
        return "INSERT";
    }
    
    public void printSubNodes(final int n) {
    }
    
    public void bindStatement() throws StandardException {
        this.getCompilerContext().pushCurrentPrivType(0);
        final FromList list = (FromList)this.getNodeFactory().getNode(37, this.getNodeFactory().doJoinOrderOptimization(), this.getContextManager());
        final DataDictionary dataDictionary = this.getDataDictionary();
        super.bindResultSetsWithTables(dataDictionary);
        this.verifyTargetTable();
        if (this.targetProperties != null) {
            this.verifyTargetProperties(dataDictionary);
        }
        this.getResultColumnList();
        if (this.targetColumnList != null) {
            if (this.synonymTableName != null) {
                this.normalizeSynonymColumns(this.targetColumnList, this.targetTableName);
            }
            this.getCompilerContext().pushCurrentPrivType(this.getPrivType());
            if (this.targetTableDescriptor != null) {
                this.targetColumnList.bindResultColumnsByName(this.targetTableDescriptor, this);
            }
            else {
                this.targetColumnList.bindResultColumnsByName(this.targetVTI.getResultColumns(), this.targetVTI, this);
            }
            this.getCompilerContext().popCurrentPrivType();
        }
        this.resultSet.replaceOrForbidDefaults(this.targetTableDescriptor, this.targetColumnList, (this.resultSet instanceof UnionNode && ((UnionNode)this.resultSet).tableConstructor()) || this.resultSet instanceof RowResultSetNode);
        super.bindExpressions();
        if (this.targetColumnList != null) {
            if (this.resultSet.getResultColumns().visibleSize() > this.targetColumnList.size()) {
                throw StandardException.newException("42802");
            }
            this.resultSet.bindUntypedNullsToResultColumns(this.targetColumnList);
            this.resultSet.setTableConstructorTypes(this.targetColumnList);
        }
        else {
            if (this.resultSet.getResultColumns().visibleSize() > this.resultColumnList.size()) {
                throw StandardException.newException("42802");
            }
            this.resultSet.bindUntypedNullsToResultColumns(this.resultColumnList);
            this.resultSet.setTableConstructorTypes(this.resultColumnList);
        }
        this.resultSet.bindResultColumns(list);
        final int visibleSize = this.resultSet.getResultColumns().visibleSize();
        final DataDictionary dataDictionary2 = this.getDataDictionary();
        if (this.targetColumnList != null) {
            if (this.targetColumnList.size() != visibleSize) {
                throw StandardException.newException("42802");
            }
        }
        else if (this.targetTableDescriptor != null && this.targetTableDescriptor.getNumberOfColumns() != visibleSize) {
            throw StandardException.newException("42802");
        }
        boolean b = true;
        final int[] array = new int[this.resultColumnList.size()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = -1;
        }
        if (this.targetColumnList != null) {
            for (int size = this.targetColumnList.size(), j = 0; j < size; ++j) {
                final int position = ((ResultColumn)this.targetColumnList.elementAt(j)).columnDescriptor.getPosition();
                if (j != position - 1) {
                    b = false;
                }
                array[position - 1] = j;
            }
        }
        else {
            for (int k = 0; k < this.resultSet.getResultColumns().visibleSize(); ++k) {
                array[k] = k;
            }
        }
        if (this.orderByList != null) {
            this.orderByList.pullUpOrderByColumns(this.resultSet);
            super.bindExpressions();
            this.orderByList.bindOrderByColumns(this.resultSet);
        }
        QueryTreeNode.bindOffsetFetch(this.offset, this.fetchFirst);
        this.resultSet = this.enhanceAndCheckForAutoincrement(this.resultSet, b, array);
        this.resultColumnList.checkStorableExpressions(this.resultSet.getResultColumns());
        if (!this.resultColumnList.columnTypesAndLengthsMatch(this.resultSet.getResultColumns())) {
            this.resultSet = (ResultSetNode)this.getNodeFactory().getNode(122, this.resultSet, this.resultColumnList, null, Boolean.FALSE, this.getContextManager());
        }
        if (this.targetTableDescriptor != null) {
            final ResultColumnList resultColumns = this.resultSet.getResultColumns();
            resultColumns.copyResultColumnNames(this.resultColumnList);
            this.parseAndBindGenerationClauses(dataDictionary, this.targetTableDescriptor, resultColumns, this.resultColumnList, false, null);
            this.checkConstraints = this.bindConstraints(dataDictionary, this.getNodeFactory(), this.targetTableDescriptor, null, resultColumns, null, null, false, true);
            if (this.resultSet.referencesTarget(this.targetTableDescriptor.getName(), true) || this.requiresDeferredProcessing()) {
                this.deferred = true;
                if (this.bulkInsertReplace && this.resultSet.referencesTarget(this.targetTableDescriptor.getName(), true)) {
                    throw StandardException.newException("42Y38", this.targetTableDescriptor.getQualifiedName());
                }
            }
            this.getAffectedIndexes(this.targetTableDescriptor);
            this.autoincRowLocation = dataDictionary2.computeAutoincRowLocations(this.getLanguageConnectionContext().getTransactionCompile(), this.targetTableDescriptor);
            if (this.isPrivilegeCollectionRequired()) {
                this.getCompilerContext().pushCurrentPrivType(this.getPrivType());
                this.getCompilerContext().addRequiredTablePriv(this.targetTableDescriptor);
                this.getCompilerContext().popCurrentPrivType();
            }
        }
        else {
            this.deferred = VTIDeferModPolicy.deferIt(1, this.targetVTI, null, this.resultSet);
        }
        this.getCompilerContext().popCurrentPrivType();
    }
    
    ResultSetNode enhanceAndCheckForAutoincrement(ResultSetNode enhanceRCLForInsert, final boolean b, final int[] array) throws StandardException {
        enhanceRCLForInsert = enhanceRCLForInsert.enhanceRCLForInsert(this, b, array);
        if (!(enhanceRCLForInsert instanceof UnionNode) || !((UnionNode)enhanceRCLForInsert).tableConstructor()) {
            this.resultColumnList.forbidOverrides(enhanceRCLForInsert.getResultColumns());
        }
        return enhanceRCLForInsert;
    }
    
    int getPrivType() {
        return 3;
    }
    
    public boolean referencesSessionSchema() throws StandardException {
        boolean b = false;
        if (this.targetTableDescriptor != null) {
            b = this.isSessionSchema(this.targetTableDescriptor.getSchemaDescriptor());
        }
        if (!b) {
            b = this.resultSet.referencesSessionSchema();
        }
        return b;
    }
    
    private void verifyTargetProperties(final DataDictionary dataDictionary) throws StandardException {
        final String property = this.targetProperties.getProperty("insertMode");
        if (property != null) {
            final String sqlToUpperCase = StringUtil.SQLToUpperCase(property);
            if (!sqlToUpperCase.equals("BULKINSERT") && !sqlToUpperCase.equals("REPLACE")) {
                throw StandardException.newException("42X60", property, this.targetTableName);
            }
            if (!this.verifyBulkInsert(dataDictionary, sqlToUpperCase)) {
                this.targetProperties.remove("insertMode");
            }
            else {
                this.bulkInsert = true;
                if (sqlToUpperCase.equals("REPLACE")) {
                    this.bulkInsertReplace = true;
                }
                final String property2 = this.targetProperties.getProperty("bulkFetch");
                if (property2 != null) {
                    final int intProperty = this.getIntProperty(property2, "bulkFetch");
                    if (intProperty <= 0) {
                        throw StandardException.newException("42Y64", String.valueOf(intProperty));
                    }
                }
            }
        }
    }
    
    private boolean verifyBulkInsert(final DataDictionary dataDictionary, final String s) throws StandardException {
        return true;
    }
    
    public ConstantAction makeConstantAction() throws StandardException {
        if (this.targetTableDescriptor != null) {
            final long heapConglomerateId = this.targetTableDescriptor.getHeapConglomerateId();
            final TransactionController transactionCompile = this.getLanguageConnectionContext().getTransactionCompile();
            final int n = (this.targetTableDescriptor != null) ? this.indexConglomerateNumbers.length : 0;
            final StaticCompiledOpenConglomInfo[] array = new StaticCompiledOpenConglomInfo[n];
            for (int i = 0; i < n; ++i) {
                array[i] = transactionCompile.getStaticCompiledConglomInfo(this.indexConglomerateNumbers[i]);
            }
            if (this.bulkInsert || this.targetTableDescriptor.getLockGranularity() == 'T') {
                this.lockMode = 7;
            }
            return this.getGenericConstantActionFactory().getInsertConstantAction(this.targetTableDescriptor, heapConglomerateId, transactionCompile.getStaticCompiledConglomInfo(heapConglomerateId), this.indicesToMaintain, this.indexConglomerateNumbers, array, this.indexNames, this.deferred, false, this.targetTableDescriptor.getUUID(), this.lockMode, null, null, this.targetProperties, this.getFKInfo(), this.getTriggerInfo(), this.resultColumnList.getStreamStorableColIds(this.targetTableDescriptor.getNumberOfColumns()), this.getIndexedCols(), null, null, null, this.resultSet.isOneRowResultSet(), this.autoincRowLocation);
        }
        return this.getGenericConstantActionFactory().getUpdatableVTIConstantAction(1, this.deferred);
    }
    
    public boolean[] getIndexedCols() throws StandardException {
        final boolean[] array = new boolean[this.targetTableDescriptor.getNumberOfColumns()];
        for (int i = 0; i < this.indicesToMaintain.length; ++i) {
            final int[] baseColumnPositions = this.indicesToMaintain[i].getIndexDescriptor().baseColumnPositions();
            for (int j = 0; j < baseColumnPositions.length; ++j) {
                array[baseColumnPositions[j] - 1] = true;
            }
        }
        return array;
    }
    
    public void optimizeStatement() throws StandardException {
        if (this.orderByList != null) {
            if (this.orderByList.size() > 1) {
                this.orderByList.removeDupColumns();
            }
            this.resultSet.pushOrderByList(this.orderByList);
            this.orderByList = null;
        }
        this.resultSet.pushOffsetFetchFirst(this.offset, this.fetchFirst, this.hasJDBClimitClause);
        super.optimizeStatement();
        final HasTableFunctionVisitor hasTableFunctionVisitor = new HasTableFunctionVisitor();
        this.accept(hasTableFunctionVisitor);
        if (hasTableFunctionVisitor.hasNode() && !this.isSessionSchema(this.targetTableDescriptor.getSchemaDescriptor())) {
            this.requestBulkInsert();
        }
    }
    
    private void requestBulkInsert() {
        if (this.targetProperties == null) {
            this.targetProperties = new Properties();
        }
        final String s = "insertMode";
        final String value = "bulkInsert";
        if (this.targetProperties.getProperty(s) == null) {
            this.targetProperties.put(s, value);
        }
        this.bulkInsert = true;
    }
    
    public void generate(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        this.generateCodeForTemporaryTable(activationClassBuilder);
        this.generateParameterValueSet(activationClassBuilder);
        if (this.targetTableDescriptor != null) {
            activationClassBuilder.pushGetResultSetFactoryExpression(methodBuilder);
            this.resultSet.generate(activationClassBuilder, methodBuilder);
            this.generateGenerationClauses(this.resultColumnList, this.resultSet.getResultSetNumber(), false, activationClassBuilder, methodBuilder);
            this.generateCheckConstraints(this.checkConstraints, activationClassBuilder, methodBuilder);
            if (this.bulkInsert) {
                final ColumnDescriptorList columnDescriptorList = this.targetTableDescriptor.getColumnDescriptorList();
                final ExecRowBuilder execRowBuilder = new ExecRowBuilder(columnDescriptorList.size(), false);
                for (int i = 0; i < columnDescriptorList.size(); ++i) {
                    execRowBuilder.setColumn(i + 1, columnDescriptorList.get(i).getType());
                }
                methodBuilder.push(activationClassBuilder.addItem(execRowBuilder));
            }
            else {
                methodBuilder.push(-1);
            }
            methodBuilder.callMethod((short)185, null, "getInsertResultSet", "org.apache.derby.iapi.sql.ResultSet", 4);
        }
        else {
            this.targetVTI.assignCostEstimate(this.resultSet.getNewCostEstimate());
            activationClassBuilder.pushGetResultSetFactoryExpression(methodBuilder);
            this.resultSet.generate(activationClassBuilder, methodBuilder);
            this.targetVTI.generate(activationClassBuilder, methodBuilder);
            methodBuilder.callMethod((short)185, null, "getInsertVTIResultSet", "org.apache.derby.iapi.sql.ResultSet", 2);
        }
    }
    
    protected final int getStatementType() {
        return 1;
    }
    
    static final int getStatementType(final Properties properties) {
        int n = 1;
        final String s = (properties == null) ? null : properties.getProperty("insertMode");
        if (s != null && StringUtil.SQLToUpperCase(s).equals("REPLACE")) {
            n = 2;
        }
        return n;
    }
    
    private void getAffectedIndexes(final TableDescriptor tableDescriptor) throws StandardException {
        final IndexLister indexLister = tableDescriptor.getIndexLister();
        this.indicesToMaintain = indexLister.getDistinctIndexRowGenerators();
        this.indexConglomerateNumbers = indexLister.getDistinctIndexConglomerateNumbers();
        this.indexNames = indexLister.getDistinctIndexNames();
        final ConglomerateDescriptor[] conglomerateDescriptors = tableDescriptor.getConglomerateDescriptors();
        final CompilerContext compilerContext = this.getCompilerContext();
        for (int i = 0; i < conglomerateDescriptors.length; ++i) {
            compilerContext.createDependency(conglomerateDescriptors[i]);
        }
    }
    
    void acceptChildren(final Visitor visitor) throws StandardException {
        super.acceptChildren(visitor);
        if (this.targetColumnList != null) {
            this.targetColumnList.accept(visitor);
        }
    }
}
