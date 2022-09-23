// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptorList;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptor;
import org.apache.derby.iapi.sql.dictionary.ConstraintDescriptor;
import org.apache.derby.iapi.sql.dictionary.ConstraintDescriptorList;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;

public class RenameNode extends DDLStatementNode
{
    protected TableName newTableName;
    protected String oldObjectName;
    protected String newObjectName;
    protected TableDescriptor td;
    private long conglomerateNumber;
    protected boolean usedAlterTable;
    protected int renamingWhat;
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5) throws StandardException {
        this.usedAlterTable = (boolean)o4;
        switch (this.renamingWhat = (int)o5) {
            case 1: {
                this.initAndCheck(o);
                this.newTableName = this.makeTableName(this.getObjectName().getSchemaName(), (String)o3);
                this.oldObjectName = null;
                this.newObjectName = this.newTableName.getTableName();
                break;
            }
            case 2: {
                TableName tableName;
                if (o instanceof TableName) {
                    tableName = (TableName)o;
                }
                else {
                    tableName = this.makeTableName(null, (String)o);
                }
                this.initAndCheck(tableName);
                this.oldObjectName = (String)o2;
                this.newObjectName = (String)o3;
                break;
            }
            case 3: {
                this.oldObjectName = (String)o2;
                this.newObjectName = (String)o3;
                break;
            }
        }
    }
    
    public String toString() {
        return "";
    }
    
    public String statementToString() {
        if (this.usedAlterTable) {
            return "ALTER TABLE";
        }
        switch (this.renamingWhat) {
            case 1: {
                return "RENAME TABLE";
            }
            case 2: {
                return "RENAME COLUMN";
            }
            case 3: {
                return "RENAME INDEX";
            }
            default: {
                return "UNKNOWN";
            }
        }
    }
    
    public void bindStatement() throws StandardException {
        final CompilerContext compilerContext = this.getCompilerContext();
        final DataDictionary dataDictionary = this.getDataDictionary();
        SchemaDescriptor schemaDescriptor;
        if (this.renamingWhat == 3) {
            schemaDescriptor = this.getSchemaDescriptor(null);
            final ConglomerateDescriptor conglomerateDescriptor = dataDictionary.getConglomerateDescriptor(this.oldObjectName, schemaDescriptor, false);
            if (conglomerateDescriptor == null) {
                throw StandardException.newException("42X65", this.oldObjectName);
            }
            this.td = dataDictionary.getTableDescriptor(conglomerateDescriptor.getTableID());
            this.initAndCheck(this.makeTableName(this.td.getSchemaName(), this.td.getName()));
        }
        else {
            schemaDescriptor = this.getSchemaDescriptor();
        }
        this.td = this.getTableDescriptor();
        if (this.td.getTableType() == 3) {
            throw StandardException.newException("42995");
        }
        switch (this.renamingWhat) {
            case 1: {
                final TableDescriptor tableDescriptor = this.getTableDescriptor(this.newObjectName, schemaDescriptor);
                if (tableDescriptor != null) {
                    throw this.descriptorExistsException(tableDescriptor, schemaDescriptor);
                }
                this.renameTableBind(dataDictionary);
                break;
            }
            case 2: {
                this.renameColumnBind(dataDictionary);
                break;
            }
            case 3: {
                final ConglomerateDescriptor conglomerateDescriptor2 = dataDictionary.getConglomerateDescriptor(this.newObjectName, schemaDescriptor, false);
                if (conglomerateDescriptor2 != null) {
                    throw this.descriptorExistsException(conglomerateDescriptor2, schemaDescriptor);
                }
                break;
            }
        }
        this.conglomerateNumber = this.td.getHeapConglomerateId();
        final ConglomerateDescriptor conglomerateDescriptor3 = this.td.getConglomerateDescriptor(this.conglomerateNumber);
        compilerContext.createDependency(this.td);
        compilerContext.createDependency(conglomerateDescriptor3);
    }
    
    public boolean referencesSessionSchema() throws StandardException {
        return this.isSessionSchema(this.td.getSchemaName()) || (this.renamingWhat == 1 && this.isSessionSchema(this.getSchemaDescriptor()));
    }
    
    private void renameTableBind(final DataDictionary dataDictionary) throws StandardException {
        final ConstraintDescriptorList constraintDescriptors = dataDictionary.getConstraintDescriptors(this.td);
        for (int n = (constraintDescriptors == null) ? 0 : constraintDescriptors.size(), i = 0; i < n; ++i) {
            final ConstraintDescriptor element = constraintDescriptors.elementAt(i);
            if (element.getConstraintType() == 4) {
                throw StandardException.newException("X0Y25.S", "RENAME", this.td.getName(), "CONSTRAINT", element.getConstraintName());
            }
        }
    }
    
    private void renameColumnBind(final DataDictionary dataDictionary) throws StandardException {
        final ColumnDescriptor columnDescriptor = this.td.getColumnDescriptor(this.oldObjectName);
        if (columnDescriptor == null) {
            throw StandardException.newException("42X14", this.oldObjectName, this.getFullName());
        }
        final ColumnDescriptor columnDescriptor2 = this.td.getColumnDescriptor(this.newObjectName);
        if (columnDescriptor2 != null) {
            throw this.descriptorExistsException(columnDescriptor2, this.td);
        }
        final ColumnDescriptorList generatedColumns = this.td.getGeneratedColumns();
        for (int size = generatedColumns.size(), i = 0; i < size; ++i) {
            final ColumnDescriptor element = generatedColumns.elementAt(i);
            final String[] referencedColumnNames = element.getDefaultInfo().getReferencedColumnNames();
            for (int length = referencedColumnNames.length, j = 0; j < length; ++j) {
                if (this.oldObjectName.equals(referencedColumnNames[j])) {
                    throw StandardException.newException("42XA8", this.oldObjectName, element.getColumnName());
                }
            }
        }
        final ConstraintDescriptorList constraintDescriptors = dataDictionary.getConstraintDescriptors(this.td);
        for (int n = (constraintDescriptors == null) ? 0 : constraintDescriptors.size(), k = 0; k < n; ++k) {
            final ConstraintDescriptor element2 = constraintDescriptors.elementAt(k);
            if (element2.getConstraintType() == 4) {
                final ColumnDescriptorList columnDescriptors = element2.getColumnDescriptors();
                for (int size2 = columnDescriptors.size(), l = 0; l < size2; ++l) {
                    if (columnDescriptors.elementAt(l) == columnDescriptor) {
                        throw StandardException.newException("42Z97", this.oldObjectName, element2.getConstraintName());
                    }
                }
            }
        }
    }
    
    public ConstantAction makeConstantAction() throws StandardException {
        return this.getGenericConstantActionFactory().getRenameConstantAction(this.getFullName(), this.getRelativeName(), this.oldObjectName, this.newObjectName, this.getSchemaDescriptor(), this.td.getUUID(), this.usedAlterTable, this.renamingWhat);
    }
    
    private StandardException descriptorExistsException(final TupleDescriptor tupleDescriptor, final TupleDescriptor tupleDescriptor2) {
        return StandardException.newException("X0Y32.S", tupleDescriptor.getDescriptorType(), tupleDescriptor.getDescriptorName(), tupleDescriptor2.getDescriptorType(), tupleDescriptor2.getDescriptorName());
    }
}
