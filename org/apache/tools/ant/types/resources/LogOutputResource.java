// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.LogOutputStream;
import org.apache.tools.ant.types.Resource;

public class LogOutputResource extends Resource implements Appendable
{
    private static final String NAME = "[Ant log]";
    private LogOutputStream outputStream;
    
    public LogOutputResource(final ProjectComponent managingComponent) {
        super("[Ant log]");
        this.outputStream = new LogOutputStream(managingComponent);
    }
    
    public LogOutputResource(final ProjectComponent managingComponent, final int level) {
        super("[Ant log]");
        this.outputStream = new LogOutputStream(managingComponent, level);
    }
    
    public OutputStream getAppendOutputStream() throws IOException {
        return this.outputStream;
    }
    
    @Override
    public OutputStream getOutputStream() throws IOException {
        return this.outputStream;
    }
}
