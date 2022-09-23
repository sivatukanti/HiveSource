// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.native2ascii;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.types.Commandline;
import java.io.File;
import org.apache.tools.ant.taskdefs.optional.Native2Ascii;

public abstract class DefaultNative2Ascii implements Native2AsciiAdapter
{
    public final boolean convert(final Native2Ascii args, final File srcFile, final File destFile) throws BuildException {
        final Commandline cmd = new Commandline();
        this.setup(cmd, args);
        this.addFiles(cmd, args, srcFile, destFile);
        return this.run(cmd, args);
    }
    
    protected void setup(final Commandline cmd, final Native2Ascii args) throws BuildException {
        if (args.getEncoding() != null) {
            cmd.createArgument().setValue("-encoding");
            cmd.createArgument().setValue(args.getEncoding());
        }
        cmd.addArguments(args.getCurrentArgs());
    }
    
    protected void addFiles(final Commandline cmd, final ProjectComponent log, final File src, final File dest) throws BuildException {
        cmd.createArgument().setFile(src);
        cmd.createArgument().setFile(dest);
    }
    
    protected abstract boolean run(final Commandline p0, final ProjectComponent p1) throws BuildException;
}
