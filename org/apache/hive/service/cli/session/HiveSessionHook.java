// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.session;

import org.apache.hive.service.cli.HiveSQLException;
import org.apache.hadoop.hive.ql.hooks.Hook;

public interface HiveSessionHook extends Hook
{
    void run(final HiveSessionHookContext p0) throws HiveSQLException;
}
