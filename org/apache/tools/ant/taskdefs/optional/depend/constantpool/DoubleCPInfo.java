// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.IOException;
import java.io.DataInputStream;

public class DoubleCPInfo extends ConstantCPInfo
{
    public DoubleCPInfo() {
        super(6, 2);
    }
    
    @Override
    public void read(final DataInputStream cpStream) throws IOException {
        this.setValue(new Double(cpStream.readDouble()));
    }
    
    @Override
    public String toString() {
        return "Double Constant Pool Entry: " + this.getValue();
    }
}
