// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.catalog;

public interface Dependable
{
    public static final String ALIAS = "Alias";
    public static final String CONGLOMERATE = "Conglomerate";
    public static final String CONSTRAINT = "Constraint";
    public static final String DEFAULT = "Default";
    public static final String HEAP = "Heap";
    public static final String INDEX = "Index";
    public static final String PREPARED_STATEMENT = "PreparedStatement";
    public static final String ACTIVATION = "Activation";
    public static final String FILE = "File";
    public static final String STORED_PREPARED_STATEMENT = "StoredPreparedStatement";
    public static final String TABLE = "Table";
    public static final String COLUMNS_IN_TABLE = "ColumnsInTable";
    public static final String TRIGGER = "Trigger";
    public static final String VIEW = "View";
    public static final String SCHEMA = "Schema";
    public static final String TABLE_PERMISSION = "TablePrivilege";
    public static final String COLUMNS_PERMISSION = "ColumnsPrivilege";
    public static final String ROUTINE_PERMISSION = "RoutinePrivilege";
    public static final String ROLE_GRANT = "RoleGrant";
    public static final String SEQUENCE = "Sequence";
    public static final String PERM = "Perm";
    
    DependableFinder getDependableFinder();
    
    String getObjectName();
    
    UUID getObjectID();
    
    boolean isPersistent();
    
    String getClassType();
}
