// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.access;

public interface AccessFactoryGlobals
{
    public static final String USER_TRANS_NAME = "UserTransaction";
    public static final String SYS_TRANS_NAME = "SystemTransaction";
    public static final int BTREE_OVERFLOW_THRESHOLD = 50;
    public static final int HEAP_OVERFLOW_THRESHOLD = 100;
    public static final int SORT_OVERFLOW_THRESHOLD = 100;
    public static final String CFG_CONGLOMDIR_CACHE = "ConglomerateDirectoryCache";
    public static final String HEAP = "heap";
    public static final String DEFAULT_PROPERTY_NAME = "derby.defaultPropertyName";
    public static final String PAGE_RESERVED_SPACE_PROP = "0";
    public static final String CONGLOM_PROP = "derby.access.Conglomerate.type";
    public static final String IMPL_TYPE = "implType";
    public static final String SORT_EXTERNAL = "sort external";
    public static final String SORT_INTERNAL = "sort internal";
    public static final String SORT_UNIQUEWITHDUPLICATENULLS_EXTERNAL = "sort almost unique external";
    public static final String NESTED_READONLY_USER_TRANS = "nestedReadOnlyUserTransaction";
    public static final String NESTED_UPDATE_USER_TRANS = "nestedUpdateUserTransaction";
    public static final String RAMXACT_CONTEXT_ID = "RAMTransactionContext";
    public static final String RAMXACT_CHILD_CONTEXT_ID = "RAMChildContext";
    public static final String RAMXACT_INTERNAL_CONTEXT_ID = "RAMInternalContext";
}
