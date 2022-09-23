// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.io.OutputStream;
import org.apache.tools.ant.util.FileUtils;
import java.io.IOException;
import java.io.InputStream;
import org.apache.tools.bzip2.CBZip2InputStream;
import org.apache.tools.ant.BuildException;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;

public class BUnzip2 extends Unpack
{
    private static final int BUFFER_SIZE = 8192;
    private static final String DEFAULT_EXTENSION = ".bz2";
    
    @Override
    protected String getDefaultExtension() {
        return ".bz2";
    }
    
    @Override
    protected void extract() {
        if (this.source.lastModified() > this.dest.lastModified()) {
            this.log("Expanding " + this.source.getAbsolutePath() + " to " + this.dest.getAbsolutePath());
            FileOutputStream out = null;
            CBZip2InputStream zIn = null;
            InputStream fis = null;
            BufferedInputStream bis = null;
            try {
                out = new FileOutputStream(this.dest);
                fis = this.srcResource.getInputStream();
                bis = new BufferedInputStream(fis);
                int b = bis.read();
                if (b != 66) {
                    throw new BuildException("Invalid bz2 file.", this.getLocation());
                }
                b = bis.read();
                if (b != 90) {
                    throw new BuildException("Invalid bz2 file.", this.getLocation());
                }
                zIn = new CBZip2InputStream(bis, true);
                final byte[] buffer = new byte[8192];
                int count = 0;
                do {
                    out.write(buffer, 0, count);
                    count = zIn.read(buffer, 0, buffer.length);
                } while (count != -1);
            }
            catch (IOException ioe) {
                final String msg = "Problem expanding bzip2 " + ioe.getMessage();
                throw new BuildException(msg, ioe, this.getLocation());
            }
            finally {
                FileUtils.close(bis);
                FileUtils.close(fis);
                FileUtils.close(out);
                FileUtils.close(zIn);
            }
        }
    }
    
    @Override
    protected boolean supportsNonFileResources() {
        return this.getClass().equals(BUnzip2.class);
    }
}
