// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.shared.common.error;

public interface ExceptionSeverity
{
    public static final int NO_APPLICABLE_SEVERITY = 0;
    public static final int WARNING_SEVERITY = 10000;
    public static final int STATEMENT_SEVERITY = 20000;
    public static final int TRANSACTION_SEVERITY = 30000;
    public static final int SESSION_SEVERITY = 40000;
    public static final int DATABASE_SEVERITY = 45000;
    public static final int SYSTEM_SEVERITY = 50000;
}
