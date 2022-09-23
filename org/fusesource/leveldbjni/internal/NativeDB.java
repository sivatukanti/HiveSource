// 
// Decompiled by Procyon v0.5.36
// 

package org.fusesource.leveldbjni.internal;

import org.fusesource.hawtjni.runtime.ArgFlag;
import org.fusesource.hawtjni.runtime.JniArg;
import org.fusesource.hawtjni.runtime.MethodFlag;
import org.fusesource.hawtjni.runtime.JniMethod;
import org.fusesource.hawtjni.runtime.ClassFlag;
import org.fusesource.hawtjni.runtime.JniClass;
import java.io.IOException;
import java.io.File;
import org.fusesource.hawtjni.runtime.Library;

public class NativeDB extends NativeObject
{
    public static final Library LIBRARY;
    
    public void delete() {
        this.assertAllocated();
        DBJNI.delete(this.self);
        this.self = 0L;
    }
    
    private NativeDB(final long self) {
        super(self);
    }
    
    static void checkStatus(final long s) throws DBException {
        final NativeStatus status = new NativeStatus(s);
        try {
            if (!status.isOk()) {
                throw new DBException(status.toString(), status.isNotFound());
            }
        }
        finally {
            status.delete();
        }
    }
    
    static void checkArgNotNull(final Object value, final String name) {
        if (value == null) {
            throw new IllegalArgumentException("The " + name + " argument cannot be null");
        }
    }
    
    public static NativeDB open(final NativeOptions options, final File path) throws IOException, DBException {
        checkArgNotNull(options, "options");
        checkArgNotNull(path, "path");
        final long[] rc = { 0L };
        try {
            checkStatus(DBJNI.Open(options, path.getCanonicalPath(), rc));
        }
        catch (IOException e) {
            if (rc[0] != 0L) {
                DBJNI.delete(rc[0]);
            }
            throw e;
        }
        return new NativeDB(rc[0]);
    }
    
    public void suspendCompactions() {
        DBJNI.SuspendCompactions(this.self);
    }
    
    public void resumeCompactions() {
        DBJNI.ResumeCompactions(this.self);
    }
    
    public void put(final NativeWriteOptions options, final byte[] key, final byte[] value) throws DBException {
        checkArgNotNull(options, "options");
        checkArgNotNull(key, "key");
        checkArgNotNull(value, "value");
        final NativeBuffer keyBuffer = NativeBuffer.create(key);
        try {
            final NativeBuffer valueBuffer = NativeBuffer.create(value);
            try {
                this.put(options, keyBuffer, valueBuffer);
            }
            finally {
                valueBuffer.delete();
            }
        }
        finally {
            keyBuffer.delete();
        }
    }
    
    private void put(final NativeWriteOptions options, final NativeBuffer keyBuffer, final NativeBuffer valueBuffer) throws DBException {
        this.put(options, new NativeSlice(keyBuffer), new NativeSlice(valueBuffer));
    }
    
    private void put(final NativeWriteOptions options, final NativeSlice keySlice, final NativeSlice valueSlice) throws DBException {
        this.assertAllocated();
        checkStatus(DBJNI.Put(this.self, options, keySlice, valueSlice));
    }
    
    public void delete(final NativeWriteOptions options, final byte[] key) throws DBException {
        checkArgNotNull(options, "options");
        checkArgNotNull(key, "key");
        final NativeBuffer keyBuffer = NativeBuffer.create(key);
        try {
            this.delete(options, keyBuffer);
        }
        finally {
            keyBuffer.delete();
        }
    }
    
    private void delete(final NativeWriteOptions options, final NativeBuffer keyBuffer) throws DBException {
        this.delete(options, new NativeSlice(keyBuffer));
    }
    
    private void delete(final NativeWriteOptions options, final NativeSlice keySlice) throws DBException {
        this.assertAllocated();
        checkStatus(DBJNI.Delete(this.self, options, keySlice));
    }
    
    public void write(final NativeWriteOptions options, final NativeWriteBatch updates) throws DBException {
        checkArgNotNull(options, "options");
        checkArgNotNull(updates, "updates");
        checkStatus(DBJNI.Write(this.self, options, updates.pointer()));
    }
    
    public byte[] get(final NativeReadOptions options, final byte[] key) throws DBException {
        checkArgNotNull(options, "options");
        checkArgNotNull(key, "key");
        final NativeBuffer keyBuffer = NativeBuffer.create(key);
        try {
            return this.get(options, keyBuffer);
        }
        finally {
            keyBuffer.delete();
        }
    }
    
    private byte[] get(final NativeReadOptions options, final NativeBuffer keyBuffer) throws DBException {
        return this.get(options, new NativeSlice(keyBuffer));
    }
    
