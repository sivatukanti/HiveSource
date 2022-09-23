// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.bzip2.CBZip2InputStream;
import java.util.zip.GZIPInputStream;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;
import java.io.BufferedInputStream;
import org.apache.tools.ant.types.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.BuildException;

public class Untar extends Expand
{
    private UntarCompressionMethod compression;
    
    public Untar() {
        this.compression = new UntarCompressionMethod();
    }
    
    public void setCompression(final UntarCompressionMethod method) {
        this.compression = method;
    }
    
    @Override
    public void setEncoding(final String encoding) {
        throw new BuildException("The " + this.getTaskName() + " task doesn't support the encoding" + " attribute", this.getLocation());
    }
    
    @Override
    public void setScanForUnicodeExtraFields(final boolean b) {
        throw new BuildException("The " + this.getTaskName() + " task doesn't support the encoding" + " attribute", this.getLocation());
    }
    
    @Override
    protected void expandFile(final FileUtils fileUtils, final File srcF, final File dir) {
        FileInputStream fis = null;
        if (!srcF.exists()) {
            throw new BuildException("Unable to untar " + srcF + " as the file does not exist", this.getLocation());
        }
        try {
            fis = new FileInputStream(srcF);
            this.expandStream(srcF.getPath(), fis, dir);
        }
        catch (IOException ioe) {
            throw new BuildException("Error while expanding " + srcF.getPath() + "\n" + ioe.toString(), ioe, this.getLocation());
        }
        finally {
            FileUtils.close(fis);
        }
    }
    
    @Override
    protected void expandResource(final Resource srcR, final File dir) {
        if (!srcR.isExists()) {
            throw new BuildException("Unable to untar " + srcR.getName() + " as the it does not exist", this.getLocation());
        }
        InputStream i = null;
        try {
            i = srcR.getInputStream();
            this.expandStream(srcR.getName(), i, dir);
        }
        catch (IOException ioe) {
            throw new BuildException("Error while expanding " + srcR.getName(), ioe, this.getLocation());
        }
        finally {
            FileUtils.close(i);
        }
    }
    
    private void expandStream(final String name, final InputStream stream, final File dir) throws IOException {
        TarInputStream tis = null;
        try {
            tis = new TarInputStream(this.compression.decompress(name, new BufferedInputStream(stream)));
            this.log("Expanding: " + name + " into " + dir, 2);
            TarEntry te = null;
            boolean empty = true;
            final FileNameMapper mapper = this.getMapper();
            while ((te = tis.getNextEntry()) != null) {
                empty = false;
                this.extractFile(FileUtils.getFileUtils(), null, dir, tis, te.getName(), te.getModTime(), te.isDirectory(), mapper);
            }
            if (empty && this.getFailOnEmptyArchive()) {
                throw new BuildException("archive '" + name + "' is empty");
            }
            this.log("expand complete", 3);
        }
        finally {
            FileUtils.close(tis);
        }
    }
    
    public static final class UntarCompressionMethod extends EnumeratedAttribute
    {
        private static final String NONE = "none";
        private static final String GZIP = "gzip";
        private static final String BZIP2 = "bzip2";
        
        public UntarCompressionMethod() {
            this.setValue("none");
        }
        
        @Override
        public String[] getValues() {
            return new String[] { "none", "gzip", "bzip2" };
        }
        
        public InputStream decompress(final String name, final InputStream istream) throws IOException, BuildException {
            final String v = this.getValue();
            if ("gzip".equals(v)) {
                return new GZIPInputStream(istream);
            }
            if ("bzip2".equals(v)) {
                final char[] magic = { 'B', 'Z' };
                for (int i = 0; i < magic.length; ++i) {
                    if (istream.read() != magic[i]) {
                        throw new BuildException("Invalid bz2 file." + name);
                    }
                }
                return new CBZip2InputStream(istream);
            }
            return istream;
        }
    }
}
