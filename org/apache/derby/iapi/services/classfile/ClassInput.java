// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.classfile;

import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;

class ClassInput extends DataInputStream
{
    ClassInput(final InputStream in) {
        super(in);
    }
    
    int getU2() throws IOException {
        return this.readUnsignedShort();
    }
    
    int getU4() throws IOException {
        return this.readInt();
    }
    
    byte[] getU1Array(final int n) throws IOException {
        final byte[] b = new byte[n];
        this.readFully(b);
        return b;
    }
}
