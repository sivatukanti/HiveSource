// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.file.tfile;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.IOException;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class MetaBlockAlreadyExists extends IOException
{
    MetaBlockAlreadyExists(final String s) {
        super(s);
    }
}