    private byte[] get(final NativeReadOptions options, final NativeSlice keySlice) throws DBException {
        this.assertAllocated();
        final NativeStdString result = new NativeStdString();
        try {
            final long s = DBJNI.Get(this.self, options, keySlice, result.pointer());
            final NativeStatus status = new NativeStatus(s);
            try {
                if (status.isOk()) {
                    return result.toByteArray();
                }
                if (status.isNotFound()) {
                    return null;
                }
                throw new DBException(status.toString(), status.isNotFound());
            }
            finally {
                status.delete();
            }
        }
        finally {
            result.delete();
        }
    }
    
    public NativeSnapshot getSnapshot() {
        return new NativeSnapshot(DBJNI.GetSnapshot(this.self));
    }
    
    public void releaseSnapshot(final NativeSnapshot snapshot) {
        checkArgNotNull(snapshot, "snapshot");
        DBJNI.ReleaseSnapshot(this.self, snapshot.pointer());
    }
    
    public NativeIterator iterator(final NativeReadOptions options) {
        checkArgNotNull(options, "options");
        return new NativeIterator(DBJNI.NewIterator(this.self, options));
    }
    
    public long[] getApproximateSizes(final NativeRange... ranges) {
        if (ranges == null) {
            return null;
        }
        final long[] rc = new long[ranges.length];
        final NativeRange.RangeJNI[] structs = new NativeRange.RangeJNI[ranges.length];
        if (rc.length > 0) {
            final NativeBuffer range_array = NativeRange.RangeJNI.arrayCreate(ranges.length);
            try {
                for (int i = 0; i < ranges.length; ++i) {
                    (structs[i] = new NativeRange.RangeJNI(ranges[i])).arrayWrite(range_array.pointer(), i);
                }
                DBJNI.GetApproximateSizes(this.self, range_array.pointer(), ranges.length, rc);
            }
            finally {
                for (int j = 0; j < ranges.length; ++j) {
                    if (structs[j] != null) {
                        structs[j].delete();
                    }
                }
                range_array.delete();
            }
        }
        return rc;
    }
    
    public String getProperty(final String name) {
        checkArgNotNull(name, "name");
        final NativeBuffer keyBuffer = NativeBuffer.create(name.getBytes());
        try {
            final byte[] property = this.getProperty(keyBuffer);
            if (property == null) {
                return null;
            }
            return new String(property);
        }
        finally {
            keyBuffer.delete();
        }
    }
    
    private byte[] getProperty(final NativeBuffer nameBuffer) {
        return this.getProperty(new NativeSlice(nameBuffer));
    }
    
    private byte[] getProperty(final NativeSlice nameSlice) {
        this.assertAllocated();
        final NativeStdString result = new NativeStdString();
        try {
            if (DBJNI.GetProperty(this.self, nameSlice, result.pointer())) {
                return result.toByteArray();
            }
            return null;
        }
        finally {
            result.delete();
        }
    }
    
    public void compactRange(final byte[] begin, final byte[] end) {
        final NativeBuffer keyBuffer = NativeBuffer.create(begin);
        try {
            final NativeBuffer valueBuffer = NativeBuffer.create(end);
            try {
                this.compactRange(keyBuffer, valueBuffer);
            }
            finally {
                if (valueBuffer != null) {
                    valueBuffer.delete();
                }
            }
        }
        finally {
            if (keyBuffer != null) {
                keyBuffer.delete();
            }
        }
    }
    
    private void compactRange(final NativeBuffer beginBuffer, final NativeBuffer endBuffer) {
        this.compactRange(NativeSlice.create(beginBuffer), NativeSlice.create(endBuffer));
    }
    
    private void compactRange(final NativeSlice beginSlice, final NativeSlice endSlice) {
        this.assertAllocated();
        DBJNI.CompactRange(this.self, beginSlice, endSlice);
    }
    
    public static void destroy(final File path, final NativeOptions options) throws IOException, DBException {
        checkArgNotNull(options, "options");
        checkArgNotNull(path, "path");
        checkStatus(DBJNI.DestroyDB(path.getCanonicalPath(), options));
    }
    
    public static void repair(final File path, final NativeOptions options) throws IOException, DBException {
        checkArgNotNull(options, "options");
        checkArgNotNull(path, "path");
        checkStatus(DBJNI.RepairDB(path.getCanonicalPath(), options));
    }
    
    static {
        LIBRARY = new Library("leveldbjni", NativeDB.class);
    }
    
    @JniClass(name = "leveldb::DB", flags = { ClassFlag.CPP })
    static class DBJNI
    {
        @JniMethod(flags = { MethodFlag.JNI, MethodFlag.POINTER_RETURN }, cast = "jobject")
        public static final native long NewGlobalRef(final Object p0);
        
        @JniMethod(flags = { MethodFlag.JNI }, cast = "jobject")
        public static final native void DeleteGlobalRef(@JniArg(cast = "jobject", flags = { ArgFlag.POINTER_ARG }) final long p0);
        
        @JniMethod(flags = { MethodFlag.JNI, MethodFlag.POINTER_RETURN }, cast = "jmethodID")
        public static final native long GetMethodID(@JniArg(cast = "jclass", flags = { ArgFlag.POINTER_ARG }) final Class p0, final String p1, final String p2);
        
