// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.IOException;
import java.io.DataInputStream;

public class Utf8CPInfo extends ConstantPoolEntry
{
    private String value;
    
    public Utf8CPInfo() {
        super(1, 1);
    }
    
    @Override
    public void read(final DataInputStream cpStream) throws IOException {
        this.value = cpStream.readUTF();
    }
    
    @Override
    public String toString() {
        return "UTF8 Value = " + this.value;
    }
    
    public String getValue() {
        return this.value;
    }
}
