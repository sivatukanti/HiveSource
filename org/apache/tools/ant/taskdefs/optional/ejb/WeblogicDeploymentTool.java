// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.ejb;

import org.apache.tools.ant.AntClassLoader;
import java.util.zip.ZipEntry;
import java.io.OutputStream;
import java.util.jar.JarOutputStream;
import java.io.FileOutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.tools.ant.taskdefs.Java;
import java.io.IOException;
import java.util.Enumeration;
import javax.xml.parsers.SAXParser;
import org.apache.tools.ant.BuildException;
import org.xml.sax.HandlerBase;
import java.io.InputStream;
import org.xml.sax.InputSource;
import java.io.FileInputStream;
import javax.xml.parsers.SAXParserFactory;
import java.util.Hashtable;
import java.util.Iterator;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Environment;
import java.io.File;
import java.util.Vector;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileUtils;

public class WeblogicDeploymentTool extends GenericDeploymentTool
{
    public static final String PUBLICID_EJB11 = "-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 1.1//EN";
    public static final String PUBLICID_EJB20 = "-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 2.0//EN";
    public static final String PUBLICID_WEBLOGIC_EJB510 = "-//BEA Systems, Inc.//DTD WebLogic 5.1.0 EJB//EN";
    public static final String PUBLICID_WEBLOGIC_EJB600 = "-//BEA Systems, Inc.//DTD WebLogic 6.0.0 EJB//EN";
    public static final String PUBLICID_WEBLOGIC_EJB700 = "-//BEA Systems, Inc.//DTD WebLogic 7.0.0 EJB//EN";
    protected static final String DEFAULT_WL51_EJB11_DTD_LOCATION = "/weblogic/ejb/deployment/xml/ejb-jar.dtd";
    protected static final String DEFAULT_WL60_EJB11_DTD_LOCATION = "/weblogic/ejb20/dd/xml/ejb11-jar.dtd";
    protected static final String DEFAULT_WL60_EJB20_DTD_LOCATION = "/weblogic/ejb20/dd/xml/ejb20-jar.dtd";
    protected static final String DEFAULT_WL51_DTD_LOCATION = "/weblogic/ejb/deployment/xml/weblogic-ejb-jar.dtd";
    protected static final String DEFAULT_WL60_51_DTD_LOCATION = "/weblogic/ejb20/dd/xml/weblogic510-ejb-jar.dtd";
    protected static final String DEFAULT_WL60_DTD_LOCATION = "/weblogic/ejb20/dd/xml/weblogic600-ejb-jar.dtd";
    protected static final String DEFAULT_WL70_DTD_LOCATION = "/weblogic/ejb20/dd/xml/weblogic700-ejb-jar.dtd";
    protected static final String DEFAULT_COMPILER = "default";
    protected static final String WL_DD = "weblogic-ejb-jar.xml";
    protected static final String WL_CMP_DD = "weblogic-cmp-rdbms-jar.xml";
    protected static final String COMPILER_EJB11 = "weblogic.ejbc";
    protected static final String COMPILER_EJB20 = "weblogic.ejbc20";
    private static final FileUtils FILE_UTILS;
    private String jarSuffix;
    private String weblogicDTD;
    private String ejb11DTD;
    private boolean keepgenerated;
    private String ejbcClass;
    private String additionalArgs;
    private String additionalJvmArgs;
    private boolean keepGeneric;
    private String compiler;
    private boolean alwaysRebuild;
    private boolean noEJBC;
    private boolean newCMP;
    private Path wlClasspath;
    private Vector sysprops;
    private Integer jvmDebugLevel;
    private File outputDir;
    
    public WeblogicDeploymentTool() {
        this.jarSuffix = ".jar";
        this.keepgenerated = false;
        this.ejbcClass = null;
        this.additionalArgs = "";
        this.additionalJvmArgs = "";
        this.keepGeneric = false;
        this.compiler = null;
        this.alwaysRebuild = true;
        this.noEJBC = false;
        this.newCMP = false;
        this.wlClasspath = null;
        this.sysprops = new Vector();
        this.jvmDebugLevel = null;
    }
    
    public void addSysproperty(final Environment.Variable sysp) {
        this.sysprops.add(sysp);
    }
    
