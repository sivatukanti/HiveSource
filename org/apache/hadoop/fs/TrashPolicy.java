// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import org.apache.hadoop.util.ReflectionUtils;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.conf.Configured;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public abstract class TrashPolicy extends Configured
{
    protected FileSystem fs;
    protected Path trash;
    protected long deletionInterval;
    
    @Deprecated
    public abstract void initialize(final Configuration p0, final FileSystem p1, final Path p2);
    
    public void initialize(final Configuration conf, final FileSystem fs) {
        throw new UnsupportedOperationException();
    }
    
    public abstract boolean isEnabled();
    
    public abstract boolean moveToTrash(final Path p0) throws IOException;
    
    public abstract void createCheckpoint() throws IOException;
    
    public abstract void deleteCheckpoint() throws IOException;
    
    public abstract Path getCurrentTrashDir();
    
    public Path getCurrentTrashDir(final Path path) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    public abstract Runnable getEmptier() throws IOException;
    
    @Deprecated
    public static TrashPolicy getInstance(final Configuration conf, final FileSystem fs, final Path home) {
        final Class<? extends TrashPolicy> trashClass = conf.getClass("fs.trash.classname", TrashPolicyDefault.class, TrashPolicy.class);
        final TrashPolicy trash = ReflectionUtils.newInstance(trashClass, conf);
        trash.initialize(conf, fs, home);
        return trash;
    }
    
    public static TrashPolicy getInstance(final Configuration conf, final FileSystem fs) {
        final Class<? extends TrashPolicy> trashClass = conf.getClass("fs.trash.classname", TrashPolicyDefault.class, TrashPolicy.class);
        final TrashPolicy trash = ReflectionUtils.newInstance(trashClass, conf);
        trash.initialize(conf, fs);
        return trash;
    }
}
