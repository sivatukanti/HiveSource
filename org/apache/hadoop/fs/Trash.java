// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.conf.Configured;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class Trash extends Configured
{
    private static final Logger LOG;
    private TrashPolicy trashPolicy;
    
    public Trash(final Configuration conf) throws IOException {
        this(FileSystem.get(conf), conf);
    }
    
    public Trash(final FileSystem fs, final Configuration conf) throws IOException {
        super(conf);
        this.trashPolicy = TrashPolicy.getInstance(conf, fs);
    }
    
    public static boolean moveToAppropriateTrash(final FileSystem fs, final Path p, Configuration conf) throws IOException {
        final Path fullyResolvedPath = fs.resolvePath(p);
        final FileSystem fullyResolvedFs = FileSystem.get(fullyResolvedPath.toUri(), conf);
        try {
            final long trashInterval = fullyResolvedFs.getServerDefaults(fullyResolvedPath).getTrashInterval();
            if (0L != trashInterval) {
                final Configuration confCopy = new Configuration(conf);
                confCopy.setLong("fs.trash.interval", trashInterval);
                conf = confCopy;
            }
        }
        catch (Exception e) {
            Trash.LOG.warn("Failed to get server trash configuration", e);
            throw new IOException("Failed to get server trash configuration", e);
        }
        final Trash trash = new Trash(fullyResolvedFs, conf);
        return trash.moveToTrash(fullyResolvedPath);
    }
    
    public boolean isEnabled() {
        return this.trashPolicy.isEnabled();
    }
    
    public boolean moveToTrash(final Path path) throws IOException {
        return this.trashPolicy.moveToTrash(path);
    }
    
    public void checkpoint() throws IOException {
        this.trashPolicy.createCheckpoint();
    }
    
    public void expunge() throws IOException {
        this.trashPolicy.deleteCheckpoint();
    }
    
    Path getCurrentTrashDir() throws IOException {
        return this.trashPolicy.getCurrentTrashDir();
    }
    
    TrashPolicy getTrashPolicy() {
        return this.trashPolicy;
    }
    
    public Runnable getEmptier() throws IOException {
        return this.trashPolicy.getEmptier();
    }
    
    public Path getCurrentTrashDir(final Path path) throws IOException {
        return this.trashPolicy.getCurrentTrashDir(path);
    }
    
    static {
        LOG = LoggerFactory.getLogger(Trash.class);
    }
}
