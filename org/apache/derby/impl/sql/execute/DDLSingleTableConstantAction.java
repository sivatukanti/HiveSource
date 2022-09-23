// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.iapi.store.access.ConglomerateController;
import java.util.ArrayList;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import java.util.Properties;
import org.apache.derby.iapi.sql.dictionary.KeyConstraintDescriptor;
import org.apache.derby.iapi.error.StandardException;
import java.util.List;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.dictionary.ConstraintDescriptor;
import org.apache.derby.catalog.UUID;

abstract class DDLSingleTableConstantAction extends DDLConstantAction
{
    protected UUID tableId;
    
    DDLSingleTableConstantAction(final UUID tableId) {
        this.tableId = tableId;
    }
    
    void dropConstraint(final ConstraintDescriptor constraintDescriptor, final Activation activation, final LanguageConnectionContext languageConnectionContext, final boolean b) throws StandardException {
        this.dropConstraint(constraintDescriptor, null, null, activation, languageConnectionContext, b);
    }
    
    void dropConstraint(final ConstraintDescriptor constraintDescriptor, final TableDescriptor tableDescriptor, final Activation activation, final LanguageConnectionContext languageConnectionContext, final boolean b) throws StandardException {
        this.dropConstraint(constraintDescriptor, tableDescriptor, null, activation, languageConnectionContext, b);
    }
    
    void dropConstraint(final ConstraintDescriptor constraintDescriptor, final TableDescriptor tableDescriptor, final List list, final Activation activation, final LanguageConnectionContext languageConnectionContext, final boolean b) throws StandardException {
        Properties properties = null;
        if (constraintDescriptor instanceof KeyConstraintDescriptor) {
            properties = new Properties();
            this.loadIndexProperties(languageConnectionContext, ((KeyConstraintDescriptor)constraintDescriptor).getIndexConglomerateDescriptor(languageConnectionContext.getDataDictionary()), properties);
        }
        final ConglomerateDescriptor drop = constraintDescriptor.drop(languageConnectionContext, b);
        if (drop == null) {
            return;
        }
        if (tableDescriptor != null && tableDescriptor.getUUID().equals(constraintDescriptor.getTableDescriptor().getUUID())) {
            if (list != null) {
                list.add(this.getConglomReplacementAction(drop, constraintDescriptor.getTableDescriptor(), properties));
            }
        }
        else {
            this.executeConglomReplacement(this.getConglomReplacementAction(drop, constraintDescriptor.getTableDescriptor(), properties), activation);
        }
    }
    
    void dropConglomerate(final ConglomerateDescriptor conglomerateDescriptor, final TableDescriptor tableDescriptor, final Activation activation, final LanguageConnectionContext languageConnectionContext) throws StandardException {
        this.dropConglomerate(conglomerateDescriptor, tableDescriptor, false, null, activation, languageConnectionContext);
    }
    
    void dropConglomerate(final ConglomerateDescriptor conglomerateDescriptor, final TableDescriptor tableDescriptor, final boolean b, final List list, final Activation activation, final LanguageConnectionContext languageConnectionContext) throws StandardException {
        final Properties properties = new Properties();
        this.loadIndexProperties(languageConnectionContext, conglomerateDescriptor, properties);
        final ConglomerateDescriptor drop = conglomerateDescriptor.drop(languageConnectionContext, tableDescriptor);
        if (drop == null) {
            return;
        }
        if (b) {
            if (list != null) {
                list.add(this.getConglomReplacementAction(drop, tableDescriptor, properties));
            }
        }
        else {
            this.executeConglomReplacement(this.getConglomReplacementAction(drop, tableDescriptor, properties), activation);
        }
    }
    
    void recreateUniqueConstraintBackingIndexAsUniqueWhenNotNull(final ConglomerateDescriptor conglomerateDescriptor, final TableDescriptor tableDescriptor, final Activation activation, final LanguageConnectionContext languageConnectionContext) throws StandardException {
        final Properties properties = new Properties();
        this.loadIndexProperties(languageConnectionContext, conglomerateDescriptor, properties);
        this.dropConglomerate(conglomerateDescriptor, tableDescriptor, false, new ArrayList(), activation, languageConnectionContext);
        String[] columnNames = conglomerateDescriptor.getColumnNames();
        if (columnNames == null) {
            final int[] baseColumnPositions = conglomerateDescriptor.getIndexDescriptor().baseColumnPositions();
            columnNames = new String[baseColumnPositions.length];
            for (int i = 0; i < columnNames.length; ++i) {
                columnNames[i] = tableDescriptor.getColumnDescriptor(baseColumnPositions[i]).getColumnName();
            }
        }
        new CreateIndexConstantAction(false, false, true, conglomerateDescriptor.getIndexDescriptor().indexType(), tableDescriptor.getSchemaName(), conglomerateDescriptor.getConglomerateName(), tableDescriptor.getName(), tableDescriptor.getUUID(), columnNames, conglomerateDescriptor.getIndexDescriptor().isAscending(), true, conglomerateDescriptor.getUUID(), properties).executeConstantAction(activation);
    }
    
    private void loadIndexProperties(final LanguageConnectionContext languageConnectionContext, final ConglomerateDescriptor conglomerateDescriptor, final Properties properties) throws StandardException {
        final ConglomerateController openConglomerate = languageConnectionContext.getTransactionExecute().openConglomerate(conglomerateDescriptor.getConglomerateNumber(), false, 4, 7, 5);
        openConglomerate.getInternalTablePropertySet(properties);
        openConglomerate.close();
    }
    
    ConstantAction getConglomReplacementAction(final ConglomerateDescriptor conglomerateDescriptor, final TableDescriptor tableDescriptor, final Properties properties) throws StandardException {
        return new CreateIndexConstantAction(conglomerateDescriptor, tableDescriptor, properties);
    }
    
    void executeConglomReplacement(final ConstantAction constantAction, final Activation activation) throws StandardException {
        final CreateIndexConstantAction createIndexConstantAction = (CreateIndexConstantAction)constantAction;
        final LanguageConnectionContext languageConnectionContext = activation.getLanguageConnectionContext();
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        createIndexConstantAction.executeConstantAction(activation);
        dataDictionary.updateConglomerateDescriptor(dataDictionary.getConglomerateDescriptors(createIndexConstantAction.getReplacedConglomNumber()), createIndexConstantAction.getCreatedConglomNumber(), languageConnectionContext.getTransactionExecute());
    }
}
