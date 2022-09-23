// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.ejb;

import org.xml.sax.SAXException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Hashtable;
import org.apache.tools.ant.BuildException;
import javax.xml.parsers.SAXParser;
import java.io.File;

public class IPlanetDeploymentTool extends GenericDeploymentTool
{
    private File iashome;
    private String jarSuffix;
    private boolean keepgenerated;
    private boolean debug;
    private String descriptorName;
    private String iasDescriptorName;
    private String displayName;
    private static final String IAS_DD = "ias-ejb-jar.xml";
    
    public IPlanetDeploymentTool() {
        this.jarSuffix = ".jar";
        this.keepgenerated = false;
        this.debug = false;
    }
    
    public void setIashome(final File iashome) {
        this.iashome = iashome;
    }
    
    public void setKeepgenerated(final boolean keepgenerated) {
        this.keepgenerated = keepgenerated;
    }
    
    public void setDebug(final boolean debug) {
        this.debug = debug;
    }
    
    public void setSuffix(final String jarSuffix) {
        this.jarSuffix = jarSuffix;
    }
    
    @Override
    public void setGenericJarSuffix(final String inString) {
        this.log("Since a generic JAR file is not created during processing, the iPlanet Deployment Tool does not support the \"genericjarsuffix\" attribute.  It will be ignored.", 1);
    }
    
    @Override
    public void processDescriptor(final String descriptorName, final SAXParser saxParser) {
        this.descriptorName = descriptorName;
        this.iasDescriptorName = null;
        this.log("iPlanet Deployment Tool processing: " + descriptorName + " (and " + this.getIasDescriptorName() + ")", 3);
        super.processDescriptor(descriptorName, saxParser);
    }
    
    @Override
    protected void checkConfiguration(final String descriptorFileName, final SAXParser saxParser) throws BuildException {
        final int startOfName = descriptorFileName.lastIndexOf(File.separatorChar) + 1;
        final String stdXml = descriptorFileName.substring(startOfName);
        if (stdXml.equals("ejb-jar.xml") && this.getConfig().baseJarName == null) {
            final String msg = "No name specified for the completed JAR file.  The EJB descriptor should be prepended with the JAR name or it should be specified using the attribute \"basejarname\" in the \"ejbjar\" task.";
            throw new BuildException(msg, this.getLocation());
        }
        final File iasDescriptor = new File(this.getConfig().descriptorDir, this.getIasDescriptorName());
        if (!iasDescriptor.exists() || !iasDescriptor.isFile()) {
            final String msg2 = "The iAS-specific EJB descriptor (" + iasDescriptor + ") was not found.";
            throw new BuildException(msg2, this.getLocation());
        }
        if (this.iashome != null && !this.iashome.isDirectory()) {
            final String msg2 = "If \"iashome\" is specified, it must be a valid directory (it was set to " + this.iashome + ").";
            throw new BuildException(msg2, this.getLocation());
        }
    }
    
    @Override
    protected Hashtable parseEjbFiles(final String descriptorFileName, final SAXParser saxParser) throws IOException, SAXException {
        final IPlanetEjbc ejbc = new IPlanetEjbc(new File(this.getConfig().descriptorDir, descriptorFileName), new File(this.getConfig().descriptorDir, this.getIasDescriptorName()), this.getConfig().srcDir, this.getCombinedClasspath().toString(), saxParser);
        ejbc.setRetainSource(this.keepgenerated);
        ejbc.setDebugOutput(this.debug);
        if (this.iashome != null) {
            ejbc.setIasHomeDir(this.iashome);
        }
        if (this.getConfig().dtdLocations != null) {
            for (final EjbJar.DTDLocation dtdLocation : this.getConfig().dtdLocations) {
                ejbc.registerDTD(dtdLocation.getPublicId(), dtdLocation.getLocation());
            }
        }
        try {
            ejbc.execute();
        }
        catch (IPlanetEjbc.EjbcException e) {
            throw new BuildException("An error has occurred while trying to execute the iAS ejbc utility", e, this.getLocation());
        }
        this.displayName = ejbc.getDisplayName();
        final Hashtable files = ejbc.getEjbFiles();
        final String[] cmpDescriptors = ejbc.getCmpDescriptors();
        if (cmpDescriptors.length > 0) {
            final File baseDir = this.getConfig().descriptorDir;
            final int endOfPath = descriptorFileName.lastIndexOf(File.separator);
            final String relativePath = descriptorFileName.substring(0, endOfPath + 1);
            for (int j = 0; j < cmpDescriptors.length; ++j) {
                final int endOfCmp = cmpDescriptors[j].lastIndexOf(47);
                final String cmpDescriptor = cmpDescriptors[j].substring(endOfCmp + 1);
                final File cmpFile = new File(baseDir, relativePath + cmpDescriptor);
                if (!cmpFile.exists()) {
                    throw new BuildException("The CMP descriptor file (" + cmpFile + ") could not be found.", this.getLocation());
                }
                files.put(cmpDescriptors[j], cmpFile);
            }
        }
        return files;
    }
    
    @Override
    protected void addVendorFiles(final Hashtable ejbFiles, final String ddPrefix) {
        ejbFiles.put("META-INF/ias-ejb-jar.xml", new File(this.getConfig().descriptorDir, this.getIasDescriptorName()));
    }
    
    @Override
    File getVendorOutputJarFile(final String baseName) {
        final File jarFile = new File(this.getDestDir(), baseName + this.jarSuffix);
        this.log("JAR file name: " + jarFile.toString(), 3);
        return jarFile;
    }
    
    @Override
    protected String getPublicId() {
        return null;
    }
    
    private String getIasDescriptorName() {
        if (this.iasDescriptorName != null) {
            return this.iasDescriptorName;
        }
        String path = "";
        final int startOfFileName = this.descriptorName.lastIndexOf(File.separatorChar);
        if (startOfFileName != -1) {
            path = this.descriptorName.substring(0, startOfFileName + 1);
        }
        String basename;
        String remainder;
        if (this.descriptorName.substring(startOfFileName + 1).equals("ejb-jar.xml")) {
            basename = "";
            remainder = "ejb-jar.xml";
        }
        else {
            int endOfBaseName = this.descriptorName.indexOf(this.getConfig().baseNameTerminator, startOfFileName);
            if (endOfBaseName < 0) {
                endOfBaseName = this.descriptorName.lastIndexOf(46) - 1;
                if (endOfBaseName < 0) {
                    endOfBaseName = this.descriptorName.length() - 1;
                }
            }
            basename = this.descriptorName.substring(startOfFileName + 1, endOfBaseName + 1);
            remainder = this.descriptorName.substring(endOfBaseName + 1);
        }
        return this.iasDescriptorName = path + basename + "ias-" + remainder;
    }
}
