// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.IOException;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class ParentNotDirectoryException extends IOException
{
    private static final long serialVersionUID = 1L;
    
    public ParentNotDirectoryException() {
    }
    
    public ParentNotDirectoryException(final String msg) {
        super(msg);
    }
}
