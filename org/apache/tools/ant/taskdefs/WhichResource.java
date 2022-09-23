// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.net.URL;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.Task;

public class WhichResource extends Task
{
    private Path classpath;
    private String classname;
    private String resource;
    private String property;
    
    public void setClasspath(final Path cp) {
        if (this.classpath == null) {
            this.classpath = cp;
        }
        else {
            this.classpath.append(cp);
        }
    }
    
    public Path createClasspath() {
        if (this.classpath == null) {
            this.classpath = new Path(this.getProject());
        }
        return this.classpath.createPath();
    }
    
    public void setClasspathRef(final Reference r) {
        this.createClasspath().setRefid(r);
    }
    
    private void validate() {
        int setcount = 0;
        if (this.classname != null) {
            ++setcount;
        }
        if (this.resource != null) {
            ++setcount;
        }
        if (setcount == 0) {
            throw new BuildException("One of classname or resource must be specified");
        }
        if (setcount > 1) {
            throw new BuildException("Only one of classname or resource can be specified");
        }
        if (this.property == null) {
            throw new BuildException("No property defined");
        }
    }
    
    @Override
    public void execute() throws BuildException {
        this.validate();
        if (this.classpath != null) {
            this.classpath = this.classpath.concatSystemClasspath("ignore");
            this.getProject().log("using user supplied classpath: " + this.classpath, 4);
        }
        else {
            this.classpath = new Path(this.getProject());
            this.classpath = this.classpath.concatSystemClasspath("only");
            this.getProject().log("using system classpath: " + this.classpath, 4);
        }
        AntClassLoader loader = null;
        try {
            loader = AntClassLoader.newAntClassLoader(this.getProject().getCoreLoader(), this.getProject(), this.classpath, false);
            String loc = null;
            if (this.classname != null) {
                this.resource = this.classname.replace('.', '/') + ".class";
            }
            if (this.resource == null) {
                throw new BuildException("One of class or resource is required");
            }
            if (this.resource.startsWith("/")) {
                this.resource = this.resource.substring(1);
            }
            this.log("Searching for " + this.resource, 3);
            final URL url = loader.getResource(this.resource);
            if (url != null) {
                loc = url.toExternalForm();
                this.getProject().setNewProperty(this.property, loc);
            }
        }
        finally {
            if (loader != null) {
                loader.cleanup();
            }
        }
    }
    
    public void setResource(final String resource) {
        this.resource = resource;
    }
    
    public void setClass(final String classname) {
        this.classname = classname;
    }
    
    public void setProperty(final String property) {
        this.property = property;
    }
}
