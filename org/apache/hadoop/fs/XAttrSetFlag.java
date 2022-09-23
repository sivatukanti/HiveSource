// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.io.IOException;
import org.apache.hadoop.HadoopIllegalArgumentException;
import java.util.EnumSet;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public enum XAttrSetFlag
{
    CREATE((short)1), 
    REPLACE((short)2);
    
    private final short flag;
    
    private XAttrSetFlag(final short flag) {
        this.flag = flag;
    }
    
    short getFlag() {
        return this.flag;
    }
    
    public static void validate(final String xAttrName, final boolean xAttrExists, final EnumSet<XAttrSetFlag> flag) throws IOException {
        if (flag == null || flag.isEmpty()) {
            throw new HadoopIllegalArgumentException("A flag must be specified.");
        }
        if (xAttrExists) {
            if (!flag.contains(XAttrSetFlag.REPLACE)) {
                throw new IOException("XAttr: " + xAttrName + " already exists. The REPLACE flag must be specified.");
            }
        }
        else if (!flag.contains(XAttrSetFlag.CREATE)) {
            throw new IOException("XAttr: " + xAttrName + " does not exist. The CREATE flag must be specified.");
        }
    }
}