        @JniMethod(flags = { MethodFlag.CPP_DELETE })
        static final native void delete(final long p0);
        
        @JniMethod(copy = "leveldb::Status", accessor = "leveldb::DB::Open")
        static final native long Open(@JniArg(flags = { ArgFlag.BY_VALUE, ArgFlag.NO_OUT }) final NativeOptions p0, @JniArg(cast = "const char*") final String p1, @JniArg(cast = "leveldb::DB**") final long[] p2);
        
        @JniMethod(copy = "leveldb::Status", flags = { MethodFlag.CPP_METHOD })
        static final native long Put(final long p0, @JniArg(flags = { ArgFlag.BY_VALUE, ArgFlag.NO_OUT }) final NativeWriteOptions p1, @JniArg(flags = { ArgFlag.BY_VALUE, ArgFlag.NO_OUT }) final NativeSlice p2, @JniArg(flags = { ArgFlag.BY_VALUE, ArgFlag.NO_OUT }) final NativeSlice p3);
        
        @JniMethod(copy = "leveldb::Status", flags = { MethodFlag.CPP_METHOD })
        static final native long Delete(final long p0, @JniArg(flags = { ArgFlag.BY_VALUE, ArgFlag.NO_OUT }) final NativeWriteOptions p1, @JniArg(flags = { ArgFlag.BY_VALUE, ArgFlag.NO_OUT }) final NativeSlice p2);
        
        @JniMethod(copy = "leveldb::Status", flags = { MethodFlag.CPP_METHOD })
        static final native long Write(final long p0, @JniArg(flags = { ArgFlag.BY_VALUE }) final NativeWriteOptions p1, @JniArg(cast = "leveldb::WriteBatch *") final long p2);
        
        @JniMethod(copy = "leveldb::Status", flags = { MethodFlag.CPP_METHOD })
        static final native long Get(final long p0, @JniArg(flags = { ArgFlag.NO_OUT, ArgFlag.BY_VALUE }) final NativeReadOptions p1, @JniArg(flags = { ArgFlag.BY_VALUE, ArgFlag.NO_OUT }) final NativeSlice p2, @JniArg(cast = "std::string *") final long p3);
        
        @JniMethod(cast = "leveldb::Iterator *", flags = { MethodFlag.CPP_METHOD })
        static final native long NewIterator(final long p0, @JniArg(flags = { ArgFlag.NO_OUT, ArgFlag.BY_VALUE }) final NativeReadOptions p1);
        
        @JniMethod(cast = "leveldb::Snapshot *", flags = { MethodFlag.CPP_METHOD })
        static final native long GetSnapshot(final long p0);
        
        @JniMethod(flags = { MethodFlag.CPP_METHOD })
        static final native void ReleaseSnapshot(final long p0, @JniArg(cast = "const leveldb::Snapshot *") final long p1);
        
        @JniMethod(flags = { MethodFlag.CPP_METHOD })
        static final native void GetApproximateSizes(final long p0, @JniArg(cast = "const leveldb::Range *") final long p1, final int p2, @JniArg(cast = "uint64_t*") final long[] p3);
        
        @JniMethod(flags = { MethodFlag.CPP_METHOD })
        static final native boolean GetProperty(final long p0, @JniArg(flags = { ArgFlag.BY_VALUE, ArgFlag.NO_OUT }) final NativeSlice p1, @JniArg(cast = "std::string *") final long p2);
        
        @JniMethod(copy = "leveldb::Status", accessor = "leveldb::DestroyDB")
        static final native long DestroyDB(@JniArg(cast = "const char*") final String p0, @JniArg(flags = { ArgFlag.BY_VALUE, ArgFlag.NO_OUT }) final NativeOptions p1);
        
        @JniMethod(copy = "leveldb::Status", accessor = "leveldb::RepairDB")
        static final native long RepairDB(@JniArg(cast = "const char*") final String p0, @JniArg(flags = { ArgFlag.BY_VALUE, ArgFlag.NO_OUT }) final NativeOptions p1);
        
        @JniMethod(flags = { MethodFlag.CPP_METHOD })
        static final native void CompactRange(final long p0, @JniArg(flags = { ArgFlag.NO_OUT }) final NativeSlice p1, @JniArg(flags = { ArgFlag.NO_OUT }) final NativeSlice p2);
        
        @JniMethod(flags = { MethodFlag.CPP_METHOD })
        static final native void SuspendCompactions(final long p0);
        
        @JniMethod(flags = { MethodFlag.CPP_METHOD })
        static final native void ResumeCompactions(final long p0);
        
        static {
            NativeDB.LIBRARY.load();
        }
    }
    
    public static class DBException extends IOException
    {
        private final boolean notFound;
        
        DBException(final String s, final boolean notFound) {
            super(s);
            this.notFound = notFound;
        }
        
        public boolean isNotFound() {
            return this.notFound;
        }
    }
}
