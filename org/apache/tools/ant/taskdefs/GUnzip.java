// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.io.InputStream;
import java.io.OutputStream;
import org.apache.tools.ant.util.FileUtils;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import java.util.zip.GZIPInputStream;
import java.io.FileOutputStream;

public class GUnzip extends Unpack
{
    private static final int BUFFER_SIZE = 8192;
    private static final String DEFAULT_EXTENSION = ".gz";
    
    @Override
    protected String getDefaultExtension() {
        return ".gz";
    }
    
    @Override
    protected void extract() {
        if (this.source.lastModified() > this.dest.lastModified()) {
            this.log("Expanding " + this.source.getAbsolutePath() + " to " + this.dest.getAbsolutePath());
            FileOutputStream out = null;
            GZIPInputStream zIn = null;
            InputStream fis = null;
            try {
                out = new FileOutputStream(this.dest);
                fis = this.srcResource.getInputStream();
                zIn = new GZIPInputStream(fis);
                final byte[] buffer = new byte[8192];
                int count = 0;
                do {
                    out.write(buffer, 0, count);
                    count = zIn.read(buffer, 0, buffer.length);
                } while (count != -1);
            }
            catch (IOException ioe) {
                final String msg = "Problem expanding gzip " + ioe.getMessage();
                throw new BuildException(msg, ioe, this.getLocation());
            }
            finally {
                FileUtils.close(fis);
                FileUtils.close(out);
                FileUtils.close(zIn);
            }
        }
    }
    
    @Override
    protected boolean supportsNonFileResources() {
        return this.getClass().equals(GUnzip.class);
    }
}
