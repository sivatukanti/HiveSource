// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.depend;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import java.net.URL;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.util.depend.DependencyAnalyzer;
import java.util.Enumeration;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.tools.ant.util.FileUtils;
import java.util.Vector;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import org.apache.tools.ant.types.Reference;
import java.util.Hashtable;
import java.io.File;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.taskdefs.MatchingTask;

public class Depend extends MatchingTask
{
    private static final int ONE_SECOND = 1000;
    private Path srcPath;
    private Path destPath;
    private File cache;
    private String[] srcPathList;
    private Hashtable affectedClassMap;
    private Hashtable classFileInfoMap;
    private Hashtable classpathDependencies;
    private Hashtable outOfDateClasses;
    private boolean closure;
    private boolean warnOnRmiStubs;
    private boolean dump;
    private Path dependClasspath;
    private static final String CACHE_FILE_NAME = "dependencies.txt";
    private static final String CLASSNAME_PREPEND = "||:";
    
    public Depend() {
        this.closure = false;
        this.warnOnRmiStubs = true;
        this.dump = false;
    }
    
    public void setClasspath(final Path classpath) {
        if (this.dependClasspath == null) {
            this.dependClasspath = classpath;
        }
        else {
            this.dependClasspath.append(classpath);
        }
    }
    
    public Path getClasspath() {
        return this.dependClasspath;
    }
    
    public Path createClasspath() {
        if (this.dependClasspath == null) {
            this.dependClasspath = new Path(this.getProject());
        }
        return this.dependClasspath.createPath();
    }
    
    public void setClasspathRef(final Reference r) {
        this.createClasspath().setRefid(r);
    }
    
    public void setWarnOnRmiStubs(final boolean warnOnRmiStubs) {
        this.warnOnRmiStubs = warnOnRmiStubs;
    }
    