    public Path createWLClasspath() {
        if (this.wlClasspath == null) {
            this.wlClasspath = new Path(this.getTask().getProject());
        }
        return this.wlClasspath.createPath();
    }
    
    public void setOutputDir(final File outputDir) {
        this.outputDir = outputDir;
    }
    
    public void setWLClasspath(final Path wlClasspath) {
        this.wlClasspath = wlClasspath;
    }
    
    public void setCompiler(final String compiler) {
        this.compiler = compiler;
    }
    
    public void setRebuild(final boolean rebuild) {
        this.alwaysRebuild = rebuild;
    }
    
    public void setJvmDebugLevel(final Integer jvmDebugLevel) {
        this.jvmDebugLevel = jvmDebugLevel;
    }
    
    public Integer getJvmDebugLevel() {
        return this.jvmDebugLevel;
    }
    
    public void setSuffix(final String inString) {
        this.jarSuffix = inString;
    }
    
    public void setKeepgeneric(final boolean inValue) {
        this.keepGeneric = inValue;
    }
    
    public void setKeepgenerated(final String inValue) {
        this.keepgenerated = Boolean.valueOf(inValue);
    }
    
    public void setArgs(final String args) {
        this.additionalArgs = args;
    }
    
    public void setJvmargs(final String args) {
        this.additionalJvmArgs = args;
    }
    
    public void setEjbcClass(final String ejbcClass) {
        this.ejbcClass = ejbcClass;
    }
    
    public String getEjbcClass() {
        return this.ejbcClass;
    }
    
    public void setWeblogicdtd(final String inString) {
        this.setEJBdtd(inString);
    }
    
    public void setWLdtd(final String inString) {
        this.weblogicDTD = inString;
    }
    
    public void setEJBdtd(final String inString) {
        this.ejb11DTD = inString;
    }
    
    public void setOldCMP(final boolean oldCMP) {
        this.newCMP = !oldCMP;
    }
    
    public void setNewCMP(final boolean newCMP) {
        this.newCMP = newCMP;
    }
    
    public void setNoEJBC(final boolean noEJBC) {
        this.noEJBC = noEJBC;
    }
    
    @Override
    protected void registerKnownDTDs(final DescriptorHandler handler) {
        handler.registerDTD("-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 1.1//EN", "/weblogic/ejb/deployment/xml/ejb-jar.dtd");
        handler.registerDTD("-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 1.1//EN", "/weblogic/ejb20/dd/xml/ejb11-jar.dtd");
        handler.registerDTD("-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 1.1//EN", this.ejb11DTD);
        handler.registerDTD("-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 2.0//EN", "/weblogic/ejb20/dd/xml/ejb20-jar.dtd");
    }
    
    protected DescriptorHandler getWeblogicDescriptorHandler(final File srcDir) {
        final DescriptorHandler handler = new DescriptorHandler(this.getTask(), srcDir) {
            @Override
            protected void processElement() {
                if (this.currentElement.equals("type-storage")) {
                    final String fileNameWithMETA = this.currentText;
                    final String fileName = fileNameWithMETA.substring("META-INF/".length(), fileNameWithMETA.length());
                    final File descriptorFile = new File(srcDir, fileName);
                    this.ejbFiles.put(fileNameWithMETA, descriptorFile);
                }
            }
        };
        handler.registerDTD("-//BEA Systems, Inc.//DTD WebLogic 5.1.0 EJB//EN", "/weblogic/ejb/deployment/xml/weblogic-ejb-jar.dtd");
        handler.registerDTD("-//BEA Systems, Inc.//DTD WebLogic 5.1.0 EJB//EN", "/weblogic/ejb20/dd/xml/weblogic510-ejb-jar.dtd");
        handler.registerDTD("-//BEA Systems, Inc.//DTD WebLogic 6.0.0 EJB//EN", "/weblogic/ejb20/dd/xml/weblogic600-ejb-jar.dtd");
        handler.registerDTD("-//BEA Systems, Inc.//DTD WebLogic 7.0.0 EJB//EN", "/weblogic/ejb20/dd/xml/weblogic700-ejb-jar.dtd");
        handler.registerDTD("-//BEA Systems, Inc.//DTD WebLogic 5.1.0 EJB//EN", this.weblogicDTD);
        handler.registerDTD("-//BEA Systems, Inc.//DTD WebLogic 6.0.0 EJB//EN", this.weblogicDTD);
        for (final EjbJar.DTDLocation dtdLocation : this.getConfig().dtdLocations) {
            handler.registerDTD(dtdLocation.getPublicId(), dtdLocation.getLocation());
        }
        return handler;
    }
    
