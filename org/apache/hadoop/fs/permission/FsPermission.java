// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.permission;

import org.apache.hadoop.io.WritableFactories;
import org.slf4j.LoggerFactory;
import java.io.InvalidObjectException;
import org.apache.hadoop.conf.Configuration;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;
import org.apache.hadoop.io.WritableFactory;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.ObjectInputValidation;
import java.io.Serializable;
import org.apache.hadoop.io.Writable;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class FsPermission implements Writable, Serializable, ObjectInputValidation
{
    private static final Logger LOG;
    private static final long serialVersionUID = 803243364L;
    static final WritableFactory FACTORY;
    public static final int MAX_PERMISSION_LENGTH = 10;
    private FsAction useraction;
    private FsAction groupaction;
    private FsAction otheraction;
    private Boolean stickyBit;
    public static final String UMASK_LABEL = "fs.permissions.umask-mode";
    public static final int DEFAULT_UMASK = 18;
    private static final FsAction[] FSACTION_VALUES;
    
    public static FsPermission createImmutable(final short permission) {
        return new ImmutableFsPermission(permission);
    }
    
    private FsPermission() {
        this.useraction = null;
        this.groupaction = null;
        this.otheraction = null;
        this.stickyBit = false;
    }
    
    public FsPermission(final FsAction u, final FsAction g, final FsAction o) {
        this(u, g, o, false);
    }
    
    public FsPermission(final FsAction u, final FsAction g, final FsAction o, final boolean sb) {
        this.useraction = null;
        this.groupaction = null;
        this.otheraction = null;
        this.stickyBit = false;
        this.set(u, g, o, sb);
    }
    
    public FsPermission(final short mode) {
        this.useraction = null;
        this.groupaction = null;
        this.otheraction = null;
        this.stickyBit = false;
        this.fromShort(mode);
    }
    
    public FsPermission(final int mode) {
        this((short)(mode & 0x3FF));
    }
    
    public FsPermission(final FsPermission other) {
        this.useraction = null;
        this.groupaction = null;
        this.otheraction = null;
        this.stickyBit = false;
        this.useraction = other.useraction;
        this.groupaction = other.groupaction;
        this.otheraction = other.otheraction;
        this.stickyBit = other.stickyBit;
    }
    
    public FsPermission(final String mode) {
        this(new RawParser(mode).getPermission());
    }
    
    public FsAction getUserAction() {
        return this.useraction;
    }
    
    public FsAction getGroupAction() {
        return this.groupaction;
    }
    
    public FsAction getOtherAction() {
        return this.otheraction;
    }
    
    private void set(final FsAction u, final FsAction g, final FsAction o, final boolean sb) {
        this.useraction = u;
        this.groupaction = g;
        this.otheraction = o;
        this.stickyBit = sb;
    }
    
    public void fromShort(final short n) {
        final FsAction[] v = FsPermission.FSACTION_VALUES;
        this.set(v[n >>> 6 & 0x7], v[n >>> 3 & 0x7], v[n & 0x7], (n >>> 9 & 0x1) == 0x1);
    }
    
    @Deprecated
    @Override
    public void write(final DataOutput out) throws IOException {
        out.writeShort(this.toShort());
    }
    
    @Deprecated
    @Override
    public void readFields(final DataInput in) throws IOException {
        this.fromShort(in.readShort());
    }
    
    public FsPermission getMasked() {
        return null;
    }
    
    public FsPermission getUnmasked() {
        return null;
    }
    
    public static FsPermission read(final DataInput in) throws IOException {
        final FsPermission p = new FsPermission();
        p.fromShort(in.readShort());
        return p;
    }
    
    public short toShort() {
        final int s = (this.stickyBit ? 512 : 0) | this.useraction.ordinal() << 6 | this.groupaction.ordinal() << 3 | this.otheraction.ordinal();
        return (short)s;
    }
    
    @Deprecated
    public short toExtendedShort() {
        return this.toShort();
    }
    
    public short toOctal() {
        final int n = this.toShort();
        final int octal = (n >>> 9 & 0x1) * 1000 + (n >>> 6 & 0x7) * 100 + (n >>> 3 & 0x7) * 10 + (n & 0x7);
        return (short)octal;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof FsPermission) {
            final FsPermission that = (FsPermission)obj;
            return this.useraction == that.useraction && this.groupaction == that.groupaction && this.otheraction == that.otheraction && this.stickyBit == (boolean)that.stickyBit;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.toShort();
    }
    
    @Override
    public String toString() {
        String str = this.useraction.SYMBOL + this.groupaction.SYMBOL + this.otheraction.SYMBOL;
        if (this.stickyBit) {
            final StringBuilder str2 = new StringBuilder(str);
            str2.replace(str2.length() - 1, str2.length(), this.otheraction.implies(FsAction.EXECUTE) ? "t" : "T");
            str = str2.toString();
        }
        return str;
    }
    
    public FsPermission applyUMask(final FsPermission umask) {
        return new FsPermission(this.useraction.and(umask.useraction.not()), this.groupaction.and(umask.groupaction.not()), this.otheraction.and(umask.otheraction.not()));
    }
    
    public static FsPermission getUMask(final Configuration conf) {
        int umask = 18;
        if (conf != null) {
            final String confUmask = conf.get("fs.permissions.umask-mode");
            try {
                if (confUmask != null) {
                    umask = new UmaskParser(confUmask).getUMask();
                }
            }
            catch (IllegalArgumentException iae) {
                final String type = (iae instanceof NumberFormatException) ? "decimal" : "octal or symbolic";
                final String error = "Unable to parse configuration fs.permissions.umask-mode with value " + confUmask + " as " + type + " umask.";
                FsPermission.LOG.warn(error);
                throw new IllegalArgumentException(error);
            }
        }
        return new FsPermission((short)umask);
    }
    
    public boolean getStickyBit() {
        return this.stickyBit;
    }
    
    @Deprecated
    public boolean getAclBit() {
        return false;
    }
    
    @Deprecated
    public boolean getEncryptedBit() {
        return false;
    }
    
    @Deprecated
    public boolean getErasureCodedBit() {
        return false;
    }
    
    public static void setUMask(final Configuration conf, final FsPermission umask) {
        conf.set("fs.permissions.umask-mode", String.format("%1$03o", umask.toShort()));
    }
    
    public static FsPermission getDefault() {
        return new FsPermission((short)511);
    }
    
    public static FsPermission getDirDefault() {
        return new FsPermission((short)511);
    }
    
    public static FsPermission getFileDefault() {
        return new FsPermission((short)438);
    }
    
    public static FsPermission getCachePoolDefault() {
        return new FsPermission((short)493);
    }
    
    public static FsPermission valueOf(final String unixSymbolicPermission) {
        if (unixSymbolicPermission == null) {
            return null;
        }
        if (unixSymbolicPermission.length() != 10) {
            throw new IllegalArgumentException(String.format("length != %d(unixSymbolicPermission=%s)", 10, unixSymbolicPermission));
        }
        int n = 0;
        for (int i = 1; i < unixSymbolicPermission.length(); ++i) {
            n <<= 1;
            final char c = unixSymbolicPermission.charAt(i);
            n += ((c != '-' && c != 'T' && c != 'S') ? 1 : 0);
        }
        if (unixSymbolicPermission.charAt(9) == 't' || unixSymbolicPermission.charAt(9) == 'T') {
            n += 512;
        }
        return new FsPermission((short)n);
    }
    
    @Override
    public void validateObject() throws InvalidObjectException {
        if (null == this.useraction || null == this.groupaction || null == this.otheraction) {
            throw new InvalidObjectException("Invalid mode in FsPermission");
        }
        if (null == this.stickyBit) {
            throw new InvalidObjectException("No sticky bit in FsPermission");
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(FsPermission.class);
        WritableFactories.setFactory(FsPermission.class, FACTORY = new WritableFactory() {
            @Override
            public Writable newInstance() {
                return new FsPermission((FsPermission$1)null);
            }
        });
        WritableFactories.setFactory(ImmutableFsPermission.class, FsPermission.FACTORY);
        FSACTION_VALUES = FsAction.values();
    }
    
    private static class ImmutableFsPermission extends FsPermission
    {
        private static final long serialVersionUID = 464213181L;
        
        public ImmutableFsPermission(final short permission) {
            super(permission);
        }
        
        @Override
        public void readFields(final DataInput in) throws IOException {
            throw new UnsupportedOperationException();
        }
    }
}
