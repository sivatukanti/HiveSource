// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class UTF8ByteArrayUtils
{
    public static int findByte(final byte[] utf, final int start, final int end, final byte b) {
        for (int i = start; i < end; ++i) {
            if (utf[i] == b) {
                return i;
            }
        }
        return -1;
    }
    
    public static int findBytes(final byte[] utf, final int start, final int end, final byte[] b) {
        for (int matchEnd = end - b.length, i = start; i <= matchEnd; ++i) {
            boolean matched = true;
            for (int j = 0; j < b.length; ++j) {
                if (utf[i + j] != b[j]) {
                    matched = false;
                    break;
                }
            }
            if (matched) {
                return i;
            }
        }
        return -1;
    }
    
    public static int findNthByte(final byte[] utf, final int start, final int length, final byte b, final int n) {
        int pos = -1;
        int nextStart = start;
        for (int i = 0; i < n; ++i) {
            pos = findByte(utf, nextStart, length, b);
            if (pos < 0) {
                return pos;
            }
            nextStart = pos + 1;
        }
        return pos;
    }
    
    public static int findNthByte(final byte[] utf, final byte b, final int n) {
        return findNthByte(utf, 0, utf.length, b, n);
    }
}