    @Override
    protected void addVendorFiles(final Hashtable ejbFiles, final String ddPrefix) {
        final File weblogicDD = new File(this.getConfig().descriptorDir, ddPrefix + "weblogic-ejb-jar.xml");
        if (weblogicDD.exists()) {
            ejbFiles.put("META-INF/weblogic-ejb-jar.xml", weblogicDD);
            if (!this.newCMP) {
                this.log("The old method for locating CMP files has been DEPRECATED.", 3);
                this.log("Please adjust your weblogic descriptor and set newCMP=\"true\" to use the new CMP descriptor inclusion mechanism. ", 3);
                final File weblogicCMPDD = new File(this.getConfig().descriptorDir, ddPrefix + "weblogic-cmp-rdbms-jar.xml");
                if (weblogicCMPDD.exists()) {
                    ejbFiles.put("META-INF/weblogic-cmp-rdbms-jar.xml", weblogicCMPDD);
                }
            }
            else {
                try {
                    final File ejbDescriptor = ejbFiles.get("META-INF/ejb-jar.xml");
                    final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
                    saxParserFactory.setValidating(true);
                    final SAXParser saxParser = saxParserFactory.newSAXParser();
                    final DescriptorHandler handler = this.getWeblogicDescriptorHandler(ejbDescriptor.getParentFile());
                    saxParser.parse(new InputSource(new FileInputStream(weblogicDD)), handler);
                    final Hashtable ht = handler.getFiles();
                    final Enumeration e = ht.keys();
                    while (e.hasMoreElements()) {
                        final String key = e.nextElement();
                        ejbFiles.put(key, ht.get(key));
                    }
                }
                catch (Exception e2) {
                    final String msg = "Exception while adding Vendor specific files: " + e2.toString();
                    throw new BuildException(msg, e2);
                }
            }
            return;
        }
        this.log("Unable to locate weblogic deployment descriptor. It was expected to be in " + weblogicDD.getPath(), 1);
    }
    
    @Override
    File getVendorOutputJarFile(final String baseName) {
        return new File(this.getDestDir(), baseName + this.jarSuffix);
    }
    
