// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import com.google.common.annotations.VisibleForTesting;
import java.text.SimpleDateFormat;
import org.apache.hadoop.fs.permission.FsAction;
import org.slf4j.LoggerFactory;
import java.text.ParseException;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Collection;
import java.util.Date;
import org.apache.hadoop.util.Time;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import java.text.DateFormat;
import org.apache.hadoop.fs.permission.FsPermission;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public class TrashPolicyDefault extends TrashPolicy
{
    private static final Logger LOG;
    private static final Path CURRENT;
    private static final FsPermission PERMISSION;
    private static final DateFormat CHECKPOINT;
    private static final DateFormat OLD_CHECKPOINT;
    private static final int MSECS_PER_MINUTE = 60000;
    private long emptierInterval;
    
    public TrashPolicyDefault() {
    }
    
    private TrashPolicyDefault(final FileSystem fs, final Configuration conf) throws IOException {
        this.initialize(conf, fs);
    }
    
    @Deprecated
    @Override
    public void initialize(final Configuration conf, final FileSystem fs, final Path home) {
        this.fs = fs;
        this.deletionInterval = (long)(conf.getFloat("fs.trash.interval", 0.0f) * 60000.0f);
        this.emptierInterval = (long)(conf.getFloat("fs.trash.checkpoint.interval", 0.0f) * 60000.0f);
    }
    
    @Override
    public void initialize(final Configuration conf, final FileSystem fs) {
        this.fs = fs;
        this.deletionInterval = (long)(conf.getFloat("fs.trash.interval", 0.0f) * 60000.0f);
        this.emptierInterval = (long)(conf.getFloat("fs.trash.checkpoint.interval", 0.0f) * 60000.0f);
        if (this.deletionInterval < 0L) {
            TrashPolicyDefault.LOG.warn("Invalid value {} for deletion interval, deletion interaval can not be negative.Changing to default value 0", (Object)this.deletionInterval);
            this.deletionInterval = 0L;
        }
    }
    
    private Path makeTrashRelativePath(final Path basePath, final Path rmFilePath) {
        return Path.mergePaths(basePath, rmFilePath);
    }
    
    @Override
    public boolean isEnabled() {
        return this.deletionInterval > 0L;
    }
    
    @Override
    public boolean moveToTrash(Path path) throws IOException {
        if (!this.isEnabled()) {
            return false;
        }
        if (!path.isAbsolute()) {
            path = new Path(this.fs.getWorkingDirectory(), path);
        }
        this.fs.getFileStatus(path);
        final String qpath = this.fs.makeQualified(path).toString();
        final Path trashRoot = this.fs.getTrashRoot(path);
        final Path trashCurrent = new Path(trashRoot, TrashPolicyDefault.CURRENT);
        if (qpath.startsWith(trashRoot.toString())) {
            return false;
        }
        if (trashRoot.getParent().toString().startsWith(qpath)) {
            throw new IOException("Cannot move \"" + path + "\" to the trash, as it contains the trash");
        }
        Path trashPath = this.makeTrashRelativePath(trashCurrent, path);
        Path baseTrashPath = this.makeTrashRelativePath(trashCurrent, path.getParent());
        IOException cause = null;
        for (int i = 0; i < 2; ++i) {
            try {
                if (!this.fs.mkdirs(baseTrashPath, TrashPolicyDefault.PERMISSION)) {
                    TrashPolicyDefault.LOG.warn("Can't create(mkdir) trash directory: " + baseTrashPath);
                    return false;
                }
            }
            catch (FileAlreadyExistsException e2) {
                Path existsFilePath;
                for (existsFilePath = baseTrashPath; !this.fs.exists(existsFilePath); existsFilePath = existsFilePath.getParent()) {}
                baseTrashPath = new Path(baseTrashPath.toString().replace(existsFilePath.toString(), existsFilePath.toString() + Time.now()));
                trashPath = new Path(baseTrashPath, trashPath.getName());
                --i;
                continue;
            }
            catch (IOException e) {
                TrashPolicyDefault.LOG.warn("Can't create trash directory: " + baseTrashPath, e);
                cause = e;
                break;
            }
            try {
                for (String orig = trashPath.toString(); this.fs.exists(trashPath); trashPath = new Path(orig + Time.now())) {}
                this.fs.rename(path, trashPath, Options.Rename.TO_TRASH);
                TrashPolicyDefault.LOG.info("Moved: '" + path + "' to trash at: " + trashPath);
                return true;
            }
            catch (IOException e) {
                cause = e;
            }
        }
        throw (IOException)new IOException("Failed to move to trash: " + path).initCause(cause);
    }
    
    @Override
    public void createCheckpoint() throws IOException {
        this.createCheckpoint(new Date());
    }
    
    public void createCheckpoint(final Date date) throws IOException {
        final Collection<FileStatus> trashRoots = this.fs.getTrashRoots(false);
        for (final FileStatus trashRoot : trashRoots) {
            TrashPolicyDefault.LOG.info("TrashPolicyDefault#createCheckpoint for trashRoot: " + trashRoot.getPath());
            this.createCheckpoint(trashRoot.getPath(), date);
        }
    }
    
    @Override
    public void deleteCheckpoint() throws IOException {
        final Collection<FileStatus> trashRoots = this.fs.getTrashRoots(false);
        for (final FileStatus trashRoot : trashRoots) {
            TrashPolicyDefault.LOG.info("TrashPolicyDefault#deleteCheckpoint for trashRoot: " + trashRoot.getPath());
            this.deleteCheckpoint(trashRoot.getPath());
        }
    }
    
    @Override
    public Path getCurrentTrashDir() {
        return new Path(this.fs.getTrashRoot(null), TrashPolicyDefault.CURRENT);
    }
    
    @Override
    public Path getCurrentTrashDir(final Path path) throws IOException {
        return new Path(this.fs.getTrashRoot(path), TrashPolicyDefault.CURRENT);
    }
    
    @Override
    public Runnable getEmptier() throws IOException {
        return new Emptier(this.getConf(), this.emptierInterval);
    }
    
    private void createCheckpoint(final Path trashRoot, final Date date) throws IOException {
        if (!this.fs.exists(new Path(trashRoot, TrashPolicyDefault.CURRENT))) {
            return;
        }
        final Path checkpointBase;
        synchronized (TrashPolicyDefault.CHECKPOINT) {
            checkpointBase = new Path(trashRoot, TrashPolicyDefault.CHECKPOINT.format(date));
        }
        Path checkpoint = checkpointBase;
        final Path current = new Path(trashRoot, TrashPolicyDefault.CURRENT);
        int attempt = 0;
        while (true) {
            try {
                this.fs.rename(current, checkpoint, Options.Rename.NONE);
                TrashPolicyDefault.LOG.info("Created trash checkpoint: " + checkpoint.toUri().getPath());
            }
            catch (FileAlreadyExistsException e) {
                if (++attempt > 1000) {
                    throw new IOException("Failed to checkpoint trash: " + checkpoint);
                }
                checkpoint = checkpointBase.suffix("-" + attempt);
                continue;
            }
            break;
        }
    }
    
    private void deleteCheckpoint(final Path trashRoot) throws IOException {
        TrashPolicyDefault.LOG.info("TrashPolicyDefault#deleteCheckpoint for trashRoot: " + trashRoot);
        FileStatus[] dirs = null;
        try {
            dirs = this.fs.listStatus(trashRoot);
        }
        catch (FileNotFoundException fnfe) {
            return;
        }
        final long now = Time.now();
        for (int i = 0; i < dirs.length; ++i) {
            final Path path = dirs[i].getPath();
            final String dir = path.toUri().getPath();
            final String name = path.getName();
            if (!name.equals(TrashPolicyDefault.CURRENT.getName())) {
                long time;
                try {
                    time = this.getTimeFromCheckpoint(name);
                }
                catch (ParseException e) {
                    TrashPolicyDefault.LOG.warn("Unexpected item in trash: " + dir + ". Ignoring.");
                    continue;
                }
                if (now - this.deletionInterval > time) {
                    if (this.fs.delete(path, true)) {
                        TrashPolicyDefault.LOG.info("Deleted trash checkpoint: " + dir);
                    }
                    else {
                        TrashPolicyDefault.LOG.warn("Couldn't delete checkpoint: " + dir + " Ignoring.");
                    }
                }
            }
        }
    }
    
    private long getTimeFromCheckpoint(final String name) throws ParseException {
        long time;
        try {
            synchronized (TrashPolicyDefault.CHECKPOINT) {
                time = TrashPolicyDefault.CHECKPOINT.parse(name).getTime();
            }
        }
        catch (ParseException pe) {
            synchronized (TrashPolicyDefault.OLD_CHECKPOINT) {
                time = TrashPolicyDefault.OLD_CHECKPOINT.parse(name).getTime();
            }
        }
        return time;
    }
    
    static {
        LOG = LoggerFactory.getLogger(TrashPolicyDefault.class);
        CURRENT = new Path("Current");
        PERMISSION = new FsPermission(FsAction.ALL, FsAction.NONE, FsAction.NONE);
        CHECKPOINT = new SimpleDateFormat("yyMMddHHmmss");
        OLD_CHECKPOINT = new SimpleDateFormat("yyMMddHHmm");
    }
    
    protected class Emptier implements Runnable
    {
        private Configuration conf;
        private long emptierInterval;
        
        Emptier(final Configuration conf, final long emptierInterval) throws IOException {
            this.conf = conf;
            this.emptierInterval = emptierInterval;
            if (emptierInterval > TrashPolicyDefault.this.deletionInterval || emptierInterval <= 0L) {
                TrashPolicyDefault.LOG.info("The configured checkpoint interval is " + emptierInterval / 60000L + " minutes. Using an interval of " + TrashPolicyDefault.this.deletionInterval / 60000L + " minutes that is used for deletion instead");
                this.emptierInterval = TrashPolicyDefault.this.deletionInterval;
            }
            TrashPolicyDefault.LOG.info("Namenode trash configuration: Deletion interval = " + TrashPolicyDefault.this.deletionInterval / 60000L + " minutes, Emptier interval = " + this.emptierInterval / 60000L + " minutes.");
        }
        
        @Override
        public void run() {
            if (this.emptierInterval == 0L) {
                return;
            }
            long now = Time.now();
            while (true) {
                final long end = this.ceiling(now, this.emptierInterval);
                Label_0038: {
                    try {
                        Thread.sleep(end - now);
                        break Label_0038;
                    }
                    catch (InterruptedException e4) {
                        try {
                            TrashPolicyDefault.this.fs.close();
                        }
                        catch (IOException e) {
                            TrashPolicyDefault.LOG.warn("Trash cannot close FileSystem: ", e);
                        }
                        return;
                        try {
                            now = Time.now();
                            if (now < end) {
                                continue;
                            }
                            final Collection<FileStatus> trashRoots = TrashPolicyDefault.this.fs.getTrashRoots(true);
                            for (final FileStatus trashRoot : trashRoots) {
                                if (!trashRoot.isDirectory()) {
                                    continue;
                                }
                                try {
                                    final TrashPolicyDefault trash = new TrashPolicyDefault(TrashPolicyDefault.this.fs, this.conf, null);
                                    trash.deleteCheckpoint(trashRoot.getPath());
                                    trash.createCheckpoint(trashRoot.getPath(), new Date(now));
                                }
                                catch (IOException e2) {
                                    TrashPolicyDefault.LOG.warn("Trash caught: " + e2 + ". Skipping " + trashRoot.getPath() + ".");
                                }
                            }
                        }
                        catch (Exception e3) {
                            TrashPolicyDefault.LOG.warn("RuntimeException during Trash.Emptier.run(): ", e3);
                        }
                    }
                }
            }
        }
        
        private long ceiling(final long time, final long interval) {
            return this.floor(time, interval) + interval;
        }
        
        private long floor(final long time, final long interval) {
            return time / interval * interval;
        }
        
        @VisibleForTesting
        protected long getEmptierInterval() {
            return this.emptierInterval / 60000L;
        }
    }
}
