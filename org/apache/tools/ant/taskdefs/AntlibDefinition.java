// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class AntlibDefinition extends Task
{
    private String uri;
    private ClassLoader antlibClassLoader;
    
    public AntlibDefinition() {
        this.uri = "";
    }
    
    public void setURI(String uri) throws BuildException {
        if (uri.equals("antlib:org.apache.tools.ant")) {
            uri = "";
        }
        if (uri.startsWith("ant:")) {
            throw new BuildException("Attempt to use a reserved URI " + uri);
        }
        this.uri = uri;
    }
    
    public String getURI() {
        return this.uri;
    }
    
    public void setAntlibClassLoader(final ClassLoader classLoader) {
        this.antlibClassLoader = classLoader;
    }
    
    public ClassLoader getAntlibClassLoader() {
        return this.antlibClassLoader;
    }
}
