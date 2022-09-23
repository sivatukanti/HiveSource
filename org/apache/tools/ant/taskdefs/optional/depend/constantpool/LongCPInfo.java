// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.IOException;
import java.io.DataInputStream;

public class LongCPInfo extends ConstantCPInfo
{
    public LongCPInfo() {
        super(5, 2);
    }
    
    @Override
    public void read(final DataInputStream cpStream) throws IOException {
        this.setValue(new Long(cpStream.readLong()));
    }
    
    @Override
    public String toString() {
        return "Long Constant Pool Entry: " + this.getValue();
    }
}
