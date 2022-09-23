// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.depend.DependencyManager;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.catalog.DependableFinder;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.depend.Provider;

public final class SchemaDescriptor extends TupleDescriptor implements UniqueTupleDescriptor, Provider
{
    public static final String STD_SYSTEM_SCHEMA_NAME = "SYS";
    public static final String IBM_SYSTEM_SCHEMA_NAME = "SYSIBM";
    public static final String IBM_SYSTEM_CAT_SCHEMA_NAME = "SYSCAT";
    public static final String IBM_SYSTEM_FUN_SCHEMA_NAME = "SYSFUN";
    public static final String IBM_SYSTEM_PROC_SCHEMA_NAME = "SYSPROC";
    public static final String IBM_SYSTEM_STAT_SCHEMA_NAME = "SYSSTAT";
    public static final String IBM_SYSTEM_NULLID_SCHEMA_NAME = "NULLID";
    public static final String STD_SQLJ_SCHEMA_NAME = "SQLJ";
    public static final String STD_SYSTEM_DIAG_SCHEMA_NAME = "SYSCS_DIAG";
    public static final String STD_SYSTEM_UTIL_SCHEMA_NAME = "SYSCS_UTIL";
    public static final String STD_DEFAULT_SCHEMA_NAME = "APP";
    public static final String SYSCAT_SCHEMA_UUID = "c013800d-00fb-2641-07ec-000000134f30";
    public static final String SYSFUN_SCHEMA_UUID = "c013800d-00fb-2642-07ec-000000134f30";
    public static final String SYSPROC_SCHEMA_UUID = "c013800d-00fb-2643-07ec-000000134f30";
    public static final String SYSSTAT_SCHEMA_UUID = "c013800d-00fb-2644-07ec-000000134f30";
    public static final String SYSCS_DIAG_SCHEMA_UUID = "c013800d-00fb-2646-07ec-000000134f30";
    public static final String SYSCS_UTIL_SCHEMA_UUID = "c013800d-00fb-2649-07ec-000000134f30";
    public static final String NULLID_SCHEMA_UUID = "c013800d-00fb-2647-07ec-000000134f30";
    public static final String SQLJ_SCHEMA_UUID = "c013800d-00fb-2648-07ec-000000134f30";
    public static final String SYSTEM_SCHEMA_UUID = "8000000d-00d0-fd77-3ed8-000a0a0b1900";
    public static final String SYSIBM_SCHEMA_UUID = "c013800d-00f8-5b53-28a9-00000019ed88";
    public static final String DEFAULT_SCHEMA_UUID = "80000000-00d2-b38f-4cda-000a0a412c00";
    public static final String STD_DECLARED_GLOBAL_TEMPORARY_TABLES_SCHEMA_NAME = "SESSION";
    public static final String DEFAULT_USER_NAME = "APP";
    public static final String SA_USER_NAME = "DBA";
    private final String name;
    private UUID oid;
    private String aid;
    private final boolean isSystem;
    private final boolean isSYSIBM;
    private int collationType;
    
    public SchemaDescriptor(final DataDictionary dataDictionary, final String s, final String aid, final UUID oid, final boolean isSystem) {
        super(dataDictionary);
        this.name = s;
        this.aid = aid;
        this.oid = oid;
        this.isSystem = isSystem;
        this.isSYSIBM = (isSystem && "SYSIBM".equals(s));
        if (isSystem) {
            this.collationType = dataDictionary.getCollationTypeOfSystemSchemas();
        }
        else {
            this.collationType = dataDictionary.getCollationTypeOfUserSchemas();
        }
    }
    
    public String getSchemaName() {
        return this.name;
    }
    
    public String getAuthorizationId() {
        return this.aid;
    }
    
    public void setAuthorizationId(final String aid) {
        this.aid = aid;
    }
    
    public UUID getUUID() {
        return this.oid;
    }
    
    public void setUUID(final UUID oid) {
        this.oid = oid;
    }
    
    public int getCollationType() {
        return this.collationType;
    }
    
    public DependableFinder getDependableFinder() {
        return this.getDependableFinder(371);
    }
    
    public String getObjectName() {
        return this.name;
    }
    
    public UUID getObjectID() {
        return this.oid;
    }
    
    public String getClassType() {
        return "Schema";
    }
    
    public String toString() {
        return this.name;
    }
    
    public boolean equals(final Object o) {
        if (!(o instanceof SchemaDescriptor)) {
            return false;
        }
        final SchemaDescriptor schemaDescriptor = (SchemaDescriptor)o;
        if (this.oid != null && schemaDescriptor.oid != null) {
            return this.oid.equals(schemaDescriptor.oid);
        }
        return this.name.equals(schemaDescriptor.name);
    }
    
    public boolean isSystemSchema() {
        return this.isSystem;
    }
    
    public boolean isSchemaWithGrantableRoutines() {
        return !this.isSystem || (this.name.equals("SQLJ") || this.name.equals("SYSCS_UTIL"));
    }
    
    public boolean isSYSIBM() {
        return this.isSYSIBM;
    }
    
    public int hashCode() {
        return this.oid.hashCode();
    }
    
    public String getDescriptorName() {
        return this.name;
    }
    
    public String getDescriptorType() {
        return "Schema";
    }
    
    public void drop(final LanguageConnectionContext languageConnectionContext, final Activation activation) throws StandardException {
        final DataDictionary dataDictionary = this.getDataDictionary();
        final DependencyManager dependencyManager = dataDictionary.getDependencyManager();
        final TransactionController transactionExecute = languageConnectionContext.getTransactionExecute();
        if (this.getSchemaName().equals("SESSION") && this.getUUID() == null) {
            throw StandardException.newException("42Y07", this.getSchemaName());
        }
        if (!dataDictionary.isSchemaEmpty(this)) {
            throw StandardException.newException("X0Y54.S", this.getSchemaName());
        }
        dependencyManager.invalidateFor(this, 32, languageConnectionContext);
        dataDictionary.dropSchemaDescriptor(this.getSchemaName(), transactionExecute);
        languageConnectionContext.resetSchemaUsages(activation, this.getSchemaName());
    }
}
