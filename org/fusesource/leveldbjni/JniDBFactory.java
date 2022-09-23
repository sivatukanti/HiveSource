// 
// Decompiled by Procyon v0.5.36
// 

package org.fusesource.leveldbjni;

import org.iq80.leveldb.Logger;
import org.iq80.leveldb.DBComparator;
import org.fusesource.leveldbjni.internal.NativeCompressionType;
import org.fusesource.leveldbjni.internal.NativeOptions;
import org.fusesource.leveldbjni.internal.NativeLogger;
import org.fusesource.leveldbjni.internal.NativeComparator;
import org.fusesource.leveldbjni.internal.NativeCache;
import java.io.InputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.fusesource.leveldbjni.internal.NativeBuffer;
import java.io.IOException;
import org.fusesource.leveldbjni.internal.JniDB;
import org.fusesource.leveldbjni.internal.NativeDB;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import java.io.File;
import java.io.UnsupportedEncodingException;
import org.iq80.leveldb.DBFactory;

public class JniDBFactory implements DBFactory
{
    public static final JniDBFactory factory;
    public static final String VERSION;
    
    public static byte[] bytes(final String value) {
        if (value == null) {
            return null;
        }
        try {
            return value.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static String asString(final byte[] value) {
        if (value == null) {
            return null;
        }
        try {
            return new String(value, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public DB open(final File path, final Options options) throws IOException {
        NativeDB db = null;
        final OptionsResourceHolder holder = new OptionsResourceHolder();
        try {
            holder.init(options);
            db = NativeDB.open(holder.options, path);
        }
        finally {
            if (db == null) {
                holder.close();
            }
        }
        return new JniDB(db, holder.cache, holder.comparator, holder.logger);
    }
    
    public void destroy(final File path, final Options options) throws IOException {
        final OptionsResourceHolder holder = new OptionsResourceHolder();
        try {
            holder.init(options);
            NativeDB.destroy(path, holder.options);
        }
        finally {
            holder.close();
        }
    }
    
    public void repair(final File path, final Options options) throws IOException {
        final OptionsResourceHolder holder = new OptionsResourceHolder();
        try {
            holder.init(options);
            NativeDB.repair(path, holder.options);
        }
        finally {
            holder.close();
        }
    }
    
    @Override
    public String toString() {
        return String.format("leveldbjni version %s", JniDBFactory.VERSION);
    }
    
    public static void pushMemoryPool(final int size) {
        NativeBuffer.pushMemoryPool(size);
    }
    
    public static void popMemoryPool() {
        NativeBuffer.popMemoryPool();
    }
    
    static {
        factory = new JniDBFactory();
        NativeDB.LIBRARY.load();
        String v = "unknown";
        final InputStream is = JniDBFactory.class.getResourceAsStream("version.txt");
        try {
            v = new BufferedReader(new InputStreamReader(is, "UTF-8")).readLine();
        }
        catch (Throwable e) {}
        finally {
            try {
                is.close();
            }
            catch (Throwable t) {}
        }
        VERSION = v;
    }
    
    private static class OptionsResourceHolder
    {
        NativeCache cache;
        NativeComparator comparator;
        NativeLogger logger;
        NativeOptions options;
        
        private OptionsResourceHolder() {
            this.cache = null;
            this.comparator = null;
            this.logger = null;
        }
        
        public void init(final Options value) {
            (this.options = new NativeOptions()).blockRestartInterval(value.blockRestartInterval());
            this.options.blockSize(value.blockSize());
            this.options.createIfMissing(value.createIfMissing());
            this.options.errorIfExists(value.errorIfExists());
            this.options.maxOpenFiles(value.maxOpenFiles());
            this.options.paranoidChecks(value.paranoidChecks());
            this.options.writeBufferSize(value.writeBufferSize());
            switch (value.compressionType()) {
                case NONE: {
                    this.options.compression(NativeCompressionType.kNoCompression);
                    break;
                }
                case SNAPPY: {
                    this.options.compression(NativeCompressionType.kSnappyCompression);
                    break;
                }
            }
            if (value.cacheSize() > 0L) {
                this.cache = new NativeCache(value.cacheSize());
                this.options.cache(this.cache);
            }
            final DBComparator userComparator = value.comparator();
            if (userComparator != null) {
                this.comparator = new NativeComparator() {
                    @Override
                    public int compare(final byte[] key1, final byte[] key2) {
                        return userComparator.compare(key1, key2);
                    }
                    
                    @Override
                    public String name() {
                        return userComparator.name();
                    }
                };
                this.options.comparator(this.comparator);
            }
            final Logger userLogger = value.logger();
            if (userLogger != null) {
                this.logger = new NativeLogger() {
                    @Override
                    public void log(final String message) {
                        userLogger.log(message);
                    }
                };
                this.options.infoLog(this.logger);
            }
        }
        
        public void close() {
            if (this.cache != null) {
                this.cache.delete();
            }
            if (this.comparator != null) {
                this.comparator.delete();
            }
            if (this.logger != null) {
                this.logger.delete();
            }
        }
    }
}
