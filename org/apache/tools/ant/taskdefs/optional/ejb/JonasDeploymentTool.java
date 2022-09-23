// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.ejb;

import org.apache.tools.ant.AntClassLoader;
import java.util.Enumeration;
import org.apache.tools.ant.types.Path;
import java.io.IOException;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.BuildException;
import java.util.Hashtable;
import javax.xml.parsers.SAXParser;
import java.io.File;

public class JonasDeploymentTool extends GenericDeploymentTool
{
    protected static final String EJB_JAR_1_1_PUBLIC_ID = "-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 1.1//EN";
    protected static final String EJB_JAR_2_0_PUBLIC_ID = "-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 2.0//EN";
    protected static final String JONAS_EJB_JAR_2_4_PUBLIC_ID = "-//ObjectWeb//DTD JOnAS 2.4//EN";
    protected static final String JONAS_EJB_JAR_2_5_PUBLIC_ID = "-//ObjectWeb//DTD JOnAS 2.5//EN";
    protected static final String RMI_ORB = "RMI";
    protected static final String JEREMIE_ORB = "JEREMIE";
    protected static final String DAVID_ORB = "DAVID";
    protected static final String EJB_JAR_1_1_DTD = "ejb-jar_1_1.dtd";
    protected static final String EJB_JAR_2_0_DTD = "ejb-jar_2_0.dtd";
    protected static final String JONAS_EJB_JAR_2_4_DTD = "jonas-ejb-jar_2_4.dtd";
    protected static final String JONAS_EJB_JAR_2_5_DTD = "jonas-ejb-jar_2_5.dtd";
    protected static final String JONAS_DD = "jonas-ejb-jar.xml";
    protected static final String GENIC_CLASS = "org.objectweb.jonas_ejb.genic.GenIC";
    protected static final String OLD_GENIC_CLASS_1 = "org.objectweb.jonas_ejb.tools.GenWholeIC";
    protected static final String OLD_GENIC_CLASS_2 = "org.objectweb.jonas_ejb.tools.GenIC";
    private String descriptorName;
    private String jonasDescriptorName;
    private File outputdir;
    private boolean keepgenerated;
    private boolean nocompil;
    private boolean novalidation;
    private String javac;
    private String javacopts;
    private String rmicopts;
    private boolean secpropag;
    private boolean verbose;
    private String additionalargs;
    private File jonasroot;
    private boolean keepgeneric;
    private String suffix;
    private String orb;
    private boolean nogenic;
    
    public JonasDeploymentTool() {
        this.keepgenerated = false;
        this.nocompil = false;
        this.novalidation = false;
        this.secpropag = false;
        this.verbose = false;
        this.keepgeneric = false;
        this.suffix = ".jar";
        this.nogenic = false;
    }
    
    public void setKeepgenerated(final boolean aBoolean) {
        this.keepgenerated = aBoolean;
    }
    
    public void setAdditionalargs(final String aString) {
        this.additionalargs = aString;
    }
    
    public void setNocompil(final boolean aBoolean) {
        this.nocompil = aBoolean;
    }
    
    public void setNovalidation(final boolean aBoolean) {
        this.novalidation = aBoolean;
    }
    
    public void setJavac(final String aString) {
        this.javac = aString;
    }
    
    public void setJavacopts(final String aString) {
        this.javacopts = aString;
    }
    
    public void setRmicopts(final String aString) {
        this.rmicopts = aString;
    }
    
    public void setSecpropag(final boolean aBoolean) {
        this.secpropag = aBoolean;
    }
    
    public void setVerbose(final boolean aBoolean) {
        this.verbose = aBoolean;
    }
    
    public void setJonasroot(final File aFile) {
        this.jonasroot = aFile;
    }
    
    public void setKeepgeneric(final boolean aBoolean) {
        this.keepgeneric = aBoolean;
    }
    
    public void setJarsuffix(final String aString) {
        this.suffix = aString;
    }
    
    public void setOrb(final String aString) {
        this.orb = aString;
    }
    
    public void setNogenic(final boolean aBoolean) {
        this.nogenic = aBoolean;
    }
    
    @Override
    public void processDescriptor(final String aDescriptorName, final SAXParser saxParser) {
        this.descriptorName = aDescriptorName;
        this.log("JOnAS Deployment Tool processing: " + this.descriptorName, 3);
        super.processDescriptor(this.descriptorName, saxParser);
        if (this.outputdir != null) {
            this.log("Deleting temp output directory '" + this.outputdir + "'.", 3);
            this.deleteAllFiles(this.outputdir);
        }
    }
    
