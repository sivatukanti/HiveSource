// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.condition;

import java.util.Iterator;
import java.io.IOException;
import org.apache.tools.ant.util.ResourceUtils;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.Union;

public class ResourcesMatch implements Condition
{
    private Union resources;
    private boolean asText;
    
    public ResourcesMatch() {
        this.resources = null;
        this.asText = false;
    }
    
    public void setAsText(final boolean asText) {
        this.asText = asText;
    }
    
    public void add(final ResourceCollection rc) {
        if (rc == null) {
            return;
        }
        (this.resources = ((this.resources == null) ? new Union() : this.resources)).add(rc);
    }
    
    public boolean eval() throws BuildException {
        if (this.resources == null) {
            throw new BuildException("You must specify one or more nested resource collections");
        }
        if (this.resources.size() > 1) {
            final Iterator<Resource> i = this.resources.iterator();
            Resource r1 = i.next();
            Resource r2 = null;
            while (i.hasNext()) {
                r2 = i.next();
                try {
                    if (!ResourceUtils.contentEquals(r1, r2, this.asText)) {
                        return false;
                    }
                }
                catch (IOException ioe) {
                    throw new BuildException("when comparing resources " + r1.toString() + " and " + r2.toString(), ioe);
                }
                r1 = r2;
            }
        }
        return true;
    }
}
