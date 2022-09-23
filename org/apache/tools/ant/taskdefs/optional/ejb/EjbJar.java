// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.ejb;

import org.apache.tools.ant.types.EnumeratedAttribute;
import java.util.List;
import org.apache.tools.ant.types.DTDLocation;
import org.apache.tools.ant.DirectoryScanner;
import javax.xml.parsers.SAXParser;
import java.util.Iterator;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import javax.xml.parsers.SAXParserFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.Task;
import java.util.ArrayList;
import java.io.File;
import org.apache.tools.ant.taskdefs.MatchingTask;

public class EjbJar extends MatchingTask
{
    private Config config;
    private File destDir;
    private String genericJarSuffix;
    private String cmpVersion;
    private ArrayList deploymentTools;
    
    public EjbJar() {
        this.config = new Config();
        this.genericJarSuffix = "-generic.jar";
        this.cmpVersion = "1.0";
        this.deploymentTools = new ArrayList();
    }
    
    protected void addDeploymentTool(final EJBDeploymentTool deploymentTool) {
        deploymentTool.setTask(this);
        this.deploymentTools.add(deploymentTool);
    }
    
    public WeblogicDeploymentTool createWeblogic() {
        final WeblogicDeploymentTool tool = new WeblogicDeploymentTool();
        this.addDeploymentTool(tool);
        return tool;
    }
    
    public WebsphereDeploymentTool createWebsphere() {
        final WebsphereDeploymentTool tool = new WebsphereDeploymentTool();
        this.addDeploymentTool(tool);
        return tool;
    }
    
    public BorlandDeploymentTool createBorland() {
        this.log("Borland deployment tools", 3);
        final BorlandDeploymentTool tool = new BorlandDeploymentTool();
        tool.setTask(this);
        this.deploymentTools.add(tool);
        return tool;
    }
    
    public IPlanetDeploymentTool createIplanet() {
        this.log("iPlanet Application Server deployment tools", 3);
        final IPlanetDeploymentTool tool = new IPlanetDeploymentTool();
        this.addDeploymentTool(tool);
        return tool;
    }
    
    public JbossDeploymentTool createJboss() {
        final JbossDeploymentTool tool = new JbossDeploymentTool();
        this.addDeploymentTool(tool);
        return tool;
    }
    
    public JonasDeploymentTool createJonas() {
        this.log("JOnAS deployment tools", 3);
        final JonasDeploymentTool tool = new JonasDeploymentTool();
        this.addDeploymentTool(tool);
        return tool;
    }
    
    public WeblogicTOPLinkDeploymentTool createWeblogictoplink() {
        this.log("The <weblogictoplink> element is no longer required. Please use the <weblogic> element and set newCMP=\"true\"", 2);
        final WeblogicTOPLinkDeploymentTool tool = new WeblogicTOPLinkDeploymentTool();
        this.addDeploymentTool(tool);
        return tool;
    }
    
    public Path createClasspath() {
        if (this.config.classpath == null) {
            this.config.classpath = new Path(this.getProject());
        }
        return this.config.classpath.createPath();
    }
    
    public DTDLocation createDTD() {
        final DTDLocation dtdLocation = new DTDLocation();
        this.config.dtdLocations.add(dtdLocation);
        return dtdLocation;
    }
    
    public FileSet createSupport() {
        final FileSet supportFileSet = new FileSet();
        this.config.supportFileSets.add(supportFileSet);
        return supportFileSet;
    }
    
    public void setManifest(final File manifest) {
        this.config.manifest = manifest;
    }
    
    public void setSrcdir(final File inDir) {
        this.config.srcDir = inDir;
    }
    
    public void setDescriptordir(final File inDir) {
        this.config.descriptorDir = inDir;
    }
    
    public void setDependency(final String analyzer) {
        this.config.analyzer = analyzer;
    }
    
    public void setBasejarname(final String inValue) {
        this.config.baseJarName = inValue;
        if (this.config.namingScheme == null) {
            (this.config.namingScheme = new NamingScheme()).setValue("basejarname");
        }
        else if (!this.config.namingScheme.getValue().equals("basejarname")) {
            throw new BuildException("The basejarname attribute is not compatible with the " + this.config.namingScheme.getValue() + " naming scheme");
        }
    }
    
