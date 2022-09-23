// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.io;

import java.io.File;
import org.apache.commons.lang3.StringUtils;
import java.net.URL;

public class HomeDirectoryLocationStrategy implements FileLocationStrategy
{
    private static final String PROP_HOME = "user.home";
    private final String homeDirectory;
    private final boolean evaluateBasePath;
    
    public HomeDirectoryLocationStrategy(final String homeDir, final boolean withBasePath) {
        this.homeDirectory = fetchHomeDirectory(homeDir);
        this.evaluateBasePath = withBasePath;
    }
    
    public HomeDirectoryLocationStrategy(final boolean withBasePath) {
        this(null, withBasePath);
    }
    
    public HomeDirectoryLocationStrategy() {
        this(false);
    }
    
    public String getHomeDirectory() {
        return this.homeDirectory;
    }
    
    public boolean isEvaluateBasePath() {
        return this.evaluateBasePath;
    }
    
    @Override
    public URL locate(final FileSystem fileSystem, final FileLocator locator) {
        if (StringUtils.isNotEmpty(locator.getFileName())) {
            final String basePath = this.fetchBasePath(locator);
            final File file = FileLocatorUtils.constructFile(basePath, locator.getFileName());
            if (file.isFile()) {
                return FileLocatorUtils.convertFileToURL(file);
            }
        }
        return null;
    }
    
    private String fetchBasePath(final FileLocator locator) {
        if (this.isEvaluateBasePath() && StringUtils.isNotEmpty(locator.getBasePath())) {
            return FileLocatorUtils.appendPath(this.getHomeDirectory(), locator.getBasePath());
        }
        return this.getHomeDirectory();
    }
    
    private static String fetchHomeDirectory(final String homeDir) {
        return (homeDir != null) ? homeDir : System.getProperty("user.home");
    }
}
