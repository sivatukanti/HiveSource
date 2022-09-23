// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.selectors;

import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.Resource;
import java.io.File;
import org.apache.tools.ant.types.resources.selectors.ResourceSelector;

public class WritableSelector implements FileSelector, ResourceSelector
{
    public boolean isSelected(final File basedir, final String filename, final File file) {
        return file != null && file.canWrite();
    }
    
    public boolean isSelected(final Resource r) {
        final FileProvider fp = r.as(FileProvider.class);
        return fp != null && this.isSelected(null, null, fp.getFile());
    }
}
