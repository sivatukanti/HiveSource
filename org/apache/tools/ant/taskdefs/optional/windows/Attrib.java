// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.windows;

import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.FileSet;
import java.io.File;
import org.apache.tools.ant.taskdefs.ExecuteOn;

public class Attrib extends ExecuteOn
{
    private static final String ATTR_READONLY = "R";
    private static final String ATTR_ARCHIVE = "A";
    private static final String ATTR_SYSTEM = "S";
    private static final String ATTR_HIDDEN = "H";
    private static final String SET = "+";
    private static final String UNSET = "-";
    private boolean haveAttr;
    
    public Attrib() {
        this.haveAttr = false;
        super.setExecutable("attrib");
        super.setParallel(false);
    }
    
    public void setFile(final File src) {
        final FileSet fs = new FileSet();
        fs.setFile(src);
        this.addFileset(fs);
    }
    
    public void setReadonly(final boolean value) {
        this.addArg(value, "R");
    }
    
    public void setArchive(final boolean value) {
        this.addArg(value, "A");
    }
    
    public void setSystem(final boolean value) {
        this.addArg(value, "S");
    }
    
    public void setHidden(final boolean value) {
        this.addArg(value, "H");
    }
    
    @Override
    protected void checkConfiguration() {
        if (!this.haveAttr()) {
            throw new BuildException("Missing attribute parameter", this.getLocation());
        }
        super.checkConfiguration();
    }
    
    @Override
    public void setExecutable(final String e) {
        throw new BuildException(this.getTaskType() + " doesn't support the executable attribute", this.getLocation());
    }
    
    public void setCommand(final String e) {
        throw new BuildException(this.getTaskType() + " doesn't support the command attribute", this.getLocation());
    }
    
    @Override
    public void setAddsourcefile(final boolean b) {
        throw new BuildException(this.getTaskType() + " doesn't support the addsourcefile attribute", this.getLocation());
    }
    
    @Override
    public void setSkipEmptyFilesets(final boolean skip) {
        throw new BuildException(this.getTaskType() + " doesn't support the " + "skipemptyfileset attribute", this.getLocation());
    }
    
    @Override
    public void setParallel(final boolean parallel) {
        throw new BuildException(this.getTaskType() + " doesn't support the parallel attribute", this.getLocation());
    }
    
    @Override
    public void setMaxParallel(final int max) {
        throw new BuildException(this.getTaskType() + " doesn't support the maxparallel attribute", this.getLocation());
    }
    
    @Override
    protected boolean isValidOs() {
        return (this.getOs() == null && this.getOsFamily() == null) ? Os.isFamily("windows") : super.isValidOs();
    }
    
    private static String getSignString(final boolean attr) {
        return attr ? "+" : "-";
    }
    
    private void addArg(final boolean sign, final String attribute) {
        this.createArg().setValue(getSignString(sign) + attribute);
        this.haveAttr = true;
    }
    
    private boolean haveAttr() {
        return this.haveAttr;
    }
}
