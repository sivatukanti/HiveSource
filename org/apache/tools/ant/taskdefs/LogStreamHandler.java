// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import org.apache.tools.ant.BuildException;
import java.io.OutputStream;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.Task;

public class LogStreamHandler extends PumpStreamHandler
{
    public LogStreamHandler(final Task task, final int outlevel, final int errlevel) {
        this((ProjectComponent)task, outlevel, errlevel);
    }
    
    public LogStreamHandler(final ProjectComponent pc, final int outlevel, final int errlevel) {
        super(new LogOutputStream(pc, outlevel), new LogOutputStream(pc, errlevel));
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
