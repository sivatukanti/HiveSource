// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.dictionary.ConstraintDescriptorList;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.depend.DependencyManager;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.depend.Dependent;
import org.apache.derby.iapi.sql.dictionary.ConstraintDescriptor;
import org.apache.derby.iapi.sql.dictionary.ForeignKeyConstraintDescriptor;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.sql.dictionary.ReferencedKeyConstraintDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.catalog.UUID;

public class DropConstraintConstantAction extends ConstraintConstantAction
{
    private boolean cascade;
    private String constraintSchemaName;
    private int verifyType;
    
    DropConstraintConstantAction(final String s, final String constraintSchemaName, final String s2, final UUID uuid, final String s3, final IndexConstantAction indexConstantAction, final int n, final int verifyType) {
        super(s, 5, s2, uuid, s3, indexConstantAction);
        this.cascade = (n == 0);
        this.constraintSchemaName = constraintSchemaName;
        this.verifyType = verifyType;
    }
    
    public String toString() {
        if (this.constraintName == null) {
            return "DROP PRIMARY KEY";
        }
        return "DROP CONSTRAINT " + ((this.constraintSchemaName == null) ? this.schemaName : this.constraintSchemaName) + "." + this.constraintName;
    }
    
    public void executeConstantAction(final Activation activation) throws StandardException {
        final LanguageConnectionContext languageConnectionContext = activation.getLanguageConnectionContext();
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        final DependencyManager dependencyManager = dataDictionary.getDependencyManager();
        final TransactionController transactionExecute = languageConnectionContext.getTransactionExecute();
        dataDictionary.startWriting(languageConnectionContext);
        final TableDescriptor tableDescriptor = dataDictionary.getTableDescriptor(this.tableId);
        if (tableDescriptor == null) {
            throw StandardException.newException("X0X05.S", this.tableName);
        }
        final SchemaDescriptor schemaDescriptor = tableDescriptor.getSchemaDescriptor();
        final SchemaDescriptor schemaDescriptor2 = (this.constraintSchemaName == null) ? schemaDescriptor : dataDictionary.getSchemaDescriptor(this.constraintSchemaName, transactionExecute, true);
        ConstraintDescriptor constraintDescriptor;
        if (this.constraintName == null) {
            constraintDescriptor = dataDictionary.getConstraintDescriptors(tableDescriptor).getPrimaryKey();
        }
        else {
            constraintDescriptor = dataDictionary.getConstraintDescriptorByName(tableDescriptor, schemaDescriptor2, this.constraintName, true);
        }
        if (constraintDescriptor == null) {
            throw StandardException.newException("42X86", (this.constraintName == null) ? "PRIMARY KEY" : (schemaDescriptor2.getSchemaName() + "." + this.constraintName), tableDescriptor.getQualifiedName());
        }
        switch (this.verifyType) {
            case 3: {
                if (constraintDescriptor.getConstraintType() != this.verifyType) {
                    throw StandardException.newException("42Z9E", this.constraintName, "UNIQUE");
                }
                break;
            }
            case 4: {
                if (constraintDescriptor.getConstraintType() != this.verifyType) {
                    throw StandardException.newException("42Z9E", this.constraintName, "CHECK");
                }
                break;
            }
            case 6: {
                if (constraintDescriptor.getConstraintType() != this.verifyType) {
                    throw StandardException.newException("42Z9E", this.constraintName, "FOREIGN KEY");
                }
                break;
            }
        }
        final boolean b = this.cascade && constraintDescriptor instanceof ReferencedKeyConstraintDescriptor;
        if (!b) {
            dependencyManager.invalidateFor(constraintDescriptor, 19, languageConnectionContext);
        }
        this.dropConstraint(constraintDescriptor, activation, languageConnectionContext, !b);
        if (b) {
            final ConstraintDescriptorList foreignKeyConstraints = ((ReferencedKeyConstraintDescriptor)constraintDescriptor).getForeignKeyConstraints(3);
            for (int size = foreignKeyConstraints.size(), i = 0; i < size; ++i) {
                final ForeignKeyConstraintDescriptor foreignKeyConstraintDescriptor = (ForeignKeyConstraintDescriptor)foreignKeyConstraints.elementAt(i);
                dependencyManager.invalidateFor(foreignKeyConstraintDescriptor, 19, languageConnectionContext);
                this.dropConstraint(foreignKeyConstraintDescriptor, activation, languageConnectionContext, true);
            }
            dependencyManager.invalidateFor(constraintDescriptor, 19, languageConnectionContext);
            dependencyManager.clearDependencies(languageConnectionContext, constraintDescriptor);
        }
    }
}
