// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.ejb;

import java.util.Enumeration;
import java.io.FilenameFilter;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.util.jar.Manifest;
import java.util.HashSet;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.xml.sax.HandlerBase;
import java.io.InputStream;
import org.xml.sax.InputSource;
import java.util.Hashtable;
import org.xml.sax.SAXException;
import javax.xml.parsers.SAXParser;
import java.util.Iterator;
import org.apache.tools.ant.BuildException;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.io.FileInputStream;
import java.util.jar.JarOutputStream;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.util.depend.DependencyAnalyzer;
import java.util.Set;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import java.io.File;

public class GenericDeploymentTool implements EJBDeploymentTool
{
    public static final int DEFAULT_BUFFER_SIZE = 1024;
    public static final int JAR_COMPRESS_LEVEL = 9;
    protected static final String META_DIR = "META-INF/";
    protected static final String MANIFEST = "META-INF/MANIFEST.MF";
    protected static final String EJB_DD = "ejb-jar.xml";
    public static final String ANALYZER_SUPER = "super";
    public static final String ANALYZER_FULL = "full";
    public static final String ANALYZER_NONE = "none";
    public static final String DEFAULT_ANALYZER = "super";
    public static final String ANALYZER_CLASS_SUPER = "org.apache.tools.ant.util.depend.bcel.AncestorAnalyzer";
    public static final String ANALYZER_CLASS_FULL = "org.apache.tools.ant.util.depend.bcel.FullAnalyzer";
    private EjbJar.Config config;
    private File destDir;
    private Path classpath;
    private String genericJarSuffix;
    private Task task;
    private ClassLoader classpathLoader;
    private Set addedfiles;
    private DescriptorHandler handler;
    private DependencyAnalyzer dependencyAnalyzer;
    
    public GenericDeploymentTool() {
        this.genericJarSuffix = "-generic.jar";
        this.classpathLoader = null;
    }
    
    public void setDestdir(final File inDir) {
        this.destDir = inDir;
    }
    
    protected File getDestDir() {
        return this.destDir;
    }
    
    public void setTask(final Task task) {
        this.task = task;
    }
    
    protected Task getTask() {
        return this.task;
    }
    
    protected EjbJar.Config getConfig() {
        return this.config;
    }
    
    protected boolean usingBaseJarName() {
        return this.config.baseJarName != null;
    }
    
    public void setGenericJarSuffix(final String inString) {
        this.genericJarSuffix = inString;
    }
    
    public Path createClasspath() {
        if (this.classpath == null) {
            this.classpath = new Path(this.task.getProject());
        }
        return this.classpath.createPath();
    }
    
    public void setClasspath(final Path classpath) {
        this.classpath = classpath;
    }
    
    protected Path getCombinedClasspath() {
        Path combinedPath = this.classpath;
        if (this.config.classpath != null) {
            if (combinedPath == null) {
                combinedPath = this.config.classpath;
            }
            else {
                combinedPath.append(this.config.classpath);
            }
        }
        return combinedPath;
    }
    
    protected void log(final String message, final int level) {
        this.getTask().log(message, level);
    }
    
    protected Location getLocation() {
        return this.getTask().getLocation();
    }
    
    private void createAnalyzer() {
        String analyzer = this.config.analyzer;
        if (analyzer == null) {
            analyzer = "super";
        }
        if (analyzer.equals("none")) {
            return;
        }
        String analyzerClassName = null;
        if (analyzer.equals("super")) {
            analyzerClassName = "org.apache.tools.ant.util.depend.bcel.AncestorAnalyzer";
        }
        else if (analyzer.equals("full")) {
            analyzerClassName = "org.apache.tools.ant.util.depend.bcel.FullAnalyzer";
        }
        else {
            analyzerClassName = analyzer;
        }
        try {
            final Class analyzerClass = Class.forName(analyzerClassName);
            (this.dependencyAnalyzer = analyzerClass.newInstance()).addClassPath(new Path(this.task.getProject(), this.config.srcDir.getPath()));
            this.dependencyAnalyzer.addClassPath(this.config.classpath);
        }
        catch (NoClassDefFoundError e) {
            this.dependencyAnalyzer = null;
            this.task.log("Unable to load dependency analyzer: " + analyzerClassName + " - dependent class not found: " + e.getMessage(), 1);
        }
        catch (Exception e2) {
            this.dependencyAnalyzer = null;
            this.task.log("Unable to load dependency analyzer: " + analyzerClassName + " - exception: " + e2.getMessage(), 1);
        }
    }
    
