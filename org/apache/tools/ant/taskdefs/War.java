// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import org.apache.tools.zip.ZipOutputStream;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.ZipFileSet;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.FileUtils;
import java.io.File;

public class War extends Jar
{
    private File deploymentDescriptor;
    private boolean needxmlfile;
    private File addedWebXmlFile;
    private static final FileUtils FILE_UTILS;
    private static final String XML_DESCRIPTOR_PATH = "WEB-INF/web.xml";
    
    public War() {
        this.needxmlfile = true;
        this.archiveType = "war";
        this.emptyBehavior = "create";
    }
    
    @Deprecated
    public void setWarfile(final File warFile) {
        this.setDestFile(warFile);
    }
    
    public void setWebxml(final File descr) {
        this.deploymentDescriptor = descr;
        if (!this.deploymentDescriptor.exists()) {
            throw new BuildException("Deployment descriptor: " + this.deploymentDescriptor + " does not exist.");
        }
        final ZipFileSet fs = new ZipFileSet();
        fs.setFile(this.deploymentDescriptor);
        fs.setFullpath("WEB-INF/web.xml");
        super.addFileset(fs);
    }
    
    public void setNeedxmlfile(final boolean needxmlfile) {
        this.needxmlfile = needxmlfile;
    }
    
    public void addLib(final ZipFileSet fs) {
        fs.setPrefix("WEB-INF/lib/");
        super.addFileset(fs);
    }
    
    public void addClasses(final ZipFileSet fs) {
        fs.setPrefix("WEB-INF/classes/");
        super.addFileset(fs);
    }
    
    public void addWebinf(final ZipFileSet fs) {
        fs.setPrefix("WEB-INF/");
        super.addFileset(fs);
    }
    
    @Override
    protected void initZipOutputStream(final ZipOutputStream zOut) throws IOException, BuildException {
        super.initZipOutputStream(zOut);
    }
    
    @Override
    protected void zipFile(final File file, final ZipOutputStream zOut, final String vPath, final int mode) throws IOException {
        boolean addFile = true;
        if ("WEB-INF/web.xml".equalsIgnoreCase(vPath)) {
            if (this.addedWebXmlFile != null) {
                addFile = false;
                if (!War.FILE_UTILS.fileNameEquals(this.addedWebXmlFile, file)) {
                    this.logWhenWriting("Warning: selected " + this.archiveType + " files include a second " + "WEB-INF/web.xml" + " which will be ignored.\n" + "The duplicate entry is at " + file + '\n' + "The file that will be used is " + this.addedWebXmlFile, 1);
                }
            }
            else {
                this.addedWebXmlFile = file;
                addFile = true;
                this.deploymentDescriptor = file;
            }
        }
        if (addFile) {
            super.zipFile(file, zOut, vPath, mode);
        }
    }
    
    @Override
    protected void cleanUp() {
        if (this.addedWebXmlFile == null && this.deploymentDescriptor == null && this.needxmlfile && !this.isInUpdateMode() && this.hasUpdatedFile()) {
            throw new BuildException("No WEB-INF/web.xml file was added.\nIf this is your intent, set needxmlfile='false' ");
        }
        this.addedWebXmlFile = null;
        super.cleanUp();
    }
    
    static {
        FILE_UTILS = FileUtils.getFileUtils();
    }
}
