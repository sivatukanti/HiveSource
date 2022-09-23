// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.conn;

import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.error.StandardException;

public interface Authorizer
{
    public static final int SQL_WRITE_OP = 0;
    public static final int SQL_SELECT_OP = 1;
    public static final int SQL_ARBITARY_OP = 2;
    public static final int SQL_CALL_OP = 3;
    public static final int SQL_DDL_OP = 4;
    public static final int PROPERTY_WRITE_OP = 5;
    public static final int JAR_WRITE_OP = 6;
    public static final int NULL_PRIV = -1;
    public static final int SELECT_PRIV = 0;
    public static final int UPDATE_PRIV = 1;
    public static final int REFERENCES_PRIV = 2;
    public static final int INSERT_PRIV = 3;
    public static final int DELETE_PRIV = 4;
    public static final int TRIGGER_PRIV = 5;
    public static final int EXECUTE_PRIV = 6;
    public static final int USAGE_PRIV = 7;
    public static final int MIN_SELECT_PRIV = 8;
    public static final int PRIV_TYPE_COUNT = 9;
    public static final int CREATE_SCHEMA_PRIV = 16;
    public static final int MODIFY_SCHEMA_PRIV = 17;
    public static final int DROP_SCHEMA_PRIV = 18;
    public static final int CREATE_ROLE_PRIV = 19;
    public static final int DROP_ROLE_PRIV = 20;
    public static final String SYSTEM_AUTHORIZATION_ID = "_SYSTEM";
    public static final String PUBLIC_AUTHORIZATION_ID = "PUBLIC";
    
    void authorize(final int p0) throws StandardException;
    
    void authorize(final Activation p0, final int p1) throws StandardException;
    
    boolean isReadOnlyConnection();
    
    void setReadOnlyConnection(final boolean p0, final boolean p1) throws StandardException;
    
    void refresh() throws StandardException;
}
