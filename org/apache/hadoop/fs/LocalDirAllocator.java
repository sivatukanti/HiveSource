// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import org.apache.hadoop.util.StringUtils;
import java.util.concurrent.atomic.AtomicReference;
import java.util.Random;
import org.slf4j.Logger;
import java.util.TreeMap;
import java.io.File;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.DiskChecker;
import org.apache.hadoop.util.DiskValidatorFactory;
import org.apache.hadoop.util.DiskValidator;
import java.util.Map;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Unstable
public class LocalDirAllocator
{
    private static Map<String, AllocatorPerContext> contexts;
    private String contextCfgItemName;
    public static final int SIZE_UNKNOWN = -1;
    private final DiskValidator diskValidator;
    
    public LocalDirAllocator(final String contextCfgItemName) {
        this.contextCfgItemName = contextCfgItemName;
        try {
            this.diskValidator = DiskValidatorFactory.getInstance("basic");
        }
        catch (DiskChecker.DiskErrorException e) {
            throw new RuntimeException(e);
        }
    }
    
    public LocalDirAllocator(final String contextCfgItemName, final DiskValidator diskValidator) {
        this.contextCfgItemName = contextCfgItemName;
        this.diskValidator = diskValidator;
    }
    
    private AllocatorPerContext obtainContext(final String contextCfgItemName) {
        synchronized (LocalDirAllocator.contexts) {
            AllocatorPerContext l = LocalDirAllocator.contexts.get(contextCfgItemName);
            if (l == null) {
                LocalDirAllocator.contexts.put(contextCfgItemName, l = new AllocatorPerContext(contextCfgItemName, this.diskValidator));
            }
            return l;
        }
    }
    
    public Path getLocalPathForWrite(final String pathStr, final Configuration conf) throws IOException {
        return this.getLocalPathForWrite(pathStr, -1L, conf);
    }
    
    public Path getLocalPathForWrite(final String pathStr, final long size, final Configuration conf) throws IOException {
        return this.getLocalPathForWrite(pathStr, size, conf, true);
    }
    
    public Path getLocalPathForWrite(final String pathStr, final long size, final Configuration conf, final boolean checkWrite) throws IOException {
        final AllocatorPerContext context = this.obtainContext(this.contextCfgItemName);
        return context.getLocalPathForWrite(pathStr, size, conf, checkWrite);
    }
    
    public Path getLocalPathToRead(final String pathStr, final Configuration conf) throws IOException {
        final AllocatorPerContext context = this.obtainContext(this.contextCfgItemName);
        return context.getLocalPathToRead(pathStr, conf);
    }
    
    public Iterable<Path> getAllLocalPathsToRead(final String pathStr, final Configuration conf) throws IOException {
        final AllocatorPerContext context;
        synchronized (this) {
            context = this.obtainContext(this.contextCfgItemName);
        }
        return context.getAllLocalPathsToRead(pathStr, conf);
    }
    
    public File createTmpFileForWrite(final String pathStr, final long size, final Configuration conf) throws IOException {
        final AllocatorPerContext context = this.obtainContext(this.contextCfgItemName);
        return context.createTmpFileForWrite(pathStr, size, conf);
    }
    
    public static boolean isContextValid(final String contextCfgItemName) {
        synchronized (LocalDirAllocator.contexts) {
            return LocalDirAllocator.contexts.containsKey(contextCfgItemName);
        }
    }
    
    @Deprecated
    @InterfaceAudience.LimitedPrivate({ "MapReduce" })
    public static void removeContext(final String contextCfgItemName) {
        synchronized (LocalDirAllocator.contexts) {
            LocalDirAllocator.contexts.remove(contextCfgItemName);
        }
    }
    
    public boolean ifExists(final String pathStr, final Configuration conf) {
        final AllocatorPerContext context = this.obtainContext(this.contextCfgItemName);
        return context.ifExists(pathStr, conf);
    }
    
    int getCurrentDirectoryIndex() {
        final AllocatorPerContext context = this.obtainContext(this.contextCfgItemName);
        return context.getCurrentDirectoryIndex();
    }
    
    static {
        LocalDirAllocator.contexts = new TreeMap<String, AllocatorPerContext>();
    }
    
    private static class AllocatorPerContext
    {
        private static final Logger LOG;
        private Random dirIndexRandomizer;
        private String contextCfgItemName;
        private AtomicReference<Context> currentContext;
        private final DiskValidator diskValidator;
        
        public AllocatorPerContext(final String contextCfgItemName, final DiskValidator diskValidator) {
            this.dirIndexRandomizer = new Random();
            this.contextCfgItemName = contextCfgItemName;
            this.currentContext = new AtomicReference<Context>(new Context());
            this.diskValidator = diskValidator;
        }
        
