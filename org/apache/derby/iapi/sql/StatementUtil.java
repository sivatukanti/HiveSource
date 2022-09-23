// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql;

public class StatementUtil
{
    private static final String[] TypeNames;
    
    private StatementUtil() {
    }
    
    public static String typeName(final int n) {
        String s = null;
        switch (n) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6: {
                s = StatementUtil.TypeNames[n];
                break;
            }
            default: {
                s = "UNKNOWN";
                break;
            }
        }
        return s;
    }
    
    static {
        TypeNames = new String[] { "", "INSERT", "INSERT", "UPDATE", "DELETE", "ENABLED", "DISABLED" };
    }
}
