// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.jlink;

import java.io.IOException;
import java.io.DataInput;

class ConstantPool
{
    static final byte UTF8 = 1;
    static final byte UNUSED = 2;
    static final byte INTEGER = 3;
    static final byte FLOAT = 4;
    static final byte LONG = 5;
    static final byte DOUBLE = 6;
    static final byte CLASS = 7;
    static final byte STRING = 8;
    static final byte FIELDREF = 9;
    static final byte METHODREF = 10;
    static final byte INTERFACEMETHODREF = 11;
    static final byte NAMEANDTYPE = 12;
    byte[] types;
    Object[] values;
    
    ConstantPool(final DataInput data) throws IOException {
        final int count = data.readUnsignedShort();
        this.types = new byte[count];
        this.values = new Object[count];
        for (int i = 1; i < count; ++i) {
            final byte type = data.readByte();
            switch (this.types[i] = type) {
                case 1: {
                    this.values[i] = data.readUTF();
                }
                case 3: {
                    this.values[i] = new Integer(data.readInt());
                    break;
                }
                case 4: {
                    this.values[i] = new Float(data.readFloat());
                    break;
                }
                case 5: {
                    this.values[i] = new Long(data.readLong());
                    ++i;
                    break;
                }
                case 6: {
                    this.values[i] = new Double(data.readDouble());
                    ++i;
                    break;
                }
                case 7:
                case 8: {
                    this.values[i] = new Integer(data.readUnsignedShort());
                    break;
                }
                case 9:
                case 10:
                case 11:
                case 12: {
                    this.values[i] = new Integer(data.readInt());
                    break;
                }
            }
        }
    }
}
