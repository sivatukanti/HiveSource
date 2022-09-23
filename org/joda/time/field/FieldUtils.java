// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.field;

import org.joda.time.DateTimeFieldType;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.DateTimeField;

public class FieldUtils
{
    private FieldUtils() {
    }
    
    public static int safeNegate(final int n) {
        if (n == Integer.MIN_VALUE) {
            throw new ArithmeticException("Integer.MIN_VALUE cannot be negated");
        }
        return -n;
    }
    
    public static int safeAdd(final int i, final int j) {
        final int n = i + j;
        if ((i ^ n) < 0 && (i ^ j) >= 0) {
            throw new ArithmeticException("The calculation caused an overflow: " + i + " + " + j);
        }
        return n;
    }
    
    public static long safeAdd(final long lng, final long lng2) {
        final long n = lng + lng2;
        if ((lng ^ n) < 0L && (lng ^ lng2) >= 0L) {
            throw new ArithmeticException("The calculation caused an overflow: " + lng + " + " + lng2);
        }
        return n;
    }
    
    public static long safeSubtract(final long lng, final long lng2) {
        final long n = lng - lng2;
        if ((lng ^ n) < 0L && (lng ^ lng2) < 0L) {
            throw new ArithmeticException("The calculation caused an overflow: " + lng + " - " + lng2);
        }
        return n;
    }
    
    public static int safeMultiply(final int i, final int j) {
        final long n = i * (long)j;
        if (n < -2147483648L || n > 2147483647L) {
            throw new ArithmeticException("Multiplication overflows an int: " + i + " * " + j);
        }
        return (int)n;
    }
    
    public static long safeMultiply(final long n, final int n2) {
        switch (n2) {
            case -1: {
                if (n == Long.MIN_VALUE) {
                    throw new ArithmeticException("Multiplication overflows a long: " + n + " * " + n2);
                }
                return -n;
            }
            case 0: {
                return 0L;
            }
            case 1: {
                return n;
            }
            default: {
                final long n3 = n * n2;
                if (n3 / n2 != n) {
                    throw new ArithmeticException("Multiplication overflows a long: " + n + " * " + n2);
                }
                return n3;
            }
        }
    }
    
    public static long safeMultiply(final long lng, final long lng2) {
        if (lng2 == 1L) {
            return lng;
        }
        if (lng == 1L) {
            return lng2;
        }
        if (lng == 0L || lng2 == 0L) {
            return 0L;
        }
        final long n = lng * lng2;
        if (n / lng2 != lng || (lng == Long.MIN_VALUE && lng2 == -1L) || (lng2 == Long.MIN_VALUE && lng == -1L)) {
            throw new ArithmeticException("Multiplication overflows a long: " + lng + " * " + lng2);
        }
        return n;
    }
    
    public static long safeDivide(final long lng, final long lng2) {
        if (lng == Long.MIN_VALUE && lng2 == -1L) {
            throw new ArithmeticException("Multiplication overflows a long: " + lng + " / " + lng2);
        }
        return lng / lng2;
    }
    
    public static int safeToInt(final long lng) {
        if (-2147483648L <= lng && lng <= 2147483647L) {
            return (int)lng;
        }
        throw new ArithmeticException("Value cannot fit in an int: " + lng);
    }
    
    public static int safeMultiplyToInt(final long n, final long n2) {
        return safeToInt(safeMultiply(n, n2));
    }
    
    public static void verifyValueBounds(final DateTimeField dateTimeField, final int i, final int j, final int k) {
        if (i < j || i > k) {
            throw new IllegalFieldValueException(dateTimeField.getType(), i, j, k);
        }
    }
    
    public static void verifyValueBounds(final DateTimeFieldType dateTimeFieldType, final int i, final int j, final int k) {
        if (i < j || i > k) {
            throw new IllegalFieldValueException(dateTimeFieldType, i, j, k);
        }
    }
    
    public static void verifyValueBounds(final String s, final int i, final int j, final int k) {
        if (i < j || i > k) {
            throw new IllegalFieldValueException(s, i, j, k);
        }
    }
    
    public static int getWrappedValue(final int n, final int n2, final int n3, final int n4) {
        return getWrappedValue(n + n2, n3, n4);
    }
    
    public static int getWrappedValue(int n, final int n2, final int n3) {
        if (n2 >= n3) {
            throw new IllegalArgumentException("MIN > MAX");
        }
        final int n4 = n3 - n2 + 1;
        n -= n2;
        if (n >= 0) {
            return n % n4 + n2;
        }
        final int n5 = -n % n4;
        if (n5 == 0) {
            return 0 + n2;
        }
        return n4 - n5 + n2;
    }
    
    public static boolean equals(final Object o, final Object obj) {
        return o == obj || (o != null && obj != null && o.equals(obj));
    }
}
