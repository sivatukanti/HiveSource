// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources;

import java.io.OutputStream;
import java.io.InputStream;
import org.apache.tools.ant.util.FileUtils;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.ResourceCollection;

public abstract class ContentTransformingResource extends ResourceDecorator
{
    private static final int BUFFER_SIZE = 8192;
    
    protected ContentTransformingResource() {
    }
    
    protected ContentTransformingResource(final ResourceCollection other) {
        super(other);
    }
    
    @Override
    public long getSize() {
        if (this.isExists()) {
            InputStream in = null;
            try {
                in = this.getInputStream();
                final byte[] buf = new byte[8192];
                int size = 0;
                int readNow;
                while ((readNow = in.read(buf, 0, buf.length)) > 0) {
                    size += readNow;
                }
                return size;
            }
            catch (IOException ex) {
                throw new BuildException("caught exception while reading " + this.getName(), ex);
            }
            finally {
                FileUtils.close(in);
            }
        }
        return 0L;
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        InputStream in = this.getResource().getInputStream();
        if (in != null) {
            in = this.wrapStream(in);
        }
        return in;
    }
    
    @Override
    public OutputStream getOutputStream() throws IOException {
        OutputStream out = this.getResource().getOutputStream();
        if (out != null) {
            out = this.wrapStream(out);
        }
        return out;
    }
    
    @Override
    public <T> T as(final Class<T> clazz) {
        if (Appendable.class.isAssignableFrom(clazz)) {
            if (this.isAppendSupported()) {
                final Appendable a = this.getResource().as(Appendable.class);
                if (a != null) {
                    return clazz.cast(new Appendable() {
                        public OutputStream getAppendOutputStream() throws IOException {
                            OutputStream out = a.getAppendOutputStream();
                            if (out != null) {
                                out = ContentTransformingResource.this.wrapStream(out);
                            }
                            return out;
                        }
                    });
                }
            }
            return null;
        }
        return FileProvider.class.isAssignableFrom(clazz) ? null : this.getResource().as(clazz);
    }
    
    protected boolean isAppendSupported() {
        return false;
    }
    
    protected abstract InputStream wrapStream(final InputStream p0) throws IOException;
    
    protected abstract OutputStream wrapStream(final OutputStream p0) throws IOException;
}
