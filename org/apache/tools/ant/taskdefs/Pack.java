// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.Resource;
import java.io.File;
import org.apache.tools.ant.Task;

public abstract class Pack extends Task
{
    private static final int BUFFER_SIZE = 8192;
    protected File zipFile;
    protected File source;
    private Resource src;
    
    public void setZipfile(final File zipFile) {
        this.zipFile = zipFile;
    }
    
    public void setDestfile(final File zipFile) {
        this.setZipfile(zipFile);
    }
    
    public void setSrc(final File src) {
        this.setSrcResource(new FileResource(src));
    }
    
    public void setSrcResource(final Resource src) {
        if (src.isDirectory()) {
            throw new BuildException("the source can't be a directory");
        }
        final FileProvider fp = src.as(FileProvider.class);
        if (fp != null) {
            this.source = fp.getFile();
        }
        else if (!this.supportsNonFileResources()) {
            throw new BuildException("Only FileSystem resources are supported.");
        }
        this.src = src;
    }
    
    public void addConfigured(final ResourceCollection a) {
        if (a.size() == 0) {
            throw new BuildException("No resource selected, " + this.getTaskName() + " needs exactly one resource.");
        }
        if (a.size() != 1) {
            throw new BuildException(this.getTaskName() + " cannot handle multiple resources at once. (" + a.size() + " resources were selected.)");
        }
        this.setSrcResource(a.iterator().next());
    }
    
    private void validate() throws BuildException {
        if (this.zipFile == null) {
            throw new BuildException("zipfile attribute is required", this.getLocation());
        }
        if (this.zipFile.isDirectory()) {
            throw new BuildException("zipfile attribute must not represent a directory!", this.getLocation());
        }
        if (this.getSrcResource() == null) {
            throw new BuildException("src attribute or nested resource is required", this.getLocation());
        }
    }
    
    @Override
    public void execute() throws BuildException {
        this.validate();
        final Resource s = this.getSrcResource();
        if (!s.isExists()) {
            this.log("Nothing to do: " + s.toString() + " doesn't exist.");
        }
        else if (this.zipFile.lastModified() < s.getLastModified()) {
            this.log("Building: " + this.zipFile.getAbsolutePath());
            this.pack();
        }
        else {
            this.log("Nothing to do: " + this.zipFile.getAbsolutePath() + " is up to date.");
        }
    }
    
    private void zipFile(final InputStream in, final OutputStream zOut) throws IOException {
        final byte[] buffer = new byte[8192];
        int count = 0;
        do {
            zOut.write(buffer, 0, count);
            count = in.read(buffer, 0, buffer.length);
        } while (count != -1);
    }
    
    protected void zipFile(final File file, final OutputStream zOut) throws IOException {
        this.zipResource(new FileResource(file), zOut);
    }
    
    protected void zipResource(final Resource resource, final OutputStream zOut) throws IOException {
        final InputStream rIn = resource.getInputStream();
        try {
            this.zipFile(rIn, zOut);
        }
        finally {
            rIn.close();
        }
    }
    
    protected abstract void pack();
    
    public Resource getSrcResource() {
        return this.src;
    }
    
    protected boolean supportsNonFileResources() {
        return false;
    }
}
