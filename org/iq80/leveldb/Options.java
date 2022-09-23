// 
// Decompiled by Procyon v0.5.36
// 

package org.iq80.leveldb;

public class Options
{
    private boolean createIfMissing;
    private boolean errorIfExists;
    private int writeBufferSize;
    private int maxOpenFiles;
    private int blockRestartInterval;
    private int blockSize;
    private CompressionType compressionType;
    private boolean verifyChecksums;
    private boolean paranoidChecks;
    private DBComparator comparator;
    private Logger logger;
    private long cacheSize;
    
    public Options() {
        this.createIfMissing = true;
        this.writeBufferSize = 4194304;
        this.maxOpenFiles = 1000;
        this.blockRestartInterval = 16;
        this.blockSize = 4096;
        this.compressionType = CompressionType.SNAPPY;
        this.verifyChecksums = true;
        this.paranoidChecks = false;
        this.logger = null;
    }
    
    static void checkArgNotNull(final Object value, final String name) {
        if (value == null) {
            throw new IllegalArgumentException("The " + name + " argument cannot be null");
        }
    }
    
    public boolean createIfMissing() {
        return this.createIfMissing;
    }
    
    public Options createIfMissing(final boolean createIfMissing) {
        this.createIfMissing = createIfMissing;
        return this;
    }
    
    public boolean errorIfExists() {
        return this.errorIfExists;
    }
    
    public Options errorIfExists(final boolean errorIfExists) {
        this.errorIfExists = errorIfExists;
        return this;
    }
    
    public int writeBufferSize() {
        return this.writeBufferSize;
    }
    
    public Options writeBufferSize(final int writeBufferSize) {
        this.writeBufferSize = writeBufferSize;
        return this;
    }
    
    public int maxOpenFiles() {
        return this.maxOpenFiles;
    }
    
    public Options maxOpenFiles(final int maxOpenFiles) {
        this.maxOpenFiles = maxOpenFiles;
        return this;
    }
    
    public int blockRestartInterval() {
        return this.blockRestartInterval;
    }
    
    public Options blockRestartInterval(final int blockRestartInterval) {
        this.blockRestartInterval = blockRestartInterval;
        return this;
    }
    
    public int blockSize() {
        return this.blockSize;
    }
    
    public Options blockSize(final int blockSize) {
        this.blockSize = blockSize;
        return this;
    }
    
    public CompressionType compressionType() {
        return this.compressionType;
    }
    
    public Options compressionType(final CompressionType compressionType) {
        checkArgNotNull(compressionType, "compressionType");
        this.compressionType = compressionType;
        return this;
    }
    
    public boolean verifyChecksums() {
        return this.verifyChecksums;
    }
    
    public Options verifyChecksums(final boolean verifyChecksums) {
        this.verifyChecksums = verifyChecksums;
        return this;
    }
    
    public long cacheSize() {
        return this.cacheSize;
    }
    
    public Options cacheSize(final long cacheSize) {
        this.cacheSize = cacheSize;
        return this;
    }
    
    public DBComparator comparator() {
        return this.comparator;
    }
    
    public Options comparator(final DBComparator comparator) {
        this.comparator = comparator;
        return this;
    }
    
    public Logger logger() {
        return this.logger;
    }
    
    public Options logger(final Logger logger) {
        this.logger = logger;
        return this;
    }
    
    public boolean paranoidChecks() {
        return this.paranoidChecks;
    }
    
    public Options paranoidChecks(final boolean paranoidChecks) {
        this.paranoidChecks = paranoidChecks;
        return this;
    }
}
