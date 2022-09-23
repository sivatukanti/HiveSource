// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.selectors;

import org.apache.tools.ant.BuildException;
import java.io.File;

public interface FileSelector
{
    boolean isSelected(final File p0, final String p1, final File p2) throws BuildException;
}
