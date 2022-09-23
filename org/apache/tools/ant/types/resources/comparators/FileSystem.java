// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources.comparators;

import java.io.File;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.util.FileUtils;

public class FileSystem extends ResourceComparator
{
    private static final FileUtils FILE_UTILS;
    
    @Override
    protected int resourceCompare(final Resource foo, final Resource bar) {
        final FileProvider fooFP = foo.as(FileProvider.class);
        if (fooFP == null) {
            throw new ClassCastException(foo.getClass() + " doesn't provide files");
        }
        final File foofile = fooFP.getFile();
        final FileProvider barFP = bar.as(FileProvider.class);
        if (barFP == null) {
            throw new ClassCastException(bar.getClass() + " doesn't provide files");
        }
        final File barfile = barFP.getFile();
        return foofile.equals(barfile) ? 0 : (FileSystem.FILE_UTILS.isLeadingPath(foofile, barfile) ? -1 : FileSystem.FILE_UTILS.normalize(foofile.getAbsolutePath()).compareTo(FileSystem.FILE_UTILS.normalize(barfile.getAbsolutePath())));
    }
    
    static {
        FILE_UTILS = FileUtils.getFileUtils();
    }
}
