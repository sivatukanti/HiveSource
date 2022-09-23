// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import org.eclipse.jetty.util.log.Log;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;
import java.io.File;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.log.Logger;

public class HomeBaseWarning
{
    private static final Logger LOG;
    
    public HomeBaseWarning() {
        boolean showWarn = false;
        final String home = System.getProperty("jetty.home");
        final String base = System.getProperty("jetty.base");
        if (StringUtil.isBlank(base)) {
            return;
        }
        final Path homePath = new File(home).toPath();
        final Path basePath = new File(base).toPath();
        try {
            showWarn = Files.isSameFile(homePath, basePath);
        }
        catch (IOException e) {
            HomeBaseWarning.LOG.ignore(e);
            return;
        }
        if (showWarn) {
            final StringBuilder warn = new StringBuilder();
            warn.append("This instance of Jetty is not running from a separate {jetty.base} directory");
            warn.append(", this is not recommended.  See documentation at http://www.eclipse.org/jetty/documentation/current/startup.html");
            HomeBaseWarning.LOG.warn("{}", warn.toString());
        }
    }
    
    static {
        LOG = Log.getLogger(HomeBaseWarning.class);
    }
}
