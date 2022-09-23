// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.io;

import java.io.IOException;
import java.io.DataInput;

public final class DataInputUtil
{
    public static void skipFully(final DataInput dataInput, int i) throws IOException {
        if (dataInput == null) {
            throw new NullPointerException();
        }
        while (i > 0) {
            int skipBytes = dataInput.skipBytes(i);
            if (skipBytes == 0) {
                dataInput.readByte();
                ++skipBytes;
            }
            i -= skipBytes;
        }
    }
}
