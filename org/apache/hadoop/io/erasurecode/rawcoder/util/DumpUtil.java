// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.rawcoder.util;

import org.apache.hadoop.io.erasurecode.ECChunk;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public final class DumpUtil
{
    private static final String HEX_CHARS_STR = "0123456789ABCDEF";
    private static final char[] HEX_CHARS;
    
    private DumpUtil() {
    }
    
    public static String bytesToHex(final byte[] bytes, int limit) {
        if (limit <= 0 || limit > bytes.length) {
            limit = bytes.length;
        }
        int len = limit * 2;
        len += limit;
        len += 2;
        final char[] hexChars = new char[len];
        hexChars[0] = '0';
        hexChars[1] = 'x';
        for (int j = 0; j < limit; ++j) {
            final int v = bytes[j] & 0xFF;
            hexChars[j * 3 + 2] = DumpUtil.HEX_CHARS[v >>> 4];
            hexChars[j * 3 + 3] = DumpUtil.HEX_CHARS[v & 0xF];
            hexChars[j * 3 + 4] = ' ';
        }
        return new String(hexChars);
    }
    
    public static void dumpMatrix(final byte[] matrix, final int numDataUnits, final int numAllUnits) {
        for (int i = 0; i < numDataUnits; ++i) {
            for (int j = 0; j < numAllUnits; ++j) {
                System.out.print(" ");
                System.out.print(0xFF & matrix[i * numAllUnits + j]);
            }
            System.out.println();
        }
    }
    
    public static void dumpChunks(final String header, final ECChunk[] chunks) {
        System.out.println();
        System.out.println(header);
        for (int i = 0; i < chunks.length; ++i) {
            dumpChunk(chunks[i]);
        }
        System.out.println();
    }
    
    public static void dumpChunk(final ECChunk chunk) {
        String str;
        if (chunk == null) {
            str = "<EMPTY>";
        }
        else {
            final byte[] bytes = chunk.toBytesArray();
            str = bytesToHex(bytes, 16);
        }
        System.out.println(str);
    }
    
    static {
        HEX_CHARS = "0123456789ABCDEF".toCharArray();
    }
}
