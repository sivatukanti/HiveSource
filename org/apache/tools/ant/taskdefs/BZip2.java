// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.util.FileUtils;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.bzip2.CBZip2OutputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

public class BZip2 extends Pack
{
    @Override
    protected void pack() {
        CBZip2OutputStream zOut = null;
        try {
            final BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(this.zipFile));
            bos.write(66);
            bos.write(90);
            zOut = new CBZip2OutputStream(bos);
            this.zipResource(this.getSrcResource(), zOut);
        }
        catch (IOException ioe) {
            final String msg = "Problem creating bzip2 " + ioe.getMessage();
            throw new BuildException(msg, ioe, this.getLocation());
        }
        finally {
            FileUtils.close(zOut);
        }
    }
    
    @Override
    protected boolean supportsNonFileResources() {
        return this.getClass().equals(BZip2.class);
    }
}