    private void buildWeblogicJar(final File sourceJar, final File destJar, final String publicId) {
        Java javaTask = null;
        if (this.noEJBC) {
            try {
                WeblogicDeploymentTool.FILE_UTILS.copyFile(sourceJar, destJar);
                if (!this.keepgenerated) {
                    sourceJar.delete();
                }
                return;
            }
            catch (IOException e) {
                throw new BuildException("Unable to write EJB jar", e);
            }
        }
        String ejbcClassName = this.ejbcClass;
        try {
            javaTask = new Java(this.getTask());
            javaTask.setTaskName("ejbc");
            javaTask.createJvmarg().setLine(this.additionalJvmArgs);
            if (!this.sysprops.isEmpty()) {
                final Enumeration en = this.sysprops.elements();
                while (en.hasMoreElements()) {
                    final Environment.Variable entry = en.nextElement();
                    javaTask.addSysproperty(entry);
                }
            }
            if (this.getJvmDebugLevel() != null) {
                javaTask.createJvmarg().setLine(" -Dweblogic.StdoutSeverityLevel=" + this.jvmDebugLevel);
            }
            if (ejbcClassName == null) {
                if ("-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 1.1//EN".equals(publicId)) {
                    ejbcClassName = "weblogic.ejbc";
                }
                else if ("-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 2.0//EN".equals(publicId)) {
                    ejbcClassName = "weblogic.ejbc20";
                }
                else {
                    this.log("Unrecognized publicId " + publicId + " - using EJB 1.1 compiler", 1);
                    ejbcClassName = "weblogic.ejbc";
                }
            }
            javaTask.setClassname(ejbcClassName);
            javaTask.createArg().setLine(this.additionalArgs);
            if (this.keepgenerated) {
                javaTask.createArg().setValue("-keepgenerated");
            }
            if (this.compiler == null) {
                final String buildCompiler = this.getTask().getProject().getProperty("build.compiler");
                if (buildCompiler != null && buildCompiler.equals("jikes")) {
                    javaTask.createArg().setValue("-compiler");
                    javaTask.createArg().setValue("jikes");
                }
            }
            else if (!this.compiler.equals("default")) {
                javaTask.createArg().setValue("-compiler");
                javaTask.createArg().setLine(this.compiler);
            }
            final Path combinedClasspath = this.getCombinedClasspath();
            if (this.wlClasspath != null && combinedClasspath != null && combinedClasspath.toString().trim().length() > 0) {
                javaTask.createArg().setValue("-classpath");
                javaTask.createArg().setPath(combinedClasspath);
            }
            javaTask.createArg().setValue(sourceJar.getPath());
            if (this.outputDir == null) {
                javaTask.createArg().setValue(destJar.getPath());
            }
            else {
                javaTask.createArg().setValue(this.outputDir.getPath());
            }
            Path classpath = this.wlClasspath;
            if (classpath == null) {
                classpath = this.getCombinedClasspath();
            }
            javaTask.setFork(true);
            if (classpath != null) {
                javaTask.setClasspath(classpath);
            }
            this.log("Calling " + ejbcClassName + " for " + sourceJar.toString(), 3);
            if (javaTask.executeJava() != 0) {
                throw new BuildException("Ejbc reported an error");
            }
        }
        catch (Exception e2) {
            final String msg = "Exception while calling " + ejbcClassName + ". Details: " + e2.toString();
            throw new BuildException(msg, e2);
        }
    }
    
    @Override
    protected void writeJar(final String baseName, final File jarFile, final Hashtable files, final String publicId) throws BuildException {
        final File genericJarFile = super.getVendorOutputJarFile(baseName);
        super.writeJar(baseName, genericJarFile, files, publicId);
        if (this.alwaysRebuild || this.isRebuildRequired(genericJarFile, jarFile)) {
            this.buildWeblogicJar(genericJarFile, jarFile, publicId);
        }
        if (!this.keepGeneric) {
            this.log("deleting generic jar " + genericJarFile.toString(), 3);
            genericJarFile.delete();
        }
    }
    
    @Override
    public void validateConfigured() throws BuildException {
        super.validateConfigured();
    }
    
