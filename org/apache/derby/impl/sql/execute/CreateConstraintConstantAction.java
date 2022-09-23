// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.dictionary.ColumnDescriptor;
import org.apache.derby.iapi.sql.dictionary.ReferencedKeyConstraintDescriptor;
import org.apache.derby.iapi.sql.dictionary.DataDescriptorGenerator;
import org.apache.derby.iapi.services.uuid.UUIDFactory;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.depend.DependencyManager;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.dictionary.ConstraintDescriptor;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.sql.dictionary.ForeignKeyConstraintDescriptor;
import org.apache.derby.iapi.sql.dictionary.ConsInfo;
import org.apache.derby.iapi.sql.dictionary.DDUtils;
import org.apache.derby.iapi.sql.depend.Dependent;
import org.apache.derby.catalog.ReferencedColumns;
import org.apache.derby.catalog.types.ReferencedColumnsDescriptorImpl;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.depend.ProviderInfo;
import org.apache.derby.iapi.services.loader.ClassFactory;

public class CreateConstraintConstantAction extends ConstraintConstantAction
{
    private final boolean forCreateTable;
    private String[] columnNames;
    private String constraintText;
    private ConstraintInfo otherConstraintInfo;
    private ClassFactory cf;
    private boolean enabled;
    private ProviderInfo[] providerInfo;
    
    CreateConstraintConstantAction(final String s, final int n, final boolean forCreateTable, final String s2, final UUID uuid, final String s3, final String[] columnNames, final IndexConstantAction indexConstantAction, final String constraintText, final boolean enabled, final ConstraintInfo otherConstraintInfo, final ProviderInfo[] providerInfo) {
        super(s, n, s2, uuid, s3, indexConstantAction);
        this.forCreateTable = forCreateTable;
        this.columnNames = columnNames;
        this.constraintText = constraintText;
        this.enabled = enabled;
        this.otherConstraintInfo = otherConstraintInfo;
        this.providerInfo = providerInfo;
    }
    
