// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.apache.hadoop.util.DataChecksum;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.util.Progressable;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public final class Options
{
    public static class CreateOpts
    {
        private CreateOpts() {
        }
        
        public static BlockSize blockSize(final long bs) {
            return new BlockSize(bs);
        }
        
        public static BufferSize bufferSize(final int bs) {
            return new BufferSize(bs);
        }
        
        public static ReplicationFactor repFac(final short rf) {
            return new ReplicationFactor(rf);
        }
        
        public static BytesPerChecksum bytesPerChecksum(final short crc) {
            return new BytesPerChecksum(crc);
        }
        
        public static ChecksumParam checksumParam(final ChecksumOpt csumOpt) {
            return new ChecksumParam(csumOpt);
        }
        
        public static Progress progress(final Progressable prog) {
            return new Progress(prog);
        }
        
        public static Perms perms(final FsPermission perm) {
            return new Perms(perm);
        }
        
        public static CreateParent createParent() {
            return new CreateParent(true);
        }
        
        public static CreateParent donotCreateParent() {
            return new CreateParent(false);
        }
        
        static <T extends CreateOpts> T getOpt(final Class<T> clazz, final CreateOpts... opts) {
            if (opts == null) {
                throw new IllegalArgumentException("Null opt");
            }
            T result = null;
            for (int i = 0; i < opts.length; ++i) {
                if (opts[i].getClass() == clazz) {
                    if (result != null) {
                        throw new IllegalArgumentException("multiple opts varargs: " + clazz);
                    }
                    final T t = result = (T)opts[i];
                }
            }
            return result;
        }
        
        static <T extends CreateOpts> CreateOpts[] setOpt(final T newValue, final CreateOpts... opts) {
            final Class<?> clazz = newValue.getClass();
            boolean alreadyInOpts = false;
            if (opts != null) {
                for (int i = 0; i < opts.length; ++i) {
                    if (opts[i].getClass() == clazz) {
                        if (alreadyInOpts) {
                            throw new IllegalArgumentException("multiple opts varargs: " + clazz);
                        }
                        alreadyInOpts = true;
                        opts[i] = newValue;
                    }
                }
            }
            CreateOpts[] resultOpt = opts;
            if (!alreadyInOpts) {
                final int oldLength = (opts == null) ? 0 : opts.length;
                final CreateOpts[] newOpts = new CreateOpts[oldLength + 1];
                if (oldLength > 0) {
                    System.arraycopy(opts, 0, newOpts, 0, oldLength);
                }
                newOpts[oldLength] = newValue;
                resultOpt = newOpts;
            }
            return resultOpt;
        }
        
        public static class BlockSize extends CreateOpts
        {
            private final long blockSize;
            
            protected BlockSize(final long bs) {
                if (bs <= 0L) {
                    throw new IllegalArgumentException("Block size must be greater than 0");
                }
                this.blockSize = bs;
            }
            
            public long getValue() {
                return this.blockSize;
            }
        }
        
        public static class ReplicationFactor extends CreateOpts
        {
            private final short replication;
            
            protected ReplicationFactor(final short rf) {
                if (rf <= 0) {
                    throw new IllegalArgumentException("Replication must be greater than 0");
                }
                this.replication = rf;
            }
            
            public short getValue() {
                return this.replication;
            }
        }
        
        public static class BufferSize extends CreateOpts
        {
            private final int bufferSize;
            
            protected BufferSize(final int bs) {
                if (bs <= 0) {
                    throw new IllegalArgumentException("Buffer size must be greater than 0");
                }
                this.bufferSize = bs;
            }
            
            public int getValue() {
                return this.bufferSize;
            }
        }
        
        public static class BytesPerChecksum extends CreateOpts
        {
            private final int bytesPerChecksum;
            
            protected BytesPerChecksum(final short bpc) {
                if (bpc <= 0) {
                    throw new IllegalArgumentException("Bytes per checksum must be greater than 0");
                }
                this.bytesPerChecksum = bpc;
            }
            
            public int getValue() {
                return this.bytesPerChecksum;
            }
        }
        
        public static class ChecksumParam extends CreateOpts
        {
            private final ChecksumOpt checksumOpt;
            
            protected ChecksumParam(final ChecksumOpt csumOpt) {
                this.checksumOpt = csumOpt;
            }
            
            public ChecksumOpt getValue() {
                return this.checksumOpt;
            }
        }
        
        public static class Perms extends CreateOpts
        {
            private final FsPermission permissions;
            
            protected Perms(final FsPermission perm) {
                if (perm == null) {
                    throw new IllegalArgumentException("Permissions must not be null");
                }
                this.permissions = perm;
            }
            
            public FsPermission getValue() {
                return this.permissions;
            }
        }
        
        public static class Progress extends CreateOpts
        {
            private final Progressable progress;
            
            protected Progress(final Progressable prog) {
                if (prog == null) {
                    throw new IllegalArgumentException("Progress must not be null");
                }
                this.progress = prog;
            }
            
            public Progressable getValue() {
                return this.progress;
            }
        }
        
        public static class CreateParent extends CreateOpts
        {
            private final boolean createParent;
            
            protected CreateParent(final boolean createPar) {
                this.createParent = createPar;
            }
            
            public boolean getValue() {
                return this.createParent;
            }
        }
    }
    
    public enum Rename
    {
        NONE((byte)0), 
        OVERWRITE((byte)1), 
        TO_TRASH((byte)2);
        
        private final byte code;
        
        private Rename(final byte code) {
            this.code = code;
        }
        
        public static Rename valueOf(final byte code) {
            return (code < 0 || code >= values().length) ? null : values()[code];
        }
        
        public byte value() {
            return this.code;
        }
    }
    
    public static class ChecksumOpt
    {
        private final DataChecksum.Type checksumType;
        private final int bytesPerChecksum;
        
        public ChecksumOpt() {
            this(DataChecksum.Type.DEFAULT, -1);
        }
        
        public ChecksumOpt(final DataChecksum.Type type, final int size) {
            this.checksumType = type;
            this.bytesPerChecksum = size;
        }
        
        public int getBytesPerChecksum() {
            return this.bytesPerChecksum;
        }
        
        public DataChecksum.Type getChecksumType() {
            return this.checksumType;
        }
        
        @Override
        public String toString() {
            return this.checksumType + ":" + this.bytesPerChecksum;
        }
        
        public static ChecksumOpt createDisabled() {
            return new ChecksumOpt(DataChecksum.Type.NULL, -1);
        }
        
        public static ChecksumOpt processChecksumOpt(final ChecksumOpt defaultOpt, final ChecksumOpt userOpt, final int userBytesPerChecksum) {
            boolean useDefaultType;
            DataChecksum.Type type;
            if (userOpt != null && userOpt.getChecksumType() != DataChecksum.Type.DEFAULT) {
                useDefaultType = false;
                type = userOpt.getChecksumType();
            }
            else {
                useDefaultType = true;
                type = defaultOpt.getChecksumType();
            }
            if (userBytesPerChecksum > 0) {
                return new ChecksumOpt(type, userBytesPerChecksum);
            }
            if (userOpt != null && userOpt.getBytesPerChecksum() > 0) {
                return useDefaultType ? new ChecksumOpt(type, userOpt.getBytesPerChecksum()) : userOpt;
            }
            return useDefaultType ? defaultOpt : new ChecksumOpt(type, defaultOpt.getBytesPerChecksum());
        }
        
        public static ChecksumOpt processChecksumOpt(final ChecksumOpt defaultOpt, final ChecksumOpt userOpt) {
            return processChecksumOpt(defaultOpt, userOpt, -1);
        }
    }
    
    public static class HandleOpt
    {
        protected HandleOpt() {
        }
        
        public static Function<FileStatus, PathHandle> resolve(final FileSystem fs, final HandleOpt... opt) {
            return resolve(fs::getPathHandle, opt);
        }
        
        public static Function<FileStatus, PathHandle> resolve(final BiFunction<FileStatus, HandleOpt[], PathHandle> fsr, final HandleOpt... opt) {
            return (Function<FileStatus, PathHandle>)(stat -> fsr.apply(stat, opt));
        }
        
        public static HandleOpt[] exact() {
            return new HandleOpt[] { changed(false), moved(false) };
        }
        
        public static HandleOpt[] content() {
            return new HandleOpt[] { changed(false), moved(true) };
        }
        
        public static HandleOpt[] path() {
            return new HandleOpt[] { changed(true), moved(false) };
        }
        
        public static HandleOpt[] reference() {
            return new HandleOpt[] { changed(true), moved(true) };
        }
        
        public static Data changed(final boolean allow) {
            return new Data(allow);
        }
        
        public static Location moved(final boolean allow) {
            return new Location(allow);
        }
        
        public static <T extends HandleOpt> Optional<T> getOpt(final Class<T> c, final HandleOpt... opt) {
            if (null == opt) {
                return Optional.empty();
            }
            T ret = null;
            for (final HandleOpt o : opt) {
                if (c.isAssignableFrom(o.getClass())) {
                    if (ret != null) {
                        throw new IllegalArgumentException("Duplicate option " + c.getSimpleName());
                    }
                    final T tmp = ret = (T)o;
                }
            }
            return Optional.ofNullable(ret);
        }
        
        public static class Data extends HandleOpt
        {
            private final boolean allowChanged;
            
            Data(final boolean allowChanged) {
                this.allowChanged = allowChanged;
            }
            
            public boolean allowChange() {
                return this.allowChanged;
            }
            
            @Override
            public String toString() {
                final StringBuilder sb = new StringBuilder();
                sb.append("data(allowChange=").append(this.allowChanged).append(")");
                return sb.toString();
            }
        }
        
        public static class Location extends HandleOpt
        {
            private final boolean allowChanged;
            
            Location(final boolean allowChanged) {
                this.allowChanged = allowChanged;
            }
            
            public boolean allowChange() {
                return this.allowChanged;
            }
            
            @Override
            public String toString() {
                final StringBuilder sb = new StringBuilder();
                sb.append("loc(allowChange=").append(this.allowChanged).append(")");
                return sb.toString();
            }
        }
    }
    
    public enum ChecksumCombineMode
    {
        MD5MD5CRC, 
        COMPOSITE_CRC;
    }
}
