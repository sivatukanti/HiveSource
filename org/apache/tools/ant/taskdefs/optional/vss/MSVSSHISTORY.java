// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.vss;

import org.apache.tools.ant.types.EnumeratedAttribute;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Commandline;

public class MSVSSHISTORY extends MSVSS
{
    @Override
    Commandline buildCmdLine() {
        final Commandline commandLine = new Commandline();
        if (this.getVsspath() == null) {
            final String msg = "vsspath attribute must be set!";
            throw new BuildException(msg, this.getLocation());
        }
        commandLine.setExecutable(this.getSSCommand());
        commandLine.createArgument().setValue("History");
        commandLine.createArgument().setValue(this.getVsspath());
        commandLine.createArgument().setValue("-I-");
        commandLine.createArgument().setValue(this.getVersionDate());
        commandLine.createArgument().setValue(this.getVersionLabel());
        commandLine.createArgument().setValue(this.getRecursive());
        commandLine.createArgument().setValue(this.getStyle());
        commandLine.createArgument().setValue(this.getLogin());
        commandLine.createArgument().setValue(this.getOutput());
        return commandLine;
    }
    
    public void setRecursive(final boolean recursive) {
        super.setInternalRecursive(recursive);
    }
    
    public void setUser(final String user) {
        super.setInternalUser(user);
    }
    
    public void setFromDate(final String fromDate) {
        super.setInternalFromDate(fromDate);
    }
    
    public void setToDate(final String toDate) {
        super.setInternalToDate(toDate);
    }
    
    public void setFromLabel(final String fromLabel) {
        super.setInternalFromLabel(fromLabel);
    }
    
    public void setToLabel(final String toLabel) {
        super.setInternalToLabel(toLabel);
    }
    
    public void setNumdays(final int numd) {
        super.setInternalNumDays(numd);
    }
    
    public void setOutput(final File outfile) {
        if (outfile != null) {
            super.setInternalOutputFilename(outfile.getAbsolutePath());
        }
    }
    
    public void setDateFormat(final String dateFormat) {
        super.setInternalDateFormat(new SimpleDateFormat(dateFormat));
    }
    
    public void setStyle(final BriefCodediffNofile attr) {
        final String option = attr.getValue();
        if (option.equals("brief")) {
            super.setInternalStyle("-B");
        }
        else if (option.equals("codediff")) {
            super.setInternalStyle("-D");
        }
        else if (option.equals("default")) {
            super.setInternalStyle("");
        }
        else {
            if (!option.equals("nofile")) {
                throw new BuildException("Style " + attr + " unknown.", this.getLocation());
            }
            super.setInternalStyle("-F-");
        }
    }
    
    public static class BriefCodediffNofile extends EnumeratedAttribute
    {
        @Override
        public String[] getValues() {
            return new String[] { "brief", "codediff", "nofile", "default" };
        }
    }
}
