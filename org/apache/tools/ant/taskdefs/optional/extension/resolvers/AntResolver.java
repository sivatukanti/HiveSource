// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.extension.resolvers;

import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Ant;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.optional.extension.Extension;
import java.io.File;
import org.apache.tools.ant.taskdefs.optional.extension.ExtensionResolver;

public class AntResolver implements ExtensionResolver
{
    private File antfile;
    private File destfile;
    private String target;
    
    public void setAntfile(final File antfile) {
        this.antfile = antfile;
    }
    
    public void setDestfile(final File destfile) {
        this.destfile = destfile;
    }
    
    public void setTarget(final String target) {
        this.target = target;
    }
    
    public File resolve(final Extension extension, final Project project) throws BuildException {
        this.validate();
        final Ant ant = new Ant();
        ant.setProject(project);
        ant.setInheritAll(false);
        ant.setAntfile(this.antfile.getName());
        try {
            final File dir = this.antfile.getParentFile().getCanonicalFile();
            ant.setDir(dir);
        }
        catch (IOException ioe) {
            throw new BuildException(ioe.getMessage(), ioe);
        }
        if (null != this.target) {
            ant.setTarget(this.target);
        }
        ant.execute();
        return this.destfile;
    }
    
    private void validate() {
        if (null == this.antfile) {
            final String message = "Must specify Buildfile";
            throw new BuildException("Must specify Buildfile");
        }
        if (null == this.destfile) {
            final String message = "Must specify destination file";
            throw new BuildException("Must specify destination file");
        }
    }
    
    @Override
    public String toString() {
        return "Ant[" + this.antfile + "==>" + this.destfile + "]";
    }
}
