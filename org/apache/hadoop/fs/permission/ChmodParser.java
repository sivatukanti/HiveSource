// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.permission;

import org.apache.hadoop.fs.FileStatus;
import java.util.regex.Pattern;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class ChmodParser extends PermissionParser
{
    private static Pattern chmodOctalPattern;
    private static Pattern chmodNormalPattern;
    
    public ChmodParser(final String modeStr) throws IllegalArgumentException {
        super(modeStr, ChmodParser.chmodNormalPattern, ChmodParser.chmodOctalPattern);
    }
    
    public short applyNewPermission(final FileStatus file) {
        final FsPermission perms = file.getPermission();
        final int existing = perms.toShort();
        final boolean exeOk = file.isDirectory() || (existing & 0x49) != 0x0;
        return (short)this.combineModes(existing, exeOk);
    }
    
    static {
        ChmodParser.chmodOctalPattern = Pattern.compile("^\\s*[+]?([01]?)([0-7]{3})\\s*$");
        ChmodParser.chmodNormalPattern = Pattern.compile("\\G\\s*([ugoa]*)([+=-]+)([rwxXt]+)([,\\s]*)\\s*");
    }
}
