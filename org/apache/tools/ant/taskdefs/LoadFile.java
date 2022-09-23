// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileResource;
import java.io.File;

public class LoadFile extends LoadResource
{
    public final void setSrcFile(final File srcFile) {
        this.addConfigured(new FileResource(srcFile));
    }
}
