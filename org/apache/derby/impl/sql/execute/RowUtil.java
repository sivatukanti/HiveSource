// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.sql.execute.ExecIndexRow;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;

public class RowUtil
{
    public static long rowCountBase;
    
    public static ExecRow getEmptyValueRow(final int n, final LanguageConnectionContext languageConnectionContext) {
        return languageConnectionContext.getLanguageConnectionFactory().getExecutionFactory().getValueRow(n);
    }
    
    public static ExecIndexRow getEmptyIndexRow(final int n, final LanguageConnectionContext languageConnectionContext) {
        return languageConnectionContext.getLanguageConnectionFactory().getExecutionFactory().getIndexableRow(n);
    }
    
    public static void copyCloneColumns(final ExecRow execRow, final ExecRow execRow2, final int n) {
        for (int i = 1; i <= n; ++i) {
            execRow.setColumn(i, execRow2.cloneColumn(i));
        }
    }
    
    public static void copyRefColumns(final ExecRow execRow, final ExecRow execRow2) {
        final DataValueDescriptor[] rowArray = execRow2.getRowArray();
        System.arraycopy(rowArray, 0, execRow.getRowArray(), 0, rowArray.length);
    }
    
    public static void copyRefColumns(final ExecRow execRow, final ExecRow execRow2, final int n) throws StandardException {
        copyRefColumns(execRow, 0, execRow2, 0, n);
    }
    
    public static void copyRefColumns(final ExecRow execRow, final ExecRow execRow2, final int n, final int n2) throws StandardException {
        copyRefColumns(execRow, 0, execRow2, n, n2);
    }
    
    public static void copyRefColumns(final ExecRow execRow, final int n, final ExecRow execRow2, final int n2, final int n3) throws StandardException {
        for (int i = 1; i <= n3; ++i) {
            execRow.setColumn(i + n, execRow2.getColumn(i + n2));
        }
    }
    
    public static void copyRefColumns(final ExecRow execRow, final ExecRow execRow2, final int[] array) throws StandardException {
        if (array == null) {
            return;
        }
        for (int length = array.length, i = 0; i < length; ++i) {
            execRow.setColumn(i + 1, execRow2.getColumn(array[i]));
        }
    }
    
    public static void copyRefColumns(final ExecRow execRow, final ExecRow execRow2, final FormatableBitSet set) throws StandardException {
        if (set == null) {
            return;
        }
        final int length = execRow.getRowArray().length;
        int i = 1;
        int n = 1;
        while (i <= length) {
            if (set.get(i)) {
                execRow.setColumn(i, execRow2.getColumn(n));
                ++n;
            }
            ++i;
        }
    }
    
    public static void copyRefColumns(final ExecRow execRow) throws StandardException {
        for (int i = 1; i <= execRow.nColumns(); ++i) {
            execRow.setColumn(i, null);
        }
    }
    
    public static String toString(final ExecRow execRow) {
        return "";
    }
    
    public static String toString(final Object[] array) {
        return "";
    }
    
    public static String toString(final ExecRow execRow, final int n, final int n2) {
        return toString(execRow.getRowArray(), n, n2);
    }
    
    public static String toString(final Object[] array, final int n, final int n2) {
        final StringBuffer sb = new StringBuffer();
        sb.append("(");
        for (int i = n; i <= n2; ++i) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(array[i]);
        }
        sb.append(")");
        return sb.toString();
    }
    
    public static String toString(final ExecRow execRow, final int[] array) {
        return toString(execRow.getRowArray(), array);
    }
    
    public static String toString(final Object[] array, final int[] array2) {
        if (array2 == null) {
            return null;
        }
        final StringBuffer sb = new StringBuffer();
        sb.append("(");
        for (int i = 0; i < array2.length; ++i) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(array[array2[i] - 1]);
        }
        sb.append(")");
        return sb.toString();
    }
    
    public static String intArrayToString(final int[] array) {
        final StringBuffer sb = new StringBuffer();
        sb.append("(");
        for (int i = 0; i < array.length; ++i) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(array[i]);
        }
        sb.append(")");
        return sb.toString();
    }
    
    public static boolean inAscendingOrder(final int[] array) {
        if (array != null) {
            int n = -1;
            for (int i = 0; i < array.length; ++i) {
                if (n > array[i]) {
                    return false;
                }
                n = array[i];
            }
        }
        return true;
    }
    
    public static FormatableBitSet shift(final FormatableBitSet set, final int n) {
        FormatableBitSet set2 = null;
        if (set != null) {
            final int size = set.size();
            set2 = new FormatableBitSet(size);
            for (int i = n; i < size; ++i) {
                if (set.get(i)) {
                    set2.set(i - n);
                }
            }
        }
        return set2;
    }
    
    static {
        RowUtil.rowCountBase = 0L;
    }
}
