// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.iapi.sql.depend.DependencyManager;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.catalog.types.RoutineAliasInfo;
import org.apache.derby.catalog.DependableFinder;
import org.apache.derby.iapi.util.IdUtil;
import org.apache.derby.catalog.types.AggregateAliasInfo;
import org.apache.derby.catalog.types.UDTAliasInfo;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.catalog.AliasInfo;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.depend.Dependent;
import org.apache.derby.iapi.sql.depend.Provider;

public final class AliasDescriptor extends TupleDescriptor implements PrivilegedSQLObject, Provider, Dependent
{
    private final UUID aliasID;
    private final String aliasName;
    private final UUID schemaID;
    private final String javaClassName;
    private final char aliasType;
    private final char nameSpace;
    private final boolean systemAlias;
    private final AliasInfo aliasInfo;
    private final String specificName;
    private final SchemaDescriptor schemaDescriptor;
    
    public AliasDescriptor(final DataDictionary dataDictionary, final UUID aliasID, final String aliasName, final UUID schemaID, final String javaClassName, final char aliasType, final char nameSpace, final boolean systemAlias, final AliasInfo aliasInfo, String systemSQLName) throws StandardException {
        super(dataDictionary);
        this.aliasID = aliasID;
        this.aliasName = aliasName;
        this.schemaID = schemaID;
        this.schemaDescriptor = dataDictionary.getSchemaDescriptor(schemaID, null);
        this.javaClassName = javaClassName;
        this.aliasType = aliasType;
        this.nameSpace = nameSpace;
        this.systemAlias = systemAlias;
        this.aliasInfo = aliasInfo;
        if (systemSQLName == null) {
            systemSQLName = dataDictionary.getSystemSQLName();
        }
        this.specificName = systemSQLName;
    }
    
    public UUID getUUID() {
        return this.aliasID;
    }
    
    public String getObjectTypeName() {
        if (this.aliasInfo instanceof UDTAliasInfo) {
            return "TYPE";
        }
        if (this.aliasInfo instanceof AggregateAliasInfo) {
            return "DERBY AGGREGATE";
        }
        return null;
    }
    
    public UUID getSchemaUUID() {
        return this.schemaID;
    }
    
    public final SchemaDescriptor getSchemaDescriptor() {
        return this.schemaDescriptor;
    }
    
    public final String getName() {
        return this.aliasName;
    }
    
    public String getSchemaName() {
        return this.schemaDescriptor.getSchemaName();
    }
    
    public String getQualifiedName() {
        return IdUtil.mkQualifiedName(this.getSchemaName(), this.aliasName);
    }
    
    public String getJavaClassName() {
        return this.javaClassName;
    }
    
    public char getAliasType() {
        return this.aliasType;
    }
    
    public char getNameSpace() {
        return this.nameSpace;
    }
    
    public boolean getSystemAlias() {
        return this.systemAlias;
    }
    
    public AliasInfo getAliasInfo() {
        return this.aliasInfo;
    }
    
    public String toString() {
        return "";
    }
    
    public boolean equals(final Object o) {
        return o instanceof AliasDescriptor && this.aliasID.equals(((AliasDescriptor)o).getUUID());
    }
    
    public int hashCode() {
        return this.aliasID.hashCode();
    }
    
    public DependableFinder getDependableFinder() {
        return this.getDependableFinder(136);
    }
    
    public String getObjectName() {
        return this.aliasName;
    }
    
    public UUID getObjectID() {
        return this.aliasID;
    }
    
    public String getClassType() {
        return "Alias";
    }
    
    public String getDescriptorType() {
        return getAliasType(this.aliasType);
    }
    
    public static final String getAliasType(final char c) {
        switch (c) {
            case 'P': {
                return "PROCEDURE";
            }
            case 'F': {
                return "FUNCTION";
            }
            case 'S': {
                return "SYNONYM";
            }
            case 'A': {
                return "TYPE";
            }
            case 'G': {
                return "DERBY AGGREGATE";
            }
            default: {
                return null;
            }
        }
    }
    
    public String getDescriptorName() {
        return this.aliasName;
    }
    
    public String getSpecificName() {
        return this.specificName;
    }
    
    public boolean isPersistent() {
        return !this.getSchemaUUID().toString().equals("c013800d-00fb-2642-07ec-000000134f30");
    }
    
    public boolean isTableFunction() {
        return this.getAliasType() == 'F' && ((RoutineAliasInfo)this.getAliasInfo()).getReturnType().isRowMultiSet();
    }
    
    public void drop(final LanguageConnectionContext languageConnectionContext) throws StandardException {
        final DataDictionary dataDictionary = this.getDataDictionary();
        final TransactionController transactionExecute = languageConnectionContext.getTransactionExecute();
        final DependencyManager dependencyManager = dataDictionary.getDependencyManager();
        int n = 0;
        switch (this.getAliasType()) {
            case 'F':
            case 'P': {
                n = 6;
                break;
            }
            case 'S': {
                n = 43;
                break;
            }
            case 'A': {
                n = 50;
                break;
            }
            case 'G': {
                n = 51;
                break;
            }
        }
        dependencyManager.invalidateFor(this, n, languageConnectionContext);
        if (this.getAliasType() == 'S') {
            final SchemaDescriptor schemaDescriptor = dataDictionary.getSchemaDescriptor(this.schemaID, transactionExecute);
            dataDictionary.dropTableDescriptor(dataDictionary.getDataDescriptorGenerator().newTableDescriptor(this.aliasName, schemaDescriptor, 4, 'R'), schemaDescriptor, transactionExecute);
        }
        else {
            dataDictionary.dropAllRoutinePermDescriptors(this.getUUID(), transactionExecute);
        }
        dataDictionary.dropAliasDescriptor(this, transactionExecute);
    }
    
    public synchronized boolean isValid() {
        return true;
    }
    
    public void prepareToInvalidate(final Provider provider, final int n, final LanguageConnectionContext languageConnectionContext) throws StandardException {
        throw StandardException.newException("X0Y30.S", this.getDataDictionary().getDependencyManager().getActionString(n), provider.getObjectName(), this.getQualifiedName());
    }
    
    public void makeInvalid(final int n, final LanguageConnectionContext languageConnectionContext) throws StandardException {
    }
}
