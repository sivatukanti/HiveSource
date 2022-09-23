// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import com.google.common.annotations.VisibleForTesting;
import java.util.Collections;
import java.io.IOException;
import com.google.common.base.Preconditions;
import java.util.HashSet;
import javax.annotation.Nonnull;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.Progressable;
import java.util.EnumSet;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public abstract class FSDataOutputStreamBuilder<S extends FSDataOutputStream, B extends FSDataOutputStreamBuilder<S, B>>
{
    private final FileSystem fs;
    private final Path path;
    private FsPermission permission;
    private int bufferSize;
    private short replication;
    private long blockSize;
    private boolean recursive;
    private final EnumSet<CreateFlag> flags;
    private Progressable progress;
    private Options.ChecksumOpt checksumOpt;
    private final Configuration options;
    private final Set<String> mandatoryKeys;
    
    protected abstract B getThisBuilder();
    
    FSDataOutputStreamBuilder(@Nonnull final FileContext fc, @Nonnull final Path p) throws IOException {
        this.permission = null;
        this.recursive = false;
        this.flags = EnumSet.noneOf(CreateFlag.class);
        this.progress = null;
        this.checksumOpt = null;
        this.options = new Configuration(false);
        this.mandatoryKeys = new HashSet<String>();
        Preconditions.checkNotNull(fc);
        Preconditions.checkNotNull(p);
        this.fs = null;
        this.path = p;
        final AbstractFileSystem afs = fc.getFSofPath(p);
        final FsServerDefaults defaults = afs.getServerDefaults(p);
        this.bufferSize = defaults.getFileBufferSize();
        this.replication = defaults.getReplication();
        this.blockSize = defaults.getBlockSize();
    }
    
    protected FSDataOutputStreamBuilder(@Nonnull final FileSystem fileSystem, @Nonnull final Path p) {
        this.permission = null;
        this.recursive = false;
        this.flags = EnumSet.noneOf(CreateFlag.class);
        this.progress = null;
        this.checksumOpt = null;
        this.options = new Configuration(false);
        this.mandatoryKeys = new HashSet<String>();
        Preconditions.checkNotNull(fileSystem);
        Preconditions.checkNotNull(p);
        this.fs = fileSystem;
        this.path = p;
        this.bufferSize = this.fs.getConf().getInt("io.file.buffer.size", 4096);
        this.replication = this.fs.getDefaultReplication(this.path);
        this.blockSize = this.fs.getDefaultBlockSize(p);
    }
    
    protected FileSystem getFS() {
        Preconditions.checkNotNull(this.fs);
        return this.fs;
    }
    
    protected Path getPath() {
        return this.path;
    }
    
    protected FsPermission getPermission() {
        if (this.permission == null) {
            this.permission = FsPermission.getFileDefault();
        }
        return this.permission;
    }
    
    public B permission(@Nonnull final FsPermission perm) {
        Preconditions.checkNotNull(perm);
        this.permission = perm;
        return this.getThisBuilder();
    }
    
    protected int getBufferSize() {
        return this.bufferSize;
    }
    
    public B bufferSize(final int bufSize) {
        this.bufferSize = bufSize;
        return this.getThisBuilder();
    }
    
    protected short getReplication() {
        return this.replication;
    }
    
    public B replication(final short replica) {
        this.replication = replica;
        return this.getThisBuilder();
    }
    
    protected long getBlockSize() {
        return this.blockSize;
    }
    
    public B blockSize(final long blkSize) {
        this.blockSize = blkSize;
        return this.getThisBuilder();
    }
    
    protected boolean isRecursive() {
        return this.recursive;
    }
    
    public B recursive() {
        this.recursive = true;
        return this.getThisBuilder();
    }
    
    protected Progressable getProgress() {
        return this.progress;
    }
    
    public B progress(@Nonnull final Progressable prog) {
        Preconditions.checkNotNull(prog);
        this.progress = prog;
        return this.getThisBuilder();
    }
    
    protected EnumSet<CreateFlag> getFlags() {
        return this.flags;
    }
    
    public B create() {
        this.flags.add(CreateFlag.CREATE);
        return this.getThisBuilder();
    }
    
    public B overwrite(final boolean overwrite) {
        if (overwrite) {
            this.flags.add(CreateFlag.OVERWRITE);
        }
        else {
            this.flags.remove(CreateFlag.OVERWRITE);
        }
        return this.getThisBuilder();
    }
    
    public B append() {
        this.flags.add(CreateFlag.APPEND);
        return this.getThisBuilder();
    }
    
    protected Options.ChecksumOpt getChecksumOpt() {
        return this.checksumOpt;
    }
    
    public B checksumOpt(@Nonnull final Options.ChecksumOpt chksumOpt) {
        Preconditions.checkNotNull(chksumOpt);
        this.checksumOpt = chksumOpt;
        return this.getThisBuilder();
    }
    
    public B opt(@Nonnull final String key, @Nonnull final String value) {
        this.mandatoryKeys.remove(key);
        this.options.set(key, value);
        return this.getThisBuilder();
    }
    
    public B opt(@Nonnull final String key, final boolean value) {
        this.mandatoryKeys.remove(key);
        this.options.setBoolean(key, value);
        return this.getThisBuilder();
    }
    
    public B opt(@Nonnull final String key, final int value) {
        this.mandatoryKeys.remove(key);
        this.options.setInt(key, value);
        return this.getThisBuilder();
    }
    
    public B opt(@Nonnull final String key, final float value) {
        this.mandatoryKeys.remove(key);
        this.options.setFloat(key, value);
        return this.getThisBuilder();
    }
    
    public B opt(@Nonnull final String key, final double value) {
        this.mandatoryKeys.remove(key);
        this.options.setDouble(key, value);
        return this.getThisBuilder();
    }
    
    public B opt(@Nonnull final String key, @Nonnull final String... values) {
        this.mandatoryKeys.remove(key);
        this.options.setStrings(key, values);
        return this.getThisBuilder();
    }
    
    public B must(@Nonnull final String key, @Nonnull final String value) {
        this.mandatoryKeys.add(key);
        this.options.set(key, value);
        return this.getThisBuilder();
    }
    
    public B must(@Nonnull final String key, final boolean value) {
        this.mandatoryKeys.add(key);
        this.options.setBoolean(key, value);
        return this.getThisBuilder();
    }
    
    public B must(@Nonnull final String key, final int value) {
        this.mandatoryKeys.add(key);
        this.options.setInt(key, value);
        return this.getThisBuilder();
    }
    
    public B must(@Nonnull final String key, final float value) {
        this.mandatoryKeys.add(key);
        this.options.setFloat(key, value);
        return this.getThisBuilder();
    }
    
    public B must(@Nonnull final String key, final double value) {
        this.mandatoryKeys.add(key);
        this.options.setDouble(key, value);
        return this.getThisBuilder();
    }
    
    public B must(@Nonnull final String key, @Nonnull final String... values) {
        this.mandatoryKeys.add(key);
        this.options.setStrings(key, values);
        return this.getThisBuilder();
    }
    
    protected Configuration getOptions() {
        return this.options;
    }
    
    @VisibleForTesting
    protected Set<String> getMandatoryKeys() {
        return Collections.unmodifiableSet((Set<? extends String>)this.mandatoryKeys);
    }
    
    public abstract S build() throws IllegalArgumentException, IOException;
}