    public void configure(final EjbJar.Config config) {
        this.config = config;
        this.createAnalyzer();
        this.classpathLoader = null;
    }
    
    protected void addFileToJar(final JarOutputStream jStream, final File inputFile, final String logicalFilename) throws BuildException {
        FileInputStream iStream = null;
        try {
            if (!this.addedfiles.contains(logicalFilename)) {
                iStream = new FileInputStream(inputFile);
                final ZipEntry zipEntry = new ZipEntry(logicalFilename.replace('\\', '/'));
                jStream.putNextEntry(zipEntry);
                final byte[] byteBuffer = new byte[2048];
                int count = 0;
                do {
                    jStream.write(byteBuffer, 0, count);
                    count = iStream.read(byteBuffer, 0, byteBuffer.length);
                } while (count != -1);
                this.addedfiles.add(logicalFilename);
            }
        }
        catch (IOException ioe) {
            this.log("WARNING: IOException while adding entry " + logicalFilename + " to jarfile from " + inputFile.getPath() + " " + ioe.getClass().getName() + "-" + ioe.getMessage(), 1);
        }
        finally {
            if (iStream != null) {
                try {
                    iStream.close();
                }
                catch (IOException ex) {}
            }
        }
    }
    
    protected DescriptorHandler getDescriptorHandler(final File srcDir) {
        final DescriptorHandler h = new DescriptorHandler(this.getTask(), srcDir);
        this.registerKnownDTDs(h);
        for (final EjbJar.DTDLocation dtdLocation : this.getConfig().dtdLocations) {
            h.registerDTD(dtdLocation.getPublicId(), dtdLocation.getLocation());
        }
        return h;
    }
    
    protected void registerKnownDTDs(final DescriptorHandler handler) {
    }
    
    public void processDescriptor(final String descriptorFileName, final SAXParser saxParser) {
        this.checkConfiguration(descriptorFileName, saxParser);
        try {
            this.handler = this.getDescriptorHandler(this.config.srcDir);
            final Hashtable ejbFiles = this.parseEjbFiles(descriptorFileName, saxParser);
            this.addSupportClasses(ejbFiles);
            String baseName = this.getJarBaseName(descriptorFileName);
            final String ddPrefix = this.getVendorDDPrefix(baseName, descriptorFileName);
            final File manifestFile = this.getManifestFile(ddPrefix);
            if (manifestFile != null) {
                ejbFiles.put("META-INF/MANIFEST.MF", manifestFile);
            }
            ejbFiles.put("META-INF/ejb-jar.xml", new File(this.config.descriptorDir, descriptorFileName));
            this.addVendorFiles(ejbFiles, ddPrefix);
            this.checkAndAddDependants(ejbFiles);
            if (this.config.flatDestDir && baseName.length() != 0) {
                int startName = baseName.lastIndexOf(File.separator);
                if (startName == -1) {
                    startName = 0;
                }
                final int endName = baseName.length();
                baseName = baseName.substring(startName, endName);
            }
            final File jarFile = this.getVendorOutputJarFile(baseName);
            if (this.needToRebuild(ejbFiles, jarFile)) {
                this.log("building " + jarFile.getName() + " with " + String.valueOf(ejbFiles.size()) + " files", 2);
                final String publicId = this.getPublicId();
                this.writeJar(baseName, jarFile, ejbFiles, publicId);
            }
            else {
                this.log(jarFile.toString() + " is up to date.", 3);
            }
        }
        catch (SAXException se) {
            final String msg = "SAXException while parsing '" + descriptorFileName + "'. This probably indicates badly-formed XML." + "  Details: " + se.getMessage();
            throw new BuildException(msg, se);
        }
        catch (IOException ioe) {
            final String msg = "IOException while parsing'" + descriptorFileName + "'.  This probably indicates that the descriptor" + " doesn't exist. Details: " + ioe.getMessage();
            throw new BuildException(msg, ioe);
        }
    }
    
    protected void checkConfiguration(final String descriptorFileName, final SAXParser saxParser) throws BuildException {
    }
    
    protected Hashtable parseEjbFiles(final String descriptorFileName, final SAXParser saxParser) throws IOException, SAXException {
        FileInputStream descriptorStream = null;
        Hashtable ejbFiles = null;
        try {
            descriptorStream = new FileInputStream(new File(this.config.descriptorDir, descriptorFileName));
            saxParser.parse(new InputSource(descriptorStream), this.handler);
            ejbFiles = this.handler.getFiles();
        }
        finally {
            if (descriptorStream != null) {
                try {
                    descriptorStream.close();
                }
                catch (IOException ex) {}
            }
        }
        return ejbFiles;
    }
    
