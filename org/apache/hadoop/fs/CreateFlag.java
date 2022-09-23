// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.io.IOException;
import java.io.FileNotFoundException;
import org.apache.hadoop.HadoopIllegalArgumentException;
import java.util.EnumSet;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public enum CreateFlag
{
    CREATE((short)1), 
    OVERWRITE((short)2), 
    APPEND((short)4), 
    SYNC_BLOCK((short)8), 
    LAZY_PERSIST((short)16), 
    NEW_BLOCK((short)32), 
    @InterfaceAudience.LimitedPrivate({ "HBase" })
    NO_LOCAL_WRITE((short)64), 
    SHOULD_REPLICATE((short)128), 
    IGNORE_CLIENT_LOCALITY((short)256);
    
    private final short mode;
    
    private CreateFlag(final short mode) {
        this.mode = mode;
    }
    
    short getMode() {
        return this.mode;
    }
    
    public static void validate(final EnumSet<CreateFlag> flag) {
        if (flag == null || flag.isEmpty()) {
            throw new HadoopIllegalArgumentException(flag + " does not specify any options");
        }
        final boolean append = flag.contains(CreateFlag.APPEND);
        final boolean overwrite = flag.contains(CreateFlag.OVERWRITE);
        if (append && overwrite) {
            throw new HadoopIllegalArgumentException(flag + "Both append and overwrite options cannot be enabled.");
        }
    }
    
    public static void validate(final Object path, final boolean pathExists, final EnumSet<CreateFlag> flag) throws IOException {
        validate(flag);
        final boolean append = flag.contains(CreateFlag.APPEND);
        final boolean overwrite = flag.contains(CreateFlag.OVERWRITE);
        if (pathExists) {
            if (!append && !overwrite) {
                throw new FileAlreadyExistsException("File already exists: " + path.toString() + ". Append or overwrite option must be specified in " + flag);
            }
        }
        else if (!flag.contains(CreateFlag.CREATE)) {
            throw new FileNotFoundException("Non existing file: " + path.toString() + ". Create option is not specified in " + flag);
        }
    }
    
    public static void validateForAppend(final EnumSet<CreateFlag> flag) {
        validate(flag);
        if (!flag.contains(CreateFlag.APPEND)) {
            throw new HadoopIllegalArgumentException(flag + " does not contain APPEND");
        }
    }
}
