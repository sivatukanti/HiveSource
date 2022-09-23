// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources;

import java.io.OutputStream;
import java.io.IOException;
import org.apache.tools.ant.util.FileUtils;
import java.io.FilterInputStream;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.ant.types.Resource;
import java.io.InputStream;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.zip.ZipEntry;
import java.io.File;
import org.apache.tools.zip.ZipExtraField;

public class ZipResource extends ArchiveResource
{
    private String encoding;
    private ZipExtraField[] extras;
    private int method;
    
    public ZipResource() {
    }
    
    public ZipResource(final File z, final String enc, final ZipEntry e) {
        super(z, true);
        this.setEncoding(enc);
        this.setEntry(e);
    }
    
    public void setZipfile(final File z) {
        this.setArchive(z);
    }
    
    public File getZipfile() {
        final FileProvider fp = this.getArchive().as(FileProvider.class);
        return fp.getFile();
    }
    
    @Override
    public void addConfigured(final ResourceCollection a) {
        super.addConfigured(a);
        if (!a.isFilesystemOnly()) {
            throw new BuildException("only filesystem resources are supported");
        }
    }
    
    public void setEncoding(final String enc) {
        this.checkAttributesAllowed();
        this.encoding = enc;
    }
    
    public String getEncoding() {
        return this.isReference() ? ((ZipResource)this.getCheckedRef()).getEncoding() : this.encoding;
    }
    
    @Override
    public void setRefid(final Reference r) {
        if (this.encoding != null) {
            throw this.tooManyAttributes();
        }
        super.setRefid(r);
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        if (this.isReference()) {
            return ((Resource)this.getCheckedRef()).getInputStream();
        }
        final ZipFile z = new ZipFile(this.getZipfile(), this.getEncoding());
        final ZipEntry ze = z.getEntry(this.getName());
        if (ze == null) {
            z.close();
            throw new BuildException("no entry " + this.getName() + " in " + this.getArchive());
        }
        return new FilterInputStream(z.getInputStream(ze)) {
            @Override
            public void close() throws IOException {
                FileUtils.close(this.in);
                z.close();
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
        };
    }
    
    @Override
    public OutputStream getOutputStream() throws IOException {
        if (this.isReference()) {
            return ((Resource)this.getCheckedRef()).getOutputStream();
        }
        throw new UnsupportedOperationException("Use the zip task for zip output.");
    }
    
    public ZipExtraField[] getExtraFields() {
        if (this.isReference()) {
            return ((ZipResource)this.getCheckedRef()).getExtraFields();
        }
        this.checkEntry();
        if (this.extras == null) {
            return new ZipExtraField[0];
        }
        return this.extras;
    }
    
    public int getMethod() {
        return this.method;
    }
    
    @Override
    protected void fetchEntry() {
        ZipFile z = null;
        try {
            z = new ZipFile(this.getZipfile(), this.getEncoding());
            this.setEntry(z.getEntry(this.getName()));
        }
        catch (IOException e) {
            this.log(e.getMessage(), 4);
            throw new BuildException(e);
        }
        finally {
            ZipFile.closeQuietly(z);
        }
    }
    
    private void setEntry(final ZipEntry e) {
        if (e == null) {
            this.setExists(false);
            return;
        }
        this.setName(e.getName());
        this.setExists(true);
        this.setLastModified(e.getTime());
        this.setDirectory(e.isDirectory());
        this.setSize(e.getSize());
        this.setMode(e.getUnixMode());
        this.extras = e.getExtraFields(true);
        this.method = e.getMethod();
    }
}