    public void setNaming(final NamingScheme namingScheme) {
        this.config.namingScheme = namingScheme;
        if (!this.config.namingScheme.getValue().equals("basejarname") && this.config.baseJarName != null) {
            throw new BuildException("The basejarname attribute is not compatible with the " + this.config.namingScheme.getValue() + " naming scheme");
        }
    }
    
    public File getDestdir() {
        return this.destDir;
    }
    
    public void setDestdir(final File inDir) {
        this.destDir = inDir;
    }
    
    public String getCmpversion() {
        return this.cmpVersion;
    }
    
    public void setCmpversion(final CMPVersion version) {
        this.cmpVersion = version.getValue();
    }
    
    public void setClasspath(final Path classpath) {
        this.config.classpath = classpath;
    }
    
    public void setFlatdestdir(final boolean inValue) {
        this.config.flatDestDir = inValue;
    }
    
    public void setGenericjarsuffix(final String inString) {
        this.genericJarSuffix = inString;
    }
    
    public void setBasenameterminator(final String inValue) {
        this.config.baseNameTerminator = inValue;
    }
    
    private void validateConfig() throws BuildException {
        if (this.config.srcDir == null) {
            throw new BuildException("The srcDir attribute must be specified");
        }
        if (this.config.descriptorDir == null) {
            this.config.descriptorDir = this.config.srcDir;
        }
        if (this.config.namingScheme == null) {
            (this.config.namingScheme = new NamingScheme()).setValue("descriptor");
        }
        else if (this.config.namingScheme.getValue().equals("basejarname") && this.config.baseJarName == null) {
            throw new BuildException("The basejarname attribute must be specified with the basejarname naming scheme");
        }
    }
    
    @Override
    public void execute() throws BuildException {
        this.validateConfig();
        if (this.deploymentTools.size() == 0) {
            final GenericDeploymentTool genericTool = new GenericDeploymentTool();
            genericTool.setTask(this);
            genericTool.setDestdir(this.destDir);
            genericTool.setGenericJarSuffix(this.genericJarSuffix);
            this.deploymentTools.add(genericTool);
        }
        for (final EJBDeploymentTool tool : this.deploymentTools) {
            tool.configure(this.config);
            tool.validateConfigured();
        }
        try {
            final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            saxParserFactory.setValidating(true);
            final SAXParser saxParser = saxParserFactory.newSAXParser();
            final DirectoryScanner ds = this.getDirectoryScanner(this.config.descriptorDir);
            ds.scan();
            final String[] files = ds.getIncludedFiles();
            this.log(files.length + " deployment descriptors located.", 3);
            for (int index = 0; index < files.length; ++index) {
                for (final EJBDeploymentTool tool2 : this.deploymentTools) {
                    tool2.processDescriptor(files[index], saxParser);
                }
            }
        }
        catch (SAXException se) {
            final String msg = "SAXException while creating parser.  Details: " + se.getMessage();
            throw new BuildException(msg, se);
        }
        catch (ParserConfigurationException pce) {
            final String msg = "ParserConfigurationException while creating parser. Details: " + pce.getMessage();
            throw new BuildException(msg, pce);
        }
    }
    
    public static class DTDLocation extends org.apache.tools.ant.types.DTDLocation
    {
    }
    
    static class Config
    {
        public File srcDir;
        public File descriptorDir;
        public String baseNameTerminator;
        public String baseJarName;
        public boolean flatDestDir;
        public Path classpath;
        public List supportFileSets;
        public ArrayList dtdLocations;
        public NamingScheme namingScheme;
        public File manifest;
        public String analyzer;
        
        Config() {
            this.baseNameTerminator = "-";
            this.flatDestDir = false;
            this.supportFileSets = new ArrayList();
            this.dtdLocations = new ArrayList();
        }
    }
    
    public static class NamingScheme extends EnumeratedAttribute
    {
        public static final String EJB_NAME = "ejb-name";
        public static final String DIRECTORY = "directory";
        public static final String DESCRIPTOR = "descriptor";
        public static final String BASEJARNAME = "basejarname";
        
        @Override
        public String[] getValues() {
            return new String[] { "ejb-name", "directory", "descriptor", "basejarname" };
        }
    }
    
    public static class CMPVersion extends EnumeratedAttribute
    {
        public static final String CMP1_0 = "1.0";
        public static final String CMP2_0 = "2.0";
        
        @Override
        public String[] getValues() {
            return new String[] { "1.0", "2.0" };
        }
    }
}
