// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.operation;

import java.util.HashSet;
import java.util.Set;
import org.apache.hadoop.hive.metastore.TableType;
import java.util.HashMap;
import java.util.Map;

public class ClassicTableTypeMapping implements TableTypeMapping
{
    private final Map<String, String> hiveToClientMap;
    private final Map<String, String> clientToHiveMap;
    
    public ClassicTableTypeMapping() {
        this.hiveToClientMap = new HashMap<String, String>();
        this.clientToHiveMap = new HashMap<String, String>();
        this.hiveToClientMap.put(TableType.MANAGED_TABLE.toString(), ClassicTableTypes.TABLE.toString());
        this.hiveToClientMap.put(TableType.EXTERNAL_TABLE.toString(), ClassicTableTypes.TABLE.toString());
        this.hiveToClientMap.put(TableType.VIRTUAL_VIEW.toString(), ClassicTableTypes.VIEW.toString());
        this.clientToHiveMap.put(ClassicTableTypes.TABLE.toString(), TableType.MANAGED_TABLE.toString());
        this.clientToHiveMap.put(ClassicTableTypes.VIEW.toString(), TableType.VIRTUAL_VIEW.toString());
    }
    
    @Override
    public String mapToHiveType(final String clientTypeName) {
        if (this.clientToHiveMap.containsKey(clientTypeName)) {
            return this.clientToHiveMap.get(clientTypeName);
        }
        return clientTypeName;
    }
    
    @Override
    public String mapToClientType(final String hiveTypeName) {
        if (this.hiveToClientMap.containsKey(hiveTypeName)) {
            return this.hiveToClientMap.get(hiveTypeName);
        }
        return hiveTypeName;
    }
    
    @Override
    public Set<String> getTableTypeNames() {
        final Set<String> typeNameSet = new HashSet<String>();
        for (final ClassicTableTypes typeNames : ClassicTableTypes.values()) {
            typeNameSet.add(typeNames.toString());
        }
        return typeNameSet;
    }
    
    public enum ClassicTableTypes
    {
        TABLE, 
        VIEW;
    }
}
