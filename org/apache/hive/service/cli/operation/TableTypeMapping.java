// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.operation;

import java.util.Set;

public interface TableTypeMapping
{
    String mapToHiveType(final String p0);
    
    String mapToClientType(final String p0);
    
    Set<String> getTableTypeNames();
}
