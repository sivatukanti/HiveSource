// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.net.URL;
import java.util.Iterator;
import java.io.IOException;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.types.resources.URLProvider;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.Task;

public class CloseResources extends Task
{
    private Union resources;
    
    public CloseResources() {
        this.resources = new Union();
    }
    
    public void add(final ResourceCollection rc) {
        this.resources.add(rc);
    }
    
    @Override
    public void execute() {
        for (final Resource r : this.resources) {
            final URLProvider up = r.as(URLProvider.class);
            if (up != null) {
                final URL u = up.getURL();
                try {
                    FileUtils.close(u.openConnection());
                }
                catch (IOException ex) {}
            }
        }
    }
}