    @Override
    protected void writeJar(final String baseName, final File jarfile, final Hashtable ejbFiles, final String publicId) throws BuildException {
        final File genericJarFile = super.getVendorOutputJarFile(baseName);
        super.writeJar(baseName, genericJarFile, ejbFiles, publicId);
        this.addGenICGeneratedFiles(genericJarFile, ejbFiles);
        super.writeJar(baseName, this.getVendorOutputJarFile(baseName), ejbFiles, publicId);
        if (!this.keepgeneric) {
            this.log("Deleting generic JAR " + genericJarFile.toString(), 3);
            genericJarFile.delete();
        }
    }
    
    @Override
    protected void addVendorFiles(final Hashtable ejbFiles, final String ddPrefix) {
        this.jonasDescriptorName = this.getJonasDescriptorName();
        final File jonasDD = new File(this.getConfig().descriptorDir, this.jonasDescriptorName);
        if (jonasDD.exists()) {
            ejbFiles.put("META-INF/jonas-ejb-jar.xml", jonasDD);
        }
        else {
            this.log("Unable to locate the JOnAS deployment descriptor. It was expected to be in: " + jonasDD.getPath() + ".", 1);
        }
    }
    
    protected File getVendorOutputJarFile(final String baseName) {
        return new File(this.getDestDir(), baseName + this.suffix);
    }
    
    private String getJonasDescriptorName() {
        boolean jonasConvention = false;
        final int startOfFileName = this.descriptorName.lastIndexOf(File.separatorChar);
        String path;
        String fileName;
        if (startOfFileName != -1) {
            path = this.descriptorName.substring(0, startOfFileName + 1);
            fileName = this.descriptorName.substring(startOfFileName + 1);
        }
        else {
            path = "";
            fileName = this.descriptorName;
        }
        if (fileName.startsWith("ejb-jar.xml")) {
            return path + "jonas-ejb-jar.xml";
        }
        int endOfBaseName = this.descriptorName.indexOf(this.getConfig().baseNameTerminator, startOfFileName);
        if (endOfBaseName < 0) {
            endOfBaseName = this.descriptorName.lastIndexOf(46) - 1;
            if (endOfBaseName < 0) {
                endOfBaseName = this.descriptorName.length() - 1;
            }
            jonasConvention = true;
        }
        final String baseName = this.descriptorName.substring(startOfFileName + 1, endOfBaseName + 1);
        final String remainder = this.descriptorName.substring(endOfBaseName + 1);
        String jonasDN;
        if (jonasConvention) {
            jonasDN = path + "jonas-" + baseName + ".xml";
        }
        else {
            jonasDN = path + baseName + "jonas-" + remainder;
        }
        this.log("Standard EJB descriptor name: " + this.descriptorName, 3);
        this.log("JOnAS-specific descriptor name: " + jonasDN, 3);
        return jonasDN;
    }
    
    @Override
    protected String getJarBaseName(final String descriptorFileName) {
        String baseName = null;
        if (this.getConfig().namingScheme.getValue().equals("descriptor") && descriptorFileName.indexOf(this.getConfig().baseNameTerminator) == -1) {
            final String aCanonicalDescriptor = descriptorFileName.replace('\\', '/');
            final int lastSeparatorIndex = aCanonicalDescriptor.lastIndexOf(47);
            int endOfBaseName;
            if (lastSeparatorIndex != -1) {
                endOfBaseName = descriptorFileName.indexOf(".xml", lastSeparatorIndex);
            }
            else {
                endOfBaseName = descriptorFileName.indexOf(".xml");
            }
            if (endOfBaseName != -1) {
                baseName = descriptorFileName.substring(0, endOfBaseName);
            }
        }
        if (baseName == null) {
            baseName = super.getJarBaseName(descriptorFileName);
        }
        this.log("JAR base name: " + baseName, 3);
        return baseName;
    }
    
