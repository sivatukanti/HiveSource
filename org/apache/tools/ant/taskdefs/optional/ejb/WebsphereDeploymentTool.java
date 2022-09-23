// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.InputStream;
import java.util.Enumeration;
import org.apache.tools.ant.AntClassLoader;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.io.OutputStream;
import java.util.jar.JarOutputStream;
import java.io.FileOutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.BuildException;
import java.util.Hashtable;
import org.apache.tools.ant.Task;
import java.util.Iterator;
import java.io.File;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileUtils;

public class WebsphereDeploymentTool extends GenericDeploymentTool
{
    public static final String PUBLICID_EJB11 = "-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 1.1//EN";
    public static final String PUBLICID_EJB20 = "-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 2.0//EN";
    protected static final String SCHEMA_DIR = "Schema/";
    protected static final String WAS_EXT = "ibm-ejb-jar-ext.xmi";
    protected static final String WAS_BND = "ibm-ejb-jar-bnd.xmi";
    protected static final String WAS_CMP_MAP = "Map.mapxmi";
    protected static final String WAS_CMP_SCHEMA = "Schema.dbxmi";
    private static final FileUtils FILE_UTILS;
    private String jarSuffix;
    private String ejb11DTD;
    private boolean keepGeneric;
    private boolean alwaysRebuild;
    private boolean ejbdeploy;
    private boolean newCMP;
    private Path wasClasspath;
    private String dbVendor;
    private String dbName;
    private String dbSchema;
    private boolean codegen;
    private boolean quiet;
    private boolean novalidate;
    private boolean nowarn;
    private boolean noinform;
    private boolean trace;
    private String rmicOptions;
    private boolean use35MappingRules;
    private String tempdir;
    private File websphereHome;
    
    public WebsphereDeploymentTool() {
        this.jarSuffix = ".jar";
        this.keepGeneric = false;
        this.alwaysRebuild = true;
        this.ejbdeploy = true;
        this.newCMP = false;
        this.wasClasspath = null;
        this.quiet = true;
        this.tempdir = "_ejbdeploy_temp";
    }
    
    public Path createWASClasspath() {
        if (this.wasClasspath == null) {
            this.wasClasspath = new Path(this.getTask().getProject());
        }
        return this.wasClasspath.createPath();
    }
    
    public void setWASClasspath(final Path wasClasspath) {
        this.wasClasspath = wasClasspath;
    }
    
    public void setDbvendor(final String dbvendor) {
        this.dbVendor = dbvendor;
    }
    
    public void setDbname(final String dbName) {
        this.dbName = dbName;
    }
    
    public void setDbschema(final String dbSchema) {
        this.dbSchema = dbSchema;
    }
    
    public void setCodegen(final boolean codegen) {
        this.codegen = codegen;
    }
    
    public void setQuiet(final boolean quiet) {
        this.quiet = quiet;
    }
    
    public void setNovalidate(final boolean novalidate) {
        this.novalidate = novalidate;
    }
    
    public void setNowarn(final boolean nowarn) {
        this.nowarn = nowarn;
    }
    
    public void setNoinform(final boolean noinform) {
        this.noinform = noinform;
    }
    
    public void setTrace(final boolean trace) {
        this.trace = trace;
    }
    
    public void setRmicoptions(final String options) {
        this.rmicOptions = options;
    }
    
    public void setUse35(final boolean attr) {
        this.use35MappingRules = attr;
    }
    
    public void setRebuild(final boolean rebuild) {
        this.alwaysRebuild = rebuild;
    }
    
    public void setSuffix(final String inString) {
        this.jarSuffix = inString;
    }
    
    public void setKeepgeneric(final boolean inValue) {
        this.keepGeneric = inValue;
    }
    