    protected void addSupportClasses(final Hashtable ejbFiles) {
        final Project project = this.task.getProject();
        for (final FileSet supportFileSet : this.config.supportFileSets) {
            final File supportBaseDir = supportFileSet.getDir(project);
            final DirectoryScanner supportScanner = supportFileSet.getDirectoryScanner(project);
            supportScanner.scan();
            final String[] supportFiles = supportScanner.getIncludedFiles();
            for (int j = 0; j < supportFiles.length; ++j) {
                ejbFiles.put(supportFiles[j], new File(supportBaseDir, supportFiles[j]));
            }
        }
    }
    
    protected String getJarBaseName(final String descriptorFileName) {
        String baseName = "";
        if (this.config.namingScheme.getValue().equals("basejarname")) {
            final String canonicalDescriptor = descriptorFileName.replace('\\', '/');
            final int index = canonicalDescriptor.lastIndexOf(47);
            if (index != -1) {
                baseName = descriptorFileName.substring(0, index + 1);
            }
            baseName += this.config.baseJarName;
        }
        else if (this.config.namingScheme.getValue().equals("descriptor")) {
            final int lastSeparatorIndex = descriptorFileName.lastIndexOf(File.separator);
            int endBaseName = -1;
            if (lastSeparatorIndex != -1) {
                endBaseName = descriptorFileName.indexOf(this.config.baseNameTerminator, lastSeparatorIndex);
            }
            else {
                endBaseName = descriptorFileName.indexOf(this.config.baseNameTerminator);
            }
            if (endBaseName == -1) {
                throw new BuildException("Unable to determine jar name from descriptor \"" + descriptorFileName + "\"");
            }
            baseName = descriptorFileName.substring(0, endBaseName);
        }
        else if (this.config.namingScheme.getValue().equals("directory")) {
            final File descriptorFile = new File(this.config.descriptorDir, descriptorFileName);
            final String path = descriptorFile.getAbsolutePath();
            final int lastSeparatorIndex2 = path.lastIndexOf(File.separator);
            if (lastSeparatorIndex2 == -1) {
                throw new BuildException("Unable to determine directory name holding descriptor");
            }
            String dirName = path.substring(0, lastSeparatorIndex2);
            final int dirSeparatorIndex = dirName.lastIndexOf(File.separator);
            if (dirSeparatorIndex != -1) {
                dirName = dirName.substring(dirSeparatorIndex + 1);
            }
            baseName = dirName;
        }
        else if (this.config.namingScheme.getValue().equals("ejb-name")) {
            baseName = this.handler.getEjbName();
        }
        return baseName;
    }
    
    public String getVendorDDPrefix(final String baseName, final String descriptorFileName) {
        String ddPrefix = null;
        if (this.config.namingScheme.getValue().equals("descriptor")) {
            ddPrefix = baseName + this.config.baseNameTerminator;
        }
        else if (this.config.namingScheme.getValue().equals("basejarname") || this.config.namingScheme.getValue().equals("ejb-name") || this.config.namingScheme.getValue().equals("directory")) {
            final String canonicalDescriptor = descriptorFileName.replace('\\', '/');
            final int index = canonicalDescriptor.lastIndexOf(47);
            if (index == -1) {
                ddPrefix = "";
            }
            else {
                ddPrefix = descriptorFileName.substring(0, index + 1);
            }
        }
        return ddPrefix;
    }
    
    protected void addVendorFiles(final Hashtable ejbFiles, final String ddPrefix) {
    }
    
    File getVendorOutputJarFile(final String baseName) {
        return new File(this.destDir, baseName + this.genericJarSuffix);
    }
    
