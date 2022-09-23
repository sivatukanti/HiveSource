// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz;

public class CorruptedInputException extends XZIOException
{
    private static final long serialVersionUID = 3L;
    
    public CorruptedInputException() {
        super("Compressed data is corrupt");
    }
    
    public CorruptedInputException(final String s) {
        super(s);
    }
}