    public void executeConstantAction(final Activation activation) throws StandardException {
        ConglomerateDescriptor conglomerateDescriptor = null;
        ConstraintDescriptor constraintDescriptor = null;
        UUID uuid = null;
        if (this.constraintType == 1) {
            return;
        }
        final LanguageConnectionContext languageConnectionContext = activation.getLanguageConnectionContext();
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        final DependencyManager dependencyManager = dataDictionary.getDependencyManager();
        final TransactionController transactionExecute = languageConnectionContext.getTransactionExecute();
        this.cf = languageConnectionContext.getLanguageConnectionFactory().getClassFactory();
        dataDictionary.startWriting(languageConnectionContext);
        final SchemaDescriptor schemaDescriptor = dataDictionary.getSchemaDescriptor(this.schemaName, transactionExecute, true);
        TableDescriptor ddlTableDescriptor = activation.getDDLTableDescriptor();
        if (ddlTableDescriptor == null) {
            if (this.tableId != null) {
                ddlTableDescriptor = dataDictionary.getTableDescriptor(this.tableId);
            }
            else {
                ddlTableDescriptor = dataDictionary.getTableDescriptor(this.tableName, schemaDescriptor, transactionExecute);
            }
            if (ddlTableDescriptor == null) {
                throw StandardException.newException("X0X05.S", this.tableName);
            }
            activation.setDDLTableDescriptor(ddlTableDescriptor);
        }
        final UUIDFactory uuidFactory = dataDictionary.getUUIDFactory();
        if (this.indexAction != null) {
            String indexName;
            if (this.indexAction.getIndexName() == null) {
                indexName = uuidFactory.createUUID().toString();
                this.indexAction.setIndexName(indexName);
            }
            else {
                indexName = this.indexAction.getIndexName();
            }
            this.indexAction.executeConstantAction(activation);
            final ConglomerateDescriptor[] conglomerateDescriptors = ddlTableDescriptor.getConglomerateDescriptors();
            for (int i = 0; i < conglomerateDescriptors.length; ++i) {
                conglomerateDescriptor = conglomerateDescriptors[i];
                if (conglomerateDescriptor.isIndex() && indexName.equals(conglomerateDescriptor.getConglomerateName())) {
                    break;
                }
            }
            uuid = conglomerateDescriptor.getUUID();
        }
        final UUID uuid2 = uuidFactory.createUUID();
        final DataDescriptorGenerator dataDescriptorGenerator = dataDictionary.getDataDescriptorGenerator();
        switch (this.constraintType) {
            case 2: {
                constraintDescriptor = dataDescriptorGenerator.newPrimaryKeyConstraintDescriptor(ddlTableDescriptor, this.constraintName, false, false, this.genColumnPositions(ddlTableDescriptor, false), uuid2, uuid, schemaDescriptor, this.enabled, 0);
                dataDictionary.addConstraintDescriptor(constraintDescriptor, transactionExecute);
                break;
            }
            case 3: {
                constraintDescriptor = dataDescriptorGenerator.newUniqueConstraintDescriptor(ddlTableDescriptor, this.constraintName, false, false, this.genColumnPositions(ddlTableDescriptor, false), uuid2, uuid, schemaDescriptor, this.enabled, 0);
                dataDictionary.addConstraintDescriptor(constraintDescriptor, transactionExecute);
                break;
            }
            case 4: {
                constraintDescriptor = dataDescriptorGenerator.newCheckConstraintDescriptor(ddlTableDescriptor, this.constraintName, false, false, uuid2, this.constraintText, new ReferencedColumnsDescriptorImpl(this.genColumnPositions(ddlTableDescriptor, false)), schemaDescriptor, this.enabled);
                dataDictionary.addConstraintDescriptor(constraintDescriptor, transactionExecute);
                this.storeConstraintDependenciesOnPrivileges(activation, constraintDescriptor, null, this.providerInfo);
                break;
            }
            case 6: {
                final ReferencedKeyConstraintDescriptor locateReferencedConstraint = DDUtils.locateReferencedConstraint(dataDictionary, ddlTableDescriptor, this.constraintName, this.columnNames, this.otherConstraintInfo);
                DDUtils.validateReferentialActions(dataDictionary, ddlTableDescriptor, this.constraintName, this.otherConstraintInfo, this.columnNames);
                constraintDescriptor = dataDescriptorGenerator.newForeignKeyConstraintDescriptor(ddlTableDescriptor, this.constraintName, false, false, this.genColumnPositions(ddlTableDescriptor, false), uuid2, uuid, schemaDescriptor, locateReferencedConstraint, this.enabled, this.otherConstraintInfo.getReferentialActionDeleteRule(), this.otherConstraintInfo.getReferentialActionUpdateRule());
                dataDictionary.addConstraintDescriptor(constraintDescriptor, transactionExecute);
                if (!this.forCreateTable && dataDictionary.activeConstraint(constraintDescriptor)) {
                    ConstraintConstantAction.validateFKConstraint(transactionExecute, dataDictionary, (ForeignKeyConstraintDescriptor)constraintDescriptor, locateReferencedConstraint, ((CreateIndexConstantAction)this.indexAction).getIndexTemplateRow());
                }
                dependencyManager.addDependency(constraintDescriptor, locateReferencedConstraint, languageConnectionContext.getContextManager());
                this.storeConstraintDependenciesOnPrivileges(activation, constraintDescriptor, locateReferencedConstraint.getTableId(), this.providerInfo);
                break;
            }
        }
        if (this.providerInfo != null) {
            for (int j = 0; j < this.providerInfo.length; ++j) {
                dependencyManager.addDependency(constraintDescriptor, (Provider)this.providerInfo[j].getDependableFinder().getDependable(dataDictionary, this.providerInfo[j].getObjectId()), languageConnectionContext.getContextManager());
            }
        }
        if (!this.forCreateTable) {
            dependencyManager.invalidateFor(ddlTableDescriptor, 22, languageConnectionContext);
        }
        if (this.constraintType == 6) {
            dependencyManager.invalidateFor(((ForeignKeyConstraintDescriptor)constraintDescriptor).getReferencedConstraint().getTableDescriptor(), 22, languageConnectionContext);
        }
    }
    
    boolean isForeignKeyConstraint() {
        return this.constraintType == 6;
    }
    
    private int[] genColumnPositions(final TableDescriptor tableDescriptor, final boolean b) throws StandardException {
        final int[] array = new int[this.columnNames.length];
        for (int i = 0; i < this.columnNames.length; ++i) {
            final ColumnDescriptor columnDescriptor = tableDescriptor.getColumnDescriptor(this.columnNames[i]);
            if (columnDescriptor == null) {
                throw StandardException.newException("42X14", this.columnNames[i], this.tableName);
            }
            if (b && !columnDescriptor.getType().getTypeId().orderable(this.cf)) {
                throw StandardException.newException("X0X67.S", columnDescriptor.getType().getTypeId().getSQLTypeName());
            }
            array[i] = columnDescriptor.getPosition();
        }
        return array;
    }
    
    String getConstraintText() {
        return this.constraintText;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("CREATE CONSTRAINT " + this.constraintName);
        sb.append("\n=========================\n");
        if (this.columnNames == null) {
            sb.append("columnNames == null\n");
        }
        else {
            for (int i = 0; i < this.columnNames.length; ++i) {
                sb.append("\n\tcol[" + i + "]" + this.columnNames[i].toString());
            }
        }
        sb.append("\n");
        sb.append(this.constraintText);
        sb.append("\n");
        if (this.otherConstraintInfo != null) {
            sb.append(this.otherConstraintInfo.toString());
        }
        sb.append("\n");
        return sb.toString();
    }
}
