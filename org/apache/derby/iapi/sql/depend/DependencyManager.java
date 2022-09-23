// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.depend;

import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.context.ContextManager;

public interface DependencyManager
{
    public static final int COMPILE_FAILED = 0;
    public static final int DROP_TABLE = 1;
    public static final int DROP_INDEX = 2;
    public static final int CREATE_INDEX = 3;
    public static final int ROLLBACK = 4;
    public static final int CHANGED_CURSOR = 5;
    public static final int DROP_METHOD_ALIAS = 6;
    public static final int DROP_VIEW = 9;
    public static final int CREATE_VIEW = 10;
    public static final int PREPARED_STATEMENT_RELEASE = 11;
    public static final int ALTER_TABLE = 12;
    public static final int DROP_SPS = 13;
    public static final int USER_RECOMPILE_REQUEST = 14;
    public static final int BULK_INSERT = 15;
    public static final int DROP_JAR = 17;
    public static final int REPLACE_JAR = 18;
    public static final int DROP_CONSTRAINT = 19;
    public static final int SET_CONSTRAINTS_ENABLE = 20;
    public static final int SET_CONSTRAINTS_DISABLE = 21;
    public static final int CREATE_CONSTRAINT = 22;
    public static final int INTERNAL_RECOMPILE_REQUEST = 23;
    public static final int DROP_TRIGGER = 27;
    public static final int CREATE_TRIGGER = 28;
    public static final int SET_TRIGGERS_ENABLE = 29;
    public static final int SET_TRIGGERS_DISABLE = 30;
    public static final int MODIFY_COLUMN_DEFAULT = 31;
    public static final int DROP_SCHEMA = 32;
    public static final int COMPRESS_TABLE = 33;
    public static final int RENAME = 34;
    public static final int DROP_COLUMN = 37;
    public static final int DROP_STATISTICS = 39;
    public static final int UPDATE_STATISTICS = 40;
    public static final int RENAME_INDEX = 41;
    public static final int TRUNCATE_TABLE = 42;
    public static final int DROP_SYNONYM = 43;
    public static final int REVOKE_PRIVILEGE = 44;
    public static final int REVOKE_PRIVILEGE_RESTRICT = 45;
    public static final int DROP_COLUMN_RESTRICT = 46;
    public static final int REVOKE_ROLE = 47;
    public static final int RECHECK_PRIVILEGES = 48;
    public static final int DROP_SEQUENCE = 49;
    public static final int DROP_UDT = 50;
    public static final int DROP_AGGREGATE = 51;
    public static final int MAX_ACTION_CODE = 65535;
    
    void addDependency(final Dependent p0, final Provider p1, final ContextManager p2) throws StandardException;
    
    void invalidateFor(final Provider p0, final int p1, final LanguageConnectionContext p2) throws StandardException;
    
    void clearDependencies(final LanguageConnectionContext p0, final Dependent p1) throws StandardException;
    
    void clearInMemoryDependency(final Dependency p0);
    
    ProviderInfo[] getPersistentProviderInfos(final Dependent p0) throws StandardException;
    
    ProviderInfo[] getPersistentProviderInfos(final ProviderList p0) throws StandardException;
    
    void clearColumnInfoInProviders(final ProviderList p0) throws StandardException;
    
    void copyDependencies(final Dependent p0, final Dependent p1, final boolean p2, final ContextManager p3) throws StandardException;
    
    String getActionString(final int p0);
    
    int countDependencies() throws StandardException;
    
    void clearDependencies(final LanguageConnectionContext p0, final Dependent p1, final TransactionController p2) throws StandardException;
    
    void copyDependencies(final Dependent p0, final Dependent p1, final boolean p2, final ContextManager p3, final TransactionController p4) throws StandardException;
}
