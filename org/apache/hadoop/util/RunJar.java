// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.slf4j.LoggerFactory;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Method;
import java.util.jar.Manifest;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import org.apache.hadoop.fs.FileUtil;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.jar.JarFile;
import org.apache.commons.io.input.TeeInputStream;
import java.util.jar.JarEntry;
import java.io.OutputStream;
import org.apache.hadoop.io.IOUtils;
import java.io.FileOutputStream;
import java.util.jar.JarInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class RunJar
{
    private static final Logger LOG;
    public static final Pattern MATCH_ANY;
    public static final int SHUTDOWN_HOOK_PRIORITY = 10;
    public static final String HADOOP_USE_CLIENT_CLASSLOADER = "HADOOP_USE_CLIENT_CLASSLOADER";
    public static final String HADOOP_CLASSPATH = "HADOOP_CLASSPATH";
    public static final String HADOOP_CLIENT_CLASSLOADER_SYSTEM_CLASSES = "HADOOP_CLIENT_CLASSLOADER_SYSTEM_CLASSES";
    public static final String HADOOP_CLIENT_SKIP_UNJAR = "HADOOP_CLIENT_SKIP_UNJAR";
    private static final int BUFFER_SIZE = 8192;
    
    public void unJar(final File jarFile, final File toDir) throws IOException {
        unJar(jarFile, toDir, RunJar.MATCH_ANY);
    }
    
    public static void unJar(final InputStream inputStream, final File toDir, final Pattern unpackRegex) throws IOException {
        try (final JarInputStream jar = new JarInputStream(inputStream)) {
            int numOfFailedLastModifiedSet = 0;
            final String targetDirPath = toDir.getCanonicalPath() + File.separator;
            for (JarEntry entry = jar.getNextJarEntry(); entry != null; entry = jar.getNextJarEntry()) {
                if (!entry.isDirectory() && unpackRegex.matcher(entry.getName()).matches()) {
                    final File file = new File(toDir, entry.getName());
                    if (!file.getCanonicalPath().startsWith(targetDirPath)) {
                        throw new IOException("expanding " + entry.getName() + " would create file outside of " + toDir);
                    }
                    ensureDirectory(file.getParentFile());
                    try (final OutputStream out = new FileOutputStream(file)) {
                        IOUtils.copyBytes(jar, out, 8192);
                    }
                    if (!file.setLastModified(entry.getTime())) {
                        ++numOfFailedLastModifiedSet;
                    }
                }
            }
            if (numOfFailedLastModifiedSet > 0) {
                RunJar.LOG.warn("Could not set last modfied time for {} file(s)", (Object)numOfFailedLastModifiedSet);
            }
            IOUtils.copyBytes(inputStream, new IOUtils.NullOutputStream(), 8192);
        }
    }
    
    @Deprecated
    public static void unJarAndSave(final InputStream inputStream, final File toDir, final String name, final Pattern unpackRegex) throws IOException {
        final File file = new File(toDir, name);
        ensureDirectory(toDir);
        try (final OutputStream jar = new FileOutputStream(file);
             final TeeInputStream teeInputStream = new TeeInputStream(inputStream, jar)) {
            unJar(teeInputStream, toDir, unpackRegex);
        }
    }
    
    public static void unJar(final File jarFile, final File toDir, final Pattern unpackRegex) throws IOException {
        try (final JarFile jar = new JarFile(jarFile)) {
            int numOfFailedLastModifiedSet = 0;
            final String targetDirPath = toDir.getCanonicalPath() + File.separator;
            final Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                final JarEntry entry = entries.nextElement();
                if (!entry.isDirectory() && unpackRegex.matcher(entry.getName()).matches()) {
                    try (final InputStream in = jar.getInputStream(entry)) {
                        final File file = new File(toDir, entry.getName());
                        if (!file.getCanonicalPath().startsWith(targetDirPath)) {
                            throw new IOException("expanding " + entry.getName() + " would create file outside of " + toDir);
                        }
                        ensureDirectory(file.getParentFile());
                        try (final OutputStream out = new FileOutputStream(file)) {
                            IOUtils.copyBytes(in, out, 8192);
                        }
                        if (!file.setLastModified(entry.getTime())) {
                            ++numOfFailedLastModifiedSet;
                        }
                    }
                }
            }
            if (numOfFailedLastModifiedSet > 0) {
                RunJar.LOG.warn("Could not set last modfied time for {} file(s)", (Object)numOfFailedLastModifiedSet);
            }
        }
    }
    
    private static void ensureDirectory(final File dir) throws IOException {
        if (!dir.mkdirs() && !dir.isDirectory()) {
            throw new IOException("Mkdirs failed to create " + dir.toString());
        }
    }
    
    public static void main(final String[] args) throws Throwable {
        new RunJar().run(args);
    }
    
    public void run(final String[] args) throws Throwable {
        final String usage = "RunJar jarFile [mainClass] args...";
        if (args.length < 1) {
            System.err.println(usage);
            System.exit(-1);
        }
        int firstArg = 0;
        final String fileName = args[firstArg++];
        final File file = new File(fileName);
        if (!file.exists() || !file.isFile()) {
            System.err.println("JAR does not exist or is not a normal file: " + file.getCanonicalPath());
            System.exit(-1);
        }
        String mainClassName = null;
        JarFile jarFile;
        try {
            jarFile = new JarFile(fileName);
        }
        catch (IOException io) {
            throw new IOException("Error opening job jar: " + fileName).initCause(io);
        }
        final Manifest manifest = jarFile.getManifest();
        if (manifest != null) {
            mainClassName = manifest.getMainAttributes().getValue("Main-Class");
        }
        jarFile.close();
        if (mainClassName == null) {
            if (args.length < 2) {
                System.err.println(usage);
                System.exit(-1);
            }
            mainClassName = args[firstArg++];
        }
        mainClassName = mainClassName.replaceAll("/", ".");
        final File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        ensureDirectory(tmpDir);
        File workDir;
        try {
            workDir = File.createTempFile("hadoop-unjar", "", tmpDir);
        }
        catch (IOException ioe) {
            System.err.println("Error creating temp dir in java.io.tmpdir " + tmpDir + " due to " + ioe.getMessage());
            System.exit(-1);
            return;
        }
        if (!workDir.delete()) {
            System.err.println("Delete failed for " + workDir);
            System.exit(-1);
        }
        ensureDirectory(workDir);
        ShutdownHookManager.get().addShutdownHook(new Runnable() {
            @Override
            public void run() {
                FileUtil.fullyDelete(workDir);
            }
        }, 10);
        if (!this.skipUnjar()) {
            this.unJar(file, workDir);
        }
        final ClassLoader loader = this.createClassLoader(file, workDir);
        Thread.currentThread().setContextClassLoader(loader);
        final Class<?> mainClass = Class.forName(mainClassName, true, loader);
        final Method main = mainClass.getMethod("main", String[].class);
        final List<String> newArgsSubList = Arrays.asList(args).subList(firstArg, args.length);
        final String[] newArgs = newArgsSubList.toArray(new String[newArgsSubList.size()]);
        try {
            main.invoke(null, newArgs);
        }
        catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }
    
    private ClassLoader createClassLoader(final File file, final File workDir) throws MalformedURLException {
        ClassLoader loader;
        if (this.useClientClassLoader()) {
            final StringBuilder sb = new StringBuilder();
            sb.append(workDir).append("/").append(File.pathSeparator).append(file).append(File.pathSeparator).append(workDir).append("/classes/").append(File.pathSeparator).append(workDir).append("/lib/*");
            final String hadoopClasspath = this.getHadoopClasspath();
            if (hadoopClasspath != null && !hadoopClasspath.isEmpty()) {
                sb.append(File.pathSeparator).append(hadoopClasspath);
            }
            final String clientClasspath = sb.toString();
            final String systemClasses = this.getSystemClasses();
            final List<String> systemClassesList = (systemClasses == null) ? null : Arrays.asList(StringUtils.getTrimmedStrings(systemClasses));
            loader = new ApplicationClassLoader(clientClasspath, this.getClass().getClassLoader(), systemClassesList);
        }
        else {
            final List<URL> classPath = new ArrayList<URL>();
            classPath.add(new File(workDir + "/").toURI().toURL());
            classPath.add(file.toURI().toURL());
            classPath.add(new File(workDir, "classes/").toURI().toURL());
            final File[] libs = new File(workDir, "lib").listFiles();
            if (libs != null) {
                for (final File lib : libs) {
                    classPath.add(lib.toURI().toURL());
                }
            }
            loader = new URLClassLoader(classPath.toArray(new URL[classPath.size()]));
        }
        return loader;
    }
    
    boolean useClientClassLoader() {
        return Boolean.parseBoolean(System.getenv("HADOOP_USE_CLIENT_CLASSLOADER"));
    }
    
    boolean skipUnjar() {
        return Boolean.parseBoolean(System.getenv("HADOOP_CLIENT_SKIP_UNJAR"));
    }
    
    String getHadoopClasspath() {
        return System.getenv("HADOOP_CLASSPATH");
    }
    
    String getSystemClasses() {
        return System.getenv("HADOOP_CLIENT_CLASSLOADER_SYSTEM_CLASSES");
    }
    
    static {
        LOG = LoggerFactory.getLogger(RunJar.class);
        MATCH_ANY = Pattern.compile(".*");
    }
}