    protected boolean isRebuildRequired(final File genericJarFile, final File weblogicJarFile) {
        boolean rebuild = false;
        JarFile genericJar = null;
        JarFile wlJar = null;
        File newWLJarFile = null;
        JarOutputStream newJarStream = null;
        ClassLoader genericLoader = null;
        try {
            this.log("Checking if weblogic Jar needs to be rebuilt for jar " + weblogicJarFile.getName(), 3);
            if (genericJarFile.exists() && genericJarFile.isFile() && weblogicJarFile.exists() && weblogicJarFile.isFile()) {
                genericJar = new JarFile(genericJarFile);
                wlJar = new JarFile(weblogicJarFile);
                final Hashtable genericEntries = new Hashtable();
                final Hashtable wlEntries = new Hashtable();
                final Hashtable replaceEntries = new Hashtable();
                Enumeration e = genericJar.entries();
                while (e.hasMoreElements()) {
                    final JarEntry je = e.nextElement();
                    genericEntries.put(je.getName().replace('\\', '/'), je);
                }
                e = wlJar.entries();
                while (e.hasMoreElements()) {
                    final JarEntry je = e.nextElement();
                    wlEntries.put(je.getName(), je);
                }
                genericLoader = this.getClassLoaderFromJar(genericJarFile);
                e = genericEntries.keys();
                while (e.hasMoreElements()) {
                    final String filepath = e.nextElement();
                    if (!wlEntries.containsKey(filepath)) {
                        this.log("File " + filepath + " not present in weblogic jar", 3);
                        rebuild = true;
                        break;
                    }
                    final JarEntry genericEntry = genericEntries.get(filepath);
                    final JarEntry wlEntry = wlEntries.get(filepath);
                    if (genericEntry.getCrc() == wlEntry.getCrc() && genericEntry.getSize() == wlEntry.getSize()) {
                        continue;
                    }
                    if (genericEntry.getName().endsWith(".class")) {
                        String classname = genericEntry.getName().replace(File.separatorChar, '.').replace('/', '.');
                        classname = classname.substring(0, classname.lastIndexOf(".class"));
                        final Class genclass = genericLoader.loadClass(classname);
                        if (genclass.isInterface()) {
                            this.log("Interface " + genclass.getName() + " has changed", 3);
                            rebuild = true;
                            break;
                        }
                        replaceEntries.put(filepath, genericEntry);
                    }
                    else {
                        if (!genericEntry.getName().equals("META-INF/MANIFEST.MF")) {
                            this.log("Non class file " + genericEntry.getName() + " has changed", 3);
                            rebuild = true;
                            break;
                        }
                        continue;
                    }
                }
                if (!rebuild) {
                    this.log("No rebuild needed - updating jar", 3);
                    newWLJarFile = new File(weblogicJarFile.getAbsolutePath() + ".temp");
                    if (newWLJarFile.exists()) {
                        newWLJarFile.delete();
                    }
                    newJarStream = new JarOutputStream(new FileOutputStream(newWLJarFile));
                    newJarStream.setLevel(0);
                    e = wlEntries.elements();
                    while (e.hasMoreElements()) {
                        final byte[] buffer = new byte[1024];
                        JarEntry je2 = e.nextElement();
                        if (je2.getCompressedSize() == -1L || je2.getCompressedSize() == je2.getSize()) {
                            newJarStream.setLevel(0);
                        }
                        else {
                            newJarStream.setLevel(9);
                        }
                        InputStream is;
                        if (replaceEntries.containsKey(je2.getName())) {
                            this.log("Updating Bean class from generic Jar " + je2.getName(), 3);
                            je2 = replaceEntries.get(je2.getName());
                            is = genericJar.getInputStream(je2);
                        }
                        else {
                            is = wlJar.getInputStream(je2);
                        }
                        newJarStream.putNextEntry(new JarEntry(je2.getName()));
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            newJarStream.write(buffer, 0, bytesRead);
                        }
                        is.close();
                    }
                }
                else {
                    this.log("Weblogic Jar rebuild needed due to changed interface or XML", 3);
                }
            }
            else {
                rebuild = true;
            }
        }
        catch (ClassNotFoundException cnfe) {
            final String cnfmsg = "ClassNotFoundException while processing ejb-jar file. Details: " + cnfe.getMessage();
            throw new BuildException(cnfmsg, cnfe);
        }
        catch (IOException ioe) {
            final String msg = "IOException while processing ejb-jar file . Details: " + ioe.getMessage();
            throw new BuildException(msg, ioe);
        }
        finally {
            if (genericJar != null) {
                try {
                    genericJar.close();
                }
                catch (IOException ex) {}
            }
            if (wlJar != null) {
                try {
                    wlJar.close();
                }
                catch (IOException ex2) {}
            }
            if (newJarStream != null) {
                try {
                    newJarStream.close();
                }
                catch (IOException ex3) {}
                try {
                    WeblogicDeploymentTool.FILE_UTILS.rename(newWLJarFile, weblogicJarFile);
                }
                catch (IOException renameException) {
                    this.log(renameException.getMessage(), 1);
                    rebuild = true;
                }
            }
            if (genericLoader != null && genericLoader instanceof AntClassLoader) {
                final AntClassLoader loader = (AntClassLoader)genericLoader;
                loader.cleanup();
            }
        }
        return rebuild;
    }
    
    protected ClassLoader getClassLoaderFromJar(final File classjar) throws IOException {
        final Path lookupPath = new Path(this.getTask().getProject());
        lookupPath.setLocation(classjar);
        final Path classpath = this.getCombinedClasspath();
        if (classpath != null) {
            lookupPath.append(classpath);
        }
        return this.getTask().getProject().createClassLoader(lookupPath);
    }
    
    static {
        FILE_UTILS = FileUtils.getFileUtils();
    }
}
