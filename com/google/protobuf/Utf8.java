// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf;

final class Utf8
{
    public static final int COMPLETE = 0;
    public static final int MALFORMED = -1;
    
    private Utf8() {
    }
    
    public static boolean isValidUtf8(final byte[] bytes) {
        return isValidUtf8(bytes, 0, bytes.length);
    }
    
    public static boolean isValidUtf8(final byte[] bytes, final int index, final int limit) {
        return partialIsValidUtf8(bytes, index, limit) == 0;
    }
    
    public static int partialIsValidUtf8(final int state, final byte[] bytes, int index, final int limit) {
        if (state != 0) {
            if (index >= limit) {
                return state;
            }
            final int byte1 = (byte)state;
            if (byte1 < -32) {
                if (byte1 < -62 || bytes[index++] > -65) {
                    return -1;
                }
            }
            else if (byte1 < -16) {
                int byte2 = (byte)~(state >> 8);
                if (byte2 == 0) {
                    byte2 = bytes[index++];
                    if (index >= limit) {
                        return incompleteStateFor(byte1, byte2);
                    }
                }
                if (byte2 > -65 || (byte1 == -32 && byte2 < -96) || (byte1 == -19 && byte2 >= -96) || bytes[index++] > -65) {
                    return -1;
                }
            }
            else {
                int byte2 = (byte)~(state >> 8);
                int byte3 = 0;
                if (byte2 == 0) {
                    byte2 = bytes[index++];
                    if (index >= limit) {
                        return incompleteStateFor(byte1, byte2);
                    }
                }
                else {
                    byte3 = (byte)(state >> 16);
                }
                if (byte3 == 0) {
                    byte3 = bytes[index++];
                    if (index >= limit) {
                        return incompleteStateFor(byte1, byte2, byte3);
                    }
                }
                if (byte2 > -65 || (byte1 << 28) + (byte2 + 112) >> 30 != 0 || byte3 > -65 || bytes[index++] > -65) {
                    return -1;
                }
            }
        }
        return partialIsValidUtf8(bytes, index, limit);
    }
    
    public static int partialIsValidUtf8(final byte[] bytes, int index, final int limit) {
        while (index < limit && bytes[index] >= 0) {
            ++index;
        }
        return (index >= limit) ? 0 : partialIsValidUtf8NonAscii(bytes, index, limit);
    }
    
    private static int partialIsValidUtf8NonAscii(final byte[] bytes, int index, final int limit) {
        while (index < limit) {
            final int byte1;
            if ((byte1 = bytes[index++]) < 0) {
                if (byte1 < -32) {
                    if (index >= limit) {
                        return byte1;
                    }
                    if (byte1 < -62 || bytes[index++] > -65) {
                        return -1;
                    }
                    continue;
                }
                else if (byte1 < -16) {
                    if (index >= limit - 1) {
                        return incompleteStateFor(bytes, index, limit);
                    }
                    final int byte2;
                    if ((byte2 = bytes[index++]) > -65 || (byte1 == -32 && byte2 < -96) || (byte1 == -19 && byte2 >= -96) || bytes[index++] > -65) {
                        return -1;
                    }
                    continue;
                }
                else {
                    if (index >= limit - 2) {
                        return incompleteStateFor(bytes, index, limit);
                    }
                    final int byte2;
                    if ((byte2 = bytes[index++]) > -65 || (byte1 << 28) + (byte2 + 112) >> 30 != 0 || bytes[index++] > -65 || bytes[index++] > -65) {
                        return -1;
                    }
                    continue;
                }
            }
        }
        return 0;
    }
    
    private static int incompleteStateFor(final int byte1) {
        return (byte1 > -12) ? -1 : byte1;
    }
    
    private static int incompleteStateFor(final int byte1, final int byte2) {
        return (byte1 > -12 || byte2 > -65) ? -1 : (byte1 ^ byte2 << 8);
    }
    
    private static int incompleteStateFor(final int byte1, final int byte2, final int byte3) {
        return (byte1 > -12 || byte2 > -65 || byte3 > -65) ? -1 : (byte1 ^ byte2 << 8 ^ byte3 << 16);
    }
    
    private static int incompleteStateFor(final byte[] bytes, final int index, final int limit) {
        final int byte1 = bytes[index - 1];
        switch (limit - index) {
            case 0: {
                return incompleteStateFor(byte1);
            }
            case 1: {
                return incompleteStateFor(byte1, bytes[index]);
            }
            case 2: {
                return incompleteStateFor(byte1, bytes[index], bytes[index + 1]);
            }
            default: {
                throw new AssertionError();
            }
        }
    }
}
