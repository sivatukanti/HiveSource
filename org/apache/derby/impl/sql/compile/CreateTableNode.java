// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.services.property.PersistentSet;
import org.apache.derby.iapi.services.property.PropertyUtil;
import org.apache.derby.impl.sql.execute.ConstraintConstantAction;
import org.apache.derby.impl.sql.execute.CreateConstraintConstantAction;
import org.apache.derby.impl.sql.execute.ColumnInfo;
import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.sql.depend.ProviderList;
import org.apache.derby.iapi.error.StandardException;
import java.util.Properties;

public class CreateTableNode extends DDLStatementNode
{
    private char lockGranularity;
    private boolean onCommitDeleteRows;
    private boolean onRollbackDeleteRows;
    private Properties properties;
    private TableElementList tableElementList;
    protected int tableType;
    private ResultColumnList resultColumns;
    private ResultSetNode queryExpression;
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4) throws StandardException {
        this.tableType = 0;
        this.lockGranularity = (char)o4;
        this.implicitCreateSchema = true;
        this.initAndCheck(o);
        this.tableElementList = (TableElementList)o2;
        this.properties = (Properties)o3;
    }
    
    public void init(Object tempTableSchemaNameCheck, final Object o, final Object o2, final Object o3, final Object o4) throws StandardException {
        this.tableType = 3;
        tempTableSchemaNameCheck = this.tempTableSchemaNameCheck(tempTableSchemaNameCheck);
        this.onCommitDeleteRows = (boolean)o3;
        this.onRollbackDeleteRows = (boolean)o4;
        this.initAndCheck(tempTableSchemaNameCheck);
        this.tableElementList = (TableElementList)o;
        this.properties = (Properties)o2;
    }
    
    public void init(final Object o, final Object o2, final Object o3) throws StandardException {
        this.tableType = 0;
        this.lockGranularity = 'R';
        this.implicitCreateSchema = true;
        this.initAndCheck(o);
        this.resultColumns = (ResultColumnList)o2;
        this.queryExpression = (ResultSetNode)o3;
    }
    
    private Object tempTableSchemaNameCheck(final Object o) throws StandardException {
        final TableName tableName = (TableName)o;
        if (tableName != null) {
            if (tableName.getSchemaName() == null) {
                tableName.setSchemaName("SESSION");
            }
            else if (!this.isSessionSchema(tableName.getSchemaName())) {
                throw StandardException.newException("428EK");
            }
        }
        return tableName;
    }
    
    public String toString() {
        return "";
    }
    
    public void printSubNodes(final int n) {
    }
    
    public String statementToString() {
        if (this.tableType == 3) {
            return "DECLARE GLOBAL TEMPORARY TABLE";
        }
        return "CREATE TABLE";
    }
    
    public void bindStatement() throws StandardException {
        final DataDictionary dataDictionary = this.getDataDictionary();
        final SchemaDescriptor schemaDescriptor = this.getSchemaDescriptor(this.tableType != 3, true);
        if (this.queryExpression != null) {
            final FromList list = (FromList)this.getNodeFactory().getNode(37, this.getNodeFactory().doJoinOrderOptimization(), this.getContextManager());
            final CompilerContext compilerContext = this.getCompilerContext();
            final ProviderList currentAuxiliaryProviderList = compilerContext.getCurrentAuxiliaryProviderList();
            final ProviderList currentAuxiliaryProviderList2 = new ProviderList();
            try {
                compilerContext.setCurrentAuxiliaryProviderList(currentAuxiliaryProviderList2);
                compilerContext.pushCurrentPrivType(0);
                this.queryExpression = this.queryExpression.bindNonVTITables(dataDictionary, list);
                (this.queryExpression = this.queryExpression.bindVTITables(list)).bindExpressions(list);
                this.queryExpression.bindResultColumns(list);
                this.queryExpression.bindUntypedNullsToResultColumns(null);
            }
            finally {
                compilerContext.popCurrentPrivType();
                compilerContext.setCurrentAuxiliaryProviderList(currentAuxiliaryProviderList);
            }
            final ResultColumnList resultColumns = this.queryExpression.getResultColumns();
            if (this.resultColumns != null) {
                if (this.resultColumns.size() != resultColumns.visibleSize()) {
                    throw StandardException.newException("42X70", this.getFullName());
                }
                resultColumns.copyResultColumnNames(this.resultColumns);
            }
            final int collationType = schemaDescriptor.getCollationType();
            this.tableElementList = new TableElementList();
            for (int i = 0; i < resultColumns.size(); ++i) {
                final ResultColumn resultColumn = (ResultColumn)resultColumns.elementAt(i);
                if (!resultColumn.isGenerated()) {
                    if (resultColumn.isNameGenerated()) {
                        throw StandardException.newException("42909");
                    }
                    final DataTypeDescriptor typeServices = resultColumn.getExpression().getTypeServices();
                    if (typeServices != null && !typeServices.isUserCreatableType()) {
                        throw StandardException.newException("42X71", typeServices.getFullSQLTypeName(), resultColumn.getName());
                    }
                    if (typeServices.getTypeId().isStringTypeId() && typeServices.getCollationType() != collationType) {
                        throw StandardException.newException("42ZA3", typeServices.getCollationName(), DataTypeDescriptor.getCollationName(collationType));
                    }
                    this.tableElementList.addTableElement((TableElementNode)this.getNodeFactory().getNode(116, resultColumn.getName(), null, resultColumn.getType(), null, this.getContextManager()));
                }
            }
        }
        else {
            this.tableElementList.setCollationTypesOnCharacterStringColumns(this.getSchemaDescriptor(this.tableType != 3, true));
        }
        this.tableElementList.validate(this, dataDictionary, null);
        if (this.tableElementList.countNumberOfColumns() > 1012) {
            throw StandardException.newException("54011", String.valueOf(this.tableElementList.countNumberOfColumns()), this.getRelativeName(), String.valueOf(1012));
        }
        final int countConstraints = this.tableElementList.countConstraints(2);
        if (countConstraints > 1) {
            throw StandardException.newException("42X90", this.getRelativeName());
        }
        final int countConstraints2 = this.tableElementList.countConstraints(4);
        final int countConstraints3 = this.tableElementList.countConstraints(6);
        final int countConstraints4 = this.tableElementList.countConstraints(3);
        final int countGenerationClauses = this.tableElementList.countGenerationClauses();
        if (this.tableType == 3 && (countConstraints > 0 || countConstraints2 > 0 || countConstraints3 > 0 || countConstraints4 > 0)) {
            throw StandardException.newException("42995");
        }
        if (countConstraints + countConstraints3 + countConstraints4 > 32767) {
            throw StandardException.newException("42Z9F", String.valueOf(countConstraints + countConstraints3 + countConstraints4), this.getRelativeName(), String.valueOf(32767));
        }
        if (countConstraints2 > 0 || countGenerationClauses > 0 || countConstraints3 > 0) {
            final FromList fromList = this.makeFromList(null, this.tableElementList, true);
            final FormatableBitSet set = new FormatableBitSet();
            if (countGenerationClauses > 0) {
                this.tableElementList.bindAndValidateGenerationClauses(schemaDescriptor, fromList, set, null);
            }
            if (countConstraints2 > 0) {
                this.tableElementList.bindAndValidateCheckConstraints(fromList);
            }
            if (countConstraints3 > 0) {
                this.tableElementList.validateForeignKeysOnGenerationClauses(fromList, set);
            }
        }
        if (countConstraints > 0) {
            this.tableElementList.validatePrimaryKeyNullability();
        }
    }
    
    public boolean referencesSessionSchema() throws StandardException {
        return this.isSessionSchema(this.getSchemaDescriptor(this.tableType != 3, true));
    }
    
    public ConstantAction makeConstantAction() throws StandardException {
        final TableElementList tableElementList = this.tableElementList;
        final ColumnInfo[] array = new ColumnInfo[tableElementList.countNumberOfColumns()];
        final int genColumnInfos = tableElementList.genColumnInfos(array);
        CreateConstraintConstantAction[] array2 = null;
        final SchemaDescriptor schemaDescriptor = this.getSchemaDescriptor(this.tableType != 3, true);
        if (genColumnInfos > 0) {
            array2 = new CreateConstraintConstantAction[genColumnInfos];
            tableElementList.genConstraintActions(true, array2, this.getRelativeName(), schemaDescriptor, this.getDataDictionary());
        }
        boolean b = false;
        int n = 0;
        for (int i = 0; i < array.length; ++i) {
            final DataTypeDescriptor dataType = array[i].dataType;
            if (dataType.getTypeId().isLongConcatableTypeId()) {
                b = true;
                break;
            }
            n += dataType.getTypeId().getApproximateLengthInBytes(dataType);
        }
        if ((b || n > 4096) && (this.properties == null || this.properties.get("derby.storage.pageSize") == null) && PropertyUtil.getServiceProperty(this.getLanguageConnectionContext().getTransactionCompile(), "derby.storage.pageSize") == null) {
            if (this.properties == null) {
                this.properties = new Properties();
            }
            this.properties.put("derby.storage.pageSize", "32768");
        }
        return this.getGenericConstantActionFactory().getCreateTableConstantAction(schemaDescriptor.getSchemaName(), this.getRelativeName(), this.tableType, array, array2, this.properties, this.lockGranularity, this.onCommitDeleteRows, this.onRollbackDeleteRows);
    }
    
    void acceptChildren(final Visitor visitor) throws StandardException {
        super.acceptChildren(visitor);
        if (this.tableElementList != null) {
            this.tableElementList.accept(visitor);
        }
    }
}
