// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.extension.resolvers;

import org.apache.tools.ant.BuildException;
import java.io.File;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.optional.extension.Extension;
import org.apache.tools.ant.taskdefs.optional.extension.ExtensionResolver;

public class LocationResolver implements ExtensionResolver
{
    private String location;
    
    public void setLocation(final String location) {
        this.location = location;
    }
    
    public File resolve(final Extension extension, final Project project) throws BuildException {
        if (null == this.location) {
            final String message = "No location specified for resolver";
            throw new BuildException("No location specified for resolver");
        }
        return project.resolveFile(this.location);
    }
    
    @Override
    public String toString() {
        return "Location[" + this.location + "]";
    }
}
