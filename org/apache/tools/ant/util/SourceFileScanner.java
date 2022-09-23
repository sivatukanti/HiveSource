// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileResource;
import java.util.Vector;
import java.io.File;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.ResourceFactory;

public class SourceFileScanner implements ResourceFactory
{
    protected Task task;
    private static final FileUtils FILE_UTILS;
    private File destDir;
    
    public SourceFileScanner(final Task task) {
        this.task = task;
    }
    
    public String[] restrict(final String[] files, final File srcDir, final File destDir, final FileNameMapper mapper) {
        return this.restrict(files, srcDir, destDir, mapper, SourceFileScanner.FILE_UTILS.getFileTimestampGranularity());
    }
    
    public String[] restrict(final String[] files, final File srcDir, final File destDir, final FileNameMapper mapper, final long granularity) {
        this.destDir = destDir;
        final Vector v = new Vector();
        for (int i = 0; i < files.length; ++i) {
            final String name = files[i];
            v.addElement(new FileResource(srcDir, name) {
                @Override
                public String getName() {
                    return name;
                }
            });
        }
        final Resource[] sourceresources = new Resource[v.size()];
        v.copyInto(sourceresources);
        final Resource[] outofdate = ResourceUtils.selectOutOfDateSources(this.task, sourceresources, mapper, this, granularity);
        final String[] result = new String[outofdate.length];
        for (int counter = 0; counter < outofdate.length; ++counter) {
            result[counter] = outofdate[counter].getName();
        }
        return result;
    }
    
    public File[] restrictAsFiles(final String[] files, final File srcDir, final File destDir, final FileNameMapper mapper) {
        return this.restrictAsFiles(files, srcDir, destDir, mapper, SourceFileScanner.FILE_UTILS.getFileTimestampGranularity());
    }
    
    public File[] restrictAsFiles(final String[] files, final File srcDir, final File destDir, final FileNameMapper mapper, final long granularity) {
        final String[] res = this.restrict(files, srcDir, destDir, mapper, granularity);
        final File[] result = new File[res.length];
        for (int i = 0; i < res.length; ++i) {
            result[i] = new File(srcDir, res[i]);
        }
        return result;
    }
    
    public Resource getResource(final String name) {
        return new FileResource(this.destDir, name);
    }
    
    static {
        FILE_UTILS = FileUtils.getFileUtils();
    }
}
