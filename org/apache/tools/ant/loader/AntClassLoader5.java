// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.loader;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.Project;
import java.io.Closeable;
import org.apache.tools.ant.AntClassLoader;

public class AntClassLoader5 extends AntClassLoader implements Closeable
{
    public AntClassLoader5(final ClassLoader parent, final Project project, final Path classpath, final boolean parentFirst) {
        super(parent, project, classpath, parentFirst);
    }
    
    @Override
    public Enumeration<URL> getResources(final String name) throws IOException {
        return this.getNamedResources(name);
    }
    
    public void close() {
        this.cleanup();
    }
}
