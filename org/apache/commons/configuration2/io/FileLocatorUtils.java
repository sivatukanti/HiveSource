// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.io;

import org.apache.commons.logging.LogFactory;
import java.util.Collection;
import java.util.Arrays;
import org.apache.commons.lang3.ObjectUtils;
import java.net.URI;
import java.net.MalformedURLException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.configuration2.ex.ConfigurationException;
import java.util.Map;
import java.io.File;
import java.net.URL;
import org.apache.commons.logging.Log;

public final class FileLocatorUtils
{
    public static final FileSystem DEFAULT_FILE_SYSTEM;
    public static final FileLocationStrategy DEFAULT_LOCATION_STRATEGY;
    private static final String FILE_SCHEME = "file:";
    private static final Log LOG;
    private static final String PROP_BASE_PATH = "basePath";
    private static final String PROP_ENCODING = "encoding";
    private static final String PROP_FILE_NAME = "fileName";
    private static final String PROP_FILE_SYSTEM = "fileSystem";
    private static final String PROP_STRATEGY = "locationStrategy";
    private static final String PROP_SOURCE_URL = "sourceURL";
    
    private FileLocatorUtils() {
    }
    
    public static File fileFromURL(final URL url) {
        return FileUtils.toFile(url);
    }
    
    public static FileLocator.FileLocatorBuilder fileLocator() {
        return fileLocator(null);
    }
    
    public static FileLocator.FileLocatorBuilder fileLocator(final FileLocator src) {
        return new FileLocator.FileLocatorBuilder(src);
    }
    
    public static FileLocator fromMap(final Map<String, ?> map) {
        final FileLocator.FileLocatorBuilder builder = fileLocator();
        if (map != null) {
            builder.basePath((String)map.get("basePath")).encoding((String)map.get("encoding")).fileName((String)map.get("fileName")).fileSystem((FileSystem)map.get("fileSystem")).locationStrategy((FileLocationStrategy)map.get("locationStrategy")).sourceURL((URL)map.get("sourceURL"));
        }
        return builder.create();
    }
    
    public static void put(final FileLocator locator, final Map<String, Object> map) {
        if (map == null) {
            throw new IllegalArgumentException("Map must not be null!");
        }
        if (locator != null) {
            map.put("basePath", locator.getBasePath());
            map.put("encoding", locator.getEncoding());
            map.put("fileName", locator.getFileName());
            map.put("fileSystem", locator.getFileSystem());
            map.put("sourceURL", locator.getSourceURL());
            map.put("locationStrategy", locator.getLocationStrategy());
        }
    }
    
    public static boolean isLocationDefined(final FileLocator locator) {
        return locator != null && (locator.getFileName() != null || locator.getSourceURL() != null);
    }
    
    public static boolean isFullyInitialized(final FileLocator locator) {
        return locator != null && locator.getBasePath() != null && locator.getFileName() != null && locator.getSourceURL() != null;
    }
    
    public static FileLocator fullyInitializedLocator(final FileLocator locator) {
        if (isFullyInitialized(locator)) {
            return locator;
        }
        final URL url = locate(locator);
        return (url != null) ? createFullyInitializedLocatorFromURL(locator, url) : null;
    }
    
    public static URL locate(final FileLocator locator) {
        if (locator == null) {
            return null;
        }
        return obtainLocationStrategy(locator).locate(obtainFileSystem(locator), locator);
    }
    
    public static URL locateOrThrow(final FileLocator locator) throws ConfigurationException {
        final URL url = locate(locator);
        if (url == null) {
            throw new ConfigurationException("Could not locate: " + locator);
        }
        return url;
    }
    
    static String getBasePath(final URL url) {
        if (url == null) {
            return null;
        }
        String s = url.toString();
        if (s.startsWith("file:") && !s.startsWith("file://")) {
            s = "file://" + s.substring("file:".length());
        }
        if (s.endsWith("/") || StringUtils.isEmpty(url.getPath())) {
            return s;
        }
        return s.substring(0, s.lastIndexOf("/") + 1);
    }
    
    static String getFileName(final URL url) {
        if (url == null) {
            return null;
        }
        final String path = url.getPath();
        if (path.endsWith("/") || StringUtils.isEmpty(path)) {
            return null;
        }
        return path.substring(path.lastIndexOf("/") + 1);
    }
    
