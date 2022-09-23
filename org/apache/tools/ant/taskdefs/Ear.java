// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import org.apache.tools.zip.ZipOutputStream;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.ZipFileSet;
import org.apache.tools.ant.BuildException;
import java.io.File;
import org.apache.tools.ant.util.FileUtils;

public class Ear extends Jar
{
    private static final FileUtils FILE_UTILS;
    private File deploymentDescriptor;
    private boolean descriptorAdded;
    private static final String XML_DESCRIPTOR_PATH = "META-INF/application.xml";
    
    public Ear() {
        this.archiveType = "ear";
        this.emptyBehavior = "create";
    }
    
    @Deprecated
    public void setEarfile(final File earFile) {
        this.setDestFile(earFile);
    }
    
    public void setAppxml(final File descr) {
        this.deploymentDescriptor = descr;
        if (!this.deploymentDescriptor.exists()) {
            throw new BuildException("Deployment descriptor: " + this.deploymentDescriptor + " does not exist.");
        }
        final ZipFileSet fs = new ZipFileSet();
        fs.setFile(this.deploymentDescriptor);
        fs.setFullpath("META-INF/application.xml");
        super.addFileset(fs);
    }
    
    public void addArchives(final ZipFileSet fs) {
        fs.setPrefix("/");
        super.addFileset(fs);
    }
    
    @Override
    protected void initZipOutputStream(final ZipOutputStream zOut) throws IOException, BuildException {
        if (this.deploymentDescriptor == null && !this.isInUpdateMode()) {
            throw new BuildException("appxml attribute is required", this.getLocation());
        }
        super.initZipOutputStream(zOut);
    }
    
    @Override
    protected void zipFile(final File file, final ZipOutputStream zOut, final String vPath, final int mode) throws IOException {
        if ("META-INF/application.xml".equalsIgnoreCase(vPath)) {
            if (this.deploymentDescriptor == null || !Ear.FILE_UTILS.fileNameEquals(this.deploymentDescriptor, file) || this.descriptorAdded) {
                this.logWhenWriting("Warning: selected " + this.archiveType + " files include a " + "META-INF/application.xml" + " which will" + " be ignored (please use appxml attribute to " + this.archiveType + " task)", 1);
            }
            else {
                super.zipFile(file, zOut, vPath, mode);
                this.descriptorAdded = true;
            }
        }
        else {
            super.zipFile(file, zOut, vPath, mode);
        }
    }
    
    @Override
    protected void cleanUp() {
        this.descriptorAdded = false;
        super.cleanUp();
    }
    
    static {
        FILE_UTILS = FileUtils.getFileUtils();
    }
}
