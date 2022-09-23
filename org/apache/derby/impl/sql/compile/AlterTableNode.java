// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.impl.sql.execute.CreateConstraintConstantAction;
import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.impl.sql.execute.ConstraintConstantAction;
import org.apache.derby.impl.sql.execute.ColumnInfo;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;

public class AlterTableNode extends DDLStatementNode
{
    public TableElementList tableElementList;
    public char lockGranularity;
    private boolean updateStatistics;
    private boolean updateStatisticsAll;
    private boolean dropStatistics;
    private boolean dropStatisticsAll;
    private String indexNameForStatistics;
    public boolean compressTable;
    public boolean sequential;
    public boolean purge;
    public boolean defragment;
    public boolean truncateEndOfTable;
    public int behavior;
    public TableDescriptor baseTable;
    protected int numConstraints;
    private int changeType;
    private boolean truncateTable;
    protected SchemaDescriptor schemaDescriptor;
    protected ColumnInfo[] colInfos;
    protected ConstraintConstantAction[] conActions;
    
    public AlterTableNode() {
        this.tableElementList = null;
        this.updateStatistics = false;
        this.updateStatisticsAll = false;
        this.compressTable = false;
        this.sequential = false;
        this.purge = false;
        this.defragment = false;
        this.truncateEndOfTable = false;
        this.changeType = 0;
        this.truncateTable = false;
        this.schemaDescriptor = null;
        this.colInfos = null;
        this.conActions = null;
    }
    
    public void init(final Object o) throws StandardException {
        this.initAndCheck(o);
        this.truncateTable = true;
        this.schemaDescriptor = this.getSchemaDescriptor();
    }
    
