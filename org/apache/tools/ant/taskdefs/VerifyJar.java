// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import java.io.Reader;
import java.io.File;
import java.util.Iterator;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.FilterChain;
import org.apache.tools.ant.types.RedirectorElement;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.filters.ChainableReader;
import org.apache.tools.ant.BuildException;

public class VerifyJar extends AbstractJarSignerTask
{
    public static final String ERROR_NO_FILE = "Not found :";
    private static final String VERIFIED_TEXT = "jar verified.";
    private boolean certificates;
    private BufferingOutputFilter outputCache;
    public static final String ERROR_NO_VERIFY = "Failed to verify ";
    
    public VerifyJar() {
        this.certificates = false;
        this.outputCache = new BufferingOutputFilter();
    }
    
    public void setCertificates(final boolean certificates) {
        this.certificates = certificates;
    }
    
    @Override
    public void execute() throws BuildException {
        final boolean hasJar = this.jar != null;
        if (!hasJar && !this.hasResources()) {
            throw new BuildException("jar must be set through jar attribute or nested filesets");
        }
        this.beginExecution();
        final RedirectorElement redirector = this.getRedirector();
        redirector.setAlwaysLog(true);
        final FilterChain outputFilterChain = redirector.createOutputFilterChain();
        outputFilterChain.add(this.outputCache);
        try {
            final Path sources = this.createUnifiedSourcePath();
            for (final Resource r : sources) {
                final FileProvider fr = r.as(FileProvider.class);
                this.verifyOneJar(fr.getFile());
            }
        }
        finally {
            this.endExecution();
        }
    }
    
    private void verifyOneJar(final File jar) {
        if (!jar.exists()) {
            throw new BuildException("Not found :" + jar);
        }
        final ExecTask cmd = this.createJarSigner();
        this.setCommonOptions(cmd);
        this.bindToKeystore(cmd);
        this.addValue(cmd, "-verify");
        if (this.certificates) {
            this.addValue(cmd, "-certs");
        }
        this.addValue(cmd, jar.getPath());
        this.log("Verifying JAR: " + jar.getAbsolutePath());
        this.outputCache.clear();
        BuildException ex = null;
        try {
            cmd.execute();
        }
        catch (BuildException e) {
            ex = e;
        }
        final String results = this.outputCache.toString();
        if (ex != null) {
            if (results.indexOf("zip file closed") < 0) {
                throw ex;
            }
            this.log("You are running jarsigner against a JVM with a known bug that manifests as an IllegalStateException.", 1);
        }
        if (results.indexOf("jar verified.") < 0) {
            throw new BuildException("Failed to verify " + jar);
        }
    }
    
    private static class BufferingOutputFilter implements ChainableReader
    {
        private BufferingOutputFilterReader buffer;
        
        public Reader chain(final Reader rdr) {
            return this.buffer = new BufferingOutputFilterReader(rdr);
        }
        
        @Override
        public String toString() {
            return this.buffer.toString();
        }
        
        public void clear() {
            if (this.buffer != null) {
                this.buffer.clear();
            }
        }
    }
    
    private static class BufferingOutputFilterReader extends Reader
    {
        private Reader next;
        private StringBuffer buffer;
        
        public BufferingOutputFilterReader(final Reader next) {
            this.buffer = new StringBuffer();
            this.next = next;
        }
        
        @Override
        public int read(final char[] cbuf, final int off, final int len) throws IOException {
            final int result = this.next.read(cbuf, off, len);
            this.buffer.append(cbuf, off, len);
            return result;
        }
        
        @Override
        public void close() throws IOException {
            this.next.close();
        }
        
        @Override
        public String toString() {
            return this.buffer.toString();
        }
        
        public void clear() {
            this.buffer = new StringBuffer();
        }
    }
}
