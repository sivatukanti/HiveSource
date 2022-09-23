// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.extension.resolvers;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Get;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.optional.extension.Extension;
import java.net.URL;
import java.io.File;
import org.apache.tools.ant.taskdefs.optional.extension.ExtensionResolver;

public class URLResolver implements ExtensionResolver
{
    private File destfile;
    private File destdir;
    private URL url;
    
    public void setUrl(final URL url) {
        this.url = url;
    }
    
    public void setDestfile(final File destfile) {
        this.destfile = destfile;
    }
    
    public void setDestdir(final File destdir) {
        this.destdir = destdir;
    }
    
    public File resolve(final Extension extension, final Project project) throws BuildException {
        this.validate();
        final File file = this.getDest();
        final Get get = new Get();
        get.setProject(project);
        get.setDest(file);
        get.setSrc(this.url);
        get.execute();
        return file;
    }
    
    private File getDest() {
        File result;
        if (null != this.destfile) {
            result = this.destfile;
        }
        else {
            final String file = this.url.getFile();
            String filename;
            if (null == file || file.length() <= 1) {
                filename = "default.file";
            }
            else {
                int index = file.lastIndexOf(47);
                if (-1 == index) {
                    index = 0;
                }
                filename = file.substring(index);
            }
            result = new File(this.destdir, filename);
        }
        return result;
    }
    
    private void validate() {
        if (null == this.url) {
            final String message = "Must specify URL";
            throw new BuildException("Must specify URL");
        }
        if (null == this.destdir && null == this.destfile) {
            final String message = "Must specify destination file or directory";
            throw new BuildException("Must specify destination file or directory");
        }
        if (null != this.destdir && null != this.destfile) {
            final String message = "Must not specify both destination file or directory";
            throw new BuildException("Must not specify both destination file or directory");
        }
    }
    
    @Override
    public String toString() {
        return "URL[" + this.url + "]";
    }
}
