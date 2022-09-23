// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.reloading;

import org.apache.commons.configuration2.io.FileSystem;
import java.io.File;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.configuration2.io.FileHandler;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

public class VFSFileHandlerReloadingDetector extends FileHandlerReloadingDetector
{
    private final Log log;
    
    public VFSFileHandlerReloadingDetector() {
        this.log = LogFactory.getLog(this.getClass());
    }
    
    public VFSFileHandlerReloadingDetector(final FileHandler handler, final long refreshDelay) {
        super(handler, refreshDelay);
        this.log = LogFactory.getLog(this.getClass());
    }
    
    public VFSFileHandlerReloadingDetector(final FileHandler handler) {
        super(handler);
        this.log = LogFactory.getLog(this.getClass());
    }
    
    @Override
    protected long getLastModificationDate() {
        final FileObject file = this.getFileObject();
        try {
            if (file == null || !file.exists()) {
                return 0L;
            }
            return file.getContent().getLastModifiedTime();
        }
        catch (FileSystemException ex) {
            this.log.error("Unable to get last modified time for" + file.getName().getURI(), (Throwable)ex);
            return 0L;
        }
    }
    
    protected FileObject getFileObject() {
        if (!this.getFileHandler().isLocationDefined()) {
            return null;
        }
        try {
            final FileSystemManager fsManager = VFS.getManager();
            final String uri = this.resolveFileURI();
            if (uri == null) {
                throw new ConfigurationRuntimeException("Unable to determine file to monitor");
            }
            return fsManager.resolveFile(uri);
        }
        catch (FileSystemException fse) {
            final String msg = "Unable to monitor " + this.getFileHandler().getURL().toString();
            this.log.error(msg);
            throw new ConfigurationRuntimeException(msg, (Throwable)fse);
        }
    }
    
    protected String resolveFileURI() {
        final FileSystem fs = this.getFileHandler().getFileSystem();
        final String uri = fs.getPath(null, this.getFileHandler().getURL(), this.getFileHandler().getBasePath(), this.getFileHandler().getFileName());
        return uri;
    }
}
