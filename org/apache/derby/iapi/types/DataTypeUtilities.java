// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

public abstract class DataTypeUtilities
{
    public static int getPrecision(final DataTypeDescriptor dataTypeDescriptor) {
        switch (dataTypeDescriptor.getTypeId().getJDBCTypeId()) {
            case -4:
            case -3:
            case -2:
            case -1:
            case 1:
            case 12:
            case 2004:
            case 2005:
            case 2009: {
                return dataTypeDescriptor.getMaximumWidth();
            }
            case 5: {
                return 5;
            }
            case 16: {
                return 1;
            }
            default: {
                return dataTypeDescriptor.getPrecision();
            }
        }
    }
    
    public static int getDigitPrecision(final DataTypeDescriptor dataTypeDescriptor) {
        switch (dataTypeDescriptor.getTypeId().getJDBCTypeId()) {
            case 6:
            case 8: {
                return 15;
            }
            case 7: {
                return 7;
            }
            default: {
                return getPrecision(dataTypeDescriptor);
            }
        }
    }
    
    public static boolean isCurrency(final DataTypeDescriptor dataTypeDescriptor) {
        final int jdbcTypeId = dataTypeDescriptor.getTypeId().getJDBCTypeId();
        return jdbcTypeId == 3 || jdbcTypeId == 2;
    }
    
    public static boolean isCaseSensitive(final DataTypeDescriptor dataTypeDescriptor) {
        final int jdbcTypeId = dataTypeDescriptor.getTypeId().getJDBCTypeId();
        return jdbcTypeId == 1 || jdbcTypeId == 12 || jdbcTypeId == 2005 || jdbcTypeId == -1 || jdbcTypeId == 2009;
    }
    
    public static int isNullable(final DataTypeDescriptor dataTypeDescriptor) {
        return dataTypeDescriptor.isNullable() ? 1 : 0;
    }
    
    public static boolean isSigned(final DataTypeDescriptor dataTypeDescriptor) {
        final int jdbcTypeId = dataTypeDescriptor.getTypeId().getJDBCTypeId();
        return jdbcTypeId == 4 || jdbcTypeId == 6 || jdbcTypeId == 3 || jdbcTypeId == 5 || jdbcTypeId == -5 || jdbcTypeId == -6 || jdbcTypeId == 2 || jdbcTypeId == 7 || jdbcTypeId == 8;
    }
    
    public static int getColumnDisplaySize(final DataTypeDescriptor dataTypeDescriptor) {
        return getColumnDisplaySize(dataTypeDescriptor.getTypeId().getJDBCTypeId(), dataTypeDescriptor.getMaximumWidth());
    }
    
    public static int getColumnDisplaySize(final int n, final int n2) {
        int n3 = 0;
        switch (n) {
            case 93: {
                n3 = 29;
                break;
            }
            case 91: {
                n3 = 10;
                break;
            }
            case 92: {
                n3 = 8;
                break;
            }
            case 4: {
                n3 = 11;
                break;
            }
            case 5: {
                n3 = 6;
                break;
            }
            case 6:
            case 7: {
                n3 = 15;
                break;
            }
            case 8: {
                n3 = 24;
                break;
            }
            case -6: {
                n3 = 15;
                break;
            }
            case -4:
            case -3:
            case -2:
            case 2004: {
                n3 = 2 * n2;
                if (n3 < 0) {
                    n3 = Integer.MAX_VALUE;
                    break;
                }
                break;
            }
            case -5: {
                n3 = 20;
                break;
            }
            case -7:
            case 16: {
                n3 = 5;
                break;
            }
            default: {
                n3 = ((n2 > 0) ? n2 : 15);
                break;
            }
        }
        return n3;
    }
    
    public static int computeMaxWidth(final int n, final int n2) {
        return (n2 == 0) ? (n + 1) : ((n2 == n) ? (n + 3) : (n + 2));
    }
}
