// 
// Decompiled by Procyon v0.5.36
// 

package org.xerial.snappy;

import java.net.URL;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.security.NoSuchAlgorithmException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.io.BufferedInputStream;
import java.lang.reflect.InvocationTargetException;
import java.io.File;
import java.lang.reflect.Method;
import java.util.List;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.io.InputStream;
import java.util.Properties;

public class SnappyLoader
{
    public static final String SNAPPY_SYSTEM_PROPERTIES_FILE = "org-xerial-snappy.properties";
    public static final String KEY_SNAPPY_LIB_PATH = "org.xerial.snappy.lib.path";
    public static final String KEY_SNAPPY_LIB_NAME = "org.xerial.snappy.lib.name";
    public static final String KEY_SNAPPY_TEMPDIR = "org.xerial.snappy.tempdir";
    public static final String KEY_SNAPPY_USE_SYSTEMLIB = "org.xerial.snappy.use.systemlib";
    public static final String KEY_SNAPPY_DISABLE_BUNDLED_LIBS = "org.xerial.snappy.disable.bundled.libs";
    private static volatile boolean isLoaded;
    private static volatile Object api;
    
    static synchronized void setApi(final Object nativeCode) {
        SnappyLoader.api = nativeCode;
    }
    
    private static void loadSnappySystemProperties() {
        try {
            final InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("org-xerial-snappy.properties");
            if (is == null) {
                return;
            }
            final Properties props = new Properties();
            props.load(is);
            is.close();
            final Enumeration<?> names = props.propertyNames();
            while (names.hasMoreElements()) {
                final String name = (String)names.nextElement();
                if (name.startsWith("org.xerial.snappy.") && System.getProperty(name) == null) {
                    System.setProperty(name, props.getProperty(name));
                }
            }
        }
        catch (Throwable ex) {
            System.err.println("Could not load 'org-xerial-snappy.properties' from classpath: " + ex.toString());
        }
    }
    
    private static ClassLoader getRootClassLoader() {
        ClassLoader cl;
        for (cl = Thread.currentThread().getContextClassLoader(); cl.getParent() != null; cl = cl.getParent()) {}
        return cl;
    }
    
    private static byte[] getByteCode(final String resourcePath) throws IOException {
        final InputStream in = SnappyLoader.class.getResourceAsStream(resourcePath);
        if (in == null) {
            throw new IOException(resourcePath + " is not found");
        }
        final byte[] buf = new byte[1024];
        final ByteArrayOutputStream byteCodeBuf = new ByteArrayOutputStream();
        int readLength;
        while ((readLength = in.read(buf)) != -1) {
            byteCodeBuf.write(buf, 0, readLength);
        }
        in.close();
        return byteCodeBuf.toByteArray();
    }
    
    public static boolean isNativeLibraryLoaded() {
        return SnappyLoader.isLoaded;
    }
    
