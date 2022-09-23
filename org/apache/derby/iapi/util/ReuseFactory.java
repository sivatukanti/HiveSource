// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.util;

public class ReuseFactory
{
    private static final Integer[] staticInts;
    private static final Integer FIFTY_TWO;
    private static final Integer TWENTY_THREE;
    private static final Integer MAXINT;
    private static final Integer MINUS_ONE;
    private static final Short[] staticShorts;
    private static final Byte[] staticBytes;
    private static final Long[] staticLongs;
    private static final byte[] staticZeroLenByteArray;
    
    private ReuseFactory() {
    }
    
    public static Integer getInteger(final int value) {
        if (value >= 0 && value < ReuseFactory.staticInts.length) {
            return ReuseFactory.staticInts[value];
        }
        switch (value) {
            case 23: {
                return ReuseFactory.TWENTY_THREE;
            }
            case 52: {
                return ReuseFactory.FIFTY_TWO;
            }
            case Integer.MAX_VALUE: {
                return ReuseFactory.MAXINT;
            }
            case -1: {
                return ReuseFactory.MINUS_ONE;
            }
            default: {
                return new Integer(value);
            }
        }
    }
    
    public static Short getShort(final short value) {
        if (value >= 0 && value < ReuseFactory.staticShorts.length) {
            return ReuseFactory.staticShorts[value];
        }
        return new Short(value);
    }
    
    public static Byte getByte(final byte value) {
        if (value >= 0 && value < ReuseFactory.staticBytes.length) {
            return ReuseFactory.staticBytes[value];
        }
        return new Byte(value);
    }
    
    public static Long getLong(final long value) {
        if (value >= 0L && value < ReuseFactory.staticLongs.length) {
            return ReuseFactory.staticLongs[(int)value];
        }
        return new Long(value);
    }
    
    public static Boolean getBoolean(final boolean b) {
        return b ? Boolean.TRUE : Boolean.FALSE;
    }
    
    public static byte[] getZeroLenByteArray() {
        return ReuseFactory.staticZeroLenByteArray;
    }
    
    static {
        staticInts = new Integer[] { new Integer(0), new Integer(1), new Integer(2), new Integer(3), new Integer(4), new Integer(5), new Integer(6), new Integer(7), new Integer(8), new Integer(9), new Integer(10), new Integer(11), new Integer(12), new Integer(13), new Integer(14), new Integer(15), new Integer(16), new Integer(17), new Integer(18) };
        FIFTY_TWO = new Integer(52);
        TWENTY_THREE = new Integer(23);
        MAXINT = new Integer(Integer.MAX_VALUE);
        MINUS_ONE = new Integer(-1);
        staticShorts = new Short[] { new Short((short)0), new Short((short)1), new Short((short)2), new Short((short)3), new Short((short)4), new Short((short)5), new Short((short)6), new Short((short)7), new Short((short)8), new Short((short)9), new Short((short)10) };
        staticBytes = new Byte[] { new Byte((byte)0), new Byte((byte)1), new Byte((byte)2), new Byte((byte)3), new Byte((byte)4), new Byte((byte)5), new Byte((byte)6), new Byte((byte)7), new Byte((byte)8), new Byte((byte)9), new Byte((byte)10) };
        staticLongs = new Long[] { new Long(0L), new Long(1L), new Long(2L), new Long(3L), new Long(4L), new Long(5L), new Long(6L), new Long(7L), new Long(8L), new Long(9L), new Long(10L) };
        staticZeroLenByteArray = new byte[0];
    }
}
