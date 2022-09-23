// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.impl.sql.execute.GenericPrivilegeInfo;
import org.apache.derby.iapi.sql.dictionary.PrivilegedSQLObject;
import org.apache.derby.impl.sql.execute.PrivilegeInfo;
import org.apache.derby.catalog.TypeDescriptor;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.catalog.types.RoutineAliasInfo;
import org.apache.derby.iapi.sql.dictionary.AliasDescriptor;
import java.util.List;
import java.util.HashMap;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.depend.Provider;

public class PrivilegeNode extends QueryTreeNode
{
    public static final int TABLE_PRIVILEGES = 0;
    public static final int ROUTINE_PRIVILEGES = 1;
    public static final int SEQUENCE_PRIVILEGES = 2;
    public static final int UDT_PRIVILEGES = 3;
    public static final int AGGREGATE_PRIVILEGES = 4;
    private int objectType;
    private TableName objectName;
    private TablePrivilegesNode specificPrivileges;
    private RoutineDesignator routineDesignator;
    private String privilege;
    private boolean restrict;
    private Provider dependencyProvider;
    
    public void init(final Object o, final Object o2, final Object o3) throws StandardException {
        switch (this.objectType = (int)o) {
            case 0: {
                this.objectName = (TableName)o2;
                this.specificPrivileges = (TablePrivilegesNode)o3;
                break;
            }
            case 1: {
                this.routineDesignator = (RoutineDesignator)o2;
                this.objectName = this.routineDesignator.name;
                break;
            }
            default: {
                throw this.unimplementedFeature();
            }
        }
    }
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4) {
        this.objectType = (int)o;
        this.objectName = (TableName)o2;
        this.privilege = (String)o3;
        this.restrict = (boolean)o4;
    }
    
    public QueryTreeNode bind(final HashMap hashMap, final List list, final boolean b) throws StandardException {
        final SchemaDescriptor schemaDescriptor = this.getSchemaDescriptor(this.objectName.getSchemaName(), true);
        this.objectName.setSchemaName(schemaDescriptor.getSchemaName());
        if (list.contains(schemaDescriptor.getAuthorizationId())) {
            throw StandardException.newException("42509", this.objectName.getFullTableName());
        }
        switch (this.objectType) {
            case 0: {
                if (schemaDescriptor.isSystemSchema()) {
                    throw StandardException.newException("42509", this.objectName.getFullTableName());
                }
                final TableDescriptor tableDescriptor = this.getTableDescriptor(this.objectName.getTableName(), schemaDescriptor);
                if (tableDescriptor == null) {
                    throw StandardException.newException("42X05", this.objectName);
                }
                if (this.isSessionSchema(schemaDescriptor.getSchemaName())) {
                    throw StandardException.newException("XCL51.S");
                }
                if (tableDescriptor.getTableType() != 0 && tableDescriptor.getTableType() != 2) {
                    throw StandardException.newException("42509", this.objectName.getFullTableName());
                }
                this.specificPrivileges.bind(tableDescriptor, b);
                this.dependencyProvider = tableDescriptor;
                break;
            }
            case 1: {
                if (!schemaDescriptor.isSchemaWithGrantableRoutines()) {
                    throw StandardException.newException("42509", this.objectName.getFullTableName());
                }
                AliasDescriptor aliasDescriptor = null;
                final List routineList = this.getDataDictionary().getRoutineList(schemaDescriptor.getUUID().toString(), this.objectName.getTableName(), this.routineDesignator.isFunction ? 'F' : 'P');
                if (this.routineDesignator.paramTypeList == null) {
                    if (routineList.size() > 1) {
                        throw StandardException.newException(this.routineDesignator.isFunction ? "42X46" : "42X47", this.objectName.getFullTableName());
                    }
                    if (routineList.size() != 1) {
                        if (this.routineDesignator.isFunction) {
                            throw StandardException.newException("42Y03.S.2", this.objectName.getFullTableName());
                        }
                        throw StandardException.newException("42Y03.S.1", this.objectName.getFullTableName());
                    }
                    else {
                        aliasDescriptor = routineList.get(0);
                    }
                }
                else {
                    int n = 0;
                    for (int n2 = routineList.size() - 1; n == 0 && n2 >= 0; --n2) {
                        aliasDescriptor = routineList.get(n2);
                        final RoutineAliasInfo routineAliasInfo = (RoutineAliasInfo)aliasDescriptor.getAliasInfo();
                        final int parameterCount = routineAliasInfo.getParameterCount();
                        if (parameterCount == this.routineDesignator.paramTypeList.size()) {
                            final TypeDescriptor[] parameterTypes = routineAliasInfo.getParameterTypes();
                            n = 1;
                            for (int i = 0; i < parameterCount; ++i) {
                                if (!parameterTypes[i].equals(this.routineDesignator.paramTypeList.get(i))) {
                                    n = 0;
                                    break;
                                }
                            }
                        }
                    }
                    if (n == 0) {
                        final StringBuffer sb = new StringBuffer(this.objectName.getFullTableName());
                        sb.append("(");
                        for (int j = 0; j < this.routineDesignator.paramTypeList.size(); ++j) {
                            if (j > 0) {
                                sb.append(",");
                            }
                            sb.append(this.routineDesignator.paramTypeList.get(j).toString());
                        }
                        throw StandardException.newException("42Y03.S.0", sb.toString());
                    }
                }
                this.routineDesignator.setAliasDescriptor(aliasDescriptor);
                this.dependencyProvider = aliasDescriptor;
                break;
            }
            case 4: {
                this.dependencyProvider = this.getDataDictionary().getAliasDescriptor(schemaDescriptor.getUUID().toString(), this.objectName.getTableName(), 'G');
                if (this.dependencyProvider == null) {
                    throw StandardException.newException("42X94", "DERBY AGGREGATE", this.objectName.getFullTableName());
                }
                break;
            }
            case 2: {
                this.dependencyProvider = this.getDataDictionary().getSequenceDescriptor(schemaDescriptor, this.objectName.getTableName());
                if (this.dependencyProvider == null) {
                    throw StandardException.newException("42X94", "SEQUENCE", this.objectName.getFullTableName());
                }
                break;
            }
            case 3: {
                this.dependencyProvider = this.getDataDictionary().getAliasDescriptor(schemaDescriptor.getUUID().toString(), this.objectName.getTableName(), 'A');
                if (this.dependencyProvider == null) {
                    throw StandardException.newException("42X94", "TYPE", this.objectName.getFullTableName());
                }
                break;
            }
            default: {
                throw this.unimplementedFeature();
            }
        }
        if (this.dependencyProvider != null && hashMap.get(this.dependencyProvider) == null) {
            this.getCompilerContext().createDependency(this.dependencyProvider);
            hashMap.put(this.dependencyProvider, this.dependencyProvider);
        }
        return this;
    }
    
    PrivilegeInfo makePrivilegeInfo() throws StandardException {
        switch (this.objectType) {
            case 0: {
                return this.specificPrivileges.makePrivilegeInfo();
            }
            case 1: {
                return this.routineDesignator.makePrivilegeInfo();
            }
            case 2:
            case 3:
            case 4: {
                return new GenericPrivilegeInfo((PrivilegedSQLObject)this.dependencyProvider, this.privilege, this.restrict);
            }
            default: {
                throw this.unimplementedFeature();
            }
        }
    }
    
    private StandardException unimplementedFeature() {
        return StandardException.newException("XSCB3.S");
    }
}