    private static boolean hasInjectedNativeLoader() {
        try {
            final String nativeLoaderClassName = "org.xerial.snappy.SnappyNativeLoader";
            final Class<?> c = Class.forName("org.xerial.snappy.SnappyNativeLoader");
            return true;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    static synchronized Object load() {
        if (SnappyLoader.api != null) {
            return SnappyLoader.api;
        }
        try {
            if (!hasInjectedNativeLoader()) {
                final Class<?> nativeLoader = injectSnappyNativeLoader();
                loadNativeLibrary(nativeLoader);
            }
            SnappyLoader.isLoaded = true;
            final Object nativeCode = Class.forName("org.xerial.snappy.SnappyNative").newInstance();
            setApi(nativeCode);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new SnappyError(SnappyErrorCode.FAILED_TO_LOAD_NATIVE_LIBRARY, e.getMessage());
        }
        return SnappyLoader.api;
    }
    
    private static Class<?> injectSnappyNativeLoader() {
        try {
            final String nativeLoaderClassName = "org.xerial.snappy.SnappyNativeLoader";
            final ClassLoader rootClassLoader = getRootClassLoader();
            final byte[] byteCode = getByteCode("/org/xerial/snappy/SnappyNativeLoader.bytecode");
            final String[] classesToPreload = { "org.xerial.snappy.SnappyNativeAPI", "org.xerial.snappy.SnappyNative", "org.xerial.snappy.SnappyErrorCode" };
            final List<byte[]> preloadClassByteCode = new ArrayList<byte[]>(classesToPreload.length);
            for (final String each : classesToPreload) {
                preloadClassByteCode.add(getByteCode(String.format("/%s.class", each.replaceAll("\\.", "/"))));
            }
            final Class<?> classLoader = Class.forName("java.lang.ClassLoader");
            final Method defineClass = classLoader.getDeclaredMethod("defineClass", String.class, byte[].class, Integer.TYPE, Integer.TYPE, ProtectionDomain.class);
            final ProtectionDomain pd = System.class.getProtectionDomain();
            defineClass.setAccessible(true);
            try {
                defineClass.invoke(rootClassLoader, "org.xerial.snappy.SnappyNativeLoader", byteCode, 0, byteCode.length, pd);
                for (int i = 0; i < classesToPreload.length; ++i) {
                    final byte[] b = preloadClassByteCode.get(i);
                    defineClass.invoke(rootClassLoader, classesToPreload[i], b, 0, b.length, pd);
                }
            }
            finally {
                defineClass.setAccessible(false);
            }
            return rootClassLoader.loadClass("org.xerial.snappy.SnappyNativeLoader");
        }
        catch (Exception e) {
            e.printStackTrace(System.err);
            throw new SnappyError(SnappyErrorCode.FAILED_TO_LOAD_NATIVE_LIBRARY, e.getMessage());
        }
    }
    
    private static void loadNativeLibrary(final Class<?> loaderClass) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (loaderClass == null) {
            throw new SnappyError(SnappyErrorCode.FAILED_TO_LOAD_NATIVE_LIBRARY, "missing snappy native loader class");
        }
        final File nativeLib = findNativeLibrary();
        if (nativeLib != null) {
            final Method loadMethod = loaderClass.getDeclaredMethod("load", String.class);
            loadMethod.invoke(null, nativeLib.getAbsolutePath());
        }
        else {
            final Method loadMethod = loaderClass.getDeclaredMethod("loadLibrary", String.class);
            loadMethod.invoke(null, "snappyjava");
        }
    }
    
    static String md5sum(final InputStream input) throws IOException {
        final BufferedInputStream in = new BufferedInputStream(input);
        try {
            final MessageDigest digest = MessageDigest.getInstance("MD5");
            final DigestInputStream digestInputStream = new DigestInputStream(in, digest);
            while (digestInputStream.read() >= 0) {}
            final ByteArrayOutputStream md5out = new ByteArrayOutputStream();
            md5out.write(digest.digest());
            return md5out.toString();
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 algorithm is not available: " + e);
        }
        finally {
            in.close();
        }
    }
    
    private static File extractLibraryFile(final String libFolderForCurrentOS, final String libraryFileName, final String targetFolder) {
        final String nativeLibraryFilePath = libFolderForCurrentOS + "/" + libraryFileName;
        final String prefix = "snappy-" + getVersion() + "-";
        final String extractedLibFileName = prefix + libraryFileName;
        final File extractedLibFile = new File(targetFolder, extractedLibFileName);
        try {
            if (extractedLibFile.exists()) {
                final String md5sum1 = md5sum(SnappyLoader.class.getResourceAsStream(nativeLibraryFilePath));
                final String md5sum2 = md5sum(new FileInputStream(extractedLibFile));
                if (md5sum1.equals(md5sum2)) {
                    return new File(targetFolder, extractedLibFileName);
                }
                final boolean deletionSucceeded = extractedLibFile.delete();
                if (!deletionSucceeded) {
                    throw new IOException("failed to remove existing native library file: " + extractedLibFile.getAbsolutePath());
                }
            }
            final InputStream reader = SnappyLoader.class.getResourceAsStream(nativeLibraryFilePath);
            final FileOutputStream writer = new FileOutputStream(extractedLibFile);
            final byte[] buffer = new byte[8192];
            int bytesRead = 0;
            while ((bytesRead = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, bytesRead);
            }
            writer.close();
            reader.close();
            if (!System.getProperty("os.name").contains("Windows")) {
                try {
                    Runtime.getRuntime().exec(new String[] { "chmod", "755", extractedLibFile.getAbsolutePath() }).waitFor();
                }
                catch (Throwable t) {}
            }
            return new File(targetFolder, extractedLibFileName);
        }
        catch (IOException e) {
            e.printStackTrace(System.err);
            return null;
        }
    }
    
    static File findNativeLibrary() {
        final boolean useSystemLib = Boolean.parseBoolean(System.getProperty("org.xerial.snappy.use.systemlib", "false"));
        final boolean disabledBundledLibs = Boolean.parseBoolean(System.getProperty("org.xerial.snappy.disable.bundled.libs", "false"));
        if (useSystemLib || disabledBundledLibs) {
            return null;
        }
        String snappyNativeLibraryPath = System.getProperty("org.xerial.snappy.lib.path");
        String snappyNativeLibraryName = System.getProperty("org.xerial.snappy.lib.name");
        if (snappyNativeLibraryName == null) {
            snappyNativeLibraryName = System.mapLibraryName("snappyjava");
        }
        if (snappyNativeLibraryPath != null) {
            final File nativeLib = new File(snappyNativeLibraryPath, snappyNativeLibraryName);
            if (nativeLib.exists()) {
                return nativeLib;
            }
        }
        snappyNativeLibraryPath = "/org/xerial/snappy/native/" + OSInfo.getNativeLibFolderPathForCurrentOS();
        boolean hasNativeLib = hasResource(snappyNativeLibraryPath + "/" + snappyNativeLibraryName);
        if (!hasNativeLib && OSInfo.getOSName().equals("Mac")) {
            final String altName = "libsnappyjava.jnilib";
            if (hasResource(snappyNativeLibraryPath + "/" + altName)) {
                snappyNativeLibraryName = altName;
                hasNativeLib = true;
            }
        }
        if (!hasNativeLib) {
            final String errorMessage = String.format("no native library is found for os.name=%s and os.arch=%s", OSInfo.getOSName(), OSInfo.getArchName());
            throw new SnappyError(SnappyErrorCode.FAILED_TO_LOAD_NATIVE_LIBRARY, errorMessage);
        }
        final String tempFolder = new File(System.getProperty("org.xerial.snappy.tempdir", System.getProperty("java.io.tmpdir"))).getAbsolutePath();
        return extractLibraryFile(snappyNativeLibraryPath, snappyNativeLibraryName, tempFolder);
    }
    
    private static boolean hasResource(final String path) {
        return SnappyLoader.class.getResource(path) != null;
    }
    
    public static String getVersion() {
        URL versionFile = SnappyLoader.class.getResource("/META-INF/maven/org.xerial.snappy/snappy-java/pom.properties");
        if (versionFile == null) {
            versionFile = SnappyLoader.class.getResource("/org/xerial/snappy/VERSION");
        }
        String version = "unknown";
        try {
            if (versionFile != null) {
                final Properties versionData = new Properties();
                versionData.load(versionFile.openStream());
                version = versionData.getProperty("version", version);
                if (version.equals("unknown")) {
                    version = versionData.getProperty("VERSION", version);
                }
                version = version.trim().replaceAll("[^0-9\\.]", "");
            }
        }
        catch (IOException e) {
            System.err.println(e);
        }
        return version;
    }
    
    static {
        SnappyLoader.isLoaded = false;
        SnappyLoader.api = null;
        loadSnappySystemProperties();
    }
}
