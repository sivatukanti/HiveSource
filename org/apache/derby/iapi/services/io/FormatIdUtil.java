// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.io;

import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;

public final class FormatIdUtil
{
    private FormatIdUtil() {
    }
    
    public static int getFormatIdByteLength(final int n) {
        return 2;
    }
    
    public static void writeFormatIdInteger(final DataOutput dataOutput, final int n) throws IOException {
        dataOutput.writeShort(n);
    }
    
    public static int readFormatIdInteger(final DataInput dataInput) throws IOException {
        return dataInput.readUnsignedShort();
    }
    
    public static int readFormatIdInteger(final byte[] array) {
        return (array[0] & 0xFF) << 8 | (array[1] & 0xFF);
    }
    
    public static String formatIdToString(final int i) {
        return Integer.toString(i);
    }
}
