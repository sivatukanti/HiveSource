// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.transaction;

public interface TransactionIsolation
{
    public static final int TRANSACTION_NONE = 0;
    public static final int TRANSACTION_READ_UNCOMMITTED = 1;
    public static final int TRANSACTION_READ_COMMITTED = 2;
    public static final int TRANSACTION_REPEATABLE_READ = 4;
    public static final int TRANSACTION_SERIALIZABLE = 8;
}