        private Context confChanged(final Configuration conf) throws IOException {
            Context ctx = this.currentContext.get();
            final String newLocalDirs = conf.get(this.contextCfgItemName);
            if (null == newLocalDirs) {
                throw new IOException(this.contextCfgItemName + " not configured");
            }
            if (!newLocalDirs.equals(ctx.savedLocalDirs)) {
                ctx = new Context();
                final String[] dirStrings = StringUtils.getTrimmedStrings(newLocalDirs);
                ctx.localFS = FileSystem.getLocal(conf);
                final int numDirs = dirStrings.length;
                final ArrayList<Path> dirs = new ArrayList<Path>(numDirs);
                final ArrayList<DF> dfList = new ArrayList<DF>(numDirs);
                for (int i = 0; i < numDirs; ++i) {
                    try {
                        final Path tmpDir = new Path(dirStrings[i]);
                        Label_0328: {
                            if (!ctx.localFS.mkdirs(tmpDir)) {
                                if (!ctx.localFS.exists(tmpDir)) {
                                    AllocatorPerContext.LOG.warn("Failed to create " + dirStrings[i]);
                                    break Label_0328;
                                }
                            }
                            try {
                                final File tmpFile = tmpDir.isAbsolute() ? new File(ctx.localFS.makeQualified(tmpDir).toUri()) : new File(dirStrings[i]);
                                this.diskValidator.checkStatus(tmpFile);
                                dirs.add(new Path(tmpFile.getPath()));
                                dfList.add(new DF(tmpFile, 30000L));
                            }
                            catch (DiskChecker.DiskErrorException de) {
                                AllocatorPerContext.LOG.warn(dirStrings[i] + " is not writable\n", de);
                            }
                        }
                    }
                    catch (IOException ie) {
                        AllocatorPerContext.LOG.warn("Failed to create " + dirStrings[i] + ": " + ie.getMessage() + "\n", ie);
                    }
                }
                ctx.localDirs = dirs.toArray(new Path[dirs.size()]);
                ctx.dirDF = dfList.toArray(new DF[dirs.size()]);
                ctx.savedLocalDirs = newLocalDirs;
                if (dirs.size() > 0) {
                    ctx.dirNumLastAccessed.set(this.dirIndexRandomizer.nextInt(dirs.size()));
                }
                this.currentContext.set(ctx);
            }
            return ctx;
        }
        
        private Path createPath(final Path dir, final String path, final boolean checkWrite) throws IOException {
            final Path file = new Path(dir, path);
            if (checkWrite) {
                try {
                    this.diskValidator.checkStatus(new File(file.getParent().toUri().getPath()));
                    return file;
                }
                catch (DiskChecker.DiskErrorException d) {
                    AllocatorPerContext.LOG.warn("Disk Error Exception: ", d);
                    return null;
                }
            }
            return file;
        }
        
        int getCurrentDirectoryIndex() {
            return this.currentContext.get().dirNumLastAccessed.get();
        }
        
        public Path getLocalPathForWrite(String pathStr, final long size, final Configuration conf, final boolean checkWrite) throws IOException {
            final Context ctx = this.confChanged(conf);
            final int numDirs = ctx.localDirs.length;
            int numDirsSearched = 0;
            if (pathStr.startsWith("/")) {
                pathStr = pathStr.substring(1);
            }
            Path returnPath = null;
            if (size == -1L) {
                final long[] availableOnDisk = new long[ctx.dirDF.length];
                long totalAvailable = 0L;
                for (int i = 0; i < ctx.dirDF.length; ++i) {
                    availableOnDisk[i] = ctx.dirDF[i].getAvailable();
                    totalAvailable += availableOnDisk[i];
                }
                if (totalAvailable == 0L) {
                    throw new DiskChecker.DiskErrorException("No space available in any of the local directories.");
                }
                final Random r = new Random();
                while (numDirsSearched < numDirs && returnPath == null) {
                    long randomPosition;
                    int dir;
                    for (randomPosition = (r.nextLong() >>> 1) % totalAvailable, dir = 0; randomPosition > availableOnDisk[dir]; randomPosition -= availableOnDisk[dir], ++dir) {}
                    ctx.dirNumLastAccessed.set(dir);
                    returnPath = this.createPath(ctx.localDirs[dir], pathStr, checkWrite);
                    if (returnPath == null) {
                        totalAvailable -= availableOnDisk[dir];
                        availableOnDisk[dir] = 0L;
                        ++numDirsSearched;
                    }
                }
            }
            else {
                int randomInc = 1;
                if (numDirs > 2) {
                    randomInc += this.dirIndexRandomizer.nextInt(numDirs - 1);
                }
                int dirNum = ctx.getAndIncrDirNumLastAccessed(randomInc);
                while (numDirsSearched < numDirs) {
                    final long capacity = ctx.dirDF[dirNum].getAvailable();
                    if (capacity > size) {
                        returnPath = this.createPath(ctx.localDirs[dirNum], pathStr, checkWrite);
                        if (returnPath != null) {
                            ctx.getAndIncrDirNumLastAccessed(numDirsSearched);
                            break;
                        }
                    }
                    dirNum = ++dirNum % numDirs;
                    ++numDirsSearched;
                }
            }
            if (returnPath != null) {
                return returnPath;
            }
            throw new DiskChecker.DiskErrorException("Could not find any valid local directory for " + pathStr);
        }
        
