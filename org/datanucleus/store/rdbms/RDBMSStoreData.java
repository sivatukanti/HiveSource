// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms;

import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.table.ViewImpl;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.ImplementsMetaData;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.metadata.ClassMetaData;
import org.datanucleus.metadata.MetaData;
import org.datanucleus.store.StoreData;

public class RDBMSStoreData extends StoreData
{
    public RDBMSStoreData(final String name, final String tableName, final boolean tableOwner, final int type, final String interfaceName) {
        super(name, null, type, interfaceName);
        this.addProperty("table", tableName);
        this.addProperty("table-owner", tableOwner ? "true" : "false");
    }
    
    public RDBMSStoreData(final ClassMetaData cmd, final Table table, final boolean tableOwner) {
        super(cmd.getFullClassName(), cmd, 1, null);
        this.addProperty("table", (table != null) ? table.toString() : null);
        this.addProperty("table-owner", tableOwner ? "true" : "false");
        if (table != null) {
            this.addProperty("tableObject", table);
            this.addProperty("tableId", table.getIdentifier());
        }
        String interfaces = null;
        final ImplementsMetaData[] implMds = cmd.getImplementsMetaData();
        if (implMds != null) {
            for (int i = 0; i < cmd.getImplementsMetaData().length; ++i) {
                if (interfaces == null) {
                    interfaces = "";
                }
                else {
                    interfaces += ",";
                }
                interfaces += cmd.getImplementsMetaData()[i].getName();
            }
            this.addProperty("interface-name", interfaces);
        }
    }
    
    public RDBMSStoreData(final AbstractMemberMetaData mmd, final Table table) {
        super(mmd.getFullFieldName(), mmd, 2, null);
        if (table == null) {
            throw new NullPointerException("table should not be null");
        }
        this.addProperty("table", table.toString());
        this.addProperty("table-owner", "true");
        this.addProperty("tableObject", table);
        this.addProperty("tableId", table.getIdentifier());
        final String interfaceName = table.getStoreManager().getMetaDataManager().isPersistentInterface(mmd.getType().getName()) ? mmd.getType().getName() : null;
        if (interfaceName != null) {
            this.addProperty("interface-name", interfaceName);
        }
    }
    
    public boolean mapsToView() {
        final Table table = this.getTable();
        return table != null && table instanceof ViewImpl;
    }
    
    public String getTableName() {
        return this.properties.get("table");
    }
    
    public boolean isTableOwner() {
        return this.properties.get("table-owner").equals("true");
    }
    
    public boolean hasTable() {
        return this.properties.get("table") != null;
    }
    
    public Table getTable() {
        return this.properties.get("tableObject");
    }
    
    public DatastoreIdentifier getDatastoreIdentifier() {
        return this.properties.get("tableId");
    }
    
    public void setDatastoreContainerObject(final DatastoreClass table) {
        if (table != null) {
            this.addProperty("table", table.toString());
            this.addProperty("tableObject", table);
            this.addProperty("tableId", table.getIdentifier());
        }
    }
    
    @Override
    public String toString() {
        final String tableName = this.properties.get("table");
        final MetaData metadata = this.getMetaData();
        if (metadata instanceof ClassMetaData) {
            final ClassMetaData cmd = (ClassMetaData)metadata;
            return RDBMSStoreData.LOCALISER.msg("035004", this.name, (tableName != null) ? tableName : "(none)", cmd.getInheritanceMetaData().getStrategy().toString());
        }
        if (metadata instanceof AbstractMemberMetaData) {
            return RDBMSStoreData.LOCALISER.msg("035005", this.name, tableName);
        }
        return RDBMSStoreData.LOCALISER.msg("035004", this.name, tableName);
    }
}
