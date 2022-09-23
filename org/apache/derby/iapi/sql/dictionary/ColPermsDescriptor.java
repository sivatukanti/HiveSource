// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.catalog.DependableFinder;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.catalog.UUID;

public class ColPermsDescriptor extends PermissionsDescriptor
{
    private UUID tableUUID;
    private String type;
    private FormatableBitSet columns;
    private String tableName;
    
    public ColPermsDescriptor(final DataDictionary dataDictionary, final String s, final String s2, final UUID tableUUID, final String type, final FormatableBitSet columns) throws StandardException {
        super(dataDictionary, s, s2);
        this.tableUUID = tableUUID;
        this.type = type;
        this.columns = columns;
        if (tableUUID != null) {
            this.tableName = dataDictionary.getTableDescriptor(tableUUID).getName();
        }
    }
    
    public ColPermsDescriptor(final DataDictionary dataDictionary, final String s, final String s2, final UUID uuid, final String s3) throws StandardException {
        this(dataDictionary, s, s2, uuid, s3, null);
    }
    
    public ColPermsDescriptor(final DataDictionary dataDictionary, final UUID oid) throws StandardException {
        super(dataDictionary, null, null);
        this.oid = oid;
    }
    
    public int getCatalogNumber() {
        return 17;
    }
    
    public UUID getTableUUID() {
        return this.tableUUID;
    }
    
    public String getType() {
        return this.type;
    }
    
    public FormatableBitSet getColumns() {
        return this.columns;
    }
    
    public String toString() {
        return "colPerms: grantee=" + this.getGrantee() + ",colPermsUUID=" + this.getUUID() + ",grantor=" + this.getGrantor() + ",tableUUID=" + this.getTableUUID() + ",type=" + this.getType() + ",columns=" + this.getColumns();
    }
    
    public boolean equals(final Object o) {
        if (!(o instanceof ColPermsDescriptor)) {
            return false;
        }
        final ColPermsDescriptor colPermsDescriptor = (ColPermsDescriptor)o;
        return super.keyEquals(colPermsDescriptor) && this.tableUUID.equals(colPermsDescriptor.tableUUID) && ((this.type != null) ? this.type.equals(colPermsDescriptor.type) : (colPermsDescriptor.type == null));
    }
    
    public int hashCode() {
        return super.keyHashCode() + this.tableUUID.hashCode() + ((this.type == null) ? 0 : this.type.hashCode());
    }
    
    public boolean checkOwner(final String anObject) throws StandardException {
        return this.getDataDictionary().getTableDescriptor(this.tableUUID).getSchemaDescriptor().getAuthorizationId().equals(anObject);
    }
    
    public String getObjectName() {
        return "Column Privilege on " + this.tableName;
    }
    
    public String getClassType() {
        return "ColumnsPrivilege";
    }
    
    public DependableFinder getDependableFinder() {
        return this.getDependableFinder(463);
    }
}
