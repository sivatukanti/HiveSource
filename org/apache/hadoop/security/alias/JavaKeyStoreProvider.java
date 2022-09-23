// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.alias;

import org.apache.hadoop.fs.FileStatus;
import java.io.InputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import java.net.URI;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class JavaKeyStoreProvider extends AbstractJavaKeyStoreProvider
{
    public static final String SCHEME_NAME = "jceks";
    private FileSystem fs;
    private FsPermission permissions;
    
    private JavaKeyStoreProvider(final URI uri, final Configuration conf) throws IOException {
        super(uri, conf);
    }
    
    @Override
    protected String getSchemeName() {
        return "jceks";
    }
    
    @Override
    protected OutputStream getOutputStreamForKeystore() throws IOException {
        final FSDataOutputStream out = FileSystem.create(this.fs, this.getPath(), this.permissions);
        return out;
    }
    
    @Override
    protected boolean keystoreExists() throws IOException {
        return this.fs.exists(this.getPath());
    }
    
    @Override
    protected InputStream getInputStreamForFile() throws IOException {
        return this.fs.open(this.getPath());
    }
    
    @Override
    protected void createPermissions(final String perms) {
        this.permissions = new FsPermission(perms);
    }
    
    @Override
    protected void stashOriginalFilePermissions() throws IOException {
        final FileStatus s = this.fs.getFileStatus(this.getPath());
        this.permissions = s.getPermission();
    }
    
    @Override
    protected void initFileSystem(final URI uri) throws IOException {
        super.initFileSystem(uri);
        this.fs = this.getPath().getFileSystem(this.getConf());
    }
    
    public static class Factory extends CredentialProviderFactory
    {
        @Override
        public CredentialProvider createProvider(final URI providerName, final Configuration conf) throws IOException {
            if ("jceks".equals(providerName.getScheme())) {
                return new JavaKeyStoreProvider(providerName, conf, null);
            }
            return null;
        }
    }
}
