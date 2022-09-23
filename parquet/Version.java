// 
// Decompiled by Procyon v0.5.36
// 

package parquet;

import java.util.jar.Manifest;
import java.util.Properties;
import java.io.IOException;
import java.util.Enumeration;
import java.net.URL;

public class Version
{
    private static final Log LOG;
    public static final String VERSION_NUMBER;
    public static final String FULL_VERSION;
    
    private static String getJarPath() {
        final URL versionClassBaseUrl = Version.class.getResource("");
        if (versionClassBaseUrl.getProtocol().equals("jar")) {
            final String path = versionClassBaseUrl.getPath();
            final int jarEnd = path.indexOf("!");
            if (jarEnd != -1) {
                final String jarPath = path.substring(0, jarEnd);
                return jarPath;
            }
        }
        return null;
    }
    
    private static URL getResourceFromJar(final String jarPath, final String path) throws IOException {
        final Enumeration<URL> resources = Version.class.getClassLoader().getResources(path);
        while (resources.hasMoreElements()) {
            final URL url = resources.nextElement();
            if (url.getProtocol().equals("jar") && url.getPath().startsWith(jarPath)) {
                return url;
            }
        }
        return null;
    }
    
    private static String readVersionNumber() {
        String version = null;
        try {
            final String jarPath = getJarPath();
            if (jarPath != null) {
                final URL pomPropertiesUrl = getResourceFromJar(jarPath, "META-INF/maven/com.twitter/parquet-column/pom.properties");
                if (pomPropertiesUrl != null) {
                    final Properties properties = new Properties();
                    properties.load(pomPropertiesUrl.openStream());
                    version = properties.getProperty("version");
                }
            }
        }
        catch (Exception e) {
            Version.LOG.warn("can't read from META-INF", e);
        }
        return version;
    }
    
    private static String readFullVersion() {
        String sha = null;
        try {
            final String jarPath = getJarPath();
            if (jarPath != null) {
                final URL manifestUrl = getResourceFromJar(jarPath, "META-INF/MANIFEST.MF");
                if (manifestUrl != null) {
                    final Manifest manifest = new Manifest(manifestUrl.openStream());
                    sha = manifest.getMainAttributes().getValue("git-SHA-1");
                }
            }
        }
        catch (Exception e) {
            Version.LOG.warn("can't read from META-INF", e);
        }
        return "parquet-mr" + ((Version.VERSION_NUMBER != null) ? (" version " + Version.VERSION_NUMBER) : "") + ((sha != null) ? (" (build " + sha + ")") : "");
    }
    
    public static void main(final String[] args) {
        System.out.println(Version.FULL_VERSION);
    }
    
    static {
        LOG = Log.getLog(Version.class);
        VERSION_NUMBER = readVersionNumber();
        FULL_VERSION = readFullVersion();
    }
}
