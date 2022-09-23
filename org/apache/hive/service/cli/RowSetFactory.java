// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli;

import org.apache.hive.service.cli.thrift.TRowSet;
import org.apache.hive.service.cli.thrift.TProtocolVersion;

public class RowSetFactory
{
    public static RowSet create(final TableSchema schema, final TProtocolVersion version) {
        if (version.getValue() >= TProtocolVersion.HIVE_CLI_SERVICE_PROTOCOL_V6.getValue()) {
            return new ColumnBasedSet(schema);
        }
        return new RowBasedSet(schema);
    }
    
    public static RowSet create(final TRowSet results, final TProtocolVersion version) {
        if (version.getValue() >= TProtocolVersion.HIVE_CLI_SERVICE_PROTOCOL_V6.getValue()) {
            return new ColumnBasedSet(results);
        }
        return new RowBasedSet(results);
    }
}
