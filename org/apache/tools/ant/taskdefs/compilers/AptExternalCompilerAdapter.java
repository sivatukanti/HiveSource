// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.compilers;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.taskdefs.Apt;

public class AptExternalCompilerAdapter extends DefaultCompilerAdapter
{
    protected Apt getApt() {
        return (Apt)this.getJavac();
    }
    
    public boolean execute() throws BuildException {
        this.attributes.log("Using external apt compiler", 3);
        final Apt apt = this.getApt();
        final Commandline cmd = new Commandline();
        cmd.setExecutable(apt.getAptExecutable());
        this.setupModernJavacCommandlineSwitches(cmd);
        AptCompilerAdapter.setAptCommandlineSwitches(apt, cmd);
        final int firstFileName = cmd.size();
        this.logAndAddFilesToCompile(cmd);
        return 0 == this.executeExternalCompile(cmd.getCommandline(), firstFileName, true);
    }
}