    @Override
    protected void registerKnownDTDs(final DescriptorHandler handler) {
        handler.registerDTD("-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 1.1//EN", this.jonasroot + File.separator + "xml" + File.separator + "ejb-jar_1_1.dtd");
        handler.registerDTD("-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 2.0//EN", this.jonasroot + File.separator + "xml" + File.separator + "ejb-jar_2_0.dtd");
        handler.registerDTD("-//ObjectWeb//DTD JOnAS 2.4//EN", this.jonasroot + File.separator + "xml" + File.separator + "jonas-ejb-jar_2_4.dtd");
        handler.registerDTD("-//ObjectWeb//DTD JOnAS 2.5//EN", this.jonasroot + File.separator + "xml" + File.separator + "jonas-ejb-jar_2_5.dtd");
    }
    
    private void addGenICGeneratedFiles(final File genericJarFile, final Hashtable ejbFiles) {
        Java genicTask = null;
        String genicClass = null;
        if (this.nogenic) {
            return;
        }
        genicTask = new Java(this.getTask());
        genicTask.setTaskName("genic");
        genicTask.setFork(true);
        genicTask.createJvmarg().setValue("-Dinstall.root=" + this.jonasroot);
        final String jonasConfigDir = this.jonasroot + File.separator + "config";
        final File javaPolicyFile = new File(jonasConfigDir, "java.policy");
        if (javaPolicyFile.exists()) {
            genicTask.createJvmarg().setValue("-Djava.security.policy=" + javaPolicyFile.toString());
        }
        try {
            this.outputdir = this.createTempDir();
        }
        catch (IOException aIOException) {
            final String msg = "Cannot create temp dir: " + aIOException.getMessage();
            throw new BuildException(msg, aIOException);
        }
        this.log("Using temporary output directory: " + this.outputdir, 3);
        genicTask.createArg().setValue("-d");
        genicTask.createArg().setFile(this.outputdir);
        final Enumeration keys = ejbFiles.keys();
        while (keys.hasMoreElements()) {
            final String key = keys.nextElement();
            final File f = new File(this.outputdir + File.separator + key);
            f.getParentFile().mkdirs();
        }
        this.log("Worked around a bug of GenIC 2.5.", 3);
        Path classpath = this.getCombinedClasspath();
        if (classpath == null) {
            classpath = new Path(this.getTask().getProject());
        }
        classpath.append(new Path(classpath.getProject(), jonasConfigDir));
        classpath.append(new Path(classpath.getProject(), this.outputdir.toString()));
        if (this.orb != null) {
            final String orbJar = this.jonasroot + File.separator + "lib" + File.separator + this.orb + "_jonas.jar";
            classpath.append(new Path(classpath.getProject(), orbJar));
        }
        this.log("Using classpath: " + classpath.toString(), 3);
        genicTask.setClasspath(classpath);
        genicClass = this.getGenicClassName(classpath);
        if (genicClass == null) {
            this.log("Cannot find GenIC class in classpath.", 0);
            throw new BuildException("GenIC class not found, please check the classpath.");
        }
        this.log("Using '" + genicClass + "' GenIC class.", 3);
        genicTask.setClassname(genicClass);
        if (this.keepgenerated) {
            genicTask.createArg().setValue("-keepgenerated");
        }
        if (this.nocompil) {
            genicTask.createArg().setValue("-nocompil");
        }
        if (this.novalidation) {
            genicTask.createArg().setValue("-novalidation");
        }
        if (this.javac != null) {
            genicTask.createArg().setValue("-javac");
            genicTask.createArg().setLine(this.javac);
        }
        if (this.javacopts != null && !this.javacopts.equals("")) {
            genicTask.createArg().setValue("-javacopts");
            genicTask.createArg().setLine(this.javacopts);
        }
        if (this.rmicopts != null && !this.rmicopts.equals("")) {
            genicTask.createArg().setValue("-rmicopts");
            genicTask.createArg().setLine(this.rmicopts);
        }
        if (this.secpropag) {
            genicTask.createArg().setValue("-secpropag");
        }
        if (this.verbose) {
            genicTask.createArg().setValue("-verbose");
        }
        if (this.additionalargs != null) {
            genicTask.createArg().setValue(this.additionalargs);
        }
        genicTask.createArg().setValue("-noaddinjar");
        genicTask.createArg().setValue(genericJarFile.getPath());
        this.log("Calling " + genicClass + " for " + this.getConfig().descriptorDir + File.separator + this.descriptorName + ".", 3);
        if (genicTask.executeJava() != 0) {
            this.log("Deleting temp output directory '" + this.outputdir + "'.", 3);
            this.deleteAllFiles(this.outputdir);
            if (!this.keepgeneric) {
                this.log("Deleting generic JAR " + genericJarFile.toString(), 3);
                genericJarFile.delete();
            }
            throw new BuildException("GenIC reported an error.");
        }
        this.addAllFiles(this.outputdir, "", ejbFiles);
    }
    
