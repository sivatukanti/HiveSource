// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.io;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import java.net.URL;

public final class FileLocator
{
    private final String fileName;
    private final String basePath;
    private final URL sourceURL;
    private final String encoding;
    private final FileSystem fileSystem;
    private final FileLocationStrategy locationStrategy;
    
    public FileLocator(final FileLocatorBuilder builder) {
        this.fileName = builder.fileName;
        this.basePath = builder.basePath;
        this.sourceURL = builder.sourceURL;
        this.encoding = builder.encoding;
        this.fileSystem = builder.fileSystem;
        this.locationStrategy = builder.locationStrategy;
    }
    
    public String getFileName() {
        return this.fileName;
    }
    
    public String getBasePath() {
        return this.basePath;
    }
    
    public URL getSourceURL() {
        return this.sourceURL;
    }
    
    public String getEncoding() {
        return this.encoding;
    }
    
    public FileSystem getFileSystem() {
        return this.fileSystem;
    }
    
    public FileLocationStrategy getLocationStrategy() {
        return this.locationStrategy;
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.getFileName()).append(this.getBasePath()).append(this.sourceURLAsString()).append(this.getEncoding()).append(this.getFileSystem()).append(this.getLocationStrategy()).toHashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FileLocator)) {
            return false;
        }
        final FileLocator c = (FileLocator)obj;
        return new EqualsBuilder().append(this.getFileName(), c.getFileName()).append(this.getBasePath(), c.getBasePath()).append(this.sourceURLAsString(), c.sourceURLAsString()).append(this.getEncoding(), c.getEncoding()).append(this.getFileSystem(), c.getFileSystem()).append(this.getLocationStrategy(), c.getLocationStrategy()).isEquals();
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("fileName", this.getFileName()).append("basePath", this.getBasePath()).append("sourceURL", this.sourceURLAsString()).append("encoding", this.getEncoding()).append("fileSystem", this.getFileSystem()).append("locationStrategy", this.getLocationStrategy()).toString();
    }
    
    private String sourceURLAsString() {
        return (this.sourceURL != null) ? this.sourceURL.toExternalForm() : "";
    }
    
    public static final class FileLocatorBuilder
    {
        private String fileName;
        private String basePath;
        private URL sourceURL;
        private String encoding;
        private FileSystem fileSystem;
        private FileLocationStrategy locationStrategy;
        
        FileLocatorBuilder(final FileLocator src) {
            if (src != null) {
                this.initBuilder(src);
            }
        }
        
        public FileLocatorBuilder encoding(final String enc) {
            this.encoding = enc;
            return this;
        }
        
        public FileLocatorBuilder fileSystem(final FileSystem fs) {
            this.fileSystem = fs;
            return this;
        }
        
        public FileLocatorBuilder basePath(final String path) {
            this.basePath = path;
            return this;
        }
        
        public FileLocatorBuilder fileName(final String name) {
            this.fileName = name;
            return this;
        }
        
        public FileLocatorBuilder sourceURL(final URL url) {
            this.sourceURL = url;
            return this;
        }
        
        public FileLocatorBuilder locationStrategy(final FileLocationStrategy strategy) {
            this.locationStrategy = strategy;
            return this;
        }
        
        public FileLocator create() {
            return new FileLocator(this);
        }
        
        private void initBuilder(final FileLocator src) {
            this.basePath = src.getBasePath();
            this.fileName = src.getFileName();
            this.sourceURL = src.getSourceURL();
            this.encoding = src.getEncoding();
            this.fileSystem = src.getFileSystem();
            this.locationStrategy = src.getLocationStrategy();
        }
    }
}
