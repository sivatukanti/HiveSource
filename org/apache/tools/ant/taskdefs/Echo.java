// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.types.LogLevel;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import java.util.Vector;
import org.apache.tools.ant.types.FilterSetCollection;
import org.apache.tools.ant.util.ResourceUtils;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.types.resources.LogOutputResource;
import org.apache.tools.ant.types.resources.StringResource;
import org.apache.tools.ant.util.StringUtils;
import org.apache.tools.ant.types.Resource;
import java.io.File;
import org.apache.tools.ant.Task;

public class Echo extends Task
{
    protected String message;
    protected File file;
    protected boolean append;
    private String encoding;
    private boolean force;
    protected int logLevel;
    private Resource output;
    
    public Echo() {
        this.message = "";
        this.file = null;
        this.append = false;
        this.encoding = "";
        this.force = false;
        this.logLevel = 1;
    }
    
    @Override
    public void execute() throws BuildException {
        final String msg = "".equals(this.message) ? StringUtils.LINE_SEP : this.message;
        try {
            ResourceUtils.copyResource(new StringResource(msg), (this.output == null) ? new LogOutputResource(this, this.logLevel) : this.output, null, null, false, false, this.append, null, "".equals(this.encoding) ? null : this.encoding, this.getProject(), this.force);
        }
        catch (IOException ioe) {
            throw new BuildException(ioe, this.getLocation());
        }
    }
    
    public void setMessage(final String msg) {
        this.message = ((msg == null) ? "" : msg);
    }
    
    public void setFile(final File file) {
        this.setOutput(new FileResource(this.getProject(), file));
    }
    
    public void setOutput(final Resource output) {
        if (this.output != null) {
            throw new BuildException("Cannot set > 1 output target");
        }
        this.output = output;
        final FileProvider fp = output.as(FileProvider.class);
        this.file = ((fp != null) ? fp.getFile() : null);
    }
    
    public void setAppend(final boolean append) {
        this.append = append;
    }
    
    public void addText(final String msg) {
        this.message += this.getProject().replaceProperties(msg);
    }
    
    public void setLevel(final EchoLevel echoLevel) {
        this.logLevel = echoLevel.getLevel();
    }
    
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }
    
    public void setForce(final boolean f) {
        this.force = f;
    }
    
    public static class EchoLevel extends LogLevel
    {
    }
}
