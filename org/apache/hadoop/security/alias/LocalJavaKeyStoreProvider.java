// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.alias;

import java.util.EnumSet;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.permission.FsPermission;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.StringTokenizer;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import org.apache.hadoop.util.Shell;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import java.net.URI;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;
import java.io.File;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public final class LocalJavaKeyStoreProvider extends AbstractJavaKeyStoreProvider
{
    public static final String SCHEME_NAME = "localjceks";
    private File file;
    private Set<PosixFilePermission> permissions;
    
    private LocalJavaKeyStoreProvider(final URI uri, final Configuration conf) throws IOException {
        super(uri, conf);
    }
    
    @Override
    protected String getSchemeName() {
        return "localjceks";
    }
    
    @Override
    protected OutputStream getOutputStreamForKeystore() throws IOException {
        if (LocalJavaKeyStoreProvider.LOG.isDebugEnabled()) {
            LocalJavaKeyStoreProvider.LOG.debug("using '" + this.file + "' for output stream.");
        }
        final FileOutputStream out = new FileOutputStream(this.file);
        return out;
    }
    
    @Override
    protected boolean keystoreExists() throws IOException {
        return this.file.exists() && this.file.length() > 0L;
    }
    
    @Override
    protected InputStream getInputStreamForFile() throws IOException {
        final FileInputStream is = new FileInputStream(this.file);
        return is;
    }
    
    @Override
    protected void createPermissions(final String perms) throws IOException {
        int mode = 700;
        try {
            mode = Integer.parseInt(perms, 8);
        }
        catch (NumberFormatException nfe) {
            throw new IOException("Invalid permissions mode provided while trying to createPermissions", nfe);
        }
        this.permissions = modeToPosixFilePermission(mode);
    }
    
    @Override
    protected void stashOriginalFilePermissions() throws IOException {
        if (!Shell.WINDOWS) {
            final Path path = Paths.get(this.file.getCanonicalPath(), new String[0]);
            this.permissions = Files.getPosixFilePermissions(path, new LinkOption[0]);
        }
        else {
            final String[] cmd = Shell.getGetPermissionCommand();
            final String[] args = new String[cmd.length + 1];
            System.arraycopy(cmd, 0, args, 0, cmd.length);
            args[cmd.length] = this.file.getCanonicalPath();
            final String out = Shell.execCommand(args);
            final StringTokenizer t = new StringTokenizer(out, Shell.TOKEN_SEPARATOR_REGEX);
            final String permString = t.nextToken().substring(1);
            this.permissions = PosixFilePermissions.fromString(permString);
        }
    }
    
    @Override
    protected void initFileSystem(final URI uri) throws IOException {
        super.initFileSystem(uri);
        try {
            this.file = new File(new URI(this.getPath().toString()));
            if (LocalJavaKeyStoreProvider.LOG.isDebugEnabled()) {
                LocalJavaKeyStoreProvider.LOG.debug("initialized local file as '" + this.file + "'.");
                if (this.file.exists()) {
                    LocalJavaKeyStoreProvider.LOG.debug("the local file exists and is size " + this.file.length());
                    if (LocalJavaKeyStoreProvider.LOG.isTraceEnabled()) {
                        if (this.file.canRead()) {
                            LocalJavaKeyStoreProvider.LOG.trace("we can read the local file.");
                        }
                        if (this.file.canWrite()) {
                            LocalJavaKeyStoreProvider.LOG.trace("we can write the local file.");
                        }
                    }
                }
                else {
                    LocalJavaKeyStoreProvider.LOG.debug("the local file does not exist.");
                }
            }
        }
        catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }
    
    @Override
    public void flush() throws IOException {
        super.flush();
        if (LocalJavaKeyStoreProvider.LOG.isDebugEnabled()) {
            LocalJavaKeyStoreProvider.LOG.debug("Resetting permissions to '" + this.permissions + "'");
        }
        if (!Shell.WINDOWS) {
            Files.setPosixFilePermissions(Paths.get(this.file.getCanonicalPath(), new String[0]), this.permissions);
        }
        else {
            final FsPermission fsPermission = FsPermission.valueOf("-" + PosixFilePermissions.toString(this.permissions));
            FileUtil.setPermission(this.file, fsPermission);
        }
    }
    
    private static Set<PosixFilePermission> modeToPosixFilePermission(final int mode) {
        final Set<PosixFilePermission> perms = EnumSet.noneOf(PosixFilePermission.class);
        if ((mode & 0x1) != 0x0) {
            perms.add(PosixFilePermission.OTHERS_EXECUTE);
        }
        if ((mode & 0x2) != 0x0) {
            perms.add(PosixFilePermission.OTHERS_WRITE);
        }
        if ((mode & 0x4) != 0x0) {
            perms.add(PosixFilePermission.OTHERS_READ);
        }
        if ((mode & 0x8) != 0x0) {
            perms.add(PosixFilePermission.GROUP_EXECUTE);
        }
        if ((mode & 0x10) != 0x0) {
            perms.add(PosixFilePermission.GROUP_WRITE);
        }
        if ((mode & 0x20) != 0x0) {
            perms.add(PosixFilePermission.GROUP_READ);
        }
        if ((mode & 0x40) != 0x0) {
            perms.add(PosixFilePermission.OWNER_EXECUTE);
        }
        if ((mode & 0x80) != 0x0) {
            perms.add(PosixFilePermission.OWNER_WRITE);
        }
        if ((mode & 0x100) != 0x0) {
            perms.add(PosixFilePermission.OWNER_READ);
        }
        return perms;
    }
    
    public static class Factory extends CredentialProviderFactory
    {
        @Override
        public CredentialProvider createProvider(final URI providerName, final Configuration conf) throws IOException {
            if ("localjceks".equals(providerName.getScheme())) {
                return new LocalJavaKeyStoreProvider(providerName, conf, null);
            }
            return null;
        }
    }
}
