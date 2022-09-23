// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources;

import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.Project;
import java.util.Stack;
import org.apache.tools.ant.AntClassLoader;
import java.io.FilterInputStream;
import java.io.InputStream;
import org.apache.tools.ant.util.FileUtils;
import java.io.IOException;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Resource;

public abstract class AbstractClasspathResource extends Resource
{
    private Path classpath;
    private Reference loader;
    private boolean parentFirst;
    
    public AbstractClasspathResource() {
        this.parentFirst = true;
    }
    
    public void setClasspath(final Path classpath) {
        this.checkAttributesAllowed();
        if (this.classpath == null) {
            this.classpath = classpath;
        }
        else {
            this.classpath.append(classpath);
        }
        this.setChecked(false);
    }
    
    public Path createClasspath() {
        this.checkChildrenAllowed();
        if (this.classpath == null) {
            this.classpath = new Path(this.getProject());
        }
        this.setChecked(false);
        return this.classpath.createPath();
    }
    
    public void setClasspathRef(final Reference r) {
        this.checkAttributesAllowed();
        this.createClasspath().setRefid(r);
    }
    
    public Path getClasspath() {
        if (this.isReference()) {
            return ((AbstractClasspathResource)this.getCheckedRef()).getClasspath();
        }
        this.dieOnCircularReference();
        return this.classpath;
    }
    
    public Reference getLoader() {
        if (this.isReference()) {
            return ((AbstractClasspathResource)this.getCheckedRef()).getLoader();
        }
        this.dieOnCircularReference();
        return this.loader;
    }
    
    public void setLoaderRef(final Reference r) {
        this.checkAttributesAllowed();
        this.loader = r;
    }
    
    public void setParentFirst(final boolean b) {
        this.parentFirst = b;
    }
    
    @Override
    public void setRefid(final Reference r) {
        if (this.loader != null || this.classpath != null) {
            throw this.tooManyAttributes();
        }
        super.setRefid(r);
    }
    
    @Override
    public boolean isExists() {
        if (this.isReference()) {
            return ((Resource)this.getCheckedRef()).isExists();
        }
        this.dieOnCircularReference();
        InputStream is = null;
        try {
            is = this.getInputStream();
            return is != null;
        }
        catch (IOException ex) {
            return false;
        }
        finally {
            FileUtils.close(is);
        }
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        if (this.isReference()) {
            return ((Resource)this.getCheckedRef()).getInputStream();
        }
        this.dieOnCircularReference();
        final ClassLoaderWithFlag classLoader = this.getClassLoader();
        return classLoader.needsCleanup() ? new FilterInputStream(this.openInputStream(classLoader.getLoader())) {
            @Override
            public void close() throws IOException {
                FileUtils.close(this.in);
                classLoader.cleanup();
            }
            
            @Override
            protected void finalize() throws Throwable {
                try {
                    this.close();
                }
                finally {
                    super.finalize();
                }
            }
        } : this.openInputStream(classLoader.getLoader());
    }
    
    protected ClassLoaderWithFlag getClassLoader() {
        ClassLoader cl = null;
        boolean clNeedsCleanup = false;
        if (this.loader != null) {
            cl = (ClassLoader)this.loader.getReferencedObject();
        }
        if (cl == null) {
            if (this.getClasspath() != null) {
                final Path p = this.getClasspath().concatSystemClasspath("ignore");
                if (this.parentFirst) {
                    cl = this.getProject().createClassLoader(p);
                }
                else {
                    cl = AntClassLoader.newAntClassLoader(this.getProject().getCoreLoader(), this.getProject(), p, false);
                }
                clNeedsCleanup = (this.loader == null);
            }
            else {
                cl = JavaResource.class.getClassLoader();
            }
            if (this.loader != null && cl != null) {
                this.getProject().addReference(this.loader.getRefId(), cl);
            }
        }
        return new ClassLoaderWithFlag(cl, clNeedsCleanup);
    }
    
    protected abstract InputStream openInputStream(final ClassLoader p0) throws IOException;
    
    @Override
    protected synchronized void dieOnCircularReference(final Stack<Object> stk, final Project p) {
        if (this.isChecked()) {
            return;
        }
        if (this.isReference()) {
            super.dieOnCircularReference(stk, p);
        }
        else {
            if (this.classpath != null) {
                DataType.pushAndInvokeCircularReferenceCheck(this.classpath, stk, p);
            }
            this.setChecked(true);
        }
    }
    
    public static class ClassLoaderWithFlag
    {
        private final ClassLoader loader;
        private final boolean cleanup;
        
        ClassLoaderWithFlag(final ClassLoader l, final boolean needsCleanup) {
            this.loader = l;
            this.cleanup = (needsCleanup && l instanceof AntClassLoader);
        }
        
        public ClassLoader getLoader() {
            return this.loader;
        }
        
        public boolean needsCleanup() {
            return this.cleanup;
        }
        
        public void cleanup() {
            if (this.cleanup) {
                ((AntClassLoader)this.loader).cleanup();
            }
        }
    }
}
