// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.Resource;
import java.io.File;
import org.apache.tools.ant.Task;

public abstract class Unpack extends Task
{
    protected File source;
    protected File dest;
    protected Resource srcResource;
    
    @Deprecated
    public void setSrc(final String src) {
        this.log("DEPRECATED - The setSrc(String) method has been deprecated. Use setSrc(File) instead.");
        this.setSrc(this.getProject().resolveFile(src));
    }
    
    @Deprecated
    public void setDest(final String dest) {
        this.log("DEPRECATED - The setDest(String) method has been deprecated. Use setDest(File) instead.");
        this.setDest(this.getProject().resolveFile(dest));
    }
    
    public void setSrc(final File src) {
        this.setSrcResource(new FileResource(src));
    }
    
    public void setSrcResource(final Resource src) {
        if (!src.isExists()) {
            throw new BuildException("the archive " + src.getName() + " doesn't exist");
        }
        if (src.isDirectory()) {
            throw new BuildException("the archive " + src.getName() + " can't be a directory");
        }
        final FileProvider fp = src.as(FileProvider.class);
        if (fp != null) {
            this.source = fp.getFile();
        }
        else if (!this.supportsNonFileResources()) {
            throw new BuildException("The source " + src.getName() + " is not a FileSystem " + "Only FileSystem resources are" + " supported.");
        }
        this.srcResource = src;
    }
    
    public void addConfigured(final ResourceCollection a) {
        if (a.size() != 1) {
            throw new BuildException("only single argument resource collections are supported as archives");
        }
        this.setSrcResource(a.iterator().next());
    }
    
    public void setDest(final File dest) {
        this.dest = dest;
    }
    
    private void validate() throws BuildException {
        if (this.srcResource == null) {
            throw new BuildException("No Src specified", this.getLocation());
        }
        if (this.dest == null) {
            this.dest = new File(this.source.getParent());
        }
        if (this.dest.isDirectory()) {
            final String defaultExtension = this.getDefaultExtension();
            this.createDestFile(defaultExtension);
        }
    }
    
    private void createDestFile(final String defaultExtension) {
        final String sourceName = this.source.getName();
        final int len = sourceName.length();
        if (defaultExtension != null && len > defaultExtension.length() && defaultExtension.equalsIgnoreCase(sourceName.substring(len - defaultExtension.length()))) {
            this.dest = new File(this.dest, sourceName.substring(0, len - defaultExtension.length()));
        }
        else {
            this.dest = new File(this.dest, sourceName);
        }
    }
    
    @Override
    public void execute() throws BuildException {
        final File savedDest = this.dest;
        try {
            this.validate();
            this.extract();
        }
        finally {
            this.dest = savedDest;
        }
    }
    
    protected abstract String getDefaultExtension();
    
    protected abstract void extract();
    
    protected boolean supportsNonFileResources() {
        return false;
    }
}