    static File getFile(final String basePath, final String fileName) {
        final File f = new File(fileName);
        if (f.isAbsolute()) {
            return f;
        }
        URL url;
        try {
            url = new URL(new URL(basePath), fileName);
        }
        catch (MalformedURLException mex1) {
            try {
                url = new URL(fileName);
            }
            catch (MalformedURLException mex2) {
                url = null;
            }
        }
        if (url != null) {
            return fileFromURL(url);
        }
        return constructFile(basePath, fileName);
    }
    
    static URL toURL(final File file) throws MalformedURLException {
        return file.toURI().toURL();
    }
    
    static URL convertURIToURL(final URI uri) {
        try {
            return uri.toURL();
        }
        catch (MalformedURLException e) {
            return null;
        }
    }
    
    static URL convertFileToURL(final File file) {
        return convertURIToURL(file.toURI());
    }
    
    static URL locateFromClasspath(final String resourceName) {
        URL url = null;
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader != null) {
            url = loader.getResource(resourceName);
            if (url != null) {
                FileLocatorUtils.LOG.debug("Loading configuration from the context classpath (" + resourceName + ")");
            }
        }
        if (url == null) {
            url = ClassLoader.getSystemResource(resourceName);
            if (url != null) {
                FileLocatorUtils.LOG.debug("Loading configuration from the system classpath (" + resourceName + ")");
            }
        }
        return url;
    }
    
    static File constructFile(final String basePath, final String fileName) {
        final File absolute = new File(fileName);
        File file;
        if (StringUtils.isEmpty(basePath) || absolute.isAbsolute()) {
            file = absolute;
        }
        else {
            file = new File(appendPath(basePath, fileName));
        }
        return file;
    }
    
    static String appendPath(final String path, final String ext) {
        final StringBuilder fName = new StringBuilder();
        fName.append(path);
        if (!path.endsWith(File.separator)) {
            fName.append(File.separator);
        }
        if (ext.startsWith("." + File.separator)) {
            fName.append(ext.substring(2));
        }
        else {
            fName.append(ext);
        }
        return fName.toString();
    }
    
    static FileSystem obtainFileSystem(final FileLocator locator) {
        return (locator != null) ? ObjectUtils.defaultIfNull(locator.getFileSystem(), FileLocatorUtils.DEFAULT_FILE_SYSTEM) : FileLocatorUtils.DEFAULT_FILE_SYSTEM;
    }
    
    static FileLocationStrategy obtainLocationStrategy(final FileLocator locator) {
        return (locator != null) ? ObjectUtils.defaultIfNull(locator.getLocationStrategy(), FileLocatorUtils.DEFAULT_LOCATION_STRATEGY) : FileLocatorUtils.DEFAULT_LOCATION_STRATEGY;
    }
    
    private static FileLocator createFullyInitializedLocatorFromURL(final FileLocator src, final URL url) {
        final FileLocator.FileLocatorBuilder fileLocatorBuilder = fileLocator(src);
        if (src.getSourceURL() == null) {
            fileLocatorBuilder.sourceURL(url);
        }
        if (StringUtils.isBlank(src.getFileName())) {
            fileLocatorBuilder.fileName(getFileName(url));
        }
        if (StringUtils.isBlank(src.getBasePath())) {
            fileLocatorBuilder.basePath(getBasePath(url));
        }
        return fileLocatorBuilder.create();
    }
    
    private static FileLocationStrategy initDefaultLocationStrategy() {
        final FileLocationStrategy[] subStrategies = { new ProvidedURLLocationStrategy(), new FileSystemLocationStrategy(), new AbsoluteNameLocationStrategy(), new BasePathLocationStrategy(), new HomeDirectoryLocationStrategy(true), new HomeDirectoryLocationStrategy(false), new ClasspathLocationStrategy() };
        return new CombinedLocationStrategy(Arrays.asList(subStrategies));
    }
    
    static {
        DEFAULT_FILE_SYSTEM = new DefaultFileSystem();
        DEFAULT_LOCATION_STRATEGY = initDefaultLocationStrategy();
        LOG = LogFactory.getLog(FileLocatorUtils.class);
    }
}
