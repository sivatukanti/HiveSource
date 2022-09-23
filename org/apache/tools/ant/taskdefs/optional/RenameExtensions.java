// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Move;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Mapper;
import java.io.File;
import org.apache.tools.ant.taskdefs.MatchingTask;

public class RenameExtensions extends MatchingTask
{
    private String fromExtension;
    private String toExtension;
    private boolean replace;
    private File srcDir;
    private Mapper.MapperType globType;
    
    public RenameExtensions() {
        this.fromExtension = "";
        this.toExtension = "";
        this.replace = false;
        (this.globType = new Mapper.MapperType()).setValue("glob");
    }
    
    public void setFromExtension(final String from) {
        this.fromExtension = from;
    }
    
    public void setToExtension(final String to) {
        this.toExtension = to;
    }
    
    public void setReplace(final boolean replace) {
        this.replace = replace;
    }
    
    public void setSrcDir(final File srcDir) {
        this.srcDir = srcDir;
    }
    
    @Override
    public void execute() throws BuildException {
        if (this.fromExtension == null || this.toExtension == null || this.srcDir == null) {
            throw new BuildException("srcDir, fromExtension and toExtension attributes must be set!");
        }
        this.log("DEPRECATED - The renameext task is deprecated.  Use move instead.", 1);
        this.log("Replace this with:", 2);
        this.log("<move todir=\"" + this.srcDir + "\" overwrite=\"" + this.replace + "\">", 2);
        this.log("  <fileset dir=\"" + this.srcDir + "\" />", 2);
        this.log("  <mapper type=\"glob\"", 2);
        this.log("          from=\"*" + this.fromExtension + "\"", 2);
        this.log("          to=\"*" + this.toExtension + "\" />", 2);
        this.log("</move>", 2);
        this.log("using the same patterns on <fileset> as you've used here", 2);
        final Move move = new Move();
        move.bindToOwner(this);
        move.setOwningTarget(this.getOwningTarget());
        move.setTaskName(this.getTaskName());
        move.setLocation(this.getLocation());
        move.setTodir(this.srcDir);
        move.setOverwrite(this.replace);
        this.fileset.setDir(this.srcDir);
        move.addFileset(this.fileset);
        final Mapper me = move.createMapper();
        me.setType(this.globType);
        me.setFrom("*" + this.fromExtension);
        me.setTo("*" + this.toExtension);
        move.execute();
    }
}