    protected boolean needToRebuild(final Hashtable ejbFiles, final File jarFile) {
        if (jarFile.exists()) {
            final long lastBuild = jarFile.lastModified();
            for (final File currentFile : ejbFiles.values()) {
                if (lastBuild < currentFile.lastModified()) {
                    this.log("Build needed because " + currentFile.getPath() + " is out of date", 3);
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    protected String getPublicId() {
        return this.handler.getPublicId();
    }
    
    protected File getManifestFile(final String prefix) {
        final File manifestFile = new File(this.getConfig().descriptorDir, prefix + "manifest.mf");
        if (manifestFile.exists()) {
            return manifestFile;
        }
        if (this.config.manifest != null) {
            return this.config.manifest;
        }
        return null;
    }
    
    protected void writeJar(final String baseName, final File jarfile, final Hashtable files, final String publicId) throws BuildException {
        JarOutputStream jarStream = null;
        try {
            if (this.addedfiles == null) {
                this.addedfiles = new HashSet();
            }
            else {
                this.addedfiles.clear();
            }
            if (jarfile.exists()) {
                jarfile.delete();
            }
            jarfile.getParentFile().mkdirs();
            jarfile.createNewFile();
            InputStream in = null;
            Manifest manifest = null;
            try {
                final File manifestFile = files.get("META-INF/MANIFEST.MF");
                if (manifestFile != null && manifestFile.exists()) {
                    in = new FileInputStream(manifestFile);
                }
                else {
                    final String defaultManifest = "/org/apache/tools/ant/defaultManifest.mf";
                    in = this.getClass().getResourceAsStream(defaultManifest);
                    if (in == null) {
                        throw new BuildException("Could not find default manifest: " + defaultManifest);
                    }
                }
                manifest = new Manifest(in);
            }
            catch (IOException e) {
                throw new BuildException("Unable to read manifest", e, this.getLocation());
            }
            finally {
                if (in != null) {
                    in.close();
                }
            }
            jarStream = new JarOutputStream(new FileOutputStream(jarfile), manifest);
            jarStream.setMethod(8);
            for (String entryName : files.keySet()) {
                if (entryName.equals("META-INF/MANIFEST.MF")) {
                    continue;
                }
                File entryFile = files.get(entryName);
                this.log("adding file '" + entryName + "'", 3);
                this.addFileToJar(jarStream, entryFile, entryName);
                final InnerClassFilenameFilter flt = new InnerClassFilenameFilter(entryFile.getName());
                final File entryDir = entryFile.getParentFile();
                final String[] innerfiles = entryDir.list(flt);
                if (innerfiles == null) {
                    continue;
                }
                for (int i = 0, n = innerfiles.length; i < n; ++i) {
                    final int entryIndex = entryName.lastIndexOf(entryFile.getName()) - 1;
                    if (entryIndex < 0) {
                        entryName = innerfiles[i];
                    }
                    else {
                        entryName = entryName.substring(0, entryIndex) + File.separatorChar + innerfiles[i];
                    }
                    entryFile = new File(this.config.srcDir, entryName);
                    this.log("adding innerclass file '" + entryName + "'", 3);
                    this.addFileToJar(jarStream, entryFile, entryName);
                }
            }
        }
        catch (IOException ioe) {
            final String msg = "IOException while processing ejb-jar file '" + jarfile.toString() + "'. Details: " + ioe.getMessage();
            throw new BuildException(msg, ioe);
        }
        finally {
            if (jarStream != null) {
                try {
                    jarStream.close();
                }
                catch (IOException ex) {}
            }
        }
    }
    
    protected void checkAndAddDependants(final Hashtable checkEntries) throws BuildException {
        if (this.dependencyAnalyzer == null) {
            return;
        }
        this.dependencyAnalyzer.reset();
        for (final String entryName : checkEntries.keySet()) {
            if (entryName.endsWith(".class")) {
                String className = entryName.substring(0, entryName.length() - ".class".length());
                className = className.replace(File.separatorChar, '/');
                className = className.replace('/', '.');
                this.dependencyAnalyzer.addRootClass(className);
            }
        }
        final Enumeration e = this.dependencyAnalyzer.getClassDependencies();
        while (e.hasMoreElements()) {
            final String classname = e.nextElement();
            final String location = classname.replace('.', File.separatorChar) + ".class";
            final File classFile = new File(this.config.srcDir, location);
            if (classFile.exists()) {
                checkEntries.put(location, classFile);
                this.log("dependent class: " + classname + " - " + classFile, 3);
            }
        }
    }
    
    protected ClassLoader getClassLoaderForBuild() {
        if (this.classpathLoader != null) {
            return this.classpathLoader;
        }
        final Path combinedClasspath = this.getCombinedClasspath();
        if (combinedClasspath == null) {
            this.classpathLoader = this.getClass().getClassLoader();
        }
        else {
            this.classpathLoader = this.getTask().getProject().createClassLoader(combinedClasspath);
        }
        return this.classpathLoader;
    }
    
    public void validateConfigured() throws BuildException {
        if (this.destDir == null || !this.destDir.isDirectory()) {
            final String msg = "A valid destination directory must be specified using the \"destdir\" attribute.";
            throw new BuildException(msg, this.getLocation());
        }
    }
}
