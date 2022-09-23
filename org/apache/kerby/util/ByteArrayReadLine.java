// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

public class ByteArrayReadLine extends ReadLine
{
    public ByteArrayReadLine(final ByteArrayInputStream in) {
        super(in);
    }
    
    @Override
    public String next() {
        return this.next(1);
    }
    
    @Override
    public String next(final int lines) {
        try {
            return super.next(lines);
        }
        catch (IOException ioe) {
            throw new RuntimeException("impossible", ioe);
        }
    }
    
    @Override
    public byte[] nextAsBytes() {
        return this.nextAsBytes(1);
    }
    
    @Override
    public byte[] nextAsBytes(final int lines) {
        try {
            return super.nextAsBytes(lines);
        }
        catch (IOException ioe) {
            throw new RuntimeException("impossible", ioe);
        }
    }
}
