// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.jlink;

import java.io.IOException;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.InputStream;

public class ClassNameReader
{
    private static final int CLASS_MAGIC_NUMBER = -889275714;
    
    public static String getClassName(final InputStream input) throws IOException {
        final DataInputStream data = new DataInputStream(input);
        final int cookie = data.readInt();
        if (cookie != -889275714) {
            return null;
        }
        data.readInt();
        final ConstantPool constants = new ConstantPool(data);
        final Object[] values = constants.values;
        data.readUnsignedShort();
        final int classIndex = data.readUnsignedShort();
        final Integer stringIndex = (Integer)values[classIndex];
        final String className = (String)values[stringIndex];
        return className;
    }
}
