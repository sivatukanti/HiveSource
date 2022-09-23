// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.operation;

import org.apache.hadoop.hive.metastore.TableType;
import java.util.HashSet;
import java.util.Set;

public class HiveTableTypeMapping implements TableTypeMapping
{
    @Override
    public String mapToHiveType(final String clientTypeName) {
        return clientTypeName;
    }
    
    @Override
    public String mapToClientType(final String hiveTypeName) {
        return hiveTypeName;
    }
    
    @Override
    public Set<String> getTableTypeNames() {
        final Set<String> typeNameSet = new HashSet<String>();
        for (final TableType typeNames : TableType.values()) {
            typeNameSet.add(typeNames.toString());
        }
        return typeNameSet;
    }
}