    public void init(final Object o, final Object o2) throws StandardException {
        this.initAndCheck(o);
        this.sequential = (boolean)o2;
        this.compressTable = true;
        this.schemaDescriptor = this.getSchemaDescriptor();
    }
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4) throws StandardException {
        this.initAndCheck(o);
        this.purge = (boolean)o2;
        this.defragment = (boolean)o3;
        this.truncateEndOfTable = (boolean)o4;
        this.compressTable = true;
        this.schemaDescriptor = this.getSchemaDescriptor(true, false);
    }
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5) throws StandardException {
        this.initAndCheck(o);
        switch (this.changeType = ((int[])o2)[0]) {
            case 1:
            case 2:
            case 3:
            case 4: {
                this.tableElementList = (TableElementList)o3;
                this.lockGranularity = (char)o4;
                this.behavior = ((int[])o5)[0];
                break;
            }
            case 5: {
                this.updateStatisticsAll = (boolean)o3;
                this.indexNameForStatistics = (String)o4;
                this.updateStatistics = true;
                break;
            }
            case 6: {
                this.dropStatisticsAll = (boolean)o3;
                this.indexNameForStatistics = (String)o4;
                this.dropStatistics = true;
                break;
            }
            default: {
                throw StandardException.newException("0A000.S");
            }
        }
        this.schemaDescriptor = this.getSchemaDescriptor();
    }
    
    public String toString() {
        return "";
    }
    
    public void printSubNodes(final int n) {
    }
    
    public String statementToString() {
        if (this.truncateTable) {
            return "TRUNCATE TABLE";
        }
        return "ALTER TABLE";
    }
    
    public int getChangeType() {
        return this.changeType;
    }
    
    public void bindStatement() throws StandardException {
        final DataDictionary dataDictionary = this.getDataDictionary();
        int countConstraints = 0;
        int countConstraints2 = 0;
        int countGenerationClauses = 0;
        int n = 0;
        if (this.compressTable && (this.purge || this.defragment || this.truncateEndOfTable)) {
            this.baseTable = this.getTableDescriptor(false);
        }
        else {
            this.baseTable = this.getTableDescriptor();
        }
        if (this.baseTable.getTableType() == 3) {
            throw StandardException.newException("42995");
        }
        this.getCompilerContext().createDependency(this.baseTable);
        if (this.changeType == 1 && this.tableElementList != null) {
            for (int i = 0; i < this.tableElementList.size(); ++i) {
                if (this.tableElementList.elementAt(i) instanceof ColumnDefinitionNode) {
                    final ColumnDefinitionNode columnDefinitionNode = (ColumnDefinitionNode)this.tableElementList.elementAt(i);
                    if (!columnDefinitionNode.hasGenerationClause() || columnDefinitionNode.getType() != null) {
                        if (columnDefinitionNode.getType() == null) {
                            throw StandardException.newException("42XA9", columnDefinitionNode.getColumnName());
                        }
                        if (columnDefinitionNode.getType().getTypeId().isStringTypeId()) {
                            columnDefinitionNode.setCollationType(this.schemaDescriptor.getCollationType());
                        }
                    }
                }
            }
        }
        if (this.tableElementList != null) {
            this.tableElementList.validate(this, dataDictionary, this.baseTable);
            if (this.tableElementList.countNumberOfColumns() + this.baseTable.getNumberOfColumns() > 1012) {
                throw StandardException.newException("54011", String.valueOf(this.tableElementList.countNumberOfColumns() + this.baseTable.getNumberOfColumns()), this.getRelativeName(), String.valueOf(1012));
            }
            n = this.tableElementList.countConstraints(2) + this.tableElementList.countConstraints(6) + this.tableElementList.countConstraints(3);
            countConstraints = this.tableElementList.countConstraints(4);
            countConstraints2 = this.tableElementList.countConstraints(6);
            countGenerationClauses = this.tableElementList.countGenerationClauses();
        }
        if (n + this.baseTable.getTotalNumberOfIndexes() > 32767) {
            throw StandardException.newException("42Z9F", String.valueOf(n + this.baseTable.getTotalNumberOfIndexes()), this.getRelativeName(), String.valueOf(32767));
        }
        if (countConstraints > 0 || countGenerationClauses > 0 || countConstraints2 > 0) {
            final FromList fromList = this.makeFromList(dataDictionary, this.tableElementList, false);
            final FormatableBitSet columnMap = this.baseTable.makeColumnMap(this.baseTable.getGeneratedColumns());
            if (countGenerationClauses > 0) {
                this.tableElementList.bindAndValidateGenerationClauses(this.schemaDescriptor, fromList, columnMap, this.baseTable);
            }
            if (countConstraints > 0) {
                this.tableElementList.bindAndValidateCheckConstraints(fromList);
            }
            if (countConstraints2 > 0) {
                this.tableElementList.validateForeignKeysOnGenerationClauses(fromList, columnMap);
            }
        }
        if (this.tableElementList != null) {
            this.tableElementList.validatePrimaryKeyNullability();
        }
        if ((this.updateStatistics && !this.updateStatisticsAll) || (this.dropStatistics && !this.dropStatisticsAll)) {
            ConglomerateDescriptor conglomerateDescriptor = null;
            if (this.schemaDescriptor.getUUID() != null) {
                conglomerateDescriptor = dataDictionary.getConglomerateDescriptor(this.indexNameForStatistics, this.schemaDescriptor, false);
            }
            if (conglomerateDescriptor == null) {
                throw StandardException.newException("42X65", this.schemaDescriptor.getSchemaName() + "." + this.indexNameForStatistics);
            }
        }
        this.getCompilerContext().createDependency(this.baseTable);
    }
    
    public boolean referencesSessionSchema() throws StandardException {
        return this.isSessionSchema(this.baseTable.getSchemaName());
    }
    
    public ConstantAction makeConstantAction() throws StandardException {
        this.prepConstantAction();
        return this.getGenericConstantActionFactory().getAlterTableConstantAction(this.schemaDescriptor, this.getRelativeName(), this.baseTable.getUUID(), this.baseTable.getHeapConglomerateId(), 0, this.colInfos, this.conActions, this.lockGranularity, this.compressTable, this.behavior, this.sequential, this.truncateTable, this.purge, this.defragment, this.truncateEndOfTable, this.updateStatistics, this.updateStatisticsAll, this.dropStatistics, this.dropStatisticsAll, this.indexNameForStatistics);
    }
    
    private void prepConstantAction() throws StandardException {
        if (this.tableElementList != null) {
            this.genColumnInfo();
        }
        if (this.numConstraints > 0) {
            this.conActions = new ConstraintConstantAction[this.numConstraints];
            this.tableElementList.genConstraintActions(false, this.conActions, this.getRelativeName(), this.schemaDescriptor, this.getDataDictionary());
            for (int i = 0; i < this.conActions.length; ++i) {
                final ConstraintConstantAction constraintConstantAction = this.conActions[i];
                if (constraintConstantAction instanceof CreateConstraintConstantAction && constraintConstantAction.getConstraintType() == 2 && this.getDataDictionary().getConstraintDescriptors(this.baseTable).getPrimaryKey() != null) {
                    throw StandardException.newException("X0Y58.S", this.baseTable.getQualifiedName());
                }
            }
        }
    }
    
    public void genColumnInfo() throws StandardException {
        this.colInfos = new ColumnInfo[this.tableElementList.countNumberOfColumns()];
        this.numConstraints = this.tableElementList.genColumnInfos(this.colInfos);
    }
    
    void acceptChildren(final Visitor visitor) throws StandardException {
        super.acceptChildren(visitor);
        if (this.tableElementList != null) {
            this.tableElementList.accept(visitor);
        }
    }
}
