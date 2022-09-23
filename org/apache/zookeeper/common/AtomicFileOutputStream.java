// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.common;

import org.slf4j.LoggerFactory;
import java.io.Closeable;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import org.slf4j.Logger;
import java.io.FilterOutputStream;

public class AtomicFileOutputStream extends FilterOutputStream
{
    private static final String TMP_EXTENSION = ".tmp";
    private static final Logger LOG;
    private final File origFile;
    private final File tmpFile;
    
    public AtomicFileOutputStream(final File f) throws FileNotFoundException {
        super(new FileOutputStream(new File(f.getParentFile(), f.getName() + ".tmp")));
        this.origFile = f.getAbsoluteFile();
        this.tmpFile = new File(f.getParentFile(), f.getName() + ".tmp").getAbsoluteFile();
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        this.out.write(b, off, len);
    }
    
    @Override
    public void close() throws IOException {
        boolean triedToClose = false;
        boolean success = false;
        try {
            this.flush();
            ((FileOutputStream)this.out).getChannel().force(true);
            triedToClose = true;
            super.close();
            success = true;
        }
        finally {
            if (success) {
                final boolean renamed = this.tmpFile.renameTo(this.origFile);
                if (!renamed && (!this.origFile.delete() || !this.tmpFile.renameTo(this.origFile))) {
                    throw new IOException("Could not rename temporary file " + this.tmpFile + " to " + this.origFile);
                }
            }
            else {
                if (!triedToClose) {
                    IOUtils.closeStream(this.out);
                }
                if (!this.tmpFile.delete()) {
                    AtomicFileOutputStream.LOG.warn("Unable to delete tmp file " + this.tmpFile);
                }
            }
        }
    }
    
    public void abort() {
        try {
            super.close();
        }
        catch (IOException ioe) {
            AtomicFileOutputStream.LOG.warn("Unable to abort file " + this.tmpFile, ioe);
        }
        if (!this.tmpFile.delete()) {
            AtomicFileOutputStream.LOG.warn("Unable to delete tmp file during abort " + this.tmpFile);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(AtomicFileOutputStream.class);
    }
}
