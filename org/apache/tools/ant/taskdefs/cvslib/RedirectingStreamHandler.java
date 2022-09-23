// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.cvslib;

import java.io.IOException;
import org.apache.tools.ant.BuildException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;

class RedirectingStreamHandler extends PumpStreamHandler
{
    RedirectingStreamHandler(final ChangeLogParser parser) {
        super(new RedirectingOutputStream(parser), new ByteArrayOutputStream());
    }
    
    String getErrors() {
        try {
            final ByteArrayOutputStream error = (ByteArrayOutputStream)this.getErr();
            return error.toString("ASCII");
        }
        catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public void stop() {
        super.stop();
        try {
            this.getErr().close();
            this.getOut().close();
        }
        catch (IOException e) {
            throw new BuildException(e);
        }
    }
}
