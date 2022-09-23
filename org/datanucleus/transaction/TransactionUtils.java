// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.transaction;

public class TransactionUtils
{
    public static String getNameForTransactionIsolationLevel(final int isolation) {
        if (isolation == 0) {
            return "none";
        }
        if (isolation == 2) {
            return "read-committed";
        }
        if (isolation == 1) {
            return "read-uncommitted";
        }
        if (isolation == 4) {
            return "repeatable-read";
        }
        if (isolation == 8) {
            return "serializable";
        }
        return "UNKNOWN";
    }
    
    public static int getTransactionIsolationLevelForName(final String isolationName) {
        if (isolationName.equalsIgnoreCase("none")) {
            return 0;
        }
        if (isolationName.equalsIgnoreCase("read-committed")) {
            return 2;
        }
        if (isolationName.equalsIgnoreCase("read-uncommitted")) {
            return 1;
        }
        if (isolationName.equalsIgnoreCase("repeatable-read")) {
            return 4;
        }
        if (isolationName.equalsIgnoreCase("serializable")) {
            return 8;
        }
        return -1;
    }
}