    String getGenicClassName(final Path classpath) {
        this.log("Looking for GenIC class in classpath: " + classpath.toString(), 3);
        AntClassLoader cl = null;
        try {
            cl = classpath.getProject().createClassLoader(classpath);
            try {
                cl.loadClass("org.objectweb.jonas_ejb.genic.GenIC");
                this.log("Found GenIC class 'org.objectweb.jonas_ejb.genic.GenIC' in classpath.", 3);
                return "org.objectweb.jonas_ejb.genic.GenIC";
            }
            catch (ClassNotFoundException cnf1) {
                this.log("GenIC class 'org.objectweb.jonas_ejb.genic.GenIC' not found in classpath.", 3);
                try {
                    cl.loadClass("org.objectweb.jonas_ejb.tools.GenWholeIC");
                    this.log("Found GenIC class 'org.objectweb.jonas_ejb.tools.GenWholeIC' in classpath.", 3);
                    return "org.objectweb.jonas_ejb.tools.GenWholeIC";
                }
                catch (ClassNotFoundException cnf2) {
                    this.log("GenIC class 'org.objectweb.jonas_ejb.tools.GenWholeIC' not found in classpath.", 3);
                    try {
                        cl.loadClass("org.objectweb.jonas_ejb.tools.GenIC");
                        this.log("Found GenIC class 'org.objectweb.jonas_ejb.tools.GenIC' in classpath.", 3);
                        return "org.objectweb.jonas_ejb.tools.GenIC";
                    }
                    catch (ClassNotFoundException cnf3) {
                        this.log("GenIC class 'org.objectweb.jonas_ejb.tools.GenIC' not found in classpath.", 3);
                    }
                }
            }
        }
        finally {
            if (cl != null) {
                cl.cleanup();
            }
        }
        return null;
    }
    
    @Override
    protected void checkConfiguration(final String descriptorFileName, final SAXParser saxParser) throws BuildException {
        if (this.jonasroot == null) {
            throw new BuildException("The jonasroot attribut is not set.");
        }
        if (!this.jonasroot.isDirectory()) {
            throw new BuildException("The jonasroot attribut '" + this.jonasroot + "' is not a valid directory.");
        }
        if (this.orb != null && !this.orb.equals("RMI") && !this.orb.equals("JEREMIE") && !this.orb.equals("DAVID")) {
            throw new BuildException("The orb attribut '" + this.orb + "' is not valid (must be either " + "RMI" + ", " + "JEREMIE" + " or " + "DAVID" + ").");
        }
        if (this.additionalargs != null && this.additionalargs.equals("")) {
            throw new BuildException("Empty additionalargs attribut.");
        }
        if (this.javac != null && this.javac.equals("")) {
            throw new BuildException("Empty javac attribut.");
        }
    }
    
    private File createTempDir() throws IOException {
        final File tmpDir = File.createTempFile("genic", null, null);
        tmpDir.delete();
        if (!tmpDir.mkdir()) {
            throw new IOException("Cannot create the temporary directory '" + tmpDir + "'.");
        }
        return tmpDir;
    }
    
    private void deleteAllFiles(final File aFile) {
        if (aFile.isDirectory()) {
            final File[] someFiles = aFile.listFiles();
            for (int i = 0; i < someFiles.length; ++i) {
                this.deleteAllFiles(someFiles[i]);
            }
        }
        aFile.delete();
    }
    
    private void addAllFiles(final File file, final String rootDir, final Hashtable hashtable) {
        if (!file.exists()) {
            throw new IllegalArgumentException();
        }
        if (file.isDirectory()) {
            final File[] files = file.listFiles();
            for (int i = 0; i < files.length; ++i) {
                String newRootDir;
                if (rootDir.length() > 0) {
                    newRootDir = rootDir + File.separator + files[i].getName();
                }
                else {
                    newRootDir = files[i].getName();
                }
                this.addAllFiles(files[i], newRootDir, hashtable);
            }
        }
        else {
            hashtable.put(rootDir, file);
        }
    }
}
