// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.io.File;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public interface DiskValidator
{
    void checkStatus(final File p0) throws DiskChecker.DiskErrorException;
}
