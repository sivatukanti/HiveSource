// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.IOException;
import java.io.DataInputStream;

public class IntegerCPInfo extends ConstantCPInfo
{
    public IntegerCPInfo() {
        super(3, 1);
    }
    
    @Override
    public void read(final DataInputStream cpStream) throws IOException {
        this.setValue(new Integer(cpStream.readInt()));
    }
    
    @Override
    public String toString() {
        return "Integer Constant Pool Entry: " + this.getValue();
    }
}
