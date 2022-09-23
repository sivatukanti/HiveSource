// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.permission;

import java.util.regex.Pattern;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
class UmaskParser extends PermissionParser
{
    private static Pattern chmodOctalPattern;
    private static Pattern umaskSymbolicPattern;
    final short umaskMode;
    
    public UmaskParser(final String modeStr) throws IllegalArgumentException {
        super(modeStr, UmaskParser.umaskSymbolicPattern, UmaskParser.chmodOctalPattern);
        this.umaskMode = (short)this.combineModes(0, false);
    }
    
    public short getUMask() {
        if (this.symbolic) {
            return (short)(~this.umaskMode & 0x1FF);
        }
        return this.umaskMode;
    }
    
    static {
        UmaskParser.chmodOctalPattern = Pattern.compile("^\\s*[+]?(0*)([0-7]{3})\\s*$");
        UmaskParser.umaskSymbolicPattern = Pattern.compile("\\G\\s*([ugoa]*)([+=-]+)([rwx]*)([,\\s]*)\\s*");
    }
}
