// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.types.ResourceCollection;
import java.io.Reader;
import org.apache.tools.ant.util.FileUtils;
import java.io.IOException;
import org.apache.tools.ant.filters.util.ChainReaderHelper;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedInputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.FilterChain;
import java.util.Vector;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.Task;

public class LoadResource extends Task
{
    private Resource src;
    private boolean failOnError;
    private boolean quiet;
    private String encoding;
    private String property;
    private final Vector<FilterChain> filterChains;
    
    public LoadResource() {
        this.failOnError = true;
        this.quiet = false;
        this.encoding = null;
        this.property = null;
        this.filterChains = new Vector<FilterChain>();
    }
    
    public final void setEncoding(final String encoding) {
        this.encoding = encoding;
    }
    
    public final void setProperty(final String property) {
        this.property = property;
    }
    
    public final void setFailonerror(final boolean fail) {
        this.failOnError = fail;
    }
    
    public void setQuiet(final boolean quiet) {
        this.quiet = quiet;
        if (quiet) {
            this.failOnError = false;
        }
    }
    
    @Override
    public final void execute() throws BuildException {
        if (this.src == null) {
            throw new BuildException("source resource not defined");
        }
        if (this.property == null) {
            throw new BuildException("output property not defined");
        }
        if (this.quiet && this.failOnError) {
            throw new BuildException("quiet and failonerror cannot both be set to true");
        }
        if (this.src.isExists()) {
            InputStream is = null;
            BufferedInputStream bis = null;
            Reader instream = null;
            this.log("loading " + this.src + " into property " + this.property, 3);
            try {
                final long len = this.src.getSize();
                this.log("resource size = " + ((len != -1L) ? String.valueOf(len) : "unknown"), 4);
                final int size = (int)len;
                is = this.src.getInputStream();
                bis = new BufferedInputStream(is);
                if (this.encoding == null) {
                    instream = new InputStreamReader(bis);
                }
                else {
                    instream = new InputStreamReader(bis, this.encoding);
                }
                String text = "";
                if (size != 0) {
                    final ChainReaderHelper crh = new ChainReaderHelper();
                    if (len != -1L) {
                        crh.setBufferSize(size);
                    }
                    crh.setPrimaryReader(instream);
                    crh.setFilterChains(this.filterChains);
                    crh.setProject(this.getProject());
                    instream = crh.getAssembledReader();
                    text = crh.readFully(instream);
                }
                else {
                    this.log("Do not set property " + this.property + " as its length is 0.", this.quiet ? 3 : 2);
                }
                if (text != null && text.length() > 0) {
                    this.getProject().setNewProperty(this.property, text);
                    this.log("loaded " + text.length() + " characters", 3);
                    this.log(this.property + " := " + text, 4);
                }
            }
            catch (IOException ioe) {
                final String message = "Unable to load resource: " + ioe.toString();
                if (this.failOnError) {
                    throw new BuildException(message, ioe, this.getLocation());
                }
                this.log(message, this.quiet ? 3 : 0);
            }
            catch (BuildException be) {
                if (this.failOnError) {
                    throw be;
                }
                this.log(be.getMessage(), this.quiet ? 3 : 0);
            }
            finally {
                FileUtils.close(is);
            }
            return;
        }
        final String message2 = this.src + " doesn't exist";
        if (this.failOnError) {
            throw new BuildException(message2);
        }
        this.log(message2, this.quiet ? 1 : 0);
    }
    
    public final void addFilterChain(final FilterChain filter) {
        this.filterChains.addElement(filter);
    }
    
    public void addConfigured(final ResourceCollection a) {
        if (a.size() != 1) {
            throw new BuildException("only single argument resource collections are supported");
        }
        this.src = a.iterator().next();
    }
}
