// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.permission;

import java.util.regex.Pattern;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
class RawParser extends PermissionParser
{
    private static Pattern rawOctalPattern;
    private static Pattern rawNormalPattern;
    private short permission;
    
    public RawParser(final String modeStr) throws IllegalArgumentException {
        super(modeStr, RawParser.rawNormalPattern, RawParser.rawOctalPattern);
        this.permission = (short)this.combineModes(0, false);
    }
    
    public short getPermission() {
        return this.permission;
    }
    
    static {
        RawParser.rawOctalPattern = Pattern.compile("^\\s*([01]?)([0-7]{3})\\s*$");
        RawParser.rawNormalPattern = Pattern.compile("\\G\\s*([ugoa]*)([+=-]+)([rwxt]*)([,\\s]*)\\s*");
    }
}
