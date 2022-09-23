// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.io.Reader;
import org.apache.tools.ant.types.resources.JavaResource;
import org.apache.tools.ant.util.FileUtils;
import java.io.IOException;
import java.util.Properties;
import java.io.ByteArrayInputStream;
import org.apache.tools.ant.filters.util.ChainReaderHelper;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedInputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileResource;
import java.io.File;
import org.apache.tools.ant.types.FilterChain;
import java.util.Vector;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.Task;

public class LoadProperties extends Task
{
    private Resource src;
    private final Vector<FilterChain> filterChains;
    private String encoding;
    private String prefix;
    private boolean prefixValues;
    
    public LoadProperties() {
        this.src = null;
        this.filterChains = new Vector<FilterChain>();
        this.encoding = null;
        this.prefix = null;
        this.prefixValues = true;
    }
    
    public final void setSrcFile(final File srcFile) {
        this.addConfigured(new FileResource(srcFile));
    }
    
    public void setResource(final String resource) {
        this.getRequiredJavaResource().setName(resource);
    }
    
    public final void setEncoding(final String encoding) {
        this.encoding = encoding;
    }
    
    public void setClasspath(final Path classpath) {
        this.getRequiredJavaResource().setClasspath(classpath);
    }
    
    public Path createClasspath() {
        return this.getRequiredJavaResource().createClasspath();
    }
    
    public void setClasspathRef(final Reference r) {
        this.getRequiredJavaResource().setClasspathRef(r);
    }
    
    public Path getClasspath() {
        return this.getRequiredJavaResource().getClasspath();
    }
    
    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }
    
    public void setPrefixValues(final boolean b) {
        this.prefixValues = b;
    }
    
    @Override
    public final void execute() throws BuildException {
        if (this.src == null) {
            throw new BuildException("A source resource is required.");
        }
        if (this.src.isExists()) {
            BufferedInputStream bis = null;
            Reader instream = null;
            ByteArrayInputStream tis = null;
            try {
                bis = new BufferedInputStream(this.src.getInputStream());
                if (this.encoding == null) {
                    instream = new InputStreamReader(bis);
                }
                else {
                    instream = new InputStreamReader(bis, this.encoding);
                }
                final ChainReaderHelper crh = new ChainReaderHelper();
                crh.setPrimaryReader(instream);
                crh.setFilterChains(this.filterChains);
                crh.setProject(this.getProject());
                instream = crh.getAssembledReader();
                String text = crh.readFully(instream);
                if (text != null && text.length() != 0) {
                    if (!text.endsWith("\n")) {
                        text += "\n";
                    }
                    tis = new ByteArrayInputStream(text.getBytes("ISO-8859-1"));
                    final Properties props = new Properties();
                    props.load(tis);
                    final Property propertyTask = new Property();
                    propertyTask.bindToOwner(this);
                    propertyTask.setPrefix(this.prefix);
                    propertyTask.setPrefixValues(this.prefixValues);
                    propertyTask.addProperties(props);
                }
            }
            catch (IOException ioe) {
                throw new BuildException("Unable to load file: " + ioe, ioe, this.getLocation());
            }
            finally {
                FileUtils.close(bis);
                FileUtils.close(tis);
            }
            return;
        }
        if (this.src instanceof JavaResource) {
            this.log("Unable to find resource " + this.src, 1);
            return;
        }
        throw new BuildException("Source resource does not exist: " + this.src);
    }
    
    public final void addFilterChain(final FilterChain filter) {
        this.filterChains.addElement(filter);
    }
    
    public synchronized void addConfigured(final ResourceCollection a) {
        if (this.src != null) {
            throw new BuildException("only a single source is supported");
        }
        if (a.size() != 1) {
            throw new BuildException("only single-element resource collections are supported");
        }
        this.src = a.iterator().next();
    }
    
    private synchronized JavaResource getRequiredJavaResource() {
        if (this.src == null) {
            (this.src = new JavaResource()).setProject(this.getProject());
        }
        else if (!(this.src instanceof JavaResource)) {
            throw new BuildException("expected a java resource as source");
        }
        return (JavaResource)this.src;
    }
}