    private Hashtable readCachedDependencies(final File depFile) throws IOException {
        final Hashtable dependencyMap = new Hashtable();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(depFile));
            String line = null;
            Vector dependencyList = null;
            String className = null;
            final int prependLength = "||:".length();
            while ((line = in.readLine()) != null) {
                if (line.startsWith("||:")) {
                    dependencyList = new Vector();
                    className = line.substring(prependLength);
                    dependencyMap.put(className, dependencyList);
                }
                else {
                    dependencyList.addElement(line);
                }
            }
        }
        finally {
            FileUtils.close(in);
        }
        return dependencyMap;
    }
    
    private void writeCachedDependencies(final Hashtable dependencyMap) throws IOException {
        if (this.cache != null) {
            BufferedWriter pw = null;
            try {
                this.cache.mkdirs();
                final File depFile = new File(this.cache, "dependencies.txt");
                pw = new BufferedWriter(new FileWriter(depFile));
                final Enumeration e = dependencyMap.keys();
                while (e.hasMoreElements()) {
                    final String className = e.nextElement();
                    pw.write("||:" + className);
                    pw.newLine();
                    final Vector dependencyList = dependencyMap.get(className);
                    for (int size = dependencyList.size(), x = 0; x < size; ++x) {
                        pw.write(String.valueOf(dependencyList.elementAt(x)));
                        pw.newLine();
                    }
                }
            }
            finally {
                FileUtils.close(pw);
            }
        }
    }
    
    private Path getCheckClassPath() {
        if (this.dependClasspath == null) {
            return null;
        }
        final String[] destPathElements = this.destPath.list();
        final String[] classpathElements = this.dependClasspath.list();
        String checkPath = "";
        for (int i = 0; i < classpathElements.length; ++i) {
            final String element = classpathElements[i];
            boolean inDestPath = false;
            for (int j = 0; j < destPathElements.length && !inDestPath; inDestPath = destPathElements[j].equals(element), ++j) {}
            if (!inDestPath) {
                if (checkPath.length() == 0) {
                    checkPath = element;
                }
                else {
                    checkPath = checkPath + ":" + element;
                }
            }
        }
        Path p = null;
        if (checkPath.length() > 0) {
            p = new Path(this.getProject(), checkPath);
        }
        this.log("Classpath without dest dir is " + p, 4);
        return p;
    }
    
    private void determineDependencies() throws IOException {
        this.affectedClassMap = new Hashtable();
        this.classFileInfoMap = new Hashtable();
        boolean cacheDirty = false;
        Hashtable dependencyMap = new Hashtable();
        File cacheFile = null;
        boolean cacheFileExists = true;
        long cacheLastModified = Long.MAX_VALUE;
        if (this.cache != null) {
            cacheFile = new File(this.cache, "dependencies.txt");
            cacheFileExists = cacheFile.exists();
            cacheLastModified = cacheFile.lastModified();
            if (cacheFileExists) {
                dependencyMap = this.readCachedDependencies(cacheFile);
            }
        }
        final Enumeration classfileEnum = this.getClassFiles(this.destPath).elements();
        while (classfileEnum.hasMoreElements()) {
            final ClassFileInfo info = classfileEnum.nextElement();
            this.log("Adding class info for " + info.className, 4);
            this.classFileInfoMap.put(info.className, info);
            Vector dependencyList = null;
            if (this.cache != null && cacheFileExists && cacheLastModified > info.absoluteFile.lastModified()) {
                dependencyList = dependencyMap.get(info.className);
            }
            if (dependencyList == null) {
                final DependencyAnalyzer analyzer = new AntAnalyzer();
                analyzer.addRootClass(info.className);
                analyzer.addClassPath(this.destPath);
                analyzer.setClosure(false);
                dependencyList = new Vector();
                final Enumeration depEnum = analyzer.getClassDependencies();
                while (depEnum.hasMoreElements()) {
                    final Object o = depEnum.nextElement();
                    dependencyList.addElement(o);
                    this.log("Class " + info.className + " depends on " + o, 4);
                }
                cacheDirty = true;
                dependencyMap.put(info.className, dependencyList);
            }
            final Enumeration depEnum2 = dependencyList.elements();
            while (depEnum2.hasMoreElements()) {
                final String dependentClass = depEnum2.nextElement();
                Hashtable affectedClasses = this.affectedClassMap.get(dependentClass);
                if (affectedClasses == null) {
                    affectedClasses = new Hashtable();
                    this.affectedClassMap.put(dependentClass, affectedClasses);
                }
                affectedClasses.put(info.className, info);
                this.log(dependentClass + " affects " + info.className, 4);
            }
        }
        this.classpathDependencies = null;
        final Path checkPath = this.getCheckClassPath();
        if (checkPath != null) {
            this.classpathDependencies = new Hashtable();
            AntClassLoader loader = null;
            try {
                loader = this.getProject().createClassLoader(checkPath);
                final Hashtable classpathFileCache = new Hashtable();
                final Object nullFileMarker = new Object();
                final Enumeration e = dependencyMap.keys();
                while (e.hasMoreElements()) {
                    final String className = e.nextElement();
                    this.log("Determining classpath dependencies for " + className, 4);
                    final Vector dependencyList2 = dependencyMap.get(className);
                    final Hashtable dependencies = new Hashtable();
                    this.classpathDependencies.put(className, dependencies);
                    final Enumeration e2 = dependencyList2.elements();
                    while (e2.hasMoreElements()) {
                        final String dependency = e2.nextElement();
                        this.log("Looking for " + dependency, 4);
                        Object classpathFileObject = classpathFileCache.get(dependency);
                        if (classpathFileObject == null) {
                            classpathFileObject = nullFileMarker;
                            if (!dependency.startsWith("java.") && !dependency.startsWith("javax.")) {
                                final URL classURL = loader.getResource(dependency.replace('.', '/') + ".class");
                                this.log("URL is " + classURL, 4);
                                if (classURL != null) {
                                    if (classURL.getProtocol().equals("jar")) {
                                        String jarFilePath = classURL.getFile();
                                        final int classMarker = jarFilePath.indexOf(33);
                                        jarFilePath = jarFilePath.substring(0, classMarker);
                                        if (!jarFilePath.startsWith("file:")) {
                                            throw new IOException("Bizarre nested path in jar: protocol: " + jarFilePath);
                                        }
                                        classpathFileObject = new File(FileUtils.getFileUtils().fromURI(jarFilePath));
                                    }
                                    else if (classURL.getProtocol().equals("file")) {
                                        classpathFileObject = new File(FileUtils.getFileUtils().fromURI(classURL.toExternalForm()));
                                    }
                                    this.log("Class " + className + " depends on " + classpathFileObject + " due to " + dependency, 4);
                                }
                            }
                            else {
                                this.log("Ignoring base classlib dependency " + dependency, 4);
                            }
                            classpathFileCache.put(dependency, classpathFileObject);
                        }
                        if (classpathFileObject != nullFileMarker) {
                            final File jarFile = (File)classpathFileObject;
                            this.log("Adding a classpath dependency on " + jarFile, 4);
                            dependencies.put(jarFile, jarFile);
                        }
                    }
                }
            }
            finally {
                if (loader != null) {
                    loader.cleanup();
                }
            }
        }
        else {
            this.log("No classpath to check", 4);
        }
        if (this.cache != null && cacheDirty) {
            this.writeCachedDependencies(dependencyMap);
        }
    }
    
    private int deleteAllAffectedFiles() {
        int count = 0;
        final Enumeration e = this.outOfDateClasses.elements();
        while (e.hasMoreElements()) {
            final String className = e.nextElement();
            count += this.deleteAffectedFiles(className);
            final ClassFileInfo classInfo = this.classFileInfoMap.get(className);
            if (classInfo != null && classInfo.absoluteFile.exists()) {
                if (classInfo.sourceFile == null) {
                    this.warnOutOfDateButNotDeleted(classInfo, className, className);
                }
                else {
                    classInfo.absoluteFile.delete();
                    ++count;
                }
            }
        }
        return count;
    }
    
    private int deleteAffectedFiles(final String className) {
        int count = 0;
        final Hashtable affectedClasses = this.affectedClassMap.get(className);
        if (affectedClasses == null) {
            return count;
        }
        final Enumeration e = affectedClasses.keys();
        while (e.hasMoreElements()) {
            final String affectedClass = e.nextElement();
            final ClassFileInfo affectedClassInfo = affectedClasses.get(affectedClass);
            if (!affectedClassInfo.absoluteFile.exists()) {
                continue;
            }
            if (affectedClassInfo.sourceFile == null) {
                this.warnOutOfDateButNotDeleted(affectedClassInfo, affectedClass, className);
            }
            else {
                this.log("Deleting file " + affectedClassInfo.absoluteFile.getPath() + " since " + className + " out of date", 3);
                affectedClassInfo.absoluteFile.delete();
                ++count;
                if (this.closure) {
                    count += this.deleteAffectedFiles(affectedClass);
                }
                else {
                    if (affectedClass.indexOf("$") == -1) {
                        continue;
                    }
                    final String topLevelClassName = affectedClass.substring(0, affectedClass.indexOf("$"));
                    this.log("Top level class = " + topLevelClassName, 3);
                    final ClassFileInfo topLevelClassInfo = this.classFileInfoMap.get(topLevelClassName);
                    if (topLevelClassInfo == null || !topLevelClassInfo.absoluteFile.exists()) {
                        continue;
                    }
                    this.log("Deleting file " + topLevelClassInfo.absoluteFile.getPath() + " since one of its inner classes was removed", 3);
                    topLevelClassInfo.absoluteFile.delete();
                    ++count;
                    if (!this.closure) {
                        continue;
                    }
                    count += this.deleteAffectedFiles(topLevelClassName);
                }
            }
        }
        return count;
    }
    
    private void warnOutOfDateButNotDeleted(final ClassFileInfo affectedClassInfo, final String affectedClass, final String className) {
        if (affectedClassInfo.isUserWarned) {
            return;
        }
        int level = 1;
        if (!this.warnOnRmiStubs && this.isRmiStub(affectedClass, className)) {
            level = 3;
        }
        this.log("The class " + affectedClass + " in file " + affectedClassInfo.absoluteFile.getPath() + " is out of date due to " + className + " but has not been deleted because its source file" + " could not be determined", level);
        affectedClassInfo.isUserWarned = true;
    }
    
    private boolean isRmiStub(final String affectedClass, final String className) {
        return this.isStub(affectedClass, className, "_Stub") || this.isStub(affectedClass, className, "_Skel") || this.isStub(affectedClass, className, "_Stub") || this.isStub(affectedClass, className, "_Skel");
    }
    
    private boolean isStub(final String affectedClass, final String baseClass, final String suffix) {
        return (baseClass + suffix).equals(affectedClass);
    }
    
    private void dumpDependencies() {
        this.log("Reverse Dependency Dump for " + this.affectedClassMap.size() + " classes:", 4);
        final Enumeration classEnum = this.affectedClassMap.keys();
        while (classEnum.hasMoreElements()) {
            final String className = classEnum.nextElement();
            this.log(" Class " + className + " affects:", 4);
            final Hashtable affectedClasses = this.affectedClassMap.get(className);
            final Enumeration affectedClassEnum = affectedClasses.keys();
            while (affectedClassEnum.hasMoreElements()) {
                final String affectedClass = affectedClassEnum.nextElement();
                final ClassFileInfo info = affectedClasses.get(affectedClass);
                this.log("    " + affectedClass + " in " + info.absoluteFile.getPath(), 4);
            }
        }
        if (this.classpathDependencies != null) {
            this.log("Classpath file dependencies (Forward):", 4);
            final Enumeration classpathEnum = this.classpathDependencies.keys();
            while (classpathEnum.hasMoreElements()) {
                final String className2 = classpathEnum.nextElement();
                this.log(" Class " + className2 + " depends on:", 4);
                final Hashtable dependencies = this.classpathDependencies.get(className2);
                final Enumeration classpathFileEnum = dependencies.elements();
                while (classpathFileEnum.hasMoreElements()) {
                    final File classpathFile = classpathFileEnum.nextElement();
                    this.log("    " + classpathFile.getPath(), 4);
                }
            }
        }
    }
    
    private void determineOutOfDateClasses() {
        this.outOfDateClasses = new Hashtable();
        for (int i = 0; i < this.srcPathList.length; ++i) {
            final File srcDir = this.getProject().resolveFile(this.srcPathList[i]);
            if (srcDir.exists()) {
                final DirectoryScanner ds = this.getDirectoryScanner(srcDir);
                final String[] files = ds.getIncludedFiles();
                this.scanDir(srcDir, files);
            }
        }
        if (this.classpathDependencies == null) {
            return;
        }
        final Enumeration classpathDepsEnum = this.classpathDependencies.keys();
        while (classpathDepsEnum.hasMoreElements()) {
            final String className = classpathDepsEnum.nextElement();
            if (this.outOfDateClasses.containsKey(className)) {
                continue;
            }
            final ClassFileInfo info = this.classFileInfoMap.get(className);
            if (info == null) {
                continue;
            }
            final Hashtable dependencies = this.classpathDependencies.get(className);
            final Enumeration e2 = dependencies.elements();
            while (e2.hasMoreElements()) {
                final File classpathFile = e2.nextElement();
                if (classpathFile.lastModified() > info.absoluteFile.lastModified()) {
                    this.log("Class " + className + " is out of date with respect to " + classpathFile, 4);
                    this.outOfDateClasses.put(className, className);
                    break;
                }
            }
        }
    }
    
    @Override
    public void execute() throws BuildException {
        try {
            final long start = System.currentTimeMillis();
            if (this.srcPath == null) {
                throw new BuildException("srcdir attribute must be set", this.getLocation());
            }
            this.srcPathList = this.srcPath.list();
            if (this.srcPathList.length == 0) {
                throw new BuildException("srcdir attribute must be non-empty", this.getLocation());
            }
            if (this.destPath == null) {
                this.destPath = this.srcPath;
            }
            if (this.cache != null && this.cache.exists() && !this.cache.isDirectory()) {
                throw new BuildException("The cache, if specified, must point to a directory");
            }
            if (this.cache != null && !this.cache.exists()) {
                this.cache.mkdirs();
            }
            this.determineDependencies();
            if (this.dump) {
                this.dumpDependencies();
            }
            this.determineOutOfDateClasses();
            final int count = this.deleteAllAffectedFiles();
            final long duration = (System.currentTimeMillis() - start) / 1000L;
            int summaryLogLevel;
            if (count > 0) {
                summaryLogLevel = 2;
            }
            else {
                summaryLogLevel = 4;
            }
            this.log("Deleted " + count + " out of date files in " + duration + " seconds", summaryLogLevel);
        }
        catch (Exception e) {
            throw new BuildException(e);
        }
    }
    
    protected void scanDir(final File srcDir, final String[] files) {
        for (int i = 0; i < files.length; ++i) {
            final File srcFile = new File(srcDir, files[i]);
            if (files[i].endsWith(".java")) {
                final String filePath = srcFile.getPath();
                String className = filePath.substring(srcDir.getPath().length() + 1, filePath.length() - ".java".length());
                className = ClassFileUtils.convertSlashName(className);
                final ClassFileInfo info = this.classFileInfoMap.get(className);
                if (info == null) {
                    this.outOfDateClasses.put(className, className);
                }
                else if (srcFile.lastModified() > info.absoluteFile.lastModified()) {
                    this.outOfDateClasses.put(className, className);
                }
            }
        }
    }
    
    private Vector getClassFiles(final Path classLocations) {
        final String[] classLocationsList = classLocations.list();
        final Vector classFileList = new Vector();
        for (int i = 0; i < classLocationsList.length; ++i) {
            final File dir = new File(classLocationsList[i]);
            if (dir.isDirectory()) {
                this.addClassFiles(classFileList, dir, dir);
            }
        }
        return classFileList;
    }
    
    private File findSourceFile(final String classname, final File sourceFileKnownToExist) {
        final int innerIndex = classname.indexOf("$");
        String sourceFilename;
        if (innerIndex != -1) {
            sourceFilename = classname.substring(0, innerIndex) + ".java";
        }
        else {
            sourceFilename = classname + ".java";
        }
        for (int i = 0; i < this.srcPathList.length; ++i) {
            final File sourceFile = new File(this.srcPathList[i], sourceFilename);
            if (sourceFile.equals(sourceFileKnownToExist) || sourceFile.exists()) {
                return sourceFile;
            }
        }
        return null;
    }
    
    private void addClassFiles(final Vector classFileList, final File dir, final File root) {
        final String[] filesInDir = dir.list();
        if (filesInDir == null) {
            return;
        }
        final int length = filesInDir.length;
        final int rootLength = root.getPath().length();
        File sourceFileKnownToExist = null;
        for (int i = 0; i < length; ++i) {
            final File file = new File(dir, filesInDir[i]);
            if (filesInDir[i].endsWith(".class")) {
                final ClassFileInfo info = new ClassFileInfo();
                info.absoluteFile = file;
                final String relativeName = file.getPath().substring(rootLength + 1, file.getPath().length() - ".class".length());
                info.className = ClassFileUtils.convertSlashName(relativeName);
                info.sourceFile = (sourceFileKnownToExist = this.findSourceFile(relativeName, sourceFileKnownToExist));
                classFileList.addElement(info);
            }
            else {
                this.addClassFiles(classFileList, file, root);
            }
        }
    }
    
    public void setSrcdir(final Path srcPath) {
        this.srcPath = srcPath;
    }
    
    public void setDestDir(final Path destPath) {
        this.destPath = destPath;
    }
    
    public void setCache(final File cache) {
        this.cache = cache;
    }
    
    public void setClosure(final boolean closure) {
        this.closure = closure;
    }
    
    public void setDump(final boolean dump) {
        this.dump = dump;
    }
    
    private static class ClassFileInfo
    {
        private File absoluteFile;
        private String className;
        private File sourceFile;
        private boolean isUserWarned;
        
        private ClassFileInfo() {
            this.isUserWarned = false;
        }
    }
}
