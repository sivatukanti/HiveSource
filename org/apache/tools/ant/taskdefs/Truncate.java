// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.io.RandomAccessFile;
import java.io.IOException;
import java.util.Iterator;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileResource;
import java.io.File;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.Task;

public class Truncate extends Task
{
    private static final int BUFFER_SIZE = 1024;
    private static final Long ZERO;
    private static final String NO_CHILD = "No files specified.";
    private static final String INVALID_LENGTH = "Cannot truncate to length ";
    private static final String READ_WRITE = "rw";
    private static final FileUtils FILE_UTILS;
    private static final byte[] FILL_BUFFER;
    private Path path;
    private boolean create;
    private boolean mkdirs;
    private Long length;
    private Long adjust;
    
    public Truncate() {
        this.create = true;
        this.mkdirs = false;
    }
    
    public void setFile(final File f) {
        this.add(new FileResource(f));
    }
    
    public void add(final ResourceCollection rc) {
        this.getPath().add(rc);
    }
    
    public void setAdjust(final Long adjust) {
        this.adjust = adjust;
    }
    
    public void setLength(final Long length) {
        this.length = length;
        if (length != null && length < 0L) {
            throw new BuildException("Cannot truncate to length " + length);
        }
    }
    
    public void setCreate(final boolean create) {
        this.create = create;
    }
    
    public void setMkdirs(final boolean mkdirs) {
        this.mkdirs = mkdirs;
    }
    
    @Override
    public void execute() {
        if (this.length != null && this.adjust != null) {
            throw new BuildException("length and adjust are mutually exclusive options");
        }
        if (this.length == null && this.adjust == null) {
            this.length = Truncate.ZERO;
        }
        if (this.path == null) {
            throw new BuildException("No files specified.");
        }
        for (final Resource r : this.path) {
            final File f = r.as(FileProvider.class).getFile();
            if (this.shouldProcess(f)) {
                this.process(f);
            }
        }
    }
    
    private boolean shouldProcess(final File f) {
        if (f.isFile()) {
            return true;
        }
        if (!this.create) {
            return false;
        }
        Exception exception = null;
        try {
            if (Truncate.FILE_UTILS.createNewFile(f, this.mkdirs)) {
                return true;
            }
        }
        catch (IOException e) {
            exception = e;
        }
        final String msg = "Unable to create " + f;
        if (exception == null) {
            this.log(msg, 1);
            return false;
        }
        throw new BuildException(msg, exception);
    }
    
    private void process(final File f) {
        final long len = f.length();
        final long newLength = (this.length == null) ? (len + this.adjust) : this.length;
        if (len == newLength) {
            return;
        }
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(f, "rw");
        }
        catch (Exception e) {
            throw new BuildException("Could not open " + f + " for writing", e);
        }
        try {
            if (newLength > len) {
                long pos = len;
                raf.seek(pos);
                while (pos < newLength) {
                    final long writeCount = Math.min(Truncate.FILL_BUFFER.length, newLength - pos);
                    raf.write(Truncate.FILL_BUFFER, 0, (int)writeCount);
                    pos += writeCount;
                }
            }
            else {
                raf.setLength(newLength);
            }
        }
        catch (IOException e2) {
            throw new BuildException("Exception working with " + raf, e2);
        }
        finally {
            try {
                raf.close();
            }
            catch (IOException e3) {
                this.log("Caught " + e3 + " closing " + raf, 1);
            }
        }
    }
    
    private synchronized Path getPath() {
        if (this.path == null) {
            this.path = new Path(this.getProject());
        }
        return this.path;
    }
    
    static {
        ZERO = new Long(0L);
        FILE_UTILS = FileUtils.getFileUtils();
        FILL_BUFFER = new byte[1024];
    }
}
