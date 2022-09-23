// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.extension;

import org.apache.tools.ant.BuildException;
import java.io.File;
import org.apache.tools.ant.Project;

public interface ExtensionResolver
{
    File resolve(final Extension p0, final Project p1) throws BuildException;
}
