// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.util.FileUtils;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import java.io.FileOutputStream;

public class GZip extends Pack
{
    @Override
    protected void pack() {
        GZIPOutputStream zOut = null;
        try {
            zOut = new GZIPOutputStream(new FileOutputStream(this.zipFile));
            this.zipResource(this.getSrcResource(), zOut);
        }
        catch (IOException ioe) {
            final String msg = "Problem creating gzip " + ioe.getMessage();
            throw new BuildException(msg, ioe, this.getLocation());
        }
        finally {
            FileUtils.close(zOut);
        }
    }
    
    @Override
    protected boolean supportsNonFileResources() {
        return this.getClass().equals(GZip.class);
    }
}