    public void setEjbdeploy(final boolean ejbdeploy) {
        this.ejbdeploy = ejbdeploy;
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
    
    public void setTempdir(final String tempdir) {
        this.tempdir = tempdir;
    }
    
    @Override
    protected DescriptorHandler getDescriptorHandler(final File srcDir) {
        final DescriptorHandler handler = new DescriptorHandler(this.getTask(), srcDir);
        handler.registerDTD("-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 1.1//EN", this.ejb11DTD);
        for (final EjbJar.DTDLocation dtdLocation : this.getConfig().dtdLocations) {
            handler.registerDTD(dtdLocation.getPublicId(), dtdLocation.getLocation());
        }
        return handler;
    }
    
    protected DescriptorHandler getWebsphereDescriptorHandler(final File srcDir) {
        final DescriptorHandler handler = new DescriptorHandler(this.getTask(), srcDir) {
            @Override
            protected void processElement() {
            }
        };
        for (final EjbJar.DTDLocation dtdLocation : this.getConfig().dtdLocations) {
            handler.registerDTD(dtdLocation.getPublicId(), dtdLocation.getLocation());
        }
        return handler;
    }
    
    @Override
    protected void addVendorFiles(final Hashtable ejbFiles, final String baseName) {
        final String ddPrefix = this.usingBaseJarName() ? "" : baseName;
        final String dbPrefix = (this.dbVendor == null) ? "" : (this.dbVendor + "-");
        final File websphereEXT = new File(this.getConfig().descriptorDir, ddPrefix + "ibm-ejb-jar-ext.xmi");
        if (websphereEXT.exists()) {
            ejbFiles.put("META-INF/ibm-ejb-jar-ext.xmi", websphereEXT);
        }
        else {
            this.log("Unable to locate websphere extensions. It was expected to be in " + websphereEXT.getPath(), 3);
        }
        final File websphereBND = new File(this.getConfig().descriptorDir, ddPrefix + "ibm-ejb-jar-bnd.xmi");
        if (websphereBND.exists()) {
            ejbFiles.put("META-INF/ibm-ejb-jar-bnd.xmi", websphereBND);
        }
        else {
            this.log("Unable to locate websphere bindings. It was expected to be in " + websphereBND.getPath(), 3);
        }
        if (!this.newCMP) {
            this.log("The old method for locating CMP files has been DEPRECATED.", 3);
            this.log("Please adjust your websphere descriptor and set newCMP=\"true\" to use the new CMP descriptor inclusion mechanism. ", 3);
        }
        else {
            try {
                final File websphereMAP = new File(this.getConfig().descriptorDir, ddPrefix + dbPrefix + "Map.mapxmi");
                if (websphereMAP.exists()) {
                    ejbFiles.put("META-INF/Map.mapxmi", websphereMAP);
                }
                else {
                    this.log("Unable to locate the websphere Map: " + websphereMAP.getPath(), 3);
                }
                final File websphereSchema = new File(this.getConfig().descriptorDir, ddPrefix + dbPrefix + "Schema.dbxmi");
                if (websphereSchema.exists()) {
                    ejbFiles.put("META-INF/Schema/Schema.dbxmi", websphereSchema);
                }
                else {
                    this.log("Unable to locate the websphere Schema: " + websphereSchema.getPath(), 3);
                }
            }
            catch (Exception e) {
                final String msg = "Exception while adding Vendor specific files: " + e.toString();
                throw new BuildException(msg, e);
            }
        }
    }
    
    @Override
    File getVendorOutputJarFile(final String baseName) {
        return new File(this.getDestDir(), baseName + this.jarSuffix);
    }
    
    protected String getOptions() {
        final StringBuffer options = new StringBuffer();
        if (this.dbVendor != null) {
            options.append(" -dbvendor ").append(this.dbVendor);
        }
        if (this.dbName != null) {
            options.append(" -dbname \"").append(this.dbName).append("\"");
        }
        if (this.dbSchema != null) {
            options.append(" -dbschema \"").append(this.dbSchema).append("\"");
        }
        if (this.codegen) {
            options.append(" -codegen");
        }
        if (this.quiet) {
            options.append(" -quiet");
        }
        if (this.novalidate) {
            options.append(" -novalidate");
        }
        if (this.nowarn) {
            options.append(" -nowarn");
        }
        if (this.noinform) {
            options.append(" -noinform");
        }
        if (this.trace) {
            options.append(" -trace");
        }
        if (this.use35MappingRules) {
            options.append(" -35");
        }
        if (this.rmicOptions != null) {
            options.append(" -rmic \"").append(this.rmicOptions).append("\"");
        }
        return options.toString();
    }
    
    private void buildWebsphereJar(final File sourceJar, final File destJar) {
        try {
            if (this.ejbdeploy) {
                final Java javaTask = new Java(this.getTask());
                javaTask.createJvmarg().setValue("-Xms64m");
                javaTask.createJvmarg().setValue("-Xmx128m");
                final Environment.Variable var = new Environment.Variable();
                var.setKey("websphere.lib.dir");
                final File libdir = new File(this.websphereHome, "lib");
                var.setValue(libdir.getAbsolutePath());
                javaTask.addSysproperty(var);
                javaTask.setDir(this.websphereHome);
                javaTask.setTaskName("ejbdeploy");
                javaTask.setClassname("com.ibm.etools.ejbdeploy.EJBDeploy");
                javaTask.createArg().setValue(sourceJar.getPath());
                javaTask.createArg().setValue(this.tempdir);
                javaTask.createArg().setValue(destJar.getPath());
                javaTask.createArg().setLine(this.getOptions());
                if (this.getCombinedClasspath() != null && this.getCombinedClasspath().toString().length() > 0) {
                    javaTask.createArg().setValue("-cp");
                    javaTask.createArg().setValue(this.getCombinedClasspath().toString());
                }
                Path classpath = this.wasClasspath;
                if (classpath == null) {
                    classpath = this.getCombinedClasspath();
                }
                javaTask.setFork(true);
                if (classpath != null) {
                    javaTask.setClasspath(classpath);
                }
                this.log("Calling websphere.ejbdeploy for " + sourceJar.toString(), 3);
                javaTask.execute();
            }
        }
        catch (Exception e) {
            final String msg = "Exception while calling ejbdeploy. Details: " + e.toString();
            throw new BuildException(msg, e);
        }
    }
    
    @Override
    protected void writeJar(final String baseName, final File jarFile, final Hashtable files, final String publicId) throws BuildException {
        if (this.ejbdeploy) {
            final File genericJarFile = super.getVendorOutputJarFile(baseName);
            super.writeJar(baseName, genericJarFile, files, publicId);
            if (this.alwaysRebuild || this.isRebuildRequired(genericJarFile, jarFile)) {
                this.buildWebsphereJar(genericJarFile, jarFile);
            }
            if (!this.keepGeneric) {
                this.log("deleting generic jar " + genericJarFile.toString(), 3);
                genericJarFile.delete();
            }
        }
        else {
            super.writeJar(baseName, jarFile, files, publicId);
        }
    }
    
    @Override
    public void validateConfigured() throws BuildException {
        super.validateConfigured();
        if (this.ejbdeploy) {
            final String home = this.getTask().getProject().getProperty("websphere.home");
            if (home == null) {
                throw new BuildException("The 'websphere.home' property must be set when 'ejbdeploy=true'");
            }
            this.websphereHome = this.getTask().getProject().resolveFile(home);
        }
    }
    
    protected boolean isRebuildRequired(final File genericJarFile, final File websphereJarFile) {
        boolean rebuild = false;
        JarFile genericJar = null;
        JarFile wasJar = null;
        File newwasJarFile = null;
        JarOutputStream newJarStream = null;
        ClassLoader genericLoader = null;
        try {
            this.log("Checking if websphere Jar needs to be rebuilt for jar " + websphereJarFile.getName(), 3);
            if (genericJarFile.exists() && genericJarFile.isFile() && websphereJarFile.exists() && websphereJarFile.isFile()) {
                genericJar = new JarFile(genericJarFile);
                wasJar = new JarFile(websphereJarFile);
                final Hashtable genericEntries = new Hashtable();
                final Hashtable wasEntries = new Hashtable();
                final Hashtable replaceEntries = new Hashtable();
                Enumeration e = genericJar.entries();
                while (e.hasMoreElements()) {
                    final JarEntry je = e.nextElement();
                    genericEntries.put(je.getName().replace('\\', '/'), je);
                }
                e = wasJar.entries();
                while (e.hasMoreElements()) {
                    final JarEntry je = e.nextElement();
                    wasEntries.put(je.getName(), je);
                }
                genericLoader = this.getClassLoaderFromJar(genericJarFile);
                e = genericEntries.keys();
                while (e.hasMoreElements()) {
                    final String filepath = e.nextElement();
                    if (!wasEntries.containsKey(filepath)) {
                        this.log("File " + filepath + " not present in websphere jar", 3);
                        rebuild = true;
                        break;
                    }
                    final JarEntry genericEntry = genericEntries.get(filepath);
                    final JarEntry wasEntry = wasEntries.get(filepath);
                    if (genericEntry.getCrc() == wasEntry.getCrc() && genericEntry.getSize() == wasEntry.getSize()) {
                        continue;
                    }
                    if (genericEntry.getName().endsWith(".class")) {
                        String classname = genericEntry.getName().replace(File.separatorChar, '.');
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
                        break;
                    }
                }
                if (!rebuild) {
                    this.log("No rebuild needed - updating jar", 3);
                    newwasJarFile = new File(websphereJarFile.getAbsolutePath() + ".temp");
                    if (newwasJarFile.exists()) {
                        newwasJarFile.delete();
                    }
                    newJarStream = new JarOutputStream(new FileOutputStream(newwasJarFile));
                    newJarStream.setLevel(0);
                    e = wasEntries.elements();
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
                            is = wasJar.getInputStream(je2);
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
                    this.log("websphere Jar rebuild needed due to changed interface or XML", 3);
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
            if (wasJar != null) {
                try {
                    wasJar.close();
                }
                catch (IOException ex2) {}
            }
            if (newJarStream != null) {
                try {
                    newJarStream.close();
                }
                catch (IOException ex3) {}
                try {
                    WebsphereDeploymentTool.FILE_UTILS.rename(newwasJarFile, websphereJarFile);
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
