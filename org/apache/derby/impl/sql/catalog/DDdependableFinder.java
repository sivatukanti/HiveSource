// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.sql.dictionary.ColumnDescriptor;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.dictionary.DefaultDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.catalog.Dependable;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;
import org.apache.derby.iapi.services.io.Formatable;
import org.apache.derby.catalog.DependableFinder;

public class DDdependableFinder implements DependableFinder, Formatable
{
    private final int formatId;
    
    public DDdependableFinder(final int formatId) {
        this.formatId = formatId;
    }
    
    public String toString() {
        return this.getSQLObjectType();
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
    }
    
    public final int getTypeFormatId() {
        return this.formatId;
    }
    
    public String getSQLObjectType() {
        switch (this.formatId) {
            case 136: {
                return "Alias";
            }
            case 135: {
                return "Conglomerate";
            }
            case 208: {
                return "Constraint";
            }
            case 325: {
                return "Default";
            }
            case 273: {
                return "File";
            }
            case 371: {
                return "Schema";
            }
            case 226: {
                return "StoredPreparedStatement";
            }
            case 137: {
                return "Table";
            }
            case 393: {
                return "ColumnsInTable";
            }
            case 320: {
                return "Trigger";
            }
            case 145: {
                return "View";
            }
            case 462: {
                return "TablePrivilege";
            }
            case 463: {
                return "ColumnsPrivilege";
            }
            case 461: {
                return "RoutinePrivilege";
            }
            case 471: {
                return "RoleGrant";
            }
            case 472: {
                return "Sequence";
            }
            case 473: {
                return "Perm";
            }
            default: {
                return null;
            }
        }
    }
    
    public final Dependable getDependable(final DataDictionary dataDictionary, final UUID uuid) throws StandardException {
        final Dependable dependable = this.findDependable(dataDictionary, uuid);
        if (dependable == null) {
            throw StandardException.newException("42X94", this.getSQLObjectType(), uuid);
        }
        return dependable;
    }
    
    Dependable findDependable(final DataDictionary dataDictionary, final UUID uuid) throws StandardException {
        switch (this.formatId) {
            case 136: {
                return dataDictionary.getAliasDescriptor(uuid);
            }
            case 135: {
                return dataDictionary.getConglomerateDescriptor(uuid);
            }
            case 208: {
                return dataDictionary.getConstraintDescriptor(uuid);
            }
            case 325: {
                final ColumnDescriptor columnDescriptorByDefaultId = dataDictionary.getColumnDescriptorByDefaultId(uuid);
                if (columnDescriptorByDefaultId != null) {
                    return new DefaultDescriptor(dataDictionary, columnDescriptorByDefaultId.getDefaultUUID(), columnDescriptorByDefaultId.getReferencingUUID(), columnDescriptorByDefaultId.getPosition());
                }
                return null;
            }
            case 273: {
                return dataDictionary.getFileInfoDescriptor(uuid);
            }
            case 371: {
                return dataDictionary.getSchemaDescriptor(uuid, null);
            }
            case 226: {
                return dataDictionary.getSPSDescriptor(uuid);
            }
            case 137: {
                return dataDictionary.getTableDescriptor(uuid);
            }
            case 320: {
                return dataDictionary.getTriggerDescriptor(uuid);
            }
            case 145: {
                return dataDictionary.getViewDescriptor(uuid);
            }
            case 463: {
                return dataDictionary.getColumnPermissions(uuid);
            }
            case 462: {
                return dataDictionary.getTablePermissions(uuid);
            }
            case 461: {
                return dataDictionary.getRoutinePermissions(uuid);
            }
            case 471: {
                return dataDictionary.getRoleGrantDescriptor(uuid);
            }
            case 472: {
                return dataDictionary.getSequenceDescriptor(uuid);
            }
            case 473: {
                return dataDictionary.getGenericPermissions(uuid);
            }
            default: {
                return null;
            }
        }
    }
}
