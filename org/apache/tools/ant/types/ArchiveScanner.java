// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types;

import org.apache.tools.ant.types.resources.FileResourceIterator;
import java.util.Iterator;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;
import java.util.TreeMap;
import java.util.Map;
import java.io.File;
import org.apache.tools.ant.DirectoryScanner;

public abstract class ArchiveScanner extends DirectoryScanner
{
    protected File srcFile;
    private Resource src;
    private Resource lastScannedResource;
    private Map<String, Resource> fileEntries;
    private Map<String, Resource> dirEntries;
    private Map<String, Resource> matchFileEntries;
    private Map<String, Resource> matchDirEntries;
    private String encoding;
    private boolean errorOnMissingArchive;
    
    public ArchiveScanner() {
        this.fileEntries = new TreeMap<String, Resource>();
        this.dirEntries = new TreeMap<String, Resource>();
        this.matchFileEntries = new TreeMap<String, Resource>();
        this.matchDirEntries = new TreeMap<String, Resource>();
        this.errorOnMissingArchive = true;
    }
    
    public void setErrorOnMissingArchive(final boolean errorOnMissingArchive) {
        this.errorOnMissingArchive = errorOnMissingArchive;
    }
    
    @Override
    public void scan() {
        if (this.src == null || (!this.src.isExists() && !this.errorOnMissingArchive)) {
            return;
        }
        super.scan();
    }
    
    public void setSrc(final File srcFile) {
        this.setSrc(new FileResource(srcFile));
    }
    
    public void setSrc(final Resource src) {
        this.src = src;
        final FileProvider fp = src.as(FileProvider.class);
        if (fp != null) {
            this.srcFile = fp.getFile();
        }
    }
    
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }
    
    @Override
    public String[] getIncludedFiles() {
        if (this.src == null) {
            return super.getIncludedFiles();
        }
        this.scanme();
        return this.matchFileEntries.keySet().toArray(new String[this.matchFileEntries.size()]);
    }
    
    @Override
    public int getIncludedFilesCount() {
        if (this.src == null) {
            return super.getIncludedFilesCount();
        }
        this.scanme();
        return this.matchFileEntries.size();
    }
    
    @Override
    public String[] getIncludedDirectories() {
        if (this.src == null) {
            return super.getIncludedDirectories();
        }
        this.scanme();
        return this.matchDirEntries.keySet().toArray(new String[this.matchDirEntries.size()]);
    }
    
    @Override
    public int getIncludedDirsCount() {
        if (this.src == null) {
            return super.getIncludedDirsCount();
        }
        this.scanme();
        return this.matchDirEntries.size();
    }
    
    Iterator<Resource> getResourceFiles(final Project project) {
        if (this.src == null) {
            return new FileResourceIterator(project, this.getBasedir(), this.getIncludedFiles());
        }
        this.scanme();
        return this.matchFileEntries.values().iterator();
    }
    
    Iterator<Resource> getResourceDirectories(final Project project) {
        if (this.src == null) {
            return new FileResourceIterator(project, this.getBasedir(), this.getIncludedDirectories());
        }
        this.scanme();
        return this.matchDirEntries.values().iterator();
    }
    
    public void init() {
        if (this.includes == null) {
            (this.includes = new String[1])[0] = "**";
        }
        if (this.excludes == null) {
            this.excludes = new String[0];
        }
    }
    
    public boolean match(final String path) {
        final String vpath = path.replace('/', File.separatorChar).replace('\\', File.separatorChar);
        return this.isIncluded(vpath) && !this.isExcluded(vpath);
    }
    
    @Override
    public Resource getResource(String name) {
        if (this.src == null) {
            return super.getResource(name);
        }
        if (name.equals("")) {
            return new Resource("", true, Long.MAX_VALUE, true);
        }
        this.scanme();
        if (this.fileEntries.containsKey(name)) {
            return this.fileEntries.get(name);
        }
        name = trimSeparator(name);
        if (this.dirEntries.containsKey(name)) {
            return this.dirEntries.get(name);
        }
        return new Resource(name);
    }
    
    protected abstract void fillMapsFromArchive(final Resource p0, final String p1, final Map<String, Resource> p2, final Map<String, Resource> p3, final Map<String, Resource> p4, final Map<String, Resource> p5);
    
    private void scanme() {
        if (!this.src.isExists() && !this.errorOnMissingArchive) {
            return;
        }
        final Resource thisresource = new Resource(this.src.getName(), this.src.isExists(), this.src.getLastModified());
        if (this.lastScannedResource != null && this.lastScannedResource.getName().equals(thisresource.getName()) && this.lastScannedResource.getLastModified() == thisresource.getLastModified()) {
            return;
        }
        this.init();
        this.fileEntries.clear();
        this.dirEntries.clear();
        this.matchFileEntries.clear();
        this.matchDirEntries.clear();
        this.fillMapsFromArchive(this.src, this.encoding, this.fileEntries, this.matchFileEntries, this.dirEntries, this.matchDirEntries);
        this.lastScannedResource = thisresource;
    }
    
    protected static final String trimSeparator(final String s) {
        return s.endsWith("/") ? s.substring(0, s.length() - 1) : s;
    }
}
