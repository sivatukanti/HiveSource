// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.native2ascii;

import org.apache.tools.ant.BuildException;
import java.io.File;
import org.apache.tools.ant.taskdefs.optional.Native2Ascii;

public interface Native2AsciiAdapter
{
    boolean convert(final Native2Ascii p0, final File p1, final File p2) throws BuildException;
}
