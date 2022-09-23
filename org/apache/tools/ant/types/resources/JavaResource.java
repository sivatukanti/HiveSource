// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources;

import org.apache.tools.ant.types.Resource;
import java.net.URL;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.apache.tools.ant.types.Path;

public class JavaResource extends AbstractClasspathResource implements URLProvider
{
    public JavaResource() {
    }
    
    public JavaResource(final String name, final Path path) {
        this.setName(name);
        this.setClasspath(path);
    }
    
    @Override
    protected InputStream openInputStream(final ClassLoader cl) throws IOException {
        InputStream inputStream;
        if (cl == null) {
            inputStream = ClassLoader.getSystemResourceAsStream(this.getName());
            if (inputStream == null) {
                throw new FileNotFoundException("No resource " + this.getName() + " on Ant's classpath");
            }
        }
        else {
            inputStream = cl.getResourceAsStream(this.getName());
            if (inputStream == null) {
                throw new FileNotFoundException("No resource " + this.getName() + " on the classpath " + cl);
            }
        }
        return inputStream;
    }
    
    public URL getURL() {
        if (this.isReference()) {
            return ((JavaResource)this.getCheckedRef()).getURL();
        }
        final ClassLoaderWithFlag classLoader = this.getClassLoader();
        if (classLoader.getLoader() == null) {
            return ClassLoader.getSystemResource(this.getName());
        }
        try {
            return classLoader.getLoader().getResource(this.getName());
        }
        finally {
            classLoader.cleanup();
        }
    }
    
    @Override
    public int compareTo(final Resource another) {
        if (this.isReference()) {
            return ((Resource)this.getCheckedRef()).compareTo(another);
        }
        if (!another.getClass().equals(this.getClass())) {
            return super.compareTo(another);
        }
        final JavaResource otherjr = (JavaResource)another;
        if (!this.getName().equals(otherjr.getName())) {
            return this.getName().compareTo(otherjr.getName());
        }
        if (this.getLoader() != otherjr.getLoader()) {
            if (this.getLoader() == null) {
                return -1;
            }
            if (otherjr.getLoader() == null) {
                return 1;
            }
            return this.getLoader().getRefId().compareTo(otherjr.getLoader().getRefId());
        }
        else {
            final Path p = this.getClasspath();
            final Path op = otherjr.getClasspath();
            if (p == op) {
                return 0;
            }
            if (p == null) {
                return -1;
            }
            if (op == null) {
                return 1;
            }
            return p.toString().compareTo(op.toString());
        }
    }
}