        public File createTmpFileForWrite(final String pathStr, final long size, final Configuration conf) throws IOException {
            final Path path = this.getLocalPathForWrite(pathStr, size, conf, true);
            final File dir = new File(path.getParent().toUri().getPath());
            final String prefix = path.getName();
            final File result = File.createTempFile(prefix, null, dir);
            result.deleteOnExit();
            return result;
        }
        
        public Path getLocalPathToRead(String pathStr, final Configuration conf) throws IOException {
            final Context ctx = this.confChanged(conf);
            final int numDirs = ctx.localDirs.length;
            int numDirsSearched = 0;
            if (pathStr.startsWith("/")) {
                pathStr = pathStr.substring(1);
            }
            while (numDirsSearched < numDirs) {
                final Path file = new Path(ctx.localDirs[numDirsSearched], pathStr);
                if (ctx.localFS.exists(file)) {
                    return file;
                }
                ++numDirsSearched;
            }
            throw new DiskChecker.DiskErrorException("Could not find " + pathStr + " in any of the configured local directories");
        }
        
        Iterable<Path> getAllLocalPathsToRead(String pathStr, final Configuration conf) throws IOException {
            final Context ctx = this.confChanged(conf);
            if (pathStr.startsWith("/")) {
                pathStr = pathStr.substring(1);
            }
            return new PathIterator(ctx.localFS, pathStr, ctx.localDirs);
        }
        
        public boolean ifExists(String pathStr, final Configuration conf) {
            final Context ctx = this.currentContext.get();
            try {
                final int numDirs = ctx.localDirs.length;
                int numDirsSearched = 0;
                if (pathStr.startsWith("/")) {
                    pathStr = pathStr.substring(1);
                }
                while (numDirsSearched < numDirs) {
                    final Path file = new Path(ctx.localDirs[numDirsSearched], pathStr);
                    if (ctx.localFS.exists(file)) {
                        return true;
                    }
                    ++numDirsSearched;
                }
            }
            catch (IOException ex) {}
            return false;
        }
        
        static {
            LOG = LoggerFactory.getLogger(AllocatorPerContext.class);
        }
        
        private static class Context
        {
            private AtomicInteger dirNumLastAccessed;
            private FileSystem localFS;
            private DF[] dirDF;
            private Path[] localDirs;
            private String savedLocalDirs;
            
            private Context() {
                this.dirNumLastAccessed = new AtomicInteger(0);
            }
            
            public int getAndIncrDirNumLastAccessed() {
                return this.getAndIncrDirNumLastAccessed(1);
            }
            
            public int getAndIncrDirNumLastAccessed(final int delta) {
                if (this.localDirs.length < 2 || delta == 0) {
                    return this.dirNumLastAccessed.get();
                }
                int oldval;
                int newval;
                do {
                    oldval = this.dirNumLastAccessed.get();
                    newval = (oldval + delta) % this.localDirs.length;
                } while (!this.dirNumLastAccessed.compareAndSet(oldval, newval));
                return oldval;
            }
        }
        
        private static class PathIterator implements Iterator<Path>, Iterable<Path>
        {
            private final FileSystem fs;
            private final String pathStr;
            private int i;
            private final Path[] rootDirs;
            private Path next;
            
            private PathIterator(final FileSystem fs, final String pathStr, final Path[] rootDirs) throws IOException {
                this.i = 0;
                this.next = null;
                this.fs = fs;
                this.pathStr = pathStr;
                this.rootDirs = rootDirs;
                this.advance();
            }
            
            @Override
            public boolean hasNext() {
                return this.next != null;
            }
            
            private void advance() throws IOException {
                while (this.i < this.rootDirs.length) {
                    this.next = new Path(this.rootDirs[this.i++], this.pathStr);
                    if (this.fs.exists(this.next)) {
                        return;
                    }
                }
                this.next = null;
            }
            
            @Override
            public Path next() {
                final Path result = this.next;
                try {
                    this.advance();
                }
                catch (IOException ie) {
                    throw new RuntimeException("Can't check existence of " + this.next, ie);
                }
                if (result == null) {
                    throw new NoSuchElementException();
                }
                return result;
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException("read only iterator");
            }
            
            @Override
            public Iterator<Path> iterator() {
                return this;
            }
        }
    }
}
