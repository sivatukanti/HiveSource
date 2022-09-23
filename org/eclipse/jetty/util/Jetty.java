// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.io.InputStream;
import org.eclipse.jetty.util.log.Log;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Properties;
import org.eclipse.jetty.util.log.Logger;

public class Jetty
{
    private static final Logger LOG;
    public static final String VERSION;
    public static final String POWERED_BY;
    public static final boolean STABLE;
    public static final String GIT_HASH;
    public static final String BUILD_TIMESTAMP;
    private static final Properties __buildProperties;
    
    private Jetty() {
    }
    
    private static String formatTimestamp(final String timestamp) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(new Date(Long.valueOf(timestamp)));
        }
        catch (NumberFormatException e) {
            Jetty.LOG.debug(e);
            return "unknown";
        }
    }
    
    static {
        LOG = Log.getLogger(Jetty.class);
        __buildProperties = new Properties();
        try (final InputStream inputStream = Jetty.class.getResourceAsStream("/org/eclipse/jetty/version/build.properties")) {
            Jetty.__buildProperties.load(inputStream);
        }
        catch (Exception e) {
            Jetty.LOG.ignore(e);
        }
        System.setProperty("jetty.git.hash", GIT_HASH = Jetty.__buildProperties.getProperty("buildNumber", "unknown"));
        BUILD_TIMESTAMP = formatTimestamp(Jetty.__buildProperties.getProperty("timestamp", "unknown"));
        final Package pkg = Jetty.class.getPackage();
        if (pkg != null && "Eclipse.org - Jetty".equals(pkg.getImplementationVendor()) && pkg.getImplementationVersion() != null) {
            VERSION = pkg.getImplementationVersion();
        }
        else {
            VERSION = System.getProperty("jetty.version", "9.3.z-SNAPSHOT");
        }
        POWERED_BY = "<a href=\"http://eclipse.org/jetty\">Powered by Jetty:// " + Jetty.VERSION + "</a>";
        STABLE = !Jetty.VERSION.matches("^.*\\.(RC|M)[0-9]+$");
    }
}
