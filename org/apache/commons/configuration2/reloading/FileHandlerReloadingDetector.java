// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.reloading;

import java.net.MalformedURLException;
import org.apache.commons.configuration2.io.FileLocatorUtils;
import java.net.URL;
import java.io.File;
import org.apache.commons.configuration2.io.FileHandler;

public class FileHandlerReloadingDetector implements ReloadingDetector
{
    private static final String JAR_PROTOCOL = "jar";
    private static final int DEFAULT_REFRESH_DELAY = 5000;
    private final FileHandler fileHandler;
    private final long refreshDelay;
    private long lastModified;
    private long lastChecked;
    
    public FileHandlerReloadingDetector(final FileHandler handler, final long refreshDelay) {
        this.fileHandler = ((handler != null) ? handler : new FileHandler());
        this.refreshDelay = refreshDelay;
    }
    
    public FileHandlerReloadingDetector(final FileHandler handler) {
        this(handler, 5000L);
    }
    
    public FileHandlerReloadingDetector() {
        this(null);
    }
    
    public FileHandler getFileHandler() {
        return this.fileHandler;
    }
    
    public long getRefreshDelay() {
        return this.refreshDelay;
    }
    
    @Override
    public boolean isReloadingRequired() {
        final long now = System.currentTimeMillis();
        if (now >= this.lastChecked + this.getRefreshDelay()) {
            this.lastChecked = now;
            final long modified = this.getLastModificationDate();
            if (modified > 0L) {
                if (this.lastModified == 0L) {
                    this.updateLastModified(modified);
                }
                else if (modified != this.lastModified) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public void reloadingPerformed() {
        this.updateLastModified(this.getLastModificationDate());
    }
    
    protected long getLastModificationDate() {
        final File file = this.getExistingFile();
        return (file != null) ? file.lastModified() : 0L;
    }
    
    protected void updateLastModified(final long time) {
        this.lastModified = time;
    }
    
    protected File getFile() {
        final URL url = this.getFileHandler().getURL();
        return (url != null) ? fileFromURL(url) : this.getFileHandler().getFile();
    }
    
    private File getExistingFile() {
        File file = this.getFile();
        if (file != null && !file.exists()) {
            file = null;
        }
        return file;
    }
    
    private static File fileFromURL(final URL url) {
        if ("jar".equals(url.getProtocol())) {
            final String path = url.getPath();
            try {
                return FileLocatorUtils.fileFromURL(new URL(path.substring(0, path.indexOf(33))));
            }
            catch (MalformedURLException mex) {
                return null;
            }
        }
        return FileLocatorUtils.fileFromURL(url);
    }
}
