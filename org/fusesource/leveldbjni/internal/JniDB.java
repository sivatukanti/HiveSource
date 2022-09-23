// 
// Decompiled by Procyon v0.5.36
// 

package org.fusesource.leveldbjni.internal;

import java.util.Iterator;
import org.iq80.leveldb.Range;
import org.iq80.leveldb.Snapshot;
import org.iq80.leveldb.WriteBatch;
import org.iq80.leveldb.WriteOptions;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.ReadOptions;
import org.iq80.leveldb.DBException;
import org.iq80.leveldb.DB;

public class JniDB implements DB
{
    private NativeDB db;
    private NativeCache cache;
    private NativeComparator comparator;
    private NativeLogger logger;
    
    public JniDB(final NativeDB db, final NativeCache cache, final NativeComparator comparator, final NativeLogger logger) {
        this.db = db;
        this.cache = cache;
        this.comparator = comparator;
        this.logger = logger;
    }
    
    public void close() {
        if (this.db != null) {
            this.db.delete();
            this.db = null;
            if (this.cache != null) {
                this.cache.delete();
                this.cache = null;
            }
            if (this.comparator != null) {
                this.comparator.delete();
                this.comparator = null;
            }
            if (this.logger != null) {
                this.logger.delete();
                this.logger = null;
            }
        }
    }
    
    public byte[] get(final byte[] key) throws DBException {
        if (this.db == null) {
            throw new DBException("Closed");
        }
        return this.get(key, new ReadOptions());
    }
    
    public byte[] get(final byte[] key, final ReadOptions options) throws DBException {
        if (this.db == null) {
            throw new DBException("Closed");
        }
        try {
            return this.db.get(this.convert(options), key);
        }
        catch (NativeDB.DBException e) {
            if (e.isNotFound()) {
                return null;
            }
            throw new DBException(e.getMessage(), e);
        }
    }
    
    public DBIterator iterator() {
        return this.iterator(new ReadOptions());
    }
    
    public DBIterator iterator(final ReadOptions options) {
        if (this.db == null) {
            throw new DBException("Closed");
        }
        return new JniDBIterator(this.db.iterator(this.convert(options)));
    }
    
    public void put(final byte[] key, final byte[] value) throws DBException {
        this.put(key, value, new WriteOptions());
    }
    
    public void delete(final byte[] key) throws DBException {
        this.delete(key, new WriteOptions());
    }
    
    public void write(final WriteBatch updates) throws DBException {
        this.write(updates, new WriteOptions());
    }
    
    public WriteBatch createWriteBatch() {
        return new JniWriteBatch(new NativeWriteBatch());
    }
    
    public Snapshot put(final byte[] key, final byte[] value, final WriteOptions options) throws DBException {
        if (this.db == null) {
            throw new DBException("Closed");
        }
        try {
            this.db.put(this.convert(options), key, value);
            return null;
        }
        catch (NativeDB.DBException e) {
            throw new DBException(e.getMessage(), e);
        }
    }
    
    public Snapshot delete(final byte[] key, final WriteOptions options) throws DBException {
        if (this.db == null) {
            throw new DBException("Closed");
        }
        try {
            this.db.delete(this.convert(options), key);
            return null;
        }
        catch (NativeDB.DBException e) {
            throw new DBException(e.getMessage(), e);
        }
    }
    
    public Snapshot write(final WriteBatch updates, final WriteOptions options) throws DBException {
        if (this.db == null) {
            throw new DBException("Closed");
        }
        try {
            this.db.write(this.convert(options), ((JniWriteBatch)updates).writeBatch());
            return null;
        }
        catch (NativeDB.DBException e) {
            throw new DBException(e.getMessage(), e);
        }
    }
    
    public Snapshot getSnapshot() {
        if (this.db == null) {
            throw new DBException("Closed");
        }
        return new JniSnapshot(this.db, this.db.getSnapshot());
    }
    
    public long[] getApproximateSizes(final Range... ranges) {
        if (this.db == null) {
            throw new DBException("Closed");
        }
        final NativeRange[] args = new NativeRange[ranges.length];
        for (int i = 0; i < args.length; ++i) {
            args[i] = new NativeRange(ranges[i].start(), ranges[i].limit());
        }
        return this.db.getApproximateSizes(args);
    }
    
    public String getProperty(final String name) {
        if (this.db == null) {
            throw new DBException("Closed");
        }
        return this.db.getProperty(name);
    }
    
    private NativeReadOptions convert(final ReadOptions options) {
        if (options == null) {
            return null;
        }
        final NativeReadOptions rc = new NativeReadOptions();
        rc.fillCache(options.fillCache());
        rc.verifyChecksums(options.verifyChecksums());
        if (options.snapshot() != null) {
            rc.snapshot(((JniSnapshot)options.snapshot()).snapshot());
        }
        return rc;
    }
    
    private NativeWriteOptions convert(final WriteOptions options) {
        if (options == null) {
            return null;
        }
        final NativeWriteOptions rc = new NativeWriteOptions();
        rc.sync(options.sync());
        if (options.snapshot()) {
            throw new UnsupportedOperationException("WriteOptions snapshot not supported");
        }
        return rc;
    }
    
    public void compactRange(final byte[] begin, final byte[] end) throws DBException {
        if (this.db == null) {
            throw new DBException("Closed");
        }
        this.db.compactRange(begin, end);
    }
    
    public void suspendCompactions() throws InterruptedException {
        if (this.db == null) {
            throw new DBException("Closed");
        }
        this.db.suspendCompactions();
    }
    
    public void resumeCompactions() {
        if (this.db == null) {
            throw new DBException("Closed");
        }
        this.db.resumeCompactions();
    }
}
