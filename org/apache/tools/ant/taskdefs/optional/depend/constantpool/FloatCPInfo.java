// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.IOException;
import java.io.DataInputStream;

public class FloatCPInfo extends ConstantCPInfo
{
    public FloatCPInfo() {
        super(4, 1);
    }
    
    @Override
    public void read(final DataInputStream cpStream) throws IOException {
        this.setValue(new Float(cpStream.readFloat()));
    }
    
    @Override
    public String toString() {
        return "Float Constant Pool Entry: " + this.getValue();
    }
}
