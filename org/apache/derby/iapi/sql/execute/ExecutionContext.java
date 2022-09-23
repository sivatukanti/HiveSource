// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.execute;

import org.apache.derby.iapi.services.context.Context;

public interface ExecutionContext extends Context
{
    public static final String CONTEXT_ID = "ExecutionContext";
    public static final int UNSPECIFIED_ISOLATION_LEVEL = 0;
    public static final int READ_UNCOMMITTED_ISOLATION_LEVEL = 1;
    public static final int READ_COMMITTED_ISOLATION_LEVEL = 2;
    public static final int REPEATABLE_READ_ISOLATION_LEVEL = 3;
    public static final int SERIALIZABLE_ISOLATION_LEVEL = 4;
    public static final int[] CS_TO_JDBC_ISOLATION_LEVEL_MAP = { 0, 1, 2, 4, 8 };
    public static final String[][] CS_TO_SQL_ISOLATION_MAP = { { "  " }, { "UR", "DIRTY READ", "READ UNCOMMITTED" }, { "CS", "CURSOR STABILITY", "READ COMMITTED" }, { "RS" }, { "RR", "REPEATABLE READ", "SERIALIZABLE" } };
    
    ExecutionFactory getExecutionFactory();
}
