// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.io.File;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import java.util.Random;
import java.net.URI;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class LocalFileSystem extends ChecksumFileSystem
{
    static final URI NAME;
    private static Random rand;
    
    public LocalFileSystem() {
        this(new RawLocalFileSystem());
    }
    
    @Override
    public void initialize(final URI name, final Configuration conf) throws IOException {
        if (this.fs.getConf() == null) {
            this.fs.initialize(name, conf);
        }
        final String scheme = name.getScheme();
        if (!scheme.equals(this.fs.getUri().getScheme())) {
            this.swapScheme = scheme;
        }
    }
    
    @Override
    public String getScheme() {
        return "file";
    }
    
    public FileSystem getRaw() {
        return this.getRawFileSystem();
    }
    
    public LocalFileSystem(final FileSystem rawLocalFileSystem) {
        super(rawLocalFileSystem);
    }
    
    public File pathToFile(final Path path) {
        return ((RawLocalFileSystem)this.fs).pathToFile(path);
    }
    
    @Override
    public void copyFromLocalFile(final boolean delSrc, final Path src, final Path dst) throws IOException {
        FileUtil.copy(this, src, this, dst, delSrc, this.getConf());
    }
    
    @Override
    public void copyToLocalFile(final boolean delSrc, final Path src, final Path dst) throws IOException {
        FileUtil.copy(this, src, this, dst, delSrc, this.getConf());
    }
    
    @Override
    public boolean reportChecksumFailure(final Path p, final FSDataInputStream in, final long inPos, final FSDataInputStream sums, final long sumsPos) {
        try {
            final File f = ((RawLocalFileSystem)this.fs).pathToFile(p).getCanonicalFile();
            final String device = new DF(f, this.getConf()).getMount();
            File parent = f.getParentFile();
            File dir = null;
            while (parent != null && FileUtil.canWrite(parent) && parent.toString().startsWith(device)) {
                dir = parent;
                parent = parent.getParentFile();
            }
            if (dir == null) {
                throw new IOException("not able to find the highest writable parent dir");
            }
            final File badDir = new File(dir, "bad_files");
            if (!badDir.mkdirs() && !badDir.isDirectory()) {
                throw new IOException("Mkdirs failed to create " + badDir.toString());
            }
            final String suffix = "." + LocalFileSystem.rand.nextInt();
            final File badFile = new File(badDir, f.getName() + suffix);
            LocalFileSystem.LOG.warn("Moving bad file " + f + " to " + badFile);
            in.close();
            boolean b = f.renameTo(badFile);
            if (!b) {
                LocalFileSystem.LOG.warn("Ignoring failure of renameTo");
            }
            final File checkFile = ((RawLocalFileSystem)this.fs).pathToFile(this.getChecksumFile(p));
            sums.close();
            b = checkFile.renameTo(new File(badDir, checkFile.getName() + suffix));
            if (!b) {
                LocalFileSystem.LOG.warn("Ignoring failure of renameTo");
            }
        }
        catch (IOException e) {
            LocalFileSystem.LOG.warn("Error moving bad file " + p, e);
        }
        return false;
    }
    
    @Override
    public boolean supportsSymlinks() {
        return true;
    }
    
    @Override
    public void createSymlink(final Path target, final Path link, final boolean createParent) throws IOException {
        this.fs.createSymlink(target, link, createParent);
    }
    
    @Override
    public FileStatus getFileLinkStatus(final Path f) throws IOException {
        return this.fs.getFileLinkStatus(f);
    }
    
    @Override
    public Path getLinkTarget(final Path f) throws IOException {
        return this.fs.getLinkTarget(f);
    }
    
    static {
        NAME = URI.create("file:///");
        LocalFileSystem.rand = new Random();
    }
}
