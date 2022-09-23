// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.permission;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public enum FsAction
{
    NONE("---"), 
    EXECUTE("--x"), 
    WRITE("-w-"), 
    WRITE_EXECUTE("-wx"), 
    READ("r--"), 
    READ_EXECUTE("r-x"), 
    READ_WRITE("rw-"), 
    ALL("rwx");
    
    private static final FsAction[] vals;
    public final String SYMBOL;
    
    private FsAction(final String s) {
        this.SYMBOL = s;
    }
    
    public boolean implies(final FsAction that) {
        return that != null && (this.ordinal() & that.ordinal()) == that.ordinal();
    }
    
    public FsAction and(final FsAction that) {
        return FsAction.vals[this.ordinal() & that.ordinal()];
    }
    
    public FsAction or(final FsAction that) {
        return FsAction.vals[this.ordinal() | that.ordinal()];
    }
    
    public FsAction not() {
        return FsAction.vals[7 - this.ordinal()];
    }
    
    public static FsAction getFsAction(final String permission) {
        for (final FsAction fsAction : FsAction.vals) {
            if (fsAction.SYMBOL.equals(permission)) {
                return fsAction;
            }
        }
        return null;
    }
    
    static {
        vals = values();
    }
}
