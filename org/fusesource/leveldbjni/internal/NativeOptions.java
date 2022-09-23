// 
// Decompiled by Procyon v0.5.36
// 

package org.fusesource.leveldbjni.internal;

import org.fusesource.hawtjni.runtime.MethodFlag;
import org.fusesource.hawtjni.runtime.JniMethod;
import org.fusesource.hawtjni.runtime.FieldFlag;
import org.fusesource.hawtjni.runtime.JniField;
import org.fusesource.hawtjni.runtime.ClassFlag;
import org.fusesource.hawtjni.runtime.JniClass;

@JniClass(name = "leveldb::Options", flags = { ClassFlag.STRUCT, ClassFlag.CPP })
public class NativeOptions
{
    @JniField(flags = { FieldFlag.CONSTANT }, cast = "Env*", accessor = "leveldb::Env::Default()")
    private static long DEFAULT_ENV;
    private boolean create_if_missing;
    private boolean error_if_exists;
    private boolean paranoid_checks;
    @JniField(cast = "size_t")
    private long write_buffer_size;
    @JniField(cast = "size_t")
    private long block_size;
    private int max_open_files;
    private int block_restart_interval;
    @JniField(flags = { FieldFlag.FIELD_SKIP })
    private NativeComparator comparatorObject;
    @JniField(cast = "const leveldb::Comparator*")
    private long comparator;
    @JniField(flags = { FieldFlag.FIELD_SKIP })
    private NativeLogger infoLogObject;
    @JniField(cast = "leveldb::Logger*")
    private long info_log;
    @JniField(cast = "leveldb::Env*")
    private long env;
    @JniField(cast = "leveldb::Cache*")
    private long block_cache;
    @JniField(flags = { FieldFlag.FIELD_SKIP })
    private NativeCache cache;
    @JniField(cast = "leveldb::CompressionType")
    private int compression;
    
    public NativeOptions() {
        this.create_if_missing = false;
        this.error_if_exists = false;
        this.paranoid_checks = false;
        this.write_buffer_size = 4194304L;
        this.block_size = 4086L;
        this.max_open_files = 1000;
        this.block_restart_interval = 16;
        this.comparatorObject = NativeComparator.BYTEWISE_COMPARATOR;
        this.comparator = this.comparatorObject.pointer();
        this.infoLogObject = null;
        this.info_log = 0L;
        this.env = NativeOptions.DEFAULT_ENV;
        this.block_cache = 0L;
        this.compression = NativeCompressionType.kSnappyCompression.value;
    }
    
    @JniMethod(flags = { MethodFlag.CONSTANT_INITIALIZER })
    private static final native void init();
    
    public NativeOptions createIfMissing(final boolean value) {
        this.create_if_missing = value;
        return this;
    }
    
    public boolean createIfMissing() {
        return this.create_if_missing;
    }
    
    public NativeOptions errorIfExists(final boolean value) {
        this.error_if_exists = value;
        return this;
    }
    
    public boolean errorIfExists() {
        return this.error_if_exists;
    }
    
    public NativeOptions paranoidChecks(final boolean value) {
        this.paranoid_checks = value;
        return this;
    }
    
    public boolean paranoidChecks() {
        return this.paranoid_checks;
    }
    
    public NativeOptions writeBufferSize(final long value) {
        this.write_buffer_size = value;
        return this;
    }
    
    public long writeBufferSize() {
        return this.write_buffer_size;
    }
    
    public NativeOptions maxOpenFiles(final int value) {
        this.max_open_files = value;
        return this;
    }
    
    public int maxOpenFiles() {
        return this.max_open_files;
    }
    
    public NativeOptions blockRestartInterval(final int value) {
        this.block_restart_interval = value;
        return this;
    }
    
    public int blockRestartInterval() {
        return this.block_restart_interval;
    }
    
    public NativeOptions blockSize(final long value) {
        this.block_size = value;
        return this;
    }
    
    public long blockSize() {
        return this.block_size;
    }
    
    public NativeComparator comparator() {
        return this.comparatorObject;
    }
    
    public NativeOptions comparator(final NativeComparator comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("comparator cannot be null");
        }
        this.comparatorObject = comparator;
        this.comparator = comparator.pointer();
        return this;
    }
    
    public NativeLogger infoLog() {
        return this.infoLogObject;
    }
    
    public NativeOptions infoLog(final NativeLogger logger) {
        this.infoLogObject = logger;
        if (logger == null) {
            this.info_log = 0L;
        }
        else {
            this.info_log = logger.pointer();
        }
        return this;
    }
    
    public NativeCompressionType compression() {
        if (this.compression == NativeCompressionType.kNoCompression.value) {
            return NativeCompressionType.kNoCompression;
        }
        if (this.compression == NativeCompressionType.kSnappyCompression.value) {
            return NativeCompressionType.kSnappyCompression;
        }
        return NativeCompressionType.kSnappyCompression;
    }
    
    public NativeOptions compression(final NativeCompressionType compression) {
        this.compression = compression.value;
        return this;
    }
    
    public NativeCache cache() {
        return this.cache;
    }
    
    public NativeOptions cache(final NativeCache cache) {
        this.cache = cache;
        if (cache != null) {
            this.block_cache = cache.pointer();
        }
        else {
            this.block_cache = 0L;
        }
        return this;
    }
    
    static {
        NativeDB.LIBRARY.load();
        init();
    }
}
